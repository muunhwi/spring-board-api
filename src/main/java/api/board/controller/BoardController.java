package api.board.controller;

import api.board.object.dto.Image.ImageFile;
import api.board.object.dto.board.BoardCondition;
import api.board.object.dto.board.BoardDTO;
import api.board.object.dto.board.BoardGridDTO;
import api.board.object.dto.comment.RecommendedDto;
import api.board.service.BoardService;
import api.board.service.UploaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class BoardController {

    private final UploaderService uploader;
    private final BoardService boardService;

    @GetMapping("/board/{id}")
    public ResponseEntity<?> boardInfo(@PathVariable("id") Long id) {
        BoardDTO findBoardDTO = boardService.findById(id);
        return ResponseEntity.ok(findBoardDTO);
    }

    @GetMapping("/board/write")
    public ResponseEntity<?> boardWrite() {
        return ResponseEntity.status(401).build();
    }

    @GetMapping(value = "/board/image/{id}")
    public ResponseEntity<?> image(@PathVariable Long id) {
        byte[] body = uploader.getImage(id);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @GetMapping("/board/grid/{categoryId}")
    public ResponseEntity<?> getGrid(@PathVariable("categoryId") Long id) {
        List<BoardGridDTO> boardGridDTOS = boardService.gridBoardList(id);
        return ResponseEntity.ok(boardGridDTOS);
    }

    @PostMapping("/board/image")
    public ResponseEntity<ImageFile> imageUpload(MultipartFile file) {
        ImageFile imageFile = this.uploader.imageUploader(file);
        return ResponseEntity.ok(imageFile);
    }

    @PostMapping("/board/recommended/{boardId}")
    public ResponseEntity<?> recommended(@PathVariable("boardId") Long boardId,
                                         @RequestBody @Valid RecommendedDto recommendedDto,
                                         @AuthenticationPrincipal User user) {
        boardService.recommended(boardId, recommendedDto.getIsRecommended(), user.getUsername());
        return ResponseEntity.ok().build();

    }

    @PostMapping("/board/save")
    public ResponseEntity<?> save(@RequestBody BoardDTO board, @AuthenticationPrincipal User user)  {
        Long id = boardService.save(board, user.getUsername());
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @PostMapping("/board/list/{categoryId}")
    public ResponseEntity<?> getBoardPage(@PathVariable("categoryId") Long id,
                                          @RequestBody @Valid BoardCondition boardCondition,
                                          @PageableDefault(size= 20) Pageable pageable) {
        Page<BoardGridDTO> boardGridDTOS = boardService.searchConditionBoardList(id, boardCondition, pageable);
        return ResponseEntity.ok(boardGridDTOS);
    }


}
