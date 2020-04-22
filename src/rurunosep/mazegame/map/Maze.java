package rurunosep.mazegame.map;

import java.util.ArrayList;
import java.util.Random;

public class Maze extends Map {

	public enum MazeElement { GROUND, WALL, START, END}

	public static final int WALL_TILE_ID_C = 16;
	public static final int WALL_TILE_ID_NWES = 5;
	public static final int WALL_TILE_ID_N = 10;
	public static final int WALL_TILE_ID_NW = 9;
	public static final int WALL_TILE_ID_NWE = 3;
	public static final int WALL_TILE_ID_NWS = 7;
	public static final int WALL_TILE_ID_NE = 8;
	public static final int WALL_TILE_ID_NES = 6;
	public static final int WALL_TILE_ID_NS = 2;
	public static final int WALL_TILE_ID_E = 14;
	public static final int WALL_TILE_ID_EW = 1;
	public static final int WALL_TILE_ID_EWS = 4;
	public static final int WALL_TILE_ID_ES = 22;
	public static final int WALL_TILE_ID_W = 15;
	public static final int WALL_TILE_ID_WS = 24;
	public static final int WALL_TILE_ID_S = 23;
	public static final int GROUND_TILE_ID = 12;
	public static final int GROUND_TILE_ID_DEC1 = 65;
	public static final int GROUND_TILE_ID_DEC2 = 66;
	public static final int END_TILE_ID = 13;
	
	private static final String TILESET_NAME = "/graphics/tileset.png";

	private MazeElement[][] mazeDef;
	private int mazeWidth = 0;
	private int mazeHeight = 0;
	private int playerStartRow = 0;
	private int playerStartColumn = 0;
	private int endTileRow = 0;
	private int endTileColumn = 0;
	private Random rand = new Random();
	private ArrayList<Integer> deadEndXs;
	private ArrayList<Integer> deadEndYs;

	public Maze() {
	}

	public void generateMaze(int mazeWidth, int mazeHeight) {

		int endingCellX = 0;
		int endingCellY = 0;
		deadEndXs = new ArrayList<>();
		deadEndYs = new ArrayList<>();
		
		// Initialize mazeDef array with all walls
		this.mazeWidth = mazeWidth;
		this.mazeHeight = mazeHeight;
		mazeDef = new MazeElement[mazeHeight][mazeWidth];
		for (int i = 0; i < mazeHeight * mazeWidth; i++) {
			mazeDef[i / mazeWidth][i % mazeWidth] = MazeElement.WALL;
		}

		int startingCellX = rand.nextInt(mazeWidth / 2) * 2 + 1;
		int startingCellY = rand.nextInt(mazeHeight / 2) * 2 + 1;

		recursiveBacktrackingStep(startingCellX, startingCellY);

		// Chose a random dead end as the maze ending
		boolean found = false;
		while(!found) {
			int endingCellNumber = rand.nextInt(deadEndXs.size());
			endingCellX = deadEndXs.get(endingCellNumber);
			endingCellY = deadEndYs.get(endingCellNumber);
			if ((endingCellX != startingCellX) || (endingCellY != startingCellY)) {
				found = true;
			}
		}

		mazeDef[startingCellY][startingCellX] = MazeElement.START;
		mazeDef[endingCellY][endingCellX] = MazeElement.END;

		super.loadTileset(TILESET_NAME);
		buildMazeMap();
	}

	private void recursiveBacktrackingStep(int currentCellX, int currentCellY) {
		final int NORTH = 0;
		final int SOUTH = 1;
		final int WEST = 2;
		final int EAST = 3;

		int nextCellX = 0;
		int nextCellY = 0;
		int pathX = 0;
		int pathY = 0;
		
		// Dig out current cell (this also marks it as visited)
		mazeDef[currentCellY][currentCellX] = MazeElement.GROUND;

		ArrayList<Integer> unvisitedNeighborCells = new ArrayList<>();
		if (currentCellY >= 3 && mazeDef[currentCellY - 2][currentCellX] == MazeElement.WALL) {
			unvisitedNeighborCells.add(NORTH);
		}
		if (currentCellX >= 3 && mazeDef[currentCellY][currentCellX - 2] == MazeElement.WALL) {
			unvisitedNeighborCells.add(WEST);
		}
		if (currentCellY <= mazeHeight - 4 && mazeDef[currentCellY + 2][currentCellX] == MazeElement.WALL) {
			unvisitedNeighborCells.add(SOUTH);
		}
		if (currentCellX <= mazeWidth - 4 && mazeDef[currentCellY][currentCellX + 2] == MazeElement.WALL) {
			unvisitedNeighborCells.add(EAST);
		}

		while (unvisitedNeighborCells.size() > 0) {
			// Choose a random unvisited neighbor cell
			int nextCell = unvisitedNeighborCells.get(rand.nextInt(unvisitedNeighborCells.size()));
			switch (nextCell) {
			case NORTH:
				nextCellX = currentCellX;
				nextCellY = currentCellY-2;
				pathX = currentCellX;
				pathY = currentCellY-1;
				break;
			case SOUTH:
				nextCellX = currentCellX;
				nextCellY = currentCellY+2;
				pathX = currentCellX;
				pathY = currentCellY+1;
				break;
			case WEST:
				nextCellX = currentCellX-2;
				nextCellY = currentCellY;
				pathX = currentCellX-1;
				pathY = currentCellY;
				break;
			case EAST:
				nextCellX = currentCellX+2;
				nextCellY = currentCellY;
				pathX = currentCellX+1;
				pathY = currentCellY;
				break;
			}
			// Make sure that chosen neighbor is still unvisited
			if (mazeDef[nextCellY][nextCellX] == MazeElement.WALL) {
				mazeDef[pathY][pathX] = MazeElement.GROUND;
				recursiveBacktrackingStep(nextCellX, nextCellY);
			}

			unvisitedNeighborCells.remove((Object)nextCell); // It works
		}
		
		// Determine if current cell is a dead end
		int numberOfOpenSides = 0;
		if (mazeDef[currentCellY][currentCellX+1] == MazeElement.GROUND) numberOfOpenSides++;
		if (mazeDef[currentCellY][currentCellX-1] == MazeElement.GROUND) numberOfOpenSides++;
		if (mazeDef[currentCellY+1][currentCellX] == MazeElement.GROUND) numberOfOpenSides++;
		if (mazeDef[currentCellY-1][currentCellX] == MazeElement.GROUND) numberOfOpenSides++;
		if (numberOfOpenSides == 1) {
			deadEndXs.add(currentCellX);
			deadEndYs.add(currentCellY);
		}
		
	}

