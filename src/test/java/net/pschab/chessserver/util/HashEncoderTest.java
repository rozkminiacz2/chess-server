package net.pschab.chessserver.util;

import org.junit.jupiter.api.Test;

import static net.pschab.chessserver.util.HashEncoder.encode;
import static net.pschab.chessserver.util.HashEncoder.matches;
import static org.assertj.core.api.Assertions.assertThat;

public class HashEncoderTest {

    @Test
    public void shouldEncodeText() {
        String textToEncode = "password1234";
        String encodedText = encode(textToEncode);

        assertThat(matches(textToEncode,encodedText)).isTrue();
    }
}
