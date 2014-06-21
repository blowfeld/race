package thomasb.race.engine;

import static java.util.Objects.hash;
import thomasb.race.engine.PathSegment;
import thomasb.race.engine.PointDouble;


public final class PathSegmentImp implements PathSegment {
	private final PointDouble start;
	private final PointDouble end;
	
	public PathSegmentImp(PointDouble start, PointDouble end) {
		if (start == null || end == null) {
			throw new IllegalArgumentException("Argumements cannot be null");
		}
		
		this.start = start;
		this.end = end;
	}
	
	@Override
	public PointDouble getStart() {
		return start;
	}
	
	@Override
	public PointDouble getEnd() {
		return end;
	}
	
	@Override
	public int hashCode() {
		return hash(start, end);
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
		
		return start.equals(other.getStart()) && end.equals(other.getEnd());
	}
	
	@Override
	public String toString() {
		return "PathSegment [start=" + start + ", end=" + end + "]";
	}
}
