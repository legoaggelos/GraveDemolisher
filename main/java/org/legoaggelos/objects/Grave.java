package org.legoaggelos.objects;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import org.legoaggelos.objects.entities.Character;

public class Grave extends Character {
    public Grave(Polygon polygon, double x, double y) {
        super(polygon, x, y);
        polygon.setFill(Color.rgb(41,41,41));
    }
}
