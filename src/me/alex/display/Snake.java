package me.alex.display;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import me.alex.display.Main.Direction;

public class Snake {
	
	private List<Rectangle> snakelets;
	private Rectangle head, tail;
	private Main main;
	
	public Snake(Rectangle start) {
		this.head = start;
		this.tail = start;
		this.main = Main.getInstance();
		snakelets = new ArrayList<Rectangle>();
		snakelets.add(head);
	}
	
	public List<Rectangle> getSnakelets() {
		return this.snakelets;
	}
	
	private boolean collisionCheck() {
		for (int i = 1; i < snakelets.size(); i++) {
			Rectangle check = snakelets.get(i);
			if (head.getX() == check.getX() && head.getY() == check.getY()) {
				return true;
			}
		}
		return false;
	}
	
	private void incLength() {
		Rectangle r = new Rectangle();
		r.setHeight(main.getSnakeHeight());
		r.setWidth(main.getSnakeWidth());
		r.setFill(Color.BLUE);
		snakelets.add(r);
		tail = r;
	}
	
	private void relocateHead(Direction dir, int increase) {	
		switch (dir) {
		case STATIONARY: return;
		case UP: head.setY(head.getY() - increase); break;
		case DOWN: head.setY(head.getY() + increase); break;
		case RIGHT: head.setX(head.getX() + increase); break;
		case LEFT: head.setX(head.getX() - increase); break;
		}
		
		if (head.getX() < 0) {
			head.setX(main.getParent().getWidth());
		} else if (head.getX() > main.getParent().getWidth()) {
			head.setX(0);
		} if (head.getY() < 0) {
			head.setY(main.getParent().getHeight());
		} else if (head.getY() > main.getParent().getHeight()) {
			head.setY(0);
		}
	}
	
	private void gameOverHandling() {
		if (collisionCheck()) {
			main.updateScore(true);
			Platform.runLater(() -> {
				for (int i = snakelets.size()-1; i > 0; i--) {
					ObservableList<Node> nodes = main.getParent().getChildren();
					Rectangle r = snakelets.remove(i);
					nodes.remove(r);
				}
			});	
			System.out.println("Game over!");
		}
	}
	
	private void eatFoodHandling() {
		if (head.intersects(main.getFood().getBoundsInLocal())) {
			main.updateScore(false);
			incLength();
			Platform.runLater(() -> {
				main.getParent().getChildren().remove(main.getFood());
				main.generateRectangles();
			});
			while (main.getParent().getChildren().contains(main.getFood())) {}
		}
	}
	
	public void moveSnake(Direction dir, int increase) {
		double lastX = head.getX(), lastY = head.getY();
		relocateHead(dir, increase);
		Thread th = new Thread(() -> {
		gameOverHandling(); });
		th.start();
		try {
			th.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		eatFoodHandling();
		if (snakelets.size() != 1 && !main.getParent().getChildren().contains(tail)) {
			Platform.runLater(() -> {
				main.getParent().getChildren().add(tail);
			});
		}
		
		for (int i = 1; i < snakelets.size(); i++) { // Shift all rectangles to the place of the rectangle in front of it (before it was changed)
			Rectangle snakelet = snakelets.get(i);
			double tempX = snakelet.getX(), tempY = snakelet.getY();
			snakelet.setX(lastX);
			snakelet.setY(lastY);
			lastX = tempX;
			lastY = tempY;
		}
		
	}

}