	private void buildMazeMap() {
		super.numberOfRows = mazeDef.length;
		super.numberOfColumns = mazeDef[0].length;
		super.mapWidth = numberOfColumns * TILE_SIZE;
		super.mapHeight = numberOfRows * TILE_SIZE;

		tileIDs = new int[numberOfRows][numberOfColumns];
		passabilities = new int[numberOfRows][numberOfColumns];

		for (int i = 0; i < mazeDef.length; i++) {
			for (int j = 0; j < mazeDef[i].length; j++) {
				switch (mazeDef[i][j]) {

					case GROUND:
						// Chose a ground tile at random, for decorative purposes
						int tileType;
						int randInt = rand.nextInt(100);
						if (randInt < 17) {
							tileType = GROUND_TILE_ID_DEC1;
						} else if (randInt < 20) {
							tileType = GROUND_TILE_ID_DEC2;
						} else {
							tileType = GROUND_TILE_ID;
						}
						tileIDs[i][j] = tileType;
						passabilities[i][j] = 0;
						break;

					case WALL:
						// Check for adjacent walls
						boolean north = false;
						boolean south = false;
						boolean west = false;
						boolean east = false;
						if (i > 0 && mazeDef[i - 1][j] == MazeElement.WALL) north = true;
						if (j > 0 && mazeDef[i][j - 1] == MazeElement.WALL) west = true;
						if (i < mazeDef.length - 1 && mazeDef[i + 1][j] == MazeElement.WALL) south = true;
						if (j < mazeDef[0].length - 1 && mazeDef[i][j + 1] == MazeElement.WALL) east = true;
						// Determine tile
						int tileID = 0;
						if (north && south && west && east) tileID = WALL_TILE_ID_NWES;
						if (north && !south && !west && !east) tileID = WALL_TILE_ID_N;
						if (north && !south && west && !east) tileID = WALL_TILE_ID_NW;
						if (north && !south && west && east) tileID = WALL_TILE_ID_NWE;
						if (north && south && west && !east) tileID = WALL_TILE_ID_NWS;
						if (north && !south && !west && east) tileID = WALL_TILE_ID_NE;
						if (north && south && !west && east) tileID = WALL_TILE_ID_NES;
						if (north && south && !west && !east) tileID = WALL_TILE_ID_NS;
						if (!north && !south && !west && east) tileID = WALL_TILE_ID_E;
						if (!north && !south && west && east) tileID = WALL_TILE_ID_EW;
						if (!north && south && west && east) tileID = WALL_TILE_ID_EWS;
						if (!north && south && !west && east) tileID = WALL_TILE_ID_ES;
						if (!north && !south && west && !east) tileID = WALL_TILE_ID_W;
						if (!north && south && west && !east) tileID = WALL_TILE_ID_WS;
						if (!north && south && !west && !east) tileID = WALL_TILE_ID_S;
						if (!north && !south && !west && !east) tileID = WALL_TILE_ID_C;
						// Set tile to impassable wall tile
						tileIDs[i][j] = tileID;
						passabilities[i][j] = 1;
						break;

					case START:
						playerStartRow = i;
						playerStartColumn = j;
						tileIDs[i][j] = GROUND_TILE_ID;
						passabilities[i][j] = 0;
						break;

					case END:
						endTileRow = i;
						endTileColumn = j;
						tileIDs[i][j] = END_TILE_ID;
						passabilities[i][j] = 0;
						break;
				}
			}
		}
	}
	
	public int getPlayerStartRow() {
		return playerStartRow;
	}

	public int getPlayerStartColumn() {
		return playerStartColumn;
	}

	public int getEndTileRow() {
		return endTileRow;
	}
	
	public int getEndTileColumn() {
		return endTileColumn;
	}
}
