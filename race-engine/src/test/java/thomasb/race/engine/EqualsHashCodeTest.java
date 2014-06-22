package thomasb.race.engine;

import java.util.Arrays;
import java.util.Collection;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class EqualsHashCodeTest {
	
	private Class<?> clazz;

	@Parameters
	public static Collection<Object[]> classes() {
		return Arrays.asList(new Object[][] {
				{ControlStateImp.class, true},
				{BaseSegment.class, true},
				{VectorPoint.class, true},
				{RacePathSegment.class, true},
				{RaceTrackSegment.class, true}});
	}
	
	public EqualsHashCodeTest(Class<?> clazz, boolean expected) {
		this.clazz = clazz;
	}
	
	@Test
	public void testEquals() {
		EqualsVerifier.forClass(clazz)
				.suppress(Warning.NULL_FIELDS)
			.verify();
	}
}
