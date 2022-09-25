package api.board.jwt;

import api.board.object.dto.error.ErrorDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class JwtExceptionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            log.info("401 exception");
            makeExceptionMessage(e,401,response);
        } catch(JwtException e) {
            makeExceptionMessage(e,400,response);
        }
    }

    private void makeExceptionMessage(RuntimeException e, int status, HttpServletResponse response) throws IOException {

        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();

        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        ErrorDto error = ErrorDto.builder()
                .date(date)
                .error("expired")
                .message(e.getMessage())
                .build();

        String result = objectMapper.writeValueAsString(error);
        response.getWriter().write(result);
    }
}
