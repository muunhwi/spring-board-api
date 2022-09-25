package api.board.object.dto.board;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BoardGridDTO {

    private Long boardId;

    private String title;

    private String categoryName;

    private Long commentCount;

    private String nickname;

    private LocalDateTime createDate;

    private String hoursAgo;

    @QueryProjection
    public BoardGridDTO(Long boardId, String title, String categoryName, Long commentCount, String nickname, LocalDateTime createDate) {
        this.boardId = boardId;
        this.title = title;
        this.categoryName = categoryName;
        this.commentCount = commentCount;
        this.nickname = nickname;
        this.createDate = createDate;
    }

    public void setHoursAgo(String hoursAgo) {
        this.hoursAgo = hoursAgo;
    }
}
