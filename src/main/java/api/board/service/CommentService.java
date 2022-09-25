package api.board.service;

import api.board.exception.RecommendedException;
import api.board.object.board.Board;
import api.board.object.comment.Comment;
import api.board.object.comment.CommentMemberRecommended;
import api.board.object.comment.QComment;
import api.board.object.comment.QCommentMemberRecommended;
import api.board.object.dto.comment.CommentDTO;
import api.board.object.dto.comment.CommentData;

import api.board.object.dto.comment.QCommentDTO;
import api.board.object.dto.comment.QCommentData;
import api.board.object.member.Member;
import api.board.object.member.QMember;
import api.board.repository.BoardRepository;
import api.board.repository.CommentMemberRecommendedRepository;
import api.board.repository.CommentRepository;
import api.board.repository.MemberRepository;
import api.board.utils.Utils;
import com.querydsl.core.types.ExpressionUtils;
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

import java.util.List;
import java.util.Optional;

import static api.board.object.comment.QComment.comment;
import static api.board.object.comment.QCommentMemberRecommended.*;
import static api.board.object.member.QMember.member;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final JPAQueryFactory queryFactory;
    private final CommentMemberRecommendedRepository commentMemberRecommendedRepository ;

    public CommentDTO saveComment(Long boardId, String contents, String email) {
        Optional<Board> findBoard = boardRepository.findOneBoardWithMemberWithCategoryById(boardId);
        Board board = findBoard.orElseThrow(() -> new EntityNotFoundException("Board"));

        CommentData commentData = queryFactory.select(new QCommentData(comment.group.max(), comment.order.max()))
                .from(comment)
                .where(comment.board.id.eq(boardId))
                .fetchOne();

        Comment comment = Comment.builder()
                .board(board)
                .contents(contents)
                .step(0)
                .group(commentData.getGroups() + 1)
                .order(commentData.getOrders() + 1)
                .member(board.getMember())
                .build();

        commentRepository.save(comment);
        return commentMapToCommentDTO(comment);
    }

    public CommentDTO saveReply(Long boardId, Long commentId, String contents, String email) {
        Optional<Board> findBoard = boardRepository.findOneBoardWithMemberWithCategoryById(boardId);
        Board board = findBoard.orElseThrow(() -> new EntityNotFoundException("Board"));

        CommentData commentData = queryFactory.select(new QCommentData(
                queryFactory
                .select(comment.group).from(comment)
                        .where(comment.id.eq(commentId)), comment.order.max().nullif(0)))
                .from(comment)
                .where(comment.board.id.eq(boardId) ,comment.parentId.eq(commentId))
                .fetchOne();

        Comment comment = Comment.builder()
                .board(board)
                .parentId(commentId)
                .contents(contents)
                .step(1)
                .group(commentData.getGroups())
                .order(commentData.getOrders() + 1)
                .member(board.getMember())
                .build();

        commentRepository.save(comment);
        return replyMapToCommentDTO(comment);
    }

    public void recommended(Long commentId, Boolean isRecommended, String email) {


        CommentMemberRecommended commentMemberRecommended = getCommentMemberRecommended(commentId, email);

        if(isRecommended) {
           if(commentMemberRecommended != null) {
               Comment comment = commentMemberRecommended.getComment();
               if(commentMemberRecommended.getRecommended() == 0 && commentMemberRecommended.getNotRecommended() == 0) {
                   commentMemberRecommended.setRecommended(1);
                   comment.setTotalRecommended(comment.getTotalRecommended()+ 1);
               } else if(commentMemberRecommended.getRecommended() == 1 && commentMemberRecommended.getNotRecommended() == 0) {
                   commentMemberRecommended.setRecommended(0);
                   comment.setTotalRecommended(comment.getTotalRecommended()- 1);
               } else {
                   throw new RecommendedException("recommended");
               }
           } else {
               Optional<Member> findMember = memberRepository.findByEmail(email);
               Member member = findMember.orElseThrow(() -> new EntityNotFoundException("Member"));
               Optional<Comment> findComment = commentRepository.findById(commentId);
               Comment comment = findComment.orElseThrow(() -> new EntityNotFoundException("Comment"));

               CommentMemberRecommended save = CommentMemberRecommended.builder()
                       .recommended(1)
                       .notRecommended(0)
                       .comment(comment)
                       .member(member)
                       .build();

               comment.setTotalRecommended(comment.getTotalRecommended() + 1);
               commentMemberRecommendedRepository.save(save);
           }
        } else {
            if(commentMemberRecommended != null) {
                Comment comment = commentMemberRecommended.getComment();
                if(commentMemberRecommended.getRecommended() == 0 && commentMemberRecommended.getNotRecommended() == 0) {
                    commentMemberRecommended.setNotRecommended(1);
                    comment.setTotalNotRecommended(comment.getTotalNotRecommended()+ 1);
                } else if(commentMemberRecommended.getRecommended() == 0 && commentMemberRecommended.getNotRecommended() == 1) {
                    commentMemberRecommended.setNotRecommended(0);
                    comment.setTotalRecommended(comment.getTotalNotRecommended()- 1);
                } else {
                    throw new RecommendedException("recommended");
                }
            } else {
                Optional<Member> findMember = memberRepository.findByEmail(email);
                Member member = findMember.orElseThrow(() -> new EntityNotFoundException("Member"));
                Optional<Comment> findComment = commentRepository.findById(commentId);
                Comment comment = findComment.orElseThrow(() -> new EntityNotFoundException("Comment"));

                CommentMemberRecommended save = CommentMemberRecommended.builder()
                        .recommended(0)
                        .notRecommended(1)
                        .comment(comment)
                        .member(member)
                        .build();

                comment.setTotalRecommended(comment.getTotalNotRecommended() + 1);
                commentMemberRecommendedRepository.save(save);
            }
        }

    }

    public CommentMemberRecommended getCommentMemberRecommended(Long commentId, String email) {
        return queryFactory.select(commentMemberRecommended)
                .from(commentMemberRecommended)
                .innerJoin(commentMemberRecommended.comment, comment)
                .fetchJoin()
                .innerJoin(commentMemberRecommended.member, member)
                .fetchJoin()
                .where(
                        commentMemberRecommended.comment.id.eq(commentId).and(
                                commentMemberRecommended.member.email.eq(email)
                        )
                ).fetchOne();
    }

    public Page<CommentDTO> getComments(Long boardId, Pageable pageable) {

        QComment c1 = new QComment("c1");
        QComment c2 = new QComment("c2");

        List<CommentDTO> list = queryFactory.select(new QCommentDTO(
                c1.id,
                c1.contents,
                c1.step,
                c1.group,
                c1.order,
                c1.member.nickname,
                ExpressionUtils.as(
                        JPAExpressions.select(c1.member.nickname)
                                .from(c2)
                                .where(c2.id.eq(c1.parentId)),"parentName"
                ),
                c1.createdDate,
                c1.totalRecommended,
                c1.totalNotRecommended
        ))
                .from(c1)
                .innerJoin(c1.member, member)
                .where(c1.board.id.eq(boardId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(c1.group.asc())
                .fetch();


        JPAQuery<Long> count = queryFactory.select(comment.count()
        )
                .from(comment)
                .where(comment.board.id.eq(boardId));


        for (CommentDTO commentDTO : list) {
            commentDTO.setHoursAgo(Utils.getHoursAgo(commentDTO.getCreatedDate()));
        }
        return PageableExecutionUtils.getPage(list, pageable, count::fetchOne);

    }

    private CommentDTO commentMapToCommentDTO(Comment comment) {

        CommentDTO commentDTO = CommentDTO.builder()
                .id(comment.getId())
                .name(comment.getMember().getNickname())
                .contents(comment.getContents())
                .parentName(null)
                .group(comment.getGroup())
                .order(comment.getOrder())
                .step(comment.getStep())
                .createdDate(comment.getCreatedDate())
                .build();

        commentDTO.setHoursAgo(Utils.getHoursAgo(commentDTO.getCreatedDate()));
        return commentDTO;
    }

    private CommentDTO replyMapToCommentDTO(Comment comment) {
        QComment c1 = new QComment("c1");
        String parentName = queryFactory.select(c1.member.nickname)
                .from(c1)
                .innerJoin(c1.member, member)
                .where(c1.id.eq(comment.getParentId()))
                .fetchOne();
        CommentDTO commentDTO = CommentDTO.builder()
                .id(comment.getId())
                .name(comment.getMember().getNickname())
                .contents(comment.getContents())
                .parentName(parentName)
                .group(comment.getGroup())
                .order(comment.getOrder())
                .step(comment.getStep())
                .createdDate(comment.getCreatedDate())
                .build();
        commentDTO.setHoursAgo(Utils.getHoursAgo(commentDTO.getCreatedDate()));
        return commentDTO;

    }



}
