package bae.springexperiment.member;

import bae.springexperiment.entity.Member;
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
public class MemberServiceTest {
    @Autowired
    MemberService memberService;

    static Member setupMember;

    @BeforeEach
    @Transactional
    void setup(){
        Member build = Member.builder()
                .email("setup@test.com")
                .phone("01011112222")
                .name("setup_member")
                .nickname("setupMember")
                .build();
        memberService.save(build);
        setupMember = memberService.findByEmail(build.getEmail());
    }

    @AfterEach
    @Transactional
    void clear(){
        memberService.deleteById(setupMember.getId());
    }

    @Test
    @DisplayName("Pass_MemberService_findOne")
    void findOne(){
        Member byEmail = memberService.findByEmail(setupMember.getEmail());
        assertThat(byEmail).isNotNull();
        Member byId = memberService.findById(setupMember.getId());
        assertThat(byId).isNotNull();
    }

    @Test
    @DisplayName("Pass_MemberService_findAll")
    void findAll(){
        List<Member> all = memberService.findAll();
        assertThat(all).isNotNull();
    }

    @Test
    @Transactional
    @DisplayName("Pass_MemberService_save")
    void save(){
        Member build = Member.builder()
                .email("setupCreate@test.com")
                .phone("01011112222")
                .name("setup_member_create")
                .nickname("setupMember_create")
                .build();
        memberService.save(build);
        Member byEmail = memberService.findByEmail("setupCreate@test.com");
        assertThat(byEmail).isNotNull();
    }

    @Test
    @Transactional
    @DisplayName("Pass_MemberService_delete")
    void delete(){
        memberService.deleteById(setupMember.getId());
        assertThatThrownBy(() -> memberService.findById(setupMember.getId()))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @Transactional
    @DisplayName("Pass_MemberService_softDelete")
    void softDelete(){
        assertThat(setupMember.getIsRemoved()).isFalse();
        memberService.softDeleteById(setupMember.getId());
        Member after = memberService.findById(setupMember.getId());
        assertThat(after.getIsRemoved()).isTrue();
    }

    @Test
    @Transactional
    @DisplayName("Pass_MemberService_update")
    void update(){
        setupMember.setName("John Doe");
        memberService.save(setupMember);
        Member after = memberService.findById(setupMember.getId());
        assertThat(after.getName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Fail_MemberService_save_Duplicate")
    void save_fail(){
        Member duplicatePhone = Member.builder()
                .email("duplicate@test.com")
                .phone("01011112222") // setupMember's phone
                .name("duplicate_member")
                .nickname("duplicateMember")
                .build();

        assertThatThrownBy(() -> memberService.save(duplicatePhone))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("phone number already exists");

        Member duplicateNickname = Member.builder()
                .email("duplicate2@test.com")
                .phone("01099998888")
                .name("duplicate_member2")
                .nickname("setupMember") // setupMember's nickname
                .build();

        assertThatThrownBy(() -> memberService.save(duplicateNickname))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("nickname already exists");
    }

    @Test
    @DisplayName("Fail_MemberService_findOne_NoMember")
    void findOne_fail(){
        assertThatThrownBy(() -> memberService.findById(-1))
                .isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> memberService.findByEmail("@@@"))
                .isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> memberService.findByNickName("nickname's length should not be over 30. so that this sentence cannot passed"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Fail_MemberService_update_Duplicate")
    void update_fail(){
        Member newMember = Member.builder()
                .email("new@test.com")
                .phone("01033334444")
                .name("new_member")
                .nickname("newMember")
                .build();
        memberService.save(newMember);

        newMember.setPhone("01011112222"); // setupMember's  phone number
        assertThatThrownBy(() -> memberService.save(newMember))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("phone number already exists");

        newMember.setPhone("01033334444"); // phone number rollback
        newMember.setNickname("setupMember"); // setupMember's nickname
        assertThatThrownBy(() -> memberService.save(newMember))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("nickname already exists");
    }
}
