module me.navanda.shutdown_application {
	requires javafx.controls;
	requires javafx.fxml;

	requires com.fasterxml.jackson.databind;
	requires java.sql;

	opens me.navanda.shutdown_application to javafx.fxml;
	exports me.navanda.shutdown_application;
}