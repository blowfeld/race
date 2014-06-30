package thomasb.race.engine;

/**
 * A {@code ControlState} represent the state of the player.
 */
public interface ControlState {
	
	/**
	 * Returns the current direction in which the player is moving.
	 * 
	 * @return the current direction. The returned value is between 0 and 359
	 * 			(inclusive)
	 */
	int getSteering();
	
	
	/**
	 * Returns the current speed of the player.
	 * 
	 * @return the current speed. The returned value is between 0 and 2
	 * 			(inclusive)
	 */
	int getSpeed();
	
	/**
	 * Returns a {@code ControlState} instance with speed and steering updated
	 * according to the values in the specified {@link ControlEvent}.
	 * @param event a {@code ControlEvent} containing changes to the state
	 * 
	 * @return a {@code ControlState} instance with udpated speed and steering
	 */
	ControlState adjust(ControlEvent event);
	
}