package thomasb.race.engine;

import static java.util.Objects.hash;

public final class RacePathSegment extends AbstractSegment implements PathSegment {
	private final double startTime;
	private final double endTime;
	private final PlayerStatus status;
	
	RacePathSegment(PointDouble start,
			PointDouble end,
			double startTime,
			double endTime) {
		this(start, end, startTime, endTime, PlayerStatus.ACTIVE);
	}
	
	RacePathSegment(PointDouble start,
			PointDouble end,
			double startTime,
			double endTime,
			PlayerStatus status) {
		super(start, end);
		this.startTime = startTime;
		this.endTime = endTime;
		this.status = status;
	}
	
	@Override
	public double getStartTime() {
		return startTime;
	}
	
	@Override
	public double getEndTime() {
		return endTime;
	}
	
	PlayerStatus getStatus() {
		return status;
	}
	
	@Override
	public int hashCode() {
		return hash(getStart(), getEnd(), startTime, endTime);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (!(obj instanceof PathSegment)) {
			return false;
		}
		
		PathSegment other = (PathSegment) obj;
		
		return getStart().equals(other.getStart()) &&
				getEnd().equals(other.getEnd()) &&
				Double.compare(startTime, other.getStartTime()) == 0 &&
				Double.compare(endTime, other.getEndTime()) == 0;
	}

	@Override
	public String toString() {
		return "RacePathSegment [startTime=" + startTime +
				", endTime=" + endTime +
				", start=" + getStart() +
				", end=" + getEnd() + "]";
	}
}
