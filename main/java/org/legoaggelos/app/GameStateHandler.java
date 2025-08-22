package org.legoaggelos.app;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.legoaggelos.app.gui.GraveDemolisherGuiComponentsHolder;
import org.legoaggelos.file.FileHandler;
import org.legoaggelos.objects.Grave;
import org.legoaggelos.objects.entities.Character;
import org.legoaggelos.objects.entities.EntityHandler;
import org.legoaggelos.objects.entities.player.HandPosition;
import org.legoaggelos.objects.entities.player.Player;
import org.legoaggelos.objects.entities.player.PlayerCount;
import org.legoaggelos.sound.SoundHandler;
import org.legoaggelos.time.NanoTime;
import org.legoaggelos.time.TimerTime;
import org.legoaggelos.util.ArrayListUtils;
import org.legoaggelos.util.ChangeableBoolean;
import org.legoaggelos.util.Counter;
import org.legoaggelos.util.GraveUtil;
import org.legoaggelos.util.player.HandleCallHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.sql.Time;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.legoaggelos.app.Application.logger;
import static org.legoaggelos.file.FileHandler.loadHighScores;

public class GameStateHandler {
    public static final double xResolutionRatio = Screen.getPrimary().getBounds().getMaxX() / 1920;
    public static double yResolutionRatio = Screen.getPrimary().getBounds().getMaxY() / 1080;
    private final ChangeableBoolean p2HasJoinedSession = new ChangeableBoolean(false);
    private final SecureRandom random = new SecureRandom();
    private static final int maxScore = 99999;
    private static final int maxFreeplayScore = 999999;
    private Difficulty currentDifficulty;
    private final Counter playerTwoLeaveJoinCounter = new Counter(); //Purpose: prevent p2 joining and leaving constantly when pressing the p2 button. On the tick the player is joining, if it is 0, it goes to 1, and when the user releases the p2 button, it goes back to 0.
    private final HashMap<Integer, Difficulty> getDifficultyFromTime = new HashMap<>();
    private final HashMap<Difficulty, Integer> getTimeFromDifficulty = new HashMap<>();
    private List<Integer> playerHighScores = new ArrayList<>();
    private final Counter scoreLogCounter = new Counter(); //initially 0, set to 1 when a player attacks a grave, and slowly disappears (by lowering opacity of scorelog). Stays on screen for about 2 seconds, but that is subject to change
    private final Counter emptyGravesCounter = new Counter(); //used for the process of a grave respawn, adding the text, the points, resetting the timer etc
    private final NanoTime timeInBetweenHandleCalls = new NanoTime(0);
    private final TimerTime time = new TimerTime(new Time(0, 0, 0).toLocalTime());
    private final GraveDemolisherGuiComponentsHolder guiHolder;
    private final EntityHandler entityHandler;
    private final Stage stage;
    private final AtomicInteger timeInSeconds = new AtomicInteger(60);
    private final HandleCallHandler handleCallHandler = new HandleCallHandler();
    private double timeBeforeLastEscape = 0;
    private final ChangeableBoolean isGameBeaten = new ChangeableBoolean(false);
    private final SoundHandler soundHandler;

    private void loadDifficulties() {
        getDifficultyFromTime.put(60, Difficulty.EASY);
        getDifficultyFromTime.put(40, Difficulty.MEDIUM);
        getDifficultyFromTime.put(30, Difficulty.HARD);
        getDifficultyFromTime.put(20, Difficulty.IMPOSSIBLE);
        getDifficultyFromTime.put(600, Difficulty.FREEPLAY);
        for (Integer key : getDifficultyFromTime.keySet()) {
            getTimeFromDifficulty.put(getDifficultyFromTime.get(key), key);
        }
    }

    public GameStateHandler(Stage stage) {
        for (int i = 0; i < 8 && playerHighScores.size() < 8; i++) {
            playerHighScores.add(0);
        }
        this.stage = stage;
        loadDifficulties();
        playerHighScores = loadHighScores();
        soundHandler = new SoundHandler(List.of("/sounds/TNS Vret - track 1.m4a")); //TODO: add loading sound from files
        guiHolder = new GraveDemolisherGuiComponentsHolder(stage, time, xResolutionRatio, yResolutionRatio, soundHandler);
        entityHandler = new EntityHandler(guiHolder.getGameComponents());
        initialiseQuitFunctions();
        initialiseDifficultySelectFunctions();
        initialiseLeaveGame();
        initialiseSoundFunctions();
    }

