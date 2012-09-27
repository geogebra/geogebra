package org.rosuda.REngine ;

/**
 * Exception thrown when an error occurs during eval. 
 * 
 * <p>
 * This class is a placeholder and should be extended when more information
 * can be extracted from R (call stack, etc ... )
 * </p>
 */
public class REngineEvalException extends REngineException {
	
	/**
	 * Value returned by the rniEval native method when the input passed to eval
	 * is invalid
	 */ 
	public static final int INVALID_INPUT = -1 ;
	
	/**
	 * Value returned by the rniEval native method when an error occured during 
	 * eval (stop, ...)
	 */
	public static final int ERROR = -2 ;  

	/**
	 * Type of eval error
	 */
	protected int type ; 
	
	/**
	 * Constructor
	 *
	 * @param eng associated REngine
	 * @param message error message
	 * @param type type of error (ERROR or INVALID_INPUT)
	 */
	public REngineEvalException( REngine eng, String message, int type ){
		super( eng, message );
		this.type = type ;
	}
	
	/**
	 * Constructor using ERROR type
	 *
	 * @param eng associated REngine
	 * @param message error message
	 */
	public REngineEvalException( REngine eng, String message){
		this( eng, message, ERROR );
	}
	
	/**
	 * @return the type of error (ERROR or INVALID_INPUT)
	 */
	public int getType(){
		return type ;
	}
	
}
