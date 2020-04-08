import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.*;

public class RacketBall extends SimpleFramework{
	
	//Keyboard keyboard = new Keyboard();
	Random rem = new Random();
	
	private String scoreTitle;
	private boolean up, down;
	private int racketPosition, racket_height, racket_width, racket_x;
	private int racket_face;
	private int ball_x, ball_y, ball_vx, ball_vy;
	private int initialBallSpeed;
	private int score, scoreboard_width;
	private int wall_width;
			/*     _____________________________
			 * 									|
			 * 									|
			 * 									|
			 * 									|	Wall -> vertical
			 * 									|
			 * 									|
			 * 									|
			 *     _____________________________|
			 */
	
	//Constructor
	public RacketBall() {
		this.score = 0;
		this.scoreTitle = "SCORE";
		this.up = false;
		this.down = false;
		this.initialBallSpeed = 1;
		this.scoreboard_width = 150;
		this.wall_width = 75;
		this.racketPosition = this.appHeight / 2;
		this.racket_height = 75;
		this.racket_width = 10;
		this.racket_x = 30;
		this.ball_x = this.racket_x + this.racket_width;
		this.ball_y = this.racketPosition;
		this.ball_vx = this.initialBallSpeed;
		this.ball_vy = rem.nextInt(3) - 1;
		this.racket_face = racket_x + racket_width / 2;
		
		super.appWidth = 840;
	}
	
	/* 1st - draw the wall on the right side of frame
	 *
	 *__________________________________________
	 *     _____________________________		|
	 * 									|		|
	 * 									|		|
	 * 									|		|
	 * 									|		|
	 * 									|		|
	 * 									|		|
	 * 									|		|
	 *     _____________________________|		|
	 *__________________________________________|
	 *
	 * 
	 * 2nd - draw the scoreTitle & score # in top right corner
	 *
	 *______________________________________________
	 *     _____________________________	SCORE	|
	 * 									|	  0		|
	 * 									|			|
	 * 									|			|
	 * 									|			|
	 * 									|			|
	 * 									|			|
	 * 									|			|
	 *     _____________________________|			|
	 *______________________________________________|
	 *
	 * 
	 * 3rd - draw the racket & ball on the left side
	 *
	 *	 ___________________________________________|_______________________________________________
	 *	|											| __________________________________	SCORE	|
	 *	|											|	   								|	  0		|
	 *	|	|-> racket								|	   								|			|
	 *	|	|										|	   								|			|
	 *	|	|	O -> ball							|	   								|			|
	 * 	|											|									|			|
	 *	|											|									|			|
	 *	|											| __________________________________|	 	 	|
	 *	|___________________________________________|_______________________________________________|
	 *		 racket(up/down) & ball <- other 50% <- | -> 50% width of frame -> wall & score
	 */
	
	@Override public void render(Graphics g) {
		g.setColor(Color.GREEN);
		
		//draw wall
		g.fillRect(this.appWidth - scoreboard_width - wall_width, 0, wall_width, this.appHeight);
		
		//draw racket		
		g.fillRect(racket_x - racket_width / 2, this.racketPosition - this.racket_height / 2, racket_width, this.racket_height);

		//draw ball
		g.fillOval(ball_x, ball_y, racket_width, racket_width);
		
		//draw score
		int padding = 10;
		g.drawString(this.scoreTitle, this.appWidth - scoreboard_width + padding * 5, 3 * padding);
		g.drawString(Integer.toString(this.score), this.appWidth - scoreboard_width + padding * 7, padding * 5);
	}
	
	/*	After game starts, ball will move automatically towards the wall
	 *	& bounce back the exact opposite direction. When it comes in
	 *	contact with the racket, it'll bounce off, etc.
	 *
	 *	Use UP & DOWN arrow keys to control racket.
	 */

	@Override protected void processInput(float delta) {
		keyboard.process();
		this.down = false;
		this.up = false;
		if (keyboard.isPressed(KeyEvent.VK_UP) && this.racketPosition >= this.racket_height / 2) {
			//racket moves upward
			this.up = true;			
		}
		if (keyboard.isPressed(KeyEvent.VK_DOWN) && this.racketPosition <= this.appHeight - this.racket_height / 2) {
			//racket moves downward
			this.down = true;
		}
	}
	
	@Override protected void updateObjects(float delta) {
		//move racket
		if (this.up == true)
			racketPosition -= 3;
		if (this.down == true)
			racketPosition += 3;
		
		//move ball
		ball_x += ball_vx;
		ball_y += ball_vy;
		if (ball_x >= appWidth - scoreboard_width - wall_width)
			ball_vx = -ball_vx;
		if (ball_y <= 0)
			ball_vy = -ball_vy;
		if (ball_y >= appHeight)
			ball_vy = -ball_vy;
		
		//if ball hits racket {  ball moves opposite direction;    score++;  }
		int racket_top, racket_bottom;
		racket_top = racketPosition - racket_height / 2;
		racket_bottom = racketPosition + racket_height / 2;
		if (ball_vx < 0 && ball_y >= racket_top && ball_y <= racket_bottom && ball_x <= racket_face && ball_x >= racket_x) {
			ball_vx = -ball_vx *2; //+1 for easy mode  *2 for fun
			ball_vy = rem.nextInt(3) - 1;
			score++;
		}
	}

//=========================================================================

	public static void main(String[] args) {
		launchApp(new RacketBall());
	}
		
}
