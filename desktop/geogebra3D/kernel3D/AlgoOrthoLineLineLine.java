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
import geogebra.common.kernel.Matrix.CoordMatrixUtil;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoLineND;


/**
 * Compute a line orthogonal to two lines
 *
 * @author  matthieu
 * @version 
 */
public class AlgoOrthoLineLineLine extends AlgoOrthoLineLine {

 
    private GeoLineND line2; // input     


    public AlgoOrthoLineLineLine(Construction cons, String label, GeoLineND line1, GeoLineND line2) {
        super(cons,label,line1);
        this.line2 = line2;
        
        setInputOutput(new GeoElement[] {(GeoElement) line1, (GeoElement) line2}, new GeoElement[] {getLine()});

        // compute line 
        compute();
        getLine().setLabel(label);
    }

    @Override
	public Commands getClassName() {
        return Commands.OrthogonalLine;
    }

	@Override
	protected void setOriginAndDirection2() {
    	Coords o2 = line2.getPointInD(3, 0);
    	direction2 = line2.getPointInD(3, 1).sub(o2);
		Coords[] points = CoordMatrixUtil.nearestPointsFromTwoLines(origin1, direction1, o2, direction2);
		origin = points[0];
	
	}

	// TODO Consider locusequability
    

    @Override
	public String toString(StringTemplate tpl) {
        return app.getPlain("LinePerpendicularToAandB",line1.getLabel(tpl),line2.getLabel(tpl));

    }
    


}
