package thomasb.race.engine;

public interface TrackPathSegment extends PathSegment {
	
	int getMaxSpeed();
	
	boolean isFinish();
	
	boolean isTerminating();
	
}
