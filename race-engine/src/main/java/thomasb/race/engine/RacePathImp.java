package thomasb.race.engine;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

public final class RacePathImp implements RacePath {
	private final PlayerStatus status;
	private final List<? extends PathSegment> segments;
	
	public RacePathImp(PlayerStatus status, List<? extends PathSegment> segments) {
		this.status = checkNotNull(status);
		this.segments = checkNotNull(segments);
	}
	
	public PlayerStatus getStatus() {
		return status;
	}
	
	public List<? extends PathSegment> getSegments() {
		return segments;
	}
	
	@Override
	public String toString() {
		return "RacePathImp [status=" + status + ", segments=" + segments + "]";
	}
}
