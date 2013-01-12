/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/


package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoVectorND;


/**
 * Compute a line through a point and parallel to a vector
 *
 * @author  matthieu
 * @version 
 */
public class AlgoLinePointVector3D extends AlgoLinePoint {

 

    public AlgoLinePointVector3D(Construction cons, String label, GeoPointND point, GeoVectorND v) {
        super(cons,label,point, (GeoElement) v);
    }

    @Override
	public Commands getClassName() {
        return Commands.Line;
    }

	@Override
	protected Coords getDirection() {
		return ((GeoVectorND) getInputParallel()).getCoordsInD(3);
	}



}
