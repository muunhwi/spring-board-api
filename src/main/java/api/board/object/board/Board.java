package api.board.object.board;

import api.board.object.BaseEntity;
import api.board.object.category.Category;
import api.board.object.comment.Comment;
import api.board.object.member.Member;
import api.board.object.dto.board.BoardDTO;
import lombok.*;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of="id", callSuper = false)
@SequenceGenerator(
        name = "BOARD_SEQ_GENERATOR",
        sequenceName = "BOARD_SEQ",
        initialValue = 1, allocationSize = 50)
public class Board extends BaseEntity {
    @Id
    @GeneratedValue(generator = "BOARD_SEQ_GENERATOR")
    private Long id;
    private String title;

    @Lob
    @Column
    private String contents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="member_id")
    private Member member;

    @OneToOne
    @JoinColumn(name ="category_id")
    private Category category;

    @OneToMany(mappedBy = "board")
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    private int totalRecommended;
    private int totalNotRecommended;

    private Boolean isDeleted;

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public void setMember(Member member) {
        if(member != null) {
            this.member = member;
        }
    }

    public void setCategory(Category category) {
        if(category != null) {
            this.category = category;
        }
    }

    public void setTitle(String title) {

        if(!StringUtils.hasText(title)) {
            return;
        }
        this.title = title;
    }

    public void setContents(String contents) {
        if(!StringUtils.hasText(contents)) {
            return;
        }
        this.contents = contents;
    }

    public void setTotalRecommended(int totalRecommended) {
        this.totalRecommended = totalRecommended;
    }
    public void setTotalNotRecommended(int totalNotRecommended) {
        this.totalNotRecommended = totalNotRecommended;
    }

    public static Board MapToBoard(BoardDTO board, Category category) {
        return Board.builder()
                .category(category)
                .contents(board.getContents())
                .totalNotRecommended(0)
                .totalRecommended(0)
                .title(board.getTitle())
                .isDeleted(false)
                .build();
    }

    public static BoardDTO MapToBoardDTO(Board board) {
        return BoardDTO.builder()
                .category(board.getCategory().getId())
                .categoryName(board.getCategory().getName())
                .member(Member.MapToMemberDto(board.getMember()))
                .contents(board.getContents())
                .title(board.getTitle())
                .recommended(board.getTotalRecommended())
                .notRecommended(board.getTotalNotRecommended())
                .isDeleted(board.getIsDeleted())
                .build();
    }
}
