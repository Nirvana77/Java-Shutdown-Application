package me.navanda.shutdown_application.Model;

import javafx.scene.control.Alert;
import me.navanda.shutdown_application.Services.Database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.*;

public class Schedule implements Runnable {


	private boolean willShutdown, stopFlag, willDelay;
	private Thread shutdownThread;
	private Shutdown shutdown = null;
	private final Config config;

	private final Database db;


	public Schedule(Config config) {
		willShutdown = false;
		stopFlag = false;
		shutdownThread = null;
		this.config = config;
		try {
			//TODO: Make it a request to the config file to get the values.
			// As well as make the database encrypted.
			db = new Database("db.gamingpassestime.com", "5432", "testing", "shutdownTimer", "");
			if (!db.tableExists("logs")) {
				db.executeSQL("" +
						"CREATE TABLE logs (\n" +
						"  id SERIAL PRIMARY KEY,\n" +
						"  log_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
						"  log_level VARCHAR(10),\n" +
						"  message TEXT\n" +
						");"
				);
			}
		} catch (SQLException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public void shutdown() {
		shutdown = new Shutdown(this, context -> {
			String[] split = context.split(",");
			System.out.println(context);
			try {
				db.log(Database.LogLevel.fromString(split[0]), split[1]);
			} catch (SQLException ignored) {
			}

		});
		shutdownThread = new Thread(shutdown);
		shutdownThread.start();
		willShutdown = true;
	}

	@SuppressWarnings("unused")
	public void cancelShutdown() {
		if (!willShutdown)
			return;

		if (shutdown != null)
			shutdown.exitThread();

		try {
			Process process = Runtime.getRuntime().exec("shutdown /a");
			process.waitFor();
			Shutdown.showAlert("Warning", "Computers shutdown has been aborted", Alert.AlertType.WARNING);
			try {
				db.log(Database.LogLevel.WARNING, "Shutdown has been aborted");
			} catch (SQLException ignored) {
			}
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
					Thread.sleep(1000 * 60); // 1 minute
				} catch (InterruptedException e) {
					// handle the exception if needed
				}
				delayCheck();
			}
		}
	}

	public void startShutdown() {
		setWillDelay(true);
		if (!willShutdown) {
			shutdown();
			willShutdown = true;
		}

		delayCheck();

	}

	private void delayCheck() {
		boolean willNotDelay = true;
		List<String> programs = loadPrograms();
		Set<String> excludingPrograms = new HashSet<>(config.getGameNames());

		if (excludingPrograms.size() != 0) {
			for (String program : programs) {
				if (excludingPrograms.contains(program)) {
					willNotDelay = false;
					System.out.println(program);
					break;
				}
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
