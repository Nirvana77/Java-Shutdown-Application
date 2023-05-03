package me.navanda.shutdown_application.Model;

public class Shutdown implements Runnable {
	private boolean willExit;
	private final Schedule schedule;
	private Callback callback;

	public Shutdown(Schedule schedule, Callback callback) {
		willExit = false;
		this.schedule = schedule;
		this.callback = callback;
	}

	public Shutdown(Shutdown shutdown) {
		this.schedule = shutdown.schedule;
		this.willExit = shutdown.willExit;
	}

	/**
	 * Runs this operation.
	 */
	@Override
	public void run() {
		while (schedule.isWillDelay() && !willExit) {
			callback.callback("LOG,Shutdown awaits");
			try {
				Thread.sleep(1000 * 10);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		if (willExit) {
			callback.callback("ERROR,Shutdown interrupted");
			return;
		}
		//Process process = Runtime.getRuntime().exec("shutdown /s /f /t 0");
		//process.waitFor();
		callback.callback("LOG,Shutdown!");
		schedule.setWillShutdown(false);

	}

	public void exitThread() {
		willExit = true;
	}
}
