/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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

	public NoSymbolicParametersException(Throwable cause) {
		super("No symbolic parameters available", cause);
	}

}
