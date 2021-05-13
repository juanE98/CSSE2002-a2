package towersim.control;

import towersim.aircraft.Aircraft;
import towersim.aircraft.AircraftCharacteristics;
import towersim.aircraft.FreightAircraft;
import towersim.aircraft.PassengerAircraft;
import towersim.ground.AirplaneTerminal;
import towersim.ground.Gate;
import towersim.ground.HelicopterTerminal;
import towersim.ground.Terminal;
import towersim.tasks.Task;
import towersim.tasks.TaskList;
import towersim.tasks.TaskType;
import towersim.util.MalformedSaveException;
import towersim.util.NoSpaceException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

/**
 * Utility class that contains static methods for loading a control tower and associated
 * entities from files.
 */
public class ControlTowerInitialiser {

    /**
     * Loads the number of ticks elapsed from the given reader instance.
     * The contents of the reader should match the format specified in the tickWriter row of in
     * the  table shown in ViewModel.saveAs().
     * <p>
     * For an example of valid tick reader contents, see the provided saves/tick_basic.txt and
     * saves/tick_default.txt files.
     * <p>
     * The contents read from the reader are invalid if any of the following conditions are true:
     * <p>
     * The number of ticks elapsed is not an integer (i.e. cannot be parsed by  Long.parseLong
     * (String)).
     * The number of ticks elapsed is less than zero.
     *
     * @param reader reader from which to load the number of ticks elapsed
     * @return number of ticks elapsed
     * @throws MalformedSaveException if the format of the text read from the reader is invalid
     *                                according to the rules above
     * @throws IOException            if an IOException is encountered when reading from the reader
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
        } finally {
            file.close();
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
     * <p>
     * For an example of valid aircraft reader contents, see the provided saves/aircraft_basic
     * .txt  and saves/aircraft_default.txt files.
     * <p>
     * The contents read from the reader are invalid if any of the following conditions are true:
     * <p>
     * The number of aircraft specified on the first line of the reader is not an integer (i.e.
     * cannot be parsed by Integer.parseInt(String)).
     * The number of aircraft specified on the first line is not equal to the number of aircraft
     * actually read from the reader.
     * Any of the conditions listed in the Javadoc for readAircraft(String) are true.
     * This method should call readAircraft(String).
     *
     * @param reader reader from which to load the list of aircraft
     * @return list of aircraft read from the reader
     * @throws IOException            if an IOException is encountered when reading from the reader
     * @throws MalformedSaveException if the format of the text read from the reader is invalid
     *                                according to the rules above
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
        } finally {
            file.close();
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
     * <p>
     * The format of the string should match the encoded representation of an aircraft, as
     * described  in Aircraft.encode().
     * <p>
     * The encoded string is invalid if any of the following conditions are true:
     * <p>
     * More/fewer colons (:) are detected in the string than expected.
     * The aircraft's AircraftCharacteristics is not valid, i.e. it is not one of those listed in
     * AircraftCharacteristics.values().
     * The aircraft's fuel amount is not a double (i.e. cannot be parsed by  Double.parseDouble
     * (String)).
     * The aircraft's fuel amount is less than zero or greater than the aircraft's maximum fuel
     * capacity.
     * The amount of cargo (freight/passengers) onboard the aircraft is not an integer (i.e.
     * cannot  be parsed by Integer.parseInt(String)).
     * The amount of cargo (freight/passengers) onboard the aircraft is less than zero or greater
     * than the aircraft's maximum freight/passenger capacity.
     * Any of the conditions listed in the Javadoc for readTaskList(String) are true.
     *
     * @param line line of text containing the encoded aircraft
     * @return decoded aircraft instance
     * @throws MalformedSaveException if the format of the given string is invalid according  to
     *                                the rules above
     */
    public static Aircraft readAircraft(String line) throws MalformedSaveException {
        String callsign;
        AircraftCharacteristics aircraftCharacteristics;
        TaskList taskListEncoded;
        Double fuelAmount;
        boolean emergencyStatus;
        int cargo;
        Aircraft aircraftRead;
        String[] aircraftProperties = line.split(":");
        if (aircraftProperties.length != 6) {
            throw new MalformedSaveException();
        }
        try {
            callsign = aircraftProperties[0];
            aircraftCharacteristics = AircraftCharacteristics.valueOf(aircraftProperties[1]);
            taskListEncoded = readTaskList(aircraftProperties[2]);
            fuelAmount = Double.parseDouble(aircraftProperties[3]);
            emergencyStatus = Boolean.parseBoolean(aircraftProperties[4]);
            cargo = Integer.parseInt(aircraftProperties[5]);
            //check if cargo is a negative value
            if (cargo < 0) {
                throw new MalformedSaveException();
            }
            //PassengerAircraft is created
            if (cargo > 0) {
                aircraftRead = new PassengerAircraft(callsign, aircraftCharacteristics,
                        taskListEncoded, fuelAmount,
                        cargo);
                if (cargo > aircraftRead.getCharacteristics().passengerCapacity) {
                    throw new MalformedSaveException();
                }
            } else {
                //FreightAircraft is created
                aircraftRead = new FreightAircraft(callsign, aircraftCharacteristics,
                        taskListEncoded, fuelAmount, 0);
            }
        } catch (IllegalArgumentException e) {
            throw new MalformedSaveException();
        }
        if (emergencyStatus) {
            aircraftRead.declareEmergency();
        }
        return aircraftRead;
    }

