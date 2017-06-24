package me.alex.display;

import java.util.List;
import java.util.Random;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {
	
	private static Main instance;
	private AnchorPane parent;
	private Random rand;
	private Snake snake;
	private Rectangle food;
	private int score = 0;
	private long delay = 0;
	private Text scoreKeeper;
	private final int xSnakeDimension = 15;
	private final int ySnakeDimension = 15;
	private final int snakeIncrease = 15;
	private volatile Direction dir;
	private volatile boolean running;
	
	public enum Direction {
		STATIONARY, UP, DOWN, LEFT, RIGHT;
	}
	
	public Rectangle getFood() {
		return this.food;
	}
	
	public static Main getInstance() {
		return instance;
	}
	
	public AnchorPane getParent() {
		return this.parent;
	}
	
	public int getSnakeWidth() {
		return this.xSnakeDimension;
	}
	
	public int getSnakeHeight() {
		return this.ySnakeDimension;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	private boolean validSpawnForFood(int x, int y) {
		List<Rectangle> snakelets = snake.getSnakelets();
		for (Rectangle r : snakelets) {
			if (r.intersects(x, y, xSnakeDimension, ySnakeDimension)
				|| r.intersects(x, y, -xSnakeDimension, ySnakeDimension)
				|| r.intersects(x, y, xSnakeDimension, -ySnakeDimension)
				|| r.intersects(x, y, -xSnakeDimension, -ySnakeDimension)) {
				return false;
			} 
		}
		return (x > 20 && x < parent.getWidth() - 20 && y > 20 && y < parent.getHeight() - 20);
	}
	
	public void generateRectangles() {
		Rectangle r = new Rectangle();
		r.setFill(Color.RED);
		r.setHeight(ySnakeDimension);
		r.setWidth(xSnakeDimension);
		parent.getChildren().add(r);
		double width = parent.getWidth(), height = parent.getHeight();
		int randX = rand.nextInt((int) width);
		int randY = rand.nextInt((int) height);
		while (!validSpawnForFood(randX, randY)) {
			randX = rand.nextInt((int) width);
			randY = rand.nextInt((int) height);
		}			
		r.setX(rand.nextInt(randX));
		r.setY(rand.nextInt(randY));
		this.food = r;
	}
	
	public void updateScore(boolean hasLost) {
		if (!hasLost) {
			score++;
		} else {
			score = 0;
		}
		this.scoreKeeper.setText("Score: " + score);
	}
	
	private void moveSnake() {
		Thread th = new Thread(() -> {
			while (running) {
				snake.moveSnake(dir, snakeIncrease);
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		th.start();
	}
	
	public void stop() {
		running = false;
	}
	                     
	
	private void initialize() {
		this.dir = Direction.STATIONARY;
		this.rand = new Random();
		this.running = true;
		Rectangle rec = new Rectangle();
		rec.setHeight(xSnakeDimension);
		rec.setWidth(ySnakeDimension);
		rec.setFill(Color.BLUE);
		AnchorPane ap = new AnchorPane();
		ap.getChildren().addAll(rec);
		
		Text t = new Text("Score: 0");
		t.setFill(Color.GREEN);
		t.setFont(Font.font("Sans-serif", FontWeight.BOLD, 12));
		t.setX(10);
		t.setY(390);
		t.setVisible(true);
		ap.getChildren().add(t);
		
		this.scoreKeeper = t;
		this.parent = ap;
		this.snake = new Snake(rec);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		if (instance != null) {
			throw new UnsupportedOperationException("Must..be..singleton");
		} else {
			instance = this;
		}
		initialize();
		Scene scene = new Scene(parent, 400, 400);
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent ke) {
				if (System.currentTimeMillis() - delay > 20) {
					switch(ke.getCode()) {
					case RIGHT: if (dir != Direction.LEFT) { dir = Direction.RIGHT;} break;
					case LEFT: if (dir != Direction.RIGHT) { dir = Direction.LEFT;} break;
					case UP: if (dir != Direction.DOWN) {dir = Direction.UP;} break;
					case DOWN: if (dir != Direction.UP) {dir = Direction.DOWN;} break;
					}
				}
				delay = System.currentTimeMillis();
			}
		});
		generateRectangles();
		moveSnake();
		primaryStage.setScene(scene);
		primaryStage.setTitle("Snake!");
		primaryStage.show();
				
	}
	

}
