package net.pschab.chessserver.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
public class PlayerValidatorTest {

    @Autowired
    private PlayerValidator playerValidator;

    @Test
    public void shouldValidateCorrectPlayerName() {
        assertThatCode(() -> playerValidator.validatePlayerName("name")).doesNotThrowAnyException();
    }

    @Test
    public void shouldNotValidateBlankPlayerName() {
        assertThatThrownBy(() -> playerValidator.validatePlayerName(" "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Player name cannot be empty.");
    }

    @Test
    public void shouldNotValidateEmptyPlayerName() {
        assertThatThrownBy(() -> playerValidator.validatePlayerName(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Player name cannot be empty.");
    }

    @Test
    public void shouldNotValidateBlankPlayerPassword() {
        assertThatThrownBy(() -> playerValidator.validatePlayerPassword(" "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Player password cannot be empty.");
    }

    @Test
    public void shouldNotValidateEmptyPlayerPassword() {
        assertThatThrownBy(() -> playerValidator.validatePlayerPassword(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Player password cannot be empty.");
    }
}
