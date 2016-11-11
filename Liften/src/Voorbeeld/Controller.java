package Voorbeeld;


import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Box;
import javafx.util.Duration;

import java.util.List;

public class Controller {

    @FXML
    private AnchorPane anchorPane;

    private Application application;

    @FXML
    void startSimulatie(ActionEvent event) {
        System.out.println("actie");
        List<Box> liften = application.getLiften();

        TranslateTransition tt = new TranslateTransition(Duration.millis(2000), liften.get(0));
        tt.setByY(200f);
//        tt.setCycleCount(4f);
//        tt.setAutoReverse(true);

        tt.play();
    }

    public AnchorPane getAnchorPane() {
        return anchorPane;
    }

    public void setApplication(Application application) {
        this.application = application;
    }
}
