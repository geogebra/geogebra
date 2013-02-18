/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoAngleConic.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoAngleConic extends AlgoElement {

    private GeoConic c; // input
    private GeoAngle angle; // output                  

    public AlgoAngleConic(Construction cons, String label, GeoConic c) {
        super(cons);
        this.c = c;
        angle = new GeoAngle(cons);
        setInputOutput(); // for AlgoElement                
        compute();
        angle.setLabel(label);
    }

    @Override
	public Commands getClassName() {
        return Commands.Angle;
    }

    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_ANGLE;
    }
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = c;

        setOutputLength(1);
        setOutput(0,angle);
        setDependencies(); // done by AlgoElement
    }

    public GeoAngle getAngle() {
        return angle;
    }
    
    GeoConic getConic() {
        return c;
    }

    // compute conic's angle
    @Override
	public final void compute() {
        // take a look at first eigenvector
        angle.setValue(Math.atan2(c.eigenvec[0].getY(), c.eigenvec[0].getX()));
    }

    @Override
	public final String toString(StringTemplate tpl) {
    	return loc.getPlain("AngleOfA",c.getLabel(tpl));
    }

	// TODO Consider locusequability
}