    private String generateVolumeText(double volume) {
        int decVolume = Math.round((float) volume * 10); //Multiply by 10, to avoid dealing with float division and potential errors(other way would have been volume / 0.1). According to MediaPlayer docs, volume is always 0.0-1.0, so it can be converted to float(we only need 0.1 accuracy here)
        return 10*decVolume + "% [ " + "-".repeat(decVolume) + " ".repeat(10 - decVolume) + " ]"; //TODO Fix the incosistent spacing
    }

    private void initialiseSoundFunctions() {
        guiHolder.getVolumeState().setText(generateVolumeText(0.3));
        guiHolder.getVolumeSwitch().setText("On");

        guiHolder.getVolumeDown().setOnMouseClicked(event -> {
            soundHandler.changeVolume(-0.1);
            guiHolder.getVolumeState().setText(generateVolumeText(soundHandler.getVolume()));
        });

        guiHolder.getVolumeUp().setOnMouseClicked(event -> {
            soundHandler.changeVolume(0.1);
            guiHolder.getVolumeState().setText(generateVolumeText(soundHandler.getVolume()));
        });

        guiHolder.getVolumeSwitch().setOnMouseClicked(
                event -> {
                    if (guiHolder.getVolumeSwitch().getText().trim().equalsIgnoreCase("On")) {
                        guiHolder.getVolumeSwitch().setText("Off");
                        soundHandler.muteAll();
                    } else {
                        guiHolder.getVolumeSwitch().setText("On");
                        soundHandler.unmuteAll();
                    }
                }
        );
    }

    public SecureRandom getRandom() {
        return random;
    }

    public Difficulty getCurrentDifficulty() {
        return currentDifficulty;
    }

    public Counter getPlayerTwoLeaveJoinCounter() {
        return playerTwoLeaveJoinCounter;
    }

    public HashMap<Integer, Difficulty> getGetDifficultyFromTime() {
        return getDifficultyFromTime;
    }

    public HashMap<Difficulty, Integer> getGetTimeFromDifficulty() {
        return getTimeFromDifficulty;
    }

    public Counter getScoreLogCounter() {
        return scoreLogCounter;
    }

    public Counter getEmptyGravesCounter() {
        return emptyGravesCounter;
    }

    public NanoTime getTimeInBetweenHandleCalls() {
        return timeInBetweenHandleCalls;
    }

    public TimerTime getTime() {
        return time;
    }

    public GraveDemolisherGuiComponentsHolder getGuiHolder() {
        return guiHolder;
    }

    public EntityHandler getEntityHandler() {
        return entityHandler;
    }

    public Stage getStage() {
        return stage;
    }

    public AtomicInteger getTimeInSeconds() {
        return timeInSeconds;
    }

    public HandleCallHandler getHandleCallHandler() {
        return handleCallHandler;
    }

    public double getTimeBeforeLastEscape() {
        return timeBeforeLastEscape;
    }

    public ChangeableBoolean getIsGameBeaten() {
        return isGameBeaten;
    }

    public List<Integer> getPlayerHighScores() {
        return playerHighScores;
    }

    public void initialiseLeaveGame() {
        difficultySelect(Difficulty.EASY);
        stage.getScene().setRoot(guiHolder.getMainMenuComponents());
    }

    public void initialiseQuitFunctions() {
        guiHolder.getQuit().setOnMouseClicked(event -> {
            FileHandler.overwriteHighScores(playerHighScores);
            stage.close();
        });
        guiHolder.getQuitGame().setOnMouseClicked(event -> {
            FileHandler.overwriteHighScores(playerHighScores);
            stage.close();
        });
    }

