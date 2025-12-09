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

package org.geogebra.web.geogebra3D.web.euclidianFor3D;

import org.geogebra.common.euclidian.EuclidianControllerCompanion;
import org.geogebra.common.geogebra3D.euclidianFor3D.EuclidianControllerFor3DCompanion;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.web.html5.euclidian.EuclidianControllerW;

/**
 * euclidian controller for 2D view with 3D geos
 */
public class EuclidianControllerFor3DW extends EuclidianControllerW {

	/**
	 * @param kernel
	 *            kernel
	 */
	public EuclidianControllerFor3DW(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected EuclidianControllerCompanion newCompanion() {
		return new EuclidianControllerFor3DCompanion(this);
	}

}
