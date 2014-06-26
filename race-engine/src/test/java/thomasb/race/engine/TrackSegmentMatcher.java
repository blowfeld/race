package thomasb.race.engine;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

class TrackSegmentMatcher extends BaseMatcher<TrackSegment> {
	private final TrackSegment expected;
	private final double precision;

	TrackSegmentMatcher(TrackSegment expected, double precision) {
		this.expected = expected;
		this.precision = precision;
	}
	
	@Override
	public boolean matches(Object item) {
		if (!(item instanceof TrackSegment)) {
			return false;
		}
		
		TrackSegment other = (TrackSegment) item;
		
		boolean startClose = new PointMatcher(expected.getStart(), precision)
				.matches(other.getStart());
		boolean endClose = new PointMatcher(expected.getEnd(), precision)
				.matches(other.getEnd());
		boolean maxSpeedMatches = expected.getMaxSpeed() == other.getMaxSpeed();
		boolean isFinishMatches = expected.crossedFinish() == other.crossedFinish();
		
		return startClose && endClose && maxSpeedMatches && isFinishMatches;
	}

	@Override
	public void describeTo(Description description) {
		description.appendValue(expected);
	}
}