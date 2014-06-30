package thomasb.race.engine;

import java.util.List;

/**
 * A {@code RaceTrack} provides the definition of the race track.
 */
public interface RaceTrack {
	
	/**
	 * A list of {@link TrackSection}s describing the boundaries of the
	 * different sections of the track.
	 * <p>
	 * The list must be ordered from the innermost to the outermost boundary,
	 * and each section must be completely contained in its successor in the
	 * list.
	 * 
	 * @return a list of {@link TrackSection}s describing the race track
	 */
	List<? extends TrackSection> getSections();
	
	/**
	 * A list of points defining the finish line of the track.
	 * <p>
	 * The list must describe a path that is not closed. The order must
	 * describe the finsh line from left to right, viewed in the direction
	 * the line is to be crossed.
	 * 
	 * @return a list of points defining the finish line
	 */
	List<? extends PointDouble> getFinish();
	
	/**
	 * An iterable of points defining the start points of the players in the
	 * order of their start positions.
	 * 
	 * @return an iterable over the start grid
	 */
	Iterable<? extends PointDouble> getStartGrid();
	
	int getMaxLaps();
	
}
