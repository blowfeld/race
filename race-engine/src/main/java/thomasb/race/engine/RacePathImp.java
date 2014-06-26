package thomasb.race.engine;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

final class RacePathImp implements RacePath {
	private final PlayerState state;
	private final List<? extends PathSegment> segments;
	
	RacePathImp(PlayerState state,
			List<? extends PathSegment> segments) {
		this.state = checkNotNull(state);
		this.segments = checkNotNull(segments);
	}
	
	@Override
	public PlayerState getEndState() {
		return state;
	}
	
	@Override
	public List<? extends PathSegment> getSegments() {
		return segments;
	}
	
	@Override
	public String toString() {
		return "RacePathImp [status=" + state +
				", segments=" + segments + "]";
	}
}
