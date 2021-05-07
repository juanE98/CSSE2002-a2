package towersim.control;

import org.junit.Before;
import org.junit.Test;
import towersim.aircraft.Aircraft;
import towersim.aircraft.AircraftCharacteristics;
import towersim.aircraft.FreightAircraft;
import towersim.aircraft.PassengerAircraft;
import towersim.tasks.Task;
import towersim.tasks.TaskList;
import towersim.tasks.TaskType;

import java.util.Arrays;
import java.util.LinkedList;
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
    public void peekAircraftBasic() {
        this.landingQueueMix.addAircraft(aircraft1);
        assertEquals(aircraft1, this.landingQueueMix.peekAircraft());
    }

    @Test
    public void peekAircraftEmergency() {
        this.freightCraft2.declareEmergency();
        this.landingQueueMix.addAircraft(aircraft1);
        this.landingQueueMix.addAircraft(freightCraft1);
        this.landingQueueMix.addAircraft(freightCraft2);
        assertEquals(this.freightCraft2, this.landingQueueMix.peekAircraft());

        //2nd emergency state aircraft added but should return first aircraft added
        this.aircraft3.declareEmergency();
        this.landingQueueMix.addAircraft(aircraft3);
        assertEquals(this.freightCraft2, this.landingQueueMix.peekAircraft());
    }


    @Test
    public void peekAircraftLowFuel() {
        PassengerAircraft lowFuel = new PassengerAircraft("LOW1",
                AircraftCharacteristics.AIRBUS_A320,
                this.taskList1,
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity * 0.2,
                88);
        PassengerAircraft lowFuel2 = new PassengerAircraft("LOW2",
                AircraftCharacteristics.AIRBUS_A320,
                taskList1,
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity * 0.1,
                88);
        this.landingQueuePassenger.addAircraft(aircraft1);
        this.landingQueuePassenger.addAircraft(aircraft2);
        this.landingQueuePassenger.addAircraft(lowFuel);
        assertEquals(lowFuel, this.landingQueuePassenger.peekAircraft());

        this.landingQueuePassenger.addAircraft(lowFuel2);
        assertNotEquals(lowFuel2, this.landingQueuePassenger.peekAircraft());

        this.landingQueueMix.addAircraft(aircraft2);
        this.landingQueueMix.addAircraft(freightCraft1);
        this.landingQueueMix.addAircraft(lowFuel2);
        assertEquals(lowFuel2, this.landingQueueMix.peekAircraft());
    }

    @Test
    public void peekAircraftPassengers() {
        this.landingQueueMix.addAircraft(freightCraft1);
        this.landingQueueMix.addAircraft(freightCraft2);
        this.landingQueueMix.addAircraft(aircraft3);
        this.landingQueueMix.addAircraft(aircraft2);
        assertEquals(aircraft3, this.landingQueueMix.peekAircraft());
    }

    @Test
    public void peekAircraftFirst(){
        FreightAircraft freightcraft3 = new FreightAircraft("FREG887",
                AircraftCharacteristics.BOEING_747_8F,
                taskList1,
                AircraftCharacteristics.BOEING_747_8F.fuelCapacity * 0.5,
                AircraftCharacteristics.BOEING_747_8F.freightCapacity);
        this.landingQueueMix.addAircraft(freightCraft2);
        this.landingQueueMix.addAircraft(freightCraft1);
        this.landingQueueMix.addAircraft(freightcraft3);
        assertEquals(freightCraft2, this.landingQueueMix.peekAircraft());
        assertEquals(null, this.landingQueuePassenger.peekAircraft());
    }

    @Test
    public void removeAircraftFirst() {
        this.landingQueueMix.addAircraft(aircraft1);
        this.landingQueueMix.addAircraft(aircraft2);
        this.landingQueueMix.addAircraft(freightCraft2);
        this.landingQueueMix.removeAircraft();
        Queue<Aircraft> landingQueueCompare = new LinkedList<Aircraft>(Arrays.asList(aircraft2,
                freightCraft2));
        assertEquals(landingQueueCompare, this.landingQueueMix.getAircraftInOrder());
    }

    @Test
    public void removeAircraftEmergency() {
        this.freightCraft2.declareEmergency();
        this.landingQueueMix.addAircraft(aircraft1);
        this.landingQueueMix.addAircraft(freightCraft1);
        this.landingQueueMix.addAircraft(freightCraft2);
        assertEquals(freightCraft2, this.landingQueueMix.removeAircraft());

        //2nd emergency state aircraft added, 1st one should've been removed
        this.aircraft2.declareEmergency();
        this.landingQueueMix.addAircraft(aircraft2);
        assertEquals(aircraft2,this.landingQueueMix.removeAircraft());
    }

    @Test
    public void removeAircraftLowFuel() {
        PassengerAircraft lowFuel = new PassengerAircraft("LOW1",
                AircraftCharacteristics.AIRBUS_A320,
                this.taskList1,
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity * 0.2,
                88);
        PassengerAircraft lowFuel2 = new PassengerAircraft("LOW2",
                AircraftCharacteristics.AIRBUS_A320,
                taskList1,
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity * 0.1,
                88);
        this.landingQueuePassenger.addAircraft(aircraft1);
        this.landingQueuePassenger.addAircraft(aircraft2);
        this.landingQueuePassenger.addAircraft(lowFuel);
       assertEquals(lowFuel, this.landingQueuePassenger.removeAircraft());

        this.landingQueuePassenger.addAircraft(lowFuel2);
        assertEquals(lowFuel2, this.landingQueuePassenger.removeAircraft());

        assertEquals(aircraft1, this.landingQueuePassenger.removeAircraft());
    }

    @Test
    public void removeAircraftPassengers() {
        this.landingQueueMix.addAircraft(freightCraft1);
        this.landingQueueMix.addAircraft(freightCraft2);
        this.landingQueueMix.addAircraft(aircraft3);
        this.landingQueueMix.addAircraft(aircraft2);
        assertEquals(aircraft3, this.landingQueueMix.removeAircraft());
        assertEquals(aircraft2, this.landingQueueMix.removeAircraft());

    }

    @Test
    public void getAircraftInOrderBasic() {
        this.landingQueueMix.addAircraft(aircraft3);
        this.landingQueueMix.addAircraft(aircraft1);
        Queue<Aircraft> landingQueueCompare = new LinkedList<Aircraft>(Arrays.asList(aircraft3,
                aircraft1));
        assertEquals(landingQueueCompare, this.landingQueueMix.getAircraftInOrder());
    }

    @Test
    public void getAircraftInOrderEmergency() {
        this.freightCraft2.declareEmergency();
        this.landingQueueMix.addAircraft(aircraft1);
        this.landingQueueMix.addAircraft(freightCraft1);
        this.landingQueueMix.addAircraft(freightCraft2);
        Queue<Aircraft> landingQueueCompare =
                new LinkedList<Aircraft>(Arrays.asList(freightCraft2, aircraft1,
                freightCraft1));
        assertEquals(landingQueueCompare, this.landingQueueMix.getAircraftInOrder());

    }

    @Test
    public void getAircraftInOrderLowFuel() {
        PassengerAircraft lowFuel = new PassengerAircraft("LOW1",
                AircraftCharacteristics.AIRBUS_A320,
                this.taskList1,
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity * 0.2,
                88);
        PassengerAircraft lowFuel2 = new PassengerAircraft("LOW2",
                AircraftCharacteristics.AIRBUS_A320,
                taskList1,
                AircraftCharacteristics.AIRBUS_A320.fuelCapacity * 0.1,
                88);
        this.landingQueuePassenger.addAircraft(aircraft1);
        this.landingQueuePassenger.addAircraft(aircraft2);
        this.landingQueuePassenger.addAircraft(lowFuel);
        Queue<Aircraft> landingQueueCompare =
                new LinkedList<Aircraft>(Arrays.asList(lowFuel, aircraft1,
                        aircraft2));

        assertEquals(landingQueueCompare, this.landingQueuePassenger.getAircraftInOrder());

        this.landingQueuePassenger.addAircraft(lowFuel2);
        Queue<Aircraft> landingQueueCompare2 =
                new LinkedList<Aircraft>(Arrays.asList(lowFuel,lowFuel2, aircraft1,
                        aircraft2));
        assertEquals(landingQueueCompare2, this.landingQueuePassenger.getAircraftInOrder());
    }

    @Test
    public void getAircraftInOrderPassengers() {
        this.landingQueueMix.addAircraft(freightCraft1);
        this.landingQueueMix.addAircraft(freightCraft2);
        this.landingQueueMix.addAircraft(aircraft3);
        this.landingQueueMix.addAircraft(aircraft2);
        Queue<Aircraft> landingQueueCompare =
                new LinkedList<Aircraft>(Arrays.asList(aircraft3, aircraft2,
                        freightCraft1,freightCraft2));
        assertEquals(landingQueueCompare, this.landingQueueMix.getAircraftInOrder());
    }

    @Test
    public void containsAircraft() {
        this.landingQueueMix.addAircraft(aircraft1);
        assertTrue(this.landingQueueMix.containsAircraft(aircraft1));
        assertFalse(this.landingQueueMix.containsAircraft(aircraft3));
        assertFalse(this.landingQueuePassenger.containsAircraft(null));

    }

    @Test
    public void toStringTest() {

    }
}