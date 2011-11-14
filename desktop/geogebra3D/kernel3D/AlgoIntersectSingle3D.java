package geogebra3D.kernel3D;

import geogebra.common.euclidian.EuclidianConstants;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;

import geogebra.kernel.kernelND.GeoPointND;


/**
 * Single intersection point 
 */
public class AlgoIntersectSingle3D extends AlgoIntersect3D {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// input
	private AlgoIntersect3D algo;
	private int index; // index of point in algo	
	private GeoPointND refPoint;
	
	
	// output
	private GeoPoint3D point;
	
	private GeoPoint3D [] parentOutput;

	// intersection point is the (a) nearest to refPoint
	AlgoIntersectSingle3D(String label, AlgoIntersect3D algo, GeoPointND refPoint) {
		super(algo.getConstruction());
		this.algo = algo;
		algo.addUser(); // this algorithm is a user of algo			
		this.refPoint = refPoint;
		
		point = new GeoPoint3D(algo.getConstruction());								
		
		setInputOutput(); 
		initForNearToRelationship();
		compute();
		point.setLabel(label);		
	}
	
	
	// intersection point is index-th intersection point of algo
	AlgoIntersectSingle3D(String label, AlgoIntersect3D algo, int index) {
		super(algo.getConstruction());
		this.algo = algo;
		algo.addUser(); // this algorithm is a user of algo			
		
		// check index
		if (index < 0) 
			index = 0;
		else 
			this.index = index;
		
		point = new GeoPoint3D(algo.getConstruction());								
		
		setInputOutput(); 
		initForNearToRelationship();
		compute();
		point.setLabel(label);		
	}
	
    protected boolean showUndefinedPointsInAlgebraView() {
    	return true;
    }
	
	public String getClassName() {
		return "AlgoIntersectSingle3D";
	}
    
    public int getRelatedModeID() {
    	return EuclidianConstants.MODE_INTERSECT;
    }
	
	// for AlgoElement
	public void setInputOutput() {
		if (refPoint==null) {
			input = new GeoElement[3];
			input[0] = algo.getInput()[0];
			input[1] = algo.getInput()[1];
			//			dummy value to store the index of the intersection point
			// index + 1 is used here to let numbering start at 1
			input[2] = new GeoNumeric(cons, index+1); 
		} else {
			input = new GeoElement[3];
			input[0] = algo.getInput()[0];
			input[1] = algo.getInput()[1];
			input[2] = (GeoElement) refPoint; 
		}

		setOutputLength(1);
		this.setOutput(0, point);
	                   
		setDependencies(); // done by AlgoElement
	}
	
	public GeoPoint3D getPoint() {
		return point;
	}
	
	protected GeoPoint3D [] getIntersectionPoints() {
		return (GeoPoint3D []) getOutput();
	}
		
	protected GeoPoint3D[] getLastDefinedIntersectionPoints() {	
		return null;
	}

    public boolean isNearToAlgorithm() {
    	return true;
    }
    
	protected final void initForNearToRelationship() {				
		parentOutput = algo.getIntersectionPoints();					
		
		// tell parent algorithm about the loaded position;
		// this is needed for initing the intersection algo with
		// the intersection point stored in XML files
		algo.initForNearToRelationship();
		algo.setIntersectionPoint(index, point);
		algo.compute();
	}

	protected void compute() {
		parentOutput = algo.getIntersectionPoints();
		
		if (refPoint!=null)
			if (refPoint.isDefined())
				index = algo.getClosestPointIndex(refPoint);
		
		if (input[0].isDefined() && input[1].isDefined() && index < parentOutput.length) {	
			// 	get coordinates from helper algorithm
			point.setCoords(parentOutput[index].getCoords());
		} else {
			point.setUndefined();
		}
	}   
	
	public void remove() {
		super.remove();
		algo.removeUser(); // this algorithm was a user of algo
	}



}
