package thomasb.race.engine;

import java.util.List;

public interface RacePath {
	
	PlayerState getEndState();
	
	List<? extends PathSegment> getSegments();
	
}