    public void difficultySelect(Difficulty difficulty) {
        if (difficulty == Difficulty.FREEPLAY) {
            guiHolder.getBonus().setTranslateX(430D * xResolutionRatio);
        } else {
            guiHolder.getBonus().setTranslateX(400D * xResolutionRatio);
        }
        p2HasJoinedSession.setBool(false);
        emptyGravesCounter.resetCounter();
        guiHolder.resetWaveClearScoreLogTextOpacities();
        currentDifficulty = difficulty;
        timeInSeconds.set(getTimeFromDifficulty.get(difficulty));
        time.setTime(new Time(0, 0, 0).toLocalTime());
        int numberOfZeroes = (difficulty == Difficulty.FREEPLAY ? 6 : 5);
        entityHandler.getPlayer1().resetPlayerPosition();
        if (entityHandler.isPlayer2Playing()) {
            entityHandler.getPlayer2().resetPlayerPosition();
            guiHolder.removePlayer(entityHandler.getPlayer2());
            entityHandler.nullPlayer2();
            Player.hasPlayerXJoined.put(PlayerCount.PLAYER_2, false);
        }
        guiHolder.resetGameEnvironment(time, numberOfZeroes, playerHighScores, difficulty);
        guiHolder.getGameComponents().getChildren().removeAll(entityHandler.getGraves().stream().map(Character::getCharacter).toList());
        entityHandler.resetGraves();
        if (!new HashSet<>(guiHolder.getGameComponents().getChildren()).containsAll(entityHandler.getGraves().stream().map(org.legoaggelos.objects.entities.Character::getCharacter).toList())) {
            guiHolder.getGameComponents().getChildren().addAll(entityHandler.getGraves().stream().map(Character::getCharacter).toList());
        }


        stage.getScene().setRoot(guiHolder.getGameComponents());

    }

    public void initialiseDifficultySelectFunctions() {
        guiHolder.getEasySelect().setOnMouseClicked(event -> {
            difficultySelect(Difficulty.EASY);
            soundHandler.setTrackToRepeat(0);
        });
        guiHolder.getMediumSelect().setOnMouseClicked(event -> {
            difficultySelect(Difficulty.MEDIUM);
            soundHandler.setTrackToRepeat(0);
        });
        guiHolder.getHardSelect().setOnMouseClicked(event -> {
            difficultySelect(Difficulty.HARD);
            soundHandler.setTrackToRepeat(0);
        });
        guiHolder.getImpossibleSelect().setOnMouseClicked(event -> {
            difficultySelect(Difficulty.IMPOSSIBLE);
            soundHandler.setTrackToRepeat(0);
        });
        guiHolder.getPlayFreeplay().setOnMouseClicked(event -> {
            difficultySelect(Difficulty.FREEPLAY);
            soundHandler.setTrackToRepeat(0);
        });
    }

    public void updateTime(LocalTime newTime) {
        time.setTime(newTime);
        guiHolder.getTimer().setText(time.toString());
    }

    public boolean isEscapePressed() {
        return guiHolder.getPressedKeys().getOrDefault(KeyCode.ESCAPE, false) && timeBeforeLastEscape >= 0.25;
    }

    private void addPlayer2() {
        Player p2 = entityHandler.createPlayer2();
        playerTwoLeaveJoinCounter.increaseCounter();
        p2.changeHand(HandPosition.HIDE);
        guiHolder.addPlayer(p2);
        currentDifficulty = getDifficultyFromTime.get(timeInSeconds.get());
        int index = 0;
        index = Difficulty.indexOf(currentDifficulty) + 5;
        int numberOfZeroes = (currentDifficulty == Difficulty.FREEPLAY ? 6 : 5) - String.valueOf(playerHighScores.get(index)).length();
        guiHolder.getHighScore().setText("High Score: " + "0".repeat(numberOfZeroes) + playerHighScores.get(index));
        p2HasJoinedSession.setBool(true);
        logger.info("Player 2 added.");
    }

    private void removePlayer2() {
        guiHolder.removePlayer(entityHandler.getPlayer2());
        entityHandler.nullPlayer2();
        currentDifficulty = getDifficultyFromTime.get(timeInSeconds.get());
        int index = Difficulty.indexOf(currentDifficulty) + (p2HasJoinedSession.bool() ? 5 : 0);
        int numberOfZeroes = (currentDifficulty == Difficulty.FREEPLAY ? 6 : 5) - String.valueOf(playerHighScores.get(index)).length();
        guiHolder.getHighScore().setText("High Score: " + "0".repeat(numberOfZeroes) + playerHighScores.get(index));
        Player.hasPlayerXJoined.put(PlayerCount.PLAYER_2, false);
        playerTwoLeaveJoinCounter.increaseCounter();
        logger.info("Player 2 removed.");
    }

    public boolean canPlayerStartAttack(Player player) {
        if (player == null) {
            return false;
        }
        //TODO mess with the values?
        return player.canPlayerStartAttacking() && /*makes sure player is not attacking while the graves are respawning.*/!guiHolder.getGameComponents().getChildren().contains(guiHolder.getGraveRespawning());
    }

