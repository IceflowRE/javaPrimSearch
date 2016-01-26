package Exceptions;

public class WrongPrimException extends RuntimeException {
	public WrongPrimException() {
        super("WrongPrimException");
    }
	public WrongPrimException(String s) {
        super(s);
    }
}