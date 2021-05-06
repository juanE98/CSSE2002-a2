package towersim.control;

import org.junit.Before;
import org.junit.Test;
import towersim.aircraft.AircraftCharacteristics;
import towersim.aircraft.FreightAircraft;
import towersim.aircraft.PassengerAircraft;
import towersim.tasks.Task;
import towersim.tasks.TaskList;
import towersim.tasks.TaskType;

import java.util.List;
import java.util.Queue;

import static org.junit.Assert.*;

public class LandingQueueTest {

    private PassengerAircraft aircraft1;
    private PassengerAircraft aircraft2;
    private PassengerAircraft aircraft3;
    private PassengerAircraft emptyAircraft;
    private PassengerAircraft emptyAircraft2;
    private PassengerAircraft fullAircraft;
    private TaskList taskList1;
    private FreightAircraft freightCraft1;
    private FreightAircraft freightCraft2;
    private LandingQueue landingQueueMix;
    private LandingQueue landingQueuePassenger;

    @Before
    public void setUp() throws Exception {
        this.taskList1 = new TaskList(List.of(
                new Task(TaskType.LOAD, 0), // load no passengers
                new Task(TaskType.TAKEOFF),
                new Task(TaskType.AWAY),
                new Task(TaskType.LAND)));

        TaskList taskList2 = new TaskList(List.of(
                new Task(TaskType.LOAD, 100), // load 100% of capacity of passengers
                new Task(TaskType.TAKEOFF),
                new Task(TaskType.AWAY),
                new Task(TaskType.LAND)));

        TaskList taskList3 = new TaskList(List.of(
                new Task(TaskType.LOAD, 50), // load 50% of capacity of passengers
                new Task(TaskType.TAKEOFF),
                new Task(TaskType.AWAY),
                new Task(TaskType.LAND)));
        this.aircraft1 = new PassengerAircraft("ABC001",
                AircraftCharacteristics.AIRBUS_A320,
                taskList1,
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity / 2,
                AircraftCharacteristics.AIRBUS_A320.passengerCapacity);

        this.aircraft2 = new PassengerAircraft("ABC002",
                AircraftCharacteristics.AIRBUS_A320,
                taskList2,
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity / 2,
                AircraftCharacteristics.AIRBUS_A320.passengerCapacity);

        this.aircraft3 = new PassengerAircraft("ABC003",
                AircraftCharacteristics.AIRBUS_A320,
                taskList3,
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity / 2,
                88);

        this.emptyAircraft = new PassengerAircraft("EMP001",
                AircraftCharacteristics.AIRBUS_A320,
                taskList1, AircraftCharacteristics.AIRBUS_A320.fuelCapacity, 0);

        this.emptyAircraft2 = new PassengerAircraft("EMP002",
                AircraftCharacteristics.AIRBUS_A320,
                taskList3, AircraftCharacteristics.AIRBUS_A320.fuelCapacity, 0);

        this.fullAircraft = new PassengerAircraft("FUL001",
                AircraftCharacteristics.AIRBUS_A320,
                taskList3, AircraftCharacteristics.AIRBUS_A320.fuelCapacity,
                AircraftCharacteristics.AIRBUS_A320.passengerCapacity - 20);

        this.freightCraft1 = new FreightAircraft("ABC001", AircraftCharacteristics.BOEING_747_8F,
                taskList1,
                AircraftCharacteristics.BOEING_747_8F.fuelCapacity * 0.6,
                AircraftCharacteristics.BOEING_747_8F.freightCapacity);

        this.freightCraft2 = new FreightAircraft("ABC002", AircraftCharacteristics.BOEING_747_8F,
                taskList3,
                AircraftCharacteristics.BOEING_747_8F.fuelCapacity * 0.6,
                60000);

        this.landingQueueMix = new LandingQueue();
        this.landingQueuePassenger = new LandingQueue();
    }

    @Test
    public void addAircraft() {
        this.landingQueuePassenger.addAircraft(aircraft1);
        this.landingQueuePassenger.addAircraft(aircraft2);
        LandingQueue compare = new LandingQueue();
        compare.addAircraft(aircraft1);
        compare.addAircraft(aircraft2);
        assertEquals(compare.getAircraftInOrder(),this.landingQueuePassenger.getAircraftInOrder());
    }

    @Test
    public void peekAircraft() {

    }

    @Test
    public void removeAircraft() {

    }

    @Test
    public void getAircraftInOrder() {
    }

    @Test
    public void containsAircraft() {
    }
}