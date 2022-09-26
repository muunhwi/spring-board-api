package api.board.service;

import api.board.exception.NotActiveException;
import api.board.jwt.TokenProvider;
import api.board.object.member.Authority;
import api.board.object.member.Member;
import api.board.object.member.MemberToken;
import api.board.object.dto.member.MemberDto;
import api.board.object.dto.member.TokenDto;
import api.board.repository.MemberRepository;
import api.board.repository.MemberTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService implements UserDetailsService {
    
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberTokenRepository memberTokenRepository;
    private final TokenProvider tokenProvider;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return memberRepository.findOneWithAuthoritiesByEmail(email)
                .map(member -> createMember(email, member))
                .orElseThrow(() -> new EntityNotFoundException("이메일"));
    }

    public Member signup(MemberDto memberDto) {

        Optional<Member> byEmail = memberRepository.findOneWithAuthoritiesByEmail(memberDto.getEmail());

        if(byEmail.isPresent()) {
          throw new DuplicateKeyException("email");
        }

        Optional<Member> byNickname = memberRepository.findByNickname(memberDto.getNickname());

        if(byNickname.isPresent()) {
            throw new DuplicateKeyException("nickname");
        }

        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        Member member = Member.builder()
                .email(memberDto.getEmail())
                .password(passwordEncoder.encode(memberDto.getPassword()))
                .nickname(memberDto.getNickname())
                .authorities(Collections.singleton(authority))
                .activated(true)
                .build();

        return memberRepository.save(member);
    }

    public String[] getTokens(Authentication authentication) {

        Optional<Member> findMember = memberRepository.findByEmail(authentication.getName());
        Member member = findMember.orElseThrow(EntityNotFoundException::new);

        String refreshToken = tokenProvider.createRefreshToken(authentication);
        String accessToken = tokenProvider.createToken(authentication);

        if(member.getMemberToken() != null) {
            member.getMemberToken().setRefreshToken(refreshToken);
            return new String[]{refreshToken, accessToken};
        }

        MemberToken memberToken = MemberToken.builder()
                .member(member)
                .refreshToken(refreshToken)
                .build();

        memberTokenRepository.save(memberToken);
        return new String[]{refreshToken, accessToken};
    }

    @Transactional
    public TokenDto tokenRemake(TokenDto tokenDto) {
        Optional<MemberToken> findToken = memberTokenRepository.findOneByRefreshToken(tokenDto.getRefreshToken());
        MemberToken memberToken = findToken.orElseThrow(() -> new EntityNotFoundException("token"));

        String refreshToken = memberToken.getRefreshToken();
        String accessToken = null;

        tokenProvider.validateToken(refreshToken);
        accessToken = tokenProvider.createToken(memberToken.getMember());

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public Boolean isExpired(String refreshToken) {

        try {
            tokenProvider.validateToken(refreshToken);
            return true;
        } catch (ExpiredJwtException e) {
            log.info("jwt expired! = {}", e.getMessage());
        } catch (RuntimeException e) {
            log.info("jwt exception!  = {}", e.getMessage());
        }
        return false;

    }

    private User createMember(String email, Member member) {

        if(!member.getActivated()) {
            throw new NotActiveException("Not Active");
        }

        List<SimpleGrantedAuthority> grantedAuthorities = member.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
                .collect(Collectors.toList());

        return new User(email, member.getPassword(), grantedAuthorities);
    }
}
