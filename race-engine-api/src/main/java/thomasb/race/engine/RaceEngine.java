package thomasb.race.engine;


public interface RaceEngine {
	
	RacePath calculatePath(PointDouble start,
			double startTime,
			double duration,
			ControlState control);
	
}
