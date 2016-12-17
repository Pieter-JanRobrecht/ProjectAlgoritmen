package Voorbeeld;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;

public class Application extends javafx.application.Application {

    @Override
    public void start(Stage primaryStage) {

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
             viewController = loader.<Controller>getController() ;
            assert(viewController != null);

            //Link tussen controller en view
            anchorPane = viewController.getAnchorPane();
            viewController.setApplication(this);

        } catch (IOException e) {
            e.printStackTrace();
        }

        viewController.makeWorld();
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
}

