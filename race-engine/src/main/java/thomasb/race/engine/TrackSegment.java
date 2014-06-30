package thomasb.race.engine;

interface TrackSegment {
	
	PointDouble getStart();
	
	PointDouble getEnd();
	
	int getMaxSpeed();
	
	int crossedFinish();
	
}
