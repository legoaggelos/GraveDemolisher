package org.legoaggelos.util;

import org.legoaggelos.objects.Grave;
import org.legoaggelos.util.player.RectanglePolygonFactory;

import java.util.ArrayList;
import java.util.List;

public class GraveUtil {
    //TODO make constructor and a parameterless method
    private static RectanglePolygonFactory factory;
    public static List<Grave> graveGrid(int x, int y, double translateX, double translateY, double graveWidth, double graveHeight, double graveAreaWidth, double graveAreaHeight){
        factory=new RectanglePolygonFactory(graveWidth,graveHeight,translateX,translateY);
        List<Grave> graves = new ArrayList<>();
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                graves.add(new Grave(factory.getNewPolygon(),i*graveAreaWidth+128+(graveAreaWidth/2),j*graveAreaHeight+70+(graveAreaHeight-graveHeight)));
                graves.getLast().getCharacter().setTranslateZ(0);
            }
        }
        return graves;
    }
}