    public boolean shouldScoreLogBeOnScreen() {
        return guiHolder.getGameComponents().getChildren().contains(guiHolder.getScoreLog());
    }

    public boolean shouldReduceScoreLogOpacity() {
        return scoreLogCounter.getCounter() > 10;
    }

    public boolean shouldRemoveScoreLog() {
        return scoreLogCounter.getCounter() >= 60;
    }

    public void updateCurrentScore(long newScore) {
        if ((newScore > maxScore && currentDifficulty != Difficulty.FREEPLAY) ||
                (newScore > maxFreeplayScore)) {
            guiHolder.getScoreLog().setText("Score: " + (currentDifficulty == Difficulty.FREEPLAY ? maxFreeplayScore : maxScore));
            return;
        }
        int numberOfZeroes = (currentDifficulty == Difficulty.FREEPLAY ? 6 : 5) - String.valueOf(newScore).length();
        guiHolder.getScore().setText("Score: " + "0".repeat(Math.max(0, numberOfZeroes)) + (newScore));
    }

    public void updateHighScore(long newScore) {
        int index = Difficulty.indexOf(currentDifficulty) + (p2HasJoinedSession.bool() ? 5 : 0);
        if (newScore <= playerHighScores.get(index)) {
            return;
        }
        if ((newScore > maxScore && currentDifficulty != Difficulty.FREEPLAY) ||
                (newScore > maxFreeplayScore)) {
            playerHighScores.set(index, (currentDifficulty == Difficulty.FREEPLAY ? maxFreeplayScore : maxScore));
            return;
        }
        playerHighScores.set(index, (int) newScore);
        if (Long.parseLong(guiHolder.getHighScore().getText().split(" ")[2]) < playerHighScores.get(index)) {
            int numberOfZeroes = (currentDifficulty == Difficulty.FREEPLAY ? 6 : 5) - String.valueOf(newScore).length();
            System.out.println(playerHighScores.get(index));
            guiHolder.getHighScore().setText("High Score: " + "0".repeat(Math.max(0, numberOfZeroes)) + playerHighScores.get(index));
        }
    }

    public void updateScore(long newScore, Difficulty difficulty) {
        updateCurrentScore(newScore);
        updateHighScore(newScore);
    }

