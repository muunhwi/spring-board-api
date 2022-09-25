package api.board.repository;

import api.board.object.comment.CommentMemberRecommended;
import org.springframework.data.jpa.repository.JpaRepository;



import java.util.Optional;


public interface CommentMemberRecommendedRepository extends JpaRepository<CommentMemberRecommended, Long> {

    Optional<CommentMemberRecommended> findOneByCommentIdAndMemberId(Long commentId, Long memberId);

}
