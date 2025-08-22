package org.legoaggelos.app.gui;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.legoaggelos.app.Difficulty;
import org.legoaggelos.objects.Grave;
import org.legoaggelos.objects.entities.Character;
import org.legoaggelos.objects.entities.player.HandPosition;
import org.legoaggelos.objects.entities.player.Player;
import org.legoaggelos.sound.SoundHandler;
import org.legoaggelos.time.TimerTime;
import org.legoaggelos.util.ChangeableBoolean;
import org.legoaggelos.util.Counter;
import org.legoaggelos.util.GraveUtil;

import java.security.SecureRandom;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static org.legoaggelos.app.Application.logger;

public class GraveDemolisherGuiComponentsHolder {
    private final Pane gameComponents = new Pane();

    //Adjust for resolution in nodeInit.
    private void nodeInit(Shape node, Color color, Double x, Double y) {
        node.setFill(color);
        if (x != null) {
            node.setTranslateX(x * xResolutionRatio);
        }
        if (y != null) {
            node.setTranslateY(y * yResolutionRatio);
        }

    }

    //Adjust for font resolution in textInit.
    private void textInit(Text node, Color color, Font font, Double x, Double y) {
        nodeInit(node, color, x, y);
        node.setFont(Font.font(font.getSize() * Math.max(xResolutionRatio, yResolutionRatio))); //If the aspect ratio is the same, these are equal. If it isnt, use the biggest one, so it the font isnt too tall/wide, just too short/thin which is better, because it prevents texts colliding

    }

    private final double xResolutionRatio;
    private final double yResolutionRatio;
    private final Text timer;
    private final VBox optionsVBox;
    private VBox mainMenuComponents;
    private Text title;
    private Text playSurvival;
    private Text playFreeplay;
    private Text options;
    private Text quit;
    private Text backFromOptions;
    private VBox howToPlay;
    private Text howToPlayTitle;
    private Text backFromHowToPlay;
    private Text guide;
    private VBox controls;
    private Text controlsTitle;
    private Text backFromControls;
    private Text gotoControls;
    private VBox creditsVBox;
    private Text creditsTitle;
    private Text creditsLegoaggelos;
    private Text creditsJedElinoffScotthomas;
    private Text creditsTns;
    private Text credits;
    private Text backFromCredits;
    private VBox escapeMenu;
    private Text resume;
    private Text gotoMainMenu;
    private Text quitGame;
    final double[] timeBeforeLastEscape = {1};
    private Text graveRespawning;
    private Text graveRespawned;
    private Text score;
    private Text highScore;
    private Text bonus;
    private Text scoreLog;
    private Text scoreLogClear;
    private Text scoreLogBonus;
    private Text waveClearedText;
    private Text youLost;
    private Text noTime;
    private Text scoreResetRetry;
    private Text youWon;
    private Text noRanTime;
    private Text beatingTheGame;
    private VBox difficultySelection;
    private Text difficultySelectPrompt;
    private Text easySelect;
    private Text mediumSelect;
    private Text hardSelect;
    private Text impossibleSelect;
    private Text backFromSelection;
    private final Scene gameScene;
    private final HashMap<KeyCode, Boolean> pressedKeys;
    private VBox musicVBox;
    private HBox volumeHBox;
    private Text gotoMusic;
    private Text musicTitle;
    private Text volumeState; //[----...--]
    private Text volumeUp;
    private Text volumeSwitch;
    private Text volumeDown;
    private Text backFromMusic;

    public double getxResolutionRatio() {
        return xResolutionRatio;
    }

    public double getyResolutionRatio() {
        return yResolutionRatio;
    }

    public Text getCreditsTns() {
        return creditsTns;
    }

    public VBox getMusicVBox() {
        return musicVBox;
    }

    public HBox getVolumeHBox() {
        return volumeHBox;
    }

    public Text getGotoMusic() {
        return gotoMusic;
    }

    public Text getMusicTitle() {
        return musicTitle;
    }

    public Text getVolumeState() {
        return volumeState;
    }

    public Text getVolumeUp() {
        return volumeUp;
    }

