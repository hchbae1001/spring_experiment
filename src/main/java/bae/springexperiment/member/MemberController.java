package bae.springexperiment.member;

import bae.springexperiment.CommonResponse;
import bae.springexperiment.entity.Member;
import bae.springexperiment.member.dto.request.SaveMemberRequest;
import bae.springexperiment.member.dto.response.MemberCommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
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

}
