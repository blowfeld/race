package thomasb.race.app.dispatch;

import java.util.Map;

import thomasb.race.engine.ControlEvent;

import com.google.common.collect.ImmutableMap;


public enum ArrowControlEvent implements ControlEvent {
	LEFT(0, -1),
	RIGHT(0, 1),
	UP(1, 0),
	DOWN(-1, 0),
	VOID(0, 0);
	
	private static Map<Integer, ArrowControlEvent> keyMap = ImmutableMap.of(
		37, LEFT,
		39, RIGHT,
		38, UP,
		40, DOWN);
	
	private static final int STEERING = 15;
	private static final int SPEED = 10;
	
	private final int speedChange;
	private final int steeringChange;
	
	private ArrowControlEvent(int speedChange, int steeringChange) {
		this.speedChange = speedChange * SPEED;
		this.steeringChange = steeringChange * STEERING;
	}
	
	public static ArrowControlEvent fromKey(int key) {
		return keyMap.containsKey(key) ?
				keyMap.get(key) : VOID;
	}
	
	@Override
	public int speedChange() {
		return speedChange;
	}
	
	@Override
	public int steeringChange() {
		return steeringChange;
	}
}
