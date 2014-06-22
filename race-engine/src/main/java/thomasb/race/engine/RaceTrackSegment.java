package thomasb.race.engine;

public final class RaceTrackSegment extends BaseSegment implements TrackSegment {
	private final int maxSpeed;
	private final boolean terminating;
	private final boolean finish;

	public RaceTrackSegment(PointDouble start,
			PointDouble end,
			int maxSpeed,
			boolean isTerminating,
			boolean isFinish) {
		super(start, end);
		this.maxSpeed = maxSpeed;
		this.terminating = isTerminating;
		this.finish = isFinish;
	}

	@Override
	public int getMaxSpeed() {
		return maxSpeed;
	}


	@Override
	public boolean isFinish() {
		return finish;
	}

	
	@Override
	public boolean isTerminating() {
		return terminating;
	}

	@Override
	public String toString() {
		return "RaceTrackSegment [maxSpeed=" + maxSpeed +
				", terminating=" + terminating +
				", finish=" + finish +
				", start=" + getStart() +
				", end=" + getEnd() + "]";
	}
}
