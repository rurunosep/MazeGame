package rurunosep.mazegame.main;

import javax.swing.JFrame;

public class Main {
	
	public static void main (String[] args) {
		
		JFrame window = new JFrame("Maze Game");
		GamePanel game = GamePanel.getInstance();
		window.setContentPane(game);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);

		game.start();
		
	}
	
}
