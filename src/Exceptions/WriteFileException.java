package Exceptions;

public class WriteFileException extends RuntimeException {
	public WriteFileException() {
        super("An undetected problem occurred, writing primes into file isnt finished.");
    }
	public WriteFileException(String s) {
        super(s);
    }
}