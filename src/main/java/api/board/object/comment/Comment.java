package api.board.object.comment;

import api.board.object.BaseEntity;
import api.board.object.member.Member;
import api.board.object.board.Board;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of="id", callSuper = false)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String contents;

    private Boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private int step;
    @Column(name ="orders")
    private int order;
    @Column(name ="groups")
    private int group;
    private Long parentId;

    private int totalRecommended;
    private int totalNotRecommended;

    public void setTotalRecommended(int totalRecommended) {
        this.totalRecommended = totalRecommended;
    }
    public void setTotalNotRecommended(int totalNotRecommended) {
        this.totalNotRecommended = totalNotRecommended;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
}
