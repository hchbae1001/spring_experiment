package bae.springexperiment.authtoken;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthTokenServiceV1 implements AuthTokenService{
    private final AuthTokenRepository authTokenRepository;



}
