/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoUnitVectorVector.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoVectorND;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoUnitVectorVector extends AlgoUnitVector2D {
    
        
    /** Creates new AlgoOrthoVectorVector */
    public AlgoUnitVectorVector(Construction cons, String label,GeoVectorND v) {        
        super(cons, label, (GeoElement) v);
    }   
    

    @Override
	final protected void setXY(){
    	x = ((GeoVector) inputGeo).x;
    	y = ((GeoVector) inputGeo).y;
    }



	@Override
	final protected GeoPointND getInputStartPoint() {
		return ((GeoVector) inputGeo).getStartPoint();
	}   
    
}
