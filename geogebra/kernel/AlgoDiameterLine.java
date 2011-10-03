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

package geogebra.kernel;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoDiameterLine extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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

    public String getClassName() {
        return "AlgoDiameterLine";
    }

    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = g;
        input[1] = c;

        output = new GeoElement[1];
        output[0] = diameter;
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
    protected final void compute() {
        g.getDirection(v);
        c.diameterLine(v, diameter);
    }

    final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("DiameterOfAConjugateToB",c.getLabel(),g.getLabel());
    }
}
