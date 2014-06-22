package thomasb.race.engine;

import static com.google.common.base.Preconditions.checkNotNull;


abstract class AbstractSegment {
	private final PointDouble start;
	private final PointDouble end;
	
	public AbstractSegment(PointDouble start, PointDouble end) {
		this.start = checkNotNull(start);
		this.end = checkNotNull(end);
	}
	
	public final PointDouble getStart() {
		return start;
	}
	
	public final PointDouble getEnd() {
		return end;
	}
}
