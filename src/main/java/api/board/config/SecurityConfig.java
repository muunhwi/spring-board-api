package api.board.config;

import api.board.jwt.JwtAccessDeniedHandler;
import api.board.jwt.JwtAuthenticationEntryPoint;
import api.board.jwt.JwtSecurityConfig;
import api.board.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.http.HttpMethod.*;


@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAccessDeniedHandler accessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain webConfig(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .authorizeRequests()
                .mvcMatchers(GET,"/").permitAll()
                .mvcMatchers(GET, "/comment/**").permitAll()
                .mvcMatchers(GET, "/reply/**").permitAll()
                .mvcMatchers(GET, "/board/**").permitAll()
                .mvcMatchers(POST, "/login").permitAll()
                .mvcMatchers(POST, "/signup").permitAll()
                .mvcMatchers(POST, "/token/expired").permitAll()
                .mvcMatchers(POST, "/token/remake").permitAll()
                .mvcMatchers(POST, "/board/list/**").permitAll()
                .mvcMatchers(POST, "/board/image/**").permitAll()
                .anyRequest().authenticated()

                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(authenticationEntryPoint)

                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .apply(new JwtSecurityConfig(tokenProvider))

                .and()
                .headers()
                .frameOptions()
                .sameOrigin();


        return http.build();
    }

}
