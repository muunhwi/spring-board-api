package api.board.object.dto.board;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateBoardDTO {

    @NotEmpty
    private Long boardId;

    private String title;

    private String contents;

    private String categoryName;

}
