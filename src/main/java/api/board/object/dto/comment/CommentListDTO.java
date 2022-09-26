package api.board.object.dto.comment;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CommentListDTO {
    private Long id;
    private Long boardId;
    private String contents;
    private String hoursAgo;
    private LocalDateTime createdDate;
    private Boolean isDeleted;
    private String nickname;

    @QueryProjection
    public CommentListDTO(Long id, Long boardId, String contents, LocalDateTime createdDate, Boolean isDeleted, String nickname) {
        this.id = id;
        this.boardId = boardId;
        this.contents = contents;
        this.createdDate = createdDate;
        this.isDeleted = isDeleted;
        this.nickname = nickname;
    }

    public void setHoursAgo(String hoursAgo) {
        this.hoursAgo = hoursAgo;
    }
}
