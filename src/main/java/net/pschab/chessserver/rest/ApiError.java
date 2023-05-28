package net.pschab.chessserver.rest;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Objects;

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

    ApiError(HttpStatus status, String message, String debugMessage) {
        super(status, message);
        this.debugMessage = debugMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ApiError apiError = (ApiError) o;
        return Objects.equals(debugMessage, apiError.debugMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), debugMessage);
    }

    @Override
    public String toString() {
        return "ApiError{" +
                "debugMessage='" + debugMessage + '\'' +
                ", status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
