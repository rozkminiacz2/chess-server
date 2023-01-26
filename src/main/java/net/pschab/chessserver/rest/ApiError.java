package net.pschab.chessserver.rest;

import lombok.Data;
import org.springframework.http.HttpStatus;
@Data
public class ApiError extends ApiResponse {

    private String debugMessage;

    private ApiError() {
        super();
    }

    public ApiError(HttpStatus status) {
        super(status);
    }

    ApiError(HttpStatus status, Throwable ex) {
        super(status);
        this.message = "Unexpected error";
        this.debugMessage = ex.getLocalizedMessage();
    }

    ApiError(HttpStatus status, String message, Throwable ex) {
        super(status, message);
        this.debugMessage = ex.getLocalizedMessage();
    }
}
