package thomasb.web.clocking;


public interface ClockedExecutor extends Runnable {
	void launch();
	
	void finish();
	
	@Override
	void run();
	
	Interval getCurrentInterval();
	
	int getIntervalCount();
	
	interface Interval {

		void finish();
	
	}
}
