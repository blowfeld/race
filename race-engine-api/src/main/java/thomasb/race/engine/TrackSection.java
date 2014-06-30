package thomasb.race.engine;

import java.util.List;

/**
 * A {@link TrackSection} defines the outer polygon boundary of a section of
 * the race track.
 */
public interface TrackSection {
	
	/**
	 * Returns the corner points of the outer boundary of the section.
	 * <p>
	 * The boundary of the section must be a closed line with no intersections.
	 * The returned list must contain the corner points in subsequent order.
	 * 
	 * @return the corner points of the outer boundary of the section
	 */
	List<? extends PointDouble> getCorners();
	
	/**
	 * Returns the type of the section represented by the
	 * {@code TrackSection} instance.
	 * 
	 * @return the type of the section
	 */
	SectionType getType();
	
}
