package thomasb.race.engine;

import static thomasb.race.engine.PlayerStatus.ACTIVE;

import com.google.common.collect.ImmutableList;

public class BitmapRaceEngine implements RaceEngine {

	@Override
	public RacePath calculatePath(PointDouble start, ControlState control, int time) {
		PathSegmentImp inPlace = new PathSegmentImp(start, start);
		
		return new RacePathImp(ACTIVE, ImmutableList.of(inPlace));
	}

}
