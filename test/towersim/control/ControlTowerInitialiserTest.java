package towersim.control;

import org.junit.Before;
import org.junit.Test;
import towersim.aircraft.Aircraft;
import towersim.aircraft.PassengerAircraft;
import towersim.util.MalformedSaveException;

import javax.naming.ldap.Control;
import java.io.*;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.*;

public class ControlTowerInitialiserTest {

    private Reader aircraftBasic;
    private Reader aircraftDefault;
    private Reader queuesBasic;
    private Reader queuesDefault;
    private Reader terminalsGatesBasic;
    private Reader terminalGatesDefault;
    private Reader tickBasic;
    private Reader tickDefault;
    private Map<Aircraft,Integer> loadingAircraftMap;
    private TakeoffQueue takeoffQueue;
    private LandingQueue landingQueue;


    @Before
    public void setUp() throws Exception {
        aircraftBasic = new FileReader("saves/aircraft_basic.txt");
        aircraftDefault = new FileReader("saves/aircraft_default.txt");
        queuesBasic = new FileReader("saves/queues_basic.txt");
        queuesDefault = new FileReader("saves/queues_default.txt");
        terminalsGatesBasic = new FileReader("saves/terminalsWithGates_basic.txt");
        terminalGatesDefault = new FileReader("saves/terminalsWithGates_default.txt");
        tickBasic = new FileReader("saves/tick_basic.txt");
        tickDefault = new FileReader("saves/tick_default.txt");
        takeoffQueue = new TakeoffQueue();
        landingQueue = new LandingQueue();
        loadingAircraftMap =
                new TreeMap<>(Comparator.comparing(Aircraft::getCallsign));

    }

    @Test
    public void loadAircraftDefault() {
        try {
            ControlTowerInitialiser.loadAircraft(aircraftDefault);
        } catch (IOException e) {
            fail();
        } catch (MalformedSaveException e) {
            fail();
        }
    }

    @Test public void loadAircraftBasic() {
        try {
            ControlTowerInitialiser.loadAircraft(aircraftBasic);
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
            String line = "AWAY,AWAY,LAND,WAIT,WAIT,LOAD@60@,TAKEOFF,AWAY, ";
            ControlTowerInitialiser.readTaskList(line);
            fail();
        } catch (MalformedSaveException e) {
            //null symbol
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
    public void loadQueuesDefault() {

    }

    @Test
    public void readQueueBasic() {

    }



}