package geogebra.kernel.algos;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.kernel.Construction;
import geogebra.kernel.geos.GeoNumeric;
import geogebra.kernel.geos.GeoPoint;

import java.util.ArrayList;


/**
 * Single intersection point 
 */
public class AlgoIntersectSingle extends AlgoIntersect {

	// input
	private AlgoIntersect algo;
	private int index; // index of point in algo, can be input directly or be calculated from refPoint
	private GeoPoint refPoint; // reference point in algo to calculate index; can be null or undefined
	
	// output
	private GeoPoint point;
	
	private GeoPoint [] parentOutput;

	// intersection point is the (a) nearest to refPoint
	public AlgoIntersectSingle(String label, AlgoIntersect algo, GeoPoint refPoint) {
		super((Construction) algo.cons);
		this.algo = algo;
		algo.addUser(); // this algorithm is a user of algo			
		this.refPoint = refPoint;
		
		point = new GeoPoint(algo.cons);								
		
		setInputOutput(); 
		initForNearToRelationship();
		compute();
		setIncidence();
		
		point.setLabel(label);		
	}
	
	// intersection point is index-th intersection point of algo
	public AlgoIntersectSingle(String label, AlgoIntersect algo, int index) {
		super((Construction) algo.cons);
		this.algo = algo;
		algo.addUser(); // this algorithm is a user of algo			
		
		// check index
		if (index < 0) 
			index = 0;
		else 
			this.index = index;
		
		refPoint = null;
		
		point = new GeoPoint(algo.cons);								
		
		setInputOutput(); 
		initForNearToRelationship();
		compute();
		setIncidence();
		point.setLabel(label);		
	}
	
	private void setIncidence() {
		//point's incidence with parent algo's two intersectable objects
		if (algo instanceof AlgoIntersectConics) {
			point.addIncidence( ((AlgoIntersectConics)algo).getA() );
			point.addIncidence( ((AlgoIntersectConics)algo).getB() );
			
			//these two lines are already done in point.addIncidence() 
			//((GeoConic) ((AlgoIntersectConics)algo).getA()).addPointOnConic(point);
			//((GeoConic) ((AlgoIntersectConics)algo).getB()).addPointOnConic(point);
			
		} else if  (algo instanceof AlgoIntersectLineConic) {
			point.addIncidence( ((AlgoIntersectLineConic)algo).getLine() );
			point.addIncidence( ((AlgoIntersectLineConic)algo).getConic() );
			
			//this is already done in point.addIncidence()
			//((AlgoIntersectLineConic)algo).getConic().addPointOnConic(point);
		}
		
		//points's incidence with one of the intersection points --
		// this is done in compute(), because it depends on the index, which can be changed dynamically
	}

	
    @Override
	protected boolean showUndefinedPointsInAlgebraView() {
    	return true;
    }
	
	@Override
	public String getClassName() {
		return "AlgoIntersectSingle";
	}
    
    @Override
	public int getRelatedModeID() {
    	return EuclidianConstants.MODE_INTERSECT;
    }
	
	// for AlgoElement
	@Override
	public void setInputOutput() {
		
		if (refPoint==null) {
			input = new GeoElement[3];
			input[0] = algo.input[0];
			input[1] = algo.input[1];
			//			dummy value to store the index of the intersection point
			// index + 1 is used here to let numbering start at 1
			input[2] = new GeoNumeric(cons, index+1); 
		} else {
			input = new GeoElement[3];
			input[0] = algo.input[0];
			input[1] = algo.input[1];
			input[2] = refPoint; 
		}
		
        super.setOutputLength(1);
        super.setOutput(0, point);
	                   
		setDependencies(); // done by AlgoElement
	}
	
	public GeoPoint getPoint() {
		return point;
	}
	
	@Override
	protected GeoPoint [] getIntersectionPoints() {
		return (GeoPoint[]) super.getOutput();
	}
		
	@Override
	protected GeoPoint[] getLastDefinedIntersectionPoints() {	
		return null;
	}

    @Override
	public boolean isNearToAlgorithm() {
    	return true;
    }
    
	@Override
	public
	final void initForNearToRelationship() {				
		parentOutput = algo.getIntersectionPoints();					
		
		// tell parent algorithm about the loaded position;
		// this is needed for initing the intersection algo with
		// the intersection point stored in XML files
		algo.initForNearToRelationship();
		algo.setIntersectionPoint(index, point);
		algo.compute();
	}

	@Override
	public void compute() {

		parentOutput = algo.getIntersectionPoints();
		
		if (point!=null) {
			if (kernel.getLoadingMode() && point.hasUpdatePrevilege) { //for backward compatability
				algo.setIntersectionPoint(index, point);
				point.hasUpdatePrevilege = false;
			} else if (kernel.isContinuous()) {
				int cpi = algo.getClosestPointIndex(point);
				if (cpi != index)
					algo.setIntersectionPoint(index, parentOutput[cpi]);
			}
		}
		
		
		//update index if reference point has been defined
		if (refPoint!=null)
			if (refPoint.isDefined())
				index = algo.getClosestPointIndex(refPoint);
		
		if (input[0].isDefined() && input[1].isDefined() && index < parentOutput.length) {	
			// 	get coordinates from helper algorithm
			point.setCoords(parentOutput[index]);
			
			if (point.getIncidenceList()!=null) {
				for (int i = 0; i < parentOutput.length; ++i) {
					if (!parentOutput[index].contains(parentOutput[i]))
						point.getIncidenceList().remove(parentOutput[i]);
				}
			}
			point.addIncidence(parentOutput[index]);
		} else {
			point.setUndefined();
			ArrayList<GeoElement> al = point.getIncidenceList();
			if (al != null)
				for (int i = 0; i < parentOutput.length; ++i) {
					al.remove(parentOutput[i]);
				}
		}
	}   
	
	@Override
	public void remove() {
		super.remove();
		algo.removeUser(); // this algorithm was a user of algo
	}
	
	  @Override
	public String toString() {      
	        // Michael Borcherds 2008-03-30
	        // simplified to allow better Chinese translation
		  if (refPoint==null) {
			  return app.getPlain("IntersectionPointOfAB",input[0].getLabel(),input[1].getLabel());
		  } else {
			  return app.getPlain("IntersectionPointOfABNearC",
					  input[0].getLabel(),input[1].getLabel(),input[2].getLabel());
		  }
	  }

}
