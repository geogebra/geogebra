package geogebra.kernel.commands;

public class MyException extends Exception {

	public static final int INVALID_INPUT = 1;
	int errorType = INVALID_INPUT;
	
	public MyException(String string, int errorType) {
		super(string);
		this.errorType = errorType;
	}
	
	public int getErrorType() {
		return errorType;
	}


}
