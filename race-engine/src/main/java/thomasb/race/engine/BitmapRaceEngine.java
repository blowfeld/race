package thomasb.race.engine;

import static thomasb.race.engine.PlayerStatus.ACTIVE;

import com.google.common.collect.ImmutableList;

public class BitmapRaceEngine implements RaceEngine {

	@Override
	public RacePath calculatePath(PointDouble start, double startTime, double duration, ControlState control) {
		if (control.getSpeed() == 0) {
			return zeroLengthPath(start, startTime, duration);
		}
		
		VectorPoint delta = VectorPoint.fromDirection(control.getSteering())
			.multiply(control.getSpeed());
		VectorPoint endPoint = delta.add(start);
		PathSegment segment = new RacePathSegment(start, endPoint, startTime, startTime + duration);
		
		return new RacePathImp(ACTIVE, ImmutableList.of(segment));
	}

	private RacePath zeroLengthPath(PointDouble start, double startTime, double duration) {
		PathSegment segment = new RacePathSegment(start, start, startTime, startTime + duration);
		
		return new RacePathImp(ACTIVE, ImmutableList.of(segment));
	}
}
