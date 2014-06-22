package thomasb.race.engine;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

class PathSegmentMatcher extends BaseMatcher<PathSegment> {
	private final PathSegment expected;
	private final double precision;

	PathSegmentMatcher(PathSegment expected, double precision) {
		this.expected = expected;
		this.precision = precision;
	}
	
	@Override
	public boolean matches(Object item) {
		if (!(item instanceof PathSegment)) {
			return false;
		}
		
		PathSegment other = (PathSegment) item;
		
		boolean startClose = VectorPoint.from(expected.getStart())
				.isClose(other.getStart(), precision);
		boolean endClose = VectorPoint.from(expected.getEnd())
				.isClose(other.getEnd(), precision);
		boolean startTimeClose = expected.getStartTime() -
				other.getStartTime() <= precision;
		boolean endTimeClose = expected.getEndTime() -
				other.getEndTime() <= precision;
		
		return startClose && endClose && startTimeClose && endTimeClose;
	}

	@Override
	public void describeTo(Description description) {
		description.appendValue(expected);
	}
}