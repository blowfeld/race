package thomasb.race.engine;

enum TrackType {
	ASPHALT(2), GREEN(1), WALL(0);
	
	private final int maxSpeed;

	private TrackType(int maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	int getMaxSpeed() {
		return maxSpeed;
	}
}
