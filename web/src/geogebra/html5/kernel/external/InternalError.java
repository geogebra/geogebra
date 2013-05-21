/**
 *
 */
package geogebra.html5.kernel.external;

/**
 * @author dave.trudes
 *
 */
public class InternalError extends RuntimeException {

	private static final long serialVersionUID = -6518450317013799532L;

	public InternalError(){
		super();
	}

	public InternalError(String message){
		super(message);
	}

	public InternalError(String message, Throwable cause){
		super(message, cause);
	}
}
