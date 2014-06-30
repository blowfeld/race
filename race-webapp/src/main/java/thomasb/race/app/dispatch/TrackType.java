package thomasb.race.app.dispatch;

import thomasb.race.engine.SectionType;

public enum TrackType implements SectionType {
	ASPHALT(20), GREEN(10), WALL(0);
	
	private final int maxSpeed;

	private TrackType(int maxSpeed) {
		this.maxSpeed = maxSpeed;
	}
	
	@Override
	public int getMaxSpeed() {
		return maxSpeed;
	}
}
