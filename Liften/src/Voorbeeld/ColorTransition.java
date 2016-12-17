package Voorbeeld;

import javafx.animation.Transition;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

/**
 * Created by Pieter-Jan on 25/11/2016.
 */
public class ColorTransition extends Transition {
    private Box b;
    private Color color;

    public ColorTransition(Box c, Color color) {
        b = c;
        this.color = color;
    }

    @Override
    protected void interpolate(double frac) {
        b.setMaterial(new PhongMaterial(color));
    }
}
