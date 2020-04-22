package rurunosep.mazegame.main;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import rurunosep.mazegame.scene.SceneManager;
import rurunosep.mazegame.scene.SceneTitle;

@SuppressWarnings("serial")
public final class GamePanel extends JPanel implements Runnable {

	public static final int TARGET_FPS = 60;
	public static final int TARGET_FRAME_DURATION = 1000 / TARGET_FPS;
	public static final int MAX_FPS = 120;
	public static final int MIN_FRAME_DURATION = 1000 / MAX_FPS;
	private static final String SAVE_FILE_PATH = "mazegame.sav";

	private static GamePanel instance;
	private boolean running = false;
	private Thread thread;
	private double delta;
	private SceneManager sceneManager;
	private Screen screen;
	private InputHandler inputHandler;
	private Globals globals;
	
	private GamePanel() {
		super(true);
		setIgnoreRepaint(true);
		setPreferredSize(new Dimension(Screen.SCREEN_WIDTH * Screen.SCREEN_SCALE, Screen.SCREEN_HEIGHT * Screen.SCREEN_SCALE));
		setFocusable(true);
		requestFocus();
		
		sceneManager = SceneManager.getInstance();
		screen = Screen.getInstance();
		inputHandler = InputHandler.getInstance();
		globals = Globals.getInstance();

		inputHandler.setKeyBindings(this);
	}
	
	public static GamePanel getInstance() {
		if (instance == null) {
			instance = new GamePanel();
		}
		return instance;
	}
	
	public void start() {
		if (thread == null) {
			thread = new Thread(this);
		}
		thread.start();
	}

	private void init() {
		globals.loadSaveData(SAVE_FILE_PATH);
		sceneManager.setScene(SceneTitle.class);
	}

	private void update() {
		sceneManager.update();
	}

	private void render() {
		screen.clearScreen();
		sceneManager.render();
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		screen.drawScreenToWindow(g);
	}

	private void exit() {
		globals.writeSaveData(SAVE_FILE_PATH);
		System.exit(0);
	}

	@Override
	public void run() {
		init();
		gameLoop();
		exit();
	}

	private void gameLoop() {
		
		long lastTime = System.nanoTime();
		
		running = true;
		while(running) {

			long now = System.nanoTime();
			long elapsed = now - lastTime;
			lastTime = now;
			delta = (double)elapsed / (TARGET_FRAME_DURATION * 1000000);

			update();
			render();

			long wait = MIN_FRAME_DURATION - (System.nanoTime() - lastTime) / 1000000;
			if (wait < 0) wait = 0;
			try {
				Thread.sleep(wait);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
		
	}

	public void endGame() {
		running = false;
	}
	
	public double getDelta() {
		return delta;
	}

	
}
