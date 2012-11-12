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
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;


/**
 * Compute a line orthogonal to a line, through a point, and parallel to a plane
 *
 * @author  matthieu
 * @version 
 */
public class AlgoOrthoLineLinePointPlane extends AlgoOrthoLineLine {


	private GeoPointND point; // input     
	private GeoDirectionND direction; // input     


    public AlgoOrthoLineLinePointPlane(Construction cons, String label, GeoPointND point, GeoLineND line1, GeoDirectionND direction) {
        super(cons,label,line1);
        this.point = point;
        this.direction = direction;
               
        setInputOutput(new GeoElement[] {(GeoElement) point, (GeoElement) line1, (GeoElement) direction}, new GeoElement[] {getLine()});

        // compute line 
        compute();
        getLine().setLabel(label);
    }





	@Override
	public Algos getClassName() {
        return Algos.AlgoOrthoLineLinePointPlane;
    }




	@Override
	protected void setOriginAndDirection2() {
    	direction2 = direction.getDirectionInD3();
		origin = point.getInhomCoordsInD(3);
	
	}

	// TODO Consider locusequability
    
    @Override
	public String toString(StringTemplate tpl) {
        return app.getPlain("LineThroughAPerpendicularToBParallelToC",point.getLabel(tpl),line1.getLabel(tpl),direction.getLabel(tpl));

    }
    


}
