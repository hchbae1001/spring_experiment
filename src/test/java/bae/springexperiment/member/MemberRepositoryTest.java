package bae.springexperiment.member;

import bae.springexperiment.entity.Member;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    static Member setupMember;

    @BeforeEach
    @Transactional
    void setup() {
        Member build = Member.builder()
                .email("setup@test.com")
                .phone("01011112222")
                .name("setup_member")
                .nickname("setupMember")
                .build();
        memberRepository.save(build);
        setupMember = memberRepository.findByName("setup_member").orElseThrow();
    }

    @AfterEach
    @Transactional
    void clear() {
        memberRepository.deleteById(setupMember.getId());
    }

    @Test
    @Transactional
    @DisplayName("Pass_Member_Create")
    void save() {
        Member build = Member.builder()
                .email("testCreate@test.com")
                .phone("01000000000")
                .name("test")
                .nickname("tester")
                .build();
        memberRepository.save(build);

        Member foundMember = memberRepository.findByEmail("testCreate@test.com").orElseThrow();
        assertThat(foundMember).isNotNull();
        assertThat(foundMember.getEmail()).isEqualTo("testCreate@test.com");
        assertThat(foundMember.getPhone()).isEqualTo("01000000000");
        assertThat(foundMember.getName()).isEqualTo("test");
        assertThat(foundMember.getNickname()).isEqualTo("tester");
    }

    @Test
    @DisplayName("Pass_Member_FindOne")
    void findOne() {
        Member foundByPhone = memberRepository.findByPhone(setupMember.getPhone()).orElseThrow(
                () -> new RuntimeException("No User")
        );
        assertThat(foundByPhone).isNotNull();
        assertThat(foundByPhone.getPhone()).isEqualTo(setupMember.getPhone());

        Member foundByName = memberRepository.findByName(setupMember.getName()).orElseThrow(
                () -> new RuntimeException("No User")
        );
        assertThat(foundByName).isNotNull();
        assertThat(foundByName.getName()).isEqualTo(setupMember.getName());

        Member foundByNickname = memberRepository.findByNickname(setupMember.getNickname()).orElse(null);
        assertThat(foundByNickname).isNotNull();
        assertThat(foundByNickname.getNickname()).isEqualTo(setupMember.getNickname());

        Member foundById = memberRepository.findById(setupMember.getId()).orElseThrow();
        assertThat(foundById).isNotNull();
        assertThat(foundById.getId()).isEqualTo(setupMember.getId());
    }

    @Test
    @DisplayName("Pass_Member_FindAll")
    void findAll() {
        Iterable<Member> members = memberRepository.findAll();
        assertThat(members).isNotEmpty();
    }

    @Test
    @Transactional
    @DisplayName("Pass_Member_Update")
    void update() {
        Member memberToUpdate = memberRepository.findById(setupMember.getId()).orElseThrow();
        memberToUpdate.setName("UpdatedName");
        memberRepository.save(memberToUpdate);

        Member updatedMember = memberRepository.findById(setupMember.getId()).orElseThrow();
        assertThat(updatedMember.getName()).isEqualTo("UpdatedName");
    }

    @Test
    @Transactional
    @DisplayName("Pass_Member_Delete")
    void delete() {
        Member memberToDelete = memberRepository.findById(setupMember.getId()).orElseThrow();
        memberRepository.delete(memberToDelete);

        Optional<Member> deletedMember = memberRepository.findById(setupMember.getId());
        assertThat(deletedMember).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("Pass_Member_SoftDelete")
    void softDelete() {
        Member memberToSoftDelete = memberRepository.findById(setupMember.getId()).orElseThrow();
        memberToSoftDelete.setIsRemoved(true);
        memberRepository.save(memberToSoftDelete);

        Member softDeletedMember = memberRepository.findById(setupMember.getId()).orElseThrow();
        assertThat(softDeletedMember.getIsRemoved()).isTrue();
    }

    @Test
    @DisplayName("Pass_Member_Exists_PhoneAndNickName")
    void save_check(){
        assertThat(memberRepository.existsByPhone(setupMember.getPhone())).isTrue();
        assertThat(memberRepository.existsByNickname(setupMember.getNickname())).isTrue();
    }

    @Test
    @DisplayName("Pass_Member_Not_Exists_PhoneAndNickName")
    void update_check() {
        assertThat(memberRepository.existsByPhoneAndIdNot("01011112222", setupMember.getId())).isFalse();
        assertThat(memberRepository.existsByNicknameAndIdNot("setupMember", setupMember.getId())).isFalse();
    }
}
