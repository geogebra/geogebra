/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoOrthoLinePointLine.java
 *
 * line through P orthogonal to l
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.kernel.Construction;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoLine;
import geogebra.kernel.geos.GeoPoint;
import geogebra.kernel.geos.GeoVec3D;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoOrthoLinePointLine extends AlgoElement {

    private GeoPoint P; // input
    private GeoLine l; // input
    private GeoLine g; // output       

    /** Creates new AlgoOrthoLinePointLine 
     * @param cons 
     * @param label 
     * @param P 
     * @param l */
    public AlgoOrthoLinePointLine(
        Construction cons,
        String label,
        GeoPoint P,
        GeoLine l) {
        super(cons);
        this.P = P;
        this.l = l;
        g = new GeoLine(cons);
        g.setStartPoint(P);
        setInputOutput(); // for AlgoElement

        // compute line 
        compute();
        setIncidence();
        g.setLabel(label);
    }

    private void setIncidence() {
    	P.addIncidence(g);
	}

	@Override
	public String getClassName() {
        return "AlgoOrthoLinePointLine";
    }
    
    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_ORTHOGONAL;
    }  

    // for AlgoElement
    @Override
	public void setInputOutput() {
        input = new GeoElement[2];
        input[0] = P;
        input[1] = l;

        setOutputLength(1);
        setOutput(0,g);
        setDependencies(); // done by AlgoElement
    }

    public GeoLine getLine() {
        return g;
    }
    
    GeoPoint getP() {
        return P;
    }
    
    GeoLine getl() {
        return l;
    }

    // calc the line g through P and normal to l   
    @Override
	public final void compute() {
        GeoVec3D.cross(P, l.x, l.y, 0.0, g);
    }

    @Override
	public final String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("LineThroughAPerpendicularToB",P.getLabel(),l.getLabel());

    }
}
