package me.navanda.shutdown_application;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Config {
	private final Map<Integer, int[]> shutdownTimes;
	private volatile List<String> gameNames;
	private final Map<Integer, int[]> defaultShutdownTimes = new HashMap<>();

	@JsonIgnore
	private static ObjectMapper objectMapper;
	@JsonIgnore
	private static File configFile;

	private static final int DAYSINWEEK = 7;

	public Config() {
		shutdownTimes = new HashMap<>();
		objectMapper = new ObjectMapper();
		configFile = new File("settings.conf");
		gameNames = new ArrayList<>();

		// Initialize shutdown times for each day of the week
		defaultShutdownTimes.put(0, new int[]{23, 0, 1}); // 10pm on Sunday is ON
		defaultShutdownTimes.put(1, new int[]{22, 0, 1}); // 10pm on Monday is ON
		defaultShutdownTimes.put(2, new int[]{22, 0, 1}); // 10pm on Tuesday is ON
		defaultShutdownTimes.put(3, new int[]{22, 0, 1}); // 10pm on Wednesday is ON
		defaultShutdownTimes.put(4, new int[]{22, 0, 1}); // 11pm on Thursday is ON
		defaultShutdownTimes.put(5, new int[]{23, 0, 0}); // 11pm on Friday is OFF
		defaultShutdownTimes.put(6, new int[]{23, 0, 1}); // 11pm on Saturday is ON
		insertStandardData();
	}

	private void insertStandardData() {
		for (int i = 0; i < defaultShutdownTimes.size(); i++) {
			int[] arr = shutdownTimes.get(i);
			if (arr == null)
				shutdownTimes.put(i, defaultShutdownTimes.get(i));
		}
	}

	public void create() {
		Config config = new Config();

		config.setShutdownTimes(this.shutdownTimes);
		config.setGameNames(this.gameNames);

		try (FileWriter writer = new FileWriter(configFile)) {
			objectMapper.writeValue(writer, config);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	//TODO: Add a try statement to solve or detect errors
	public void delete() {
		configFile.delete();
	}

	public synchronized Map<Integer, int[]> getShutdownTimes() {
		return shutdownTimes;
	}

	public synchronized void setShutdownTimes(Integer dayOfWeek, int[] shutdownTime, boolean isOn) {
		if (dayOfWeek < 0 || dayOfWeek > 6 ||
				shutdownTime[0] < 0 || shutdownTime[0] > 24 ||
				shutdownTime[1] < 0 || shutdownTime[1] > 59) {
			return;
		}


		int[] arr = new int[3];
		arr[0] = shutdownTime[0];
		arr[1] = shutdownTime[1];
		arr[2] = isOn ? 1 : 0;

		System.out.println("arr: [" + arr[0] + "," + arr[1] + "," + arr[2] + "]");

		shutdownTimes.put(dayOfWeek, arr);
	}

	public List<String> getGameNames() {
		return gameNames;
	}

	public void setGameNames(List<String> gameNames) {
		this.gameNames = gameNames;
	}

	public synchronized void setShutdownTimes(Map<Integer, int[]> shutdownTimes) {
		this.shutdownTimes.clear(); // clear the existing map

		// iterate over the entries in the input map and copy them to the shutdownTimes map
		for (Map.Entry<Integer, int[]> entry : shutdownTimes.entrySet()) {
			Integer dayOfWeek = entry.getKey();
			int[] shutdownTime = entry.getValue();

			// validate the shutdown time array
			if (shutdownTime.length != 3) {
				throw new IllegalArgumentException("Shutdown time array must have length 3");
			}

			this.shutdownTimes.put(dayOfWeek, Arrays.copyOf(shutdownTime, 3));
		}

	}

	public int getHour(int day) {
		return shutdownTimes.get(day)[0];
	}

	public int getMinute(int day) {
		return shutdownTimes.get(day)[1];
	}

	public boolean isShutdownDay(int day) {
		return shutdownTimes.get(day)[2] == 1;
	}

	public void save() {
		try (FileWriter writer = new FileWriter(configFile)) {
			Config config = new Config();
			config.setShutdownTimes(shutdownTimes);
			objectMapper.writeValue(writer, config);
			System.out.println("Saved config");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void validate() {
		int size = shutdownTimes.size();
		if (size < DAYSINWEEK) {
			insertStandardData();
		} else if (size > DAYSINWEEK) {
			for (int i = DAYSINWEEK; i < size; i++) {
				shutdownTimes.remove(i);
			}
		} else {
			boolean hasRemoved = false;
			for (int i = 0; i < size; i++) {
				int[] shutdownTime = shutdownTimes.get(i);
				if (shutdownTime[0] < 0 || shutdownTime[0] > 24 ||
						shutdownTime[1] < 0 || shutdownTime[1] > 59) {
					hasRemoved = true;
					shutdownTimes.remove(i);
				}
			}

			if (hasRemoved) {
				validate();
				save();
			}
		}

	}
}
