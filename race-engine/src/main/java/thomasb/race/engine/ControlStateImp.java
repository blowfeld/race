package thomasb.race.engine;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Objects.hash;

public final class ControlStateImp implements ControlState {
	private static final int SPEED_MIN = 0;
	private static final int SPEED_MAX = 2;
	
	private final int speed;
	private final int steering;
	
	public ControlStateImp(int speed, int steering) {
		checkArgument(0 <= speed && speed <= 2, "Speed must be in [0, 2]");
		checkArgument(0 <= steering && steering < 360, "Speed must be in [0, 360)");
		
		this.speed = speed;
		this.steering = steering;
	}
	
	@Override
	public int getSpeed() {
		return speed;
	}
	
	@Override
	public int getSteering() {
		return steering;
	}
	
	@Override
	public ControlState adjust(ControllEvent event) {
		int newSpeed = min(max(speed + event.speedChange(), SPEED_MIN), SPEED_MAX);
		int steeringChange = event.steeringChange() % 360;
		int newDirection = (360 + steering + steeringChange) % 360;
		
		return new ControlStateImp(newSpeed, newDirection);
	}
	
	@Override
	public int hashCode() {
		return hash(speed, steering);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (!(obj instanceof ControlState)) {
			return false;
		}
		
		ControlState other = (ControlState) obj;
		
		return speed == other.getSpeed() &&
				steering == other.getSteering();
	}
	
	@Override
	public String toString() {
		return "ControlState [steering=" + steering + ", speed=" + speed + "]";
	}
}
