package bae.springexperiment.member;

import bae.springexperiment.CommonResponse;
import bae.springexperiment.entity.Member;
import bae.springexperiment.entity.enumerate.Role;
import bae.springexperiment.member.dto.request.LoginRequest;
import bae.springexperiment.member.dto.request.SaveMemberRequest;
import bae.springexperiment.member.dto.response.LoginResponse;
import bae.springexperiment.member.dto.response.MemberCommonResponse;
import bae.springexperiment.util.BcryptUtil;
import bae.springexperiment.config.jwt.Auth;
import bae.springexperiment.config.jwt.AuthInformation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
@Slf4j
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @PostMapping("/")
    public void save(@RequestBody SaveMemberRequest request){
        Member member = Member.builder()
                .phone(request.phone())
                .name(request.name())
                .nickname(request.nickname())
                .email(request.email())
                .role(Role.normal)
                .password(BcryptUtil.encodedPassword(request.password()))
                .isRemoved(false)
                .build();
        memberService.save(member);
    }

    @GetMapping("/{member_id}")
    public ResponseEntity<?> findById(@PathVariable long member_id){
        Member member = memberService.findById(member_id);
        return ResponseEntity.ok(CommonResponse.success(new MemberCommonResponse(member)));
    }

    @GetMapping("/list")
    public ResponseEntity<?> findAll(){
        List<MemberCommonResponse> list = memberService.findAll().stream()
                .map(MemberCommonResponse::new)
                .toList();
        return ResponseEntity.ok(CommonResponse.success(list));
    }

    @DeleteMapping("/hard/{member_id}")
    public void hardDelete(@PathVariable long member_id){
        //Todo - Should Check Auth in memberService
        memberService.deleteById(member_id);
    }

    @DeleteMapping("/soft/{member_id}")
    public void softDelete(@PathVariable long member_id){
        //Todo - Should Check Auth in memberService
        memberService.softDeleteById(member_id);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request){
        LoginResponse login = memberService.login(request);
        CommonResponse<LoginResponse> response = CommonResponse.success(login);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/renew")
    public ResponseEntity<?> renewToken(HttpServletRequest request){
        String refreshToken = request.getHeader("x-refresh-token");
        log.info(refreshToken);
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new IllegalArgumentException("Refresh token is missing");
        }
        LoginResponse loginResponse = memberService.renewToken(refreshToken);
        CommonResponse<LoginResponse> response = CommonResponse.success(loginResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/logout")
    public void logout(){
        memberService.logout();
    }
}
