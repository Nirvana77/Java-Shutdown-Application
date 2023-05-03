package me.navanda.shutdown_application.Model;

import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.IOException;

public class Shutdown implements Runnable {
	private boolean willExit;
	private final Schedule schedule;
	private Callback callback;

	public Shutdown(Schedule schedule, Callback callback) {
		willExit = false;
		this.schedule = schedule;
		this.callback = callback;
	}

	public Shutdown(Shutdown shutdown) {
		this.schedule = shutdown.schedule;
		this.willExit = shutdown.willExit;
	}

	/**
	 * Runs this operation.
	 */
	@Override
	public void run() {
		while (schedule.isWillDelay() && !willExit) {
			callback.callback("LOG,Shutdown awaits");
			try {
				Thread.sleep(1000 * 10);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		if (willExit) {
			callback.callback("ERROR,Shutdown interrupted");
			return;
		}
		showAlert("Warning", "Computer will shutdown in 1 minute", Alert.AlertType.WARNING);
		try {
			Process process = Runtime.getRuntime().exec("shutdown /s /f /t 6000");
			process.waitFor();
		} catch (InterruptedException e) {
			try {
				Runtime.getRuntime().exec("shutdown /a");
				schedule.setWillShutdown(false);
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		callback.callback("LOG,Shutdown!");

	}

	public static void showAlert(String title, String message, Alert.AlertType type) {
		Platform.runLater(() -> {
			Alert alert = new Alert(type);
			alert.setTitle(title);
			alert.setHeaderText(null);
			alert.setContentText(message);
			alert.showAndWait();
		});
	}

	public void exitThread() {
		willExit = true;
	}
}
