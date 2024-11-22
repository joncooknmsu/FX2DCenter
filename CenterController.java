//
// Controller for CenteredPlayer app
// - this app uses arrow keys to "move" a player (the red ball) around
//   a game area (grey area), but the player stays in the center of 
//   the window, and the game area moves around the player, instead of
//   the player moving.
// - TODO: once the player is near enough to a border or corner, the game
//   area should really remain in place and then the player should move 
//   in the window until they are at the border; so both game area and 
//   player movement are needed for a real game.
//

import javafx.scene.layout.Pane;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.shape.Circle;
import javafx.scene.control.Slider;
import javafx.scene.control.MenuBar;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import java.util.ArrayList;
import java.util.PrimitiveIterator;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.PrimitiveIterator.OfInt;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

public class CenterController extends AnimationTimer
{

@FXML private MenuBar menuBar;
@FXML private Slider speedSlider;
@FXML private Circle redBall;
@FXML private Pane gamePane;
@FXML private Pane windowPane;
private ArrayList<FlyingBox> boxes;
private ArrayList<FlyingSprite> sprites;

// Constructor cannot really do much since the FXML elements are not
// yet initialized (try printing them; they're null)
public CenterController()
{
    // System.out.println("BCC: FXApp:"+CenteredPlayer.app+ " scene:"+
    // CenteredPlayer.app.scene+ " pane:"+CenteredPlayer.app.pane);
    // System.out.println("BCC: slider:"+speedSlider+ " gpane:"+gamePane+ "
    // circle:"+redBall);
}

public boolean goNorth, goSouth, goWest, goEast, running;

// This is where we can actually do some "constructor" work when using FXML
@FXML
public void initialize()
{
   // by default the slider has the focus (not sure why), so we request it
   // back to the main window here. Must be in a runLater() context!
   Platform.runLater(() -> windowPane.requestFocus()); 
   menuBar.setOpacity(1.0);
   menuBar.setViewOrder(-1.0); // finally, my game area does not draw over this!
   // https://stackoverflow.com/questions/29962395/how-to-write-a-keylistener-for-javafx
   // Now create callback handlers for the keypress and keyrelease events for
   // the arrow keys; I don't care for anonymous classes used this way but I
   // need to think about an alternative design; for now, it works.
   windowPane.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                System.out.println("key pressed: " + event.getCode());
                switch (event.getCode()) {
                    case UP:    goNorth = true; break;
                    case DOWN:  goSouth = true; break;
                    case LEFT:  goWest  = true; break;
                    case RIGHT: goEast  = true; break;
                    case SHIFT: running = true; break;
                }
            }
        });
   windowPane.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                System.out.println("key released: " + event.getCode());
                switch (event.getCode()) {
                    case UP:    goNorth = false; break;
                    case DOWN:  goSouth = false; break;
                    case LEFT:  goWest  = false; break;
                    case RIGHT: goEast  = false; break;
                    case SHIFT: running = false; break;
                }
            }
        });

    // clipping: https://stackoverflow.com/questions/15920680/how-to-restrict-visibility-of-items
    // https://blog.e-zest.com/sliding-in-javafx-its-all-about-clipping/
    // This clipping seems to possibly not be working yet. Sometimes the grey
    // game area is drawn over the menu bar; I haven't figured it out yet...
    // - note: it is doing something, but 
    Rectangle clip = new Rectangle(800, 400);
    clip.setLayoutX(0); clip.setLayoutY(0);
    gamePane.setClip(clip);
    // System.out.println("CPI: FXApp:" + CenteredPlayer.app + " scene:" + CenteredPlayer.app.scene
    // +
    // " pane:" + CenteredPlayer.app.pane);
    // System.out.println("CPI: slider:"+speedSlider+ " gpane:"+gamePane+ "
    // circle:"+redBall);
    //
    // I have the red ball circle in my FXML but for some reason it is not
    // resolving, so I explicitly look it up here (and this works). I think it
    // is because it is nested in my gamePane, and maybe I need to do something
    // different in my FXML to get it to auto-resolve.
    redBall = (Circle) gamePane.lookup("#redBall");
    // make the flying boxes
    makeBoxes();
    // position ball (player) in center of screen
    redBall.setCenterX(600); redBall.setCenterY(300);
    // create the background -- checker board of greyish rectangles
    double grayLevel = 0.5;
    for (int x=0; x < 1200; x+=200) {
       for (int y=0; y < 600; y+=200) {
          Rectangle r = new Rectangle(200, 200, Color.color(grayLevel, 
                                          grayLevel, grayLevel, 0.25));
          gamePane.getChildren().add(r);
          // not sure if I should use layoutX/Y or just X/Y. This works
          // but I need to read more about it. the For background I think it
          // is OK
          final int lx = x; final int ly = y; // needed for runLater()
          Platform.runLater(() -> {
              r.setLayoutX(lx);  r.setLayoutY(ly);
          });
          if (grayLevel > 0.45)  // flip grayness back and forth
             grayLevel = 0.25;
          else
             grayLevel = 0.5;
       }
    }
    System.out.println("LO: " + gamePane.getLayoutX() + "," + gamePane.getLayoutY());
    System.out.println("BLO: " + gamePane.getLayoutBounds());
    System.out.println("BIL: " + gamePane.getBoundsInLocal());
    System.out.println("BIP: " + gamePane.getBoundsInParent());
    // The start line below is for the AnimationTimer part of this class;
    // we just let it run for the duration of the application
    this.start();
}

