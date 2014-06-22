package thomasb.race.engine;

import java.util.List;

public interface RacePath {
	
	PlayerStatus getStatus();
	
	int finishedLaps();
	
	List<? extends PathSegment> getSegments();
	
}
