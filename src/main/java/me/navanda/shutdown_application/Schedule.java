package me.navanda.shutdown_application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Schedule implements Runnable {


	private boolean willShotdown, stopFlag, willDelay;
	private Thread shutdownThread, ownThread;
	private Shutdown shutdown = null;
	private final Config config;

	public Schedule(Config config) {
		willShotdown = false;
		stopFlag = false;
		shutdownThread = null;
		this.config = config;
		shutdown = new Shutdown(this);
	}

	public void shutdown() {
		shutdown = new Shutdown(shutdown);
		shutdownThread = new Thread(shutdown);
		shutdownThread.start();
		willShotdown = true;
	}

	public void cancelShutdown() {
		shutdown.exitThread();
		try {
			Process process = Runtime.getRuntime().exec("shutdown /a");
			process.waitFor();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		willShotdown = false;
	}

	/**
	 * Runs this operation.
	 */
	@Override
	public void run() {
		while (!stopFlag) {
			Date date = new Date();
			int day = date.getDay();
			Map<Integer, int[]> shutdownTimes = config.getShutdownTimes();
			if (shutdownTimes != null && !willShotdown) {
				int[] time = shutdownTimes.get(day);
				if (time[0] == date.getHours() && time[1] == date.getMinutes()) {
					startShutdown();
				}
			} else if (willShotdown) {
				try {
					Thread.sleep(10000); // 1 minute
				} catch (InterruptedException e) {
					// handle the exception if needed
				}
				startShutdown();
			}
		}
	}

	public void startShutdown() {
		setWillDelay(true);
		if (!willShotdown && shutdownThread == null) {
			shutdown();
			willShotdown = true;
		}

		boolean willNotDelay = true;
		List<String> programs = loadPrograms();
		Set<String> excludingPrograms = new HashSet<>(config.getGameNames());

		for (String program : programs) {
			if (excludingPrograms.contains(program)) {
				willNotDelay = false;
				System.out.println(program);
				break;
			}
		}

		if (willNotDelay) {
			setWillDelay(false);
		}

	}

	private static List<String> loadPrograms() {
		List<String> programs = new ArrayList<>();
		try {
			ProcessBuilder processBuilder = new ProcessBuilder("tasklist.exe");
			Process process = processBuilder.start();
			InputStream inputStream = process.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

			String line;
			while ((line = bufferedReader.readLine()) != null) {
				String[] split = line.split(" ");
				String program = split[0];
				if (!programs.contains(program))
					programs.add(program);
			}

			bufferedReader.close();
			inputStreamReader.close();
			inputStream.close();

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		Collections.sort(programs, (s1, s2) -> s1.compareToIgnoreCase(s2));

		return programs;
	}

	public void setOwnThred(Thread scheduleThread) {
		ownThread = scheduleThread;
	}

	public void stopThread() {
		stopFlag = true;
	}

	public synchronized void setWillDelay(boolean willDelay) {
		this.willDelay = willDelay;
	}

	public synchronized boolean isWillDelay() {
		return willDelay;
	}

	public synchronized void setWillShutdown(boolean willShotdown) {
		this.willShotdown = willShotdown;
	}
}
