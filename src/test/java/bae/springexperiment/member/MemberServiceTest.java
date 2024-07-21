package bae.springexperiment.member;

import bae.springexperiment.entity.Member;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class MemberServiceTest {
    @Autowired
    MemberService memberService;

    static Member setupMember;

    @BeforeEach
    @Transactional
    void setup(){

    }

    @AfterEach
    @Transactional
    void clear(){

    }

    @Test
    @DisplayName("Pass_MemberService_findOne")
    void findOne(){

    }

    @Test
    @DisplayName("Pass_MemberService_findAll")
    void findAll(){

    }

    @Test
    @Transactional
    @DisplayName("Pass_MemberService_save")
    void save(){

    }

    @Test
    @Transactional
    @DisplayName("Pass_MemberService_delete")
    void delete(){

    }

    @Test
    @Transactional
    @DisplayName("Pass_MemberService_softDelete")
    void softDelete(){

    }

    @Test
    @Transactional
    @DisplayName("Pass_MemberService_update")
    void update(){

    }

    @Test
    @DisplayName("Fail_MemberService_save_Duplicate")
    void save_fail(){
        // duplication (phone, nickname) except .self

    }

    @Test
    @DisplayName("Fail_MemberService_findOne_NoMember")
    void findOne_fail(){

    }

    @Test
    @DisplayName("Fail_MemberService_update_Duplicate")
    void update_fail(){
        // duplication (phone, nickname) except .self
    }
}
