package api.board.object.member;

import api.board.object.BaseEntity;
import api.board.object.dto.member.MemberDto;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Builder
@EqualsAndHashCode(of="id", callSuper = false)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String email;

    private String password;

    private String nickname;

    private Boolean activated;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "member_authority",
            joinColumns = {@JoinColumn(name = "member_id", referencedColumnName = "member_id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")}
    )
    private Set<Authority> authorities;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "member")
    private MemberToken memberToken;


    public static MemberDto MapToMemberDto(Member member) {
        return MemberDto.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .build();
    }
}
