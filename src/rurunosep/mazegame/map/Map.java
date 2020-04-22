package rurunosep.mazegame.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import rurunosep.mazegame.entity.Entity;
import rurunosep.mazegame.main.Screen;

public class Map {

	public static final int TILE_SIZE = 16;
	private BufferedImage tileset = null;
	private String tilesetName = "";
	protected int[][] tileIDs;
	protected int[][] passabilities;
	protected int numberOfRows;
	protected int numberOfColumns;
	private ArrayList<Entity> entities = new ArrayList<>();
	protected int mapWidth;
	protected int mapHeight;
	private double cameraX = 0;
	private double cameraY = 0;
	private Entity cameraFollowTarget = null;
	private boolean cameraIsBoundToMap = true;
	private BufferedImage viewport;
	private Graphics2D g;
	private Screen screen;
	
	public Map () {
		screen = Screen.getInstance();
	}

	public void loadTileset(String resourcePath) {
		if (tilesetName.equals(resourcePath)) return;
		try {
			tileset = ImageIO.read(getClass().getResourceAsStream(resourcePath));
			tilesetName = resourcePath;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void drawToScreen(int sx, int sy, int w, int h) {
		// Create a new viewport if necessary
		if (viewport == null) {
			viewport = new BufferedImage(w, h, Screen.IMAGE_TYPE);
			g = viewport.createGraphics();
		} else if ((viewport.getWidth() != w) || (viewport.getHeight() != h)) {
			viewport = new BufferedImage(w, h, Screen.IMAGE_TYPE);
			g = viewport.createGraphics();
		}
		
		// Clear viewport
		g.setBackground(new Color(0x00000000));
		g.clearRect(0, 0, viewport.getWidth(), viewport.getHeight());

		if (cameraFollowTarget != null) {
			cameraX = cameraFollowTarget.getX();
			cameraY = cameraFollowTarget.getY();
		}
		
		// If camera is bound to map and anything off map would be shown, adjust the camera postion
		if (cameraIsBoundToMap) {
			if (cameraX - (w/2.0f) < 0) cameraX = 0 + (w/2.0f);
			if (cameraX + (w/2.0f) > mapWidth) cameraX = mapWidth - (w/2.0f);
			if (cameraY - (h/2.0f) < 0) cameraY = 0 + (h/2.0f);
			if (cameraY + (h/2.0f) > mapHeight) cameraY = mapHeight - (h/2.0f);
		}

		// Viewport postion in map
		int viewportX = (int)(cameraX - (w/2));
		int viewportY = (int)(cameraY - (h/2));

		// Draw tiles
		for (int row = 0; row < numberOfRows; row++) {
			for (int column = 0; column < numberOfColumns; column++) {

				// If tile is not within the bounds of the viewport, don't draw
				if ((row * TILE_SIZE < viewportY - TILE_SIZE) ||
						(row * TILE_SIZE > viewportY + viewport.getHeight()) ||
						(column * TILE_SIZE < viewportX - TILE_SIZE) ||
						(column * TILE_SIZE > viewportX + viewport.getWidth())) {
					continue;
				}

				int tileID = tileIDs[row][column];

				if (tileID == 0) continue;

				// Draw tile
				int tileXInViewport = column * TILE_SIZE - viewportX;
				int tileYInViewport = row * TILE_SIZE - viewportY;
				int tileXInTileset = (tileID - 1) % (tileset.getWidth() / TILE_SIZE) * TILE_SIZE;
				int tileYInTileset = (tileID - 1) / (tileset.getWidth() / TILE_SIZE) * TILE_SIZE;
				g.drawImage(tileset, tileXInViewport, tileYInViewport, tileXInViewport + TILE_SIZE, tileYInViewport + TILE_SIZE,
						tileXInTileset, tileYInTileset, tileXInTileset + TILE_SIZE, tileYInTileset + TILE_SIZE, null);

			}
		}

		for (Entity temp : entities) {
			// If entity is not within the bounds of the viewport, don't draw
			if ((temp.getX() < viewportX - temp.getWidth()) ||
					(temp.getX() > viewportX + viewport.getWidth()) ||
					(temp.getY() < viewportY - temp.getHeight()) ||
					(temp.getY() > viewportY + viewport.getHeight())) {
				continue;
			}
			// Draw entity
			int entityXInViewport = (int) (temp.getX() - (temp.getWidth() / 2) - viewportX);
			int entityYInViewport = (int) (temp.getY() - (temp.getHeight() / 2) - viewportY);
			g.drawImage(temp.getSprite(), entityXInViewport, entityYInViewport, null);
		}

		screen.drawToScreen(viewport, sx, sy, 0, 0, w, h);
	}

	public void addEntity(Entity e) {
		e.setMap(this);
		entities.add(e);
	}

	public boolean isTileBlocked(int row, int column) {
		if ((row < 0) || (row >= numberOfRows) || (column < 0) || (column >= numberOfColumns)) {
			return true;
		}
		return passabilities[row][column] == 1;
	}

	public void update() {
		for (Entity entity : entities) {
			entity.update();
		}
	}

	public void setCameraIsBoundToMap(boolean cameraIsBoundToMap) {
		this.cameraIsBoundToMap = cameraIsBoundToMap;
	}

	public int getWidth() {
		return mapWidth;
	}

	public int getHeight() {
		return mapHeight;
	}

	public void setCameraFollowTarget(Entity cameraFollowTarget) {
		this.cameraFollowTarget = cameraFollowTarget;
	}
	
	public int getNumberOfEntities() {
		return entities.size();
	}
	
	public Entity getEntity(int i) {
		return entities.get(i);
	}
}