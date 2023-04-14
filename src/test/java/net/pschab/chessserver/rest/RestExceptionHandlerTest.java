package net.pschab.chessserver.rest;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;

public class RestExceptionHandlerTest {
    private final RestExceptionHandler handler = new RestExceptionHandler();

    @Test
    void shouldValidateDuplicateKeyExceptionApiError() {
        String errorMessage = "Player with name PlayerRestTester1 already exists.";
        final ResponseEntity<Object> responseEntity =
                handler.handleDuplicateKeyException(new DuplicateKeyException(errorMessage));

        assertThat(responseEntity.getBody()).isInstanceOf(ApiError.class);
        ApiError apiError = (ApiError) responseEntity.getBody();

        assertThat(apiError).isNotNull();
        assertThat(apiError.getMessage()).isEqualTo("Duplicate key.");
        assertThat(apiError.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(apiError.getDebugMessage()).isEqualTo(errorMessage);
    }

    @Test
    void shouldValidateIllegalArgumentExceptionApiError() {
        String errorMessage = "Player name cannot be empty.";
        final ResponseEntity<Object> responseEntity =
                handler.handleIllegalArgumentException(new IllegalArgumentException(errorMessage));

        assertThat(responseEntity.getBody()).isInstanceOf(ApiError.class);
        ApiError apiError = (ApiError) responseEntity.getBody();

        assertThat(apiError).isNotNull();
        assertThat(apiError.getMessage()).isEqualTo("Invalid data.");
        assertThat(apiError.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(apiError.getDebugMessage()).isEqualTo(errorMessage);
    }

    @Test
    void shouldValidateNoSuchElementExceptionApiError() {
        String errorMessage = "No player with name: PlayerRestTester2";
        final ResponseEntity<Object> responseEntity =
                handler.handleNoSuchElementException(new NoSuchElementException(errorMessage));

        assertThat(responseEntity.getBody()).isInstanceOf(ApiError.class);
        ApiError apiError = (ApiError) responseEntity.getBody();

        assertThat(apiError).isNotNull();
        assertThat(apiError.getMessage()).isEqualTo("The item does not exist.");
        assertThat(apiError.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(apiError.getDebugMessage()).isEqualTo(errorMessage);
    }
}
