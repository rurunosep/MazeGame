package rurunosep.mazegame.main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public final class Screen extends BufferedImage {

	public static final int SCREEN_WIDTH = 160;
	public static final int SCREEN_HEIGHT = 144;
	public static final int SCREEN_SCALE = 3;
	public static final int IMAGE_TYPE = TYPE_INT_ARGB;
	private static Screen instance;
	private Graphics2D g;
	
	private Screen() {
		super(SCREEN_WIDTH, SCREEN_HEIGHT, IMAGE_TYPE);
		g = createGraphics();		
	}
	
	public static synchronized Screen getInstance() {
		if (instance == null) {
			instance = new Screen();
		}
		return instance;
	}

	public void drawScreenToWindow(Graphics gWindow) {
		gWindow.drawImage(this, 0, 0, SCREEN_WIDTH * SCREEN_SCALE, SCREEN_HEIGHT * SCREEN_SCALE, null);
	}

	public void clearScreen() {
		g.setBackground(new Color(0x00000000));
		g.clearRect(0, 0, getWidth(), getHeight());
	}

	public void drawToScreen(BufferedImage image, int xo, int yo) {
		g.drawImage(image, xo, yo, image.getWidth() + xo, image.getHeight() + yo,
				0, 0, image.getWidth(), image.getHeight(), null);
	}

	public void drawToScreen(BufferedImage image, int xo, int yo, int xsrc, int ysrc, int wsrc, int hsrc) {
		g.drawImage(image, xo, yo, wsrc + xo, hsrc + yo, xsrc, ysrc, xsrc + wsrc, ysrc + hsrc, null);
	}
	
}
