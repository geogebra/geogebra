package org.geogebra.common.kernel.prover;

/**
 * An exception that is thrown when a geo or algo has no symbolic parameters.
 * 
 * @author Simon Weitzhofer
 *
 */
public class NoSymbolicParametersException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates new NoSymbolicParametersException exception
	 * 
	 */
	public NoSymbolicParametersException() {
		super("No symbolic parameters available");
	}

}
