/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoDiameterLine.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoVector;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoDiameterLine extends AlgoElement {

    private GeoConic c; // input
    private GeoLine g; // input
    private GeoLine diameter; // output

    private GeoVector v;

    /** Creates new AlgoJoinPoints */
    AlgoDiameterLine(Construction cons, String label, GeoConic c, GeoLine g) {
        super(cons);
        this.c = c;
        this.g = g;
        diameter = new GeoLine(cons);
        v = new GeoVector(cons);

        setInputOutput(); // for AlgoElement

        compute();
        diameter.setLabel(label);
    }

    @Override
	public String getClassName() {
        return "AlgoDiameterLine";
    }

    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_POLAR_DIAMETER;
    }
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = g;
        input[1] = c;

        super.setOutputLength(1);
        super.setOutput(0, diameter);
        setDependencies(); // done by AlgoElement
    }

    GeoLine getLine() {
        return g;
    }
    GeoConic getConic() {
        return c;
    }
    GeoLine getDiameter() {
        return diameter;
    }

    // calc diameter line of v relativ to c
    @Override
	protected final void compute() {
        g.getDirection(v);
        c.diameterLine(v, diameter);
    }

    @Override
	final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("DiameterOfAConjugateToB",c.getLabel(),g.getLabel());
    }
}
