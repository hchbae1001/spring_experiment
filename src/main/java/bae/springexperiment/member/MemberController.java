package bae.springexperiment.member;

import bae.springexperiment.CommonResponse;
import bae.springexperiment.entity.Member;
import bae.springexperiment.member.dto.request.LogOutRequest;
import bae.springexperiment.member.dto.request.LoginRequest;
import bae.springexperiment.member.dto.request.SaveMemberRequest;
import bae.springexperiment.member.dto.request.UpdateMemberRequest;
import bae.springexperiment.member.dto.response.MemberCommonResponse;
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
    private final MemberFacade memberFacade;

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @PostMapping("/")
    public void save(@RequestBody SaveMemberRequest request){
        memberService.save(MemberMapper.saveMemberRequestToEntity(request));
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
        memberService.deleteById(member_id);
    }

    @DeleteMapping("/soft/{member_id}")
    public void softDelete(@PathVariable long member_id){
        memberService.softDeleteById(member_id);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request){
        return ResponseEntity.ok(CommonResponse.success(memberFacade.login(request)));
    }

    @PostMapping("/renew")
    public ResponseEntity<?> renewToken(HttpServletRequest request){
        String refreshToken = request.getHeader("x-refresh-token");
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new IllegalArgumentException("Refresh token is missing");
        }
        return ResponseEntity.ok(CommonResponse.success(memberFacade.renewToken(refreshToken)));
    }

    @PutMapping("/{member_id}")
    public void update(@PathVariable Long member_id, @RequestBody UpdateMemberRequest request){
        memberService.update(member_id, request);
    }

    @GetMapping("/logout")
    public void logout(@RequestBody LogOutRequest request){
        memberFacade.logout(request);
    }
}
