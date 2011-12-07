/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoJoinPoints.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.kernel.Construction;
import geogebra.kernel.geos.GeoLine;
import geogebra.kernel.geos.GeoPoint;
import geogebra.kernel.geos.GeoVec3D;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoJoinPoints extends AlgoElement {

    private GeoPoint P, Q;  // input
    private GeoLine  g;     // output       
        
    /** Creates new AlgoJoinPoints */
    public AlgoJoinPoints(Construction cons, String label, GeoPoint P, GeoPoint Q) {
        this(cons, P, Q);
        g.setLabel(label);
    }   
    
    public AlgoJoinPoints(Construction cons, GeoPoint P, GeoPoint Q) {
        super(cons);
        this.P = P;
        this.Q = Q;                
        g = new GeoLine(cons); 
        g.setStartPoint(P);
        g.setEndPoint(Q);
        setInputOutput(); // for AlgoElement
        
        // compute line through P, Q
        compute();      
        addIncidence();
    }   
    
    private void addIncidence() {
        P.addIncidence(g);
        Q.addIncidence(g);
	}

	@Override
	public String getClassName() {
        return "AlgoJoinPoints";
    }

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_JOIN;
	}
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = P;
        input[1] = Q;
         
        super.setOutputLength(1);
        super.setOutput(0, g);
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoLine getLine() { return g; }
    GeoPoint getP() { return P; }
    GeoPoint getQ() { return Q; }
    
    // calc the line g through P and Q    
    @Override
	public final void compute() {
        // g = P v Q  <=>  g_n : n = P x Q
        // g = cross(P, Q)
        GeoVec3D.lineThroughPoints(P, Q, g);
    }   
    
    @Override
	final public String toString() {
     
        // Michael Borcherds 2008-03-31
        // simplified to allow better translation
        return app.getPlain("LineThroughAB",P.getLabel(),Q.getLabel());

    }
}
