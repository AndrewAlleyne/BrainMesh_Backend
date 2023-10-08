package com.alleynejr.brainmesh_backend.exceptions;

public class RefreshTokenException extends RuntimeException {
    public RefreshTokenException(String token, String s) {
        super(token + " " + s);
    }
}
