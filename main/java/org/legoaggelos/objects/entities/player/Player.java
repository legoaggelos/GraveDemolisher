package org.legoaggelos.objects.entities.player;

import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import org.legoaggelos.objects.Grave;
import org.legoaggelos.objects.entities.Character;
import org.legoaggelos.exceptions.PlayerNumberOutOfBoundsException;
import org.legoaggelos.exceptions.PlayerTwoWithoutPlayerOneException;
import org.legoaggelos.util.ChangeableBoolean;
import org.legoaggelos.util.Counter;
import org.legoaggelos.util.player.Direction;
import org.legoaggelos.util.player.RectanglePolygonFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.legoaggelos.app.Application.*;
import static org.legoaggelos.app.GameStateHandler.xResolutionRatio;
import static org.legoaggelos.app.GameStateHandler.yResolutionRatio;

public class Player extends Character {
    public static final HashMap<PlayerCount, Boolean> hasPlayerXJoined = new HashMap<>();
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
    private HashMap<PlayerCount, KeyCode[]> playerMovementKeycodes; // Array is [go up, go down, go left, go right, attack]. Both is for F12 to add/remove player 2
    private HashMap<Direction, Double> distanceToMovePerDirection;
    private PlayerCount playerIndex;
    private final Counter globalMoveCounter = new Counter(11);
    private final Counter attackingCounter = new Counter(-1);
    private final Counter moveAfterGraveRespawn = new Counter(-1);
    private final Dot playerCanMoveUtility;
    private final ChangeableBoolean isGraveInTheWay = new ChangeableBoolean(false);//in method