    /**
     * Reads a task list from its encoded representation in the given string.
     * The format of the string should match the encoded representation of a task list, as
     * described  in TaskList.encode().
     * <p>
     * The encoded string is invalid if any of the following conditions are true:
     * <p>
     * The task list's TaskType is not valid (i.e. it is not one of those listed in TaskType
     * .values()).
     * A task's load percentage is not an integer (i.e. cannot be parsed by Integer.parseInt
     * (String)).
     * A task's load percentage is less than zero.
     * More than one at-symbol (@) is detected for any task in the task list.
     * The task list is invalid according to the rules specified in TaskList(List).
     *
     * @param taskListPart string containing the encoded task list
     * @return decoded task list instance
     * @throws MalformedSaveException if the format of the given string is invalid according to
     *                                the rules above
     */
    public static TaskList readTaskList(String taskListPart) throws MalformedSaveException {
        //tasks to be added to TaskList returned
        List<Task> taskList = new ArrayList<>();
        String[] task = taskListPart.split(",");
        for (String taskString : task) {
            try {
                if (taskString.contains("@")) {
                    // counter for the @ symbol within the String.
                    int symbolCounter = 0;
                    for (int i = 0; i < taskString.length(); i++) {
                        char c = taskString.charAt(i);
                        if (c == '@') {
                            symbolCounter++;
                        }
                    }
                    //if more than one @ symbol is found, throw exception.
                    if (symbolCounter > 1) {
                        throw new MalformedSaveException();
                    }
                    // check if taskString contains multiple '@'
                    String[] taskLoad = taskString.split("@");

                    int loadPercent = Integer.parseInt(taskLoad[1]);
                    Task taskSpecific = new Task(TaskType.valueOf(taskLoad[0]), loadPercent);
                    taskList.add(taskSpecific);
                    //loadPercent cannot be negative number
                    if (loadPercent < 0) {
                        throw new IllegalArgumentException();
                    }
                } else {
                    taskList.add(new Task(TaskType.valueOf(taskString)));
                }
            } catch (IllegalArgumentException e) {
                throw new MalformedSaveException();
            }
        }
        try {
            return new TaskList(taskList);
        } catch (IllegalArgumentException e) {
            throw new MalformedSaveException();
        }
    }

    /**
     * Loads the takeoff queue, landing queue and map of loading aircraft from the given reader
     * instance.
     * Rather than returning a list of queues, this method does not return anything. Instead,  it
     * should modify the given takeoff queue, landing queue and loading map by adding aircraft, etc.
     * <p>
     * The contents of the reader should match the format specified in the queuesWriter row of in
     * the table shown in ViewModel.saveAs().
     * <p>
     * For an example of valid queues reader contents, see the provided saves/queues_basic.txt
     * and  saves/queues_default.txt files.
     * <p>
     * The contents read from the reader are invalid if any of the conditions listed in the
     * Javadoc  for readQueue(BufferedReader, List, AircraftQueue) and readLoadingAircraft
     * (BufferedReader, List, Map) are true.
     * <p>
     * This method should call readQueue(BufferedReader, List, AircraftQueue) and
     * readLoadingAircraft(BufferedReader, List, Map).
     *
     * @param reader          reader from which to load the queues and loading map
     * @param aircraft        list of all aircraft, used when validating that callsigns exist
     * @param takeoffQueue    empty takeoff queue that aircraft will be added to
     * @param landingQueue    empty landing queue that aircraft will be added to
     * @param loadingAircraft empty map that aircraft and loading times will be added to
     * @throws MalformedSaveException if the format of the text read from the reader is invalid
     *                                according to the rules above
     * @throws IOException            if an IOException is encountered when reading from the reader
     */
    public static void loadQueues(Reader reader, List<Aircraft> aircraft,
                                  TakeoffQueue takeoffQueue, LandingQueue landingQueue,
                                  Map<Aircraft, Integer> loadingAircraft)
            throws MalformedSaveException, IOException {
        if (reader == null) {
            throw new IOException();
        }
        BufferedReader file = new BufferedReader(reader);
        try {
            readQueue(file, aircraft, takeoffQueue);
            readQueue(file, aircraft, landingQueue);
            readLoadingAircraft(file, aircraft, loadingAircraft);
        } catch (IOException e) {
            throw new IOException();
        } catch (MalformedSaveException e) {
            throw new MalformedSaveException();
        } finally {
            file.close();
        }
    }

