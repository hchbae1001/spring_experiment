package bae.springexperiment.config.jwt;

import bae.springexperiment.entity.Member;
import bae.springexperiment.entity.enumerate.Role;


public record AuthInformation (
        long member_id,
        String email,
        Role role
){
    public AuthInformation(Member member){
        this(
                member.getId(),
                member.getEmail(),
                member.getRole()
        );
    }
}
