package api.board.controller;

import api.board.object.dto.board.BoardGridDTO;
import api.board.object.dto.board.UpdateBoardDTO;
import api.board.object.dto.comment.CommentDTO;
import api.board.object.dto.comment.CommentListDTO;
import api.board.object.dto.comment.RecommendedDto;
import api.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/comment/{id}")
    public ResponseEntity<?> getComments(@PathVariable("id") Long BoardId,
                                         @PageableDefault(size=50) Pageable pageable
                                        ) {
        Page<CommentDTO> comments = commentService.getComments(BoardId, pageable);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/comment/state/{id}")
    public ResponseEntity<?> getCommentsWithUser(
                @AuthenticationPrincipal User user,
                @PathVariable("id") Long BoardId,
                @PageableDefault(size=50) Pageable pageable) {
        Page<CommentDTO> comments = commentService.getCommentsWithUser(BoardId, pageable, user.getUsername());
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/comment/mypage")
    public ResponseEntity<?> mypage(@AuthenticationPrincipal User user, @PageableDefault(size= 20) Pageable pageable) {
        Page<CommentListDTO> commentListDTOS = commentService.myPageCommentList(user.getUsername(), pageable);
        return ResponseEntity.ok(commentListDTOS);
    }

    @PostMapping("/comment/update/{id}")
    public ResponseEntity<?> update(@PathVariable("id")Long commentId, @RequestBody String contents)  {
        commentService.update(commentId, contents);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/comment/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long commentId) {
        commentService.delete(commentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/comment/{id}")
    public ResponseEntity<?> saveComment(@PathVariable("id") Long BoardId,
                                         @RequestBody String contents,
                                         @AuthenticationPrincipal User user) {
        CommentDTO commentDTO = commentService.saveComment(BoardId, contents, user.getUsername());
        return ResponseEntity.ok(commentDTO);
    }

    @PostMapping("/reply/{boardId}/{commentId}")
    public ResponseEntity<?> saveReply(
            @PathVariable("boardId") Long BoardId,
            @PathVariable("commentId") Long commentId,
            @RequestBody String contents,
            @AuthenticationPrincipal User user
    ) {
        CommentDTO commentDTO = commentService.saveReply(BoardId, commentId, contents, user.getUsername());
        return ResponseEntity.ok(commentDTO);
    }

    @PostMapping("/comment/recommended/{commentId}")
    public ResponseEntity<?> recommended(@PathVariable("commentId") Long commentId,
                                         @RequestBody @Valid RecommendedDto recommendedDto,
                                         @AuthenticationPrincipal User user) {
        commentService.recommended(commentId, recommendedDto.getIsRecommended(), user.getUsername());
        return ResponseEntity.ok().build();
    }

}
