package hr.fer.zemris.chat.utils;

public class InvalidMessageException extends Exception{


	private static final long serialVersionUID = 1L;
	
	public InvalidMessageException() {
		super();
	}
	
	public InvalidMessageException(String text) {
		super(text);
	}
}
