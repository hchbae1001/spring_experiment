package bae.springexperiment.authtoken;

import bae.springexperiment.entity.AuthToken;
import bae.springexperiment.entity.Member;
import bae.springexperiment.entity.enumerate.DeviceType;
import bae.springexperiment.entity.enumerate.OS;
import bae.springexperiment.error.CustomException;
import bae.springexperiment.member.MemberService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class AuthTokenServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    AuthTokenService authTokenService;

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

        authTokenService.save(token);
        authTokenService.save(token2);

        List<AuthToken> allByMemberId = authTokenService.findByMemberId(setupMember.getId());
        assertThat(allByMemberId.size()).isEqualTo(2);
    }

    @AfterEach
    void clean(){
        authTokenService.deleteAllByMemberId(setupMember.getId());
        memberService.deleteById(setupMember.getId());
    }

    @Test
    @DisplayName("Pass_AuthTokenService_findByMemberId")
    void findByMemberId(){
        assertThat(authTokenService.findByMemberId(setupMember.getId()).size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Pass_AuthTokenService_findByMemberIdAndDeviceType")
    void findByMemberIdAndDeviceType(){
        AuthToken tablet = authTokenService.findByMemberIdAndDeviceType(setupMember.getId(), DeviceType.tablet);
        AuthToken phone = authTokenService.findByMemberIdAndDeviceType(setupMember.getId(), DeviceType.phone);
        assertThat(tablet).isNotNull();
        assertThat(phone).isNotNull();
        assertThatThrownBy(()-> authTokenService.findByMemberIdAndDeviceType(setupMember.getId(), DeviceType.web))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("Fail_AuthTokenService_WhenMultipleLoginsOnSameDeviceType")
    void save() {
        AuthToken token = AuthToken.builder()
                .member(setupMember)
                .os(OS.ios)
                .deviceType(DeviceType.phone)
                .refreshToken("ios_phone_refreshToken")
                .build();
        assertThatThrownBy(()-> authTokenService.save(token))
                .isInstanceOf(CustomException.class);

        AuthToken token2 = AuthToken.builder()
                .member(setupMember)
                .os(OS.ios)
                .deviceType(DeviceType.tablet)
                .refreshToken("ios_tablet_refreshToken")
                .build();
        assertThatThrownBy(()-> authTokenService.save(token2))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("Pass_AuthTokenService_updateRefreshToken")
    void updateRefreshToken(){
        AuthToken phone = authTokenService.findByMemberIdAndDeviceType(setupMember.getId(), DeviceType.phone);
        String newRefreshToken = "ios_phone_updated_refreshToken";
        authTokenService.updateRefreshToken(setupMember.getId(), DeviceType.phone ,newRefreshToken);
        AuthToken updatedPhone = authTokenService.findByMemberIdAndDeviceType(setupMember.getId(), DeviceType.phone);
        assertThat(updatedPhone.getRefreshToken()).isEqualTo(newRefreshToken);
    }

}
