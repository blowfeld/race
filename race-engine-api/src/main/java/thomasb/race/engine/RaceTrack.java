package thomasb.race.engine;

import java.util.List;

public interface RaceTrack {
	
	List<TrackSegment> partitions(PointDouble startPoint, int direction);

}
