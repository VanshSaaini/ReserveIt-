package com.Reserveit.v1.exception;

/** Thrown when an authenticated user tries to act outside what their role/ownership permits. */
public class ForbiddenActionException extends RuntimeException {
    public ForbiddenActionException(String message) {
        super(message);
    }
}
