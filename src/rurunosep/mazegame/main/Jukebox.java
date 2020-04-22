package rurunosep.mazegame.main;

import java.util.HashMap;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public final class Jukebox {

	private static Jukebox instance;
	private HashMap<String, Clip> clips;
	private String currentBGM = "";
	
	private Jukebox() {
		clips = new HashMap<>();
	}
	
	public static Jukebox getInstance() {
		if (instance == null) {
			instance = new Jukebox();
		}
		return instance;
	}

	public void playSound(String resourcePath) {
		if(clips.get(resourcePath) ==  null) loadSound(resourcePath);
		if(clips.get(resourcePath) != null) {
			Clip clip = clips.get(resourcePath);
			if(clip.isRunning()) {
				clip.stop();
			}
			clip.setFramePosition(0);
			clip.start();
		}
	}

	public void loopSound(String resourcePath) {
		if(clips.get(resourcePath) ==  null) loadSound(resourcePath);
		if(clips.get(resourcePath) != null) {
			Clip clip = clips.get(resourcePath);
			clip.setLoopPoints(0, -1);
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		}
	}

	public void stopSound(String resourcePath) {
		if(clips.get(resourcePath) != null) {
			Clip clip = clips.get(resourcePath);
			clip.stop();
			clip.setFramePosition(0);
		}
	}

	public void loadSound(String resourcePath) {
		if(clips.get(resourcePath) != null) return;
		Clip clip = null;
		try {		
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(getClass().getResource(resourcePath));
			clip = AudioSystem.getClip();
			clip.open(audioIn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		clips.put(resourcePath, clip);
	}

	public void setBGM(String resourcePath) {
		if (currentBGM.equals(resourcePath)) return;
		stopSound(currentBGM);
		loopSound(resourcePath);
		currentBGM = resourcePath;
	}

}
