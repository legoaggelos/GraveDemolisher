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
import org.legoaggelos.file.ScoresFileUtil;
import org.legoaggelos.objects.Grave;
import org.legoaggelos.objects.entities.Character;
import org.legoaggelos.objects.entities.player.Dot;
import org.legoaggelos.objects.entities.player.HandPosition;
import org.legoaggelos.objects.entities.player.Player;
import org.legoaggelos.objects.entities.player.PlayerCount;
import org.legoaggelos.util.*;
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
    private final Logger logger = LogManager.getLogger(Application.class);

    @Override
    public void start(Stage stage) {
        HashMap<Integer, Difficulty> getDifficultyFromTime = new HashMap<>();
        getDifficultyFromTime.put(60, Difficulty.EASY);
        getDifficultyFromTime.put(40, Difficulty.MEDIUM);
        getDifficultyFromTime.put(30, Difficulty.HARD);
        getDifficultyFromTime.put(20, Difficulty.IMPOSSIBLE);
        getDifficultyFromTime.put(600, Difficulty.FREEPLAY);
        final ArrayList<Integer> playerHighScores = new ArrayList<>(List.of(0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        Path scoresFile = Paths.get(ScoresFileUtil.determinePath().toString());
        try {
            File directory = new File(scoresFile.toString().replace("scores.csv", ""));
            directory.mkdirs();
            if (scoresFile.toFile().createNewFile()) {
                logger.info("scores.csv file created");
                Files.write(scoresFile, ("0, 0, 0, 0, 0 ,0, 0, 0, 0, 0").getBytes());
            } else {
                logger.info("scores.csv file exists. Trying to read from it.");
                if (areFileContentsValid(scoresFile)) {
                    playerHighScores.clear();
                    playerHighScores.addAll(Arrays.stream(getFileContents(scoresFile).split(",")).map(Integer::parseInt).toList());
                } else {
                    if (!scoresFile.toFile().delete()) {
                        throw new IOException("File could not be deleted");
                    }
                    logger.info("Deleted file. Recreating it.");
                    scoresFile.toFile().createNewFile();
                    Files.write(scoresFile, (("0, 0, 0, 0, 0 ,0, 0, 0, 0, 0").getBytes()));
                }
            }
        } catch (IOException ioException) {
            logger.error("IOException caught when doing initial file procedure. Game will continue to work, but high scores may be unexpected.", ioException);
        }

        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        Pane gameComponents = new Pane();
        TimerTime time = new TimerTime(new Time(0, 0, 0).toLocalTime());
        Text timer = new Text((double) 1920 / 2 - 120, 60, time.toString());
        //Player player = new Player(90.0,45.0,((double) 128 /2)-10,38.0,30.0,30.0,((double) 128 /2)+5,8.0,15,45.0,((double) 128 /2)+5,38+22.5,33.0,33.0,((double) 128 /2)+50,44.0,false,Color.BEIGE,Color.RED,Color.BEIGE,Color.WHITE);
        //RectanglePolygonFactory testHitbox = new RectanglePolygonFactory(128,90,0,90);
        //Player player = new Player(63.28125,45.0,((double) 90 /2)-22.5,116.71875,21.09375,21.09375,((double) 90 /2),91.09375,12,45,((double) 90 /2)+5,116.71875+22.5,25,25,((double) 90 /2)+50,116.71875+10,false,Color.BEIGE,Color.RED,Color.BEIGE,Color.WHITE);

        //Player player = new Player(200,110,100, 105,90,90, (double) 305 /2-25,15,45,140,190,90,90,90,330,90-22.5,false,Color.BEIGE,Color.RED,Color.BEIGE,Color.WHITE);
        final int[] numbOfZeroes = new int[]{5};
        //TODO clean up system println(right before release)
        //After 1.0
        //TODO optimize Animationtimer and stuff
        //TODO make a generateText method to not spam setFOnt and stuff
        //TODO perfect text disappearing speed+time(AFTER 1.0)
        //TODO replace counter with timer(AFTER 1.0)
        //TODO use a good font(LOTS OF WORK BECAUSE OF SPACE DIFFERENCE)
        //TODO clean up everything so it doesn't have calculations(AFTER 1.0)
        AtomicInteger timeInSeconds = new AtomicInteger(60);
        VBox optionsVBox = new VBox();
        optionsVBox.setAlignment(Pos.TOP_CENTER);
        VBox mainMenuComponents = new VBox();
        VBox howToPlay = new VBox();
        howToPlay.setAlignment(Pos.TOP_CENTER);
        Text howToPlayTitle = new Text("How To Play");
        howToPlayTitle.setFont(Font.font(75));
        howToPlayTitle.setFill(Color.GRAY);
        howToPlayTitle.setTranslateY(howToPlayTitle.getTranslateY() - 980);
        Text howToPlayText = new Text("""
                 Survival:
                 There are waves of evil graves in front of you.\s
                 Your mission is to demolish all the graves in time.
                 If you take too much time to clear a wave, you lose.
                 Your score increases every time you demolish a grave
                 or clear a wave.
                 There are 4 difficulties:\s
                 Easy, you have 1 minute to clear a wave,
                 Medium, you have 40 seconds to clear a wave,
                 Hard, you have 30 seconds to clear a wave
                 Impossible, you have 20 seconds to clear a wave
                \s
                 Freeplay:
                 You have 10 minutes to clear a wave.
                 Your only goal is to get as high of a goal as possible.
                \s""");
        howToPlayText.setFont(Font.font(45));
        howToPlayText.setFill(Color.GRAY);
        howToPlayText.setTranslateY(howToPlayText.getTranslateY() + 50);
        Text backFromHowToPlay = new Text("Back");
        backFromHowToPlay.setFont(Font.font(75));
        backFromHowToPlay.setFill(Color.GRAY);
        backFromHowToPlay.setTranslateY(howToPlayTitle.getTranslateY() + 870);
        backFromHowToPlay.setOnMouseClicked(e -> stage.getScene().setRoot(optionsVBox));
        howToPlay.getChildren().addAll(howToPlayText, howToPlayTitle, backFromHowToPlay);
        VBox controls = new VBox();
        controls.setAlignment(Pos.TOP_CENTER);
        Text controlsTitle = new Text("Controls");
        controlsTitle.setFont(Font.font(75));
        controlsTitle.setFill(Color.GRAY);
        controlsTitle.setTranslateY(controlsTitle.getTranslateY() - 850);
        Text controlsText = new Text("""
                 Player 1
                 W: Go Up
                 S: Go Down
                 A: Go Left
                 D: Go Right
                 X: Attack
                 Player 2
                 Up Arrow: Go Up
                 Down Arrow: Go Down
                 Left Arrow: Go Left
                 Right Arrow: Go Right
                 Control(Left or Right): Attack
                 F12: Add Or Remove Player 2
                \s""");
        controlsText.setFont(Font.font(50));
        controlsText.setFill(Color.GRAY);
        controlsText.setTranslateY(controlsText.getTranslateY() + 100);
        Text backFromControls = new Text("Back");
        backFromControls.setFont(Font.font(75));
        backFromControls.setFill(Color.GRAY);
        backFromControls.setTranslateY(controlsTitle.getTranslateY() + 770);
        backFromControls.setOnMouseClicked(e -> stage.getScene().setRoot(optionsVBox));
        controls.getChildren().addAll(controlsText, controlsTitle, backFromControls);
        Text backFromOptions = new Text("Back");
        backFromOptions.setFont(Font.font(75));
        backFromOptions.setFill(Color.GRAY);
        backFromOptions.setTranslateY(650);
        backFromOptions.setOnMouseClicked(e -> stage.getScene().setRoot(mainMenuComponents));
        Text guide = new Text("How To Play");
        guide.setFont(Font.font(75));
        guide.setFill(Color.GRAY);
        guide.setTranslateY(guide.getTranslateY() + 300);
        guide.setOnMouseClicked(e -> stage.getScene().setRoot(howToPlay));
        mainMenuComponents.setAlignment(Pos.TOP_CENTER);
        Text title = new Text("GRAVE DEMOLISHER");
        title.setFont(Font.font(150));
        title.setFill(Color.GRAY);
        title.setTranslateY(title.getTranslateY() + 100);
        Text playSurvival = new Text("Play Survival");
        playSurvival.setFont(Font.font(75));
        playSurvival.setFill(Color.GRAY);
        playSurvival.setTranslateY(playSurvival.getTranslateY() + 300);
        playSurvival.setTranslateX(playSurvival.getTranslateX() - 10);
        Text playFreeplay = new Text("Freeplay");
        playFreeplay.setFont(Font.font(75));
        playFreeplay.setFill(Color.GRAY);
        playFreeplay.setTranslateY(playFreeplay.getTranslateY() + 350);
        playFreeplay.setTranslateX(playFreeplay.getTranslateX() - 10);
        Text options = new Text("Options");
        options.setFont(Font.font(75));
        options.setFill(Color.GRAY);
        options.setTranslateY(options.getTranslateY() + 400);
        options.setTranslateX(options.getTranslateX() - 10);
        options.setOnMouseClicked(e -> stage.getScene().setRoot(optionsVBox));
        Text quit = new Text("Quit");
        quit.setFont(Font.font(75));
        quit.setFill(Color.GRAY);
        quit.setTranslateY(quit.getTranslateY() + 450);
        quit.setTranslateX(quit.getTranslateX() - 10);
        quit.setOnMouseClicked(e -> {
            try {
                if (!scoresFile.toFile().delete()) {
                    throw new IOException("File could not be deleted");
                }
                logger.info("Deleted file. Recreating it.");
                scoresFile.toFile().createNewFile();
                Files.write(scoresFile, ((ArrayListUtils.toString(playerHighScores)).getBytes()));
            } catch(IOException exception){
                logger.error("Unable to save high scores",exception);
            }
            stage.close();
        });
        VBox creditsVBox = new VBox();
        Text creditsTitle = new Text("Credits");
        creditsTitle.setFont(Font.font(75));
        creditsTitle.setFill(Color.GRAY);
        creditsTitle.setTranslateY(creditsTitle.getTranslateY() + 10);
        Text creditsLegoaggelos = new Text("legoaggelos - Coding, texturing, testing");
        creditsLegoaggelos.setFont(Font.font(50));
        creditsLegoaggelos.setFill(Color.GRAY);
        creditsLegoaggelos.setTranslateY(creditsLegoaggelos.getTranslateY() + 10);
        Text creditsJedElinoffScotthomas = new Text("Jed Elinoff, Scott Thomas - Making the show that inspired this game(RC9GN)");
        creditsJedElinoffScotthomas.setFont(Font.font(50));
        creditsJedElinoffScotthomas.setFill(Color.GRAY);
        creditsJedElinoffScotthomas.setTranslateY(creditsJedElinoffScotthomas.getTranslateY() + 10);
        creditsVBox.setAlignment(Pos.TOP_CENTER);
        Text credits = new Text("Credits");
        credits.setFont(Font.font(75));
        credits.setFill(Color.GRAY);
        credits.setTranslateY(credits.getTranslateY() + 350);
        credits.setOnMouseClicked(e -> stage.getScene().setRoot(creditsVBox));
        Text gotoControls = new Text("Controls");
        gotoControls.setFont(Font.font(75));
        gotoControls.setFill(Color.GRAY);
        gotoControls.setTranslateY(gotoControls.getTranslateY() + 400);
        gotoControls.setOnMouseClicked(e -> stage.getScene().setRoot(controls));
        Text backFromCredits = new Text("Back");
        backFromCredits.setFont(Font.font(75));
        backFromCredits.setFill(Color.GRAY);
        backFromCredits.setTranslateY(750);
        backFromCredits.setOnMouseClicked(e -> stage.getScene().setRoot(optionsVBox));

        Text backFromSelection = new Text("Back");
        backFromSelection.setFont(Font.font(75));
        backFromSelection.setFill(Color.GRAY);
        backFromSelection.setTranslateY(200);
        backFromSelection.setOnMouseClicked(e -> stage.getScene().setRoot(mainMenuComponents));
        VBox escapeMenu = new VBox();
        Text resume = new Text("Resume");
        resume.setFont(Font.font(75));
        resume.setFill(Color.GRAY);
        resume.setOnMouseClicked(e -> gameComponents.getChildren().remove(escapeMenu));
        Text gotoMainMenu = new Text("Main Menu");
        gotoMainMenu.setFont(Font.font(75));
        gotoMainMenu.setFill(Color.GRAY);

        Text quitGame = new Text("Quit Game");
        quitGame.setFont(Font.font(75));
        quitGame.setFill(Color.GRAY);
        quitGame.setOnMouseClicked(e -> {
            gameComponents.getChildren().remove(escapeMenu);
            try {
                if (!scoresFile.toFile().delete()) {
                    throw new IOException("File could not be deleted");
                }
                logger.info("Deleted file. Recreating it.");
                scoresFile.toFile().createNewFile();
                Files.write(scoresFile, ((ArrayListUtils.toString(playerHighScores)).getBytes()));
            } catch(IOException exception){
                logger.error("Unable to save high scores",exception);
            }
            stage.close();
        });
        escapeMenu.setAlignment(Pos.CENTER);
        escapeMenu.setTranslateY(300);
        escapeMenu.setTranslateX(770);
        escapeMenu.getChildren().addAll(resume, gotoMainMenu, quitGame);

        final double[] timeBeforeLastEscape = {1};
        mainMenuComponents.getChildren().addAll(title, playSurvival, playFreeplay, options, quit);
        final Player player = new Player(109.53125, 60.0, 66, 110.46875, 47.84375, 30.0, 81, 72.625, 19.921875, 110.0, 97, 120, 43.828125, 43.828125, 181, 108.75, true, 75, 20, 70, 220, 30, Color.BEIGE, Color.RED, Color.BEIGE, Color.WHITE, Color.LIGHTBLUE);
        final Player[] players = {player, null};
        player.changeHand(HandPosition.HIDE);
        gameComponents.getChildren().addAll(player.getPlayer().stream().map(Character::getCharacter).toList());
        logger.info("Added player 1.");
        List<Grave> graves = GraveUtil.graveGrid(14, 6, 0, 0, 80, 135, 128, 165);
        gameComponents.getChildren().addAll(graves.stream().map(Character::getCharacter).toList());
        Scene gameScene = new Scene(mainMenuComponents, 1920, 1080);
        gameScene.setFill(Color.rgb(38, 17, 0));
        HashMap<KeyCode, Boolean> pressedKeys = new HashMap<>();
        gameScene.setOnKeyPressed(event -> pressedKeys.put(event.getCode(), Boolean.TRUE));
        gameScene.setOnKeyReleased(event -> pressedKeys.put(event.getCode(), Boolean.FALSE));
        Counter globalMoveCounter = new Counter(11);
        Counter attackingCounter = new Counter(-1);
        Counter globalMoveCounterP2 = new Counter(11);
        Counter attackingCounterP2 = new Counter(-1);
        Counter scoreLogCounter = new Counter();
        Dot playerCanMoveUtility = new Dot(new RectanglePolygonFactory(1, 1, -10000, -10000).getNewPolygon(), -10000, -10000);
        gameComponents.getChildren().add(playerCanMoveUtility.getCharacter());
        Dot playerTwoCanMoveUtility = new Dot(new RectanglePolygonFactory(1, 1, -10000, -10000).getNewPolygon(), -10000, -10000);
        gameComponents.getChildren().add(playerTwoCanMoveUtility.getCharacter());
        Counter emptyGravesCounter = new Counter();
        Counter moveAfterGraveRespawn = new Counter(-1);
        Counter moveAfterGraveRespawnP2 = new Counter(-1);
        Text graveRespawning = new Text((double) 1920 / 2 - 350, 60, "Graves Respawning...");
        graveRespawning.setFill(Color.rgb(0, 77, 0));
        graveRespawning.setFont(Font.font(80));
        Text graveRespawned = new Text((double) 1920 / 2 - 350, 60, "Graves Respawned!");
        graveRespawned.setFill(Color.rgb(0, 77, 0));
        graveRespawned.setFont(Font.font(80));
        Text score = new Text(0, 60, "Score: 00000");
        score.setFill(Color.rgb(0, 77, 0));
        score.setFont(Font.font(70));
        Text highScore = new Text(1300, 60, "High Score: 00000");
        highScore.setFill(Color.rgb(0, 77, 0));
        highScore.setFont(Font.font(60));
        Text bonus = new Text(400, 22, "");
        bonus.setFill(Color.rgb(0, 77, 0));
        bonus.setFont(Font.font(28));
        Text scoreLog = new Text(200, 77, "+1(Grave Demolished)");
        scoreLog.setFill(Color.rgb(0, 77, 0));
        scoreLog.setFont(Font.font(17));
        Text scoreLogClear = new Text(200, 92, "+100(Wave Cleared)");
        scoreLogClear.setFill(Color.rgb(0, 77, 0));
        scoreLogClear.setFont(Font.font(17));
        Text scoreLogBonus = new Text(200, 102, "+6(Bonus)");
        scoreLogBonus.setFill(Color.rgb(0, 77, 0));
        scoreLogBonus.setFont(Font.font(17));

        NanoTime timeInBetweenHandleCalls = new NanoTime(0);
        timer.setFill(Color.rgb(37, 139, 0));
        timer.setFont(Font.font(80));
        Text waveClearedText = new Text((double) 1920 / 2 - 550, (double) 1080 / 2 - 60, "WAVE CLEARED!");
        waveClearedText.setFill(Color.rgb(204, 255, 204));
        waveClearedText.setFont(Font.font(175));
        SecureRandom random = new SecureRandom();
        Text youLost = new Text((double) 1920 / 2 - 350, (double) 1080 / 2 - 180, "You Lost!");
        Text noTime = new Text((double) 1920 / 2 - 550, (double) 1080 / 2 - 60, "You ran out of time!");
        Text scoreResetRetry = new Text((double) 1920 / 2 - 525, (double) 1080 / 2 + 60, "Score Reset! Try again!");
        youLost.setFill(Color.rgb(153, 0, 0));
        youLost.setFont(Font.font(175));
        noTime.setFill(Color.rgb(153, 0, 0));
        noTime.setFont(Font.font(150));
        scoreResetRetry.setFont(Font.font(120));
        scoreResetRetry.setFill(Color.rgb(140, 0, 0));

        Text youWon = new Text((double) 1920 / 2 - 725, (double) 1080 / 2 - 280, "You Beat The Game!");
        Text noRanTime = new Text((double) 1920 / 2 - 825, (double) 1080 / 2 - 60, "You reached max score without \n       ever running out of time!");
        Text beatingTheGame = new Text((double) 1920 / 2 - 825, (double) 1080 / 2 + 240, "Thank you for playing the game!\n       Press R to restart!");
        youWon.setFill(Color.rgb(102, 255, 102));
        youWon.setFont(Font.font(175));
        noRanTime.setFill(Color.rgb(179, 89, 0));
        noRanTime.setFont(Font.font(125));
        beatingTheGame.setFont(Font.font(125));
        beatingTheGame.setFill(Color.rgb(179, 89, 0));

        gameComponents.getChildren().addAll(score, timer, highScore);
        ChangeableBoolean isGameBeaten = new ChangeableBoolean(false);
        Counter scoreFadeCounter = new Counter();
        Counter playerTwoLeaveJoinCounter = new Counter();
        ChangeableBoolean needToRemovePlayerArm = new ChangeableBoolean(true);
        ChangeableBoolean needToRemovePlayerTwoArm = new ChangeableBoolean(true);
        VBox difficultySelection = new VBox();
        difficultySelection.setAlignment(Pos.CENTER);
        Text difficultySelectPrompt = new Text("Select difficulty:");
        difficultySelectPrompt.setFont(Font.font(75));
        difficultySelectPrompt.setFill(Color.GRAY);
        Text easySelect = new Text("Easy");
        easySelect.setFont(Font.font(75));
        easySelect.setFill(Color.rgb(37, 139, 0).brighter());
        easySelect.setOnMouseClicked(e -> {
            timeInSeconds.set(60);
            timer.setText("00:00");
            time.setTime(new Time(0, 0, 0).toLocalTime());
            numbOfZeroes[0] = 5;
            gameComponents.getChildren().remove(escapeMenu);
            player.getPlayerBody().setTranslateX(66);
            player.getPlayerBody().setTranslateY(50.46875 - 10 + 70);
            player.getPlayerHead().setTranslateX(((double) 128 / 2) + 17);
            player.getPlayerHead().setTranslateY(8.0 * 1.328125 + 62);
            player.changeLegPositions(70, 220);
            if (players[1] != null) {
                players[1].getPlayerBody().setTranslateX(66);
                players[1].getPlayerBody().setTranslateY(50.46875 - 10 + 70);
                players[1].getPlayerHead().setTranslateX(((double) 128 / 2) + 17);
                players[1].getPlayerHead().setTranslateY(8.0 * 1.328125 + 62);
                players[1].changeLegPositions(70, 220);
                players[1].changeHand(HandPosition.HIDE);
                gameComponents.getChildren().removeAll(players[1].getPlayer().stream().map(Character::getCharacter).toList());
                players[1] = null;
                Player.hasPlayerXJoined.put(PlayerCount.PLAYER_2, false);
            }
            if (!new HashSet<>(gameComponents.getChildren()).containsAll(List.of(youLost, scoreResetRetry, noTime))) {
                gameComponents.getChildren().addAll(youLost, scoreResetRetry, noTime);
                youLost.toFront();
                scoreResetRetry.toFront();
                noTime.toFront();
            }
            gameComponents.getChildren().removeAll(youLost, scoreResetRetry, noTime, scoreLog, scoreLogClear, scoreLogBonus, bonus);
            player.changeHand(HandPosition.HIDE);
            bonus.setTranslateX(0);
            score.setText("Score: " + "0".repeat(numbOfZeroes[0]));
            int numberOfZeroes = 5 - String.valueOf(playerHighScores.getFirst()).split("").length;
            highScore.setText("High Score: " + "0".repeat(numberOfZeroes) + playerHighScores.getFirst());
            gameComponents.getChildren().remove(timer);
            gameComponents.getChildren().removeAll(graves.stream().map(Character::getCharacter).toList());


            if (!gameComponents.getChildren().contains(timer)) {
                gameComponents.getChildren().add(timer);
            }
            time.setTime(new Time(0, 0, 0).toLocalTime());
            timer.setText(time.toString());
            graves.clear();
            graves.addAll(GraveUtil.graveGrid(14, 6, 0, 0, 80, 135, 128, 165));
            graves.stream().map(Character::getCharacter).forEach(Node::toBack);
            if (!new HashSet<>(gameComponents.getChildren()).containsAll(graves.stream().map(Character::getCharacter).toList())) {
                gameComponents.getChildren().addAll(graves.stream().map(Character::getCharacter).toList());
            }
            stage.getScene().setRoot(gameComponents);
        });
        Text mediumSelect = new Text("Medium");
        mediumSelect.setFont(Font.font(75));
        mediumSelect.setFill(Color.rgb(187, 194, 4));
        mediumSelect.setOnMouseClicked(e -> {

            timeInSeconds.set(40);
            timer.setText("00:00");
            time.setTime(new Time(0, 0, 0).toLocalTime());
            numbOfZeroes[0] = 5;
            gameComponents.getChildren().remove(escapeMenu);
            player.getPlayerBody().setTranslateX(66);
            player.getPlayerBody().setTranslateY(50.46875 - 10 + 70);
            player.getPlayerHead().setTranslateX(((double) 128 / 2) + 17);
            player.getPlayerHead().setTranslateY(8.0 * 1.328125 + 62);
            player.changeLegPositions(70, 220);
            bonus.setTranslateX(0);
            if (players[1] != null) {
                players[1].getPlayerBody().setTranslateX(66);
                players[1].getPlayerBody().setTranslateY(50.46875 - 10 + 70);
                players[1].getPlayerHead().setTranslateX(((double) 128 / 2) + 17);
                players[1].getPlayerHead().setTranslateY(8.0 * 1.328125 + 62);
                players[1].changeLegPositions(70, 220);
                players[1].changeHand(HandPosition.HIDE);
                gameComponents.getChildren().removeAll(players[1].getPlayer().stream().map(Character::getCharacter).toList());
                players[1] = null;
                Player.hasPlayerXJoined.put(PlayerCount.PLAYER_2, false);
            }
            if (!new HashSet<>(gameComponents.getChildren()).containsAll(List.of(youLost, scoreResetRetry, noTime))) {
                gameComponents.getChildren().addAll(youLost, scoreResetRetry, noTime);
                youLost.toFront();
                scoreResetRetry.toFront();
                noTime.toFront();
            }
            gameComponents.getChildren().removeAll(youLost, scoreResetRetry, noTime, scoreLog, scoreLogClear, scoreLogBonus, bonus);
            player.changeHand(HandPosition.HIDE);
            score.setText("Score: " + "0".repeat(numbOfZeroes[0]));
            int numberOfZeroes = 5 - String.valueOf(playerHighScores.get(1)).split("").length;
            highScore.setText("High Score: " + "0".repeat(numberOfZeroes) + playerHighScores.get(1));
            gameComponents.getChildren().remove(timer);
            gameComponents.getChildren().removeAll(graves.stream().map(Character::getCharacter).toList());


            if (!gameComponents.getChildren().contains(timer)) {
                gameComponents.getChildren().add(timer);
            }
            time.setTime(new Time(0, 0, 0).toLocalTime());
            timer.setText(time.toString());
            graves.clear();
            graves.addAll(GraveUtil.graveGrid(14, 6, 0, 0, 80, 135, 128, 165));
            graves.stream().map(Character::getCharacter).forEach(Node::toBack);
            if (!new HashSet<>(gameComponents.getChildren()).containsAll(graves.stream().map(Character::getCharacter).toList())) {
                gameComponents.getChildren().addAll(graves.stream().map(Character::getCharacter).toList());
            }
            stage.getScene().setRoot(gameComponents);
        });
        Text hardSelect = new Text("Hard");
        hardSelect.setFont(Font.font(75));
        hardSelect.setFill(Color.rgb(194, 4, 4));
        hardSelect.setOnMouseClicked(e -> {
            timeInSeconds.set(30);
            timer.setText("00:00");
            time.setTime(new Time(0, 0, 0).toLocalTime());
            numbOfZeroes[0] = 5;
            gameComponents.getChildren().remove(escapeMenu);
            player.getPlayerBody().setTranslateX(66);
            player.getPlayerBody().setTranslateY(50.46875 - 10 + 70);
            player.getPlayerHead().setTranslateX(((double) 128 / 2) + 17);
            player.getPlayerHead().setTranslateY(8.0 * 1.328125 + 62);
            player.changeLegPositions(70, 220);
            bonus.setTranslateX(0);
            if (players[1] != null) {
                players[1].getPlayerBody().setTranslateX(66);
                players[1].getPlayerBody().setTranslateY(50.46875 - 10 + 70);
                players[1].getPlayerHead().setTranslateX(((double) 128 / 2) + 17);
                players[1].getPlayerHead().setTranslateY(8.0 * 1.328125 + 62);
                players[1].changeLegPositions(70, 220);
                players[1].changeHand(HandPosition.HIDE);
                gameComponents.getChildren().removeAll(players[1].getPlayer().stream().map(Character::getCharacter).toList());
                players[1] = null;
                Player.hasPlayerXJoined.put(PlayerCount.PLAYER_2, false);
            }
            if (!new HashSet<>(gameComponents.getChildren()).containsAll(List.of(youLost, scoreResetRetry, noTime))) {
                gameComponents.getChildren().addAll(youLost, scoreResetRetry, noTime);
                youLost.toFront();
                scoreResetRetry.toFront();
                noTime.toFront();
            }
            gameComponents.getChildren().removeAll(youLost, scoreResetRetry, noTime, scoreLog, scoreLogClear, scoreLogBonus, bonus);
            player.changeHand(HandPosition.HIDE);
            score.setText("Score: " + "0".repeat(numbOfZeroes[0]));
            int numberOfZeroes = 5 - String.valueOf(playerHighScores.get(2)).split("").length;
            highScore.setText("High Score: " + "0".repeat(numberOfZeroes) + playerHighScores.get(2));
            gameComponents.getChildren().remove(timer);
            gameComponents.getChildren().removeAll(graves.stream().map(Character::getCharacter).toList());


            if (!gameComponents.getChildren().contains(timer)) {
                gameComponents.getChildren().add(timer);
            }
            time.setTime(new Time(0, 0, 0).toLocalTime());
            timer.setText(time.toString());
            graves.clear();
            graves.addAll(GraveUtil.graveGrid(14, 6, 0, 0, 80, 135, 128, 165));
            graves.stream().map(Character::getCharacter).forEach(Node::toBack);
            if (!new HashSet<>(gameComponents.getChildren()).containsAll(graves.stream().map(Character::getCharacter).toList())) {
                gameComponents.getChildren().addAll(graves.stream().map(Character::getCharacter).toList());
            }
            stage.getScene().setRoot(gameComponents);
        });
        Text impossibleSelect = new Text("Impossible");
        impossibleSelect.setFill(Color.rgb(68, 0, 89));
        impossibleSelect.setFont(Font.font(75));
        impossibleSelect.setOnMouseClicked(e -> {
            bonus.setTranslateX(0);
            timeInSeconds.set(20);
            timer.setText("00:00");
            time.setTime(new Time(0, 0, 0).toLocalTime());
            numbOfZeroes[0] = 5;
            gameComponents.getChildren().remove(escapeMenu);
            player.getPlayerBody().setTranslateX(66);
            player.getPlayerBody().setTranslateY(50.46875 - 10 + 70);
            player.getPlayerHead().setTranslateX(((double) 128 / 2) + 17);
            player.getPlayerHead().setTranslateY(8.0 * 1.328125 + 62);
            player.changeLegPositions(70, 220);
            if (players[1] != null) {
                players[1].getPlayerBody().setTranslateX(66);
                players[1].getPlayerBody().setTranslateY(50.46875 - 10 + 70);
                players[1].getPlayerHead().setTranslateX(((double) 128 / 2) + 17);
                players[1].getPlayerHead().setTranslateY(8.0 * 1.328125 + 62);
                players[1].changeLegPositions(70, 220);
                players[1].changeHand(HandPosition.HIDE);
                gameComponents.getChildren().removeAll(players[1].getPlayer().stream().map(Character::getCharacter).toList());
                players[1] = null;
                Player.hasPlayerXJoined.put(PlayerCount.PLAYER_2, false);

            }
            if (!new HashSet<>(gameComponents.getChildren()).containsAll(List.of(youLost, scoreResetRetry, noTime))) {
                gameComponents.getChildren().addAll(youLost, scoreResetRetry, noTime);
                youLost.toFront();
                scoreResetRetry.toFront();
                noTime.toFront();
            }
            gameComponents.getChildren().removeAll(youLost, scoreResetRetry, noTime, scoreLog, scoreLogClear, scoreLogBonus, bonus);
            player.changeHand(HandPosition.HIDE);
            score.setText("Score: " + "0".repeat(numbOfZeroes[0]));
            int numberOfZeroes = 5 - String.valueOf(playerHighScores.get(3)).split("").length;
            highScore.setText("High Score: " + "0".repeat(numberOfZeroes) + playerHighScores.get(3));
            gameComponents.getChildren().remove(timer);
            gameComponents.getChildren().removeAll(graves.stream().map(Character::getCharacter).toList());


            if (!gameComponents.getChildren().contains(timer)) {
                gameComponents.getChildren().add(timer);
            }
            time.setTime(new Time(0, 0, 0).toLocalTime());
            timer.setText(time.toString());
            graves.clear();
            graves.addAll(GraveUtil.graveGrid(14, 6, 0, 0, 80, 135, 128, 165));
            graves.stream().map(Character::getCharacter).forEach(Node::toBack);
            if (!new HashSet<>(gameComponents.getChildren()).containsAll(graves.stream().map(Character::getCharacter).toList())) {
                gameComponents.getChildren().addAll(graves.stream().map(Character::getCharacter).toList());
            }
            stage.getScene().setRoot(gameComponents);
        });
        difficultySelection.getChildren().addAll(difficultySelectPrompt, easySelect, mediumSelect, hardSelect, impossibleSelect, backFromSelection);
        creditsVBox.getChildren().addAll(creditsTitle, creditsLegoaggelos, creditsJedElinoffScotthomas, backFromCredits);
        optionsVBox.getChildren().addAll(guide, credits, gotoControls, backFromOptions);
        playSurvival.setOnMouseClicked(e -> stage.getScene().setRoot(difficultySelection));
        gotoMainMenu.setOnMouseClicked(e -> {
            numbOfZeroes[0] = 5;
            gameComponents.getChildren().remove(escapeMenu);
            stage.getScene().setRoot(mainMenuComponents);
            player.getPlayerBody().setTranslateX(66);
            player.getPlayerBody().setTranslateY(50.46875 - 10 + 70);
            player.getPlayerHead().setTranslateX(((double) 128 / 2) + 17);
            player.getPlayerHead().setTranslateY(8.0 * 1.328125 + 62);
            player.changeLegPositions(70, 220);
            if (players[1] != null) {
                players[1].getPlayerBody().setTranslateX(66);
                players[1].getPlayerBody().setTranslateY(50.46875 - 10 + 70);
                players[1].getPlayerHead().setTranslateX(((double) 128 / 2) + 17);
                players[1].getPlayerHead().setTranslateY(8.0 * 1.328125 + 62);
                players[1].changeLegPositions(70, 220);
                players[1].changeHand(HandPosition.HIDE);
                gameComponents.getChildren().removeAll(players[1].getPlayer().stream().map(Character::getCharacter).toList());
                players[1] = null;
                Player.hasPlayerXJoined.put(PlayerCount.PLAYER_2, false);
            }

            if (!new HashSet<>(gameComponents.getChildren()).containsAll(List.of(youLost, scoreResetRetry, noTime))) {
                gameComponents.getChildren().addAll(youLost, scoreResetRetry, noTime);
                youLost.toFront();
                scoreResetRetry.toFront();
                noTime.toFront();
            }
            gameComponents.getChildren().removeAll(youLost, scoreResetRetry, noTime, scoreLog, scoreLogClear, scoreLogBonus, bonus);
            player.changeHand(HandPosition.HIDE);

            score.setText("Score: " + "0".repeat(numbOfZeroes[0]));
            gameComponents.getChildren().remove(timer);
            gameComponents.getChildren().removeAll(graves.stream().map(Character::getCharacter).toList());


            if (!gameComponents.getChildren().contains(timer)) {
                gameComponents.getChildren().add(timer);
            }
            time.setTime(new Time(0, 0, 0).toLocalTime());
            timer.setText(time.toString());
            graves.clear();
            graves.addAll(GraveUtil.graveGrid(14, 6, 0, 0, 80, 135, 128, 165));
            graves.stream().map(Character::getCharacter).forEach(Node::toBack);
            if (!new HashSet<>(gameComponents.getChildren()).containsAll(graves.stream().map(Character::getCharacter).toList())) {
                gameComponents.getChildren().addAll(graves.stream().map(Character::getCharacter).toList());
            }
        });
        playFreeplay.setOnMouseClicked(e -> {
            timeInSeconds.set(600);
            gameComponents.getChildren().remove(escapeMenu);
            stage.getScene().setRoot(mainMenuComponents);
            player.getPlayerBody().setTranslateX(66);
            player.getPlayerBody().setTranslateY(50.46875 - 10 + 70);
            player.getPlayerHead().setTranslateX(((double) 128 / 2) + 17);
            player.getPlayerHead().setTranslateY(8.0 * 1.328125 + 62);
            player.changeLegPositions(70, 220);
            if (players[1] != null) {
                players[1].getPlayerBody().setTranslateX(66);
                players[1].getPlayerBody().setTranslateY(50.46875 - 10 + 70);
                players[1].getPlayerHead().setTranslateX(((double) 128 / 2) + 17);
                players[1].getPlayerHead().setTranslateY(8.0 * 1.328125 + 62);
                players[1].changeLegPositions(70, 220);
                players[1].changeHand(HandPosition.HIDE);
                gameComponents.getChildren().removeAll(players[1].getPlayer().stream().map(Character::getCharacter).toList());
                players[1] = null;
                Player.hasPlayerXJoined.put(PlayerCount.PLAYER_2, false);
            }
            gameComponents.getChildren().removeAll(graves.stream().map(Character::getCharacter).toList());
            if (!new HashSet<>(gameComponents.getChildren()).containsAll(List.of(youLost, scoreResetRetry, noTime))) {
                gameComponents.getChildren().addAll(youLost, scoreResetRetry, noTime);
                youLost.toFront();
                scoreResetRetry.toFront();
                noTime.toFront();
            }
            score.setText("Score: 000000");
            bonus.setTranslateX(bonus.getTranslateX()+45);
            int numberOfZeroes = 6 - String.valueOf(playerHighScores.get(4)).split("").length;
            highScore.setText("High Score: " + "0".repeat(numberOfZeroes) + playerHighScores.get(4));
            gameComponents.getChildren().remove(timer);
            graves.clear();
            graves.addAll(GraveUtil.graveGrid(14, 6, 0, 0, 80, 135, 128, 165));
            graves.stream().map(Character::getCharacter).forEach(Node::toBack);
            if (!new HashSet<>(gameComponents.getChildren()).containsAll(graves.stream().map(Character::getCharacter).toList())) {
                gameComponents.getChildren().addAll(graves.stream().map(Character::getCharacter).toList());
            }
            gameComponents.getChildren().removeAll(youLost, scoreResetRetry, noTime);
            if (!gameComponents.getChildren().contains(timer)) {
                gameComponents.getChildren().add(timer);
            }
            time.setTime(new Time(0, 0, 0).toLocalTime());
            timer.setText(time.toString());
            numbOfZeroes[0] = 6;
            stage.getScene().setRoot(gameComponents);
        });
        logger.info("Object initialization finished.");
        logger.info("Starting AnimationTimer.");
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                long before = System.nanoTime();
                if (!(timeInBetweenHandleCalls.getNanoTime() == 0)) {
                    if (!gameComponents.getChildren().contains(escapeMenu) && !gameComponents.getChildren().contains(youWon)) {
                        time.setTime(time.getTime().plusNanos((long) (1.00000 * before - timeInBetweenHandleCalls.getNanoTime())));
                        timer.setText(time.toString());
                    }
                    if (timeBeforeLastEscape[0] < 3) {
                        timeBeforeLastEscape[0] = timeBeforeLastEscape[0] + (before - (double) timeInBetweenHandleCalls.getNanoTime()) / 1000000000;
                    }
                }
                if (pressedKeys.getOrDefault(KeyCode.F11, false)) {
                    if (!stage.isFullScreen()) {
                        stage.setFullScreen(true);
                    }
                }
                if (pressedKeys.getOrDefault(KeyCode.ESCAPE, false) && timeBeforeLastEscape[0] >= 0.25) {
                    if (gameComponents.getChildren().contains(escapeMenu)) {
                        gameComponents.getChildren().remove(escapeMenu);
                    } else {
                        gameComponents.getChildren().add(escapeMenu);
                    }
                    stage.setFullScreen(true);
                    timeBeforeLastEscape[0] = 0;
                }
                if (stage.getScene().getRoot().equals(gameComponents) && !gameComponents.getChildren().contains(escapeMenu)) {
                    if (score.getText().equalsIgnoreCase("score: 99999") || score.getText().equalsIgnoreCase("score: 999999")) {
                        player.changeHand(HandPosition.HIDE);
                        if (players[1] != null) {
                            players[1].changeHand(HandPosition.HIDE);
                        }
                        isGameBeaten.setBool(true);
                    }
                    if (isGameBeaten.bool()) {
                        if (!new HashSet<>(gameComponents.getChildren()).containsAll(List.of(youWon, beatingTheGame, noRanTime))) {
                            if (score.getText().equalsIgnoreCase("score: 999999")) {
                                youWon.setText("You Beat Freeplay!");
                                logger.info("Freeplay beaten.");
                            } else {
                                youWon.setText("You Beat The Game!");
                                logger.info("Game beaten.");
                            }
                            gameComponents.getChildren().addAll(List.of(youWon, beatingTheGame, noRanTime));
                        }
                        if (pressedKeys.getOrDefault(KeyCode.R, false)) {
                            isGameBeaten.setBool(false);
                            player.getPlayerBody().setTranslateX(66);
                            player.getPlayerBody().setTranslateY(50.46875 - 10 + 70);
                            player.getPlayerHead().setTranslateX(((double) 128 / 2) + 17);
                            player.getPlayerHead().setTranslateY(8.0 * 1.328125 + 62);
                            player.changeLegPositions(70, 220);
                            if (players[1] != null) {
                                players[1].getPlayerBody().setTranslateX(66);
                                players[1].getPlayerBody().setTranslateY(50.46875 - 10 + 70);
                                players[1].getPlayerHead().setTranslateX(((double) 128 / 2) + 17);
                                players[1].getPlayerHead().setTranslateY(8.0 * 1.328125 + 62);
                                players[1].changeLegPositions(70, 220);
                            }
                            gameComponents.getChildren().removeAll(graves.stream().map(Character::getCharacter).toList());
                            score.setText("Score: " + "0".repeat(numbOfZeroes[0]));
                            needToRemovePlayerArm.setBool(true);
                            needToRemovePlayerTwoArm.setBool(true);
                            gameComponents.getChildren().remove(timer);
                            graves.clear();
                            graves.addAll(GraveUtil.graveGrid(14, 6, 0, 0, 80, 135, 128, 165));
                            graves.stream().map(Character::getCharacter).forEach(Node::toBack);
                            if (!new HashSet<>(gameComponents.getChildren()).containsAll(graves.stream().map(Character::getCharacter).toList())) {
                                gameComponents.getChildren().addAll(graves.stream().map(Character::getCharacter).toList());
                            }
                            gameComponents.getChildren().removeAll(youLost, scoreResetRetry, noTime, youWon, noRanTime, beatingTheGame/*,player.getPlayerFist().getCharacter(),player.getPlayerArm().getCharacter()*/);
                            if (!gameComponents.getChildren().contains(timer)) {
                                gameComponents.getChildren().add(timer);
                            }

                        }
                        time.setTime(new Time(0, 0, 0).toLocalTime());
                        timer.setText(time.toString());
                        timeInBetweenHandleCalls.setNanoTime(0);
                        return;
                    }
                    if (pressedKeys.getOrDefault(KeyCode.F12, false)) {
                        if (players[1] == null && playerTwoLeaveJoinCounter.getCounter() < 1) {
                            players[1] = new Player(109.53125, 60.0, 66, 110.46875, 48.84375, 29.0, 81, 72.625, 19.921875, 110.0, 97, 120, 43.828125, 43.828125, 181, 108.75, true, 75, 20, 70, 220, 30, Color.BEIGE, Color.RED, Color.GREEN, Color.WHITE, Color.LIGHTBLUE);
                            playerTwoLeaveJoinCounter.increaseCounter();
                            players[1].changeHand(HandPosition.HIDE);
                            gameComponents.getChildren().addAll(players[1].getPlayer().stream().map(Character::getCharacter).toList());
                            Difficulty currentDifficulty = getDifficultyFromTime.get(timeInSeconds.get());
                            int numberOfZeroes = 0;
                            int index = 0;
                            if (currentDifficulty == Difficulty.FREEPLAY) {
                                numberOfZeroes = 6 - String.valueOf(playerHighScores.get(9)).split("").length;
                                index = 9;
                            } else if (currentDifficulty == Difficulty.IMPOSSIBLE) {
                                numberOfZeroes = 5 - String.valueOf(playerHighScores.get(8)).split("").length;
                                index = 8;
                            } else if (currentDifficulty == Difficulty.HARD) {
                                numberOfZeroes = 5 - String.valueOf(playerHighScores.get(7)).split("").length;
                                index = 7;
                            } else if (currentDifficulty == Difficulty.MEDIUM) {
                                numberOfZeroes = 5 - String.valueOf(playerHighScores.get(6)).split("").length;
                                index = 6;
                            } else if (currentDifficulty == Difficulty.EASY) {
                                numberOfZeroes = 5 - String.valueOf(playerHighScores.get(5)).split("").length;
                                index = 5;
                            }
                            highScore.setText("High Score: " + "0".repeat(numberOfZeroes) + playerHighScores.get(index));
                            logger.info("Player 2 added.");
                        } else if (players[1] != null && playerTwoLeaveJoinCounter.getCounter() < 1) {
                            gameComponents.getChildren().removeAll(players[1].getPlayer().stream().map(Character::getCharacter).toList());
                            players[1] = null;
                            int numberOfZeroes = 0;
                            int index = 0;
                            Difficulty currentDifficulty = getDifficultyFromTime.get(timeInSeconds.get());
                            if (currentDifficulty == Difficulty.FREEPLAY) {
                                numberOfZeroes = 6 - String.valueOf(playerHighScores.get(4)).split("").length;
                                index = 4;
                            } else if (currentDifficulty == Difficulty.HARD) {
                                numberOfZeroes = 5 - String.valueOf(playerHighScores.get(2)).split("").length;
                                index = 2;
                            } else if (currentDifficulty == Difficulty.MEDIUM) {
                                numberOfZeroes = 5 - String.valueOf(playerHighScores.get(1)).split("").length;
                                index = 1;
                            } else if (currentDifficulty == Difficulty.EASY) {
                                numberOfZeroes = 5 - String.valueOf(playerHighScores.getFirst()).split("").length;
                            } else if (currentDifficulty == Difficulty.IMPOSSIBLE) {
                                numberOfZeroes = 5 - String.valueOf(playerHighScores.get(3)).split("").length;
                                index = 3;
                            }
                            highScore.setText("High Score: " + "0".repeat(numberOfZeroes) + playerHighScores.get(index));
                            Player.hasPlayerXJoined.put(PlayerCount.PLAYER_2, false);
                            playerTwoLeaveJoinCounter.increaseCounter();
                            logger.info("Player 2 removed.");
                        }
                    } else if (playerTwoLeaveJoinCounter.getCounter() > 0) {
                        playerTwoLeaveJoinCounter.resetCounter();
                    }
                    ChangeableBoolean isGraveInTheWay = new ChangeableBoolean(false);
                    ChangeableBoolean isGraveInTheWayP2 = new ChangeableBoolean(false);
                    try {
                        if (pressedKeys.getOrDefault(KeyCode.W, false)) {
                            if (!(player.getPlayerHead().getTranslateY() - 165 < 0) && globalMoveCounter.getCounter() > 10 && moveAfterGraveRespawn.getCounter() == -1) {
                                playerCanMoveUtility.setTranslateX(player.getPlayerBody().getTranslateX());
                                playerCanMoveUtility.setTranslateY(player.getPlayerBody().getTranslateY() - 165);
                                graves.forEach(v -> {
                                    if (v.colliding(playerCanMoveUtility)) {
                                        isGraveInTheWay.setBool(true);
                                    }
                                });
                                if (!isGraveInTheWay.bool()) {
                                    Thread.sleep(25);
                                    player.moveHorizontally(-165);
                                    globalMoveCounter.setCounter(-1);
                                    player.changeHand(HandPosition.HIDE);
                                    attackingCounter.setCounter(-1);
                                    pressedKeys.put(KeyCode.X, false);
                                }
                                isGraveInTheWay.setBool(false);
                                playerCanMoveUtility.setTranslateX(-10000);
                                playerCanMoveUtility.setTranslateY(-10000);
                            }
                        }
                        if (pressedKeys.getOrDefault(KeyCode.UP, false) && players[1] != null) {
                            if (!(players[1].getPlayerHead().getTranslateY() - 165 < 0) && globalMoveCounterP2.getCounter() > 10 && moveAfterGraveRespawnP2.getCounter() == -1) {
                                playerTwoCanMoveUtility.setTranslateX(players[1].getPlayerBody().getTranslateX());
                                playerTwoCanMoveUtility.setTranslateY(players[1].getPlayerBody().getTranslateY() - 165);
                                graves.forEach(v -> {
                                    if (v.colliding(playerTwoCanMoveUtility)) {
                                        isGraveInTheWayP2.setBool(true);
                                    }
                                });
                                if (!isGraveInTheWayP2.bool()) {
                                    Thread.sleep(25);
                                    players[1].moveHorizontally(-165);
                                    globalMoveCounterP2.setCounter(-1);
                                    players[1].changeHand(HandPosition.HIDE);
                                    attackingCounterP2.setCounter(-1);
                                    pressedKeys.put(KeyCode.CONTROL, false);
                                }
                                isGraveInTheWayP2.setBool(false);
                                playerTwoCanMoveUtility.setTranslateX(-10000);
                                playerTwoCanMoveUtility.setTranslateY(-10000);
                            }
                        }
                        if (pressedKeys.getOrDefault(KeyCode.S, false) && globalMoveCounter.getCounter() > 10 && moveAfterGraveRespawn.getCounter() == -1) {
                            if (!(player.getPlayerBody().getTranslateY() + 165 > 1080)) {
                                playerCanMoveUtility.setTranslateX(player.getPlayerBody().getTranslateX());
                                playerCanMoveUtility.setTranslateY(player.getPlayerBody().getTranslateY() + 165);
                                graves.forEach(v -> {
                                    if (v.colliding(playerCanMoveUtility)) {
                                        isGraveInTheWay.setBool(true);
                                    }
                                });
                                if (!isGraveInTheWay.bool()) {
                                    playerCanMoveUtility.setTranslateX(player.getPlayerLegOne().getTranslateX());
                                    playerCanMoveUtility.setTranslateY(player.getPlayerLegOne().getTranslateY() + 235);
                                    graves.forEach(v -> {
                                        if (v.colliding(playerCanMoveUtility)) {
                                            isGraveInTheWay.setBool(true);
                                        }
                                    });
                                }
                                if (!isGraveInTheWay.bool()) {
                                    player.moveHorizontally(165);
                                    globalMoveCounter.setCounter(-1);
                                    player.changeHand(HandPosition.HIDE);
                                    attackingCounter.setCounter(-1);
                                    pressedKeys.put(KeyCode.X, false);
                                }
                                isGraveInTheWay.setBool(false);
                                playerCanMoveUtility.setTranslateX(-10000);
                                playerCanMoveUtility.setTranslateY(-10000);
                            }
                        }
                        if (pressedKeys.getOrDefault(KeyCode.DOWN, false) && players[1] != null) {
                            if (!(players[1].getPlayerBody().getTranslateY() + 165 > 1080) && globalMoveCounterP2.getCounter() > 10 && moveAfterGraveRespawnP2.getCounter() == -1) {

                                playerTwoCanMoveUtility.setTranslateX(players[1].getPlayerBody().getTranslateX());
                                playerTwoCanMoveUtility.setTranslateY(players[1].getPlayerBody().getTranslateY() + 165);
                                graves.forEach(v -> {
                                    if (v.colliding(playerTwoCanMoveUtility)) {
                                        isGraveInTheWayP2.setBool(true);
                                    }
                                });
                                if (!isGraveInTheWayP2.bool()) {
                                    playerTwoCanMoveUtility.setTranslateX(players[1].getPlayerLegOne().getTranslateX());
                                    playerTwoCanMoveUtility.setTranslateY(players[1].getPlayerLegOne().getTranslateY() + 235);
                                    graves.forEach(v -> {
                                        if (v.colliding(playerTwoCanMoveUtility)) {
                                            isGraveInTheWayP2.setBool(true);
                                        }
                                    });
                                }
                                if (!isGraveInTheWayP2.bool()) {
                                    Thread.sleep(25);
                                    players[1].moveHorizontally(165);
                                    globalMoveCounterP2.setCounter(-1);
                                    players[1].changeHand(HandPosition.HIDE);
                                    attackingCounterP2.setCounter(-1);
                                    pressedKeys.put(KeyCode.CONTROL, false);
                                }
                                isGraveInTheWayP2.setBool(false);
                                playerTwoCanMoveUtility.setTranslateX(-10000);
                                playerTwoCanMoveUtility.setTranslateY(-10000);
                            }
                        }
                        if (pressedKeys.getOrDefault(KeyCode.A, false) && globalMoveCounter.getCounter() > 10 && moveAfterGraveRespawn.getCounter() == -1) {
                            if (!(player.getPlayerBody().getTranslateX() - 128 < 0)) {
                                playerCanMoveUtility.setTranslateX(player.getPlayerBody().getTranslateX() - 128);
                                playerCanMoveUtility.setTranslateY(player.getPlayerBody().getTranslateY());
                                graves.forEach(v -> {
                                    if (v.colliding(playerCanMoveUtility)) {
                                        isGraveInTheWay.setBool(true);
                                    }
                                });
                                if (!isGraveInTheWay.bool()) {
                                    playerCanMoveUtility.setTranslateX(player.getPlayerLegOne().getTranslateX() - 128);
                                    playerCanMoveUtility.setTranslateY(player.getPlayerLegOne().getTranslateY() + 70);
                                    graves.forEach(v -> {
                                        if (v.colliding(playerCanMoveUtility)) {
                                            isGraveInTheWay.setBool(true);
                                        }
                                    });
                                }
                                if (!isGraveInTheWay.bool()) {
                                    Thread.sleep(25);
                                    player.moveVertically(-128);
                                    globalMoveCounter.setCounter(-1);
                                    player.changeHand(HandPosition.HIDE);
                                    attackingCounter.setCounter(-1);
                                    pressedKeys.put(KeyCode.X, false);
                                }
                                isGraveInTheWay.setBool(false);
                                playerCanMoveUtility.setTranslateX(-10000);
                                playerCanMoveUtility.setTranslateY(-10000);
                            }
                        }
                        if (pressedKeys.getOrDefault(KeyCode.LEFT, false) && players[1] != null) {
                            if (!(players[1].getPlayerBody().getTranslateX() - 128 < 0) && globalMoveCounterP2.getCounter() > 10 && moveAfterGraveRespawnP2.getCounter() == -1) {
                                playerTwoCanMoveUtility.setTranslateX(players[1].getPlayerBody().getTranslateX() - 128);
                                playerTwoCanMoveUtility.setTranslateY(players[1].getPlayerBody().getTranslateY());
                                graves.forEach(v -> {
                                    if (v.colliding(playerTwoCanMoveUtility)) {
                                        isGraveInTheWayP2.setBool(true);
                                    }
                                });
                                if (!isGraveInTheWayP2.bool()) {
                                    playerTwoCanMoveUtility.setTranslateX(players[1].getPlayerLegOne().getTranslateX() - 128);
                                    playerTwoCanMoveUtility.setTranslateY(players[1].getPlayerLegOne().getTranslateY() + 70);
                                    graves.forEach(v -> {
                                        if (v.colliding(playerTwoCanMoveUtility)) {
                                            isGraveInTheWayP2.setBool(true);
                                        }
                                    });
                                }
                                if (!isGraveInTheWayP2.bool()) {
                                    Thread.sleep(25);
                                    players[1].moveVertically(-128);
                                    globalMoveCounterP2.setCounter(-1);
                                    players[1].changeHand(HandPosition.HIDE);
                                    attackingCounterP2.setCounter(-1);
                                    pressedKeys.put(KeyCode.CONTROL, false);
                                }
                                isGraveInTheWayP2.setBool(false);
                                playerTwoCanMoveUtility.setTranslateX(-10000);
                                playerTwoCanMoveUtility.setTranslateY(-10000);
                            }
                        }
                        if (pressedKeys.getOrDefault(KeyCode.D, false) && globalMoveCounter.getCounter() > 10 && moveAfterGraveRespawn.getCounter() == -1) {
                            if (!(player.getPlayerBody().getTranslateX() + 128 > 1920)) {
                                playerCanMoveUtility.setTranslateX(player.getPlayerBody().getTranslateX() + 128);
                                playerCanMoveUtility.setTranslateY(player.getPlayerBody().getTranslateY());
                                graves.forEach(v -> {
                                    if (v.colliding(playerCanMoveUtility)) {
                                        isGraveInTheWay.setBool(true);
                                    }
                                });
                                if (!isGraveInTheWay.bool()) {
                                    playerCanMoveUtility.setTranslateX(player.getPlayerLegOne().getTranslateX() + 128);
                                    playerCanMoveUtility.setTranslateY(player.getPlayerLegOne().getTranslateY() + 70);
                                    graves.forEach(v -> {
                                        if (v.colliding(playerCanMoveUtility)) {
                                            isGraveInTheWay.setBool(true);
                                        }
                                    });
                                }
                                if (!isGraveInTheWay.bool()) {
                                    Thread.sleep(25);
                                    player.moveVertically(128);
                                    globalMoveCounter.setCounter(-1);
                                    player.changeHand(HandPosition.HIDE);
                                    attackingCounter.setCounter(-1);
                                    pressedKeys.put(KeyCode.X, false);
                                }
                                isGraveInTheWay.setBool(false);
                                playerCanMoveUtility.setTranslateX(-10000);
                                playerCanMoveUtility.setTranslateY(-10000);
                            }
                        }
                        if (pressedKeys.getOrDefault(KeyCode.RIGHT, false) && players[1] != null) {
                            if (!(players[1].getPlayerBody().getTranslateX() + 128 > 1920) && globalMoveCounterP2.getCounter() > 10 && moveAfterGraveRespawn.getCounter() == -1) {
                                playerTwoCanMoveUtility.setTranslateX(players[1].getPlayerBody().getTranslateX() + 128);
                                playerTwoCanMoveUtility.setTranslateY(players[1].getPlayerBody().getTranslateY());
                                graves.forEach(v -> {
                                    if (v.colliding(playerTwoCanMoveUtility)) {
                                        isGraveInTheWayP2.setBool(true);
                                    }
                                });
                                if (!isGraveInTheWayP2.bool()) {
                                    playerTwoCanMoveUtility.setTranslateX(players[1].getPlayerLegOne().getTranslateX() + 128);
                                    playerTwoCanMoveUtility.setTranslateY(players[1].getPlayerLegOne().getTranslateY() + 70);
                                    graves.forEach(v -> {
                                        if (v.colliding(playerTwoCanMoveUtility)) {
                                            isGraveInTheWayP2.setBool(true);
                                        }
                                    });
                                }
                                if (!isGraveInTheWayP2.bool()) {
                                    Thread.sleep(25);
                                    players[1].moveVertically(128);
                                    globalMoveCounterP2.setCounter(-1);
                                    players[1].changeHand(HandPosition.HIDE);
                                    attackingCounterP2.setCounter(-1);
                                    pressedKeys.put(KeyCode.CONTROL, false);
                                }
                                isGraveInTheWayP2.setBool(false);
                                playerTwoCanMoveUtility.setTranslateX(-10000);
                                playerTwoCanMoveUtility.setTranslateY(-10000);
                            }
                        }
                        if (pressedKeys.getOrDefault(KeyCode.X, false) && globalMoveCounter.getCounter() > 10 && attackingCounter.getCounter() == -1 && moveAfterGraveRespawn.getCounter() == -1) {
                            if (!player.isAttacking() && !gameComponents.getChildren().contains(graveRespawning)) {
                                player.changeHand(HandPosition.SHOW);
                                attackingCounter.increaseCounter();
                                //gameComponents.getChildren().removeAll(graves.stream().map(Character::getCharacter).toList());
                                //graves.clear();

                            }
                        }
                        if (pressedKeys.getOrDefault(KeyCode.CONTROL, false) && globalMoveCounter.getCounter() > 10 && attackingCounterP2.getCounter() == -1 && moveAfterGraveRespawnP2.getCounter() == -1 && players[1] != null) {
                            if (!players[1].isAttacking() && !gameComponents.getChildren().contains(graveRespawning)) {
                                players[1].changeHand(HandPosition.SHOW);
                                attackingCounterP2.increaseCounter();
                                //gameComponents.getChildren().removeAll(graves.stream().map(Character::getCharacter).toList());
                                //graves.clear();

                            }
                        }
                        if (attackingCounter.getCounter() > -1 && attackingCounter.getCounter() < 35) {
                            attackingCounter.increaseCounter();
                        }
                        if (attackingCounter.getCounter() == 35) {
                            player.changeHand(HandPosition.HIDE);
                            attackingCounter.resetCounter();
                        }
                        if (players[1] != null) {
                            if (attackingCounterP2.getCounter() > -1 && attackingCounterP2.getCounter() < 35) {
                                attackingCounterP2.increaseCounter();
                            }
                            if (attackingCounterP2.getCounter() == 35) {
                                players[1].changeHand(HandPosition.HIDE);
                                attackingCounterP2.resetCounter();
                            }
                        }
                    } catch (InterruptedException e) {
                        logger.error(e);
                        logger.error(e.getStackTrace());
                    }
                    globalMoveCounter.increaseCounter();
                    globalMoveCounterP2.increaseCounter();
                    if (scoreLogCounter.getCounter() > 0 && scoreLogCounter.getCounter() < 220 && gameComponents.getChildren().contains(scoreLog)) {
                        if (!gameComponents.getChildren().contains(scoreLog)) {
                            gameComponents.getChildren().add(scoreLog);
                        }
                        if (scoreLogCounter.getCounter() > 30) {
                            scoreLog.setOpacity(scoreLog.getOpacity() - (scoreLog.getOpacity() / 220));
                        }
                        scoreLogCounter.increaseCounter();
                    } else if (scoreLogCounter.getCounter() == 220) {
                        gameComponents.getChildren().remove(scoreLog);
                    }
                    try {
                        graves.forEach(v -> {
                            if (v.colliding(player.getPlayerFist())) {
                                long currentScore = Long.parseLong(score.getText().split(" ")[1]) + 1;
                                if (numbOfZeroes[0] == 5) {
                                    if (currentScore < 99999) {
                                        int numberOfZeroes = 5 - String.valueOf(currentScore).split("").length;
                                        score.setText("Score: " + "0".repeat(Math.max(0, numberOfZeroes)) + (currentScore));
                                        int index;
                                        Difficulty currentDifficulty = getDifficultyFromTime.get(timeInSeconds.get());
                                        if (players[1] != null) {
                                            index = 5;
                                            if (currentDifficulty == Difficulty.HARD) {
                                                index = 7;
                                            } else if (currentDifficulty == Difficulty.MEDIUM) {
                                                index = 6;
                                            } else if (currentDifficulty == Difficulty.IMPOSSIBLE) {
                                                index = 8;
                                            }
                                        } else {
                                            index = 0;
                                            if (currentDifficulty == Difficulty.HARD) {
                                                index = 2;
                                            } else if (currentDifficulty == Difficulty.MEDIUM) {
                                                index = 1;
                                            } else if (currentDifficulty == Difficulty.IMPOSSIBLE) {
                                                index = 3;
                                            }
                                        }
                                        numberOfZeroes = 5 - playerHighScores.get(index).toString().length();
                                        if (currentScore > playerHighScores.get(index)) {
                                            highScore.setText("High Score: " + "0".repeat(numberOfZeroes) + currentScore);
                                            playerHighScores.set(index, (int) currentScore);
                                        }
                                    } else {
                                        score.setText("Score: 99999");
                                        int index;
                                        Difficulty currentDifficulty = getDifficultyFromTime.get(timeInSeconds.get());
                                        if (players[1] != null) {
                                            index = 5;
                                            if (currentDifficulty == Difficulty.HARD) {
                                                index = 7;
                                            } else if (currentDifficulty == Difficulty.MEDIUM) {
                                                index = 6;
                                            } else if (currentDifficulty == Difficulty.IMPOSSIBLE) {
                                                index = 8;
                                            }
                                        } else {
                                            index = 0;
                                            if (currentDifficulty == Difficulty.HARD) {
                                                index = 2;
                                            } else if (currentDifficulty == Difficulty.MEDIUM) {
                                                index = 1;
                                            } else if (currentDifficulty == Difficulty.IMPOSSIBLE) {
                                                index = 3;
                                            }
                                        }
                                        if (99999 > playerHighScores.get(index)) {
                                            highScore.setText("High Score: 99999");
                                            playerHighScores.set(index, 99999);
                                        }
                                    }
                                    if (!gameComponents.getChildren().contains(scoreLog)) {
                                        gameComponents.getChildren().add(scoreLog);
                                    }
                                }
                                if (numbOfZeroes[0] == 6) {
                                    if (currentScore < 999999) {
                                        int numberOfZeroes = 6 - String.valueOf(currentScore).split("").length;
                                        score.setText("Score: " + "0".repeat(Math.max(0, numberOfZeroes)) + (currentScore));
                                        if (players[1] == null) {
                                            if (currentScore > playerHighScores.get(4)) {
                                                highScore.setText("High Score: " + "0".repeat(numberOfZeroes) + currentScore);
                                                playerHighScores.set(4, (int) currentScore);
                                            }
                                        } else {
                                            if (currentScore > playerHighScores.get(9)) {
                                                highScore.setText("High Score: " + "0".repeat(numberOfZeroes) + currentScore);
                                                playerHighScores.set(9, (int) currentScore);
                                            }
                                        }
                                    } else {
                                        score.setText("Score: 999999");
                                        if (players[1] == null) {
                                            if (999999 > playerHighScores.get(4)) {
                                                playerHighScores.set(4, 999999);
                                            }
                                        } else {
                                            if (999999 > playerHighScores.get(9)) {
                                                playerHighScores.set(9, 999999);
                                            }
                                        }
                                    }
                                    if (!gameComponents.getChildren().contains(scoreLog)) {
                                        gameComponents.getChildren().add(scoreLog);
                                    }
                                }
                                scoreLogCounter.setCounter(1);
                                scoreLog.setOpacity(1);
                                gameComponents.getChildren().remove(v.getCharacter());
                                graves.remove(v);
                            }
                        });
                    } catch (ConcurrentModificationException e) {
                        logger.info("Exception caught but game working as expected.(P1)");
                    }
                    try {
                        if (players[1] != null) {
                            graves.forEach(v -> {
                                if (v.colliding(players[1].getPlayerFist())) {
                                    long currentScore = Long.parseLong(score.getText().split(" ")[1]) + 1;
                                    if (numbOfZeroes[0] == 5) {
                                        if (currentScore < 99999) {
                                            int numberOfZeroes = 5 - String.valueOf(currentScore).split("").length;
                                            score.setText("Score: " + "0".repeat(Math.max(0, numberOfZeroes)) + (currentScore));
                                            int index;
                                            Difficulty currentDifficulty = getDifficultyFromTime.get(timeInSeconds.get());
                                            index = 5;
                                            if (currentDifficulty == Difficulty.HARD) {
                                                index = 7;
                                            } else if (currentDifficulty == Difficulty.MEDIUM) {
                                                index = 6;
                                            } else if (currentDifficulty == Difficulty.IMPOSSIBLE) {
                                                index = 8;
                                            }
                                            numberOfZeroes = 5 - playerHighScores.get(index).toString().length();
                                            if (currentScore > playerHighScores.get(index)) {
                                                highScore.setText("High Score: " + "0".repeat(numberOfZeroes) + currentScore);
                                                playerHighScores.set(index, (int) currentScore);
                                            }
                                        } else {
                                            score.setText("Score: 99999");
                                            int index;
                                            Difficulty currentDifficulty = getDifficultyFromTime.get(timeInSeconds.get());
                                            if (players[1] != null) {
                                                index = 5;
                                                if (currentDifficulty == Difficulty.HARD) {
                                                    index = 7;
                                                } else if (currentDifficulty == Difficulty.MEDIUM) {
                                                    index = 6;
                                                } else if (currentDifficulty == Difficulty.IMPOSSIBLE) {
                                                    index = 8;
                                                }
                                            } else {
                                                index = 0;
                                                if (currentDifficulty == Difficulty.HARD) {
                                                    index = 2;
                                                } else if (currentDifficulty == Difficulty.MEDIUM) {
                                                    index = 1;
                                                } else if (currentDifficulty == Difficulty.IMPOSSIBLE) {
                                                    index = 3;
                                                }
                                            }
                                            if (99999 > playerHighScores.get(index)) {
                                                highScore.setText("High Score: 99999");
                                                playerHighScores.set(index, 99999);
                                            }
                                        }
                                        if (!gameComponents.getChildren().contains(scoreLog)) {
                                            gameComponents.getChildren().add(scoreLog);
                                        }
                                    }
                                    if (numbOfZeroes[0] == 6) {
                                        if (currentScore < 999999) {
                                            int numberOfZeroes = 6 - String.valueOf(currentScore).split("").length;
                                            score.setText("Score: " + "0".repeat(Math.max(0, numberOfZeroes)) + (currentScore));
                                            if (currentScore > playerHighScores.get(9)) {
                                                highScore.setText("High Score: " + "0".repeat(numberOfZeroes) + currentScore);
                                                playerHighScores.set(9, (int) currentScore);
                                            }
                                        } else {
                                            score.setText("Score: 999999");
                                            if (999999 > playerHighScores.get(9)) {
                                                playerHighScores.set(4, 999999);
                                            }
                                            if (!gameComponents.getChildren().contains(scoreLog)) {
                                                gameComponents.getChildren().add(scoreLog);
                                            }
                                        }

                                    }
                                    scoreLogCounter.setCounter(1);
                                    scoreLog.setOpacity(1);
                                    gameComponents.getChildren().remove(v.getCharacter());
                                    graves.remove(v);
                                }
                            });
                        }
                    } catch (ConcurrentModificationException e) {
                        logger.info("Exception caught but game working as expected.(P2)");
                    }
                    if (graves.isEmpty() && emptyGravesCounter.getCounter() == 0) {
                        long bonusLong = (long) Math.floor(random.nextDouble(10));
                        long currentScore = (Long.parseLong(score.getText().split(" ")[1]) + 100 + bonusLong);
                        if (numbOfZeroes[0] == 5) {
                            if (currentScore < 99999) {
                                int numberOfZeroes = 5 - String.valueOf(currentScore).split("").length;
                                score.setText("Score: " + "0".repeat(Math.max(0, numberOfZeroes)) + (currentScore));
                                int index;
                                Difficulty currentDifficulty = getDifficultyFromTime.get(timeInSeconds.get());
                                if (players[1] != null) {
                                    index = 5;
                                    if (currentDifficulty == Difficulty.HARD) {
                                        index = 7;
                                    } else if (currentDifficulty == Difficulty.MEDIUM) {
                                        index = 6;
                                    } else if (currentDifficulty == Difficulty.IMPOSSIBLE) {
                                        index = 8;
                                    }
                                } else {
                                    index = 0;
                                    if (currentDifficulty == Difficulty.HARD) {
                                        index = 2;
                                    } else if (currentDifficulty == Difficulty.MEDIUM) {
                                        index = 1;
                                    } else if (currentDifficulty == Difficulty.IMPOSSIBLE) {
                                        index = 3;
                                    }
                                }
                                numberOfZeroes = 5 - playerHighScores.get(index).toString().length();
                                if (currentScore > playerHighScores.get(index)) {
                                    highScore.setText("High Score: " + "0".repeat(numberOfZeroes) + currentScore);
                                    playerHighScores.set(index, (int) currentScore);
                                }
                            } else {
                                score.setText("Score: 99999");
                                int index;
                                Difficulty currentDifficulty = getDifficultyFromTime.get(timeInSeconds.get());
                                if (players[1] != null) {
                                    index = 5;
                                    if (currentDifficulty == Difficulty.HARD) {
                                        index = 7;
                                    } else if (currentDifficulty == Difficulty.MEDIUM) {
                                        index = 6;
                                    } else if (currentDifficulty == Difficulty.IMPOSSIBLE) {
                                        index = 8;
                                    }
                                } else {
                                    index = 0;
                                    if (currentDifficulty == Difficulty.HARD) {
                                        index = 2;
                                    } else if (currentDifficulty == Difficulty.MEDIUM) {
                                        index = 1;
                                    } else if (currentDifficulty == Difficulty.IMPOSSIBLE) {
                                        index = 3;
                                    }
                                }
                                if (99999 > playerHighScores.get(index)) {
                                    highScore.setText("High Score: 99999");
                                    playerHighScores.set(index, 99999);
                                }
                            }
                            if (!gameComponents.getChildren().contains(scoreLog)) {
                                gameComponents.getChildren().add(scoreLog);
                            }
                        }
                        if (numbOfZeroes[0] == 6) {
                            if (currentScore < 999999) {
                                int numberOfZeroes = 6 - String.valueOf(currentScore).split("").length;
                                score.setText("Score: " + "0".repeat(Math.max(0, numberOfZeroes)) + (currentScore));
                                if (players[1] == null) {
                                    if (currentScore > playerHighScores.get(4)) {
                                        highScore.setText("High Score: " + "0".repeat(numberOfZeroes) + currentScore);
                                        playerHighScores.set(4, (int) currentScore);
                                    }
                                } else {
                                    if (currentScore > playerHighScores.get(9)) {
                                        highScore.setText("High Score: " + "0".repeat(numberOfZeroes) + currentScore);
                                        playerHighScores.set(9, (int) currentScore);
                                    }
                                }
                            } else {
                                score.setText("Score: 999999");
                                if (players[1] == null) {
                                    if (999999 > playerHighScores.get(4)) {
                                        playerHighScores.set(4, 999999);
                                    }
                                } else {
                                    if (999999 > playerHighScores.get(9)) {
                                        playerHighScores.set(9, 999999);
                                    }
                                }
                            }
                            if (!gameComponents.getChildren().contains(scoreLog)) {
                                gameComponents.getChildren().add(scoreLog);
                            }
                        }
                        /*if (numbOfZeroes[0] == 5) {
                            if (currentScore < 99999) {
                                int numberOfZeroes = 5 - String.valueOf(currentScore).split("").length;
                                score.setText("Score: " + "0".repeat(Math.max(0, numberOfZeroes)) + (currentScore));
                            } else {
                                score.setText("Score: 99999");
                            }
                            //System.out.println(Long.parseLong(score.getText().split(" ")[1]));
                            if (!gameComponents.getChildren().contains(scoreLog)) {
                                gameComponents.getChildren().add(scoreLog);
                            }
                        }
                        if (numbOfZeroes[0] == 6) {
                            if (currentScore < 999999) {
                                int numberOfZeroes = 6 - String.valueOf(currentScore).split("").length;
                                score.setText("Score: " + "0".repeat(Math.max(0, numberOfZeroes)) + (currentScore));
                            } else {
                                score.setText("Score: 999999");
                            }
                            //System.out.println(Long.parseLong(score.getText().split(" ")[1]));
                            if (!gameComponents.getChildren().contains(scoreLog)) {
                                gameComponents.getChildren().add(scoreLog);
                            }
                        }*/
                        bonus.setText("Bonus Score: \n" + bonusLong);
                        scoreLogBonus.setText("+" + bonusLong + "(Bonus)");
                        if (!gameComponents.getChildren().contains(bonus)) {
                            gameComponents.getChildren().add(bonus);
                        }
                        gameComponents.getChildren().remove(timer);
                        time.setTime(new Time(0, 0, 0).toLocalTime());
                        timer.setText(time.toString());
                        gameComponents.getChildren().addAll(graveRespawning, waveClearedText);
                        emptyGravesCounter.increaseCounter();
                    } else if (graves.isEmpty() && emptyGravesCounter.getCounter() < 120 && emptyGravesCounter.getCounter() > 0) {
                        if (emptyGravesCounter.getCounter() % 40 == 0) {
                            if (waveClearedText.getFill().toString().equals("0xccffccff")) {
                                waveClearedText.setFill(Color.rgb(0, 0, 0));
                            } else if (waveClearedText.getFill().toString().equals("0x000000ff")) {
                                waveClearedText.setFill(Color.rgb(204, 255, 204));
                            }
                        }



                        emptyGravesCounter.increaseCounter();
                    } else if (graves.isEmpty() && emptyGravesCounter.getCounter() == 120) {

                        gameComponents.getChildren().removeAll(graveRespawning);
                        gameComponents.getChildren().add(graveRespawned);
                        player.getPlayerBody().setTranslateX(66);
                        player.getPlayerBody().setTranslateY(50.46875 - 10 + 70);
                        player.getPlayerHead().setTranslateX(((double) 128 / 2) + 17);
                        player.getPlayerHead().setTranslateY(8.0 * 1.328125 + 62);
                        player.changeLegPositions(70, 220);
                        player.changeHand(HandPosition.HIDE);
                        if (players[1] != null) {
                            players[1].getPlayerBody().setTranslateX(66);
                            players[1].getPlayerBody().setTranslateY(50.46875 - 10 + 70);
                            players[1].getPlayerHead().setTranslateX(((double) 128 / 2) + 17);
                            players[1].getPlayerHead().setTranslateY(8.0 * 1.328125 + 62);
                            players[1].changeLegPositions(70, 220);
                            players[1].changeHand(HandPosition.HIDE);
                        }
                        graves.addAll(GraveUtil.graveGrid(14, 6, 0, 0, 80, 135, 128, 165));

                        gameComponents.getChildren().remove(waveClearedText);
                        gameComponents.getChildren().addAll(graves.stream().map(Character::getCharacter).toList());
                        gameComponents.getChildren().add(waveClearedText);
                        emptyGravesCounter.increaseCounter();
                        moveAfterGraveRespawn.increaseCounter();
                        moveAfterGraveRespawnP2.increaseCounter();
                    } else if (emptyGravesCounter.getCounter() < 150 && emptyGravesCounter.getCounter() > 120) {
                        emptyGravesCounter.increaseCounter();
                        scoreLogBonus.setOpacity(scoreLogBonus.getOpacity() - scoreLogBonus.getOpacity() / 120);
                        scoreLogClear.setOpacity(scoreLogClear.getOpacity() - scoreLogClear.getOpacity() / 120);
                        bonus.setOpacity(bonus.getOpacity() - bonus.getOpacity() / 120);

                    } else if (emptyGravesCounter.getCounter() == 150) {

                        emptyGravesCounter.increaseCounter();
                        scoreLogBonus.setOpacity(scoreLogBonus.getOpacity() - scoreLogBonus.getOpacity() / 120);
                        scoreLogClear.setOpacity(scoreLogClear.getOpacity() - scoreLogClear.getOpacity() / 120);
                        bonus.setOpacity(bonus.getOpacity() - bonus.getOpacity() / 120);

                        moveAfterGraveRespawn.resetCounter();
                        moveAfterGraveRespawnP2.resetCounter();

                    } else if (emptyGravesCounter.getCounter() < 210 && emptyGravesCounter.getCounter() > 120) {
                        if (emptyGravesCounter.getCounter() == 160) {
                            if (waveClearedText.getFill().toString().equals("0xccffccff")) {
                                waveClearedText.setFill(Color.rgb(0, 0, 0));
                            } else if (waveClearedText.getFill().toString().equals("0x000000ff")) {
                                waveClearedText.setFill(Color.rgb(204, 255, 204));
                            }
                        }
                        if (emptyGravesCounter.getCounter() > 165) {
                            gameComponents.getChildren().remove(waveClearedText);
                        }
                        emptyGravesCounter.increaseCounter();
                        scoreLogBonus.setOpacity(scoreLogBonus.getOpacity() - scoreLogBonus.getOpacity() / 120);
                        scoreLogClear.setOpacity(scoreLogClear.getOpacity() - scoreLogClear.getOpacity() / 120);
                        bonus.setOpacity(bonus.getOpacity() - bonus.getOpacity() / 120);
                        graveRespawned.setOpacity(graveRespawned.getOpacity() - graveRespawned.getOpacity() / 70);
                    } else if (emptyGravesCounter.getCounter() == 210) {
                        gameComponents.getChildren().removeAll(graveRespawned, bonus, scoreLogBonus, scoreLogClear);
                        time.setTime(new Time(0, 0, 0).toLocalTime());
                        timer.setText(time.toString());
                        gameComponents.getChildren().add(timer);
                        emptyGravesCounter.resetCounter();
                        scoreLogClear.setOpacity(1);
                        scoreLogBonus.setOpacity(1);
                    }

                    boolean isPlayerCollidingWithScoreText = ((player.getPlayerHead().getTranslateX() == 209.0) && (player.getPlayerHead().getTranslateY() == 72.625)) || (((player.getPlayerHead().getTranslateX() == 337.0) && (player.getPlayerHead().getTranslateY() == 72.625))) && (gameComponents.getChildren().contains(scoreLog));

                    if ((isPlayerCollidingWithScoreText) && scoreFadeCounter.getCounter() < 60) {
                        scoreLog.setOpacity(scoreLog.getOpacity() - scoreLog.getOpacity() / 60);
                        scoreFadeCounter.increaseCounter();
                    } else if ((!isPlayerCollidingWithScoreText) && scoreFadeCounter.getCounter() == 60) {
                        scoreLog.setOpacity(1);
                        scoreFadeCounter.resetCounter();
                        gameComponents.getChildren().removeAll(scoreLog);
                    } else if (isPlayerCollidingWithScoreText && scoreFadeCounter.getCounter() == 60) {
                        gameComponents.getChildren().removeAll(scoreLog);
                        scoreLog.setOpacity(1);
                        scoreFadeCounter.resetCounter();
                    } else {
                        if (players[1] != null) {
                            boolean isPlayerTwoCollidingWithScoreText = ((players[1].getPlayerHead().getTranslateX() == 209.0) && (players[1].getPlayerHead().getTranslateY() == 72.625)) || (((players[1].getPlayerHead().getTranslateX() == 337.0) && (players[1].getPlayerHead().getTranslateY() == 72.625))) && (gameComponents.getChildren().contains(scoreLog));

                            if ((isPlayerTwoCollidingWithScoreText) && scoreFadeCounter.getCounter() < 60) {
                                scoreLog.setOpacity(scoreLog.getOpacity() - scoreLog.getOpacity() / 60);
                                scoreFadeCounter.increaseCounter();
                            } else if ((!isPlayerTwoCollidingWithScoreText) && scoreFadeCounter.getCounter() == 60) {
                                scoreLog.setOpacity(1);
                                scoreFadeCounter.resetCounter();
                                gameComponents.getChildren().removeAll(scoreLog);
                            } else if (isPlayerTwoCollidingWithScoreText && scoreFadeCounter.getCounter() == 60) {
                                gameComponents.getChildren().removeAll(scoreLog);
                                scoreLog.setOpacity(1);
                                scoreFadeCounter.resetCounter();
                            }
                        }
                    }
                    if (time.getInSeconds() == timeInSeconds.get()) {
                        player.getPlayerBody().setTranslateX(66);
                        player.getPlayerBody().setTranslateY(50.46875 - 10 + 70);
                        player.getPlayerHead().setTranslateX(((double) 128 / 2) + 17);
                        player.getPlayerHead().setTranslateY(8.0 * 1.328125 + 62);
                        player.changeLegPositions(70, 220);
                        player.changeHand(HandPosition.HIDE);
                        if (players[1] != null) {
                            players[1].getPlayerBody().setTranslateX(66);
                            players[1].getPlayerBody().setTranslateY(50.46875 - 10 + 70);
                            players[1].getPlayerHead().setTranslateX(((double) 128 / 2) + 17);
                            players[1].getPlayerHead().setTranslateY(8.0 * 1.328125 + 62);
                            players[1].changeLegPositions(70, 220);
                            players[1].changeHand(HandPosition.HIDE);
                        }
                        gameComponents.getChildren().removeAll(graves.stream().map(Character::getCharacter).toList());
                        if (!new HashSet<>(gameComponents.getChildren()).containsAll(List.of(youLost, scoreResetRetry, noTime))) {
                            gameComponents.getChildren().addAll(youLost, scoreResetRetry, noTime);
                            youLost.toFront();
                            scoreResetRetry.toFront();
                            noTime.toFront();
                        }
                        score.setText("Score: " + "0".repeat(numbOfZeroes[0]));
                        gameComponents.getChildren().remove(timer);
                    } else if (timeInSeconds.get() + 3 == time.getInSeconds()) {
                        graves.clear();
                        graves.addAll(GraveUtil.graveGrid(14, 6, 0, 0, 80, 135, 128, 165));
                        graves.stream().map(Character::getCharacter).forEach(Node::toBack);
                        if (!new HashSet<>(gameComponents.getChildren()).containsAll(graves.stream().map(Character::getCharacter).toList())) {
                            gameComponents.getChildren().addAll(graves.stream().map(Character::getCharacter).toList());
                        }
                        gameComponents.getChildren().removeAll(youLost, scoreResetRetry, noTime);
                        if (!gameComponents.getChildren().contains(timer)) {
                            gameComponents.getChildren().add(timer);
                        }
                        time.setTime(new Time(0, 0, 0).toLocalTime());
                        timer.setText(time.toString());
                    }
                }
                long nanoTimeNow = System.nanoTime();
                if (!gameComponents.getChildren().contains(escapeMenu) && !gameComponents.getChildren().contains(youWon)) {
                    time.setTime(time.getTime().plusNanos(nanoTimeNow - before));
                    timer.setText(time.toString());
                }
                timeInBetweenHandleCalls.setNanoTime(nanoTimeNow);
                if (timeBeforeLastEscape[0] < 3) {
                    timeBeforeLastEscape[0] = timeBeforeLastEscape[0] + (nanoTimeNow - (double) before) / 1000000000;
                }
            }
        }.start();
        stage.setScene(gameScene);
        stage.setFullScreen(true);
        stage.setTitle("Grave Demolisher 1.0 Initial Release");
        stage.show();
        stage.setOnCloseRequest(e -> {
            try {
                if (!scoresFile.toFile().delete()) {
                    throw new IOException("File could not be deleted");
                }
                logger.info("Deleted file. Recreating it.");
                scoresFile.toFile().createNewFile();
                Files.write(scoresFile, ((ArrayListUtils.toString(playerHighScores)).getBytes()));
            } catch(IOException exception){
                logger.error("Unable to save high scores",exception);
            }
        });
        logger.info("Main game window appeared.");
    }
}
