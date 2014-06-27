package thomasb.race.engine;

import java.util.Objects;


public final class RacePlayerState implements PlayerState {
	private final PointDouble position;
	private final ControlState controlState;
	private final Lap laps;
	private final PlayerStatus status;

	public RacePlayerState(PointDouble position,
			ControlState controlState,
			Lap laps,
			PlayerStatus status) {
				this.position = position;
				this.controlState = controlState;
				this.laps = laps;
				this.status = status;
	}

	@Override
	public PointDouble getPosition() {
		return position;
	}

	@Override
	public ControlState getControlState() {
		return controlState;
	}

	@Override
	public Lap getLaps() {
		return laps;
	}

	@Override
	public PlayerStatus getPlayerStatus() {
		return status;
	}

	@Override
	public PlayerState adjust(ControlEvent event) {
		return new RacePlayerState(position,
				controlState.adjust(event),
				laps,
				status);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(position, controlState, laps, status);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
		return true;
	}
	
	if (!(obj instanceof PlayerState)) {
		return false;
	}
	
	PlayerState other = (PlayerState) obj;
	
	return Objects.equals(position, other.getPosition()) &&
			Objects.equals(controlState, other.getControlState()) &&
			Objects.equals(laps, other.getLaps()) &&
			Objects.equals(status, other.getPlayerStatus());
	}
}
