package rurunosep.mazegame.entity;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import rurunosep.mazegame.main.GamePanel;
import rurunosep.mazegame.main.Screen;
import rurunosep.mazegame.map.Map;

public class Entity {

	public final static int SPRITE_SIZE = 16;
	protected BufferedImage spriteSheet = null;
	protected String spriteSheetName = "";
	protected int spriteFrameID = 0;
	protected BufferedImage sprite = null;
	protected Map map = null;
	protected double x = 0.0;
	protected double y = 0.0;
	protected double moveSpeed = 0.0;
	protected boolean movingUp = false;
	protected boolean movingDown = false;
	protected boolean movingLeft = false;
	protected boolean movingRight = false;
	protected double hitboxWidth = SPRITE_SIZE;
	protected double hitboxHeight = SPRITE_SIZE;
	protected Screen screen;
	protected GamePanel game;


	public Entity() {
		screen = Screen.getInstance();
		game = GamePanel.getInstance();
	}

	public void loadSpriteSheet(String resourcePath) {
		if (spriteSheetName.equals(resourcePath)) return;
		try {
			spriteSheet = ImageIO.read(getClass().getResourceAsStream(resourcePath));
			spriteSheetName = resourcePath;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void update() {

		double destinationX = x;
		double destinationY = y;
		if (movingUp) destinationY -= moveSpeed * game.getDelta();
		if (movingDown) destinationY += moveSpeed * game.getDelta();
		if (movingLeft) destinationX -= moveSpeed * game.getDelta();
		if (movingRight) destinationX += moveSpeed * game.getDelta();

		// Check for collision and adjust destination coordinates until a valid destination is found
		boolean validDestinationFound;
		do {
			validDestinationFound = true;

			double destinationTopSide = destinationY - hitboxHeight / 2;
			double destinationBottomSide = destinationY + hitboxHeight / 2;
			double destinationLeftSide = destinationX - hitboxWidth / 2;
			double destinationRightSide = destinationX + hitboxWidth / 2;

			// Check for collision with tiles
			if (movingUp) {
				int destinationTileRow = (int) Math.floor(destinationTopSide / Map.TILE_SIZE);
				int destinationTileColumn = (int) Math.floor(destinationX / Map.TILE_SIZE);
				if(map.isTileBlocked(destinationTileRow, destinationTileColumn)) {
					validDestinationFound = false;
					destinationY = (destinationTileRow * Map.TILE_SIZE) + Map.TILE_SIZE + (hitboxHeight/2);
				}
			}
			if (movingDown) {
				int destinationTileRow = (int) Math.floor((destinationBottomSide - 1) / Map.TILE_SIZE);
				int destinationTileColumn = (int) Math.floor(destinationX / Map.TILE_SIZE);
				if(map.isTileBlocked(destinationTileRow, destinationTileColumn)) {
					validDestinationFound = false;
					destinationY = (destinationTileRow * Map.TILE_SIZE) - (hitboxHeight/2);
				}
			}
			if (movingLeft) {
				int destinationTileRow = (int) Math.floor(destinationY / Map.TILE_SIZE);
				int destinationTileColumn = (int) Math.floor(destinationLeftSide / Map.TILE_SIZE);
				if(map.isTileBlocked(destinationTileRow, destinationTileColumn)) {
					validDestinationFound = false;
					destinationX = (destinationTileColumn * Map.TILE_SIZE) + Map.TILE_SIZE + (hitboxWidth/2);
				}
			}
			if (movingRight) {
				int destinationTileRow = (int) Math.floor(destinationY / Map.TILE_SIZE);
				int destinationTileColumn = (int) Math.floor((destinationRightSide - 1) / Map.TILE_SIZE);
				if(map.isTileBlocked(destinationTileRow, destinationTileColumn)) {
					validDestinationFound = false;
					destinationX = (destinationTileColumn * Map.TILE_SIZE) - (hitboxWidth/2);
				}
			}

			// Check for collision with entities
			for (int i = 0; i < map.getNumberOfEntities(); i++) {
				Entity temp = map.getEntity(i);
				if (temp ==  this) continue;
				if (movingUp) {
					if ((destinationTopSide < temp.getY() + temp.hitboxHeight/2) && (destinationTopSide > temp.getY() - temp.hitboxHeight/2) &&
							(destinationX < temp.getX() + temp.hitboxWidth/2) && (destinationX > temp.getX() - temp.hitboxWidth/2)) {
						validDestinationFound = false;
						destinationY = temp.getY() + temp.hitboxHeight/2 + this.hitboxHeight/2;
					}
				}
				if (movingDown) {
					if ((destinationBottomSide - 1 < temp.getY() + temp.hitboxHeight/2) && (destinationBottomSide - 1 > temp.getY() - temp.hitboxHeight/2) &&
							(destinationX < temp.getX() + temp.hitboxWidth/2) && (destinationX > temp.getX() - temp.hitboxWidth/2)) {
						validDestinationFound = false;
						destinationY = temp.getY() - temp.hitboxHeight/2 - this.hitboxHeight/2;
					}
				}
				if (movingLeft) {
					if ((destinationLeftSide < temp.getX() + temp.hitboxWidth/2) && (destinationLeftSide > temp.getX() - temp.hitboxWidth/2) &&
							(destinationY < temp.getY() + temp.hitboxHeight/2) && (destinationY > temp.getY() - temp.hitboxHeight/2)) {
						validDestinationFound = false;
						destinationX = temp.getX() + temp.hitboxWidth/2 + this.hitboxWidth/2;
					}
				}
				if (movingRight) {
					if ((destinationRightSide - 1 < temp.getX() + temp.hitboxWidth/2) && (destinationRightSide - 1 > temp.getX() - temp.hitboxWidth/2) &&
							(destinationY < temp.getY() + temp.hitboxHeight/2) && (destinationY > temp.getY() - temp.hitboxHeight/2)) {
						validDestinationFound = false;
						destinationX = temp.getX() - temp.hitboxWidth/2 - this.hitboxWidth/2;
					}
				}
			}
		} while (!validDestinationFound);

		x = destinationX;
		y = destinationY;
	}

	private void updateSprite() {
		sprite = new BufferedImage(SPRITE_SIZE, SPRITE_SIZE, Screen.IMAGE_TYPE);
		int spriteXInSpriteSheet = (spriteFrameID - 1) % (spriteSheet.getWidth() / SPRITE_SIZE) * SPRITE_SIZE;
		int spriteYInSpriteSheet = (spriteFrameID - 1) / (spriteSheet.getWidth() / SPRITE_SIZE) * SPRITE_SIZE;
		sprite.getGraphics().drawImage(spriteSheet, 0, 0, SPRITE_SIZE, SPRITE_SIZE,
				spriteXInSpriteSheet, spriteYInSpriteSheet, spriteXInSpriteSheet + SPRITE_SIZE, spriteYInSpriteSheet + SPRITE_SIZE, null);
	}
	
	public BufferedImage getSprite() {
		return sprite;
	}
	
	public int getWidth() {
		return SPRITE_SIZE;
	}
	
	public int getHeight() {
		return SPRITE_SIZE;
	}

	public void setMap(Map map) {
		this.map = map;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void setSpriteFrameID(int spriteFrameID) {
		this.spriteFrameID = spriteFrameID;
		updateSprite();
	}

	public void setMoveSpeed(double moveSpeed) {
		this.moveSpeed = moveSpeed;
	}

	public void setMovingUp(boolean movingUp) {
		this.movingUp = movingUp;
	}

	public void setMovingDown(boolean movingDown) {
		this.movingDown = movingDown;
	}

	public void setMovingLeft(boolean movingLeft) {
		this.movingLeft = movingLeft;
	}

	public void setMovingRight(boolean movingRight) {
		this.movingRight = movingRight;
	}
	
}
