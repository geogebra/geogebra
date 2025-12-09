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

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Command to force e.g Corner[n,1] to create a 3D point even if n!=3
 * 
 * @author mathieu
 *
 */
public class CmdVertexForce3D extends CmdVertex3D {
	/**
	 * @param kernel
	 *            Kernel
	 */
	public CmdVertexForce3D(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoPointND cornerOfDrawingPad(String label, GeoNumberValue number,
			GeoNumberValue ev) {

		return cornerOfDrawingPad3D(label, number, ev);

	}

}
