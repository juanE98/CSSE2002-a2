package towersim.control;

import org.junit.Before;
import org.junit.Test;
import towersim.aircraft.Aircraft;
import towersim.aircraft.AircraftCharacteristics;
import towersim.aircraft.PassengerAircraft;
import towersim.tasks.Task;
import towersim.tasks.TaskList;
import towersim.tasks.TaskType;

import java.util.List;

import static org.junit.Assert.*;

public class AircraftQueueTest {

    private PassengerAircraft aircraft1;
    private PassengerAircraft aircraft2;
    private PassengerAircraft aircraft3;
    private TaskList taskList1;
    private DummyAircraftQueue dummyAircraftQueue1;

    class DummyAircraftQueue extends AircraftQueue {

        @Override
        public void addAircraft(Aircraft aircraft) {

        }

        @Override
        public Aircraft removeAircraft() {
            return null;
        }

        @Override
        public Aircraft peekAircraft() {
            return null;
        }

        @Override
        public List<Aircraft> getAircraftInOrder() {
            return null;
        }

        @Override
        public boolean containsAircraft(Aircraft aircraft) {
            return false;
        }
    }


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

        this.dummyAircraftQueue1 = new DummyAircraftQueue();
    }

    @Test
    public void testToString() {

        assertEquals("",this.aircraft1.toString());
    }

    @Test
    public void encode() {
    }
}