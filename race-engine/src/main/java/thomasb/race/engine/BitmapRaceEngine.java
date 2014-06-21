package thomasb.race.engine;

import static thomasb.race.engine.PlayerStatus.ACTIVE;

import com.google.common.collect.ImmutableList;

public class BitmapRaceEngine implements RaceEngine {

	@Override
	public RacePath calculatePath(PointDouble start, ControlState control, int time) {
		if (control.getSpeed() == 0) {
			return zeroLengthPath(start);
		}
		
		VectorPoint delta = VectorPoint.fromDirection(control.getSteering())
			.multiply(control.getSpeed());
		VectorPoint endPoint = delta.add(start);
		PathSegment segment = new PathSegmentImp(start, endPoint);
		
		return new RacePathImp(ACTIVE, ImmutableList.of(segment));
	}

	private RacePath zeroLengthPath(PointDouble start) {
		PathSegmentImp inPlace = new PathSegmentImp(start, start);
		
		return new RacePathImp(ACTIVE, ImmutableList.of(inPlace));
	}
}
