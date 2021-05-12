package towersim.control;

import org.junit.Before;
import org.junit.Test;
import towersim.aircraft.Aircraft;
import towersim.aircraft.AircraftCharacteristics;
import towersim.aircraft.PassengerAircraft;
import towersim.tasks.Task;
import towersim.tasks.TaskList;
import towersim.tasks.TaskType;
import towersim.util.MalformedSaveException;

import javax.naming.ldap.Control;
import java.io.*;
import java.nio.Buffer;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.*;

public class ControlTowerInitialiserTest {


    private Map<Aircraft,Integer> loadingAircraftMap;
    private TakeoffQueue takeoffQueue;
    private LandingQueue landingQueue;
    private List<Aircraft> aircrafts;
    private PassengerAircraft passengerAircraft1;
    private PassengerAircraft passengerAircraft2;
    private PassengerAircraft passengerAircraft3;
    private PassengerAircraft passengerAircraftTakingOff;
    private PassengerAircraft passengerAircraftLanding;
    private PassengerAircraft passengerAircraftLoading;

    @Before
    public void setUp() throws Exception {

        TaskList taskList1 = new TaskList(List.of(
                new Task(TaskType.WAIT),
                new Task(TaskType.LOAD, 100),
                new Task(TaskType.TAKEOFF),
                new Task(TaskType.AWAY),
                new Task(TaskType.AWAY),
                new Task(TaskType.LAND),
                new Task(TaskType.WAIT)));

        TaskList taskList2 = new TaskList(List.of(
                new Task(TaskType.WAIT),
                new Task(TaskType.LOAD, 50),
                new Task(TaskType.TAKEOFF),
                new Task(TaskType.AWAY),
                new Task(TaskType.AWAY),
                new Task(TaskType.LAND),
                new Task(TaskType.WAIT)));

        TaskList taskList3 = new TaskList(List.of(
                new Task(TaskType.WAIT),
                new Task(TaskType.LOAD, 35),
                new Task(TaskType.TAKEOFF),
                new Task(TaskType.AWAY),
                new Task(TaskType.AWAY),
                new Task(TaskType.LAND),
                new Task(TaskType.WAIT)));

        TaskList taskListTakeoff = new TaskList(List.of(
                new Task(TaskType.TAKEOFF),
                new Task(TaskType.AWAY),
                new Task(TaskType.AWAY),
                new Task(TaskType.LAND),
                new Task(TaskType.WAIT),
                new Task(TaskType.WAIT),
                new Task(TaskType.LOAD, 100)));

        TaskList taskListLand = new TaskList(List.of(
                new Task(TaskType.LAND),
                new Task(TaskType.WAIT),
                new Task(TaskType.WAIT),
                new Task(TaskType.LOAD, 100),
                new Task(TaskType.TAKEOFF),
                new Task(TaskType.AWAY),
                new Task(TaskType.AWAY)));

        TaskList taskListLoad = new TaskList(List.of(
                new Task(TaskType.LOAD, 70),
                new Task(TaskType.TAKEOFF),
                new Task(TaskType.AWAY),
                new Task(TaskType.AWAY),
                new Task(TaskType.LAND),
                new Task(TaskType.WAIT),
                new Task(TaskType.WAIT)));

        TaskList taskListAway = new TaskList(List.of(
                new Task(TaskType.AWAY),
                new Task(TaskType.LAND),
                new Task(TaskType.WAIT),
                new Task(TaskType.WAIT),
                new Task(TaskType.LOAD, 70),
                new Task(TaskType.TAKEOFF),
                new Task(TaskType.AWAY)));
        this.passengerAircraft1 = new PassengerAircraft("VH-BFK",
                AircraftCharacteristics.AIRBUS_A320,
                taskList1,
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity / 10, 0);

        this.passengerAircraft2 = new PassengerAircraft("ABC002",
                AircraftCharacteristics.AIRBUS_A320,
                taskList2,
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity / 2, 0);

        this.passengerAircraft3 = new PassengerAircraft("ABC003",
                AircraftCharacteristics.ROBINSON_R44,
                taskList3,
                AircraftCharacteristics.ROBINSON_R44.fuelCapacity / 2, 0);

        this.passengerAircraftTakingOff = new PassengerAircraft("TAK001",
                AircraftCharacteristics.AIRBUS_A320,
                taskListTakeoff,
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity / 2, 100);

        this.passengerAircraftLanding = new PassengerAircraft("LAN001",
                AircraftCharacteristics.AIRBUS_A320,
                taskListLand,
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity / 2, 100);
        this.passengerAircraftLoading = new PassengerAircraft("LOD001",
                AircraftCharacteristics.AIRBUS_A320,
                taskListLoad,
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity / 8, 0);

        aircrafts = List.of(passengerAircraft1,passengerAircraft2,passengerAircraft3,
                passengerAircraftTakingOff,passengerAircraftLoading);
        takeoffQueue = new TakeoffQueue();
        landingQueue = new LandingQueue();
        loadingAircraftMap =
                new TreeMap<>(Comparator.comparing(Aircraft::getCallsign));

    }

