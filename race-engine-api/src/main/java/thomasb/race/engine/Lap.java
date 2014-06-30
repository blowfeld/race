package thomasb.race.engine;

/**
 * A {@code Lap} instance represents the current state of the player.
 */
public interface Lap extends Comparable<Lap> {
	
	/**
	 * The number of times the participant crossed the finish line in the
	 * prescribed direction. This number can be negative if the finish line is
	 * crossed in the wrong direction.
	 * 
	 * @return the lap count of the player
	 */
	int getCount();
	
	/**
	 * Returns the time at which the player crossed the finish line the last time.
	 * 
	 * @return the time at which the player crossed the finish line the last time
	 */
	double getLapTime();
	
}
