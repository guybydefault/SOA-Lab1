package ru.guybydefault.lab2.exception;

import java.net.ConnectException;

public class ServerUnavailableException  extends RuntimeException{
    public ServerUnavailableException(Exception cause) {
        super(cause);
    }
}
