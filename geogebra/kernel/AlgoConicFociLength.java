/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoEllipseFociLength.java
 *
 * Created on 15. November 2001, 21:37
 */

package geogebra.kernel;

import geogebra.kernel.arithmetic.NumberValue;

/**
 *
 * @author  Markus
 * @version 
 */
public abstract class AlgoConicFociLength extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected GeoPoint A, B; // input    
    protected NumberValue a; // input     
    private GeoElement ageo;
    private GeoConic conic; // output             

    AlgoConicFociLength(
        Construction cons,
        String label,
        GeoPoint A,
        GeoPoint B,
        NumberValue a) {
        super(cons);
        this.A = A;
        this.B = B;
        this.a = a;
        ageo = a.toGeoElement();
        conic = new GeoConic(cons);
        setInputOutput(); // for AlgoElement

        compute();
        conic.setLabel(label);
    }

    public abstract String getClassName();

    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[3];
        input[0] = A;
        input[1] = B;
        input[2] = ageo;

        output = new GeoElement[1];
        output[0] = conic;
        setDependencies(); // done by AlgoElement
    }

    GeoConic getConic() {
        return conic;
    }
    GeoPoint getFocus1() {
        return A;
    }
    GeoPoint getFocus2() {
        return B;
    }

    // compute ellipse with foci A, B and length of half axis a
    protected final void compute() {
        conic.setEllipseHyperbola(A, B, a.getDouble());
    }

   
}
