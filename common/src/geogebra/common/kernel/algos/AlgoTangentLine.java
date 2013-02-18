/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoTangentLine.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoVector;


/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoTangentLine extends AlgoElement implements TangentAlgo{

    private GeoLine g;  // input
    private GeoConic c;  // input
    private GeoLine [] tangents;     // output  
    
    private GeoLine diameter;
    private GeoVector direction;
    private AlgoIntersectLineConic algoIntersect;
    private GeoPoint [] tangentPoints;
    private int i;
        
    /** Creates new AlgoTangentLine */
    AlgoTangentLine(Construction cons, String label, GeoLine g, GeoConic c) {
        this(cons, g,c);
        GeoElement.setLabels(label, tangents);            
    }
    
    public AlgoTangentLine(Construction cons, String [] labels, GeoLine g, GeoConic c) {
        this(cons, g,c);
        GeoElement.setLabels(labels, tangents);            
    }
    
    @Override
	public Commands getClassName() {
		return Commands.Tangent;
	}      
    
    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_TANGENTS;
    }
    
    AlgoTangentLine(Construction cons, GeoLine g, GeoConic c) {
        super(cons);
        this.g = g;
        this.c = c;                
        
        // the tangents are computed by intersecting the
        // diameter line of g with c
        diameter = new GeoLine(cons);        
        direction = new GeoVector(cons);
        g.getDirection(direction);
        c.diameterLine(direction, diameter);
        algoIntersect = new AlgoIntersectLineConic(cons, diameter, c);
        //  this is only an internal Algorithm that shouldn't be in the construction list
        cons.removeFromConstructionList(algoIntersect); 
        tangentPoints = algoIntersect.getIntersectionPoints();
        
        tangents = new GeoLine[2];
        tangents[0] = new GeoLine(cons);
        tangents[1] = new GeoLine(cons);
        tangents[0].setStartPoint(tangentPoints[0]);
        tangents[1].setStartPoint(tangentPoints[1]);
        
        setInputOutput(); // for AlgoElement
                
        compute();                      
    }   
    
    // for AlgoElement
    @Override
	public void setInputOutput() {
        input = new GeoElement[2];
        input[0] = g;
        input[1] = c;
          
        super.setOutput(tangents);
        setDependencies(); // done by AlgoElement
    }    
    
    public GeoLine [] getTangents() { return tangents; }
    GeoLine getLine() { return g; }
    GeoConic getConic() { return c; }
    
    /**
     * return intersection point of tangent line and conic c.
     * return null if line is not defined as tangent of conic c.
     */
    GeoPoint getTangentPoint(GeoConic conic, GeoLine line) {
        if (conic != c) return null;
        
        if (line == tangents[0]) {
			return tangentPoints[0];
        } else if (line == tangents[1]) {
			return tangentPoints[1];
        } else {
            return null;
        }
    }
    
    /**
     * Inits the helping interesection algorithm to take
     * the current position of the lines into account.
     * This is important so the the tangent lines are not
     * switched after loading a file
     */
    @Override
	public void initForNearToRelationship() {
    	// if first tangent point is not on first tangent,
    	// we switch the intersection points
    	if (!tangents[0].isOnFullLine(tangentPoints[0], Kernel.MIN_PRECISION)) {
        	algoIntersect.initForNearToRelationship();
        	
        	// remember first point
    		double px = tangentPoints[0].x;
    		double py = tangentPoints[0].y;
    		double pz = tangentPoints[0].z;
    		
    		// first = second
    		algoIntersect.setIntersectionPoint(0, tangentPoints[1]);
    		
    		// second = first
    		tangentPoints[1].setCoords(px, py, pz);
    		algoIntersect.setIntersectionPoint(1, tangentPoints[1]);
     	}		    	
    }
    
    // calc tangents parallel to g
    @Override
	public final void compute() {               
        // degenerates should not have any tangents
        if (c.isDegenerate()) {
            tangents[0].setUndefined();
            tangents[1].setUndefined();
            return;
        }         
        
        // update diameter line
        g.getDirection(direction);
        c.diameterLine(direction, diameter);
                       
        // intersect diameter line with conic -> tangentPoints
        algoIntersect.update();

        // calc tangents through tangentPoints
        for (i=0; i < tangents.length; i++) {
            tangents[i].x = g.x;
            tangents[i].y = g.y;                
            tangents[i].z = -( tangentPoints[i].inhomX * g.x +
                                        tangentPoints[i].inhomY * g.y);
        }                
    }
    
    @Override
	public final String toString(StringTemplate tpl) {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
    	return loc.getPlain("TangentToAParallelToB",c.getLabel(tpl),g.getLabel(tpl));
    }

	// TODO Consider locusequability
}
