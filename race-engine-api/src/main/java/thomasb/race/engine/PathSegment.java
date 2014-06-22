package thomasb.race.engine;

public interface PathSegment {
	
	PointDouble getStart();
	
	PointDouble getEnd();
	
	double getStartTime();
	
	double getEndTime();
	
}