    public Player(double bodyHeight, double bodyWidth, double bodyTranslateX, double bodyTranslateY, double headHeight, double headWidth, double headTranslateX, double headTranslateY, double armHeight, double armWidth, double armTranslateX, double armTranslateY, double fistHeight, double fistWidth, double fistTranslateX, double fistTranslateY, boolean hidePlayerHand, double legHeight, double legWidth, double legOneTranslateX, double legOneTranslateY, double legDistance, Color initialArmColor, Color initialFistColor, Color initialHeadColor, Color initialBodyColor, Color initialLegColor) throws PlayerNumberOutOfBoundsException, PlayerTwoWithoutPlayerOneException {
        super(new Polygon(0), 0, 0);
        playerCanMoveUtility = new Dot(new RectanglePolygonFactory(bodyWidth, bodyHeight, -10000, -10000).getNewPolygon(), -10000, -10000);
        this.bodyHeight = bodyHeight;
        this.bodyWidth = bodyWidth;
        this.headHeight = headHeight;
        this.headWidth = headWidth;
        this.armHeight = armHeight;
        this.armWidth = armWidth;
        this.fistHeight = fistHeight;
        this.fistWidth = fistWidth;
        this.playerLegWidth = legWidth;
        this.playerLegHeight = legHeight;
        this.legDistance = legDistance;
        this.playerIndex = PlayerCount.NONE;
        if (hasPlayerXJoined.getOrDefault(PlayerCount.PLAYER_1, false) && hasPlayerXJoined.getOrDefault(PlayerCount.PLAYER_2, false)) {
            throw new PlayerNumberOutOfBoundsException("Invalid player count!");
        } else if (hasPlayerXJoined.getOrDefault(PlayerCount.PLAYER_1, false) && !hasPlayerXJoined.getOrDefault(PlayerCount.PLAYER_2, false)) {
            hasPlayerXJoined.put(PlayerCount.PLAYER_2, true);
            playerIndex = PlayerCount.PLAYER_2;
        } else if (!hasPlayerXJoined.getOrDefault(PlayerCount.PLAYER_1, false) && !hasPlayerXJoined.getOrDefault(PlayerCount.PLAYER_2, false)) {
            hasPlayerXJoined.put(PlayerCount.PLAYER_1, true);
            playerIndex = PlayerCount.PLAYER_1;
        } else if (!hasPlayerXJoined.getOrDefault(PlayerCount.PLAYER_1, false) && hasPlayerXJoined.getOrDefault(PlayerCount.PLAYER_2, false)) {
            throw new PlayerTwoWithoutPlayerOneException("Player 2 can not be playing without player 1!");
        }
        super.setCharacter((Polygon) null);
        playerHead = new PlayerPart(new RectanglePolygonFactory(headWidth, headHeight, headTranslateX, headTranslateY).getNewPolygon());
        playerBody = new PlayerPart(new RectanglePolygonFactory(bodyWidth, bodyHeight, bodyTranslateX, bodyTranslateY).getNewPolygon());
        //System.out.println(bodyTranslateX +" "+ bodyTranslateY);
        //System.out.println(playerBody.getTranslateX() +" "+ playerBody.getTranslateY());

        playerLegOne = new PlayerPart(new RectanglePolygonFactory(playerLegWidth, playerLegHeight, legOneTranslateX, legOneTranslateY).getNewPolygon());
        playerLegTwo = new PlayerPart(new RectanglePolygonFactory(playerLegWidth, playerLegHeight, legOneTranslateX + legDistance, legOneTranslateY).getNewPolygon());
        if (hidePlayerHand) {
            playerArm = new PlayerPart(new RectanglePolygonFactory(armWidth, armHeight, -10000, -10000).getNewPolygon());
            playerFist = new PlayerPart(new RectanglePolygonFactory(fistWidth, fistHeight, -10000, -10000).getNewPolygon());
            playerArmIdle = new PlayerPart(new RectanglePolygonFactory(armHeight, armWidth - 25, armTranslateX - 11.25, armTranslateY).getNewPolygon());
            playerFistIdle = new PlayerPart(new RectanglePolygonFactory(fistHeight, fistWidth, playerArmIdle.getTranslateX() - 11.25, playerArmIdle.getTranslateY() + armWidth - 30).getNewPolygon());
            changeHand(HandPosition.HIDE);
        } else {
            playerArm = new PlayerPart(new RectanglePolygonFactory(armWidth, armHeight, armTranslateX, armTranslateY).getNewPolygon());
            playerFist = new PlayerPart(new RectanglePolygonFactory(fistWidth, fistHeight, fistTranslateX, fistTranslateY).getNewPolygon());
            playerArmIdle = new PlayerPart(new RectanglePolygonFactory(armHeight, armWidth, -10000, -10000).getNewPolygon());
            playerFistIdle = new PlayerPart(new RectanglePolygonFactory(fistHeight, fistWidth, -10000, -10000).getNewPolygon());
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
        playerBody.getCharacter().setTranslateZ(1);
        playerFist.getCharacter().setTranslateZ(1);
        playerFistIdle.getCharacter().setTranslateZ(1);
        player = new ArrayList<>();

        isAttacking = getHandPositionFromBoolean(!hidePlayerHand);
        player.addAll(List.of(playerHead, playerBody, playerLegOne, playerLegTwo, playerArm, playerFist, playerArmIdle, playerFistIdle));
        playerMovementKeycodes = new HashMap<>();
        distanceToMovePerDirection = new HashMap<>();
        distanceToMovePerDirection.put(Direction.UP, -165D);
        distanceToMovePerDirection.put(Direction.DOWN, 165D);
        distanceToMovePerDirection.put(Direction.LEFT, -128D);
        distanceToMovePerDirection.put(Direction.RIGHT, 128D);
        //TODO potentially make this use enums for keycodes? with nested hashmaps
        playerMovementKeycodes.put(PlayerCount.PLAYER_1, new KeyCode[]{KeyCode.W, KeyCode.S, KeyCode.A, KeyCode.D, KeyCode.X});
        playerMovementKeycodes.put(PlayerCount.PLAYER_2, new KeyCode[]{KeyCode.UP, KeyCode.DOWN, KeyCode.LEFT, KeyCode.RIGHT, KeyCode.CONTROL});
        playerMovementKeycodes.put(PlayerCount.NONE, new KeyCode[]{KeyCode.F12});
    }
    public void stopAttacking() {
        this.getAttackingCounter().resetCounter();
        this.changeHand(HandPosition.HIDE);
    }
    public boolean shouldContinueAttacking() {
        return this.getAttackingCounter().getCounter() > -1;
    }
    public void startAttacking() {
        this.changeHand(HandPosition.SHOW);
        this.getAttackingCounter().increaseCounter();
    }
    public boolean shouldPlayerNotContinueAttacking() {
        return this.getAttackingCounter().getCounter() == -1 || this.getAttackingCounter().getCounter() >= 10;
    }
    public boolean canPlayerStartAttacking() {
        return this.getGlobalMoveCounter().getCounter() > 2/*2 tick cooldown before moving and attacking.*/ && this.getAttackingCounter().getCounter()/*the player it attacking for 10 ticks, this means it is not attacking, values 0..10 are mid attack*/ == -1 && this.getMoveAfterGraveRespawn().getCounter() == -1/*must not be attacking while graves are respawning*/ && !this.isAttacking()/*player shouldnt start attack if he is already attacking.*/;
    }
    public KeyCode[] getPlayerMovementKeycodes() {
        return playerMovementKeycodes.get(playerIndex);
    }
    public boolean isAttacking() {
        return getBooleanFromHandPosition(isAttacking);
    }

    public void setAttacking(boolean attacking) {
        isAttacking = getHandPositionFromBoolean(attacking);
    }

    public ArrayList<PlayerPart> getPlayer() {
        return player;
    }

    public void resetPlayerPosition() {
        this.getPlayerBody().setTranslateX(70 - (bodyWidth-playerLegWidth-legDistance*xResolutionRatio)/2.0*(1.0/xResolutionRatio));
        this.getPlayerBody().setTranslateY(50.46875 - 10.9*(1.0/yResolutionRatio) + 70);
        this.getPlayerHead().setTranslateX(((double) 128 / 2) + 17);
        this.getPlayerHead().setTranslateY(8.0 * 1.328125 + 62);
        this.changeLegPositions(70, 220);
        this.changeHand(HandPosition.HIDE, true);
        playerBody.getCharacter().toFront();
        playerArmIdle.getCharacter().toFront();
        playerFistIdle.getCharacter().toFront();
        playerArm.getCharacter().toFront();
        playerFist.getCharacter().toFront();

    }
    public boolean moveBasedOnInputs(List<Grave> graves, HashMap<KeyCode, Boolean> pressedKeys) throws InterruptedException {

        double distanceToMoveY = 0;
        double distanceToMoveX = 0;
        var playerCount = playerIndex == PlayerCount.PLAYER_1 ? PlayerCount.PLAYER_1 : PlayerCount.PLAYER_2;
        Direction direction = null;

        if (pressedKeys.getOrDefault(playerMovementKeycodes.get(playerCount)[0], false) && !pressedKeys.getOrDefault(playerMovementKeycodes.get(playerCount)[1], false)) {
            direction = Direction.UP;

        } else if (pressedKeys.getOrDefault(playerMovementKeycodes.get(playerCount)[1], false) && !pressedKeys.getOrDefault(playerMovementKeycodes.get(playerCount)[0], false)) {

            direction = Direction.DOWN;
        } else if (pressedKeys.getOrDefault(playerMovementKeycodes.get(playerCount)[2], false) && !pressedKeys.getOrDefault(playerMovementKeycodes.get(playerCount)[3], false)) {
            direction = Direction.LEFT;

        } else if (pressedKeys.getOrDefault(playerMovementKeycodes.get(playerCount)[3], false) && !pressedKeys.getOrDefault(playerMovementKeycodes.get(playerCount)[2], false)) {
            direction = Direction.RIGHT;

        }

        if (direction == Direction.UP || direction == Direction.DOWN) {
            distanceToMoveY = distanceToMovePerDirection.get(direction);

        }
        if (direction == Direction.LEFT || direction == Direction.RIGHT) {
            distanceToMoveX = distanceToMovePerDirection.get(direction);
        }
        return moveBasedOnInputs(graves, pressedKeys, distanceToMoveY, distanceToMoveX);
    }

    public ChangeableBoolean getIsGraveInTheWay() {
        return isGraveInTheWay;
    }

    public Dot getPlayerCanMoveUtility() {
        return playerCanMoveUtility;
    }

    public Counter getMoveAfterGraveRespawn() {
        return moveAfterGraveRespawn;
    }

    public Counter getAttackingCounter() {
        return attackingCounter;
    }

    public Counter getGlobalMoveCounter() {
        return globalMoveCounter;
    }

    public boolean moveBasedOnInputs(List<Grave> graves, HashMap<KeyCode, Boolean> pressedKeys, double distanceToMoveY, double distanceToMoveX) throws InterruptedException { //move based on player count(this player also)... ughh
        if (distanceToMoveY == 0 && distanceToMoveX == 0) {
            return false;
        }
        Counter playerMoveAfterGraveRespawn = new Counter(0);

        playerMoveAfterGraveRespawn.setCounter(moveAfterGraveRespawn.getCounter());


        if ((this.getPlayerHead().getTranslateY() + distanceToMoveY*yResolutionRatio < 1050*yResolutionRatio && this.getPlayerHead().getTranslateX() + distanceToMoveX*xResolutionRatio < 1920*xResolutionRatio) && (this.getPlayerHead().getTranslateY() + distanceToMoveY*yResolutionRatio >= 0 && this.getPlayerHead().getTranslateX() + distanceToMoveX*xResolutionRatio >= 0) && globalMoveCounter.getCounter() > 3 && playerMoveAfterGraveRespawn.getCounter() == -1) {


            playerCanMoveUtility.getCharacter().setTranslateX(this.getPlayerBody().getTranslateX()+distanceToMoveX*xResolutionRatio);
            playerCanMoveUtility.getCharacter().setTranslateY(this.getPlayerBody().getTranslateY()+distanceToMoveY*yResolutionRatio);

            graves/*graves input of method*/.forEach(v -> {
                if (v.colliding(playerCanMoveUtility)) {
                    isGraveInTheWay.setBool(true);//input in method
                    //System.out.println(1);
                }
            });


            playerCanMoveUtility.getCharacter().setTranslateX(this.getPlayerBody().getTranslateX() + distanceToMoveX*xResolutionRatio);
            playerCanMoveUtility.getCharacter().setTranslateY(this.getPlayerLegOne().getTranslateY() + (distanceToMoveY + 70)*yResolutionRatio);


            graves/*graves input of method*/.forEach(v -> {
                if (v.colliding(playerCanMoveUtility)) {
                    isGraveInTheWay.setBool(true);//input in method
                }
            });
            if (!isGraveInTheWay.bool()) {
                Thread.sleep(25);
                this.moveHorizontally(distanceToMoveY);
                this.moveVertically(distanceToMoveX);
                globalMoveCounter.setCounter(-1);
                this.changeHand(HandPosition.HIDE);
                attackingCounter.setCounter(-1);
                pressedKeys.put(playerMovementKeycodes.get(playerIndex)[4], false);
            }
            boolean isGraveInTheWayTemp = isGraveInTheWay.bool();
            isGraveInTheWay.setBool(false);
            playerCanMoveUtility.setTranslateX(-10000);
            playerCanMoveUtility.setTranslateY(-10000);
            return !isGraveInTheWayTemp;
        }


        return false;
    }
    public void changeHand(HandPosition position) {
        changeHand(position, false);
    }
    public void changeHand(HandPosition hideOrShow, boolean override) {
        if (hideOrShow == isAttacking && !override) {
            return;
        }
        if (hideOrShow == HandPosition.HIDE) {
            playerArmIdle.changePosition((playerBody.getTranslateX() + 20)*(1.0/xResolutionRatio), (playerBody.getTranslateY() + 10)*(1.0/yResolutionRatio)); //when in relation to other player parts, we dont want to adjust for resolution ratio, so we dont
            playerFistIdle.changePosition((playerArmIdle.getTranslateX() - 11)*(1.0/xResolutionRatio) /*(fistWidth - armWidth)/16*(1.0/xResolutionRatio)*/, (playerArmIdle.getTranslateY() )*(1.0/yResolutionRatio)+ 70); //this last one neds just the additive to not be adjusted, i dont know why, it is my fault, but it works like this
            playerArm.changePosition(-10000, -10000);
            playerFist.changePosition(-10000, -10000);
            setAttacking(false);
        }
        if (hideOrShow == HandPosition.SHOW) {
              //playerArm.changePosition(playerBody.getTranslateX()+(21)*(1.0/xResolutionRatio), (playerBody.getTranslateY() + 15.53125)*(1.0/yResolutionRatio));
              playerArm.changePosition((playerBody.getTranslateX() + 20)*(1.0/xResolutionRatio), (playerBody.getTranslateY()+20)*(1.0/yResolutionRatio));
              playerFist.changePosition((playerBody.getTranslateX() + 90)*(1.0/xResolutionRatio), (playerArm.getTranslateY() - 11.25)*(1.0/yResolutionRatio));
              playerArmIdle.changePosition(-10000, -10000);
              playerFistIdle.changePosition(-10000, -10000);
              setAttacking(true);
          }
    }

    public void moveHorizontally(double amount) {
        playerBody.moveY(amount);
        playerHead.moveY(amount);
        playerLegTwo.moveY(amount);
        playerLegOne.moveY(amount);
        if (isAttacking()) {
            playerArm.moveY(amount);
            playerFist.moveY(amount);
        } else {
            playerArmIdle.moveY(amount);
            playerFistIdle.moveY(amount);
        }
    }

    public void moveVertically(double amount) {
        playerBody.moveX(amount);
        playerHead.moveX(amount);
        playerLegTwo.moveX(amount);
        playerLegOne.moveX(amount);
        if (isAttacking()) {
            playerArm.moveX(amount);
            playerFist.moveX(amount);
        } else {
            playerArmIdle.moveX(amount);
            playerFistIdle.moveX(amount);
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

    public void changeLegPositions(double newTranslateX, double newTranslateY) {
        playerLegOne.changePosition(newTranslateX, newTranslateY);
        playerLegTwo.changePosition(newTranslateX + legDistance, newTranslateY);
    }

    public boolean getBooleanFromHandPosition(HandPosition handPosition) {
        return handPosition == HandPosition.SHOW;
    }

    public HandPosition getHandPositionFromBoolean(boolean bool) {
        if (bool) {
            return HandPosition.SHOW;
        }
        return HandPosition.HIDE;
    }
}
