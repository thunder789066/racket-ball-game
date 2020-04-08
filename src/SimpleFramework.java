/**
 * @(#)SimpleFramework.java
 *
 *
 * @Clemens 
 * @version 1.00 2017/5/9
 */


import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;


/**
 * 	SimpleFramework is a JFrame implements the Runnable interface, which 
 *	indicates this object is Thread executable. When we create a new
 *	Thread and start it, it will execute the Runnable object's run()
 *	method. 
 *
 *	There are two ways we could execute a new Object in a different
 *	Thread. The first is to have that object class extend Thread itself.
 *	However, if we do this then each time an instance is created so to
 *	is a new Thread, and each Thread has its own object associated with
 *	it. If we implement Runnable, than multiple Threads can access the
 *	same object instance... AND if we extend Thread we can't inherit from
 *	any other objects, like JFrame!
 *
 *	Instructions for inheriting from this class:
 *	
 *	1) Override initialize():
 *		First call super.initialize(), then instantiate all necessary objects for child
 *		game in this method.
 *
 *	2) Override processInputs():
 *		First call super.processInputs(), then handle inputs for child game accordingly here.
 *
 *	3) Override updateObjects():
 *		First call super, then handle updating object states each frame here.
 *
 *	4) Override render():
 *		First call super, then handle drawing all components to graphics
 *
 *	DONE
 */
public class SimpleFramework extends JFrame implements Runnable {
	
	/** Game thread **/
	private Thread gameThread;
	
	/** Buffering Strategy for Graphics Object (Explained on instantiation below) **/
	private BufferStrategy bs;
	
	/**
	 *	True if game thread is still active.
	 *	Volatile - Prevents multiple threads from accessing
	 *			   this field at the same time. Ensures each
	 *			   Thread sees the updated value, Thread have
	 *			   their own MEMORY CACHE! Any write to a
	 *			   volatile variable is done before reads and
	 *			   volatile variables are visible to ALL Threads.
	 */
	private volatile boolean running;
	
	
	/** Frame Rate for app **/
	protected FrameRate frameRate;
	/** Canvas to draw animation/game onto **/
	protected Canvas canvas;
	
	/** Keyboard listener object **/
	protected Keyboard keyboard;
	
	/** App details - Accessible in child classes (protected) **/
	protected Color appBackground = Color.BLACK;
	protected Color appBorder = Color.LIGHT_GRAY;
	protected float appBorderScale = 0.8f;
	protected Color appFPSColor = Color.GREEN;
	protected Font appFont = new Font("Courier New", Font.PLAIN, 14);
	protected Color appFontColor = Color.GREEN;
	protected int appSleep = 10;
	protected String appTitle = "TBD-Title";
	protected int appWidth = 640;
	protected int appHeight = 480;
	
	
	/**
	 *	Constructor - Don't set up anything here, set things up on Event
	 *				  Dispatch Thread later by invoking the createAndShowGUI().
	 */
	public SimpleFramework() {
		//Nothing here, see createAndShowGUI()
	}
	
