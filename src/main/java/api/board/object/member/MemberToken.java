package api.board.object.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberToken {

    @Id
    @GeneratedValue
    @Column(name = "token_id")
    private Long id;
    private String refreshToken;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void setRefreshToken(String refreshToken) {
        if(refreshToken == null) return;
        this.refreshToken = refreshToken;
    }
}
