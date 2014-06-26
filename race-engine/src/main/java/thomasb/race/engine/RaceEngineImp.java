package thomasb.race.engine;

import static java.lang.Math.min;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Iterables;

public class RaceEngineImp implements RaceEngine {
	private final RaceTrack raceTrack;
	private PathCalculator pathCalc;

	public RaceEngineImp(RaceTrack raceTrack) {
		this.raceTrack = raceTrack;
	}
	
	@Override
	public RacePath calculatePath(PlayerState state, double startTime, double duration) {
		if (state.getControlState().getSpeed() == 0) {
			return zeroLengthPath(state, startTime, duration);
		}
		
		List<TrackSegment> trackSegments = raceTrack.segmentsFor(state.getPosition(), state.getControlState().getSteering());
		
		pathCalc = new PathCalculator(trackSegments, state.getControlState(), startTime, startTime + duration);
		pathCalc.calculate();
		
		List<PathSegment> pathSegments = pathCalc.pathSegments;
		RacePlayerState playerState = new RacePlayerState(pathCalc.endPosition, state.getControlState(), pathCalc.laps, pathCalc.getStatus());
		
		return new RacePathImp(playerState, pathSegments);
	}
	
	private RacePath zeroLengthPath(PlayerState state, double startTime, double duration) {
		PathSegment segment = new RacePathSegment(state.getPosition(), state.getPosition(), startTime, startTime + duration);
		
		return new RacePathImp(state, ImmutableList.of(segment));
	}
	
	private static class PathCalculator {
		private final List<TrackSegment> trackSegments;
		private final ControlState control;
		private final double startTime;
		private final double endTime;

		private int laps = 0;
		private boolean terminating = false;
		private List<PathSegment> pathSegments;
		private PointDouble endPosition;
		
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
			
			if (!pathSegments.isEmpty()) {
				endPosition = Iterables.getLast(pathSegments).getEnd();
			}
		}
		
		private RacePathSegment calculatePathSegment(RaceTrackSegment segment, double segmentStartTime) {
			if (segment.getMaxSpeed() == 0) {
				terminating = true;
				
				return new RacePathSegment(segment.getStart(),
						segment.getStart(),
						segmentStartTime,
						endTime);
			}
			
			laps += segment.crossedFinish();
			
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

		PlayerStatus getStatus() {
			return terminating ? PlayerStatus.TERMINATED : PlayerStatus.ACTIVE;
		}
	}
}
