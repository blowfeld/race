package thomasb.race.engine.entities;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Objects.hash;

import java.util.List;

import thomasb.race.engine.PathSegment;
import thomasb.race.engine.PlayerStatus;
import thomasb.race.engine.RacePath;

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
	public int hashCode() {
		return hash(status, segments);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj instanceof RacePath) {
			return false;
		}
		
		RacePath other = (RacePath) obj;

		return status.equals(other.getStatus()) &&
				segments.equals(other.getSegments());
	}
	
	@Override
	public String toString() {
		return "RacePathImp [status=" + status + ", segments=" + segments + "]";
	}
}
