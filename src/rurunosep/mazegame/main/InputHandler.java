package rurunosep.mazegame.main;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import rurunosep.mazegame.scene.SceneManager;

public final class InputHandler{

	private static InputHandler instance = null;
	private boolean[] keyStates = new boolean[256];
	private SceneManager sceneManager;
	
	private InputHandler() {
		sceneManager = SceneManager.getInstance();
	}
	
	public static InputHandler getInstance() {
		if (instance == null) {
			instance = new InputHandler();
		}
		return instance;
	}
	
	public void setKeyBindings(JComponent jc) {
		
		InputMap im = jc.getInputMap();
		ActionMap am = 	jc.getActionMap();
		
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false), "up.p");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, true), "up.r");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false), "down.p");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true), "down.r");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "left.p");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true), "left.r");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "right.p");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true), "right.r");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "enter.p");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "enter.r");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false), "esc.p");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true), "esc.r");
		
		am.put("up.p", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e) {
				keyStates[KeyEvent.VK_UP] = true;
				sceneManager.keyPressed(KeyEvent.VK_UP);
			}
		});
		am.put("up.r", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e) {
				keyStates[KeyEvent.VK_UP] = false;
			}
		});
		am.put("down.p", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e) {
				keyStates[KeyEvent.VK_DOWN] = true;
				sceneManager.keyPressed(KeyEvent.VK_DOWN);
			}
		});
		am.put("down.r", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e) {
				keyStates[KeyEvent.VK_DOWN] = false;
			}
		});
		am.put("left.p", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e) {
				keyStates[KeyEvent.VK_LEFT] = true;
				sceneManager.keyPressed(KeyEvent.VK_LEFT);
			}
		});
		am.put("left.r", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e) {
				keyStates[KeyEvent.VK_LEFT] = false;
			}
		});
		am.put("right.p", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e) {
				keyStates[KeyEvent.VK_RIGHT] = true;
				sceneManager.keyPressed(KeyEvent.VK_RIGHT);
			}
		});
		am.put("right.r", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e) {
				keyStates[KeyEvent.VK_RIGHT] = false;
			}
		});
		am.put("enter.p", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e) {
				keyStates[KeyEvent.VK_ENTER] = true;
				sceneManager.keyPressed(KeyEvent.VK_ENTER);
			}
		});
		am.put("enter.r", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e) {
				keyStates[KeyEvent.VK_ENTER] = false;
			}
		});
		am.put("esc.p", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e) {
				keyStates[KeyEvent.VK_ESCAPE] = true;
				sceneManager.keyPressed(KeyEvent.VK_ESCAPE);
			}
		});
		am.put("esc.r", new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e) {
				keyStates[KeyEvent.VK_ESCAPE] = false;
			}
		});
		
	}

	public boolean getKeyState(int keycode) {
		return keyStates[keycode];
	}
	
}
