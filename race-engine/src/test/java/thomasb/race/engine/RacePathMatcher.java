package thomasb.race.engine;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

class RacePathMatcher extends BaseMatcher<RacePath> {
	private final RacePath expected;

	static RacePathMatcher isCloseTo(RacePath expected) {
		return new RacePathMatcher(expected);
	}
	
	RacePathMatcher(RacePath expected) {
		this.expected = expected;
	}
	
	@Override
	public boolean matches(Object item) {
		if (!(item instanceof RacePath)) {
			return false;
		}
		
		RacePath other = (RacePath) item;
		
		if (expected.getStatus() != other.getStatus() ||
				expected.getSegments().size() != other.getSegments().size()) {
			return false;
		}
		
		for (int i = 0; i < other.getSegments().size(); i++) {
			if (!startAndEndPointsClose(other, i, 0.00001)) {
				return false;
			}
		}
		
		return true;
	}

	private boolean startAndEndPointsClose(RacePath other, int i, double precision) {
		PathSegment expectedSegment = expected.getSegments().get(i);
		PathSegment otherSegment = other.getSegments().get(i);
		boolean startClose = VectorPoint.fromPoint(expectedSegment.getStart())
				.isClose(otherSegment.getStart(), precision);
		boolean endClose = VectorPoint.fromPoint(expectedSegment.getEnd())
				.isClose(otherSegment.getEnd(), precision);
		boolean startTimeClose = expectedSegment.getStartTime() -
				otherSegment.getStartTime() < precision;
		boolean endTimeClose = expectedSegment.getEndTime() -
				otherSegment.getEndTime() < precision;
		
		return startClose && endClose && startTimeClose && endTimeClose;
	}

	@Override
	public void describeTo(Description description) {
		description.appendValue(expected);
	}
}