package towersim.control;

import towersim.aircraft.Aircraft;
import towersim.util.Encodable;

import java.util.List;

/**
 * Abstract representation of a queue containing aircraft.
 * Aircraft can be added to the queue, and aircraft at the front of the queue can be queried or
 * removed. A list of all aircraft contained in the queue (in queue order) can be obtained. The
 * queue can be checked for containing a specified aircraft.
 *
 * The order that aircraft are removed from the queue depends on the chosen concrete
 * implementation  of the AircraftQueue.
 */
public abstract class AircraftQueue implements Encodable {
    /**
     * Adds the given aircraft to the queue.
     * @param aircraft aircraft to add to queue
     */
    public abstract void addAircraft(Aircraft aircraft);

    /**
     * Removes and returns the aircraft at the front of the queue. Returns null if the queue is empty.
     * @return aircraft at front of queue
     */
    public abstract Aircraft removeAircraft();

    /**
     * Returns the aircraft at the front of the queue without removing it from the queue, or
     * null if the queue is empty.
     * @return aircraft at front of queue
     */
    public abstract Aircraft peekAircraft();

    /**
     * Returns a list containing all aircraft in the queue, in order.
     * That is, the first element of the returned list should be the first aircraft that would
     * be returned by calling removeAircraft(), and so on.
     *
     * Adding or removing elements from the returned list should not affect the original queue.
     * @return list of all aircraft in queue, in queue order
     */
    public abstract List<Aircraft> getAircraftInOrder();

    /**
     * Returns true if the given aircraft is in the queue.
     * @param aircraft aircraft to find in queue
     * @return true if aircraft is in queue; false otherwise
     */
    public abstract boolean containsAircraft(Aircraft aircraft);

    /**
     * Returns the human-readable string representation of this aircraft queue.
     * The format of the string to return is
     *
     * QueueType [callsign1, callsign2, ..., callsignN]
     * where QueueType is the concrete queue class (i.e. LandingQueue or TakeoffQueue) and
     * callsign1 through callsignN are the callsigns of all aircraft in the queue, in queue order (see getAircraftInOrder()).
     * For example, "LandingQueue [ABC123, XYZ987, BOB555]" for a landing queue with three
     * aircraft  and "TakeoffQueue []" for a takeoff queue with no aircraft.
     * @return string representation of this queue
     */
    @Override
    public String toString() {
        String aircrafts = this.callsignList(false);
        return String.format("%s [%s]", this.getClass().getSimpleName(), aircrafts);
    }

    /**
     * Returns the machine-readable string representation of this aircraft queue.
     * The format of the string to return is
     *
     * QueueType:numAircraft
     * callsign1,callsign2,...,callsignN
     * where:
     * QueueType is the simple class name of this queue, e.g. LandingQueue
     * numAircraft is the number of aircraft currently waiting in the queue
     * callsignX is the callsign of the Xth aircraft in the queue, in the same order as returned
     * by getAircraftInOrder(), for X between 1 and N inclusive, where N is the number of aircraft in the queue
     * For example:
     * LandingQueue:0
     * For example:
     * TakeoffQueue:3
     * ABC101,QWE456,XYZ789
     * @return encoded string representation of this aircraft queue
     */
    public String encode() {
        StringBuilder encodedString = new StringBuilder();
        encodedString.append(this.getClass().getSimpleName()).append(":").append(this.getAircraftInOrder().size());
        encodedString.append(System.lineSeparator());
        encodedString.append(this.callsignList(true));
        return String.valueOf(encodedString);
    }

    /**
     * Helper method to get the String representation of the aircrafts' callsign.
     * @param encode true if called by encode, false otherwise
     * @return string representation of aircrafts' callsign
     */
    private String callsignList(boolean encode) {
        StringBuilder aircraftsCallsign = new StringBuilder();
        List<Aircraft> aircraftInOrder = this.getAircraftInOrder();
        if (aircraftInOrder != null && !(aircraftInOrder.isEmpty())) {
            if (!encode)
            {
                for (Aircraft aircraft : aircraftInOrder) {
                    aircraftsCallsign.append(aircraft.getCallsign()).append(", ");
                }
                if (aircraftsCallsign.length() > 0) {
                    aircraftsCallsign.delete(aircraftsCallsign.length() - 2,
                            aircraftsCallsign.length());
                }
            }
            else {
                for (Aircraft aircraft : aircraftInOrder) {
                    aircraftsCallsign.append(aircraft.getCallsign()).append(",");
                }
                if (aircraftsCallsign.length() > 0) {
                    aircraftsCallsign.delete(aircraftsCallsign.length() - 1,
                            aircraftsCallsign.length());
                }
            }
        }
        return String.valueOf(aircraftsCallsign);
    }
}
