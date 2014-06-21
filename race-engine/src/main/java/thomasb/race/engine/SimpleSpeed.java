package thomasb.race.engine;

import thomasb.race.engine.Speed;

public enum SimpleSpeed implements Speed {
	STOP(0),
	SLOW(10),
	FAST(20);
	
	private final int value;
	
	SimpleSpeed(int value) {
		this.value = value;
	}
	
	@Override
	public int getSpeed() {
		return value;
	}
}