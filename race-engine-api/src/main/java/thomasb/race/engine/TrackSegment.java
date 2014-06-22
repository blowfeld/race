package thomasb.race.engine;

public interface TrackSegment extends Segment {
	
	int getMaxSpeed();
	
	boolean isFinish();
	
	boolean isTerminating();
	
}
