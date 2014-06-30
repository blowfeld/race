package thomasb.race.engine;

import static java.lang.Math.sqrt;
import static java.util.Objects.hash;

final class TrackSegment extends AbstractSegment {
	private final int maxSpeed;
	private final int finish;

	TrackSegment(PointDouble start,
			PointDouble end,
			int maxSpeed,
			int crossedFinish) {
		super(start, end);
		this.maxSpeed = maxSpeed;
		this.finish = crossedFinish;
	}

	int getMaxSpeed() {
		return maxSpeed;
	}

	int crossedFinish() {
		return finish;
	}
	
	double length() {
		VectorPoint diff = VectorPoint.from(getEnd()).diff(getStart());
		
		return sqrt(diff.dot(diff));
	}
	
	@Override
	public final int hashCode() {
		return hash(getStart(), getEnd(), maxSpeed, finish);
	}
	
	@Override
	public final boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (!(obj instanceof TrackSegment)) {
			return false;
		}
		
		TrackSegment other = (TrackSegment) obj;
		
		return getStart().equals(other.getStart()) &&
				getEnd().equals(other.getEnd()) &&
				maxSpeed == other.getMaxSpeed() &&
				finish == other.crossedFinish();
	}


	@Override
	public String toString() {
		return "RaceTrackSegment [maxSpeed=" + maxSpeed +
				", finish=" + finish +
				", start=" + getStart() +
				", end=" + getEnd() + "]";
	}
}
