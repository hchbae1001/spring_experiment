package bae.springexperiment.util;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BcryptUtilTest {

    static String originalPassword = "PASSWORD";

    @Test
    @DisplayName("Pass_Password_Matched")
    void match(){
        String encodedPassword = BcryptUtil.encodedPassword(originalPassword);
        assertThat(BcryptUtil.matchPassword(originalPassword,encodedPassword)).isTrue();
    }

    @Test
    @DisplayName("Fail_Password_Not_Matched")
    void notMatched(){
        String encodedPassword = BcryptUtil.encodedPassword(originalPassword);
        assertThatThrownBy(() -> BcryptUtil.matchPassword("Wrong_Password", encodedPassword))
                .isInstanceOf(RuntimeException.class)
                .hasMessageEndingWith("Password not matched");
    }
}
