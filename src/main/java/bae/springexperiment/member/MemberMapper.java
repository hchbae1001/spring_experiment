package bae.springexperiment.member;

import bae.springexperiment.entity.Member;
import bae.springexperiment.entity.enumerate.Role;
import bae.springexperiment.member.dto.request.SaveMemberRequest;
import bae.springexperiment.member.dto.request.UpdateMemberRequest;
import bae.springexperiment.member.dto.response.MemberCommonResponse;
import bae.springexperiment.util.BcryptUtil;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MemberMapper {

    public static void updateMemberFromRequest(UpdateMemberRequest request, Member existingMember) {
        Optional.ofNullable(request.phone()).ifPresent(existingMember::setPhone);
        Optional.ofNullable(request.email()).ifPresent(existingMember::setEmail);
        Optional.ofNullable(request.nickname()).ifPresent(existingMember::setNickname);
        Optional.ofNullable(request.password()).ifPresent(password -> existingMember.setPassword(BcryptUtil.encodedPassword(password)));
    }

    public static Member saveMemberRequestToEntity(SaveMemberRequest request){
        return Member.builder()
                .phone(request.phone())
                .name(request.name())
                .nickname(request.nickname())
                .email(request.email())
                .role(Role.normal)
                .password(BcryptUtil.encodedPassword(request.password()))
                .isRemoved(false)
                .build();
    }
}
