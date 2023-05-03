module me.navanda.shutdown_application {
	requires javafx.controls;
	requires javafx.fxml;

	requires com.fasterxml.jackson.databind;
	requires java.sql;

	opens me.navanda.shutdown_application to javafx.fxml;
	exports me.navanda.shutdown_application;
	exports me.navanda.shutdown_application.Model;
	opens me.navanda.shutdown_application.Model to javafx.fxml;
	exports me.navanda.shutdown_application.Services;
	opens me.navanda.shutdown_application.Services to javafx.fxml;
}