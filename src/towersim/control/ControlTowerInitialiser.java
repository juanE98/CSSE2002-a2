package towersim.control;

import towersim.aircraft.Aircraft;
import towersim.aircraft.AircraftCharacteristics;
import towersim.aircraft.FreightAircraft;
import towersim.aircraft.PassengerAircraft;
import towersim.ground.Gate;
import towersim.ground.Terminal;
import towersim.tasks.Task;
import towersim.tasks.TaskList;
import towersim.tasks.TaskType;
import towersim.util.MalformedSaveException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility class that contains static methods for loading a control tower and associated
 * entities from files.
 */
public class ControlTowerInitialiser {

    /**
     * Loads the number of ticks elapsed from the given reader instance.
     * The contents of the reader should match the format specified in the tickWriter row of in
     * the  table shown in ViewModel.saveAs().
     *
     * For an example of valid tick reader contents, see the provided saves/tick_basic.txt and
     * saves/tick_default.txt files.
     *
     * The contents read from the reader are invalid if any of the following conditions are true:
     *
     * The number of ticks elapsed is not an integer (i.e. cannot be parsed by Long.parseLong(String)).
     * The number of ticks elapsed is less than zero.
     * @param reader reader from which to load the number of ticks elapsed
     * @return number of ticks elapsed
     * @throws MalformedSaveException if the format of the text read from the reader is invalid
     * according to the rules above
     * @throws IOException if an IOException is encountered when reading from the reader
     */
    public static long loadTick(Reader reader) throws MalformedSaveException, IOException {
        if (reader == null) {
            throw new IOException();
        }
        BufferedReader file = new BufferedReader(reader);
        long ticksElapsed;
        try {
            ticksElapsed = Long.parseLong(file.readLine());
        } catch (NumberFormatException e) {
            throw new MalformedSaveException();
        } catch (IOException e) {
            throw new IOException();
        }
        if (ticksElapsed < 0) {
            throw new MalformedSaveException();
        }
        return ticksElapsed;
    }

    /**
     * Loads the list of all aircraft managed by the control tower from the given reader instance.
     * The contents of the reader should match the format specified in the aircraftWriter row of
     * in the table shown in ViewModel.saveAs().
     *
     * For an example of valid aircraft reader contents, see the provided saves/aircraft_basic
     * .txt  and saves/aircraft_default.txt files.
     *
     * The contents read from the reader are invalid if any of the following conditions are true:
     *
     * The number of aircraft specified on the first line of the reader is not an integer (i.e.
     * cannot be parsed by Integer.parseInt(String)).
     * The number of aircraft specified on the first line is not equal to the number of aircraft
     * actually read from the reader.
     * Any of the conditions listed in the Javadoc for readAircraft(String) are true.
     * This method should call readAircraft(String).
     * @param reader reader from which to load the list of aircraft
     * @return list of aircraft read from the reader
     * @throws IOException if an IOException is encountered when reading from the reader
     * @throws MalformedSaveException if the format of the text read from the reader is invalid
     * according to the rules above
     */
    public static List<Aircraft> loadAircraft(Reader reader) throws IOException,
            MalformedSaveException {
        if (reader == null) {
            throw new IOException();
        }
        int numAircraft = 0; //number of aircrafts expected
        int numAircraftRead = 0; //number of aircrafts read from file
        List<Aircraft> aircrafts = new ArrayList<>(); //list of aircrafts to be returend
        BufferedReader file = new BufferedReader(reader);
        String aircraftEncoded;
        try {
            numAircraft = Integer.parseInt(file.readLine());
            if (numAircraft > 0) {
                while (((aircraftEncoded = file.readLine()) != null)) {
                    numAircraftRead++;
                    aircrafts.add(readAircraft(aircraftEncoded));
                }
            }
        } catch (NumberFormatException e) {
            throw new MalformedSaveException();
        } catch (IOException e) {
            throw new IOException();
        }

        if (numAircraft != numAircraftRead) {
            throw new MalformedSaveException();
        }
        return aircrafts;
    }

