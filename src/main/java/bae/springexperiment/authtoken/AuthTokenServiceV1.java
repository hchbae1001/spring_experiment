package bae.springexperiment.authtoken;

import bae.springexperiment.config.jwt.Auth;
import bae.springexperiment.entity.AuthToken;
import bae.springexperiment.entity.enumerate.DeviceType;
import bae.springexperiment.error.CustomException;
import bae.springexperiment.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthTokenServiceV1 implements AuthTokenService{
    private final AuthTokenRepository authTokenRepository;


    @Override
    public List<AuthToken> findByMemberId(Long member_id) {
        return authTokenRepository.findAllByMemberId(member_id);
    }

    @Override
    public AuthToken findByMemberIdAndDeviceType(Long member_id, DeviceType deviceType) {
        return authTokenRepository.findAuthTokenByMemberIdAndDeviceType(member_id, deviceType).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_EXIST_INFO)
        );
    }

    @Override
    @Transactional
    public void deleteAllByMemberId(Long member_id) {
        authTokenRepository.deleteAllByMemberId(member_id);
    }

    @Override
    @Transactional
    public void save(AuthToken authToken) {
        Long member_id = authToken.getMember().getId();
        DeviceType deviceType = authToken.getDeviceType();
        if(authTokenRepository.existsByMemberIdAndDeviceType(member_id,deviceType)){
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_DEVICE_TYPE);
        }
        authTokenRepository.save(authToken);
    }

    @Override
    @Transactional
    public void updateRefreshToken(Long member_id, DeviceType deviceType, String refreshToken) {
        AuthToken authToken = authTokenRepository.findAuthTokenByMemberIdAndDeviceType(member_id, deviceType).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_EXIST_INFO)
        );
        authToken.setRefreshToken(refreshToken);
        authTokenRepository.save(authToken);
    }

    @Override
    public void deleteByMemberIdAndDeviceType(Long member_id, DeviceType deviceType) {
        if(!authTokenRepository.existsByMemberIdAndDeviceType(member_id, deviceType)){
            throw new CustomException(ErrorCode.NOT_EXIST_INFO);
        }
        authTokenRepository.deleteByMemberIdAndDeviceType(member_id, deviceType);
    }
}
