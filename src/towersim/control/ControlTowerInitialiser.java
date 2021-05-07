package towersim.control;

import towersim.aircraft.Aircraft;
import towersim.ground.Terminal;
import towersim.util.MalformedSaveException;

import java.io.IOException;
import java.io.Reader;
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
        return 0;
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
     * @throws IOException if the format of the text read from the reader is invalid according to
     * the rules above
     * @throws MalformedSaveException if an IOException is encountered when reading from the reader
     */
    public static List<Aircraft> loadAircraft(Reader reader) throws IOException,
            MalformedSaveException {

        return null;
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
     * The contents of the reader should match the format specified in the terminalsWithGatesWriter row of in the table shown in ViewModel.saveAs().
     *
     * For an example of valid queues reader contents, see the provided saves/terminalsWithGates_basic.txt and saves/terminalsWithGates_default.txt files.
     *
     * The contents read from the reader are invalid if any of the following conditions are true:
     *
     * The number of terminals specified at the top of the file is not an integer (i.e. cannot be parsed by Integer.parseInt(String)).
     * The number of terminals specified is not equal to the number of terminals actually read from the reader.
     * Any of the conditions listed in the Javadoc for readTerminal(String, BufferedReader, List) and readGate(String, List) are true.
     * This method should call readTerminal(String, BufferedReader, List).
     * @param reader reader from which to load the list of terminals and their gates
     * @param aircraft list of all aircraft, used when validating that callsigns exist
     * @return list of terminals (with their gates) read from the reader
     * @throws MalformedSaveException if the format of the text read from the reader is invalid according to the rules above
     * @throws IOException if an IOException is encountered when reading from the reader
     */
    public static List<Terminal> loadTerminalsWithGates(Reader reader, List<Aircraft> aircraft) throws MalformedSaveException, IOException {

        return null;
    }

    /**
     * Creates a control tower instance by reading various airport entities from the given readers.
     * The following methods should be called in this order, and their results stored temporarily, to load information from the readers:
     *
     * loadTick(Reader) to load the number of elapsed ticks
     * loadAircraft(Reader) to load the list of all aircraft
     * loadTerminalsWithGates(Reader, List) to load the terminals and their gates
     * loadQueues(Reader, List, TakeoffQueue, LandingQueue, Map) to load the takeoff queue,
     * landing  queue and map of loading aircraft to their loading time remaining
     * Note: before calling loadQueues(), an empty takeoff queue and landing queue should be
     * created  by calling their respective constructors. Additionally, an empty map should be created by calling:
     *
     * new TreeMap<>(Comparator.comparing(Aircraft::getCallsign))
     * This is important as it will ensure that the map is ordered by aircraft callsign (lexicographically).
     * Once all information has been read from the readers, a new control tower should be
     * initialised  by calling ControlTower(long, List, LandingQueue, TakeoffQueue, Map).
     * Finally,  the terminals that have been read should be added to the control tower by calling ControlTower.addTerminal(Terminal).
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
