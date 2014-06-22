package thomasb.race.engine;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Objects.hash;


public class BaseSegment implements Segment {
	private final PointDouble start;
	private final PointDouble end;
	
	public BaseSegment(PointDouble start, PointDouble end) {
		this.start = checkNotNull(start);
		this.end = checkNotNull(end);
	}
	
	@Override
	public final PointDouble getStart() {
		return start;
	}
	
	@Override
	public final PointDouble getEnd() {
		return end;
	}
	
	@Override
	public final int hashCode() {
		return hash(start, end);
	}
	
	@Override
	public final boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (!(obj instanceof Segment)) {
			return false;
		}
		
		Segment other = (Segment) obj;
		
		return start.equals(other.getStart()) && end.equals(other.getEnd());
	}
	
	@Override
	public String toString() {
		return "PathSegment [start=" + start + ", end=" + end + "]";
	}
}
