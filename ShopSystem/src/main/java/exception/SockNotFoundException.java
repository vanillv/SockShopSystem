package exception;

public class SockNotFoundException extends RuntimeException {
    public SockNotFoundException(String message){
        super(message);
    }
}
