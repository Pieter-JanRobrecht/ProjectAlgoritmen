package DataGenereren;
import java.io.IOException;
import java.io.InputStream;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class SetupData extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Event Handler Demo");
		
		try {
			FXMLLoader loader = new FXMLLoader();
			InputStream s = null;
			try{
				s = getClass().getClassLoader().getResource("Setup.fxml").openStream();
			}catch(Exception e){

			}
			Parent root = (Parent) loader.load(s);

			//Setten van enkele elementen van het hoofdscherm
			primaryStage.setTitle("Liften");
			primaryStage.setScene(new Scene(root));
			primaryStage.show();
		} catch (IOException e) {
			System.err.println("Error loading EventHandlerDemo.fxml!");
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}