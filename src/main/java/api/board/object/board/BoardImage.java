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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="board_id")
    private Board board;

}
