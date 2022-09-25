package api.board.service;

import api.board.exception.RecommendedException;
import api.board.object.board.Board;
import api.board.object.board.BoardMemberRecommended;
import api.board.object.category.Category;
import api.board.object.dto.board.BoardDTO;
import api.board.object.dto.board.BoardGridDTO;
import api.board.object.dto.comment.RecommendedDto;
import api.board.object.dto.member.MemberDto;
import api.board.object.member.Authority;
import api.board.object.member.Member;
import api.board.repository.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BoardServiceTest {

    @Autowired
    private BoardMemberRecommendedRepository boardMemberRecommendedRepository;
    @Autowired
    private BoardService boardService;
    @Autowired
    private BoardRepository boardRepository;

    @BeforeEach
    public void init() {
        BoardDTO boardDTO = BoardDTO.builder()
                .category(2L)
                .contents("test")
                .title("title")
                .build();

        Long save = boardService.save(boardDTO, "test1@123.com");
    }


    @Test
    void recommended() {
        RecommendedDto recommendedDto = new RecommendedDto(true);
        boardService.recommended(1L, recommendedDto.getIsRecommended(), "test1@123.com");

        BoardMemberRecommended boardMemberRecommended = boardService.getBoardMemberRecommended(1L, "test1@123.com");
        int recommended = boardMemberRecommended.getRecommended();
        assertThat(recommended).isEqualTo(1);

        RecommendedDto recommendedDto2 = new RecommendedDto(false);
        assertThatThrownBy(() -> boardService.recommended(1L, recommendedDto2.getIsRecommended(), "test1@123.com"))
                .isInstanceOf(RecommendedException.class);
    }

    @Test
    void notRecommended() {
        RecommendedDto recommendedDto = new RecommendedDto(false);
        boardService.recommended(1L, recommendedDto.getIsRecommended(), "test1@123.com");

        BoardMemberRecommended boardMemberRecommended = boardService.getBoardMemberRecommended(1L, "test1@123.com");
        int notRecommended = boardMemberRecommended.getNotRecommended();
        assertThat(notRecommended).isEqualTo(1);

        RecommendedDto recommendedDto2 = new RecommendedDto(true);
        assertThatThrownBy(() -> boardService.recommended(1L, recommendedDto2.getIsRecommended(), "test1@123.com"))
                .isInstanceOf(RecommendedException.class);
    }

    @Test
    @Transactional
    void ChangeCategoryUp() {
        Optional<Board> findBoard = boardRepository.findById(1L);
        Board board = findBoard.orElseThrow(EntityNotFoundException::new);
        board.setTotalRecommended(24);

        RecommendedDto recommendedDto = new RecommendedDto(true);
        boardService.recommended(1L, recommendedDto.getIsRecommended(), "test1@123.com");
        boardService.getBoardMemberRecommended(1L, "test1@123.com");
        String name = board.getCategory().getName();
        assertThat(name).isEqualTo("또드립");
    }

    @Test
    @Transactional
    void ChangeCategoryDown() {
        Optional<Board> findBoard = boardRepository.findById(1L);
        Board board = findBoard.orElseThrow(EntityNotFoundException::new);
        board.setTotalNotRecommended(24);

        RecommendedDto recommendedDto = new RecommendedDto(false);
        boardService.recommended(1L, recommendedDto.getIsRecommended(), "test1@123.com");
        boardService.getBoardMemberRecommended(1L, "test1@123.com");
        String name = board.getCategory().getName();
        assertThat(name).isEqualTo("붐업 베스트");
    }
}