    /**
     * Reads an aircraft queue from the given reader instance.
     * Rather than returning a queue, this method does not return anything. Instead, it  should
     * modify the given aircraft queue by adding aircraft to it.
     * <p>
     * The contents of the text read from the reader should match the encoded representation of
     * an aircraft queue, as described in AircraftQueue.encode().
     * <p>
     * The contents read from the reader are invalid if any of the following conditions are true:
     * <p>
     * The first line read from the reader is null.
     * The first line contains more/fewer colons (:) than expected.
     * The queue type specified in the first line is not equal to the simple class name of the
     * queue provided as a parameter.
     * The number of aircraft specified on the first line is not an integer (i.e. cannot be
     * parsed  by Integer.parseInt(String)).
     * The number of aircraft specified is greater than zero and the second line read is null.
     * The number of callsigns listed on the second line is not equal to the number of aircraft
     * specified on the first line.
     * A callsign listed on the second line does not correspond to the callsign of any aircraft
     * contained in the list of aircraft given as a parameter.
     *
     * @param reader   reader from which to load the aircraft queue
     * @param aircraft list of all aircraft, used when validating that callsigns exist
     * @param queue    empty queue that aircraft will be added to
     * @throws IOException            if an IOException is encountered when reading from the reader
     * @throws MalformedSaveException if the format of the text read from the reader is invalid
     *                                according to the rules above
     */
    public static void readQueue(BufferedReader reader, List<Aircraft> aircraft,
                                 AircraftQueue queue) throws IOException, MalformedSaveException {
        int numAircraft;
        //actual number of aircrafts read
        int numAircraftRead = 0;
        String line;
        //length of array with one colon as a delimeter
        int colonsExpected = 2;
        try {
            String[] queueParts = reader.readLine().split(":");
            if (queueParts.length != colonsExpected) {
                throw new MalformedSaveException();
            }
            if (!queueParts[0].equals(queue.getClass().getSimpleName())) {
                throw new MalformedSaveException();
            }
            numAircraft = Integer.parseInt(queueParts[1]);
            if (numAircraft > 0) {
                String [] aircraftsInQueue = reader.readLine().split(",");
                if (aircraftsInQueue.length == 0) {
                    throw new MalformedSaveException();
                }
                numAircraftRead = aircraftsInQueue.length;
                if (numAircraft != numAircraftRead) {
                    throw new MalformedSaveException();
                }
                for (String aircraftRead : aircraftsInQueue) {
                    boolean aircraftFound = false;
                    for (Aircraft aircraftListed : aircraft) {
                        String aircraftCallsign = aircraftListed.getCallsign();
                        if (aircraftCallsign.equals(aircraftRead)) {
                            aircraftFound = true;
                            queue.addAircraft(aircraftListed);
                        }
                    }
                    if (!aircraftFound) {
                        throw new MalformedSaveException();
                    }
                }
            }

        } catch (IOException e) {
            throw new IOException();
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new MalformedSaveException();
        }
    }

