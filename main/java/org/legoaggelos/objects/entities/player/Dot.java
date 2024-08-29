package org.legoaggelos.objects.entities.player;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import org.legoaggelos.objects.entities.Character;

public class Dot extends Character {
    public Dot(Polygon polygon, double x, double y) {
        super(polygon, x, y);
        //getCharacter().setFill(Color.TRANSPARENT);
    }
}
