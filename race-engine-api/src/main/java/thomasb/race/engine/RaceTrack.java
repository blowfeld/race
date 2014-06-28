package thomasb.race.engine;

import java.util.List;

public interface RaceTrack {
	
	List<? extends TrackSection> getSections();
	
	List<? extends PointDouble> getFinish();
	
	Iterable<? extends PointDouble> getStartGrid();
	
	List<? extends PointDouble> getContour();
	
	int getMaxLaps();
	
}
