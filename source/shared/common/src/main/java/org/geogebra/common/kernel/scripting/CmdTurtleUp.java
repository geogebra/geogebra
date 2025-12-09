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

package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;

/**
 * Lifts up the pen.
 * 
 * TurtleUp[ &lt;Turtle&gt;]
 * 
 * @author judit
 */
public class CmdTurtleUp extends CmdTurtleCommand {

	/**
	 * @param kernel
	 *            the kernel
	 */
	public CmdTurtleUp(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected void performTurtleCommand(Command c, GeoElement[] args)
			throws MyError {
		getTurtle(args).setPenDown(false);
	}

}
