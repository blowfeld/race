package thomasb.race.engine.entities;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Objects.hash;
import thomasb.race.engine.ControlState;
import thomasb.race.engine.Speed;
import thomasb.race.engine.Steering;

public final class ControlStateImp implements ControlState {
	private final Steering steering;
	private final Speed speed;
	
	public ControlStateImp(Steering steering, Speed speed) {
		this.steering = checkNotNull(steering);
		this.speed = checkNotNull(speed);
	}
	
	@Override
	public Steering getSteering() {
		return steering;
	}
	
	@Override
	public Speed getSpeed() {
		return speed;
	}
	
	@Override
	public int hashCode() {
		return hash(speed.getSpeed(), steering.getDegrees());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj instanceof ControlState) {
			return false;
		}
		
		ControlState other = (ControlState) obj;
		
		if (other.getSpeed() == null || other.getSteering() == null) {
			return false;
		}
		
		return speed.getSpeed() == other.getSpeed().getSpeed() &&
				steering.getDegrees() == other.getSteering().getDegrees();
	}
	
	@Override
	public String toString() {
		return "ControlState [steering=" + steering + ", speed=" + speed + "]";
	}
}