    /**
     * Reads the map of currently loading aircraft from the given reader instance.
     * Rather than returning a map, this method does not return anything. Instead, it should
     * modify  the given map by adding entries (aircraft/integer pairs) to it.
     * <p>
     * The contents of the text read from the reader should match the format specified in the
     * queuesWriter row of in the table shown in ViewModel.saveAs(). Note that this method should
     * only read the map of loading aircraft, not the takeoff queue or landing queue. Reading
     * these  queues is handled in the readQueue(BufferedReader, List, AircraftQueue) method.
     * <p>
     * For an example of valid encoded map of loading aircraft, see the provided
     * saves/queues_basic.txt and saves/queues_default.txt files.
     * <p>
     * The contents read from the reader are invalid if any of the following conditions are true:
     * <p>
     * The first line read from the reader is null.
     * The number of colons (:) detected on the first line is more/fewer than expected.
     * The number of aircraft specified on the first line is not an integer (i.e. cannot be
     * parsed  by Integer.parseInt(String)).
     * The number of aircraft is greater than zero and the second line read from the reader is null.
     * The number of aircraft specified on the first line is not equal to the number of callsigns
     * read on the second line.
     * For any callsign/loading time pair on the second line, the number of colons detected is
     * not equal to one. For example, ABC123:5:9 is invalid.
     * A callsign listed on the second line does not correspond to the callsign of any aircraft
     * contained in the list of aircraft given as a parameter.
     * Any ticksRemaining value on the second line is not an integer (i.e. cannot be parsed by
     * Integer.parseInt(String)).
     * Any ticksRemaining value on the second line is less than one (1).
     *
     * @param reader          reader from which to load the map of loading aircraft
     * @param aircraft        list of all aircraft, used when validating that callsigns exist
     * @param loadingAircraft empty map that aircraft and their loading times will be added to
     * @throws IOException            if an IOException is encountered when reading from the reader
     * @throws MalformedSaveException if the format of the text read from the reader is  invalid
     *                                according to the rules above
     */
    public static void readLoadingAircraft(BufferedReader reader, List<Aircraft> aircraft,
                                           Map<Aircraft, Integer> loadingAircraft)
            throws IOException, MalformedSaveException {
        int numAircraft;
        int numAircraftRead = 0;
        try {
            String[] loadingAircraftParts = reader.readLine().split(":");
            if (loadingAircraftParts.length > 2) {
                throw new MalformedSaveException();
            }
            numAircraft = Integer.parseInt(loadingAircraftParts[1]);
            if (numAircraft > 0) {
                String loadingAircraftRead;
                while ((loadingAircraftRead = reader.readLine()) != null) {
                    numAircraftRead++;
                    boolean aircraftFound = false;
                    String[] loadingMap = loadingAircraftRead.split(":");
                    if (loadingMap.length > 2) {
                        throw new MalformedSaveException();
                    }
                    int ticksRemaining = Integer.parseInt(loadingMap[1]);
                    if (ticksRemaining < 1) {
                        throw new MalformedSaveException();
                    }
                    for (Aircraft aircraftParked : aircraft) {
                        if (aircraftParked.getCallsign().equals(loadingMap[0])) {
                            aircraftFound = true;
                            loadingAircraft.put(aircraftParked, ticksRemaining);
                            break;
                        }
                    }
                    if (!aircraftFound) {
                        throw new MalformedSaveException();
                    }
                }
            }
            if (numAircraft != numAircraftRead) {
                throw new MalformedSaveException();
            }
        } catch (IOException e) {
            throw new IOException();
        } catch (NullPointerException | IllegalArgumentException e) {
            throw new MalformedSaveException();
        }
    }

    /**
     * Loads the list of terminals and their gates from the given reader instance.
     * The contents of the reader should match the format specified in the
     * terminalsWithGatesWriter  row of in the table shown in ViewModel.saveAs().
     * <p>
     * For an example of valid queues reader contents, see the provided
     * saves/terminalsWithGates_basic.txt and saves/terminalsWithGates_default.txt files.
     * <p>
     * The contents read from the reader are invalid if any of the following conditions are true:
     * <p>
     * The number of terminals specified at the top of the file is not an integer  (i.e. cannot
     * be parsed by Integer.parseInt(String)).
     * The number of terminals specified is not equal to the number of terminals actually read
     * from the reader.
     * Any of the conditions listed in the Javadoc for readTerminal(String, BufferedReader, List)
     * and readGate(String, List) are true.
     * This method should call readTerminal(String, BufferedReader, List).
     *
     * @param reader   reader from which to load the list of terminals and their gates
     * @param aircraft list of all aircraft, used when validating that callsigns exist
     * @return list of terminals (with their gates) read from the reader
     * @throws MalformedSaveException if the format of the text read from the reader is invalid
     *                                according to the rules above
     * @throws IOException if an IOException is encountered when reading from the reader
     */
    public static List<Terminal> loadTerminalsWithGates(Reader reader, List<Aircraft> aircraft)
            throws MalformedSaveException, IOException {
        if (reader == null) {
            throw new IOException();
        }
        List<Terminal> terminalsLoaded = new ArrayList<>();
        int numTerminals = 0;
        BufferedReader file = new BufferedReader(reader);
        try {
            numTerminals = Integer.parseInt(file.readLine());
            String terminalLine;
            if (numTerminals > 0) {
                while ((terminalLine = file.readLine()) != null) {
                    terminalsLoaded.add(readTerminal(terminalLine, file, aircraft));
                }
            }
        } catch (NumberFormatException e) {
            throw new MalformedSaveException();
        } finally {
            file.close();
        }
        if (terminalsLoaded.size() != numTerminals) {
            throw new MalformedSaveException();
        }
        return terminalsLoaded;
    }

