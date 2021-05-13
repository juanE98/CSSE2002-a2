package towersim.control;

import towersim.aircraft.Aircraft;
import towersim.aircraft.PassengerAircraft;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Represents a rule-based queue of aircraft waiting in the air to land.
 * The rules in the landing queue are designed to ensure that aircraft are prioritised for
 * landing based on "urgency" factors such as remaining fuel onboard, emergency status and cargo
 * type.
 */
public class LandingQueue extends AircraftQueue {

    /** List of aircrafts at the landingQueue */
    private Queue<Aircraft> landingQueue;

    /**
     * Constructs a new LandingQueue with an initially empty queue of aircraft.
     */
    public LandingQueue() {
        this.landingQueue = new LinkedList<>();
    }

    /**
     * Adds the given aircraft to the queue.
     * @param aircraft aircraft to add to queue
     */
    @Override
    public void addAircraft(Aircraft aircraft) {
        this.landingQueue.add(aircraft);
    }

    /**
     * Returns the aircraft at the front of the queue without removing it from the queue, or null
     * if the queue is empty.
     * The rules for determining which aircraft in the queue should be returned next are as follows:
     *
     * If an aircraft is currently in a state of emergency, it should be returned. If more than
     * one  aircraft are in an emergency, return the one added to the queue first.
     * If an aircraft has less than or equal to 20 percent fuel remaining, a critical level, it
     * should be returned (see Aircraft.getFuelPercentRemaining()). If more than one aircraft
     * have  a critical level of fuel onboard, return the one added to the queue first.
     * If there are any passenger aircraft in the queue, return the passenger aircraft that was
     * added to the queue first.
     * If this point is reached and no aircraft has been returned, return the aircraft that was
     * added to the queue first.
     * @return aircraft at front of queue
     */
    @Override
    public Aircraft peekAircraft() {
        for (Aircraft aircraft : this.landingQueue) {
            if (aircraft.hasEmergency()) {
                return aircraft;
            }
        }
        for (Aircraft aircraft : this.landingQueue) {
            if (aircraft.getFuelPercentRemaining() <= 20) {
                return aircraft;
            }
        }
        for (Aircraft aircraft : this.landingQueue) {
            if (aircraft instanceof PassengerAircraft) {
                return aircraft;
            }
        }
        return this.landingQueue.peek();
    }

    /**
     * Removes and returns the aircraft at the front of the queue. Returns null if the queue is
     * empty.
     * The same rules as described in peekAircraft() should be used for determining which
     * aircraft to remove and return.
     * @return aircraft at front of queue
     */
    @Override
    public Aircraft removeAircraft() {
        Aircraft removeAircraft = this.peekAircraft();
        if (removeAircraft != null) {
            this.landingQueue.remove(removeAircraft);
        }
        return removeAircraft;
    }

    /**
     * Returns a list containing all aircraft in the queue, in order.
     * That is, the first element of the returned list should be the first aircraft that would be
     * returned by calling removeAircraft(), and so on.
     *
     * Adding or removing elements from the returned list should not affect the original queue.
     * @return list of all aircraft in queue, in queue order
     */
    @Override
    public List<Aircraft> getAircraftInOrder() {
        //List to be returned
        List<Aircraft> aircraftInOrder = new LinkedList<>();

        //Copy of LandingQueue
        LandingQueue landingQueueCopy = new LandingQueue();
        for (Aircraft aircraft : this.landingQueue) {
            landingQueueCopy.addAircraft(aircraft);
        }

        //Order the aircrafts in copy
        for (int i = 0; i < this.landingQueue.size(); i++) {
            aircraftInOrder.add(landingQueueCopy.removeAircraft());
        }
        return aircraftInOrder;
    }

    /**
     * Returns true if the given aircraft is in the queue.
     * @param aircraft aircraft to find in queue
     * @return true if aircraft is in queue; false otherwise
     */
    @Override
    public boolean containsAircraft(Aircraft aircraft) {
        return this.landingQueue.contains(aircraft);
    }
}