    @Test
    public void loadAircraftDefault() {
        try {
            String fileContents = "0";
            ControlTowerInitialiser.loadAircraft(new StringReader(fileContents));
        } catch (IOException e) {
            fail();
        } catch (MalformedSaveException e) {
            fail();
        }
    }

    @Test public void loadAircraftBasic() {
        try {
            String fileContents = String.join(System.lineSeparator(),"4", "QFA481:AIRBUS_A320" +
                    ":AWAY,AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY:10000.00:false:132", "UTD302" +
                    ":BOEING_787:WAIT,LOAD@100,TAKEOFF,AWAY,AWAY,AWAY,LAND:10000.00:false:0",
                    "UPS119:BOEING_747_8F:WAIT,LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:4000" +
                            ".00:false:0", "VH-BFK:ROBINSON_R44:LAND,WAIT,LOAD@75,TAKEOFF,AWAY," +
                            "AWAY:40.00:false:4");
            ControlTowerInitialiser.loadAircraft(new StringReader(fileContents));
        } catch (IOException e) {
            fail();
        } catch (MalformedSaveException e) {
            fail();
        }
    }

    @Test
    public void loadAircraftTestNull() {
        String fileContents = String.join(System.lineSeparator(), "");
        try {
            ControlTowerInitialiser.loadAircraft(new StringReader(fileContents));
            fail();
        } catch (MalformedSaveException e) {
            //expected, not an integer

        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void loadAircraftTestNotNumber() {
        String fileContents = String.join(System.lineSeparator(), "ONE");
        try {
            ControlTowerInitialiser.loadAircraft(new StringReader(fileContents));
            fail();
        } catch (MalformedSaveException e) {
            //expected, not an integer

        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void loadAircraftTestAircraftsMatchLess() {
        String fileContents = String.join(System.lineSeparator(), "3","QFA481:AIRBUS_A320:AWAY," +
                "AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY:10000.00:false:132", "UTD302:BOEING_787" +
                ":WAIT,LOAD@100,TAKEOFF,AWAY,AWAY,AWAY,LAND:10000.00:false:0");
        try {
            ControlTowerInitialiser.loadAircraft(new StringReader(fileContents));
            fail();
        } catch (MalformedSaveException e) {
            //aircrafts specified do not match

        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void loadAircraftTestAircraftsMatchMore() {
        String fileContents = String.join(System.lineSeparator(), "3","QFA481:AIRBUS_A320:AWAY," +
                "AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY:10000.00:false:132", "UTD302:BOEING_787" +
                ":WAIT,LOAD@100,TAKEOFF,AWAY,AWAY,AWAY,LAND:10000.00:false:0", "UPS119" +
                ":BOEING_747_8F:WAIT,LOAD@50,TAKEOFF,AWAY,AWAY,AWAY,LAND:4000.00:false:0", "VH-BFK:ROBINSON_R44:LAND,WAIT,LOAD@75,TAKEOFF,AWAY,AWAY:40.00:false:4");
        try {
            ControlTowerInitialiser.loadAircraft(new StringReader(fileContents));
            fail();
        } catch (MalformedSaveException e) {
            //aircrafts specified do not match

        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void readAircraftBasic() {
        try {
            String line = "QFA481:AIRBUS_A320:AWAY,AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY:10000.00:false:132";
            ControlTowerInitialiser.readAircraft(line);
        } catch (MalformedSaveException e) {
            fail();
        }
    }

    @Test
    public void readAircraftMoreColons() {
        try {
            String line = "QFA481::AIRBUS_A320:AWAY,AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF," +
                    "AWAY:10000.00:false:132";
            ControlTowerInitialiser.readAircraft(line);
            fail();
        } catch (MalformedSaveException e) {
            // more colons detected.
        }
    }

    @Test
    public void readAircraftLessColons() {
        try {
            String line = "QFA481:AIRBUS_A320:AWAY,AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF," +
                    "AWAY:10000.00false:132";
            ControlTowerInitialiser.readAircraft(line);
            fail();
        } catch (MalformedSaveException e) {
            // less colons detected.
        }
    }

    @Test
    public void readAircraftNotValid() {
        try {
            String line = "QFA481:AIRBUS_A3120:AWAY,AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF," +
                    "AWAY:10000.00:false:132";
            ControlTowerInitialiser.readAircraft(line);
            fail();
        } catch (MalformedSaveException e) {
            //Aircraft AIRBUS_A3120 is not a value of AircraftCharacteristics
        }
    }

    @Test
    public void readAircraftFuelAmountInvalid() {
        try {
            String line = "QFA481:AIRBUS_A320:AWAY,AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF," +
                    "AWAY:TWENTY:false:132";
            ControlTowerInitialiser.readAircraft(line);
            fail();
        } catch (MalformedSaveException e) {
            //fuel amount not a double
        }
    }

    @Test
    public void readAircraftFuelAmountNegative() {
        try {
            String line = "QFA481:AIRBUS_A320:AWAY,AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF," +
                    "AWAY:-100.00:false:132";
            ControlTowerInitialiser.readAircraft(line);
            fail();
        } catch (MalformedSaveException e) {
            //negative fuel amount
        }
    }

    @Test
    public void readAircraftFuelOver() {
        try {
            String line = "QFA481:AIRBUS_A320:AWAY,AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY:27201" +
                    ".00:false:132";
            ControlTowerInitialiser.readAircraft(line);
            fail();
        } catch (MalformedSaveException e) {
            //fuel greater than aircraft's maximum capacity
        }
    }

    @Test
    public void readAircraftCargoInvalid() {
        try {
            String line = "QFA481:AIRBUS_A320:AWAY,AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY:10000" +
                    ".00:false:Twenty";
            ControlTowerInitialiser.readAircraft(line);
            fail();
        } catch (MalformedSaveException e) {
            //cargo is not an integer
        }
    }

    @Test
    public void readAircraftCallsignEquals() {
        try {
            String line = "QFA481:AIRBUS_A320:AWAY,AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY:10000" +
                    ".00:false:132";
            Aircraft aircraftCompare = ControlTowerInitialiser.readAircraft(line);
            assertEquals("QFA481", aircraftCompare.getCallsign());
        } catch (MalformedSaveException e) {
            fail();
        }
    }

    @Test
    public void readAircraftCargoNegative() {
        try {
            String line = "QFA481:AIRBUS_A320:AWAY,AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY:10000" +
                    ".00:false:-132";
            ControlTowerInitialiser.readAircraft(line);
            fail();
        } catch (MalformedSaveException e) {
            //cargo is negative
        }
    }

    @Test
    public void readAircraftCargoOver() {
        try {
            String line = "QFA481:AIRBUS_A320:AWAY,AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY:10000" +
                    ".00:false:151";
            ControlTowerInitialiser.readAircraft(line);
            fail();
        } catch (MalformedSaveException e) {
            //too many passsengers
        }
    }

    @Test
    public void readAircraftnullCallsign() {
        try {
            String line = "AIRBUS_A320:AWAY,AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY:10000" +
                    ".00:false:145";
            ControlTowerInitialiser.readAircraft(line);
            fail();
        } catch (MalformedSaveException e) {
            //too many passsengers
        }
    }

    @Test
    public void readTaskListBasic() {
        try {
            String line = "AWAY,AWAY,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY";
            ControlTowerInitialiser.readTaskList(line);
        } catch (MalformedSaveException e) {
            fail();
        }
    }

    @Test
    public void readTaskListNull() {
        try {
            String line = "AWAY,AWAY,WAIT,WAIT,LOAD@60, ,AWAY";
            ControlTowerInitialiser.readTaskList(line);
            fail();
        } catch (MalformedSaveException e) {

        }
    }

    @Test
    public void readTaskListInvalidTaskType() {
        try {
            String line = "AWAY,HOLD,LAND,WAIT,WAIT,LOAD@60,TAKEOFF,AWAY";
            ControlTowerInitialiser.readTaskList(line);
            fail();
        } catch (MalformedSaveException e) {
            //HOLD is not a TaskType value
        }
    }

    @Test
    public void readTaskListLoadPercentageInvalid() {
        try {
            String line = "AWAY,AWAY,LAND,WAIT,WAIT,LOAD@TWENTY,TAKEOFF,AWAY";
            ControlTowerInitialiser.readTaskList(line);
            fail();
        } catch (MalformedSaveException e) {
            //load percentage is not an integer
        }
    }

    @Test
    public void readTaskListLoadPercentageNegative() {
        try {
            String line = "AWAY,AWAY,LAND,WAIT,WAIT,LOAD@-1,TAKEOFF,AWAY";
            ControlTowerInitialiser.readTaskList(line);
            fail();
        } catch (MalformedSaveException e) {
            //load percentage is negative
        }
    }

    @Test
    public void readTaskListInvalidSymbol() {
        try {
            String line = "AWAY,AWAY,LAND,WAIT,WAIT,LOAD@60@,TAKEOFF,AWAY";
            ControlTowerInitialiser.readTaskList(line);
            fail();
        } catch (MalformedSaveException e) {
            //more than one @ symbol
        }
    }

    @Test
    public void readTaskListInvalidSymbol2() {
        try {
            String line = "AWAY,AWAY,LAND,WAIT,WAIT,LOAD@60@65,TAKEOFF,AWAY";
            ControlTowerInitialiser.readTaskList(line);
            fail();
        } catch (MalformedSaveException e) {
            //more than one @ symbol
        }
    }

    @Test
    public void readTaskListInvalidSymbol3() {
        try {
            String line = "AWAY,AWAY,LAND,WAIT,WAIT,LOAD 60,TAKEOFF,AWAY";
            ControlTowerInitialiser.readTaskList(line);
            fail();
        } catch (MalformedSaveException e) {
            //space between load and load percentage
        }
    }

    @Test
    public void readTaskListInvalidSymbol4() {
        try {
            String line = "AWAY,AWAY,LAND,WAIT,WAIT,LOAD 60,TAKEOFF,AWAY,";
            ControlTowerInitialiser.readTaskList(line);
            fail();
        } catch (MalformedSaveException e) {
            //extra ','
        }
    }

    @Test
    public void readTaskListTaskListRules() {
        try {
            String line = "TAKEOFF,AWAY,LAND,WAIT,WAIT,LOAD@60@,TAKEOFF,AWAY";
            ControlTowerInitialiser.readTaskList(line);
            fail();
        } catch (MalformedSaveException e) {
            //breaks TaskList rules
        }
    }

    @Test
    public void readTaskListTaskListRulesMultipleSymbol() {
        try {
            String line = "TAKEOFF,AWAY,LAND,WAIT,WAIT,LOAD@60@65,TAKEOFF,AWAY";
            ControlTowerInitialiser.readTaskList(line);
            fail();
        } catch (MalformedSaveException e) {
            //breaks TaskList rules
        }
    }
    @Test
    public void readTaskListTaskListRules1() {
        try {
            String line = "LOAD,AWAY";
            ControlTowerInitialiser.readTaskList(line);
            fail();
        } catch (MalformedSaveException e) {
            //breaks TaskList rules
        }
    }
    @Test
    public void readTaskListTaskListRules2() {
        try {
            String line = " ";
            ControlTowerInitialiser.readTaskList(line);
            fail();
        } catch (MalformedSaveException e) {
            //breaks TaskList rules
        }
    }
    @Test
    public void readTaskListTaskListRules3() {
        try {
            String line = "WAIT,LAND";
            ControlTowerInitialiser.readTaskList(line);
            fail();
        } catch (MalformedSaveException e) {
            //breaks TaskList rules
        }
    }

    @Test
    public void loadQueuesBasic() {
        String fileContents = String.join(System.lineSeparator(),
                "TakeoffQueue:0",
                "LandingQueue:1",
                "VH-BFK",
                "LoadingAircraft:0");
        try {
            ControlTowerInitialiser.loadQueues(new StringReader(fileContents),aircrafts,
                    takeoffQueue,
                    landingQueue,loadingAircraftMap);
        } catch (MalformedSaveException e) {
            fail();
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void loadQueuesDefault() {
        String fileContents = String.join(System.lineSeparator(), "TakeoffQueue:0", "LandingQueue" +
                ":0", "LoadingAircraft:0");
        try {
            ControlTowerInitialiser.loadQueues(new StringReader(fileContents),aircrafts,takeoffQueue,
                    landingQueue,loadingAircraftMap);
        } catch (MalformedSaveException e) {
            fail();

        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void loadQueuesBoth() {
        String fileContents = String.join(System.lineSeparator(),
                "TakeoffQueue:1",
                "ABC002",
                "LandingQueue:1",
                "VH-BFK",
                "LoadingAircraft:0");
        try {
            ControlTowerInitialiser.loadQueues(new StringReader(fileContents),aircrafts,takeoffQueue,
                    landingQueue,loadingAircraftMap);
        } catch (MalformedSaveException e) {
            fail();

        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void loadQueuesThree() {
        String fileContents = String.join(System.lineSeparator(),
                "TakeoffQueue:1",
                "ABC002",
                "LandingQueue:1",
                "VH-BFK",
                "LoadingAircraft:1",
                "LOD001:3");
        try {
            ControlTowerInitialiser.loadQueues(new StringReader(fileContents),aircrafts,takeoffQueue,
                    landingQueue,loadingAircraftMap);
        } catch (MalformedSaveException e) {
            fail();

        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void readQueueDefault() {
        String fileContents = String.join(System.lineSeparator(), "TakeoffQueue:0", "LandingQueue" +
                ":0", "LoadingAircraft:0");
        BufferedReader br = new BufferedReader(new StringReader(fileContents));
        try {
            ControlTowerInitialiser.readQueue(br,aircrafts,takeoffQueue);
        } catch (IOException e) {
            fail();
        } catch (MalformedSaveException e) {
            fail();
        }
    }

    @Test
    public void readQueueBasic() {
        String fileContents = String.join(System.lineSeparator(),
                "TakeoffQueue:1",
                "ABC002",
                "LandingQueue:1",
                "VH-BFK",
                "LoadingAircraft:0");
        BufferedReader br = new BufferedReader(new StringReader(fileContents));
        try {
            ControlTowerInitialiser.readQueue(br,aircrafts,takeoffQueue);
        } catch (IOException e) {
            fail();
        } catch (MalformedSaveException e) {
            fail();
        }
    }

    @Test
    public void loadTerminalsWithGatesBasic() {

    }

    @Test
    public void loadTerminalsWithGatesDefault() {

    }

}