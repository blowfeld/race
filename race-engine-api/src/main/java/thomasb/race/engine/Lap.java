package thomasb.race.engine;

public interface Lap extends Comparable<Lap> {
	
	int getCount();
	
	double getLapTime();
	
}
