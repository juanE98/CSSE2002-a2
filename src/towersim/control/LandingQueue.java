package towersim.control;

import towersim.aircraft.Aircraft;

import java.util.List;

/**
 * Represents a rule-based queue of aircraft waiting in the air to land.
 * The rules in the landing queue are designed to ensure that aircraft are prioritised for
 * landing based on "urgency" factors such as remaining fuel onboard, emergency status and cargo type.
 */
public class LandingQueue extends AircraftQueue {

    /**
     * Constructs a new LandingQueue with an initially empty queue of aircraft.
     */
    public LandingQueue() {
        
    }

    @Override
    public void addAircraft(Aircraft aircraft) {

    }

    @Override
    public Aircraft peekAircraft() {
        return null;
    }

    @Override
    public Aircraft removeAircraft() {
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
