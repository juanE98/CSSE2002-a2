package towersim.control;

import towersim.aircraft.Aircraft;

import java.util.List;

public class TakeoffQueue extends AircraftQueue {

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
