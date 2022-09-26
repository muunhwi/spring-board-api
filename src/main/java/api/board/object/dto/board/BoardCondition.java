package api.board.object.dto.board;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BoardCondition {

    private Integer type;

    private String contents;

}
