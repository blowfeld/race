package thomasb.race.engine;

import static thomasb.race.engine.PlayerStatus.ACTIVE;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class RaceEngineImp implements RaceEngine {
	private final RaceTrack raceTrack;

	public RaceEngineImp(RaceTrack raceTrack) {
		this.raceTrack = raceTrack;
	}

	@Override
	public RacePath calculatePath(PointDouble start, double startTime, double duration, ControlState control) {
		if (control.getSpeed() == 0) {
			return zeroLengthPath(start, startTime, duration);
		}
		
		List<TrackSegment> trackSegments = raceTrack.partitions(start, control.getSteering());
		
		double remaining = duration;
		Builder<PathSegment> result = ImmutableList.builder();
		
		Iterator<TrackSegment> segmentItr = trackSegments.iterator();
		while (segmentItr.hasNext() && remaining > 0) {
			RaceTrackSegment segment = RaceTrackSegment.from(segmentItr.next());
			
			int speed = Math.min(control.getSpeed(), segment.getMaxSpeed());
			double maxDistance = speed * remaining;
			
			if (maxDistance < segment.length()) {
				result.add(new RacePathSegment(segment.getStart(),
						VectorPoint.from(segment.getStart()).add(VectorPoint.fromDirection(control.getSteering()).multiply(maxDistance)),
						startTime + duration - remaining,
						startTime + duration));
				break;
			} else {
				result.add(new RacePathSegment(segment.getStart(),
						segment.getEnd(),
						startTime + duration - remaining,
						startTime + duration - remaining + (segment.length() / speed)));
				remaining -= (segment.length() / speed);
			}
		};
		
		return new RacePathImp(ACTIVE, result.build());
	}

	private RacePath zeroLengthPath(PointDouble start, double startTime, double duration) {
		PathSegment segment = new RacePathSegment(start, start, startTime, startTime + duration);
		
		return new RacePathImp(ACTIVE, ImmutableList.of(segment));
	}
}
