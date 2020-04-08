//package javagames.util;

public class FrameRate {
	private String frameRate;
	private long lastTime;
	private long delta;
	private int frameCount;
	/**
	 * 	Invoked before the frame rate measurements begin. 
	 * 	Initializes the frame rate to 0 and sets the last
	 * 	time to the current time.
	 * 	
	 */
	public void initialize() {
		this.lastTime = System.currentTimeMillis();
		this.frameRate = "FPS 0";
	}
	/**
	 * 	Invoked once for every rendered frame. Calculates the
	 * 	frame rate by subtracting the last time from the 
	 * 	current time and then updates all necessary info.
	 * 
	 */
	public void calculate() {
		long current = System.currentTimeMillis();
		this.delta += current - this.lastTime;
		this.lastTime = current;
		this.frameCount++;
		if( this.delta > 1000 ) {
			this.delta -= 1000;
			this.frameRate = String.format( "FPS %s", this.frameCount );
			this.frameCount = 0;
		}
	}
	/**
	 * 	Returns the current frame rate
	 * 
	 * 	@return	current frame rate String
	 */
	public String getFrameRate() {
		return this.frameRate;
	}
}