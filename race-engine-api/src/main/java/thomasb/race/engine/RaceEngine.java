package thomasb.race.engine;


public interface RaceEngine {
	
	RacePath calculatePath(PlayerState state,
			double startTime,
			double duration);
	
}
