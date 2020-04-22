package rurunosep.mazegame.scene;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import rurunosep.mazegame.entity.Player;
import rurunosep.mazegame.main.Globals;
import rurunosep.mazegame.main.Jukebox;
import rurunosep.mazegame.map.Map;
import rurunosep.mazegame.map.Maze;

public class SceneMaze extends Scene {

	public enum Difficulty {NORMAL, HYPER, HYPEREST}
	private enum State {SOLVING, PAUSED, TIMEUP}

	private static final String SFX_MAZE_COMPLETED = "/sounds/completed_maze.wav";
	private static final String SFX_MAZE_COMPLETED_5 = "/sounds/completed_maze_5.wav";
	private static final String SFX_COUNTDOWN = "/sounds/countdown.wav";
	private static final String SFX_TIME_UP = "/sounds/timeup.wav";
	private static final String PLAYER_SPRITESHEET = "/graphics/player.png";
	private static final int TIME_LIMIT_NORMAL_MODE = 30; // in seconds
	private static final int TIME_LIMIT_HYPER_MODE = 15;
	private static final int TIME_LIMIT_HYPEREST_MODE = 10;
	private static final double PLAYER_SPEED_NORMAL_MODE = 3.0; // pixels per frame
	private static final double	PLAYER_SPEED_HYPER_MODE = 4.5;
	private static final double PLAYER_SPEED_HYPEREST_MODE = 5.0;
	private static final double PLAYER_CLUTCH_SPEED_MULTIPLIER = 1.3;

	private Maze maze;
	private Player player;
	private int score = 0;
	private int currentMazeWidth;
	private int currentMazeHeight;
	private double defaultPlayerSpeed;
	private boolean showHighScore = false;
	private boolean showNope = false;
	private long showedNopeTime = 0;
	private boolean hyperestMessageVisible = false;
	private long hypersetMessageLastToggled = 0;
	private State state;
	private int timeLimit;
	private int timerLastTime = 0;
	private int timerTimeLeft = 0;
	private int timerNextTickTime = 0;
	private boolean timerVisible;
	private SceneManager sceneManager;
	private Jukebox jukebox;
	private Globals globals;
	
	public SceneMaze() {
		sceneManager = SceneManager.getInstance();
		jukebox = Jukebox.getInstance();
		globals = Globals.getInstance();
	}
	
	@Override
	public void init() {
		// Load sounds
		jukebox.loadSound(SFX_MAZE_COMPLETED);
		jukebox.loadSound(SFX_MAZE_COMPLETED_5);
		jukebox.loadSound(SFX_COUNTDOWN);

		// Generate initial maze
		currentMazeWidth = 5;
		currentMazeHeight = 5;
		maze = new Maze();
		maze.generateMaze(currentMazeWidth, currentMazeHeight);

		// Initialize player
		player = new Player();
		player.loadSpriteSheet(PLAYER_SPRITESHEET);
		player.setSpriteFrameID(7);
		maze.addEntity(player);
		maze.setCameraFollowTarget(player);
		maze.setCameraIsBoundToMap(true);
		player.setX(maze.getPlayerStartColumn() * Map.TILE_SIZE + (player.getWidth()/2.0f));
		player.setY(maze.getPlayerStartRow() * Map.TILE_SIZE + (player.getHeight()/2.0f));

		// Set default player speed and time limit according to play mode
		switch (globals.difficulty) {
		case NORMAL:
			defaultPlayerSpeed = PLAYER_SPEED_NORMAL_MODE;
			timeLimit = TIME_LIMIT_NORMAL_MODE;
			break;
		case HYPER:
			defaultPlayerSpeed = PLAYER_SPEED_HYPER_MODE;
			timeLimit = TIME_LIMIT_HYPER_MODE;
			break;
		case HYPEREST:
			defaultPlayerSpeed = PLAYER_SPEED_HYPEREST_MODE;
			timeLimit = TIME_LIMIT_HYPEREST_MODE;
			break;
		}
		// Start timer
		timerTimeLeft = timeLimit * 1000;
		timerLastTime = (int)System.currentTimeMillis();
		timerNextTickTime = 10 * 1000; // 10 seconds
		timerVisible = true;

		state = State.SOLVING;
	}
	
	@Override
	public void update() {
		maze.update();
		if (state == State.SOLVING) {
			updateSolving();
		}
	}

	public void updateSolving() {

		player.setMovingUp(inputHandler.getKeyState(KeyEvent.VK_UP));
		player.setMovingDown(inputHandler.getKeyState(KeyEvent.VK_DOWN));
		player.setMovingLeft(inputHandler.getKeyState(KeyEvent.VK_LEFT));
		player.setMovingRight(inputHandler.getKeyState(KeyEvent.VK_RIGHT));

		// Check if player has reached end of maze
		int playerRow = (int)player.getY() / Map.TILE_SIZE;
		int playerColumn = (int)player.getX() / Map.TILE_SIZE;
		if ((playerRow == maze.getEndTileRow()) && (playerColumn == maze.getEndTileColumn())) {
			score++;
			if ((score % 5) == 0) {
				jukebox.playSound(SFX_MAZE_COMPLETED_5);
			} else {
				jukebox.playSound(SFX_MAZE_COMPLETED);
			}
			// Generate new maze
			if (currentMazeWidth == currentMazeHeight) {
				currentMazeWidth += 2;
			} else {
				currentMazeHeight += 2;
			}
			maze.generateMaze(currentMazeWidth, currentMazeHeight);
			player.setX(maze.getPlayerStartColumn() * Map.TILE_SIZE + (player.getWidth()/2.0f));
			player.setY(maze.getPlayerStartRow() * Map.TILE_SIZE + (player.getHeight()/2.0f));
			// Reset timer
			timerTimeLeft = timeLimit * 1000;
			timerLastTime = (int)System.currentTimeMillis();
			timerNextTickTime = 10 * 1000; // 10 seconds
			timerVisible = true;
		}

		// Update timer
		int elapsed = (int) (System.currentTimeMillis() - timerLastTime);
		timerLastTime = (int)System.currentTimeMillis();
		timerTimeLeft -= elapsed;
		if (timerTimeLeft <= 0) timerTimeLeft = 0;

		// Play timer tick sfx
		if (timerTimeLeft < timerNextTickTime) {
			jukebox.playSound(SFX_COUNTDOWN);
			timerNextTickTime -= 1000;
		}

		// Set player speed according to time left
		if (timerTimeLeft < 10 * 1000) {
			player.setMoveSpeed(defaultPlayerSpeed * PLAYER_CLUTCH_SPEED_MULTIPLIER);
		} else {
			player.setMoveSpeed(defaultPlayerSpeed);
		}

		if (timerTimeLeft <= 0) {
			timeup();
		}
	}

