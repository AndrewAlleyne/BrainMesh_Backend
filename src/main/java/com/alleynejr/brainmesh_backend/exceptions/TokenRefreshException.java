package com.alleynejr.brainmesh_backend.exceptions;

public class TokenRefreshException extends RuntimeException {
    public TokenRefreshException(String p0, String s) {
        super(p0 + " " + s);
    }
}