    /**
     * Reads an aircraft from its encoded representation in the given string.
     * If the AircraftCharacteristics.passengerCapacity of the encoded aircraft is greater than
     * zero,  then a PassengerAircraft should be created and returned. Otherwise, a
     * FreightAircraft  should be created and returned.
     *
     * The format of the string should match the encoded representation of an aircraft, as
     * described  in Aircraft.encode().
     *
     * The encoded string is invalid if any of the following conditions are true:
     *
     * More/fewer colons (:) are detected in the string than expected.
     * The aircraft's AircraftCharacteristics is not valid, i.e. it is not one of those listed in
     * AircraftCharacteristics.values().
     * The aircraft's fuel amount is not a double (i.e. cannot be parsed by Double.parseDouble(String)).
     * The aircraft's fuel amount is less than zero or greater than the aircraft's maximum fuel capacity.
     * The amount of cargo (freight/passengers) onboard the aircraft is not an integer (i.e.
     * cannot  be parsed by Integer.parseInt(String)).
     * The amount of cargo (freight/passengers) onboard the aircraft is less than zero or greater
     * than the aircraft's maximum freight/passenger capacity.
     * Any of the conditions listed in the Javadoc for readTaskList(String) are true.
     * @param line line of text containing the encoded aircraft
     * @return decoded aircraft instance
     * @throws MalformedSaveException if the format of the given string is invalid according  to
     * the rules above
     */
    public static Aircraft readAircraft (String line) throws MalformedSaveException {
        String callsign;
        AircraftCharacteristics aircraftCharacteristics;
        TaskList taskListEncoded;
        Double fuelAmount;
        String emergencyStatus;
        int cargo;
        Aircraft aircraftRead;
        String [] aircraftProperties = line.split(":");
        if (aircraftProperties.length != 6) {
            throw new MalformedSaveException();
        }
        try {
            callsign = aircraftProperties[0];
            aircraftCharacteristics = AircraftCharacteristics.valueOf(aircraftProperties[1]);
            taskListEncoded = readTaskList(aircraftProperties[2]);
            fuelAmount = Double.parseDouble(aircraftProperties[3]);
            emergencyStatus = aircraftProperties[4];
            cargo = Integer.parseInt(aircraftProperties[5]);
            if (cargo > 0) {
                aircraftRead = new PassengerAircraft(callsign, aircraftCharacteristics, taskListEncoded, fuelAmount,
                        cargo);
            }
            else {
                aircraftRead = new FreightAircraft(callsign, aircraftCharacteristics, taskListEncoded, fuelAmount,0);
            }

        } catch (IllegalArgumentException e) {
            throw new MalformedSaveException();
        }
        return aircraftRead;
    }


    /**
     *
     * @param taskListPart
     * @return
     * @throws MalformedSaveException
     */
    public static TaskList readTaskList (String taskListPart) throws MalformedSaveException {
        List<Task> taskList = new ArrayList<>();
        String [] task = taskListPart.split(",");
        for (String taskString : task) {
            if (taskString.contains("@")) {
                // check if taskString contains multiple '@'
                String [] taskLoad = taskString.split("@");
                if (taskLoad.length > 2) {
                    throw new MalformedSaveException();
                }
                try {
                    int loadPercent = Integer.parseInt(taskLoad[1]);
                    taskList.add(new Task(TaskType.valueOf(taskLoad[0]),loadPercent));
                    if (loadPercent < 0) {
                        throw new IllegalArgumentException();
                    }
                } catch (IllegalArgumentException e) {
                    throw new MalformedSaveException();
                }
            }
            taskList.add(new Task(TaskType.valueOf(taskString)));
        }
        return new TaskList(taskList);
    }

    public static void readQueue (BufferedReader reader, List<Aircraft> aircraft,
                                  AircraftQueue queue) throws IOException, MalformedSaveException {

    }

    public static void readLoadingAircraft (BufferedReader reader, List<Aircraft> aircraft,
                                            Map<Aircraft, Integer> loadingAircraft) throws IOException, MalformedSaveException {

    }

    public static Terminal readTerminal (String line, BufferedReader reader,
                                         List<Aircraft> aircraft) throws IOException,
            MalformedSaveException {

    }

    public static Gate readGate (String line, List<Aircraft> aircraft) throws MalformedSaveException {

    }

