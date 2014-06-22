package thomasb.race.engine;

import static java.lang.Math.min;
import static thomasb.race.engine.PlayerStatus.ACTIVE;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class RaceEngineImp implements RaceEngine {
	private final RaceTrack raceTrack;
	private PathCalculator pathCalc;

	public RaceEngineImp(RaceTrack raceTrack) {
		this.raceTrack = raceTrack;
	}
	
	@Override
	public RacePath calculatePath(PointDouble start, double startTime, double duration, ControlState control) {
		if (control.getSpeed() == 0) {
			return zeroLengthPath(start, startTime, duration);
		}
		
		List<TrackSegment> trackSegments = raceTrack.segmentsFor(start, control.getSteering());
		
		pathCalc = new PathCalculator(trackSegments, control, startTime, startTime + duration);
		pathCalc.calculate();
		
		return new RacePathImp(pathCalc.getStatus(), pathCalc.getLaps(), pathCalc.getPathSegments());
	}
	
	private RacePath zeroLengthPath(PointDouble start, double startTime, double duration) {
		PathSegment segment = new RacePathSegment(start, start, startTime, startTime + duration);
		
		return new RacePathImp(ACTIVE, 0, ImmutableList.of(segment));
	}
	
	private static class PathCalculator {
		private final List<TrackSegment> trackSegments;
		private final ControlState control;
		private final double startTime;
		private final double endTime;

		private int laps = 0;
		private boolean terminating = false;
		private List<PathSegment> pathSegments;
		
		PathCalculator(List<TrackSegment> trackSegments,
				ControlState control,
				double startTime,
				double endTime) {
			this.trackSegments = trackSegments;
			this.control = control;
			this.startTime = startTime;
			this.endTime = endTime;
		}

		void calculate() {
			double segmentStartTime = startTime;
			
			Builder<PathSegment> segments = ImmutableList.builder();
			Iterator<TrackSegment> segmentItr = trackSegments.iterator();
			while (segmentItr.hasNext() && segmentStartTime < endTime) {
				RaceTrackSegment segment = RaceTrackSegment.from(segmentItr.next());
				
				RacePathSegment pathSegment = calculatePathSegment(segment, segmentStartTime);
				segments.add(pathSegment);
				
				segmentStartTime = pathSegment.getEndTime();
			}
			
			pathSegments = segments.build();
		}
		
		private RacePathSegment calculatePathSegment(RaceTrackSegment segment, double segmentStartTime) {
			if (segment.getMaxSpeed() == 0) {
				terminating = true;
				
				return new RacePathSegment(segment.getStart(),
						segment.getStart(),
						segmentStartTime,
						endTime);
			}
			
			if (segment.isFinish()) {
				laps += 1;
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

		int getLaps() {
			return laps;
		}
		
		PlayerStatus getStatus() {
			return terminating ? PlayerStatus.TERMINATED : PlayerStatus.ACTIVE;
		}

		List<PathSegment> getPathSegments() {
			return pathSegments;
		}
	}
}
