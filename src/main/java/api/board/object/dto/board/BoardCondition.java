package api.board.object.dto.board;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BoardCondition {

    @NotNull
    private Integer type;

    @NotNull
    private String contents;

}
