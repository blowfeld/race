package thomasb.race.engine;

public final class RacePathSegment extends BaseSegment implements PathSegment {
	private double startTime;
	private double endTime;

	public RacePathSegment(PointDouble start,
			PointDouble end,
			double startTime,
			double endTime) {
		super(start, end);
		this.startTime = startTime;
		this.endTime = endTime;
	}


	@Override
	public double getStartTime() {
		return startTime;
	}

	@Override
	public double getEndTime() {
		return endTime;
	}

	@Override
	public String toString() {
		return "RacePathSegment [startTime=" + startTime +
				", endTime=" + endTime +
				", start=" + getStart() +
				", end=" + getEnd() + "]";
	}
}
