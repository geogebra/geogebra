/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

package org.geogebra.web.geogebra3D.web.euclidianForPlane;

import org.geogebra.common.euclidian.EuclidianControllerCompanion;
import org.geogebra.common.geogebra3D.euclidianForPlane.EuclidianControllerForPlaneCompanion;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.web.html5.euclidian.EuclidianControllerW;

/**
 * controller for view for plane
 */
public class EuclidianControllerForPlaneW extends EuclidianControllerW {

	/**
	 * constructor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public EuclidianControllerForPlaneW(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected EuclidianControllerCompanion newCompanion() {
		return new EuclidianControllerForPlaneCompanion(this);
	}
}
