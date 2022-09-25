package api.board.repository;

import api.board.object.comment.Comment;
import api.board.object.dto.comment.CommentData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = "select new api.board.object.dto.comment.CommentData(max(c.group), max(c.order)) from Comment c where c.board.id = :boardId")
    CommentData findByBoardId(@Param("boardId") Long boardId);

}
