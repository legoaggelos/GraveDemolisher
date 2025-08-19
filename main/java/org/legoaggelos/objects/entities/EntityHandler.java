package org.legoaggelos.objects.entities;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.legoaggelos.objects.Grave;
import org.legoaggelos.objects.entities.player.HandPosition;
import org.legoaggelos.objects.entities.player.Player;
import org.legoaggelos.util.ChangeableBoolean;
import org.legoaggelos.util.Counter;
import org.legoaggelos.util.GraveUtil;

import java.util.HashMap;
import java.util.List;

import static org.legoaggelos.app.Application.logger;

public class EntityHandler {
    final Player player = new Player(109.53125, 60.0, 66, 110.46875, 47.84375, 30.0, 81, 72.625, 19.921875, 110.0, 97, 120, 43.828125, 43.828125, 181, 108.75, true, 75, 20, 70, 220, 30, Color.BEIGE, Color.RED, Color.BEIGE, Color.WHITE, Color.LIGHTBLUE);
    final Player[] players = {player, null};
    private final List<Grave> graves;
    public EntityHandler(Pane gameComponents)  {
        player.changeHand(HandPosition.HIDE);
        gameComponents.getChildren().addAll(player.getPlayer().stream().map(org.legoaggelos.objects.entities.Character::getCharacter).toList());
        gameComponents.getChildren().add(players[0].getPlayerCanMoveUtility().getCharacter());
        logger.info("Added player 1.");
        graves = GraveUtil.graveGrid(14, 6, 0, 0, 80, 135, 128, 165);
        gameComponents.getChildren().addAll(graves.stream().map(Character::getCharacter).toList());

    }
    public void resetGraves() {
        graves.clear();
        graves.addAll(GraveUtil.graveGrid(14, 6, 0, 0, 80, 135, 128, 165));
        graves.stream().map(org.legoaggelos.objects.entities.Character::getCharacter).forEach(Node::toBack);
    }
    public void hidePlayerHands() {
        player.changeHand(HandPosition.HIDE);
        if (isPlayer2Playing()) {
            getPlayer2().changeHand(HandPosition.HIDE);
        }
    }
    public boolean isPlayer2Playing() {
        return players[1] != null;
    }
    public Player createPlayer2() {
        players[1] = new Player(109.53125, 60.0, 66, 110.46875, 48.84375, 29.0, 81, 72.625, 19.921875, 110.0, 97, 120, 43.828125, 43.828125, 181, 108.75, true, 75, 20, 70, 220, 30, Color.BEIGE, Color.RED, Color.GREEN, Color.WHITE, Color.LIGHTBLUE);
        return players[1];
    }
    public Player getPlayer1() {
        return players[0];
    }

    public Player getPlayer2() {
        return players[1];
    }
    public void nullPlayer2() {
        players[1] = null;
    }
    public Player[] getPlayers() {
        return players;
    }

    public List<Grave> getGraves() {
        return graves;
    }
    public Player[] getNonNullPlayers(){
        if (players[1] == null) {
            return new Player[]{players[0]};
        }
        return players;
    }
    public void tickPlayerMovement(HashMap<KeyCode, Boolean> pressedKeys) throws InterruptedException {
        for (Player player : getPlayers()) {
            if (player != null) {
                player.moveBasedOnInputs(graves, pressedKeys);
            }
        }
    }
    public boolean gravesEmpty(){
        return graves.isEmpty();
    }
    public void resetPlayers() {
        for (Player player : getNonNullPlayers()) {
            player.resetPlayerPosition();
        }
    }
}
