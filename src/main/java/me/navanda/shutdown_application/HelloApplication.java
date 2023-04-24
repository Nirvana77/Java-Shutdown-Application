package me.navanda.shutdown_application;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;

public class HelloApplication extends Application {

	private static Config config;
	private static ObjectMapper objectMapper;
	private static File configFile;
	private static Schedule schedule;
	private static Thread scheduleThread;

	@Override
	public void start(Stage stage) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
		Parent root = fxmlLoader.load();
		HelloController controller = fxmlLoader.getController();
		controller.setApplication(this);
		controller.setStage(stage);
		Scene scene = new Scene(root, 320, 240);
		stage.setTitle("Hello!");
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		config = new Config();
		objectMapper = new ObjectMapper();
		configFile = new File("settings.conf");
		if (!configFile.exists()) {
			Config.create();
		} else {
			try {
				config = objectMapper.readValue(configFile, Config.class);
			} catch (IOException e) {
				Config.delete();
				Config.create();
			}
		}

		schedule = new Schedule(config);
		scheduleThread = new Thread(schedule);
		schedule.setOwnThred(scheduleThread);
		scheduleThread.start();

		launch();
	}

	public Config getConfig() {
		return config;
	}

	public Schedule getSchedule() {
		return schedule;
	}
}