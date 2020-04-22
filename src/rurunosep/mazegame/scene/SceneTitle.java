package rurunosep.mazegame.scene;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import rurunosep.mazegame.main.GamePanel;
import rurunosep.mazegame.main.Globals;
import rurunosep.mazegame.main.Jukebox;

public class SceneTitle extends Scene {

	private enum MenuOrCredits {MENU, CREDITS}

	private static final String BGM = "/sounds/bgm.wav";
	private static final String SFX_MOVE_CURSOR = "/sounds/selection.wav";
	private static final String SFX_SELECT_OPTION = "/sounds/completed_maze.wav";
	private static final String TITLE_BG_SPRITE = "/graphics/title_bg.png";
	private static final String CURSOR_SPRITE = "/graphics/cursor.png";
	private static final int CURSOR_ANIM_DELAY = (int)(1000 * 0.157);
	private static final int CURSOR_NUM_FRAMES = 4;

	private MenuOrCredits menuOrCredits;
	private BufferedImage background;
	private BufferedImage cursor;
	private int numberOfOptions = 4;
	private int menuCursorPosition = 0;
	private boolean hyperModeUnlocked = false;
	private boolean hyperestModeUnlocked = false;
	private int cursorFrame = 0;
	private long cursorLastTime = 0;
	private SceneManager sceneManager;
	private GamePanel game;
	private Jukebox jukebox;
	private Globals globals;
	
	public SceneTitle() {
		sceneManager = SceneManager.getInstance();
		game = GamePanel.getInstance();
		jukebox = Jukebox.getInstance();
		globals = Globals.getInstance();
	}
	
	@Override
	public void init() {
		// Determine if hyper or hyperest modes are unlocked
		if (globals.normalModeHighScore >= 20) {
			hyperModeUnlocked = true;
		}
		if (globals.hyperModeHighScore >= 10) {
			hyperestModeUnlocked = true;
			numberOfOptions++;
		}
		
		// Load sprites
		try {
			background = ImageIO.read(getClass().getResourceAsStream(TITLE_BG_SPRITE));
			cursor = ImageIO.read(getClass().getResourceAsStream(CURSOR_SPRITE));
		} catch (Exception e) {
			e.printStackTrace();
		}

		jukebox.loopSound(BGM);
		menuOrCredits = MenuOrCredits.MENU;
	}

	@Override
	public void update() {
		// Update cursor animation
		if (System.currentTimeMillis() - cursorLastTime > CURSOR_ANIM_DELAY) {
			cursorLastTime = System.currentTimeMillis();
			cursorFrame++;
			if(cursorFrame >= CURSOR_NUM_FRAMES) cursorFrame = 0;
		}
	}

	@Override
	public void render() {
		Graphics g = screen.getGraphics();
		screen.drawToScreen(background, 0, 0);
		switch (menuOrCredits) {
			case MENU:
				renderMenu(g);
				break;
			case CREDITS:
				renderCredits(g);
				break;
		}
		g.dispose();
	}

	private void renderMenu (Graphics g) {
		// Draw menu options
		Color enabledOptionColor = new Color(255, 255, 150);
		g.setColor(enabledOptionColor);
		g.drawString("NORMAL MODE", 36, 65);
		g.drawString("CREDITS", 52, 95);
		g.drawString("QUIT", 63, 110);
		if(hyperModeUnlocked) g.setColor(enabledOptionColor);
		else g.setColor(new Color(120, 120, 100));
		g.drawString("HYPER MODE", 41, 80);
		if(hyperestModeUnlocked) {
			g.setColor(enabledOptionColor);
			g.drawString("???", 67, 125);
		}

		// Draw help text
		g.setColor(Color.WHITE);
		switch (menuCursorPosition) {
			case 0: // Normal mode
				g.drawString("Normal mode high score: " + globals.normalModeHighScore, 0, 10);
				break;
			case 1: // Hyper mode
				if (hyperModeUnlocked) {
					g.drawString("Hyper mode high score: " + globals.hyperModeHighScore, 0, 10);
				} else {
					g.drawString("20 points in normal to unlock", 0, 10);
				}
				break;
			case 2: // Credits
				g.drawString("Who made it?", 0, 10);
				break;
			case 3: // Quit
				g.drawString("Quit?  :(", 0, 10);
				break;
			case 4: // Hyperest mode
				g.drawString("Super Secret Hyperest Mode", 0, 10);
				break;
		}

		// Draw cursor
		int cursorX = 20;
		int cursorY = 53 + menuCursorPosition * 15;
		screen.drawToScreen(cursor, cursorX, cursorY, cursorFrame * 16, 0, 16, 16);
	}

	private void renderCredits (Graphics g) {
		g.setColor(new Color(0xFFFFFF));
		g.drawString("Press ENTER to return", 18, 10);
		g.setColor(new Color(255, 255, 150));
		g.drawString("Game:                 (2014)", 18, 60);
		g.drawString("Kevin Hernandez-Rives", 17, 72);
		g.drawString("Art: Lanea Zimmerman", 18, 90); // 95
		g.drawString("  (Sharm)", 18, 102);
		g.drawString("Music: SketchyLogic", 18, 114);
		g.drawString("from OpenGameArt.org", 17, 126);
	}

	@Override
	public void exit() {
	}

	@Override
	public void upPressed() {
		if (menuOrCredits == MenuOrCredits.MENU) {
			menuCursorPosition--;
			if (menuCursorPosition < 0)	menuCursorPosition = numberOfOptions - 1;	// Loop
			jukebox.playSound(SFX_MOVE_CURSOR);
		}
	}
	
	@Override
	public void downPressed() {
		if (menuOrCredits == MenuOrCredits.MENU) {
			menuCursorPosition++;
			if (menuCursorPosition >= numberOfOptions) menuCursorPosition = 0;
			jukebox.playSound(SFX_MOVE_CURSOR);
		}
	}
	
	@Override
	public void enterPressed() {

		if (menuOrCredits == MenuOrCredits.MENU) {
			switch (menuCursorPosition) {
			case 0: // Normal mode
				globals.difficulty = SceneMaze.Difficulty.NORMAL;
				sceneManager.setScene(SceneMaze.class);
				break;
			case 1: // Hyper mode
				if (!hyperModeUnlocked) break;
				globals.difficulty = SceneMaze.Difficulty.HYPER;
				sceneManager.setScene(SceneMaze.class);
				break;
			case 2: // Credits
				menuOrCredits = MenuOrCredits.CREDITS;
				break;
			case 3: // Quit game
				game.endGame();
				break;
			case 4: // Hyperest
				if (!hyperestModeUnlocked) break;
				globals.difficulty = SceneMaze.Difficulty.HYPEREST;
				sceneManager.setScene(SceneMaze.class);
				break;
			}
		} else {
			menuOrCredits = MenuOrCredits.MENU;
		}

		jukebox.playSound(SFX_SELECT_OPTION);
	}
	
}
