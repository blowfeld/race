package thomasb.race.engine;


public interface RaceEngine {
	
	RacePath calculatePath(PointDouble start, ControlState control, int time);
	
}
