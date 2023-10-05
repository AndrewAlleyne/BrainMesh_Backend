package com.alleynejr.brainmesh_backend.exceptions;

public class ApiError {
    private String code;
    private String message;

    // Static method for convenience
    public static ApiError of(String code, String message) {
        ApiError error = new ApiError();
        error.setCode(code);
        error.setMessage(message);
        return error;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
