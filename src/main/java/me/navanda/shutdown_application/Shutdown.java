package me.navanda.shutdown_application;

public class Shutdown implements Runnable {
	private boolean willExit;
	private final Schedule schedule;

	public Shutdown(Schedule schedule) {
		willExit = false;
		this.schedule = schedule;
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
		boolean willDelay = schedule.isWillDelay();
		while (willDelay && !willExit) {
			System.out.println("Shutdown awaits....");
			try {
				Thread.sleep(1000 * 10);
				willDelay = schedule.isWillDelay();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		if (willExit) {
			System.out.println("Shutdown interrupted....");
			return;
		}
		//Process process = Runtime.getRuntime().exec("shutdown /s /f /t 0");
		//process.waitFor();
		System.out.println("Shutdown!");
		schedule.setWillShutdown(false);

	}

	public void exitThread() {
		willExit = true;
	}
}
