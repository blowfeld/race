package thomasb.race.web.handlers;

import static java.lang.Integer.toHexString;

import java.util.ArrayList;
import java.util.List;


public enum PlayerColors {
	INSTANCE;
	
	private final List<String> colors = new ArrayList<>();
	
	public synchronized String get(int i) {
		if (i < colors.size()) {
			return colors.get(i);
		}
		
		String color = "#" + toHexString((int)(Math.random() * 0x1000000));
		colors.add(color);
		
		return color;
	}
}
