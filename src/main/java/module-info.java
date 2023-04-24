module me.navanda.shutdown_application {
	requires javafx.controls;
	requires javafx.fxml;

	requires org.kordamp.ikonli.javafx;

	opens me.navanda.shutdown_application to javafx.fxml;
	exports me.navanda.shutdown_application;
}