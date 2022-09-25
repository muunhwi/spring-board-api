package api.board.repository;

import api.board.object.member.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    @EntityGraph(attributePaths = {"authorities","memberToken"})
    Optional<Member> findOneWithAuthoritiesByEmail(String email);

    @EntityGraph(attributePaths = {"memberToken"})
    Optional<Member> findByEmail(String email);

    Optional<Member> findByNickname(String Nickname);
}
