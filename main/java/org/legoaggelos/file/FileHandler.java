package org.legoaggelos.file;

import javafx.util.Pair;
import org.legoaggelos.util.ArrayListUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.legoaggelos.app.Application.logger;
import static org.legoaggelos.file.ScoresFileUtil.areFileContentsValid;
import static org.legoaggelos.file.ScoresFileUtil.getFileContents;

public class FileHandler {
    private static final Path scoresFile = Paths.get(ScoresFileUtil.determinePath().toString());
    private static final Path optionsFile = Paths.get(scoresFile.toString().replace("scores.csv", "options.txt"));
    public static boolean overwriteFile(String text, Path file) {
        try {
            if (!file.toFile().delete()) {
                logger.error(("File could not be deleted"));
                return false;
            }
            logger.info("Deleted file. Recreating it.");
            file.toFile().createNewFile();
            Files.write(file, text.getBytes());
            return true;
        } catch (IOException exception) {
            logger.error("Unable to save high scores", exception);
            return false;
        }
    }
    public static boolean overwriteOptions(int volume, boolean toggle) {
        return overwriteFile(volume + "," + toggle, optionsFile);
    }
    public static boolean overwriteHighScores(List<Integer> playerHighScores) {
        return overwriteFile((ArrayListUtils.toString(playerHighScores)),scoresFile);
    }
    public static Pair<Double, Boolean> loadVolumeSettings() {
        String string = loadFile(optionsFile, new File(optionsFile.toString().replace("options.txt", "")),"3,true");
        return new Pair<>(Double.parseDouble(string.split(",")[0])/10D, Boolean.parseBoolean(string.split(",")[1]));
    }
    public static String loadFile(Path file, File directory, String defaultString) {
        try {
            directory.mkdirs();
            if (file.toFile().createNewFile()) {
                logger.info(file.getFileName().toFile().toString()+ " file created");
                Files.write(file, defaultString.getBytes());
                return defaultString;
            } else {
                logger.info(file.getFileName().toFile().toString()+" file exists. Trying to read from it.");
                if (areFileContentsValid(file)) {
                    //playerHighScores.clear();
                    //playerHighScores.addAll(Arrays.stream(getFileContents(scoresFile).split(",")).map(Integer::parseInt).toList());
                    return getFileContents(file);
                } else {
                    return defaultString;
                }
            }
        } catch (IOException ioException) {
            logger.error("IOException caught when doing initial file procedure. Game will continue to work, but high scores/volume settings may be unexpected.", ioException);

        }
        return defaultString;
    }
    public static List<Integer> highScoresFromString(String string) {
        String[] splitFileContents=string.split(",");
        ArrayList<Integer> splitFileContentsInt=new ArrayList<>();
        assert splitFileContents.length==10;
        for (String str : splitFileContents) {
            splitFileContentsInt.add(Integer.parseInt(str));
        }
        return splitFileContentsInt;
    }
    public static List<Integer> loadHighScores() {
        return highScoresFromString(loadFile(scoresFile, new File(scoresFile.toString().replace("scores.csv", "")), "0, 0, 0, 0, 0, 0, 0, 0, 0, 0"));
    }

}
