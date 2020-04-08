import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

/**
 * Make a backend thingy to receive keyboard inputs in a separate thread
 *
 * How loops work:
 * 1) Process inputs
 * 2) Update objects
 * 3) Render next graphics frame
 * 4) Pause/sleep to delay for drawing
 */
public final class Keyboard implements KeyListener {
    private ArrayList<Key> keys = new ArrayList<>();
    private String typed = "";
    private int[] pressed = new int[Byte.MAX_VALUE * 2];
    
    /* Default constructor */
    
    public void process() {
        ArrayList<Key> processing = this.keys;
        this.typed = "";
        this.keys = new ArrayList<>();
        for (Key key: processing)
            if (key.state == State.PRESSED)
                this.pressed[key.event.getKeyCode()]++;
            else if (key.state == State.RELEASED)
                this.pressed[key.event.getKeyCode()] = 0;
            else if (key.state == State.TYPED)
                this.typed += key.event.getKeyChar();
    }
    public boolean isPressed(int keyCode) {
        return(this.pressed[keyCode] > 0);
    }
    public int timePressed(int keyCode) {
        return(this.pressed[keyCode]);
    }
    public String getText() {
        return(this.typed);
    }
    
    /* Manage key events */
    @Override public void keyPressed(KeyEvent e) {
        this.keys.add(new Key(e, State.PRESSED));
    }
    @Override public void keyReleased(KeyEvent e) {
        this.keys.add(new Key(e, State.RELEASED));
    }
    @Override public void keyTyped(KeyEvent e) {
        this.keys.add(new Key(e, State.TYPED));
    }
    
    /* Represent a single key event */
    private class Key {
        State state;
        KeyEvent event;
        public Key(KeyEvent e, State s) {
            this.state = s;
            this.event = e;
        }
    }
    private enum State {TYPED, PRESSED, RELEASED}
}