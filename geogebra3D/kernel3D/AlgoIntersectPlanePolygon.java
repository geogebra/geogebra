package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;

import geogebra.kernel.GeoPolygon;



public class AlgoIntersectPlanePolygon extends AlgoIntersectLinePolygon3D {
	
	private GeoPlane3D plane;

	public AlgoIntersectPlanePolygon(Construction c, String[] labels,
			 GeoPolygon p, GeoPlane3D plane) {	
		this(c, labels, plane, p);
	}
	
	public AlgoIntersectPlanePolygon(Construction c, String[] labels,
			GeoPlane3D plane, GeoPolygon p) {		
		super(c, labels, AlgoIntersectCS2D2D.getIntersectPlanePlane(plane, p), p);
		
		this.plane = plane;
		input = new GeoElement[2];
        
        input[0] = (GeoElement)plane;
        input[1] = (GeoElement)p;
        input[0].addAlgorithm(this);
        input[1].addAlgorithm(this);
	}

	@Override
	public String getClassName() {
		return "AlgoIntersectPlanePolygon";
	}
	
	protected void setInputOutput() {
		input = new GeoElement[0]; //set in constructor of this algo
		setDependencies();
	}

	
}

