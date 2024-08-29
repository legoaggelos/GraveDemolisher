package org.legoaggelos.objects.entities.player;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import org.legoaggelos.objects.entities.Character;
import org.legoaggelos.exceptions.PlayerNumberOutOfBoundsException;
import org.legoaggelos.exceptions.PlayerTwoWithoutPlayerOneException;
import org.legoaggelos.util.player.RectanglePolygonFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Player extends Character {
    public static final HashMap<PlayerCount, Boolean> hasPlayerXJoined=new HashMap<>();
    private final PlayerPart playerHead;
    private final PlayerPart playerBody;
    private final PlayerPart playerArm;
    private final PlayerPart playerFist;
    private final PlayerPart playerArmIdle;
    private final PlayerPart playerFistIdle;
    private final PlayerPart playerLegOne;
    private final PlayerPart playerLegTwo;
    private final ArrayList<PlayerPart> player;
    private double bodyHeight;
    private double bodyWidth;
    private double headHeight;
    private double headWidth;
    private double armWidth;
    private double armHeight;
    private double fistHeight;
    private double fistWidth;
    private final double playerLegHeight;
    private final double playerLegWidth;
    private final double legDistance;
    private HandPosition isAttacking;
    public Player(double bodyHeight, double bodyWidth, double bodyTranslateX, double bodyTranslateY,double headHeight, double headWidth, double headTranslateX, double headTranslateY,double armHeight, double armWidth, double armTranslateX, double armTranslateY,double fistHeight, double fistWidth, double fistTranslateX, double fistTranslateY, boolean hidePlayerHand, double legHeight, double legWidth, double legOneTranslateX, double legOneTranslateY, double legDistance, Color initialArmColor, Color initialFistColor, Color initialHeadColor, Color initialBodyColor, Color initialLegColor) throws PlayerNumberOutOfBoundsException,PlayerTwoWithoutPlayerOneException{
        super(new Polygon(0),0,0);
        this.bodyHeight=bodyHeight;
        this.bodyWidth=bodyWidth;
        this.headHeight=headHeight;
        this.headWidth=headWidth;
        this.armHeight=armHeight;
        this.armWidth=armWidth;
        this.fistHeight=fistHeight;
        this.fistWidth=fistWidth;
        this.playerLegWidth=legWidth;
        this.playerLegHeight=legHeight;
        this.legDistance=legDistance;
        if(hasPlayerXJoined.getOrDefault(PlayerCount.PLAYER_1,false)&&hasPlayerXJoined.getOrDefault(PlayerCount.PLAYER_2,false)){
            throw new PlayerNumberOutOfBoundsException("Invalid player count!");
        } else if (hasPlayerXJoined.getOrDefault(PlayerCount.PLAYER_1,false)&&!hasPlayerXJoined.getOrDefault(PlayerCount.PLAYER_2,false)) {
            hasPlayerXJoined.put(PlayerCount.PLAYER_2,true);
        } else if(!hasPlayerXJoined.getOrDefault(PlayerCount.PLAYER_1,false)&&!hasPlayerXJoined.getOrDefault(PlayerCount.PLAYER_2,false)){
            hasPlayerXJoined.put(PlayerCount.PLAYER_1,true);
        } else if(!hasPlayerXJoined.getOrDefault(PlayerCount.PLAYER_1,false)&&hasPlayerXJoined.getOrDefault(PlayerCount.PLAYER_2,false)){
            throw new PlayerTwoWithoutPlayerOneException("Player 2 can not be playing without player 1!");
        }
        super.setCharacter((Polygon) null);
        playerHead=new PlayerPart(new RectanglePolygonFactory(headWidth,headHeight,headTranslateX,headTranslateY).getNewPolygon());
        playerBody=new PlayerPart(new RectanglePolygonFactory(bodyWidth,bodyHeight,bodyTranslateX,bodyTranslateY).getNewPolygon());
        playerLegOne=new PlayerPart(new RectanglePolygonFactory(playerLegWidth,playerLegHeight,legOneTranslateX,legOneTranslateY).getNewPolygon());
        playerLegTwo=new PlayerPart(new RectanglePolygonFactory(playerLegWidth,playerLegHeight,legOneTranslateX+legDistance,legOneTranslateY).getNewPolygon());
        if(hidePlayerHand){
            playerArm=new PlayerPart(new RectanglePolygonFactory(armWidth,armHeight,-10000,-10000).getNewPolygon());
            playerFist=new PlayerPart(new RectanglePolygonFactory(fistWidth,fistHeight,-10000,-10000).getNewPolygon());
            playerArmIdle=new PlayerPart(new RectanglePolygonFactory(armHeight,armWidth-25,armTranslateX-11.25,armTranslateY).getNewPolygon());
            playerFistIdle=new PlayerPart(new RectanglePolygonFactory(fistHeight,fistWidth,playerArmIdle.getTranslateX()-11.25, playerArmIdle.getTranslateY()+armWidth-30).getNewPolygon());
            changeHand(HandPosition.HIDE);
        } else{
            playerArm=new PlayerPart(new RectanglePolygonFactory(armWidth,armHeight,armTranslateX,armTranslateY).getNewPolygon());
            playerFist=new PlayerPart(new RectanglePolygonFactory(fistWidth,fistHeight,fistTranslateX,fistTranslateY).getNewPolygon());
            playerArmIdle=new PlayerPart(new RectanglePolygonFactory(armHeight,armWidth,-10000,-10000).getNewPolygon());
            playerFistIdle=new PlayerPart(new RectanglePolygonFactory(fistHeight,fistWidth,-10000,-10000).getNewPolygon());
            changeHand(HandPosition.SHOW);
        }
        playerArm.getCharacter().setFill(initialArmColor);
        playerFist.getCharacter().setFill(initialFistColor);
        playerBody.getCharacter().setFill(initialBodyColor);
        playerHead.getCharacter().setFill(initialHeadColor);
        playerLegOne.getCharacter().setFill(initialLegColor);
        playerLegTwo.getCharacter().setFill(initialLegColor);
        playerFistIdle.getCharacter().setFill(initialFistColor);
        playerArmIdle.getCharacter().setFill(initialArmColor);
        playerFist.getCharacter().setTranslateZ(1);
        playerFistIdle.getCharacter().setTranslateZ(1);
        player=new ArrayList<>();
        isAttacking=getHandPositionFromBoolean(!hidePlayerHand);
        player.addAll(List.of(playerHead,playerBody,playerLegOne,playerLegTwo,playerArm,playerFist,playerArmIdle,playerFistIdle));
    }


    public boolean isAttacking() {
        return getBooleanFromHandPosition(isAttacking);
    }

    public void setAttacking(boolean attacking) {
        isAttacking=getHandPositionFromBoolean(attacking);
    }

    public ArrayList<PlayerPart> getPlayer() {
        return player;
    }

    public void changeHand(HandPosition hideOrShow){
        if(hideOrShow==HandPosition.HIDE){
            playerArmIdle.changePosition(playerBody.getTranslateX()+97-66-15, playerBody.getTranslateY()+120-110.46875);
            playerFistIdle.changePosition(playerArmIdle.getTranslateX()-11.25, playerArmIdle.getTranslateY()+armWidth-30);
            playerArm.changePosition(-10000,-10000);
            playerFist.changePosition(-10000,-10000);
            setAttacking(false);
        }
        if(hideOrShow==HandPosition.SHOW){
            playerArm.changePosition(playerBody.getTranslateX()+31, playerBody.getTranslateY()+9.53125);
            playerFist.changePosition(playerBody.getTranslateX()+105,playerArm.getTranslateY()-11.25);
            playerArmIdle.changePosition(-10000,-10000);
            playerFistIdle.changePosition(-10000,-10000);
            setAttacking(true);
        }
    }
    public void moveHorizontally(double amount){
        playerBody.setTranslateY(playerBody.getTranslateY()+amount);
        playerHead.setTranslateY(playerHead.getTranslateY()+amount);
        playerLegTwo.setTranslateY(playerLegTwo.getTranslateY()+amount);
        playerLegOne.setTranslateY(playerLegOne.getTranslateY()+amount);
        if(isAttacking()){
            playerArm.setTranslateY(playerArm.getTranslateY()+amount);
            playerFist.setTranslateY(playerFist.getTranslateY()+amount);
        } else{
            playerArmIdle.setTranslateY(playerArmIdle.getTranslateY()+amount);
            playerFistIdle.setTranslateY(playerFistIdle.getTranslateY()+amount);
        }
    }
    public void moveVertically(double amount){
        playerBody.setTranslateX(playerBody.getTranslateX()+amount);
        playerHead.setTranslateX(playerHead.getTranslateX()+amount);
        playerLegTwo.setTranslateX(playerLegTwo.getTranslateX()+amount);
        playerLegOne.setTranslateX(playerLegOne.getTranslateX()+amount);
        if(isAttacking()){
            playerArm.setTranslateX(playerArm.getTranslateX()+amount);
            playerFist.setTranslateX(playerFist.getTranslateX()+amount);
        } else{
            playerArmIdle.setTranslateX(playerArmIdle.getTranslateX()+amount);
            playerFistIdle.setTranslateX(playerFistIdle.getTranslateX()+amount);
        }
    }
    public PlayerPart getPlayerHead() {
        return playerHead;
    }

    public PlayerPart getPlayerBody() {
        return playerBody;
    }

    public PlayerPart getPlayerArm() {
        return playerArm;
    }

    public PlayerPart getPlayerFist() {
        return playerFist;
    }

    public PlayerPart getPlayerArmIdle() {
        return playerArmIdle;
    }

    public double getFistHeight() {
        return fistHeight;
    }

    public void setFistHeight(double fistHeight) {
        this.fistHeight = fistHeight;
    }

    public double getBodyHeight() {
        return bodyHeight;
    }

    public void setBodyHeight(double bodyHeight) {
        this.bodyHeight = bodyHeight;
    }

    public PlayerPart getPlayerFistIdle() {
        return playerFistIdle;
    }

    public double getBodyWidth() {
        return bodyWidth;
    }

    public void setBodyWidth(double bodyWidth) {
        this.bodyWidth = bodyWidth;
    }

    public double getHeadHeight() {
        return headHeight;
    }

    public void setHeadHeight(double headHeight) {
        this.headHeight = headHeight;
    }

    public double getHeadWidth() {
        return headWidth;
    }

    public void setHeadWidth(double headWidth) {
        this.headWidth = headWidth;
    }

    public double getArmWidth() {
        return armWidth;
    }

    public void setArmWidth(double armWidth) {
        this.armWidth = armWidth;
    }

    public double getArmHeight() {
        return armHeight;
    }

    public void setArmHeight(double armHeight) {
        this.armHeight = armHeight;
    }

    public double getFistWidth() {
        return fistWidth;
    }

    public void setFistWidth(double fistWidth) {
        this.fistWidth = fistWidth;
    }

    public PlayerPart getPlayerLegOne() {
        return playerLegOne;
    }
    public PlayerPart getPlayerLegTwo() {
        return playerLegTwo;
    }
    public void changeLegPositions(double newTranslateX, double newTranslateY){
        playerLegOne.changePosition(newTranslateX,newTranslateY);
        playerLegTwo.changePosition(newTranslateX+legDistance,newTranslateY);
    }
    public boolean getBooleanFromHandPosition(HandPosition handPosition){
        return handPosition == HandPosition.SHOW;
    }
    public HandPosition getHandPositionFromBoolean(boolean bool){
        if(bool){
            return HandPosition.SHOW;
        }
        return HandPosition.HIDE;
    }
}
