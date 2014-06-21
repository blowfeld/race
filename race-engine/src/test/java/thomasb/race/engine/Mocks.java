package thomasb.race.engine;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class Mocks {
	public static ControllEvent controllEvent(int speedChange, int steeringChange) {
		ControllEvent controllEvent = mock(ControllEvent.class);
		when(controllEvent.speedChange()).thenReturn(speedChange);
		when(controllEvent.steeringChange()).thenReturn(steeringChange);
		
		return controllEvent;
	}
	
	public static ControlState controllState(int speed, int steering) {
		ControlState controllState = mock(ControlState.class);
		when(controllState.getSpeed()).thenReturn(speed);
		when(controllState.getSteering()).thenReturn(steering);
		
		return controllState;
	}	
}
