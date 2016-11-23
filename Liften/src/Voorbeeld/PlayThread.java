package Voorbeeld;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * Created by Pieter-Jan on 23/11/2016.
 */
public class PlayThread extends Thread {
    Controller c;
    Simulation simulation;

    public PlayThread(Controller GUIController, Simulation simulation) {
        c = GUIController;
        this.simulation = simulation;
    }

    @Override
    public void run() {
        synchronized(this){
            c.sequence.setOnFinished(event -> {
                System.out.println("\t\t GUI - Clearing sequence");
                c.sequence.getChildren().clear();
                notifyAll();
            });
            System.out.println("\t\t GUI - Playing sequence");
            c.sequence.play();
        }
    }
}
