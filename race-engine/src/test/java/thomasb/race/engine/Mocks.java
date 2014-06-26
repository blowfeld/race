package thomasb.race.engine;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class Mocks {
	public static ControlEvent controlEvent(int speedChange, int steeringChange) {
		String description = String.format(
				"ControlState[speedChange=%s, steeringChange=%s]",
				speedChange, steeringChange);
		
		ControlEvent controlEvent = mock(ControlEvent.class);
		when(controlEvent.speedChange()).thenReturn(speedChange);
		when(controlEvent.steeringChange()).thenReturn(steeringChange);
		when(controlEvent.toString()).thenReturn(description);
		
		return controlEvent;
	}
	
	public static ControlState controlState(int speed, int steering) {
		String description = String.format(
				"ControlState[speed=%s, steering=%s]",
				speed, steering);
		
		ControlState controlState = mock(ControlState.class);
		when(controlState.getSpeed()).thenReturn(speed);
		when(controlState.getSteering()).thenReturn(steering);
		when(controlState.toString()).thenReturn(description);
		
		return controlState;
	}	
	
	public static TrackSegment trackSegment(PointDouble start,
			PointDouble end,
			int maxSpeed,
			boolean isFinish) {
		String description = String.format(
				"TrackSegmentMock[start=%s, end=%s, maxSpeed=%s, isFinished=%s]",
				start, end, maxSpeed, isFinish);

		TrackSegment segment = mock(TrackSegment.class);
		when(segment.getStart()).thenReturn(start);
		when(segment.getEnd()).thenReturn(end);
		when(segment.getMaxSpeed()).thenReturn(maxSpeed);
		when(segment.isFinish()).thenReturn(isFinish);
		when(segment.toString()).thenReturn(description);
		
		return segment;
	}	
}
