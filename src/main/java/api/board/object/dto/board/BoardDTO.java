package api.board.object.dto.board;

import api.board.object.dto.member.MemberDto;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardDTO {

    @NotEmpty
    private String title;
    @NotEmpty
    private String contents;
    private Long category;
    private String categoryName;
    private MemberDto member;
    private int recommended;
    private int notRecommended;

}
