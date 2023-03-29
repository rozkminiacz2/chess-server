package net.pschab.chessserver.rest;

import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class ControllerTestHelper {

    static void assertApiError(ApiError apiError, HttpStatus httpStatus, String message, String debugMessage) {
        assertApiResponse(apiError, httpStatus, message);
        assertThat(apiError.getDebugMessage()).isEqualTo(debugMessage);
    }

    static void assertApiResponse(ApiResponse apiResponse, HttpStatus httpStatus, String message) {
        assertThat(apiResponse).isNotNull();
        assertThat(apiResponse.getStatus()).isEqualTo(httpStatus);
        assertThat(apiResponse.getMessage()).isEqualTo(message);
    }
}
