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
		ControllEvent controllEvent = Mocks.controllEvent(1, 0);
		
		ControlState actual = zeroState.adjust(controllEvent);
		
		assertEquals(new ControlStateImp(1, 0), actual);
	}
	
	@Test
	public void increaseSteering() {
		ControllEvent controllEvent = Mocks.controllEvent(0, 1);
		
		ControlState actual = zeroState.adjust(controllEvent);
		
		assertEquals(new ControlStateImp(0, 1), actual);
	}
	
	@Test
	public void decreaseSteeringBelowZero() {
		ControllEvent controllEvent = Mocks.controllEvent(0, -1);
		
		ControlState actual = zeroState.adjust(controllEvent);
		
		assertEquals(new ControlStateImp(0, 359), actual);
	}
	
	@Test
	public void decreaseSteeringBelowMinus360() {
		ControllEvent controllEvent = Mocks.controllEvent(0, -361);
		
		ControlState actual = zeroState.adjust(controllEvent);
		
		assertEquals(new ControlStateImp(0, 359), actual);
	}
	
	@Test
	public void increaseSteeringAbove360() {
		ControllEvent controllEvent = Mocks.controllEvent(0, 361);
		
		ControlState actual = zeroState.adjust(controllEvent);
		
		assertEquals(new ControlStateImp(0, 1), actual);
	}
	
	@Test
	public void speedAlwaysPositive() {
		ControllEvent controllEvent = Mocks.controllEvent(-1, 0);
		
		ControlState actual = zeroState.adjust(controllEvent);
		
		assertEquals(new ControlStateImp(0, 0), actual);
	}
	
	@Test
	public void speedAtMostTwo() {
		ControllEvent controllEvent = Mocks.controllEvent(3, 0);
		
		ControlState actual = zeroState.adjust(controllEvent);
		
		assertEquals(new ControlStateImp(2, 0), actual);
	}

	@Test
	public void fullExample() {
		ControlState state = new ControlStateImp(1, 45);
		ControllEvent controllEvent = Mocks.controllEvent(1, -90);
		
		ControlState actual = state.adjust(controllEvent);
		
		assertEquals(new ControlStateImp(2, 315), actual);
	}
}