    /**
     * Loads the takeoff queue, landing queue and map of loading aircraft from the given reader
     * instance.
     * Rather than returning a list of queues, this method does not return anything. Instead,  it
     * should modify the given takeoff queue, landing queue and loading map by adding aircraft, etc.
     *
     * The contents of the reader should match the format specified in the queuesWriter row of in
     * the table shown in ViewModel.saveAs().
     *
     * For an example of valid queues reader contents, see the provided saves/queues_basic.txt
     * and  saves/queues_default.txt files.
     *
     * The contents read from the reader are invalid if any of the conditions listed in the
     * Javadoc  for readQueue(BufferedReader, List, AircraftQueue) and readLoadingAircraft
     * (BufferedReader, List, Map) are true.
     *
     * This method should call readQueue(BufferedReader, List, AircraftQueue) and
     * readLoadingAircraft(BufferedReader, List, Map).
     * @param reader reader from which to load the queues and loading map
     * @param aircraft list of all aircraft, used when validating that callsigns exist
     * @param takeoffQueue empty takeoff queue that aircraft will be added to
     * @param landingQueue empty landing queue that aircraft will be added to
     * @param loadingAircraft empty map that aircraft and loading times will be added to
     * @throws MalformedSaveException if the format of the text read from the reader is invalid
     * according to the rules above
     * @throws IOException if an IOException is encountered when reading from the reader
     */
    public static void loadQueues(Reader reader, List<Aircraft> aircraft,
                                   TakeoffQueue takeoffQueue, LandingQueue landingQueue,
                                   Map<Aircraft,Integer> loadingAircraft) throws MalformedSaveException, IOException {

    }

    /**
     * Loads the list of terminals and their gates from the given reader instance.
     * The contents of the reader should match the format specified in the
     * terminalsWithGatesWriter  row of in the table shown in ViewModel.saveAs().
     *
     * For an example of valid queues reader contents, see the provided
     * saves/terminalsWithGates_basic.txt and saves/terminalsWithGates_default.txt files.
     *
     * The contents read from the reader are invalid if any of the following conditions are true:
     *
     * The number of terminals specified at the top of the file is not an integer  (i.e. cannot
     * be parsed by Integer.parseInt(String)).
     * The number of terminals specified is not equal to the number of terminals actually read
     * from the reader.
     * Any of the conditions listed in the Javadoc for readTerminal(String, BufferedReader, List)
     * and readGate(String, List) are true.
     * This method should call readTerminal(String, BufferedReader, List).
     * @param reader reader from which to load the list of terminals and their gates
     * @param aircraft list of all aircraft, used when validating that callsigns exist
     * @return list of terminals (with their gates) read from the reader
     * @throws MalformedSaveException if the format of the text read from the reader is invalid
     * according to the rules above
     * @throws IOException if an IOException is encountered when reading from the reader
     */
    public static List<Terminal> loadTerminalsWithGates(Reader reader, List<Aircraft> aircraft) throws MalformedSaveException, IOException {

        return null;
    }

    /**
     * Creates a control tower instance by reading various airport entities from the given readers.
     * The following methods should be called in this order, and their results stored
     * temporarily,  to load information from the readers:
     *
     * loadTick(Reader) to load the number of elapsed ticks
     * loadAircraft(Reader) to load the list of all aircraft
     * loadTerminalsWithGates(Reader, List) to load the terminals and their gates
     * loadQueues(Reader, List, TakeoffQueue, LandingQueue, Map) to load the takeoff queue,
     * landing  queue and map of loading aircraft to their loading time remaining
     * Note: before calling loadQueues(), an empty takeoff queue and landing queue should be
     * created  by calling their respective constructors. Additionally, an empty map should be
     * created by calling:
     *
     * new TreeMap<>(Comparator.comparing(Aircraft::getCallsign))
     * This is important as it will ensure that the map is ordered by aircraft callsign
     * (lexicographically).
     * Once all information has been read from the readers, a new control tower should be
     * initialised  by calling ControlTower(long, List, LandingQueue, TakeoffQueue, Map).
     * Finally,  the terminals that have been read should be added to the control tower by
     * calling  ControlTower.addTerminal(Terminal).
     * @param tick reader from which to load the number of ticks elapsed
     * @param aircraft reader from which to load the list of aircraft
     * @param queues reader from which to load the aircraft queues and map of loading aircraft
     * @param terminalsWithGates reader from which to load the terminals and their gates
     * @return control tower created by reading from the given readers
     * @throws MalformedSaveException if reading from any of the given readers results in a
     * MalformedSaveException, indicating the contents of that reader are invalid
     * @throws IOException if an IOException is encountered when reading from any of the readers
     */
    public static ControlTower createControlTower(Reader tick, Reader aircraft, Reader queues,
                                                  Reader terminalsWithGates) throws MalformedSaveException, IOException {

        return null;
    }
}
