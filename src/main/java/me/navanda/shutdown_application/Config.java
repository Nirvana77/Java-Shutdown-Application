package me.navanda.shutdown_application;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Config {
	private final Map<Integer, int[]> shutdownTimes;
	private List<String> gameNames;

	private static ObjectMapper objectMapper;
	private static File configFile;

	public Config() {
		shutdownTimes = new HashMap<>();
		objectMapper = new ObjectMapper();
		configFile = new File("settings.conf");
		gameNames = new ArrayList<>();

		// Initialize shutdown times for each day of the week
		shutdownTimes.put(0, new int[]{23, 0}); // 10pm on Sunday
		shutdownTimes.put(1, new int[]{22, 0}); // 10pm on Monday
		shutdownTimes.put(2, new int[]{22, 0}); // 10pm on Tuesday
		shutdownTimes.put(3, new int[]{22, 0}); // 10pm on Wednesday
		shutdownTimes.put(4, new int[]{22, 0}); // 11pm on Thursday
		shutdownTimes.put(5, new int[]{23, 0}); // 11pm on Friday
		shutdownTimes.put(6, new int[]{23, 0}); // 11pm on Saturday
	}

	public static void create() {
		try (FileWriter writer = new FileWriter(configFile)) {
			objectMapper.writeValue(writer, Config.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public static void delete() {
		configFile.delete();
	}

	public Map<Integer, int[]> getShutdownTimes() {
		return shutdownTimes;
	}

	public void setShutdownTimes(Integer dayOfWeek, int[] shutdownTime) {
		shutdownTimes.put(dayOfWeek, shutdownTime);
	}

	public List<String> getGameNames() {
		return gameNames;
	}

	public void setGameNames(List<String> gameNames) {
		this.gameNames = gameNames;
	}


}
