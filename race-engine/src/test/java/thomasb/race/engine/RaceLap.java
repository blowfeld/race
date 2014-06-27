package thomasb.race.engine;

import java.util.Objects;


public final class RaceLap implements Lap, Comparable<Lap> {
	private final int count;
	private final double lapTime;
	
	public RaceLap(int count, double lapTime) {
		this.count = count;
		this.lapTime = lapTime;
	}
	
	@Override
	public int getCount() {
		return count;
	}
	
	@Override
	public double getLapTime() {
		return lapTime;
	}

	@Override
	public int compareTo(Lap o) {
		int countDiff = count - o.getCount();
		
		return countDiff == 0 ?
				Double.compare(o.getLapTime(), lapTime) :
				countDiff;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(count, lapTime);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (!(obj instanceof Lap)) {
			return false;
		}
		
		Lap other = (Lap) obj;
		
		return Objects.equals(count, other.getCount()) &&
				Objects.equals(lapTime, other.getLapTime());
	}
}
