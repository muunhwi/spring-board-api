package api.board.object.dto.comment;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Data
public class CommentDTO {

    private Long id;
    private String contents;
    private int step;
    private int group;
    private int order;
    private String name;
    private String parentName;
    private LocalDateTime createdDate;
    private String hoursAgo;
    private int Recommended;
    private int NotRecommended;

    @QueryProjection
    @Builder
    public CommentDTO(Long id, String contents,
                      int step,
                      int group,
                      int order,
                      String name,
                      String parentName,
                      LocalDateTime createdDate,
                      int Recommended,
                      int NotRecommended) {
        this.id = id;
        this.contents = contents;
        this.step = step;
        this.group = group;
        this.order = order;
        this.name = name;
        this.parentName = parentName;
        this.createdDate = createdDate;
        this.Recommended = Recommended;
        this.NotRecommended =NotRecommended;
    }

    public void setHoursAgo(String hoursAgo) {
        if(StringUtils.hasText(hoursAgo)) {
            this.hoursAgo = hoursAgo;
        }
    }
}
