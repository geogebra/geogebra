/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoMidPoint.java
 *
 * Created on 24. September 2001, 21:37
 */

package geogebra.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoSegment;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoMidpointSegment extends AlgoElement {

    private GeoSegment segment; // input
    private GeoPoint2 M; // output        
    private GeoPoint2 P, Q; // endpoints of segment
    
    /** Creates new AlgoVector */
    public AlgoMidpointSegment(Construction cons, String label, GeoSegment segment) {
    	this(cons, segment);
    	M.setLabel(label);
    }
	
    AlgoMidpointSegment(Construction cons, GeoSegment segment) {
        super(cons);
        this.segment = segment;
        
        // create new Point
        M = new GeoPoint2(cons);
        setInputOutput();
        
        P = segment.getStartPoint();
    	Q = segment.getEndPoint();

        // compute M = (P + Q)/2
        compute();        
    }

    @Override
	public String getClassName() {
        return "AlgoMidpointSegment";
    }

    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_MIDPOINT;
    }
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = segment;        

        super.setOutputLength(1);
        super.setOutput(0, M);
        setDependencies(); // done by AlgoElement
    }

    public GeoPoint2 getPoint() {
        return M;
    }

    // calc midpoint
    @Override
	public final void compute() {
        boolean pInf = P.isInfinite();
        boolean qInf = Q.isInfinite();

        if (!pInf && !qInf) {
            // M = (P + Q) / 2          
            M.setCoords(
                (P.inhomX + Q.inhomX) / 2.0d,
                (P.inhomY + Q.inhomY) / 2.0d,
                1.0);
        } else if (pInf && qInf)
            M.setUndefined();
        else if (pInf)
            M.setCoords(P);
        else // qInf
            M.setCoords(Q);
    }

    @Override
	final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("MidpointOfA",segment.getLabel());

    }
}
