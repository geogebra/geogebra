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

package org.geogebra.desktop.geogebra3D.euclidianForPlane;

import org.geogebra.common.euclidian.EuclidianControllerCompanion;
import org.geogebra.common.geogebra3D.euclidianForPlane.EuclidianControllerForPlaneCompanion;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.desktop.euclidian.EuclidianControllerD;

/**
 * controller for view for plane
 */
public class EuclidianControllerForPlaneD extends EuclidianControllerD {

	/**
	 * constructor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public EuclidianControllerForPlaneD(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected EuclidianControllerCompanion newCompanion() {
		return new EuclidianControllerForPlaneCompanion(this);
	}
}
