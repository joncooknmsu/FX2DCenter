//
// CenteredPlayer main app class
// - shows Loading and starting an FXML-based JavaFX program
// - most of the application's functionality is in CenterController
//

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
//import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

public class CenteredPlayer extends Application
{

Pane pane = null;
Scene scene = null;
static CenteredPlayer app;

// set app to last object created; this is currently not used but the JavaFX runtime
// creates TWO CenteredPlayer objects: on the first it runs init() and then start(), and then
// the second object is the one that actually exists when your program is running. So
// if you had stuff in this class that you needed, a static reference to this second
// object would be handy.
public CenteredPlayer()
{
    app = this;
}

@Override
public void init()
{
    // API docs say "NOTE: This method is not called on the JavaFX
    // Application Thread. An application must not construct a Scene
    // or a Stage in this method. An application may construct other
    // JavaFX objects in this method.
}

@Override
public void start(Stage primaryStage)
{
    // This was in init, but works here; seems easier, too
    FXMLLoader loader;
    java.net.URL location = getClass().getResource("centeredplayer.fxml");
    loader = new FXMLLoader(location);
    try {
        pane = (Pane) loader.load();
    } catch (Exception e) {
        System.out.println("Exception" + e);
    }
    // System.out.println("FXApp:"+this+" pane:"+pane);
    // -------- end of what used to be in init() ------------
    scene = new Scene(pane);
    // If you need the controller object, the line below works
    //CenteredPlayerController controller = (CenteredPlayerController) loader.getController();
    // I haven't tried using CSS yet, but the below might work
    // scene.getStylesheets().add("style.css");
    primaryStage.setTitle("CenteredPlayerr");
    primaryStage.setScene(scene);
    primaryStage.show();
    // System.out.println("FXApp:"+this);
}

public static void main(String[] args)
{
    Application.launch(args);
}

} // end class

// helpful sites for loading an FXML file "by hand":
// https://docs.oracle.com/javase/8/javafx/api/javafx/fxml/doc-files/introduction_to_fxml.html
// https://jenkov.com/tutorials/javafx/fxml.html#specifiying-controller-class-in-fxml
// https://docs.oracle.com/javafx/2/api/javafx/fxml/doc-files/introduction_to_fxml.html#controllers
// https://docs.oracle.com/javafx/2/api/javafx/fxml/FXMLLoader.html
// https://docs.oracle.com/javafx/2/api/javafx/application/Application.html
//
// Good JavaFX Resources:
// * http://fxexperience.com/
// * https://martinfowler.com/eaaDev/PresentationModel.html (I'm not convinced
//          this is a good design pattern, but need to look at it more closely)
// * https://stackoverflow.com/questions/32739199/javafx-software-design
// * https://edencoding.com/mvc-in-javafx/ (some things I like, some I don't)
// * https://docs.oracle.com/javafx/2/best_practices/jfxpub-best_practices.htm
//
