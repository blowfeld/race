package thomasb.race.engine;

public interface TrackPathSegment extends Segment {
	
	int getMaxSpeed();
	
	boolean isFinish();
	
	boolean isTerminating();
	
}
