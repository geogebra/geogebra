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

package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConicSection;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;

/**
 * 
 * Vertex[ &lt;Conic section&gt; ]
 * 
 * @author mathieu
 *
 */
public class CmdCorner3D extends CmdVertex3D {

	/**
	 * @param kernel
	 *            kernel
	 */
	public CmdCorner3D(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		if (n == 1) {
			arg = resArgs(c, info);
			if (arg[0] instanceof GeoConicSection) {

				return kernel.getManager3D().corner(c.getLabels(),
						(GeoConicSection) arg[0]);
			}
		}

		return super.process(c, info);
	}

}
