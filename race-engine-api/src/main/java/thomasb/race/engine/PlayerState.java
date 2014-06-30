package thomasb.race.engine;

/**
 * A {@code PlayerState} represents the current state of a player.
 */
public interface PlayerState {
	
	/**
	 * Returns the current position of the player.
	 * 
	 * @return the current position of the player
	 */
	PointDouble getPosition();
	
	/**
	 * Returns the current {@link ControlState} of the player.
	 * 
	 * @return the current {@link ControlState} of the player
	 */
	ControlState getControlState();
	
	/**
	 * Returns the current {@link Lap} count of the player.
	 * 
	 * @return the current {@link Lap} count of the player
	 */
	Lap getLaps();
	
	/**
	 * Returns the current {@link PlayerStatus}.
	 * 
	 * @return the current {@link PlayerStatus}
	 */
	PlayerStatus getPlayerStatus();
	
	/**
	 * Returns a {@code PlayerState} instance where the {@link ControlState} is
	 * updated with speed and steering changes according to the values in the
	 * specified {@link ControlEvent}.
	 * 
	 * @param event a {@code ControlEvent} containing changes to the state
	 * 
	 * @return a {@code PlayerState} instance with updated speed and steering
	 */
	PlayerState adjust(ControlEvent event);
	
}
