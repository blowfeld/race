package thomasb.race.engine;

import static java.lang.Math.min;
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
		
		List<RacePathSegment> pathSegments = calculatePathSegments(trackSegments,
				control,
				startTime,
				startTime + duration);
		
		PlayerStatus status = pathSegments.get(pathSegments.size() - 1).getStatus();
		
		return new RacePathImp(status, pathSegments);
	}

	private List<RacePathSegment> calculatePathSegments(List<TrackSegment> trackSegments,
			ControlState control,
			double startTime,
			double endTime) {
		double segmentStartTime = startTime;
		
		Builder<RacePathSegment> pathSegments = ImmutableList.builder();
		Iterator<TrackSegment> segmentItr = trackSegments.iterator();
		while (segmentItr.hasNext() && segmentStartTime < endTime) {
			RaceTrackSegment segment = RaceTrackSegment.from(segmentItr.next());
			
			RacePathSegment pathSegment = calculatePathSegment(segment, control, segmentStartTime, endTime);
			pathSegments.add(pathSegment);
			
			segmentStartTime = pathSegment.getEndTime();
		}
		
		return pathSegments.build();
	}
	
	private RacePathSegment calculatePathSegment(RaceTrackSegment segment,
			ControlState control,
			double segmentStartTime,
			double endTime) {
		if (segment.getMaxSpeed() == 0) {
			return new RacePathSegment(segment.getStart(),
					segment.getEnd(),
					segmentStartTime,
					endTime,
					PlayerStatus.TERMINATED);
		}
		
		int speed = min(control.getSpeed(), segment.getMaxSpeed());
		double maxDistance = speed * (endTime - segmentStartTime);
		
		double segmentLength = segment.length();
		if (maxDistance >= segmentLength) {
			double segmentEndTime = segmentStartTime + (segmentLength / speed);
			
			return new RacePathSegment(segment.getStart(),
					segment.getEnd(),
					segmentStartTime,
					segmentEndTime);
		} else {
			VectorPoint direction = VectorPoint.fromDirection(control.getSteering());
			VectorPoint delta = direction.multiply(maxDistance);
			
			return new RacePathSegment(segment.getStart(),
					VectorPoint.from(segment.getStart()).add(delta),
					segmentStartTime,
					endTime);
		}
	}
	
	private RacePath zeroLengthPath(PointDouble start, double startTime, double duration) {
		PathSegment segment = new RacePathSegment(start, start, startTime, startTime + duration);
		
		return new RacePathImp(ACTIVE, ImmutableList.of(segment));
	}
}
