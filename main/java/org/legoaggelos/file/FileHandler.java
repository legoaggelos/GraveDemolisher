package org.legoaggelos.file;

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
    public static boolean overwriteHighScores(List<Integer> playerHighScores) {
        try {
            if (!scoresFile.toFile().delete()) {
                throw new IOException("File could not be deleted");
            }
            logger.info("Deleted file. Recreating it.");
            scoresFile.toFile().createNewFile();
            Files.write(scoresFile, ((ArrayListUtils.toString(playerHighScores)).getBytes()));
            return true;
        } catch (IOException exception) {
            logger.error("Unable to save high scores", exception);
            return false;
        }
    }
    public static List<Integer> loadHighScores() {
        Path scoresFile = Paths.get(ScoresFileUtil.determinePath().toString());
        List<Integer> newList = new ArrayList<>();
        try {
            File directory = new File(scoresFile.toString().replace("scores.csv", ""));
            directory.mkdirs();
            if (scoresFile.toFile().createNewFile()) {
                logger.info("scores.csv file created");
                Files.write(scoresFile, ("0, 0, 0, 0, 0 ,0, 0, 0, 0, 0").getBytes());
                newList.addAll(Arrays.asList(0, 0, 0, 0, 0 ,0, 0, 0, 0, 0));
                return newList;
            } else {
                logger.info("scores.csv file exists. Trying to read from it.");
                if (areFileContentsValid(scoresFile)) {
                    //playerHighScores.clear();
                    //playerHighScores.addAll(Arrays.stream(getFileContents(scoresFile).split(",")).map(Integer::parseInt).toList());
                    newList.addAll(Arrays.stream(getFileContents(scoresFile).split(",")).map(Integer::parseInt).toList());
                    return newList;
                } else {
                    newList.addAll(Arrays.asList(0, 0, 0, 0, 0 ,0, 0, 0, 0, 0));
                    return newList;
                }
            }
        } catch (IOException ioException) {
            logger.error("IOException caught when doing initial file procedure. Game will continue to work, but high scores may be unexpected.", ioException);

        }
        newList.addAll(Arrays.asList(0, 0, 0, 0, 0 ,0, 0, 0, 0, 0));
        return newList;
    }

}
