package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;

import geogebra.kernel.GeoPolygon;
import geogebra.kernel.kernelND.GeoConicND;
import geogebra.kernel.kernelND.GeoCoordSys2D;


public class AlgoIntersectPlaneConic extends AlgoIntersectLineConic3D {
	
	private GeoCoordSys2D plane;


	public AlgoIntersectPlaneConic(Construction cons, String[] labels,
			GeoConicND c, GeoCoordSys2D plane) {	
		this(cons, labels, plane, c);
	}
	
	public AlgoIntersectPlaneConic(Construction cons, String[] labels,
			GeoCoordSys2D plane, GeoConicND c) {	
		this(cons, plane, c);
		GeoElement.setLabels(labels, P);    
	}
	
	public AlgoIntersectPlaneConic(Construction cons, GeoCoordSys2D plane, GeoConicND c) {		
		super(cons, AlgoIntersectCS2D2D.getIntersectPlanePlane(cons, plane.getCoordSys(), c.getCoordSys()),
				c);
		this.plane = plane;
		input = new GeoElement[2];
        input[0] = (GeoElement)plane;
        input[1] = (GeoElement)c;
        input[0].addAlgorithm(this);
        input[1].addAlgorithm(this);
	}

	
    // for AlgoElement
    protected void setInputOutput() {
    	input = new GeoElement[0]; //set input in constructor
        output = P;            
        noUndefinedPointsInAlgebraView();
        setDependencies(); // done by AlgoElement
    }    
    

	@Override
	public String getClassName() {
		return "AlgoIntersectPlaneConic";
	}
	
}

