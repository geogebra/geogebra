/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoEccentricity.java
 *
 */

package geogebra.kernel;


/**
 *
 * @author  Michael
 * @version 
 */
public class AlgoEccentricity extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoConic c; // input
    private GeoNumeric num; // output                  

    AlgoEccentricity(Construction cons, String label, GeoConic c) {
        super(cons);
        this.c = c;
        num = new GeoNumeric(cons);
        setInputOutput(); // for AlgoElement                
        compute();
        num.setLabel(label);
    }

    public String getClassName() {
        return "AlgoEccentricity";
    }

    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = c;

        output = new GeoElement[1];
        output[0] = num;
        setDependencies(); // done by AlgoElement
    }

    GeoNumeric getEccentricity() {
        return num;
    }
    GeoConic getConic() {
        return c;
    }

    // set excentricity
    protected final void compute() {
        switch (c.type) {
        case GeoConic.CONIC_CIRCLE :
            num.setValue(0.0);
            break;

        case GeoConic.CONIC_PARABOLA :
            num.setValue(1.0);
            break;

            case GeoConic.CONIC_HYPERBOLA :
            case GeoConic.CONIC_ELLIPSE :
                num.setValue(c.eccentricity);
                break;

            default :
                num.setUndefined();
        }
    }

    final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
    	return app.getPlain("EccentricityOfA",c.getLabel());
    }
}
