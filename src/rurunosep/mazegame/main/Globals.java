package rurunosep.mazegame.main;

import rurunosep.mazegame.scene.SceneMaze;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public final class Globals {

	public SceneMaze.Difficulty difficulty = null;
	public int normalModeHighScore = 0;
	public int hyperModeHighScore = 0;
	private static Globals instance;
	
	private Globals() {
	}
	
	public static Globals getInstance() {
		if (instance == null) {
			instance = new Globals();
		}
		return instance;
	}
	
	// Save Format:
	// normalModeHighScore
	// hyperModeHighScore
	
	public void loadSaveData(String filepath) {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath)))) {

			normalModeHighScore = Integer.parseInt(br.readLine());
			hyperModeHighScore = Integer.parseInt(br.readLine());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeSaveData(String filepath) {
		try (PrintWriter pr = new PrintWriter(filepath, "UTF-8")) {

			pr.println(normalModeHighScore);
			pr.println(hyperModeHighScore);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
