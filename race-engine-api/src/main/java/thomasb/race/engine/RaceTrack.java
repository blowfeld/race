package thomasb.race.engine;

import java.util.List;

public interface RaceTrack {
	
	List<TrackSegment> segmentsFor(PointDouble startPoint, int direction);

}
