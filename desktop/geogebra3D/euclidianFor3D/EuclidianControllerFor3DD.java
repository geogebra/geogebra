/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

package geogebra3D.euclidianFor3D;

import geogebra.common.euclidian.EuclidianControllerCompanion;
import geogebra.common.geogebra3D.euclidianFor3D.EuclidianControllerFor3DCompanion;
import geogebra.common.kernel.Kernel;
import geogebra.euclidian.EuclidianControllerD;

/**
 * euclidian controller for 2D view with 3D geos
 */
public class EuclidianControllerFor3DD extends
		EuclidianControllerD {

	public EuclidianControllerFor3DD(Kernel kernel) {
		super(kernel);
	}
	

	@Override
	protected EuclidianControllerCompanion newCompanion(){
		return new EuclidianControllerFor3DCompanion(this);
	}
	

}
