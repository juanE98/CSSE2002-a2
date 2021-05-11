package towersim.control;

import org.junit.Before;
import org.junit.Test;
import towersim.aircraft.Aircraft;
import towersim.aircraft.PassengerAircraft;
import towersim.util.MalformedSaveException;

import javax.naming.ldap.Control;
import java.io.*;
import java.util.List;

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

    }

    @Test
    public void readTaskList() {

    }
}