    /**
     * Reads a terminal from the given string and reads its gates from the given reader instance.
     * The format of the given string and the text read from the reader should match the encoded
     * representation of a terminal, as described in Terminal.encode().
     * <p>
     * For an example of valid encoded terminal with gates, see the provided
     * saves/terminalsWithGates_basic.txt and saves/terminalsWithGates_default.txt files.
     * <p>
     * The encoded terminal is invalid if any of the following conditions are true:
     * <p>
     * The number of colons (:) detected on the first line is more/fewer than expected.
     * The terminal type specified on the first line is neither AirplaneTerminal nor
     * HelicopterTerminal.
     * The terminal number is not an integer (i.e. cannot be parsed by Integer.parseInt(String)).
     * The terminal number is less than one (1).
     * The number of gates in the terminal is not an integer.
     * The number of gates is less than zero or is greater than Terminal.MAX_NUM_GATES.
     * A line containing an encoded gate was expected, but EOF (end of file) was received (i.e.
     * BufferedReader.readLine() returns null).
     * Any of the conditions listed in the Javadoc for readGate(String, List) are true.
     *
     * @param line     string containing the first line of the encoded terminal
     * @param reader   reader from which to load the gates of the terminal (subsequent lines)
     * @param aircraft list of all aircraft, used when validating that callsigns exist
     * @return decoded terminal with its gates added
     * @throws IOException            if an IOException is encountered when reading from the reader
     * @throws MalformedSaveException if the format of the given string or the text read from the
     *                                reader is invalid according to the rules above
     */
    public static Terminal readTerminal(String line, BufferedReader reader,
                                        List<Aircraft> aircraft) throws IOException,
            MalformedSaveException {
        Terminal terminalRead;
        String terminalType;
        int numGates = 0;
        int colonsExpected = 4;
        int terminalNumber;
        boolean emergencyStatus;
        String[] terminalParts = line.split(":");
        if (terminalParts.length != colonsExpected) {
            throw new MalformedSaveException();
        }
        try {
            numGates = Integer.parseInt(terminalParts[3]);
            List<Gate> gatesOfTerminal = new ArrayList<>();
            terminalType = terminalParts[0];
            terminalNumber = Integer.parseInt(terminalParts[1]);
            emergencyStatus = Boolean.parseBoolean(terminalParts[2]);
            if (terminalNumber < 1) {
                throw new MalformedSaveException();
            }
            if (numGates < 0 || numGates > Terminal.MAX_NUM_GATES) {
                throw new MalformedSaveException();
            }
            //Create Terminal
            if (terminalType.equals("AirplaneTerminal")) {
                terminalRead = new AirplaneTerminal(terminalNumber);
            } else if (terminalType.equals("HelicopterTerminal")) {
                terminalRead = new HelicopterTerminal(terminalNumber);
            } else {
                throw new MalformedSaveException();
            }
            //read gates based on numGates
            for (int i = 0; i < numGates; i++) {
                line = reader.readLine();
                if (line == null) {
                    throw new IOException();
                }
                terminalRead.addGate(readGate(line, aircraft));
            }
        } catch (IllegalArgumentException | NoSpaceException e) {
            throw new MalformedSaveException();
        }
        if (emergencyStatus) {
            terminalRead.declareEmergency();
        }
        return terminalRead;
    }

