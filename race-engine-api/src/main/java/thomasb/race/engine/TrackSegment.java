package thomasb.race.engine;

public interface TrackSegment {
	
	PointDouble getStart();
	
	PointDouble getEnd();
	
	int getMaxSpeed();
	
	int crossedFinish();
	
}
