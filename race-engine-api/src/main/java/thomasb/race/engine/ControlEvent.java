package thomasb.race.engine;

/**
 * A {@code ControlEvent} represent the changes to the {@link ControlState}
 * associated with a client event.
 */
public interface ControlEvent {
	
	/**
	 * Returns the speed change associated with the client event.
	 * 
	 * @return the speed change associated with the event
	 */
	int speedChange();
	
	/**
	 * Returns the steering change associated with the client event.
	 * 
	 * @return the steering change associated with the event
	 */
	int steeringChange();
	
}