    /**
     * Reads a gate from its encoded representation in the given string.
     * The format of the string should match the encoded representation of a gate, as described
     * in  Gate.encode().
     * <p>
     * The encoded string is invalid if any of the following conditions are true:
     * <p>
     * The number of colons (:) detected was more/fewer than expected.
     * The gate number is not an integer (i.e. cannot be parsed by Integer.parseInt(String)).
     * The gate number is less than one (1).
     * The callsign of the aircraft parked at the gate is not empty and the callsign does not
     * correspond to the callsign of any aircraft contained in the list of aircraft given as a
     * parameter.
     *
     * @param line     string containing the encoded gate
     * @param aircraft list of all aircraft, used when validating that callsigns exist
     * @return decoded gate instance
     * @throws MalformedSaveException if the format of the given string is invalid according to
     *                                the rules above
     */
    public static Gate readGate(String line, List<Aircraft> aircraft)
            throws MalformedSaveException {
        Gate gateRead;
        int colonsExpected = 2;
        int gateNumber;
        String space;
        String[] gateParts = line.split(":");
        if (gateParts.length != colonsExpected) {
            throw new MalformedSaveException();
        }
        try {
            gateNumber = Integer.parseInt(gateParts[0]);
            if (gateNumber < 1) {
                throw new MalformedSaveException();
            }
            space = gateParts[1];
            gateRead = new Gate(gateNumber);

            if (!space.equals("empty")) {
                //park aircraft at Gate
                for (Aircraft aircraftPark : aircraft) {
                    if (aircraftPark.getCallsign().equals(space)) {
                        gateRead.parkAircraft(aircraftPark);
                    }
                }
            }
        } catch (IllegalArgumentException | NoSpaceException e) {
            throw new MalformedSaveException();
        }
        return gateRead;
    }

    /**
     * Creates a control tower instance by reading various airport entities from the given readers.
     * The following methods should be called in this order, and their results stored
     * temporarily,  to load information from the readers:
     * <p>
     * loadTick(Reader) to load the number of elapsed ticks
     * loadAircraft(Reader) to load the list of all aircraft
     * loadTerminalsWithGates(Reader, List) to load the terminals and their gates
     * loadQueues(Reader, List, TakeoffQueue, LandingQueue, Map) to load the takeoff queue,
     * landing  queue and map of loading aircraft to their loading time remaining
     * Note: before calling loadQueues(), an empty takeoff queue and landing queue should be
     * created  by calling their respective constructors. Additionally, an empty map should be
     * created by calling:
     * <p>
     * new TreeMap<>(Comparator.comparing(Aircraft::getCallsign))
     * This is important as it will ensure that the map is ordered by aircraft callsign
     * (lexicographically).
     * Once all information has been read from the readers, a new control tower should be
     * initialised  by calling ControlTower(long, List, LandingQueue, TakeoffQueue, Map).
     * Finally,  the terminals that have been read should be added to the control tower by
     * calling  ControlTower.addTerminal(Terminal).
     *
     * @param tick               reader from which to load the number of ticks elapsed
     * @param aircraft           reader from which to load the list of aircraft
     * @param queues             reader from which to load the aircraft queues and map of loading
     *                           aircraft
     * @param terminalsWithGates reader from which to load the terminals and their gates
     * @return control tower created by reading from the given readers
     * @throws MalformedSaveException if reading from any of the given readers results in a
     *                                MalformedSaveException, indicating the contents of that
     *                                reader are invalid
     * @throws IOException            if an IOException is encountered when reading from any of
     *                                the readers
     */
    public static ControlTower createControlTower(Reader tick, Reader aircraft, Reader queues,
                                                  Reader terminalsWithGates)
            throws MalformedSaveException, IOException {
        long controlTowerTick = loadTick(tick);
        List<Aircraft> controlTowerAircrafts = loadAircraft(aircraft);
        List<Terminal> controlTowerTerminals = loadTerminalsWithGates(terminalsWithGates,
                controlTowerAircrafts);

        TakeoffQueue takeoffQueue = new TakeoffQueue();
        LandingQueue landingQueue = new LandingQueue();
        Map<Aircraft, Integer> loadingAircraftMap =
                new TreeMap<>(Comparator.comparing(Aircraft::getCallsign));
        loadQueues(queues, controlTowerAircrafts, takeoffQueue, landingQueue, loadingAircraftMap);
        ControlTower controlTower = new ControlTower(controlTowerTick, controlTowerAircrafts,
                landingQueue,
                takeoffQueue, loadingAircraftMap);
        for (Terminal terminal : controlTowerTerminals) {
            controlTower.addTerminal(terminal);
        }
        return controlTower;
    }
}
