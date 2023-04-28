package me.navanda.shutdown_application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Schedule implements Runnable {


	private volatile boolean willShotdown, willDelay, stopFlag;
	private Thread shutdownThread, ownThread;
	private final Config config;

	public Schedule(Config config) {
		willShotdown = false;
		willDelay = false;
		stopFlag = false;
		shutdownThread = null;
		this.config = config;
	}

	public void shutdown() {
		shutdownThread = new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("Shutdown awaits....");
				while (willDelay) ;
				//Process process = Runtime.getRuntime().exec("shutdown /s /f /t 0");
				//process.waitFor();
				System.out.println("Shutdown!");
				if (ownThread != null) {
					try {
						stopFlag = true;
						ownThread.join();
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}
			}
		});
		shutdownThread.start();
		willShotdown = true;
	}

	public void cancelShutdown() {
		if (shutdownThread != null) {
			shutdownThread.interrupt();
			try {
				Process process = Runtime.getRuntime().exec("shutdown /a");
				process.waitFor();
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
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
			if (shutdownTimes != null) {
				int[] time = shutdownTimes.get(day);
				if ((time[0] == date.getHours() && time[1] == date.getMinutes()) || willDelay) {
					willDelay = true;
					shutdown();

					boolean willNotDelay = true;
					List<String> programs = loadPrograms();
					Set<String> excludingPrograms = new HashSet<>(config.getGameNames());

					for (String program : programs) {
						if (excludingPrograms.contains(program.toLowerCase())) {
							willNotDelay = false;
							break;
						}
					}

					if (willNotDelay) {
						willDelay = false;
					}
				}
			}
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
				programs.add(split[0]);
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
}
