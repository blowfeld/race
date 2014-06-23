package thomasb.race.engine;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

class PointMatcher extends BaseMatcher<PointDouble> {
	private final PointDouble expected;
	private final double precision;

	PointMatcher(PointDouble expected, double precision) {
		this.expected = expected;
		this.precision = precision;
	}
	
	@Override
	public boolean matches(Object item) {
		if (!(item instanceof PointDouble)) {
			return false;
		}
		
		PointDouble actual = (PointDouble) item;
		
		return VectorPoint.from(expected).isClose(actual, precision);
	
	}
	
	@Override
	public void describeTo(Description description) {
		description.appendValue(expected);
	}
}