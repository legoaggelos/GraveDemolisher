package org.legoaggelos.objects.entities;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.List;

import static org.legoaggelos.app.GameStateHandler.xResolutionRatio;
import static org.legoaggelos.app.GameStateHandler.yResolutionRatio;

public abstract class  Character {
    protected Polygon character;
    public Character(Polygon polygon, double translateX, double translateY) {
        this.character = polygon;
        this.character.setScaleX(polygon.getScaleX()*xResolutionRatio);
        this.character.setScaleY(polygon.getScaleY()*yResolutionRatio);
        this.character.setTranslateX(translateX*xResolutionRatio);
        this.character.setTranslateY(translateY*yResolutionRatio);
        getCharacter().setFill(Color.GRAY);
    }
    public void changePosition(double newX, double newY){
        this.character.setTranslateX(newX*xResolutionRatio);
        this.character.setTranslateY(newY*yResolutionRatio);
    }

    public ArrayList<Double> getPosition(){
        return new ArrayList<>(List.of(character.getTranslateX(),character.getTranslateY()));
    }
    public void moveX(double amount) {
        this.character.setTranslateX(this.character.getTranslateX() + amount*xResolutionRatio);
    }
    public void moveY(double amount) {
        this.character.setTranslateY(this.character.getTranslateY() + amount*yResolutionRatio);
    }
    public double getTranslateX(){
        return character.getTranslateX();
    }
    public double getTranslateY(){
        return character.getTranslateY();
    }

    public Polygon getCharacter() {
        return character;
    }
    public void setCharacter(Polygon character) {
        this.character=character;
    }

    public void setCharacter(Character character){
        this.changePosition(character.getTranslateX(), character.getTranslateY());
        setCharacter(character.getCharacter());
    }
    public void setCharacter(Polygon character, double x, double y){
        this.changePosition(x,y);
        setCharacter(character);
    }

    public boolean colliding(Character other) {
        Shape collisionArea = Shape.intersect(this.getCharacter(), other.getCharacter());
        return collisionArea.getBoundsInLocal().getWidth() != -1;
    }

    public void setTranslateX(double translateX) {
        character.setTranslateX(translateX*xResolutionRatio);
    }
    public void setTranslateY(double translateY) {
        character.setTranslateY(translateY*yResolutionRatio);
    }

}
