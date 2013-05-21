/**
 *
 */
package geogebra.html5.kernel.external;

/**
 * @author dave.trudes
 *
 */
public class NotImplementedException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 7157908905623414085L;

	public NotImplementedException(String name){
		super(name + " is not implemented!");
	}
}