	/**
	 *	Create and set up all components, then show GUI. This should be
	 *	invoked by the Thread that is control of the GUI. At the end of
	 *	this implementation, the game thread is born off of this Runnable
	 *	object, thus invoking our run() below.
	 */
	private void createAndShowGUI() {
		this.setTitle(this.appTitle);
		
		/** Canvas to draw onto **/
		this.canvas = new Canvas();
		this.canvas.setBackground(this.appBackground);
		
		/** 
		 *	Ignores the window manager's (thing that schedules painting on your OS)
		 *	call to repaint(), allows us full control of when and where we paint.
		 */
		this.canvas.setIgnoreRepaint(true);
		
		/** Add the canvas to container of JFrame **/
		this.getContentPane().add(this.canvas);
		
		/** Set sizes **/
		this.canvas.setSize(this.appWidth, this.appHeight);
		/** Figures out sizes of all components, resizes window **/
		this.pack();
		
		/** 
		 *	Set up input listeners
		 */
		this.keyboard = new Keyboard();
		this.canvas.addKeyListener(this.keyboard);
		
		/** Start window **/
		this.setVisible(true);
		
		
		/** 
		 *	Buffering:
		 *		Buffer - area of contiguous memory, usually in a video/image
		 *				 device, or system memory.
		 *	Here we are just setting up the canvas to perform Double Buffering.
		 *	To double buffer is to draw one frame in the background as the other
		 *	is displaying, then swap the two, and repeat the process. This helps
		 *	avoid those flashes we got in the past when the rendering of each 
		 *	frame got long.
		 *
		 */
		this.canvas.createBufferStrategy(2); //2 Argument indicates # of buffers
		this.bs = this.canvas.getBufferStrategy(); //Retrieve strategy created for use
		
		/** Buttons immediately work, don't have to click into window **/
		this.canvas.requestFocus();
		
		
		/** Create Thread **/
		
		//Remember, this object is a Runnable object, thus this object's
		//	run() method is invoked when we start this Thread.
		this.gameThread = new Thread(this);  //Pass 'this' runnable object.
		//Start Thread, which will invoke this class's run() method, since
		//	we passed this class as the Runnable reference.
		this.gameThread.start(); //invokes run()
	}
	
	/**
	 *	Invoked by the Animation Thread when it is started. On invocation, the
	 *	following is carried out.
	 *	1)	Invoke initialize() method, which is responsible for creating all of
	 *		our objects in the game, which will be overriden by child class.
	 *
	 *	2)	Track time calculations do determine how quickly an iteration of the
	 *		game thread completes, so we can make adjustments accordingly.
	 *
	 *	3)	Begins a loop, indicated by the volatile running field. Inside this loop
	 *		we calculate the time passed (in nanoseconds), then invoke the game loop
	 *		process and pass speed (amount of time) our computer is currently running
	 *		at.
	 *
	 *	4)	This process repeats until volatile running is false. 
	 *		Calculate speed  ->  invoke game loop process  ->  Calculate speed  ->
	 *		invoke game loop process  ->  calculate speed  ->  ..... etc.
	 *
	 *	NOTE: I have implemented our game loop in a different method just for simplicity,
	 *		  so you can see the full process without being to congested.
	 *
	 */
	@Override public void run() {
		//Set running 
		this.running = true;
		
		/** Responsible for setting up your objects in game **/
		this.initialize();
		
		/** Time calculations **/
		long curTime = System.nanoTime();
		long lastTime = curTime;
		double nsPerFrame;
		
		while (this.running) {
			curTime = System.nanoTime();
			nsPerFrame = curTime - lastTime;  //Time passed since last interaction
			
			/** Invoke game loop with current speed of your computer **/
			this.gameLoop( (float) (nsPerFrame / 1.0E9) );
			
			lastTime = curTime;
		}
		
		/** Once this loops ends, indicates program is done **/
		this.terminate();
	}
	
	/**
	 *	Initializes frame rate. Child classes should invoke this method, then
	 *	override to create their objects here.
	 */
	protected void initialize() {
		//Frame rate should be instantiated, then initialized, or
		//	calculations will not happen correctly.
		this.frameRate = new FrameRate();
		this.frameRate.initialize();
		
		//Child class handle rest
	}
	
	/**
	 * 	Terminates any other running Threads safely. We don't have any others,
	 *	so there is nothing to do here yet. Your child class might eventually.
	 */
	protected void terminate() {
		//Nothing yet
	}
	
	/**
	 * 	THE GAME LOOP YOU'VE HEARD SO MUCH ABOUT.... Ta-da... Pretty simple.
	 *
	 *	@param	delta amount of time your computer took to complete last frame.
	 *			Used to handle animations that don't lag.
	 */
	private void gameLoop(float delta) {
		//Step 1 - Process Inputs
		this.processInput(delta);
		
		//Step 2 - Update Objects
		this.updateObjects(delta);
		
		//Step 3 - Render (draw) next frame
		this.renderFrame();
		
		//Step 4 - Pause (sleep) this thread (gameThread, not EDT which GUI is running on)
		this.sleep(this.appSleep);
	}
	
