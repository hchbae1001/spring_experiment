package bae.springexperiment.member;

import bae.springexperiment.CommonResponse;
import bae.springexperiment.entity.Member;
import bae.springexperiment.member.dto.request.*;
import bae.springexperiment.member.dto.response.LoginResponse;
import bae.springexperiment.member.dto.response.MemberCommonResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    private final MemberFacade memberFacade;

    @PostMapping("/")
    public void save(HttpServletRequest request, @RequestBody SaveMemberRequest saveMemberRequest) {
        log.info("Received request to save member with email: {} | URI: {}", saveMemberRequest.email(), request.getRequestURI());
        memberFacade.save(saveMemberRequest);
        log.info("Successfully saved member with email: {} | URI: {}", saveMemberRequest.email(), request.getRequestURI());
    }

    @GetMapping("/{member_id}")
    public ResponseEntity<?> findById(HttpServletRequest request, @PathVariable long member_id) {
        log.info("Received request to find member by ID: {} | URI: {}", member_id, request.getRequestURI());
        Member member = memberFacade.findById(member_id);
        log.info("Successfully found member by ID: {} | URI: {}", member_id, request.getRequestURI());
        return ResponseEntity.ok(CommonResponse.success(new MemberCommonResponse(member)));
    }

    @GetMapping("/list")
    public ResponseEntity<?> findAll(HttpServletRequest request) {
        log.info("Received request to list all members | URI: {}", request.getRequestURI());
        List<MemberCommonResponse> list = memberService.findAll().stream()
                .map(MemberCommonResponse::new)
                .toList();
        log.info("Successfully listed all members | URI: {}", request.getRequestURI());
        return ResponseEntity.ok(CommonResponse.success(list));
    }

    @DeleteMapping("/hard/{member_id}")
    public void hardDelete(HttpServletRequest request, @PathVariable long member_id) {
        log.info("Received request to hard delete member by ID: {} | URI: {}", member_id, request.getRequestURI());
        memberService.deleteById(member_id);
        log.info("Successfully hard deleted member by ID: {} | URI: {}", member_id, request.getRequestURI());
    }

    @DeleteMapping("/soft/{member_id}")
    public void softDelete(HttpServletRequest request, @PathVariable long member_id) {
        log.info("Received request to soft delete member by ID: {} | URI: {}", member_id, request.getRequestURI());
        memberFacade.softDeleteById(member_id);
        log.info("Successfully soft deleted member by ID: {} | URI: {}", member_id, request.getRequestURI());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(HttpServletRequest request, HttpServletResponse response, @RequestBody LoginRequest loginRequest) {
        log.info("Received login request for email: {} | URI: {}", loginRequest.email(), request.getRequestURI());
        LoginResponse result = memberFacade.login(loginRequest);
        response.setHeader("Authorization", "Bearer "+result.accessToken());
        response.setHeader("x-refresh-token", result.refreshToken());
        log.info("Successfully logged in user with email: {} | URI: {}", loginRequest.email(), request.getRequestURI());
        return ResponseEntity.ok(CommonResponse.success(result.member()));
    }

    @PostMapping("/renew")
    public ResponseEntity<?> renewToken(HttpServletRequest request, HttpServletResponse response, @RequestBody LogOutRequest logOutRequest) {
        String refreshToken = request.getHeader("x-refresh-token");
        if (refreshToken == null || refreshToken.isEmpty()) {
            log.warn("Refresh token is missing in the request | URI: {}", request.getRequestURI());
            throw new IllegalArgumentException("Refresh token is missing");
        }
        log.info("Received token renew request | URI: {}", request.getRequestURI());
        RenewTokenRequest renewTokenRequest = new RenewTokenRequest(refreshToken, logOutRequest.deviceType().toString());
        LoginResponse result = memberFacade.renewToken(renewTokenRequest);
        response.setHeader("Authorization", "Bearer "+result.accessToken());
        response.setHeader("x-refresh-token", result.refreshToken());
        log.info("Successfully renewed token | URI: {}", request.getRequestURI());
        return ResponseEntity.ok(CommonResponse.success(result.member()));
    }

    @PutMapping("/{member_id}")
    public void update(HttpServletRequest request, @PathVariable Long member_id, @RequestBody UpdateMemberRequest updateMemberRequest) {
        log.info("Received request to update member by ID: {} | URI: {}", member_id, request.getRequestURI());
        memberFacade.update(member_id, updateMemberRequest);
        log.info("Successfully updated member by ID: {} | URI: {}", member_id, request.getRequestURI());
    }

    @GetMapping("/logout")
    public void logout(HttpServletRequest request, @RequestBody LogOutRequest logOutRequest) {
        log.info("Received logout request | URI: {}", request.getRequestURI());
        memberFacade.logout(logOutRequest);
        log.info("Successfully logged out user | URI: {}", request.getRequestURI());
    }
}
