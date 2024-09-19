//
// FlyingBox: each object represents an animated "flying" rectangle in
// the "game"
//

//import javafx.event.Event;
import javafx.scene.shape.Rectangle;
import javafx.application.Platform;
import javafx.scene.paint.Color;
//import javafx.application.Platform;
import javafx.scene.layout.Pane;

public class FlyingBox extends Rectangle
{

private int xDirection = 1;
private int yDirection = 1;
private int dirRate = 2;
private Pane area;

public FlyingBox(Pane gamePane, int x, int y, int w, int h, int c)
{
    // super(w, h, Color.color(0.4, 0.2, 0.6, 1.0));
    // make RGB values 0.0-1.0 for Color from a single int using each decimal digit as most significant
    super(w, h, Color.color(((c / 100.0) - (c / 100)), ((c / 10.0) - (c / 10)), ((c / 1000.0) - (c / 1000)), 1.0));
    // System.out.println("made a rectangle");
    // System.out.println("RGB: " + ((c / 100.0) - (c / 100)) + " " + ((c / 10.0) -
    // (double) (c / 10)) + " " + ((c / 1000.0) - (double) (c / 1000)));
    area = gamePane;
    Platform.runLater(() -> {
        setX(x);
        setY(y);
        setWidth(w);
        setHeight(h);
    });
}

// update the box's position; x,y is upper left corner on a box
// so we need to add its size when checking the right and bottom
// bounds
public void move()
{
    if (getX() + getWidth() > area.getWidth() || getX() < 0) {
        xDirection = -xDirection;
    }
    if (getY() + getHeight() > area.getHeight() || getY() < 0) {
        yDirection = -yDirection;
    }
    Platform.runLater(() -> {
        setX(getX() + dirRate * xDirection);
        setY(getY() + dirRate * yDirection);
    });
}

}