	/**
	 * 	STEP 1 - PROCESS INPUTS
	 *
	 */
	protected void processInput(float delta) {
		//  ?????
	}
	
	/**
	 *	STEP 2 - UPDATE YOUR OBJECTS
	 */
	protected void updateObjects(float delta) {
		//  ?????
	}
	
	/**
	 *	STEP 3(A) - RENDER NEXT FRAME
	 *	
	 *	Handles accessing the Graphics object form the BufferedStrategy. Since
	 *	the buffered strategy was created off of the Canvas. Anything drawn to
	 *	its Graphics is thus shown onto the Canvas.
	 *
	 *	It seems like there's a lot going on here, don't over think it. Most of
	 *	this is because of how the Window Manager from the OS handles scheduling
	 *	painting.
	 */
	protected void renderFrame() {
		do {
			do {
				Graphics g = null;
				try {
					//Retrieve graphics from Canvas's buffer strategy
					g = this.bs.getDrawGraphics();
					
					//Clear Graphics for next frame
					g.clearRect(0, 0, this.getWidth(), this.getHeight());
					
					//Render frame
					this.render(g);
				} finally {
					//Dispose of Graphics object, memory efficiency
					if (g != null)
						g.dispose();
				}
			//Stops when Window Manager has destroyed Graphics
			} while (bs.contentsRestored());
			/** Show() will swap current graphics with one just drew on */
			this.bs.show();
		//Window manager done
		} while (bs.contentsLost());
	}
	
	/**
	 *	STEP 3(B) - RENDER (FOR REAL THIS TIME)
	 *
	 *	Renders (paints) each component of game, this should be overriden
	 *	by child classes.
	 */
	protected void render(Graphics g) {
		//Draw frame rate
		g.setFont(this.appFont);
		g.setColor(this.appFPSColor);
		this.frameRate.calculate();
		//Draws frame rate.
		g.drawString(this.frameRate.getFrameRate(), 20, 20);
	}
	
	/**
	 *	STEP 4 - SLEEP
	 *
	 *	.... you get it.
	 */
	private void sleep(long sleep) {
		try {
			//Static - sleeps thread from which it was invoked (game thread)
			Thread.sleep(sleep);
		} catch (InterruptedException ex) {
			//Nothing to do
		}
	}
	
	/**
	 *	Handles safely killing threads when window closed
	 *
	 *	thread.join() - merges two thread back together. Thread
	 *					finishes process, kills. Current thread
	 *					waits until other thread has finished.
	 */
	protected void onWindowClosing() {
		try {
			//Stop loop
			this.running = false;
			//Kill game thread
			this.gameThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
//=========================================================================
	/**
	 *	To be invoked by the main method of child class, and passed
	 *	a new instance of the child. 
	 *
	 *	This method then sets up a window
	 *	listener to invoke our onWindowClosing() method above to handle
	 *	safe shutdown of the program. 
	 *
	 *	THEN, the reason our constructor does nothing, this method invokes
	 *	a new Runnable object using SwingUtilities, and activates our GUI
	 *	from that separate Thread -> which is the Event Dispatch Thread.
	 *
	 *	SO NOW... WE have our GUI being created on the EDT by invoking the
	 *	createAndShowGUI() method, which if you recall, starts the GameThread.
	 *	So we have the GUI being created on EDT, and the game loop running on
	 *	the GameThread ---> 2 Threads!.
	 *
	 *
	 */
	protected static void launchApp( final SimpleFramework app) {
		/** Add window listener to app **/
		app.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				app.onWindowClosing();
			}
		});
		
		/** Set up GUI on Event Dispatch Thread in Swing **/
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				app.createAndShowGUI();
			}
		});
		
	}

//=========================================================================

	/**
	 *	Main method example... child classes should do this...
	 */
	public static void main(String[] args) {
		launchApp ( new SimpleFramework() );
	}
	
}