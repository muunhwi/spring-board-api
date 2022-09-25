package api.board.repository;

import api.board.object.board.Board;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @EntityGraph(attributePaths = {"member","category"})
    Optional<Board> findOneBoardWithMemberWithCategoryById(Long BoardId);

    @EntityGraph(attributePaths = {"category"})
    Optional<Board> findOneBoardWithCategoryById(Long BoardId);

}
