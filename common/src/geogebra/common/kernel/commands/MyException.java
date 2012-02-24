package geogebra.common.kernel.commands;
/**
 * Exception that has a type atribute
 *
 */
public class MyException extends Exception {

	private static final long serialVersionUID = 1L;
	/** invalid input (general error message) */
	public static final int INVALID_INPUT = 1;
	private int errorType = INVALID_INPUT;
	/**
	 * 
	 * @param string error message
	 * @param errorType error type
	 */
	public MyException(String string, int errorType) {
		super(string);
		this.errorType = errorType;
	}
	/**
	 * @return error type
	 */
	public int getErrorType() {
		return errorType;
	}
}
