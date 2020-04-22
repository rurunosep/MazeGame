package rurunosep.mazegame.scene;

import java.awt.event.KeyEvent;

import rurunosep.mazegame.main.Screen;
import rurunosep.mazegame.main.InputHandler;

public abstract class Scene {

	protected Screen screen;
	protected InputHandler inputHandler;
	
	protected Scene() {
		screen = Screen.getInstance();
		inputHandler = InputHandler.getInstance();
	}

	public abstract void init();
	public abstract void update();
	public abstract void render();
	public abstract void exit();
	
	public void keyPressed(int keycode) {
		switch (keycode) {
		case KeyEvent.VK_UP: upPressed(); break;
		case KeyEvent.VK_DOWN: downPressed(); break;
		case KeyEvent.VK_ENTER: enterPressed(); break;
		case KeyEvent.VK_ESCAPE: escapePressed(); break;
		}
	}

	public void upPressed() { }
	public void downPressed() { }
	public void enterPressed() { }
	public void escapePressed() { }

}
