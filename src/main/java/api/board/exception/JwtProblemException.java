package api.board.exception;

public class JwtProblemException extends RuntimeException {

    public JwtProblemException(String message) {
        super(message);
    }
}

