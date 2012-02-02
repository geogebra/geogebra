package geogebra.common.kernel.commands;

public class MyException extends Exception {

	private static final long serialVersionUID = 1L;
	
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
