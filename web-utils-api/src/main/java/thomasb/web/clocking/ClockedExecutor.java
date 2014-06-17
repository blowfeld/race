package thomasb.web.clocking;


public interface ClockedExecutor extends Runnable {
	void launch();
	
	void finish();
	
	void step();
	
	@Override
	void run();
	
	int getIntervalCount();
}
