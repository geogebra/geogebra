/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */

package geogebra.geogebra3D.web.euclidianFor3D;

import geogebra.common.euclidian.EuclidianControllerCreator;
import geogebra.common.geogebra3D.euclidianFor3D.EuclidianControllerCreatorFor3D;
import geogebra.common.kernel.Kernel;
import geogebra.web.euclidian.EuclidianControllerW;

/**
 * euclidian controller for 2D view with 3D geos
 */
public class EuclidianControllerFor3DW extends
		EuclidianControllerW {

	public EuclidianControllerFor3DW(Kernel kernel) {
		super(kernel);
	}
	

	@Override
	protected EuclidianControllerCreator newCreator(){
		return new EuclidianControllerCreatorFor3D(this);
	}
	

}