    public void tick() {
        if (!handleCallHandler.shouldTick((short) 24)) {
            return;
        }
        if (guiHolder.getGameScene().getRoot().equals(guiHolder.getMainMenuComponents())) {
            soundHandler.stopTrack(0);
        }
        handleCallHandler.setLastHandleCall(System.currentTimeMillis());
        long before = System.nanoTime();
        if (!(timeInBetweenHandleCalls.getNanoTime() == 0)) {
            if (guiHolder.shouldIncreaseTime()) {//TODO fix this to not tick timer when out of game
                updateTime(time.getTime().plusNanos((long) (1.00000 * before - timeInBetweenHandleCalls.getNanoTime())));
            }
            if (timeBeforeLastEscape < 3) {
                timeBeforeLastEscape = timeBeforeLastEscape + (before - (double) timeInBetweenHandleCalls.getNanoTime()) / 1000000000;
            }
        }
        if (guiHolder.getPressedKeys().getOrDefault(KeyCode.F11, false)) {
            if (!stage.isFullScreen()) {
                stage.setFullScreen(true);
            }
        }
        if (isEscapePressed()) {
            guiHolder.escape(stage);
            timeBeforeLastEscape = 0;
        }

        if (guiHolder.canShowWin(stage)) {
            if (guiHolder.hasWon()) {
                entityHandler.hidePlayerHands();
                isGameBeaten.setBool(true);
            }
            if (isGameBeaten.bool()) {
                guiHolder.showWinningText();
                if (guiHolder.getPressedKeys().getOrDefault(KeyCode.R, false)) { //R is restart key
                    isGameBeaten.setBool(false);
                    difficultySelect(currentDifficulty);
                    guiHolder.getGameComponents().getChildren().removeAll(entityHandler.getGraves().stream().map(Character::getCharacter).toList());

                }
                updateTime(new Time(0, 0, 0).toLocalTime());
                timeInBetweenHandleCalls.setNanoTime(0);
                return;
            }
            //System.out.println(guiHolder.getGameComponents().getChildren().size()+" 2");
            if (guiHolder.getPressedKeys().getOrDefault(KeyCode.F12, false)) { //player 2 leave/join
                if (!entityHandler.isPlayer2Playing() && playerTwoLeaveJoinCounter.getCounter() < 1) {
                    addPlayer2();
                } else if (entityHandler.isPlayer2Playing() && playerTwoLeaveJoinCounter.getCounter() < 1) {
                    removePlayer2();
                }
            } else if (playerTwoLeaveJoinCounter.getCounter() > 0) {
                playerTwoLeaveJoinCounter.resetCounter();
            }
            //System.out.println(pressedKeys.getOrDefault(KeyCode.W, false));
            try {
                entityHandler.tickPlayerMovement(guiHolder.getPressedKeys());

                for (Player player : entityHandler.getNonNullPlayers()) {
                    if (guiHolder.getPressedKeys().getOrDefault(player.getPlayerMovementKeycodes()[4]/*4 is for attacking.*/, false) && canPlayerStartAttack(player)) {

                        player.startAttacking();
                        //guiHolder.getGameComponents().getChildren().removeAll(entityHandler.getGraves().stream().map(Character::getCharacter).toList());
                        //entityHandler.getGraves().clear();

                    }
                    if (player.shouldContinueAttacking() && !player.shouldPlayerNotContinueAttacking()) {
                        player.getAttackingCounter().increaseCounter();
                    }
                    if (player.shouldPlayerNotContinueAttacking()) {
                        player.stopAttacking();
                    }
                    player.getGlobalMoveCounter().increaseCounter();
                }
            } catch (Exception e) {
                logger.error(e);
                logger.error(e.getStackTrace());
            }
            if (shouldScoreLogBeOnScreen()) {
                guiHolder.addScoreLog();

                if (shouldReduceScoreLogOpacity()) {
                    guiHolder.reduceScoreLogOpacity(1.0 / 60);

                }
                scoreLogCounter.increaseCounter();
            } else if (shouldRemoveScoreLog()) {
                guiHolder.removeScoreLog();

            }
            //System.out.println(guiHolder.getGameComponents().getChildren().size()+" 3");
            try {
                entityHandler.getGraves().forEach(v -> {

                    //System.out.println("check 2");
                    boolean condition = v.colliding(entityHandler.getPlayer1().getPlayerFist());
                    //System.out.println(entityHandler.getPlayer1().getPlayerFist().getCharacter().getTranslateY());
                    //System.out.println(v.getTranslateY());
                    //System.out.println(new HashSet<>(guiHolder.getGameComponents().getChildren()).containsAll(List.of(v.getCharacter(), entityHandler.getPlayer1().getPlayerFist().getCharacter())));
                    if (entityHandler.isPlayer2Playing()) {
                        condition = condition || v.colliding(entityHandler.getPlayer2().getPlayerFist());
                    }
                    if (condition) {
                        //System.out.println("passed");
                        long currentScore = guiHolder.getCurrentScore() + 1;
                        updateScore(currentScore, currentDifficulty);
                        guiHolder.addScoreLog();
                        //System.out.println("passed 2");
                        scoreLogCounter.setCounter(1); //these 3 lines reset the score log by making it not invisible, adding it and starting the counter for it to eventually disappear again
                        guiHolder.getScoreLog().setOpacity(1.0);

                        //System.out.println(entityHandler.getGraves().size());
                        guiHolder.getGameComponents().getChildren().remove(v.getCharacter());
                        //System.out.println("passed 3");
                        entityHandler.getGraves().remove(v);
                        //System.out.println("passed 4");
                        //System.out.println(entityHandler.getGraves().size());
                        //System.out.println(entityHandler.getGraves().size());

                    }
                });
            } catch (ConcurrentModificationException e) {
                logger.info("Exception caught but game working as expected.(P1)");
            }
            //System.out.println(entityHandler.getGraves().size());
            //System.out.println(guiHolder.getGameComponents().getChildren().size()+"  4");
            if (entityHandler.getGraves().isEmpty() && emptyGravesCounter.getCounter() == 0) {
                short bonusLong = (short) Math.floor(random.nextDouble(10));
                long currentScore = (Long.parseLong(guiHolder.getScore().getText().split(" ")[1]) + 100 + bonusLong);
                updateScore(currentScore, currentDifficulty);
                guiHolder.addScoreLog();


                guiHolder.updateBonusText(bonusLong);
                guiHolder.addBonusLog();
                guiHolder.addBonus();
                guiHolder.addClearLog();
                guiHolder.getGameComponents().getChildren().remove(guiHolder.getTimer());
                guiHolder.addWaveClearText();
                emptyGravesCounter.increaseCounter();
            } else if (entityHandler.gravesEmpty() && emptyGravesCounter.getCounter() < 35 && emptyGravesCounter.getCounter() > 0) {
                if (emptyGravesCounter.getCounter() % 8 == 0) { //makes it switch color every 8 ticks, about 3 times a second
                    guiHolder.switchWaveClearTextColor();
                }
                guiHolder.updateClearTextOpacities(1.0 / 40);
                emptyGravesCounter.increaseCounter();
            } else if (entityHandler.gravesEmpty() && emptyGravesCounter.getCounter() == 35) {
                guiHolder.removeWaveClearText();
                guiHolder.getGameComponents().getChildren().add(guiHolder.getGraveRespawned());


                guiHolder.resetTimer(time);
                entityHandler.resetPlayers();
                for (Player player : entityHandler.getNonNullPlayers()) {
                    player.getMoveAfterGraveRespawn().resetCounter();
                }
                entityHandler.resetGraves();

                guiHolder.getGameComponents().getChildren().addAll(entityHandler.getGraves().stream().map(Character::getCharacter).toList());

                emptyGravesCounter.increaseCounter();
            } else if (emptyGravesCounter.getCounter() < 45 && emptyGravesCounter.getCounter() > 35) {
                emptyGravesCounter.increaseCounter();

            } else if (emptyGravesCounter.getCounter() == 45) {
                emptyGravesCounter.increaseCounter();


            } else if (emptyGravesCounter.getCounter() < 70 && emptyGravesCounter.getCounter() > 45) {
                emptyGravesCounter.increaseCounter();
                guiHolder.getGraveRespawned().setOpacity(guiHolder.getGraveRespawned().getOpacity() - 1.0 / 15);
            } else if (emptyGravesCounter.getCounter() == 70) {
                guiHolder.getGameComponents().getChildren().remove(guiHolder.getGraveRespawned());
                if (!guiHolder.getGameComponents().getChildren().contains(guiHolder.getTimer())) {
                    guiHolder.getGameComponents().getChildren().add(guiHolder.getTimer());
                }
                emptyGravesCounter.resetCounter();
                guiHolder.resetWaveClearScoreLogTextOpacities();
            }

                    /*boolean isPlayerCollidingWithScoreText = ((player.getPlayerHead().getTranslateX() == 209.0) && (player.getPlayerHead().getTranslateY() == 72.625)) || (((player.getPlayerHead().getTranslateX() == 337.0) && (player.getPlayerHead().getTranslateY() == 72.625))) && (gameComponents.getChildren().contains(scoreLog));

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
                    }*/
            //System.out.println(time.getInSeconds());
            if (time.getInSeconds() == timeInSeconds.get()) {//time in seconds is a semi-constant representing the max time for this difficulty, time is the current timer of the stage
                for (Player player : entityHandler.getNonNullPlayers()) {
                    player.resetPlayerPosition();
                }
                guiHolder.removeGraves(entityHandler.getGraves());
                guiHolder.addLostText();
                guiHolder.getScore().setText("Score: " + "0".repeat(currentDifficulty == Difficulty.FREEPLAY ? 6 : 5));
                guiHolder.getGameComponents().getChildren().remove(guiHolder.getTimer()); //dont reset it yet, so it can go on until the reset
            } else if (timeInSeconds.get() + 3 == time.getInSeconds()) {
                entityHandler.resetGraves();
                guiHolder.addGraves(entityHandler.getGraves());
                guiHolder.removeLostText();
                guiHolder.resetTimer(time);
                guiHolder.addTimer();
            }
        }

        long nanoTimeNow = System.nanoTime();
        if (guiHolder.shouldIncreaseTime()) {
            updateTime(time.getTime().plusNanos(nanoTimeNow - before));
        }
        timeInBetweenHandleCalls.setNanoTime(nanoTimeNow);
        if (timeBeforeLastEscape < 3) {
            timeBeforeLastEscape = timeBeforeLastEscape + (nanoTimeNow - (double) before) / 1000000000;
        }
    }
}
