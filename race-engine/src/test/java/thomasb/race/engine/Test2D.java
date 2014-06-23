package thomasb.race.engine;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.BeforeClass;

public class Test2D {
	static PointDouble[][] points = new PointDouble[20][20];
	
	@BeforeClass
	public static void setupPoints() {
		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 20; j++) {
				PointDouble mock = mock(PointDouble.class);
				setupPoint(mock, i, j);
				points[i][j] = mock;
			}
		}
	}
	
	private static void setupPoint(PointDouble point, double x, double y) {
		when(point.getX()).thenReturn(x);
		when(point.getY()).thenReturn(y);
		when(point.toString()).thenReturn("PointMock [x=" + x + ", y=" + y + "]");
	}

}
