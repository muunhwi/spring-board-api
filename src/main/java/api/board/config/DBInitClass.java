package api.board.config;

import api.board.object.member.Authority;
import api.board.object.category.Category;
import api.board.object.dto.member.MemberDto;
import api.board.repository.AuthorityRepository;
import api.board.repository.CategoryRepository;
import api.board.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;


@RequiredArgsConstructor
@Component
@Transactional
@Slf4j
public class DBInitClass {

    private final CategoryRepository categoryRepository;
    private final MemberService memberService;
    private final AuthorityRepository authorityRepository;

//    @EventListener(ApplicationReadyEvent.class)
    public void initialization() {

        String[] categories = {"또드립", "유저 게시글", "붐업 베스트",
                "읽을 거리 판",
                "컴퓨터 / IT 판"};

        Arrays.stream(categories).map(str-> Category
                .builder()
                .name(str)
                .build()
        ).forEach(categoryRepository::save);

        Authority role_user = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        authorityRepository.save(role_user);

        Authority role_admin = Authority.builder()
                .authorityName("ROLE_ADMIN")
                .build();

        authorityRepository.save(role_admin);


        MemberDto memberDto = MemberDto.builder()
                .email("test1@123.com")
                .nickname("a")
                .password("zxcv1234")
                .build();

        memberService.signup(memberDto);


    }


}
