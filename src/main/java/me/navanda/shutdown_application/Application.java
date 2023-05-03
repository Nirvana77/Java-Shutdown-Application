package me.navanda.shutdown_application;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class Application extends javafx.application.Application {

	private Config config;
	private static Schedule schedule;

	@Override
	public void start(Stage stage) throws IOException {
		config = new Config();
		ObjectMapper objectMapper = new ObjectMapper();
		File configFile = new File("settings.conf");
		if (!configFile.exists()) {
			config.create();
		}

		try {
			config = objectMapper.readValue(configFile, Config.class);
			config.validate();
		} catch (IOException e) {
			config.delete();
			config.create();

			try {
				config = objectMapper.readValue(configFile, Config.class);
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}

		schedule = new Schedule(config);
		Thread scheduleThread = new Thread(schedule);
		scheduleThread.start();

		FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("hello-view.fxml"));
		Parent root = fxmlLoader.load();
		Controller controller = fxmlLoader.getController();
		controller.setApplication(this);
		controller.setStage(stage);
		Scene scene = new Scene(root, 550, 400);
		stage.setTitle("Shutdown Times");
		stage.setScene(scene);
		stage.setOnCloseRequest(event -> schedule.stopThread());
		stage.show();
	}

	public static void main(String[] args) {

		launch();
	}

	public Config getConfig() {
		return config;
	}

	public Schedule getSchedule() {
		return schedule;
	}
}