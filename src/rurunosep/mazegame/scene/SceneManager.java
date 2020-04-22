package rurunosep.mazegame.scene;

import java.lang.reflect.InvocationTargetException;

public final class SceneManager {

	private static SceneManager instance;
	private Scene currentScene;
	
	private SceneManager() {
	}
	
	public static synchronized SceneManager getInstance() {
		if(instance == null) {
			instance  = new SceneManager();
		}
		return instance;
	}

	public void setScene(Class<? extends Scene> scene) {
		try {
			Scene next = scene.getConstructor().newInstance();
			next.init();
			if (currentScene != null) currentScene.exit();
			currentScene = next;
		} catch (NoSuchMethodException | InstantiationException |
				IllegalAccessException | InvocationTargetException e) {
			// Do nothing
		}
	}

	public void update() {
		if (currentScene != null) currentScene.update();
	}

	public void render() {
		if (currentScene != null) currentScene.render();
	}

	public void keyPressed(int keycode) {
		if (currentScene != null) currentScene.keyPressed(keycode);
	}
	
}