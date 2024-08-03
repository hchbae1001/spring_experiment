package bae.springexperiment.member;

import bae.springexperiment.entity.Member;
import bae.springexperiment.member.dto.request.*;
import bae.springexperiment.member.dto.response.LoginResponse;

public interface MemberFacade {
    void save(SaveMemberRequest request);
    void update(Long member_id, UpdateMemberRequest request);
    LoginResponse login(LoginRequest request);
    LoginResponse renewToken(RenewTokenRequest request);
    void logout(LogOutRequest request);
    void softDeleteById(Long member_id);
    Member findById(Long member_id);

    void deleteById(Long member_id);
}
