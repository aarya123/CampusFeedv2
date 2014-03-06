package controllers;

public class AuthorizationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2698330594096281734L;
	
	public AuthorizationException() {
		super();
	}
	
	public AuthorizationException(String msg) {
		super(msg);
	}

}