    public Text getVolumeSwitch() {
        return volumeSwitch;
    }

    public Text getVolumeDown() {
        return volumeDown;
    }

    public Text getBackFromMusic() {
        return backFromMusic;
    }

    private final Text controlsText = new Text("""
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
    private final Text howToPlayText = new Text("""
             Survival:
             There are waves of evil graves in front of you.\s
             Your mission is to demolish all the graves in time.
             If you take too much time to clear a wave, you lose.
             Your score increases every time you demolish a grave
             or clear a wave.
             There are 4 difficulties:\s
             Easy, you have 1 minute to clear a wave,
             Medium, you have 40 seconds to clear a wave,
             Hard, you have 30 seconds to clear a wave,
             Impossible, you have 20 seconds to clear a wave
            \s
             Freeplay:
             You have 10 minutes to clear a wave.
             Your only goal is to get as high of a score as possible.
            \s""");

    private void initMainMenu(Stage stage) {
        mainMenuComponents = new VBox();
        mainMenuComponents.setAlignment(Pos.TOP_CENTER);

        title = new Text("GRAVE DEMOLISHER");
        textInit(title, Color.GRAY, Font.font(150), null, 100D);

        playSurvival = new Text("Play Survival");
        textInit(playSurvival, Color.GRAY, Font.font(75), -10D, 300D);

        playFreeplay = new Text("Freeplay");
        textInit(playFreeplay, Color.GRAY, Font.font(75), -10D, 350D);

        options = new Text("Options");
        textInit(options, Color.GRAY, Font.font(75), -10D, 400D);

        options.setOnMouseClicked(e -> stage.getScene().setRoot(optionsVBox));

        quit = new Text("Quit");
        textInit(quit, Color.GRAY, Font.font(75), -10D, 450D);
    }

    private void initHowToPlay(Stage stage) {
        howToPlay = new VBox();
        howToPlay.setAlignment(Pos.TOP_CENTER);

        howToPlayTitle = new Text("How To Play");
        textInit(howToPlayTitle, Color.GRAY, Font.font(75), null, 0D);

        textInit(howToPlayText, Color.GRAY, Font.font(40), null, null);

        backFromHowToPlay = new Text("Back");
        textInit(backFromHowToPlay, Color.GRAY, Font.font(75), null, null);
        backFromHowToPlay.setOnMouseClicked(e -> stage.getScene().setRoot(optionsVBox));

        howToPlay.getChildren().addAll(howToPlayTitle, howToPlayText, backFromHowToPlay);

        guide = new Text("How To Play");
        textInit(guide, Color.GRAY, Font.font(75), null, 300D);
        guide.setOnMouseClicked(e -> stage.getScene().setRoot(howToPlay));
    }

    private void initControls(Stage stage) {
        controls = new VBox();
        controls.setAlignment(Pos.TOP_CENTER);

        textInit(controlsText, Color.GRAY, Font.font(50), null, null);


        controlsTitle = new Text("Controls");
        textInit(controlsTitle, Color.GRAY, Font.font(75), null, 10D);

        backFromControls = new Text("Back");
        textInit(backFromControls, Color.GRAY, Font.font(75), null, -80D);
        backFromControls.setOnMouseClicked(e -> stage.getScene().setRoot(optionsVBox));

        controls.getChildren().addAll(controlsTitle, controlsText, backFromControls);

        gotoControls = new Text("Controls");
        textInit(gotoControls, Color.GRAY, Font.font(75), null, 350D);
        gotoControls.setOnMouseClicked(e -> stage.getScene().setRoot(controls));
    }

    private void initCredits(Stage stage) {
        creditsVBox = new VBox();
        creditsVBox.setAlignment(Pos.TOP_CENTER);

        creditsTitle = new Text("Credits");
        textInit(creditsTitle, Color.GRAY, Font.font(75), null, 10D);

        creditsLegoaggelos = new Text("legoaggelos - Coding, texturing, testing");
        textInit(creditsLegoaggelos, Color.GRAY, Font.font(50), null, 10D);

        creditsJedElinoffScotthomas = new Text("Jed Elinoff, Scott Thomas - Making the show that inspired this game(RC9GN)");
        textInit(creditsJedElinoffScotthomas, Color.GRAY, Font.font(50), null, 10D);

        creditsTns = new Text("Thanasis Vrettakos - Music composer/performer");
        textInit(creditsTns, Color.GRAY, Font.font(50), null, 10D);

        credits = new Text("Credits");
        textInit(credits, Color.GRAY, Font.font(75), null, 400D);
        credits.setOnMouseClicked(e -> stage.getScene().setRoot(creditsVBox));

        backFromCredits = new Text("Back");
        textInit(backFromCredits, Color.GRAY, Font.font(75), null, 650D);
        backFromCredits.setOnMouseClicked(e -> stage.getScene().setRoot(optionsVBox));

        creditsVBox.getChildren().addAll(creditsTitle, creditsLegoaggelos, creditsJedElinoffScotthomas, creditsTns, backFromCredits);
    }

    private void initVolume(Stage stage) {
        musicVBox = new VBox();
        musicVBox.setAlignment(Pos.TOP_CENTER);
        
        musicTitle = new Text("Music");
        textInit(musicTitle, Color.GRAY, Font.font(75), null, 10D);
        
        volumeSwitch = new Text("On"); //On by default, TODO add saving in files
        textInit(volumeSwitch, Color.GRAY, Font.font(75), null, 100D);
        
        volumeHBox = new HBox();
        volumeHBox.setAlignment(Pos.CENTER);
        volumeHBox.setSpacing(50*xResolutionRatio);
        volumeHBox.setTranslateY(125D*yResolutionRatio);
        
        volumeDown = new Text("<");
        textInit(volumeDown, Color.GRAY, Font.font(85), null, null);
        
        volumeState = new Text("[ " + "-".repeat(20) + " ]");
        textInit(volumeState, Color.GRAY, Font.font(75), null, null);
        
        volumeUp = new Text(">");
        textInit(volumeUp, Color.GRAY, Font.font(85), null, null);
        
        volumeHBox.getChildren().addAll(volumeDown, volumeState, volumeUp);

        backFromMusic = new Text("Back");
        textInit(backFromMusic, Color.GRAY, Font.font(75), null, 630D);
        backFromMusic.setOnMouseClicked(e -> stage.getScene().setRoot(optionsVBox));

        musicVBox.getChildren().addAll(musicTitle, volumeSwitch, volumeHBox, backFromMusic);

        gotoMusic = new Text("Audio");
        textInit(gotoMusic, Color.GRAY, Font.font(75), null, 250D);
        gotoMusic.setOnMouseClicked(e -> stage.getScene().setRoot(musicVBox));
    }

    private void initOptions(Stage stage) {
        //options

        optionsVBox.setAlignment(Pos.TOP_CENTER);

        backFromOptions = new Text("Back");
        textInit(backFromOptions, Color.GRAY, Font.font(75), null, 550D);
        backFromOptions.setOnMouseClicked(e -> stage.getScene().setRoot(mainMenuComponents));

        initHowToPlay(stage);

        initControls(stage);

        initCredits(stage);

        initVolume(stage);

        mainMenuComponents.getChildren().addAll(title, playSurvival, playFreeplay, options, quit);
        optionsVBox.getChildren().addAll(gotoMusic, guide, gotoControls, credits, backFromOptions);
    }

    private void initEscapeMenu(Stage stage) {
        escapeMenu = new VBox();
        escapeMenu.setAlignment(Pos.CENTER);
        escapeMenu.setTranslateY(300 * yResolutionRatio);
        escapeMenu.setTranslateX(770 * xResolutionRatio);

        resume = new Text("Resume");
        textInit(resume, Color.GRAY, Font.font(75), null, null);
        resume.setOnMouseClicked(e -> gameComponents.getChildren().remove(escapeMenu));

        gotoMainMenu = new Text("Main Menu");
        textInit(gotoMainMenu, Color.GRAY, Font.font(75), null, null);
        gotoMainMenu.setOnMouseClicked(e -> stage.getScene().setRoot(mainMenuComponents));

        quitGame = new Text("Quit Game");
        textInit(quitGame, Color.GRAY, Font.font(75), null, null);

        escapeMenu.getChildren().addAll(resume, gotoMainMenu, quitGame);
    }

    private void initGraveRespawningText(Stage stage) {
        graveRespawning = new Text("Graves Respawning...");
        textInit(graveRespawning, Color.rgb(0, 77, 0), Font.font(80), (double) 1920 / 2 - 350, 60D);

        graveRespawned = new Text("Graves Respawned!");
        textInit(graveRespawned, Color.rgb(0, 77, 0), Font.font(80), (double) 1920 / 2 - 350, 60D);
    }

    private void initScoreText(Stage stage) {
        score = new Text("Score: 00000");
        textInit(score, Color.rgb(0, 77, 0), Font.font(70), 0D, 60D);

        highScore = new Text("High Score: 00000");
        textInit(highScore, Color.rgb(0, 77, 0), Font.font(60), 1400D, 60D);

        bonus = new Text("");
        textInit(bonus, Color.rgb(0, 77, 0), Font.font(28), 400D, 22D);

        gameComponents.getChildren().addAll(score, timer, highScore);
    }

    private void initLogText(Stage stage) {
        scoreLog = new Text("+1(Grave Demolished)");
        textInit(scoreLog, Color.rgb(0, 77, 0), Font.font(17), 200D, 77D);

        scoreLogClear = new Text("+100(Wave Cleared)");
        textInit(scoreLogClear, Color.rgb(0, 77, 0), Font.font(17), 200D, 92D);

        scoreLogBonus = new Text("+6(Bonus)");
        textInit(scoreLogBonus, Color.rgb(0, 77, 0), Font.font(17), 200D, 102D);
    }

    private void initClearText(Stage stage) {
        waveClearedText = new Text("WAVE CLEARED!");
        textInit(waveClearedText, Color.rgb(204, 255, 204), Font.font(175), (double) 1920 / 2 - 550, (double) 1080 / 2 - 60);
    }

    private void initLossText(Stage stage) {
        youLost = new Text("You Lost!");
        textInit(youLost, Color.rgb(153, 0, 0), Font.font(175), (double) 1920 / 2 - 350, (double) 1080 / 2 - 180);

        noTime = new Text("You ran out of time!");
        textInit(noTime, Color.rgb(153, 0, 0), Font.font(150), (double) 1920 / 2 - 550, (double) 1080 / 2 - 60);

        scoreResetRetry = new Text("Score Reset! Try again!");
        textInit(scoreResetRetry, Color.rgb(140, 0, 0), Font.font(120), (double) 1920 / 2 - 525, (double) 1080 / 2 + 60);
    }

    private void initWinText(Stage stage) {
        youWon = new Text("You Beat The Game!");
        textInit(youWon, Color.rgb(102, 255, 102), Font.font(175), (double) 1920 / 2 - 725, (double) 1080 / 2 - 280);

        noRanTime = new Text("You reached max score without \n       ever running out of time!");
        textInit(noRanTime, Color.rgb(179, 89, 0), Font.font(125), (double) 1920 / 2 - 825, (double) 1080 / 2 - 60);

        beatingTheGame = new Text("Thank you for playing the game!\n       Press R to restart!");
        textInit(beatingTheGame, Color.rgb(179, 89, 0), Font.font(125), (double) 1920 / 2 - 825, (double) 1080 / 2 + 240);
    }

    private void initDifficultyMenu(Stage stage) {
        difficultySelection = new VBox();
        difficultySelection.setAlignment(Pos.CENTER);

        difficultySelectPrompt = new Text("Select difficulty:");
        textInit(difficultySelectPrompt, Color.GRAY, Font.font(75), null, null);

        easySelect = new Text("Easy");
        textInit(easySelect, Color.rgb(37, 139, 0).brighter(), Font.font(75), null, null);

        mediumSelect = new Text("Medium");
        textInit(mediumSelect, Color.rgb(187, 194, 4), Font.font(75), null, null);

        hardSelect = new Text("Hard");
        textInit(hardSelect, Color.rgb(194, 4, 4), Font.font(75), null, null);

        impossibleSelect = new Text("Impossible");
        textInit(impossibleSelect, Color.rgb(68, 0, 89), Font.font(75), null, null);

        backFromSelection = new Text("Back");
        textInit(backFromSelection, Color.GRAY, Font.font(75), null, 200D);
        backFromSelection.setOnMouseClicked(e -> stage.getScene().setRoot(mainMenuComponents));

        difficultySelection.getChildren().addAll(difficultySelectPrompt, easySelect, mediumSelect, hardSelect, impossibleSelect, backFromSelection);

        playSurvival.setOnMouseClicked(e -> stage.getScene().setRoot(difficultySelection));
    }

    public GraveDemolisherGuiComponentsHolder(Stage stage, TimerTime time, double xResolutionRatio, double yResolutionRatio, SoundHandler soundHandler) {
        this.xResolutionRatio = xResolutionRatio;
        this.yResolutionRatio = yResolutionRatio;
        timer = new Text(time.toString());
        textInit(timer, Color.rgb(37, 139, 0), Font.font(80), (double) 1920 / 2 - 120, 60D);

        optionsVBox = new VBox(); //early initialisation for menuing

        //Main menu
        initMainMenu(stage);

        //Options
        initOptions(stage);

        //Escape Menu
        initEscapeMenu(stage);

        gameScene = new Scene(mainMenuComponents, 1920 * xResolutionRatio, 1080 * yResolutionRatio);
        stage.setScene(gameScene);
        gameScene.setFill(Color.rgb(38, 17, 0));
        pressedKeys = new HashMap<>();
        gameScene.setOnKeyPressed(event -> pressedKeys.put(event.getCode(), Boolean.TRUE));
        gameScene.setOnKeyReleased(event -> pressedKeys.put(event.getCode(), Boolean.FALSE));


        initGraveRespawningText(stage);

        initScoreText(stage);

        initLogText(stage);

        initClearText(stage);

        initLossText(stage);

        initWinText(stage);

        initDifficultyMenu(stage);
    }

    public void showWinningText() {
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
    }

    public boolean hasWon() {
        return score.getText().equalsIgnoreCase("score: 99999") || score.getText().equalsIgnoreCase("score: 999999");
    }

    public boolean canShowWin(Stage stage) {
        return stage.getScene().getRoot().equals(gameComponents) && !gameComponents.getChildren().contains(escapeMenu);
    }

    public void escape(Stage stage) {
        if (gameComponents.getChildren().contains(escapeMenu)) {
            gameComponents.getChildren().remove(escapeMenu);
        } else {
            gameComponents.getChildren().add(escapeMenu);
        }
        stage.setFullScreen(true);
    }

    public boolean shouldIncreaseTime() {
        return !gameComponents.getChildren().contains(escapeMenu) && !gameComponents.getChildren().contains(youWon) && gameScene.getRoot().equals(gameComponents);
    }

    public void addScoreLog() {
        if (!gameComponents.getChildren().contains(scoreLog)) {
            gameComponents.getChildren().add(scoreLog);
        }
    }

    public void updateBonusText(short bonusLong) {
        bonus.setText("Bonus Score: \n" + bonusLong);
        scoreLogBonus.setText("+" + bonusLong + "(Bonus)");
    }

    public void addWaveClearText() {
        if (new HashSet<>(gameComponents.getChildren()).containsAll(List.of(graveRespawning, waveClearedText))) {
            return;
        }
        gameComponents.getChildren().addAll(graveRespawning, waveClearedText);
    }

    public void removeWaveClearText() {
        gameComponents.getChildren().removeAll(graveRespawning, bonus, scoreLogBonus, scoreLogClear, waveClearedText);
    }

    public void addTimer() {
        if (gameComponents.getChildren().contains(timer)) {
            return;
        }
        gameComponents.getChildren().addAll(timer);
    }

    public void resetTimer(TimerTime time) {
        time.setTime(new Time(0, 0, 0).toLocalTime());
        timer.setText(time.toString());
    }

    public void addBonusLog() {
        if (!gameComponents.getChildren().contains(scoreLogBonus)) {
            gameComponents.getChildren().add(scoreLogBonus);
        }
    }

    public void addBonus() {
        if (!gameComponents.getChildren().contains(bonus)) {
            //System.out.println("bonus added" );
            gameComponents.getChildren().add(bonus);
        }
    }

    public long getCurrentScore() {
        return Long.parseLong(score.getText().split(" ")[1]);
    }

    public void removeScoreLog() {
        gameComponents.getChildren().remove(scoreLog);
    }

    public void reduceScoreLogOpacity(double reductionAmount) {
        scoreLog.setOpacity(scoreLog.getOpacity() - reductionAmount);
    }

    public void resetWaveClearScoreLogTextOpacities() {
        scoreLogClear.setOpacity(1);
        scoreLogBonus.setOpacity(1);
        bonus.setOpacity(1);
    }

    public void addGraveRespawnedText() {
        gameComponents.getChildren().removeAll(graveRespawning);
        gameComponents.getChildren().add(graveRespawned);
    }

    public void updateClearTextOpacities(double amount) {
        scoreLogBonus.setOpacity(scoreLogBonus.getOpacity() - amount);
        scoreLogClear.setOpacity(scoreLogClear.getOpacity() - amount);
    }

    public void addLostText() {
        if (!new HashSet<>(gameComponents.getChildren()).containsAll(List.of(youLost, scoreResetRetry, noTime))) {
            gameComponents.getChildren().addAll(youLost, scoreResetRetry, noTime);
            youLost.toFront();
            scoreResetRetry.toFront();
            noTime.toFront();
        }
    }

    public void removeLostText() {
        gameComponents.getChildren().removeAll(youLost, scoreResetRetry, noTime);
    }

    public void removeGraves(List<Grave> graves) {
        gameComponents.getChildren().removeAll(graves.stream().map(org.legoaggelos.objects.entities.Character::getCharacter).toList());
    }

    public void addGraves(List<Grave> graves) {
        if (!new HashSet<>(gameComponents.getChildren()).containsAll(graves.stream().map(Character::getCharacter).toList())) {
            gameComponents.getChildren().addAll(graves.stream().map(Character::getCharacter).toList());
        }
    }

    public void resetGameEnvironment(TimerTime time, int numOfZeroes, List<Integer> playerHighScores, Difficulty difficulty) {
        gameComponents.getChildren().remove(escapeMenu);

        gameComponents.getChildren().removeAll(youLost, scoreResetRetry, noTime, scoreLog, scoreLogClear, scoreLogBonus, bonus, waveClearedText, graveRespawning, graveRespawned, youWon, noRanTime, beatingTheGame);
        score.setText("Score: " + "0".repeat(numOfZeroes));
        int numberOfZeroes = (difficulty == Difficulty.FREEPLAY ? 6 : 5) - String.valueOf(playerHighScores.get(Difficulty.indexOf(difficulty))).split("").length;

        highScore.setText("High Score: " + "0".repeat(numberOfZeroes) + playerHighScores.get(Difficulty.indexOf(difficulty)));
        gameComponents.getChildren().remove(timer);
        if (!gameComponents.getChildren().contains(timer)) {
            gameComponents.getChildren().add(timer);
        }

        time.setTime(new Time(0, 0, 0).toLocalTime());
        timer.setText(time.toString());
    }

    public void addPlayer(Player player) {
        gameComponents.getChildren().addAll(player.getPlayer().stream().map(org.legoaggelos.objects.entities.Character::getCharacter).toList());
    }

    public void removePlayer(Player player) {
        gameComponents.getChildren().removeAll(player.getPlayer().stream().map(org.legoaggelos.objects.entities.Character::getCharacter).toList());
    }

    public Pane getGameComponents() {
        return gameComponents;
    }

    public Text getTimer() {
        return timer;
    }

    public VBox getOptionsVBox() {
        return optionsVBox;
    }

    public VBox getMainMenuComponents() {
        return mainMenuComponents;
    }

    public Text getTitle() {
        return title;
    }

    public Text getPlaySurvival() {
        return playSurvival;
    }

    public Text getPlayFreeplay() {
        return playFreeplay;
    }

    public Text getOptions() {
        return options;
    }

    public Text getQuit() {
        return quit;
    }

    public Text getBackFromOptions() {
        return backFromOptions;
    }

    public VBox getHowToPlay() {
        return howToPlay;
    }

    public Text getHowToPlayTitle() {
        return howToPlayTitle;
    }

    public Text getBackFromHowToPlay() {
        return backFromHowToPlay;
    }

    public Text getGuide() {
        return guide;
    }

    public VBox getControls() {
        return controls;
    }

    public Text getControlsTitle() {
        return controlsTitle;
    }

    public Text getBackFromControls() {
        return backFromControls;
    }

    public Text getGotoControls() {
        return gotoControls;
    }

    public VBox getCreditsVBox() {
        return creditsVBox;
    }

    public Text getCreditsTitle() {
        return creditsTitle;
    }

    public Text getCreditsLegoaggelos() {
        return creditsLegoaggelos;
    }

    public Text getCreditsJedElinoffScotthomas() {
        return creditsJedElinoffScotthomas;
    }

    public Text getCredits() {
        return credits;
    }

    public Text getBackFromCredits() {
        return backFromCredits;
    }

    public VBox getEscapeMenu() {
        return escapeMenu;
    }

    public Text getResume() {
        return resume;
    }

    public Text getGotoMainMenu() {
        return gotoMainMenu;
    }

    public Text getQuitGame() {
        return quitGame;
    }

    public double[] getTimeBeforeLastEscape() {
        return timeBeforeLastEscape;
    }

    public Text getGraveRespawning() {
        return graveRespawning;
    }

    public Text getGraveRespawned() {
        return graveRespawned;
    }

    public Text getScore() {
        return score;
    }

    public Text getHighScore() {
        return highScore;
    }

    public Text getBonus() {
        return bonus;
    }

    public Text getScoreLog() {
        return scoreLog;
    }

    public Text getScoreLogClear() {
        return scoreLogClear;
    }

    public Text getScoreLogBonus() {
        return scoreLogBonus;
    }

    public Text getWaveClearedText() {
        return waveClearedText;
    }

    public Text getYouLost() {
        return youLost;
    }

    public Text getNoTime() {
        return noTime;
    }

    public Text getScoreResetRetry() {
        return scoreResetRetry;
    }

    public Text getYouWon() {
        return youWon;
    }

    public Text getNoRanTime() {
        return noRanTime;
    }

    public Text getBeatingTheGame() {
        return beatingTheGame;
    }

    public VBox getDifficultySelection() {
        return difficultySelection;
    }

    public Text getDifficultySelectPrompt() {
        return difficultySelectPrompt;
    }

    public Text getEasySelect() {
        return easySelect;
    }

    public Text getMediumSelect() {
        return mediumSelect;
    }

    public Text getHardSelect() {
        return hardSelect;
    }

    public Text getImpossibleSelect() {
        return impossibleSelect;
    }

    public Text getBackFromSelection() {
        return backFromSelection;
    }

    public Scene getGameScene() {
        return gameScene;
    }

    public HashMap<KeyCode, Boolean> getPressedKeys() {
        return pressedKeys;
    }

    public Text getControlsText() {
        return controlsText;
    }

    public Text getHowToPlayText() {
        return howToPlayText;
    }

    public void switchWaveClearTextColor() {
        if (waveClearedText.getFill().toString().equals("0xccffccff")) {
            waveClearedText.setFill(Color.rgb(0, 0, 0));
        } else if (waveClearedText.getFill().toString().equals("0x000000ff")) {
            waveClearedText.setFill(Color.rgb(204, 255, 204));
        }
    }

    public void addClearLog() {
        gameComponents.getChildren().add(scoreLogClear);
    }
}
