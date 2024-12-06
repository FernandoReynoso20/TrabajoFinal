package com.example.sistemadetrafico;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class Linea extends Line {

    public Linea(double startX, double startY, double endX, double endY) {
        super(startX, startY, endX, endY);
        setStyle("-fx-stroke: #ff0000; -fx-stroke-width: 4;");
    }

    public void setColor(Color color) {
        setStroke(color);
    }

    public void resetColor() {
        setStroke(Color.RED); // Color original
    }
}
