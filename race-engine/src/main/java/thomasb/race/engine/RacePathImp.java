package thomasb.race.engine;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

final class RacePathImp implements RacePath {
	private final PlayerStatus status;
	private final int finishedLaps;
	private final List<? extends PathSegment> segments;
	
	RacePathImp(PlayerStatus status,
			int finishedLaps,
			List<? extends PathSegment> segments) {
		this.finishedLaps = finishedLaps;
		this.status = checkNotNull(status);
		this.segments = checkNotNull(segments);
	}
	
	@Override
	public PlayerStatus getStatus() {
		return status;
	}
	
	@Override
	public int finishedLaps() {
		return finishedLaps;
	}
	
	@Override
	public List<? extends PathSegment> getSegments() {
		return segments;
	}
	
	@Override
	public String toString() {
		return "RacePathImp [status=" + status +
				", finishedLaps=" + finishedLaps +
				", segments=" + segments + "]";
	}
}
