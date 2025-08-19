package org.legoaggelos.app;

import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.legoaggelos.file.FileHandler;
import org.legoaggelos.file.ScoresFileUtil;
import org.legoaggelos.objects.Grave;
import org.legoaggelos.objects.entities.Character;
import org.legoaggelos.objects.entities.player.Dot;
import org.legoaggelos.objects.entities.player.HandPosition;
import org.legoaggelos.objects.entities.player.Player;
import org.legoaggelos.objects.entities.player.PlayerCount;
import org.legoaggelos.util.*;
import org.legoaggelos.util.player.HandleCallHandler;
import org.legoaggelos.util.player.RectanglePolygonFactory;
import org.legoaggelos.time.NanoTime;
import org.legoaggelos.time.TimerTime;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.sql.Time;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


import static org.legoaggelos.file.ScoresFileUtil.areFileContentsValid;
import static org.legoaggelos.file.ScoresFileUtil.getFileContents;


public class Application extends javafx.application.Application {

    public static final Logger logger = LogManager.getLogger(Application.class);

    @Override
    public void start(Stage stage) {
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        Pane gameComponents = new Pane();
        GameStateHandler game = new GameStateHandler(stage);

        //Player player = new Player(90.0,45.0,((double) 128 /2)-10,38.0,30.0,30.0,((double) 128 /2)+5,8.0,15,45.0,((double) 128 /2)+5,38+22.5,33.0,33.0,((double) 128 /2)+50,44.0,false,Color.BEIGE,Color.RED,Color.BEIGE,Color.WHITE);
        //RectanglePolygonFactory testHitbox = new RectanglePolygonFactory(128,90,0,90);
        //Player player = new Player(63.28125,45.0,((double) 90 /2)-22.5,116.71875,21.09375,21.09375,((double) 90 /2),91.09375,12,45,((double) 90 /2)+5,116.71875+22.5,25,25,((double) 90 /2)+50,116.71875+10,false,Color.BEIGE,Color.RED,Color.BEIGE,Color.WHITE);

        //Player player = new Player(200,110,100, 105,90,90, (double) 305 /2-25,15,45,140,190,90,90,90,330,90-22.5,false,Color.BEIGE,Color.RED,Color.BEIGE,Color.WHITE);
        final int[] numbOfZeroes = new int[]{5};
        //TODONE clean up system println(right before release)
        //After 1.0
        //TODO optimize Animationtimer and stuff
        //TODONE make a generateText method to not spam setFOnt and stuff
        //TODO perfect text disappearing speed+time(AFTER 1.0)
        //TODONECANCELLED  replace counter with timer(AFTER 1.0) - tick counters work well enough
        //TODONECANCELLED use a good font(LOTS OF WORK BECAUSE OF SPACE DIFFERENCE)
        //TODO clean up everything so it doesn't have calculations(AFTER 1.0)
        logger.info("Object initialization finished.");
        logger.info("Starting AnimationTimer.");
        //TODONE fix the the player moving methods
        //TODONE adjust the timing for most things in the game
        //TODO clean handle up
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                  game.tick();
            }
        }.start();
        stage.setScene(game.getGuiHolder().getGameScene());
        stage.setFullScreen(true);
        stage.setTitle("Grave Demolisher 1.0 Initial Release");
        stage.show();
        stage.setOnCloseRequest(e -> {
            FileHandler.overwriteHighScores(game.getPlayerHighScores());
        });
        logger.info("Main game window appeared.");
    }

    static void clearList(List e) {
        e.clear();
    }
}
