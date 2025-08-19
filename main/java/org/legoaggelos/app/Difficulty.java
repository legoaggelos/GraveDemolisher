package org.legoaggelos.app;

public enum Difficulty {
    EASY,
    MEDIUM,
    HARD,
    IMPOSSIBLE,
    FREEPLAY;
    public static int indexOf(Difficulty difficulty) {
        return switch (difficulty) {
            case EASY -> 0;
            case MEDIUM -> 1;
            case HARD -> 2;
            case IMPOSSIBLE -> 3;
            case FREEPLAY -> 4;
        };
    }
}
