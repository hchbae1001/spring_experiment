package bae.springexperiment.util;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import static org.springframework.security.crypto.bcrypt.BCrypt.checkpw;
import static org.springframework.security.crypto.bcrypt.BCrypt.hashpw;

@Component
public class BcryptUtil {
    public static String encodedPassword(String password){
        return hashpw(password, BCrypt.gensalt(11));
    }
    public static boolean matchPassword(String rawPassword, String encodedPassword){
        if(!checkpw(rawPassword, encodedPassword)){
            throw new RuntimeException("Password not matched");
        }
        return true;
    }
}