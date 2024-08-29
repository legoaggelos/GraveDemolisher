package org.legoaggelos.exceptions;

public class PlayerTwoWithoutPlayerOneException extends RuntimeException {
    public PlayerTwoWithoutPlayerOneException(String message) {
        super(message);
    }
}
