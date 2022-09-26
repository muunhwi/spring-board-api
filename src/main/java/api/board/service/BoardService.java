package api.board.service;

import api.board.exception.RecommendedException;
import api.board.object.board.Board;
import api.board.object.board.BoardMemberRecommended;
import api.board.object.board.QBoard;
import api.board.object.board.QBoardMemberRecommended;
import api.board.object.category.Category;
import api.board.object.dto.board.*;
import api.board.object.member.Member;
import api.board.repository.BoardMemberRecommendedRepository;
import api.board.repository.BoardRepository;
import api.board.repository.CategoryRepository;
import api.board.repository.MemberRepository;
import api.board.utils.Utils;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static api.board.object.board.QBoard.board;
import static api.board.object.category.QCategory.category;
import static api.board.object.comment.QComment.comment;
import static api.board.object.member.QMember.member;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final BoardMemberRecommendedRepository boardMemberRecommendedRepository;
    private final JPAQueryFactory queryFactory;

    public BoardDTO findById(Long id) {
        Optional<Board> findBoard = boardRepository.findById(id);
        Board board = findBoard.orElseThrow(() -> (new EntityNotFoundException("Board")));
        return Board.MapToBoardDTO(board);
    }

    public Long save(BoardDTO board, String email) {
        Optional<Member> findMember = memberRepository.findByEmail(email);
        Member member = findMember.orElseThrow(() -> new EntityNotFoundException("MEMBER"));

        Category category = categoryRepository.findById(board.getCategory()).orElseThrow(() -> new EntityNotFoundException("Category"));

        Board saveBoard = Board.MapToBoard(board, category);
        saveBoard.setMember(member);

        Board save = boardRepository.save(saveBoard);
        return save.getId();
    }

    public void update(UpdateBoardDTO boardDTO) {
        Optional<Board> findBoard = boardRepository.findById(boardDTO.getBoardId());
        Board board = findBoard.orElseThrow(() -> new EntityNotFoundException("Board"));

        board.setContents(boardDTO.getContents());
        board.setTitle(boardDTO.getTitle());

        if(boardDTO.getCategoryName() != null) {
            Optional<Category> findCategory = categoryRepository.findByName(boardDTO.getCategoryName());
            Category category = findCategory.orElseThrow(() -> new EntityNotFoundException("Category"));
            board.setCategory(category);
        }
    }

    public void delete(Long id) {
        Optional<Board> findBoard = boardRepository.findById(id);
        Board board = findBoard.orElseThrow(() -> new EntityNotFoundException("Board"));

        board.setContents("삭제된 게시글");
        board.setTitle("삭제된 게시글");
        board.setDeleted(true);
    }


    public List<BoardGridDTO> gridBoardList(Long id) {

        List<BoardGridDTO> list = queryFactory.select(new QBoardGridDTO(
                board.id,
                board.title,
                category.name,
                ExpressionUtils.as(
                        JPAExpressions.select(comment.count())
                                .from(comment)
                                .where(comment.board.id.eq(board.id)),"commentCount"
                ),
                member.nickname,
                board.createdDate,
                board.isDeleted
        ))
                .from(board)
                .innerJoin(board.category, category)
                .innerJoin(board.member, member)
                .where(category.id.eq(id))
                .orderBy(board.updatedDate.desc())
                .limit(getLimit(id))
                .fetch();

        for (BoardGridDTO boardGridDTO : list) {
            boardGridDTO.setHoursAgo(Utils.getHoursAgo(boardGridDTO.getCreateDate()));
        }

        return list;

    }

    private Long getLimit(Long id) {

        if (id == 1 || id == 2) {
            return 15L;
        }
        return 5L;

    }

    public Page<BoardGridDTO> searchConditionBoardList(Long id, BoardCondition boardCondition, Pageable pageable) {

        List<BoardGridDTO> list = queryFactory.select(new QBoardGridDTO(
                board.id,
                board.title,
                category.name,
                ExpressionUtils.as(
                        JPAExpressions.select(comment.count())
                                .from(comment)
                                .where(comment.board.id.eq(board.id)),"commentCount"
                ),
                member.nickname,
                board.createdDate,
                board.isDeleted
        ))
                .from(board)
                .innerJoin(board.category, category)
                .innerJoin(board.member, member)
                .where(category.id.eq(id).and(getCondition(boardCondition)))
                .orderBy(board.updatedDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        for (BoardGridDTO boardGridDTO : list) {
            boardGridDTO.setHoursAgo(Utils.getHoursAgo(boardGridDTO.getCreateDate()));
        }

        JPAQuery<Long> countQuery = queryFactory.select(board.count())
                .from(board)
                .innerJoin(board.category, category)
                .innerJoin(board.member, member)
                .where(category.id.eq(id).and(getCondition(boardCondition)));

        return PageableExecutionUtils.getPage(list, pageable, countQuery::fetchOne);

    }

    public Page<BoardGridDTO> myPageBoardList(String email, Pageable pageable) {

        List<BoardGridDTO> list = queryFactory.select(new QBoardGridDTO(
                board.id,
                board.title,
                category.name,
                ExpressionUtils.as(
                        JPAExpressions.select(comment.count())
                                .from(comment)
                                .where(comment.board.id.eq(board.id)),"commentCount"
                ),
                member.nickname,
                board.createdDate,
                board.isDeleted
        ))
                .from(board)
                .innerJoin(board.category, category)
                .innerJoin(board.member, member)
                .where(member.email.eq(email))
                .orderBy(board.updatedDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        for (BoardGridDTO boardGridDTO : list) {
            boardGridDTO.setHoursAgo(Utils.getHoursAgo(boardGridDTO.getCreateDate()));
        }

        JPAQuery<Long> countQuery = queryFactory.select(board.count())
                .from(board)
                .innerJoin(board.category, category)
                .innerJoin(board.member, member)
                .where(member.email.eq(email));

        return PageableExecutionUtils.getPage(list, pageable, countQuery::fetchOne);

    }

    private Predicate getCondition(BoardCondition boardCondition) {

        if (boardCondition.getType() == null || boardCondition.getContents() == null) {
            return null;
        }

        String contents = boardCondition.getContents();
        switch (boardCondition.getType()) {
            case 1: return board.title.contains(contents);
            case 2: return member.nickname.contains(contents);
            case 3: return board.contents.contains(contents);
            case 4: return board.title.contains(contents).and(board.contents.contains(contents));
            default: return null;
        }

    }
    public void recommended(Long boardId, Boolean isRecommended, String email) {

        BoardMemberRecommended boardMemberRecommended = getBoardMemberRecommended(boardId, email);

        if(isRecommended) {
            if(boardMemberRecommended != null) {
                Board board = boardMemberRecommended.getBoard();
                if(boardMemberRecommended.getRecommended() == 0 && boardMemberRecommended.getNotRecommended() == 0) {
                    boardMemberRecommended.setRecommended(1);
                    board.setTotalRecommended(board.getTotalRecommended() + 1);
                } else if(boardMemberRecommended.getRecommended() == 1 && boardMemberRecommended.getNotRecommended() == 0){
                    boardMemberRecommended.setRecommended(0);
                    board.setTotalRecommended(board.getTotalRecommended() -1);
                } else {
                    throw new RecommendedException("recommended");
                }
                boardCategoryUpgrade(board);
            } else {
                Optional<Board> findBoard = boardRepository.findOneBoardWithCategoryById(boardId);
                Board board = findBoard.orElseThrow(() -> new EntityNotFoundException("Board"));
                Optional<Member> findMember = memberRepository.findByEmail(email);
                Member member = findMember.orElseThrow(() -> new EntityNotFoundException("Member"));

                board.setTotalRecommended(board.getTotalRecommended() + 1);
                BoardMemberRecommended save = BoardMemberRecommended.builder()
                        .recommended(1)
                        .notRecommended(0)
                        .board(board)
                        .member(member)
                        .build();
                boardMemberRecommendedRepository.save(save);
                boardCategoryUpgrade(board);
            }
        } else {
            if(boardMemberRecommended != null ) {
                Board board = boardMemberRecommended.getBoard();
                if(boardMemberRecommended.getNotRecommended() == 0 && boardMemberRecommended.getRecommended() == 0 ) {
                    boardMemberRecommended.setNotRecommended(1);
                    board.setTotalNotRecommended(board.getTotalNotRecommended() + 1);
                } else if(boardMemberRecommended.getNotRecommended() == 1 && boardMemberRecommended.getRecommended() == 0 ){
                    boardMemberRecommended.setNotRecommended(0);
                    board.setTotalNotRecommended(board.getTotalNotRecommended() - 1);
                } else {
                    throw new RecommendedException("NotRecommended");
                }
                boardCategoryDowngrade(board);
            } else {
                Optional<Board> findBoard = boardRepository.findOneBoardWithCategoryById(boardId);
                Board board = findBoard.orElseThrow(() -> new EntityNotFoundException("Board"));
                Optional<Member> findMember = memberRepository.findByEmail(email);
                Member member = findMember.orElseThrow(() -> new EntityNotFoundException("Member"));

                board.setTotalNotRecommended(board.getTotalNotRecommended() + 1);
                BoardMemberRecommended save = BoardMemberRecommended.builder()
                        .recommended(0)
                        .notRecommended(1)
                        .board(board)
                        .member(member)
                        .build();
                boardMemberRecommendedRepository.save(save);
                boardCategoryDowngrade(board);
            }
        }
    }

    public BoardMemberRecommended getBoardMemberRecommended(Long boardId, String email) {
        return queryFactory.select(QBoardMemberRecommended.boardMemberRecommended)
                .from(QBoardMemberRecommended.boardMemberRecommended)
                .innerJoin(QBoardMemberRecommended.boardMemberRecommended.board, board)
                .fetchJoin()
                .innerJoin(QBoardMemberRecommended.boardMemberRecommended.member, member)
                .fetchJoin()
                .where(
                        QBoardMemberRecommended.boardMemberRecommended.board.id.eq(boardId)
                                .and(QBoardMemberRecommended.boardMemberRecommended.member.email.eq(email))
                ).fetchOne();
    }


    private void boardCategoryUpgrade(Board board) {

        Category category = board.getCategory();

        if(board.getTotalRecommended() >= 25 && category.getName().equals("유저 게시글")) {
            Optional<Category> findCategory = categoryRepository.findByName("또드립");
            Category upgradeCategory =
                    findCategory.orElseThrow(() -> new EntityNotFoundException("Category"));
            board.setCategory(upgradeCategory);
        }

    }

    private void boardCategoryDowngrade(Board board) {

        Category category = board.getCategory();

        if(board.getTotalNotRecommended() >= 25 && category.getName().equals("유저 게시글")) {
            Optional<Category> findCategory = categoryRepository.findByName("붐업 베스트");
            Category upgradeCategory =
                    findCategory.orElseThrow(() -> new EntityNotFoundException("Category"));
            board.setCategory(upgradeCategory);
        }

    }


}
