package me.navanda.shutdown_application;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

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
		configFile = new File("settings.conf");
		if(!configFile.exists()) {
			config.create();
		}
		try {
			config = objectMapper.readValue(configFile, Config.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}



		launch();
	}
}