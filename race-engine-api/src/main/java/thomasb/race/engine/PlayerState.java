package thomasb.race.engine;

public interface PlayerState {
	
	PointDouble getPosition();
	
	ControlState getControlState();
	
	int getLaps();
	
	PlayerStatus getPlayerStatus();
}
