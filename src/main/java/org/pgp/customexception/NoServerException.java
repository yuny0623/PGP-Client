package org.pgp.customexception;

public class NoServerException extends RuntimeException {
    public NoServerException(String message){
        super(message);
    }
}
