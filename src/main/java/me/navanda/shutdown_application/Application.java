package me.navanda.shutdown_application;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;

public class Application extends javafx.application.Application {

	private static Config config;
	private static ObjectMapper objectMapper;
	private static File configFile;
	private static Schedule schedule;
	private static Thread scheduleThread;

	@Override
	public void start(Stage stage) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("hello-view.fxml"));
		Parent root = fxmlLoader.load();
		Controller controller = fxmlLoader.getController();
		controller.setApplication(this);
		controller.setStage(stage);
		Scene scene = new Scene(root, 550, 400);
		stage.setTitle("Shutdown Times");
		stage.setScene(scene);
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent event) {
				schedule.stopThread();
			}
		});
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