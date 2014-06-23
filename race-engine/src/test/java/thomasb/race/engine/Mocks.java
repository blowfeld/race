package thomasb.race.engine;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class Mocks {
	public static ControlEvent controlEvent(int speedChange, int steeringChange) {
		ControlEvent controlEvent = mock(ControlEvent.class);
		when(controlEvent.speedChange()).thenReturn(speedChange);
		when(controlEvent.steeringChange()).thenReturn(steeringChange);
		
		return controlEvent;
	}
	
	public static ControlState controlState(int speed, int steering) {
		ControlState controlState = mock(ControlState.class);
		when(controlState.getSpeed()).thenReturn(speed);
		when(controlState.getSteering()).thenReturn(steering);
		
		return controlState;
	}	
	
	public static TrackSegment trackSegment(PointDouble start,
			PointDouble end,
			int maxSpeed,
			boolean isFinish) {
		TrackSegment segment = mock(TrackSegment.class);
		when(segment.getStart()).thenReturn(start);
		when(segment.getEnd()).thenReturn(end);
		when(segment.getMaxSpeed()).thenReturn(maxSpeed);
		when(segment.isFinish()).thenReturn(isFinish);
		
		return segment;
	}	
}
