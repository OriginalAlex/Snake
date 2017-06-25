package me.alex.display;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {
	
	private static Main instance;
	private AnchorPane parent;
	private Backend be;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public AnchorPane getParent() {
		return parent;
	}
	
	public static Main getInstance() {
		return instance;
	}
	
	public void stop() {
		be.setRunning(false);
		System.exit(0);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		if (instance != null) {
			throw new UnsupportedOperationException("Must..be..singleton");
		} else {
			instance = this;
		}
		be = new Backend();
		Scene scene = be.initialize();
		this.parent = (AnchorPane) scene.getRoot();
		be.spawnFood();
		be.moveSnake();
		primaryStage.setScene(scene);
		primaryStage.setTitle("Snake!");
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/me/alex/display/icon.png")));
		primaryStage.show();
				
	}
	

}
