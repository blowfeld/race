package thomasb.race.engine;

import java.util.List;

public interface RacePath {
	
	PlayerStatus getStatus();
	
	List<? extends TimedPathSegment> getSegments();
	
}
