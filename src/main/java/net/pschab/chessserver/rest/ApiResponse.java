package net.pschab.chessserver.rest;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ApiResponse {
    protected HttpStatus status;
    protected String message;

    protected ApiResponse() {}

    public ApiResponse(HttpStatus status) {
        this.status = status;
    }

    public ApiResponse(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
