package thomasb.race.engine;

import thomasb.race.engine.Steering;

public enum SimpleSteering implements Steering {
	LEFT(-30),
	STRAIGHT(0),
	RIGHT(30);
	
	private final int degrees;
	
	SimpleSteering(int degrees) {
		this.degrees = degrees;
	}
	
	@Override
	public int getDegrees() {
		return degrees;
	}
}
