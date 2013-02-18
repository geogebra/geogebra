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
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;


/**
 * Compute a line through a point and orthogonal to ...
 *
 * @author  matthieu
 * @version 
 */
public abstract class AlgoOrtho extends AlgoElement3D {

 
	protected GeoPointND point; // input
	protected GeoElement inputOrtho; // input
	protected GeoLine3D line; // output       


    public AlgoOrtho(Construction cons, String label, GeoPointND point, GeoElement ortho) {
        super(cons);
        this.point = point;
        this.inputOrtho = ortho;
        line = new GeoLine3D(cons);
        
        setSpecificInputOutput();

        // compute line 
        compute();
        line.setLabel(label);
    }

    /**
     * set specific input/output for this algo
     */
    protected void setSpecificInputOutput(){
    	setInputOutput(new GeoElement[] {(GeoElement) point, inputOrtho}, new GeoElement[] {line});
    }

    public GeoLine3D getLine() {
        return line;
    }
    
    protected GeoPointND getPoint(){
    	return point;
    }

    protected GeoElement getInputOrtho(){
    	return inputOrtho;
    }


    @Override
    public String toString(StringTemplate tpl) {
    	return loc.getPlain("LineThroughAPerpendicularToB",point.getLabel(tpl),inputOrtho.getLabel(tpl));
    }
}
