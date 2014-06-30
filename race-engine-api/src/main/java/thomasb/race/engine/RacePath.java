package thomasb.race.engine;

import java.util.List;

/**
 * The {@code RacePath} represents a sequence of {@link PathSegment}s the player
 * covers between two points and the end state at the end point of the path.
 * @author tkb
 *
 */
public interface RacePath {
	
	/**
	 * Returns the {@link PlayerState} of the player at the end point of the path.
	 * 
	 * @return the final {@link PlayerState} of the player
	 */
	PlayerState getEndState();
	
	/**
	 * Returns a connected list of {@link PathSegment} the player covered
	 * between two points.
	 * 
	 * @return a connected list of {@link PathSegment}s
	 */
	List<? extends PathSegment> getSegments();
	
}
