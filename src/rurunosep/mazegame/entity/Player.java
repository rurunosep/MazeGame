package rurunosep.mazegame.entity;

public class Player extends Entity {

	public static final int ANIM_STATE_WALKING_DOWN = 0;
	public static final int ANIM_STATE_WALKING_LEFT = 1;
	public static final int ANIM_STATE_WALKING_RIGHT = 2;
	public static final int ANIM_STATE_WALKING_UP = 3;
	public static final int FRAMES_PER_ANIMATION = 3;
	public static final int ANIMATION_DELAY = (int)(1000 * 0.157); // in milliseconds (synced to music at 190 BPM)
	private boolean animationPaused = false;
	private int currentAnimationState = 0;
	private int currentAnimationFrame = 1;
	private long animationLastUpdatedTime = 0;
	private boolean frameNumberIncreasing = true;
	
	public Player() {
		super.moveSpeed = 3.0;
		super.hitboxWidth = SPRITE_SIZE * 0.5;
		super.hitboxHeight = SPRITE_SIZE * 0.5;
	}

	@Override
	public void update() {
		super.update();

		if (movingLeft) currentAnimationState = ANIM_STATE_WALKING_LEFT;
		if (movingRight) currentAnimationState = ANIM_STATE_WALKING_RIGHT;
		if (movingUp) currentAnimationState = ANIM_STATE_WALKING_UP;
		if (movingDown) currentAnimationState = ANIM_STATE_WALKING_DOWN;
		
		// Update animation frame
		if ((System.currentTimeMillis() - animationLastUpdatedTime > ANIMATION_DELAY) && (!animationPaused)) {
			animationLastUpdatedTime = System.currentTimeMillis();
			if (frameNumberIncreasing) {
				currentAnimationFrame++;
			} else {
				currentAnimationFrame--;
			}
			if (currentAnimationFrame == FRAMES_PER_ANIMATION) {
				frameNumberIncreasing = false;
			} else if (currentAnimationFrame == 1) {
				frameNumberIncreasing = true;
			}
		}

		setSpriteFrameID(currentAnimationState * FRAMES_PER_ANIMATION + currentAnimationFrame);
	}
	
	public void setAnimationPaused(boolean animationPaused) {
		this.animationPaused = animationPaused;
	}
	
}
