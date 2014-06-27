package thomasb.race.engine;

public interface PlayerState {
	
	PointDouble getPosition();
	
	ControlState getControlState();
	
	Lap getLaps();
	
	PlayerStatus getPlayerStatus();
	
	PlayerState adjust(ControlEvent event);
	
}
