package exception;

public class InsufficientSockStockException extends RuntimeException{
    public InsufficientSockStockException(String message) {
        super(message);
    }
}
