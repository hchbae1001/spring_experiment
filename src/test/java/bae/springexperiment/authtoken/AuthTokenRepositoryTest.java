package bae.springexperiment.authtoken;

import bae.springexperiment.entity.AuthToken;
import bae.springexperiment.entity.Member;
import bae.springexperiment.entity.enumerate.DeviceType;
import bae.springexperiment.entity.enumerate.OS;
import bae.springexperiment.member.MemberService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class AuthTokenRepositoryTest {
    @Autowired
    AuthTokenRepository authTokenRepository;

    @Autowired
    MemberService memberService;

    static Member setupMember;

    @BeforeEach
    void setup(){
        Member build = Member.builder()
                .email("authtoken_setup@test.com")
                .phone("01000000101")
                .name("test_member")
                .nickname("test_Member")
                .isRemoved(false)
                .build();
        memberService.save(build);
        setupMember = memberService.findByEmail(build.getEmail());

        AuthToken token = AuthToken.builder()
                .member(setupMember)
                .os(OS.ios)
                .deviceType(DeviceType.phone)
                .refreshToken("ios_phone_refreshToken")
                .build();
        AuthToken token2 = AuthToken.builder()
                .member(setupMember)
                .os(OS.ios)
                .deviceType(DeviceType.tablet)
                .refreshToken("ios_tablet_refreshToken")
                .build();

        authTokenRepository.save(token);
        authTokenRepository.save(token2);

        List<AuthToken> allByMemberId = authTokenRepository.findAllByMemberId(setupMember.getId());
        assertThat(allByMemberId.size()).isEqualTo(2);

    }

    @AfterEach
    void clean(){
        authTokenRepository.deleteAllByMemberId(setupMember.getId());
        memberService.deleteById(setupMember.getId());
    }


    @Test
    @DisplayName("Pass_AuthTokenRepository_findAllByMemberId")
    void findAllByMemberId() {
        assertThat(authTokenRepository.findAllByMemberId(setupMember.getId()).size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Pass_AuthTokenRepository_findByMemberIdAndDeviceType")
    void findByMemberIdAndDeviceType(){
        // phone and tablet already created by setup()
        assertThat(authTokenRepository.findAuthTokenByMemberIdAndDeviceType(setupMember.getId(), DeviceType.phone)).isPresent();
        assertThat(authTokenRepository.findAuthTokenByMemberIdAndDeviceType(setupMember.getId(), DeviceType.tablet)).isPresent();
        assertThat(authTokenRepository.findAuthTokenByMemberIdAndDeviceType(setupMember.getId(), DeviceType.web)).isNotPresent();
    }

    @Test
    @DisplayName("Pass_AuthTokenRepository_findByRefreshToken")
    void findByRefreshToken(){
        assertThat(authTokenRepository.findByRefreshToken("ios_tablet_refreshToken")).isPresent();
        assertThat(authTokenRepository.findByRefreshToken("ios_phone_refreshToken")).isPresent();
        assertThat(authTokenRepository.findByRefreshToken("ios_web_refreshToken")).isNotPresent();
    }

    @Test
    @DisplayName("Pass_AuthTokenRepository_saveAndDeleteAuthToken")
    void saveAndDeleteAuthToken(){
        AuthToken token = AuthToken.builder()
                .member(setupMember)
                .os(OS.web)
                .deviceType(DeviceType.web)
                .refreshToken("web_tablet_refreshToken")
                .build();
        authTokenRepository.save(token);
        assertThat(authTokenRepository.findAllByMemberId(setupMember.getId()).size()).isEqualTo(3);
        authTokenRepository.deleteByMemberIdAndDeviceType(setupMember.getId(), DeviceType.web);
        assertThat(authTokenRepository.findAllByMemberId(setupMember.getId()).size()).isEqualTo(2);
        assertThat(authTokenRepository.findAuthTokenByMemberIdAndDeviceType(setupMember.getId(), DeviceType.web)).isNotPresent();
    }

}
