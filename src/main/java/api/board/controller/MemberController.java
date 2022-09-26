package api.board.controller;

import api.board.jwt.JwtFilter;
import api.board.object.member.Member;
import api.board.object.dto.member.LoginDto;
import api.board.object.dto.member.MemberDto;
import api.board.object.dto.member.TokenDto;
import api.board.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@Valid @RequestBody LoginDto loginDto) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String[] tokens = memberService.getTokens(authentication);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer "+ tokens[0]);

        return new ResponseEntity<>(new TokenDto(tokens[0], tokens[1]), httpHeaders, HttpStatus.OK);

    }

    @PostMapping("/token/expired")
    public ResponseEntity<?> isTokenExpired(@Valid @RequestBody TokenDto tokenDto) {
        Boolean result = memberService.isExpired(tokenDto.getRefreshToken());

        if(result) {
            return new ResponseEntity<>(tokenDto, HttpStatus.OK);
        }

        return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);

    }

    @PostMapping("/token/remake")
    public ResponseEntity<TokenDto> tokenRemake(@Valid @RequestBody TokenDto tokenDto) {
        TokenDto tokens = memberService.tokenRemake(tokenDto);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/signup")
    public ResponseEntity<Member> signup(@Valid @RequestBody MemberDto memberDto) {
        return ResponseEntity.ok(memberService.signup(memberDto));
    }



}
