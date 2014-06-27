package thomasb.race.engine;

public class RacePlayerState implements PlayerState {
	private final PointDouble position;
	private final ControlState controlState;
	private final int laps;
	private final PlayerStatus status;

	public RacePlayerState(PointDouble position,
			ControlState controlState,
			int laps,
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
	public int getLaps() {
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
}
