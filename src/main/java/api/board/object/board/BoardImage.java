package api.board.object.board;

import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@EqualsAndHashCode(of="id")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardImage {

    @Id
    @GeneratedValue
    private Long id;
    private String originalName;
    private String serverSavedName;

}