	private void timeup() {
		player.setMovingUp(false);
		player.setMovingDown(false);
		player.setMovingLeft(false);
		player.setMovingRight(false);
		state = State.TIMEUP;
		jukebox.playSound(SFX_TIME_UP);
		// Assign high score and display highscore message
		if (globals.difficulty == Difficulty.HYPER && score > globals.hyperModeHighScore) {
			showHighScore = true;
			globals.hyperModeHighScore = score;
		} else if (globals.difficulty == Difficulty.NORMAL && score > globals.normalModeHighScore){
			showHighScore = true;
			globals.normalModeHighScore = score;
		}
		// Pause player animation
		player.setAnimationPaused(true);
	}

	@Override
	public void render() {
		Graphics2D g = screen.createGraphics();

		// Draw maze
		int x, y, w, h;
		if (maze.getWidth() < screen.getWidth() - 16) {
			x = screen.getWidth()/2 - maze.getWidth()/2;
			w = maze.getWidth();
		} else {
			x = 8;
			w = screen.getWidth() - 16;
		}
		if (maze.getHeight() < screen.getHeight() - 16) {
			y = screen.getHeight()/2 - maze.getHeight()/2;
			h = maze.getHeight();
		} else {
			y = 8;
			h = screen.getHeight() - 16;
		}
		maze.drawToScreen(x, y, w, h);

		g.setColor(Color.WHITE);
		g.drawString("Score: " + score, 0, 10);

		if (timerVisible) drawTimer();

		// Draw nope message
		if (showNope) {
			g.setColor(Color.RED);
			g.drawString("NOPE", 63, screen.getHeight()/2 + 5);
			if (System.currentTimeMillis() - showedNopeTime > 1000 * 0.2) showNope = false;
		}

		if (state == State.PAUSED) {
			renderPaused(g);
		} else if (state == State.TIMEUP) {
			renderTimeup(g);
		}

		g.dispose();
	}

	public void renderTimeup (Graphics g) {
		// Draw high score announcement
		if (showHighScore) {
			g.setColor(Color.YELLOW);
			g.drawString("HIGH SCORE", 42, screen.getHeight()/2 + 5);
		}
		// Draw enter message
		g.setColor(Color.WHITE);
		g.drawString("Press ENTER to quit", 24, 25);
		// Draw hyperest announcement
		if (globals.difficulty == Difficulty.HYPEREST) {
			if (hyperestMessageVisible) {
				g.setColor(Color.YELLOW);
				g.drawString("HYPEREST", 48, screen.getHeight()/2 + 5);
			}
			// Toggle announcement visibility (flash)
			if (System.currentTimeMillis() - hypersetMessageLastToggled > 1000 * 0.1) {
				hyperestMessageVisible = !hyperestMessageVisible;
				hypersetMessageLastToggled = System.currentTimeMillis();
			}
		}
	}

	private void renderPaused (Graphics g) {
		g.setColor(Color.WHITE);
		g.drawString("Press ENTER to quit", 24, 25);
		g.setColor(Color.RED);
		g.drawString("PAUSED", 56, screen.getHeight()/2 + 5);
	}

	private void drawTimer() {
		// Generate timer string
		String centisecondsString = "" + timerTimeLeft / 10 % 100;
		String secondsString = "" + timerTimeLeft / 1000 % 60;
		String minutesString = "" + timerTimeLeft / 1000 / 60;
		if (centisecondsString.length() < 2) centisecondsString = "0" + centisecondsString;
		if (secondsString.length() < 2) secondsString = "0" + secondsString;
		String timerString = minutesString + ":" + secondsString + "." + centisecondsString;
		// Set timer color depending on time left
		Graphics2D  g = screen.createGraphics();
		if (timerTimeLeft > 10 * 1000) {
			g.setColor(Color.WHITE);
		} else {
			g.setColor(Color.RED);
		}
		g.drawString(timerString, 0, screen.getHeight());
		g.dispose();
	}

	@Override
	public void escapePressed() {
		if (state == State.SOLVING) {
			if (globals.difficulty == Difficulty.HYPEREST) {
				showNope = true;
				showedNopeTime = System.currentTimeMillis();
			} else {
				state = State.PAUSED;
			}
		}
		else if (state == State.PAUSED) {
			timerLastTime = (int)System.currentTimeMillis();
			player.setAnimationPaused(false);
			state = State.SOLVING;
		}
	}
	
	@Override
	public void enterPressed() {
		if (state == State.PAUSED | state == State.TIMEUP) {
			sceneManager.setScene(SceneTitle.class);
		}
	}

	@Override
	public void exit() {
	}
}
