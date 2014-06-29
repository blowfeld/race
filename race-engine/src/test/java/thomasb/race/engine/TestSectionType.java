package thomasb.race.engine;

public enum TestSectionType implements SectionType {
	ASPHALT(2), GREEN(1), WALL(0);
	
	private final int maxSpeed;

	private TestSectionType(int maxSpeed) {
		this.maxSpeed = maxSpeed;
	}
	
	@Override
	public int getMaxSpeed() {
		return maxSpeed;
	}
}
