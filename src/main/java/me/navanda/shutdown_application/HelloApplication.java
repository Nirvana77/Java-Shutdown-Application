package me.navanda.shutdown_application;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HelloApplication extends Application {

	private static Config config;
	private static ObjectMapper objectMapper;
	private static File configFile;
	@Override
	public void start(Stage stage) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
		Scene scene = new Scene(fxmlLoader.load(), 320, 240);
		stage.setTitle("Hello!");
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		config = new Config();
		objectMapper = new ObjectMapper();
		configFile = new File("settings.conf");
		if(!configFile.exists()) {
			config.create();
		}
		try {
			config = objectMapper.readValue(configFile, Config.class);
		} catch (IOException e) {
			config.delete();
			config.create();
		}
		List<String> programs = loadPrograms();

		launch();
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
}