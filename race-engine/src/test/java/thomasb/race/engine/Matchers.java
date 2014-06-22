package thomasb.race.engine;

public class Matchers {
	
	static PathSegmentMatcher isCloseTo(PathSegment expected, double precision) {
		return new PathSegmentMatcher(expected, precision);
	}
	
	static RacePathMatcher isCloseTo(RacePath expected, double precision) {
		return new RacePathMatcher(expected, precision);
	}
	
}
