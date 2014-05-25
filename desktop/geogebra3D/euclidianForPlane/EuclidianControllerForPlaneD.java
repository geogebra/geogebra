/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

package geogebra3D.euclidianForPlane;

import geogebra.common.euclidian.EuclidianControllerCompanion;
import geogebra.common.geogebra3D.euclidianForPlane.EuclidianControllerForPlaneCompanion;
import geogebra.common.kernel.Kernel;
import geogebra.euclidian.EuclidianControllerD;

/**
 * controller for view for plane
 */
public class EuclidianControllerForPlaneD extends EuclidianControllerD {

	/**
	 * constructor
	 * @param kernel kernel
	 */
	public EuclidianControllerForPlaneD(Kernel kernel) {
		super(kernel);
	}


	@Override
	protected EuclidianControllerCompanion newCompanion(){
		return new EuclidianControllerForPlaneCompanion(this);
	}
}
