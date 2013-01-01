/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoDirectrix.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoVec2D;
import geogebra.common.kernel.kernelND.GeoConicNDConstants;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoDirectrix extends AlgoElement {

    private GeoConic c; // input
    private GeoLine directrix; // output          

    private GeoVec2D[] eigenvec;
    private GeoVec2D b;
    private GeoPoint P;

    public AlgoDirectrix(Construction cons, String label, GeoConic c) {
        super(cons);
        this.c = c;

        eigenvec = c.eigenvec;
        b = c.b;

        directrix = new GeoLine(cons);
        P = new GeoPoint(cons);
        directrix.setStartPoint(P);

        setInputOutput(); // for AlgoElement                
        compute();
        directrix.setLabel(label);
    }

    @Override
	public Commands getClassName() {
        return Commands.Directrix;
    }

    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = c;

        super.setOutputLength(1);
        super.setOutput(0, directrix);
        setDependencies(); // done by AlgoElement
    }

    public GeoLine getDirectrix() {
        return directrix;
    }
    GeoConic getConic() {
        return c;
    }

    // calc axes
    @Override
	public final void compute() {
        // only parabola has directrix
        if (c.type == GeoConicNDConstants.CONIC_PARABOLA) {
            // directrix has direction of second eigenvector
            // through point (b - p/2* eigenvec1)        
            directrix.x = -eigenvec[1].getY();
            directrix.y = eigenvec[1].getX();
            double px = b.getX() - c.p / 2.0 * eigenvec[0].getX();
            double py = b.getY() - c.p / 2.0 * eigenvec[0].getY();
            directrix.z = - (directrix.x * px + directrix.y * py);

            P.setCoords(px, py, 1.0);
        } else
            directrix.setUndefined();
    }

    @Override
	final public String toString(StringTemplate tpl) {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("DirectrixOfA",c.getLabel(tpl));
    }

	// TODO Consider locusequability
}
