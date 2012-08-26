/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoConicPart;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoVec3D;
import geogebra.common.kernel.kernelND.GeoConicNDConstants;
import geogebra.common.main.App;

/**
 * Circle arc or sector defined by three points.
 */
public class AlgoConicPartCircumcircle extends AlgoConicPart {

	private GeoPoint A, B, C;	
	
	private GeoLine line; // for degenerate case

    public AlgoConicPartCircumcircle(Construction cons, String label,
    		GeoPoint A, GeoPoint B, GeoPoint C,
    		int type) 
    {
    	this(cons, A, B, C, type);
    	 conicPart.setLabel(label);
    }
    
    public AlgoConicPartCircumcircle(Construction cons,
    		GeoPoint A, GeoPoint B, GeoPoint C,
    		int type) {
        super(cons, type);        
        this.A = A;
        this.B = B; 
        this.C = C;
        
        // helper algo to get circle
        AlgoCircleThreePoints algo = 
        	new AlgoCircleThreePoints(cons, A, B, C);
        cons.removeFromConstructionList(algo);		
        conic = (GeoConic) algo.getCircle(); 
        
        conicPart = new GeoConicPart(cons, type);
        conicPart.addPointOnConic(A);
        conicPart.addPointOnConic(B);
        conicPart.addPointOnConic(C);
        
        setInputOutput(); // for AlgoElement      
        compute();               
        setIncidence();
    }    	        
    
	private void setIncidence() {
		A.addIncidence(conicPart);
		B.addIncidence(conicPart);
		C.addIncidence(conicPart);
	}

	@Override
	public Algos getClassName() {
		switch (type) {
			case GeoConicPart.CONIC_PART_ARC:
				return Algos.AlgoCircumcircleArc;
			default:
				return Algos.AlgoCircumcircleSector;
		}		
	}
	
	@Override
	public int getRelatedModeID() {
		switch (type) {
			case GeoConicPart.CONIC_PART_ARC:
				return EuclidianConstants.MODE_CIRCUMCIRCLE_ARC_THREE_POINTS;
			default:
				return EuclidianConstants.MODE_CIRCUMCIRCLE_SECTOR_THREE_POINTS;
		}
	}

    // for AlgoElement
	@Override
	protected void setInputOutput() {
        input = new GeoElement[3];
        input[0] = A;      
        input[1] = B;
        input[2] = C;        

        super.setOutputLength(1);
        super.setOutput(0, conicPart);

        setDependencies();
    }
    
    @Override
	public void compute() {
    	if (!conic.isDefined()) {
    		conicPart.setUndefined();
    		return;
    	}
    	
    	conicPart.set(conic); 
    	switch (conicPart.getType()) {
    		case GeoConicNDConstants.CONIC_PARALLEL_LINES: 	
    			computeDegenerate();
    			break;
    		
			case GeoConicNDConstants.CONIC_CIRCLE: 
				computeCircle();
		    	break;
		    
		    default:
		    	// this should not happen
		    	App.debug("AlgoCirclePartPoints: unexpected conic type: " + conicPart.getType());
		    	conicPart.setUndefined();
    	}	
    }
    
//  arc degenerated to segment or two rays
    private void computeDegenerate() {
		if (line == null) { // init lines 
			line = conicPart.getLines()[0];
			line.setStartPoint(A);
			line.setEndPoint(C);
			conicPart.getLines()[1].setStartPoint(C);
		}
		
		// make sure the line goes through A and C
		GeoVec3D.lineThroughPoints(A, C, line);
		
		// check if B is between A and C => (1) segment AC
		// otherwise we got (2) two rays starting at A and C in oposite directions
		// case (1): use parameters 0, 1 and positive orientation to tell conicPart how to behave
		// case (2): use parameters 0, 1 and negative orientation
		double lambda = GeoPoint.affineRatio(A, C, B);
		if (lambda < 0 || lambda > 1) {
			// two rays
			// second ray with start point C and direction of AC 				
			conicPart.getLines()[1].setCoords(line);
			// first ray with start point A and oposite direction
			line.changeSign();
			
			// tell conicPart about this case: two rays
			conicPart.setParameters(0, 1, false);
		} else {
			// segment
			// tell conicPart about this case: one segment
			conicPart.setParameters(0, 1, true);
		}
    }
    
//  circle through A, B, C 
    private void computeCircle() {
    	// start angle from vector MA
    	double alpha = Math.atan2(A.inhomY - conicPart.getTranslationVector().getY(), A.inhomX - conicPart.getTranslationVector().getX()); 
		// end angle from vector MC
    	double beta = Math.atan2(C.inhomY - conicPart.getTranslationVector().getY(), C.inhomX - conicPart.getTranslationVector().getX());
    	
    	// check orientation of triangle A, B, C to see
		// whether we have to swap start and end angle
		double det =  (B.inhomX - A.inhomX) * (C.inhomY - A.inhomY)
					- (B.inhomY - A.inhomY) * (C.inhomX - A.inhomX);
		
		conicPart.setParameters(alpha, beta, det > 0);
    }

	
	/**
	 * Method for LocusEqu.
	 * @return first point.
	 */
	public GeoPoint getA() {
		return A;
	}

	/**
	 * Method for LocusEqu.
	 * @return second point.
	 */
	public GeoPoint getB() {
		return B;
	}

	/**
	 * Method for LocusEqu.
	 * @return third point.
	 */
	public GeoPoint getC() {
		return C;
	}

	@Override
	public boolean isLocusEquable() {
		return true;
	}
}
