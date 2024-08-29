package org.legoaggelos.objects.entities.player;

import javafx.scene.shape.Polygon;
import org.legoaggelos.objects.entities.Character;

public class PlayerPart extends Character {
    public PlayerPart(Polygon polygon) {
        super(polygon, polygon.getTranslateX(), polygon.getTranslateY());
    }
}
