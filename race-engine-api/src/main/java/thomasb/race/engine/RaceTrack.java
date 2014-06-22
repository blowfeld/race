package thomasb.race.engine;

import java.util.List;

public interface RaceTrack {
	
	List<TrackSegment> partitions(Segment segment);

}
