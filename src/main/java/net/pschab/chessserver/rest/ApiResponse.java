package net.pschab.chessserver.rest;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiResponse that = (ApiResponse) o;
        return status == that.status && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, message);
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
