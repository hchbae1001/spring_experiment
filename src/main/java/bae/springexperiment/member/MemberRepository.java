package bae.springexperiment.member;


import bae.springexperiment.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> {

    Optional<Member> findByName(String name);
    Optional<Member> findByEmail(String email);
    Optional<Member> findByPhone(String phone);
    Optional<Member> findByNickname(String nickname);

    boolean existsByPhone(String phone);
    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);

    boolean existsByPhoneAndIdNot(String phone, Long id);
    boolean existsByNicknameAndIdNot(String nickname, Long id);
    boolean existsByEmailAndIdNot(String email, Long id);
}
