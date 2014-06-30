package thomasb.race.engine;

/**
 * The {@code RaceEngine} calculates the path of the player.
 */
public interface RaceEngine {
	
	/**
	 * Calculates the {@link RacePath} of a player for the specified period.
	 * <p>
	 * The calculated {@link RacePath} contains the path segments from the start
	 * point the the point the player reaches after the specified duration
	 * according to the specified start point, {@link ControlState} and the race
	 * track conditions. In addition, the final state of the player is calculated.
	 * 
	 * @param state the state of the player at the start time
	 * @param startTime the start time of the path
	 * @param duration the duration for which the path is calculated
	 * 
	 * @return the resulting {@link RacePath}
	 */
	RacePath calculatePath(PlayerState state,
			double startTime,
			double duration);
	
}
