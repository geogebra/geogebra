/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoLinePointVector.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoLinePointVector extends AlgoElement {

    private GeoPoint P; // input
    private GeoVector v; // input
    private GeoLine g; // output       

    /** Creates new AlgoJoinPoints */
    AlgoLinePointVector(
        Construction cons,
        String label,
        GeoPoint P,
        GeoVector v) {
        super(cons);
        this.P = P;
        this.v = v;
        g = new GeoLine(cons);
        g.setStartPoint(P);
        setInputOutput(); // for AlgoElement

        // compute line through P, Q
        compute();
        setIncidence();
        g.setLabel(label);
    }

    private void setIncidence() {
    	P.addIncidence(g);
	}
    
    
    @Override
	public String getClassName() {
        return "AlgoLinePointVector";
    }

    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = P;
        input[1] = v;
        
        super.setOutputLength(1);
        super.setOutput(0, g);
        setDependencies(); // done by AlgoElement
    }

    GeoLine getLine() {
        return g;
    }
    GeoPoint getP() {
        return P;
    }
    GeoVector getv() {
        return v;
    }

    // calc the line g through P and Q    
    @Override
	protected final void compute() {
        // g = cross(P, v)
        GeoVec3D.lineThroughPointVector(P, v, g);
    }

    @Override
	final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("LineThroughAwithDirectionB",P.getLabel(),v.getLabel());
    }

}
