/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoConicFivePoints.java
 *
 * Created on 15. November 2001, 21:37
 */

package geogebra.kernel;

import geogebra.euclidian.EuclidianConstants;


/**
 *
 * @author  Tam
 * @version 
 */
public class AlgoConicFromCoeffList extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoList L; // input  A list of 6 coeffs      
    private GeoConic conic; // output             


    AlgoConicFromCoeffList(Construction cons, String label, GeoList L) {
        super(cons);
        this.L = L;
        conic = new GeoConic(cons, label, L);
        
        setInputOutput(); // for AlgoElement

    }

    public String getClassName() {
        return "AlgoConicFivePoints";
    }
    
    public int getRelatedModeID() {
    	return EuclidianConstants.MODE_CONIC_FIVE_POINTS;
    }

    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[] {L};

        setOutputLength(1);
        setOutput(0, conic);
       
        setDependencies(); // done by AlgoElement
    }

    GeoConic getConic() {
        return conic;
    }
    GeoList getCoeffList() {
        return L;
    }


    protected final void compute() {
		conic.setCoeffs(((GeoNumeric)L.get(0)).getDouble(),
				((GeoNumeric)L.get(3)).getDouble(),
				((GeoNumeric)L.get(1)).getDouble(),
				((GeoNumeric)L.get(4)).getDouble(),
				((GeoNumeric)L.get(5)).getDouble(),
				((GeoNumeric)L.get(2)).getDouble());
    }




    final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("ConicFromCoeffList",L.getLabel());
    }
}
