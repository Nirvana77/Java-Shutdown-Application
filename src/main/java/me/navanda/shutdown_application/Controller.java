package me.navanda.shutdown_application;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Controller {
	private Application application;
	private Stage stage;
	@FXML
	private TextField hourInput0;
	@FXML
	private TextField minuteInput0;
	@FXML
	private CheckBox dayCheckbox0;
	@FXML
	private TextField hourInput1;
	@FXML
	private TextField minuteInput1;
	@FXML
	private CheckBox dayCheckbox1;
	@FXML
	private TextField hourInput2;
	@FXML
	private TextField minuteInput2;
	@FXML
	private CheckBox dayCheckbox2;
	@FXML
	private TextField hourInput3;
	@FXML
	private TextField minuteInput3;
	@FXML
	private CheckBox dayCheckbox3;
	@FXML
	private TextField hourInput4;
	@FXML
	private TextField minuteInput4;
	@FXML
	private CheckBox dayCheckbox4;
	@FXML
	private TextField hourInput5;
	@FXML
	private TextField minuteInput5;
	@FXML
	private CheckBox dayCheckbox5;
	@FXML
	private TextField hourInput6;
	@FXML
	private TextField minuteInput6;
	@FXML
	private CheckBox dayCheckbox6;

	private Config config;

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setApplication(Application application) {
		this.application = application;
		config = application.getConfig();

		setField(hourInput0, config.getHour(0));
		setField(minuteInput0, config.getMinute(0));
		setCheckBox(dayCheckbox0, config.isShutdownDay(0));

		setField(hourInput1, config.getHour(1));
		setField(minuteInput1, config.getMinute(1));
		setCheckBox(dayCheckbox1, config.isShutdownDay(1));

		setField(hourInput2, config.getHour(2));
		setField(minuteInput2, config.getMinute(2));
		setCheckBox(dayCheckbox2, config.isShutdownDay(2));

		setField(hourInput3, config.getHour(3));
		setField(minuteInput3, config.getMinute(3));
		setCheckBox(dayCheckbox3, config.isShutdownDay(3));

		setField(hourInput4, config.getHour(4));
		setField(minuteInput4, config.getMinute(4));
		setCheckBox(dayCheckbox4, config.isShutdownDay(4));

		setField(hourInput5, config.getHour(5));
		setField(minuteInput5, config.getMinute(5));
		setCheckBox(dayCheckbox5, config.isShutdownDay(5));

		setField(hourInput6, config.getHour(6));
		setField(minuteInput6, config.getMinute(6));
		setCheckBox(dayCheckbox6, config.isShutdownDay(6));
	}

	@FXML
	protected void onStopClick() {
		application.getSchedule().shutdown();
		stage.close();
	}

	@FXML
	protected void onSaveClick() {
		System.out.println("Save!");
	}

	private void setField(TextField textField, int value) {
		textField.setText(Integer.toString(value));
	}

	private void setCheckBox(CheckBox checkBox, boolean value) {
		checkBox.setSelected(value);
	}

}