package bae.springexperiment.authtoken;

import bae.springexperiment.entity.AuthToken;
import bae.springexperiment.entity.enumerate.DeviceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthTokenServiceV1 implements AuthTokenService{
    private final AuthTokenRepository authTokenRepository;


    @Override
    public List<AuthToken> findAuthTokensByMemberId() {
        return List.of();
    }

    @Override
    public List<AuthToken> findAuthTokensByMemberId(Long member_id) {
        return List.of();
    }

    @Override
    public AuthToken findByMemberIdAndDeviceType(DeviceType deviceType) {
        return null;
    }

    @Override
    public AuthToken findByMemberIdAndDeviceType(Long member_id, DeviceType deviceType) {
        return null;
    }

    @Override
    public void deleteAllByMemberId(Long member_id) {

    }

    @Override
    public void saveAuthToken(AuthToken authToken) {

    }
}
