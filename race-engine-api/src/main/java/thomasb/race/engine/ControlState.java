package thomasb.race.engine;

public interface ControlState {
	
	int getSteering();
	
	int getSpeed();
	
	ControlState adjust(ControlEvent event);
	
}