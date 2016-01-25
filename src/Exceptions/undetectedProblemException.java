package Exceptions;

public class undetectedProblemException extends RuntimeException {
	public undetectedProblemException() {
        super("An undetected problem occurred, test primes isnt finished");
    }
	undetectedProblemException(String s) {
        super(s);
    }
}