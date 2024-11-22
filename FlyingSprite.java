//
// FlyingSprite: each object represents an animated "flying" image in
// the "game"
//

//import javafx.event.Event;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.geometry.Rectangle2D;

public class FlyingSprite extends ImageView
{

private int xDirection = 1;
private int yDirection = 1;
private int dirRate = 2;
private Pane area;
private Image sprite;
private int width;
private int height;
private Rectangle2D spriteRects[];
private int startSpriteX, startSpriteY;
private int widthSpriteX, heightSpriteY;
private int curSpriteIndex;
private int spriteSpeed;

public FlyingSprite(Pane gamePane, int x, int y, int w, int h, String spriteFile)
{
    sprite = new Image(spriteFile);
    setImage(sprite);
    setFitWidth(w);
    setPreserveRatio(true);
    setSmooth(true);
    setCache(true);
    area = gamePane;
    width = w;
    height = h;
    Platform.runLater(() -> {
        setX(x);
        setY(y);
    });
}

public FlyingSprite(Pane gamePane, int x, int y, int w, int h, String spriteFile, int sx, int sy, int ws, int hs, int num)
{
    sprite = new Image(spriteFile);
    setImage(sprite);
    setSmooth(true);
    area = gamePane;
    width = w;
    height = h;
    startSpriteX = sx; 
    startSpriteY = sy;
    widthSpriteX = ws;
    heightSpriteY = hs;
    spriteSpeed = 0;
    spriteRects = new Rectangle2D[num];
    for (int i=0; i < num; i++) {
        spriteRects[i] = new Rectangle2D(startSpriteX+(i*widthSpriteX), startSpriteY, widthSpriteX, heightSpriteY);
    }
    curSpriteIndex = 0;
    setViewport(spriteRects[curSpriteIndex]);
    Platform.runLater(() -> {
        setX(x);
        setY(y);
    });
}

// update the box's position; x,y is upper left corner on a box
// so we need to add its size when checking the right and bottom
// bounds
public void move()
{
    if (spriteRects != null) {
        if (spriteSpeed == 4) {
            curSpriteIndex++;
            if (curSpriteIndex >= spriteRects.length)
                curSpriteIndex = 0;
            setViewport(spriteRects[curSpriteIndex]);
        }
        spriteSpeed = (spriteSpeed+1)%5;
    }
    if (getX() + width > area.getWidth() || getX() < 0) {
        xDirection = -xDirection;
    }
    if (getY() + height > area.getHeight() || getY() < 0) {
        yDirection = -yDirection;
    }
    Platform.runLater(() -> {
        setX(getX() + dirRate * xDirection);
        setY(getY() + dirRate * yDirection);
    });
}

}
