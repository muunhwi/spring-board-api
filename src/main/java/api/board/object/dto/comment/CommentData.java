package api.board.object.dto.comment;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentData {
    int groups;
    int orders;

    @QueryProjection
    public CommentData(int groups, int orders) {
        this.groups = groups;
        this.orders = orders;
    }
}