// Make a number of bouncing rectangles
public void makeBoxes()
{
    IntStream randStream = (new Random()).ints(0, 1000001);
    PrimitiveIterator.OfInt randInts = randStream.iterator();
    boxes = new ArrayList<FlyingBox>();
    for (int i = 0; i < 10; i++) {
        boxes.add(new FlyingBox(gamePane, randInts.next() % 200 + 20, randInts.next() % 200 + 20,
                randInts.next() % 40 + 2, randInts.next() % 40 + 2, randInts.next()));
    }
    gamePane.getChildren().addAll(boxes);
    sprites = new ArrayList<FlyingSprite>();
    sprites.add(new FlyingSprite(gamePane, 50, 50, 40, 40, "/myAvatar.png"));
    sprites.add(new FlyingSprite(gamePane, 150, 150, 50, 50, "/bunnysheet5.png", 12, 216, 38, 38, 8));
    gamePane.getChildren().addAll(sprites);
}

// Animation data for timer
private long moveRate = 10000000; // animation timer is in nanoseconds!
private long prevTime = 0;
// animation data for red ball
private int direction = 0;
private int dirRate = 2;
private int posAdjustX, posAdjustY;
private boolean timerBasedMovement = false;
private double loY = 0;

// This handle() method is where the timer is used, this is called every
// time JavaFX thinks we should update the animation -- i.e., the view on
// our application. We do not control how often or how fast this happens,
// JavaFX just adapts it to whatever hardware or platform we are running on
@Override
public void handle(long now)
{
    if (prevTime == 0) {
        // just set starting time on first call and skip animation
        prevTime = now;
        return;
    }
    if (loY != gamePane.getLayoutY()) {
       loY = gamePane.getLayoutY();
       System.out.println("LO: " + gamePane.getLayoutX() + "," + gamePane.getLayoutY());
       System.out.println("BLO: " + gamePane.getLayoutBounds());
       System.out.println("BIL: " + gamePane.getBoundsInLocal());
       System.out.println("BIP: " + gamePane.getBoundsInParent());
       System.out.println("MBR: " + menuBar.getHeight());
       System.out.println("----------------------------------------------");
    }
    //System.out.println("pane width: " + gamePane.getWidth());
    Rectangle clip = (Rectangle) gamePane.getClip();
    clip.setWidth(gamePane.getWidth());   // doesn't work, why?
    clip.setHeight(gamePane.getHeight());
    // calculate time since last call
    long elapsed = now - prevTime;
    if (elapsed < moveRate) // don't do anything, just wait for more time
        return;
    // take out of elapsed time as many moveRates as possible
    int numRates = (int) (elapsed / moveRate); // integer division
    prevTime += moveRate * numRates;
    if (timerBasedMovement) {
       // this is left over from the bounce example, and is not used in this
       // demo; I just want to keep it in, in case it comes in handy
       if (redBall.getCenterX() + redBall.getRadius() > gamePane.getWidth())
           direction = -dirRate;
       else if (redBall.getCenterX() - redBall.getRadius() < 0)
           direction = dirRate;
       posAdjustX = direction * numRates;
       posAdjustY = 0;
    } else {
       // keyboard based movement
       if (goSouth) posAdjustY = dirRate;
       else if (goNorth) posAdjustY = -dirRate;
       else posAdjustY = 0;
       if (posAdjustY > 0 && redBall.getCenterY() + redBall.getRadius() > gamePane.getHeight())
           posAdjustY = 0;
       else if (posAdjustY < 0 && redBall.getCenterY() - redBall.getRadius() < 0)
           posAdjustY = 0;
       if (goWest) posAdjustX = -dirRate;
       else if (goEast) posAdjustX = dirRate;
       else posAdjustX = 0;
       if (posAdjustX > 0 && redBall.getCenterX() + redBall.getRadius() > gamePane.getWidth())
           posAdjustX = 0;
       else if (posAdjustX < 0 && redBall.getCenterX() - redBall.getRadius() < 0)
           posAdjustX = 0;
    }
    // best practice is to update any viewable thing by letting Java/JavaFX
    // to decide to fit it into the proper thread, with Platform.runLater()
    // - the easiest syntax to use is a "lambda expression", as below
    Platform.runLater(() -> {
        redBall.setCenterX(redBall.getCenterX() + posAdjustX);
        gamePane.setTranslateX(gamePane.getTranslateX() - posAdjustX);
        redBall.setCenterY(redBall.getCenterY() + posAdjustY);
        gamePane.setTranslateY(gamePane.getTranslateY() - posAdjustY);
    });
    // tell the bouncing rectangles to update their own positions
    for (FlyingBox box : boxes) {
        box.move();
    }
    for (FlyingSprite sprite : sprites) {
        sprite.move();
    }
    return;
}

// This is our handler for when the slider is moved
// NOT USED right now
@FXML
public void speedChanged(Event e)
{
    // System.out.println("Speed changed! ("+e+") "+redBall);
    dirRate = (int) speedSlider.getValue();
    if (direction < 0)
        direction = -dirRate;
    else
        direction = dirRate;
}

// Handler for menu File->Close selection
@FXML
public void menuFileClose(Event e)
{
    System.out.println("File->Close! (" + e + ")");
    javafx.application.Platform.exit();
}

// Handler for menu Help->About selection
@FXML
public void menuHelpAbout(Event e)
{
    System.out.println("Help->About! (" + e + ")");
    System.out.println("Controller:" + this);
}

} // end class
