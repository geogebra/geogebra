/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.kernel.Construction;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoLine;
import geogebra.kernel.geos.GeoPoint;
import geogebra.kernel.geos.GeoSegment;


public class AlgoLineBisectorSegment extends AlgoElement {

    private GeoSegment s;  // input   
    private GeoLine  g;     // output        
    
    private GeoPoint midPoint;
        
    /** Creates new AlgoLineBisector */
    public AlgoLineBisectorSegment(Construction cons, String label, GeoSegment s) {
        super(cons);
        this.s = s;             
        g = new GeoLine(cons); 
        midPoint = new GeoPoint(cons);
        g.setStartPoint(midPoint);
        setInputOutput(); // for AlgoElement
        
        // compute bisector of A, B
        compute();      
        g.setLabel(label);
    }   
    
    @Override
	public String getClassName() {
        return "AlgoLineBisectorSegment";
    }
    
    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_LINE_BISECTOR;
    }
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = s;        
  
        super.setOutputLength(1);
        super.setOutput(0, g);
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoLine getLine() { return g; }
    GeoSegment getSegment() {return s;  }
    
    // line through P normal to v
    @Override
	public final void compute() { 
    	 GeoPoint A = s.getStartPoint();     
    	 GeoPoint B = s.getEndPoint();
    	
        // get inhomogenous coords
        double ax = A.inhomX;
        double ay = A.inhomY;
        double bx = B.inhomX;
        double by = B.inhomY;
         
        // comput line
        g.x = ax - bx;
        g.y = ay - by;
        midPoint.setCoords( (ax + bx), (ay + by), 2.0);   
        g.z = -(midPoint.x * g.x + midPoint.y * g.y)/2.0;     
    }   
    
    @Override
	final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("LineBisectorOfA",s.getLabel());

    }
}
