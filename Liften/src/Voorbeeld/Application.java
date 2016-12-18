package Voorbeeld;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.io.InputStream;

public class Application extends javafx.application.Application {

    private Controller vorige;

    @Override
    public void start(Stage primaryStage) {
        reset(primaryStage);
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public void reset(Stage primaryStage) {
        AnchorPane anchorPane = null;
        Scene scene = null;
        Controller viewController=null;
        try {
            //Laden van de fxml file waarin alle gui elementen zitten
            FXMLLoader loader = new FXMLLoader();
            InputStream s = null;
            try{
                s = getClass().getClassLoader().getResource("Sample.fxml").openStream();
            }catch(Exception e){

            }
            Parent root = (Parent) loader.load(s);

            //Setten van enkele elementen van het hoofdscherm
            primaryStage.setTitle("Liften");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

            //Ophalen van de controller horende bij de view klasse
            viewController = loader.<Controller>getController();
            if(vorige != null) {
                if(vorige.getFile() != null) {
                    viewController.setFile(vorige.getFile());
                }
            }
            vorige = viewController;

            assert(viewController != null);

            //Link tussen controller en view
            anchorPane = viewController.getAnchorPane();
            viewController.setApplication(this);

            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    System.exit(0);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        viewController.makeWorld();
    }
}

