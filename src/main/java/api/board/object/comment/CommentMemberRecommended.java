package api.board.object.comment;

import api.board.object.member.Member;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of="id")
public class CommentMemberRecommended {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    private int recommended;
    private int notRecommended;

    public void setComments(Comment comment) {
        if(comment != null) {
            this.comment = comment;
        }
    }

    public void setMember(Member member) {
        if(member != null) {
            this.member = member;
        }
    }

    public void setRecommended(int recommended) {
        if(recommended == 0 || recommended == 1) {
            this.recommended = recommended;
        }
    }

    public void setNotRecommended(int notRecommended) {
        if(notRecommended == 0 ||notRecommended == 1) {
            this.notRecommended = notRecommended;
        }
    }

}
