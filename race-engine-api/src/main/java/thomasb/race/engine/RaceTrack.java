package thomasb.race.engine;

import java.util.List;

public interface RaceTrack {
	
	List<TrackPathSegment> partitions(Segment segment);

}
