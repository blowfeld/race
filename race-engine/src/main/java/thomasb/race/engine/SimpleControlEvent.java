package thomasb.race.engine;

public enum SimpleControlEvent implements ControlEvent {
	LEFT(0, 1),
	RIGHT(0, -1),
	SPEED_UP(1, 0),
	SLOW_DOWN(-1, 0),
	VOID(0, 0);

	private final int speedChange;
	private final int steeringChange;

	private SimpleControlEvent(int speedChange, int steeringChange) {
		this.speedChange = speedChange;
		this.steeringChange = steeringChange;
	}
	
	@Override
	public int speedChange() {
		return speedChange;
	}

	@Override
	public int steeringChange() {
		return steeringChange;
	}
}
