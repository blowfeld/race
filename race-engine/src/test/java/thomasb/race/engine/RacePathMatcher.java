package thomasb.race.engine;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

class RacePathMatcher extends BaseMatcher<RacePath> {
	private final RacePath expected;
	private final double precision;

	static RacePathMatcher isCloseTo(RacePath expected, double precision) {
		return new RacePathMatcher(expected, precision);
	}
	
	RacePathMatcher(RacePath expected, double precision) {
		this.expected = expected;
		this.precision = precision;
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
			if (!isClose(expected.getSegments().get(i), other.getSegments().get(i), precision)) {
				return false;
			}
		}
		
		return true;
	}

	private boolean isClose(PathSegment expected, PathSegment actual, double precision) {
		return new PathSegmentMatcher(expected, precision).matches(actual);
	}

	@Override
	public void describeTo(Description description) {
		description.appendValue(expected);
	}
}