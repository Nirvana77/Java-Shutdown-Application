package me.navanda.shutdown_application;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class HelloController {
	private HelloApplication application;
	private Stage stage;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setApplication(HelloApplication application) {
		this.application = application;
	}

	@FXML
	private Label welcomeText;

	@FXML
	protected void onHelloButtonClick() {
		welcomeText.setText("Welcome to JavaFX Application!");
	}

	@FXML
	protected void onStopClick() {
		application.getSchedule().shutdown();
		stage.close();
	}
}