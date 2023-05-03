package me.navanda.shutdown_application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Schedule implements Runnable {


	private boolean willShutdown, stopFlag, willDelay;
	private Thread shutdownThread;
	private Shutdown shutdown = null;
	private final Config config;

	public Schedule(Config config) {
		willShutdown = false;
		stopFlag = false;
		shutdownThread = null;
		this.config = config;
	}

	public void shutdown() {
		shutdown = new Shutdown(shutdown);
		shutdownThread = new Thread(shutdown);
		shutdownThread.start();
		willShutdown = true;
	}

	@SuppressWarnings("unused")
	public void cancelShutdown() {
		if (shutdown != null)
			shutdown.exitThread();

		try {
			Process process = Runtime.getRuntime().exec("shutdown /a");
			process.waitFor();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		willShutdown = false;
	}

	/**
	 * Runs this operation.
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		while (!stopFlag) {
			Date date = new Date();
			int day = date.getDay();
			Map<Integer, int[]> shutdownTimes = config.getShutdownTimes();
			if (shutdownTimes != null && !willShutdown) {
				int[] time = shutdownTimes.get(day);
				if (time[0] == date.getHours() && time[1] == date.getMinutes()) {
					startShutdown();
				}
			} else if (willShutdown) {
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
		if (!willShutdown && shutdownThread == null) {
			shutdown();
			willShutdown = true;
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

		programs.sort(String::compareToIgnoreCase);

		return programs;
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

	public synchronized void setWillShutdown(boolean willShutdown) {
		this.willShutdown = willShutdown;
	}
}
