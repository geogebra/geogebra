package org.geogebra.common.cas.error;

import org.geogebra.common.kernel.CASException;

/**
 * Signals a Computation exception from the CAS.
 * 
 * This exception gets thrown when the CAS runs into a problem (maybe syntax
 * error, incompatible input etc.) and cannot return the correct result.
 * Currently used in SingularWS only.
 * 
 * @author Zoltan Kovacs <zoltan@geogebra.org>
 */
public class ComputationException extends CASException {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates new computation exception
	 * 
	 * @param message
	 *            exception message
	 */
	public ComputationException(String message) {
		super(message);
	}

	@Override
	public String getKey() {
		return "CAS.GeneralErrorMessage"; // maybe we could set a better message
	}

}
