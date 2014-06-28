package thomasb.race.engine;

import java.util.List;

public interface TrackSection {
	
	List<? extends PointDouble> getCorners();
	
	SectionType getType();
}
