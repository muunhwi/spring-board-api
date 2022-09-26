package api.board.repository;

import api.board.object.board.Board;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @EntityGraph(attributePaths = {"member","category"})
    Optional<Board> findOneBoardWithMemberWithCategoryById(Long BoardId);

    @EntityGraph(attributePaths = {"category"})
    Optional<Board> findOneBoardWithCategoryById(Long BoardId);

    @Query("SELECT b FROM Board b " +
            "join fetch b.member m " +
            "join fetch b.category c " +
            "where b.title =: title and m.email =: email")
    Optional<Board> findOneBoardWithMemberWithCategoryByTitleAndMemberEmail(@Param("title") String title, @Param("email") String email);
}
