package api.board.repository;

import api.board.object.member.MemberToken;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface MemberTokenRepository extends JpaRepository<MemberToken, Long> {
    @EntityGraph(attributePaths = {"member"})
    Optional<MemberToken> findOneByRefreshToken(String refreshToken);

    Optional<MemberToken> findOneByMemberId(Long id);
}
