package bae.springexperiment.authtoken;

import bae.springexperiment.entity.AuthToken;
import bae.springexperiment.entity.enumerate.DeviceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {
    List<AuthToken> findAllByMemberId(long member_id);
    Optional<AuthToken> findAuthTokenByMemberIdAndDeviceType(Long member_id, DeviceType deviceType);
    Optional<AuthToken> findByRefreshToken(String refreshToken);

    void deleteAllByMemberId(Long member_id);
    void deleteByMemberIdAndRefreshToken(Long member_id, String refreshToken);
    void deleteByMemberIdAndDeviceType(Long member_id, DeviceType deviceType);

    Boolean existsByMemberId(Long member_id);
    Boolean existsByMemberIdAndDeviceType(Long member_id, DeviceType deviceType);
}
