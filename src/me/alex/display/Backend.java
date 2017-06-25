package me.alex.display;

import java.util.List;
import java.util.Random;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class Backend {
	
	private static Backend instance;
	private AnchorPane parent;
	private Random rand;
	private Snake snake;
	private Rectangle food;
	private int score = 0;
	private int highscore = 0;
	private long delay = 0;
	private Text highscoreKeeper;
	private Text scoreKeeper;
	private final int xSnakeDimension = 15;
	private final int ySnakeDimension = 15;
	private final int snakeIncrease = 15;
	private volatile Direction dir;
	private volatile boolean running;
	
	public Backend() {
		if (instance == null) {
			instance = this;
		} else {
			throw new UnsupportedOperationException("why can't i be a singleton");
		}
	}
	
	public static Backend getInstance() {
		return instance;
	}
	
	public enum Direction {
		STATIONARY, UP, DOWN, LEFT, RIGHT;
	}
	
	public Rectangle getFood() {
		return this.food;
	}
	
	public int getSnakeWidth() {
		return this.xSnakeDimension;
	}
	
	public int getSnakeHeight() {
		return this.ySnakeDimension;
	}
	
	private boolean validSpawnForFood(int x, int y) {
		List<Rectangle> snakelets = snake.getSnakelets();
		for (Rectangle r : snakelets) { // Ensure that the food does not touch any of the rectangles.
			if (r.intersects(x, y, xSnakeDimension, ySnakeDimension)
				|| r.intersects(x - xSnakeDimension, y, xSnakeDimension, ySnakeDimension)
				|| r.intersects(x, y - ySnakeDimension, xSnakeDimension, ySnakeDimension)
				|| r.intersects(x - xSnakeDimension, y - ySnakeDimension, xSnakeDimension, ySnakeDimension)) {
				return false;
			} 
		}
		return (x > 20 && x < parent.getWidth() - 20 && y > 20 && y < parent.getHeight() - 20);
	}
	
	public void setRunning(boolean value) {
		this.running = value;
	}
	
	public void spawnFood() {
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
			if (score > highscore) {
				highscoreKeeper.setText("Highscore: " + score);
				highscore = score;
			}
		} else {
			score = 0;
		}
		Platform.runLater(() -> {
			this.scoreKeeper.setText("Score: " + score);
		});
	}
	
	public void moveSnake() {
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
	
	private Scene initScene(Parent root) {
		Scene scene = new Scene(root, 400, 400);
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent ke) {
				if (System.currentTimeMillis() - delay > 20) {
					switch(ke.getCode()) {
					case RIGHT: if (dir != Direction.LEFT) { dir = Direction.RIGHT;} break;
					case LEFT: if (dir != Direction.RIGHT) { dir = Direction.LEFT;} break;
					case UP: if (dir != Direction.DOWN) {dir = Direction.UP;} break;
					case DOWN: if (dir != Direction.UP) {dir = Direction.DOWN;} break;
					default:
					}
				}
				delay = System.currentTimeMillis();
			}
		});
		return scene;
	}
	
	private void styleText(Text t) {
		t.setFill(Color.GREEN);
		t.setFont(Font.font("Sans-serif", FontWeight.BOLD, 12));
		
	}
	
	public Scene initialize() {
		this.dir = Direction.STATIONARY;
		this.rand = new Random();
		this.running = true;
		Rectangle rec = new Rectangle();
		rec.setHeight(xSnakeDimension);
		rec.setWidth(ySnakeDimension);
		rec.setFill(Color.BLUE);
		rec.setX(185);
		rec.setY(185);
		AnchorPane ap = new AnchorPane();
		
		Text highscore = new Text("Highscore: 0");
		styleText(highscore);
		highscore.setX(320);
		highscore.setY(390);
		
		Text t = new Text("Score: 0");
		styleText(t);
		t.setX(10);
		t.setY(390);
		ap.getChildren().addAll(highscore, t, rec);
		
		this.scoreKeeper = t;
		this.parent = ap;
		this.highscoreKeeper = highscore;
		this.snake = new Snake(rec);
		return initScene(ap);
	}

}
