package Voorbeeld;

import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

/**
 * Created by Pieter-Jan on 17/12/2016.
 */
public class DataController {
    private Simulation simulation;

    @FXML
    private Label aantalHop;

    @FXML
    private Label totAfhandel;

    @FXML
    private Label maxWacht;

    @FXML
    private Label totWacht;

    @FXML
    private Label totReken;

    @FXML
    private Label totTimeout;

    @FXML
    private CategoryAxis gebruikerAs;

    @FXML
    private NumberAxis wachtAs;

    @FXML
    private LineChart<String, Number> grafiek;
    private Controller controller;

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    public void showData() {
        aantalHop.setText(simulation.getAantalLiftHoppers() + " gameticks");
        totAfhandel.setText(simulation.getTotaleAfhandelTijd() + " gameticks");
        maxWacht.setText(simulation.getMaxWachtTijd() + " gameticks");
        totWacht.setText(simulation.getTotaleWachttijd() + " gameticks");
        totReken.setText(simulation.getTotaleRekenTijd() + " gameticks");
        totTimeout.setText(simulation.getAantalTimeouts() + " gameticks");

        XYChart.Series series1 = new XYChart.Series();
        for (int i = 0; i < simulation.getWachttijden().size(); i++) {
            series1.getData().add(new XYChart.Data<String, Number>(i+"", simulation.getWachttijden().get(i)));
        }
        grafiek.getData().addAll(series1);
    }
}
