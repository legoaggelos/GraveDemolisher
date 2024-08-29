package org.legoaggelos.util.player;

import javafx.scene.shape.Polygon;

public class RectanglePolygonFactory {
    private Polygon polygon;
    public RectanglePolygonFactory(double width, double height, double translateX, double translateY){
        polygon = new Polygon(0,0,width,0,width,height,0,height);
        polygon.setTranslateX(translateX);
        polygon.setTranslateY(translateY);
    }

    public Polygon getNewPolygon() {
        Polygon tempPolygon=new Polygon(polygon.getPoints().getFirst(),polygon.getPoints().get(1),polygon.getPoints().get(2),polygon.getPoints().get(3),polygon.getPoints().get(4),polygon.getPoints().get(5),polygon.getPoints().get(6),polygon.getPoints().get(7));
        tempPolygon.setTranslateX(polygon.getTranslateX());
        tempPolygon.setTranslateY(polygon.getTranslateY());
        return tempPolygon;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }
}
