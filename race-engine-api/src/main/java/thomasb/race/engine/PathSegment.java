package thomasb.race.engine;

/**
 * A {@code PathSegment} represents a timed segment of the path the player coverd.
 */
public interface PathSegment {
	
	/**
	 * Returns the start point of the path segment.
	 * 
	 * @return the start point of the path segment
	 */
	PointDouble getStart();
	
	/**
	 * Returns the end point of the path segment.
	 * 
	 * @return the end point of the path segment
	 */
	PointDouble getEnd();
	
	/**
	 * Returns the start time of the path segment.
	 * 
	 * @return the start time of the path segment
	 */
	double getStartTime();
	
	/**
	 * Returns the end time of the path segment.
	 * 
	 * @return the end time of the path segment
	 */
	double getEndTime();
	
}
