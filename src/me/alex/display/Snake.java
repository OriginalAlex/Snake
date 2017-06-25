package me.alex.display;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import me.alex.display.Backend.Direction;

public class Snake {
	
	private List<Rectangle> snakelets;
	private Rectangle head, tail;
	private Main main;
	private Backend be;
	
	public Snake(Rectangle start) {
		this.head = start;
		this.tail = start;
		this.be = Backend.getInstance();
		this.main = Main.getInstance();
		snakelets = new ArrayList<Rectangle>();
		snakelets.add(head);
	}
	
	public List<Rectangle> getSnakelets() {
		return this.snakelets;
	}
	
	private boolean collidesWithItself() {
		for (int i = 1; i < snakelets.size(); i++) {
			Rectangle check = snakelets.get(i);
			if (head.getX() == check.getX() && head.getY() == check.getY()) {
				return true;
			}
		}
		return false;
	}
	
	private void incrementLength() {
		Rectangle r = new Rectangle();
		r.setHeight(be.getSnakeHeight());
		r.setWidth(be.getSnakeWidth());
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
			head.setX(main.getParent().getWidth() - 10);
		} else if (head.getX() > main.getParent().getWidth()) {
			head.setX(1);
		} if (head.getY() < 0) {
			head.setY(main.getParent().getHeight() - 10);
		} else if (head.getY() > main.getParent().getHeight()) {
			head.setY(1);
		}
	}
	
	private void gameOverHandling() {
		if (collidesWithItself()) {
			be.updateScore(true);
			Platform.runLater(() -> {
				for (int i = snakelets.size()-1; i > 0; i--) {
					ObservableList<Node> nodes = main.getParent().getChildren();
					Rectangle r = snakelets.remove(i);
					nodes.remove(r);
					tail = head;
				}
			});	
			System.out.println("Game over!");
		}
	}
	
	private void eatFoodHandling() {
		if (head.intersects(be.getFood().getBoundsInLocal())) {
			be.updateScore(false);
			incrementLength();
			Rectangle r = be.getFood();
			Platform.runLater(() -> {
				main.getParent().getChildren().remove(be.getFood());
				be.spawnFood();
			});
			while (main.getParent().getChildren().contains(r)) {}
		}
	}
	
	public void moveSnake(Direction dir, int increase) {
		double lastX = head.getX();
		double lastY = head.getY();
		relocateHead(dir, increase);
		Thread th = new Thread(() -> {
			gameOverHandling(); 
		});
		th.start();
		try {
			th.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		eatFoodHandling();
		if (!main.getParent().getChildren().contains(tail)) {
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
