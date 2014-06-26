package thomasb.race.engine;

import org.junit.BeforeClass;

public class Test2D {
	static PointDouble[][] points = new PointDouble[21][21];
	
	@BeforeClass
	public static void setupPoints() {
		for (int i = 0; i < 21; i++) {
			for (int j = 0; j < 21; j++) {
				PointDouble point = new VectorPoint(i, j);
				points[i][j] = point;
			}
		}
	}
}
