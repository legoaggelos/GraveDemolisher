package org.legoaggelos.file;

import org.legoaggelos.file.OS.OperatingSystem;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class ScoresFileUtil {
    private static final String OS = System.getProperty("os.name").toLowerCase();
    public static boolean isWindows = (OS.contains("win"));
    public static boolean isMac = (OS.contains("mac"));
    public static boolean isUnix = (OS.contains("nix") || OS.contains("nux") || OS.indexOf("aix") > 0);
    public static Path determinePath() {
        String userHome = System.getProperty("user.home");

        return switch (getOperatingSystem()) {
            case OperatingSystem.UNIX -> Path.of(userHome, ".config", "gravedemolisher", "scores.csv");
            case OperatingSystem.WINDOWS -> Path.of(userHome, "AppData", "Local", "GraveDemolisher", "scores.csv");
            case OperatingSystem.MACOS -> Path.of(userHome, "Library", "Application Support", "GraveDemolisher", "scores.csv");
            case UNSUPPORTED -> Path.of(userHome,"GraveDemolisher", "scores.csv");
        };
    }
    private static OperatingSystem getOperatingSystem(){
        if(isWindows){
            return OperatingSystem.WINDOWS;
        }
        if(isUnix){
            return OperatingSystem.UNIX;
        }
        if(isMac){
            return OperatingSystem.MACOS;
        }
        return OperatingSystem.UNSUPPORTED;
    }
    public static boolean areFileContentsValid(String fileContents){
        String[] splitFileContents=fileContents.split(",");
        ArrayList<Integer> splitFileContentsInt=new ArrayList<>();
        if(splitFileContents.length!=10){
            return false;
        }
        try{
            splitFileContentsInt.addAll(Arrays.stream(splitFileContents).map(Integer::parseInt).toList());
        } catch (NumberFormatException e) {
            return false;
        }
        AtomicBoolean whatToReturn = new AtomicBoolean(true);
        splitFileContentsInt.forEach(v->{
            if(v>999999){
                whatToReturn.set(false);
            }
        });
        return whatToReturn.get();
    }
    public static boolean areFileContentsValid(Path file) throws IOException {
        return areFileContentsValid(getFileContents(file));
    }
    public static String getFileContents(Path file) throws IOException{
        Scanner scanner = new Scanner(file);
        StringBuilder fileContents = new StringBuilder();
        while(scanner.hasNextLine()){
            fileContents.append(scanner.nextLine());
        }
        return fileContents.toString();
    }
}
