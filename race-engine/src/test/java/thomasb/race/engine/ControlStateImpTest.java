package thomasb.race.engine;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ControlStateImpTest {
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	private ControlStateImp zeroState;

	@Before
	public void setupState() {
		zeroState = new ControlStateImp(0, 0);
	}
	
	@Test
	public void increaseSpeed() {
		ControlEvent controlEvent = Mocks.controlEvent(1, 0);
		
		ControlState actual = zeroState.adjust(controlEvent);
		
		assertEquals(new ControlStateImp(1, 0), actual);
	}
	
	@Test
	public void increaseSteering() {
		ControlEvent controlEvent = Mocks.controlEvent(0, 1);
		
		ControlState actual = zeroState.adjust(controlEvent);
		
		assertEquals(new ControlStateImp(0, 1), actual);
	}
	
	@Test
	public void decreaseSteeringBelowZero() {
		ControlEvent controlEvent = Mocks.controlEvent(0, -1);
		
		ControlState actual = zeroState.adjust(controlEvent);
		
		assertEquals(new ControlStateImp(0, 359), actual);
	}
	
	@Test
	public void decreaseSteeringBelowMinus360() {
		ControlEvent controlEvent = Mocks.controlEvent(0, -361);
		
		ControlState actual = zeroState.adjust(controlEvent);
		
		assertEquals(new ControlStateImp(0, 359), actual);
	}
	
	@Test
	public void increaseSteeringAbove360() {
		ControlEvent controlEvent = Mocks.controlEvent(0, 361);
		
		ControlState actual = zeroState.adjust(controlEvent);
		
		assertEquals(new ControlStateImp(0, 1), actual);
	}
	
	@Test
	public void speedAlwaysPositive() {
		ControlEvent controlEvent = Mocks.controlEvent(-1, 0);
		
		ControlState actual = zeroState.adjust(controlEvent);
		
		assertEquals(new ControlStateImp(0, 0), actual);
	}
	
	@Test
	public void speedAtMostTwo() {
		ControlEvent controlEvent = Mocks.controlEvent(3, 0);
		
		ControlState actual = zeroState.adjust(controlEvent);
		
		assertEquals(new ControlStateImp(2, 0), actual);
	}

	@Test
	public void fullExample() {
		ControlState state = new ControlStateImp(1, 45);
		ControlEvent controlEvent = Mocks.controlEvent(1, -90);
		
		ControlState actual = state.adjust(controlEvent);
		
		assertEquals(new ControlStateImp(2, 315), actual);
	}
}
