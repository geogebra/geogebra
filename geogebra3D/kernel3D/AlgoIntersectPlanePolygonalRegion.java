package geogebra3D.kernel3D;

import java.awt.Color;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;

import geogebra.kernel.GeoPolygon;
import geogebra.kernel.kernelND.GeoSegmentND;


public class AlgoIntersectPlanePolygonalRegion extends AlgoIntersectLinePolygonalRegion3D {
	
	private GeoPlane3D plane; //input
	
	public AlgoIntersectPlanePolygonalRegion(Construction c, String[] labels,
			 GeoPolygon p, GeoPlane3D plane) {	
		this(c, labels, plane, p);
	}
	
	public AlgoIntersectPlanePolygonalRegion(Construction c, String[] labels,
			GeoPlane3D plane, GeoPolygon p) {		
		super(c, labels, AlgoIntersectCS2D2D.getIntersectPlanePlane(plane, p), p);
		
	    //try this
		//TODO: better place to create input
		this.plane = plane;
			input = new GeoElement[2];
	        
	        input[0] = (GeoElement)plane;
	        input[1] = (GeoElement)p;
	        input[0].addAlgorithm(this);
	        input[1].addAlgorithm(this);
	}

	@Override
	public String getClassName() {
		return "AlgoIntersectPlanePolygonalRegion";
	}
	
	//try this
	protected void setInputOutput() {
		input = new GeoElement[0]; //set in constructor of this algo
		setDependencies();
	}
	
	public String toString() {
        return app.getPlain("IntersectionPathsOfAB",((GeoElement) plane).getLabel(),p.getLabel());
    }
	

	protected void setStyle(GeoSegmentND segment) {
		//TODO use default intersection style for lines
		segment.setObjColor(Color.red);
	}
	
	protected void calcLineInPlaneOfPolygon() {
		
    	lineInPlaneOfPolygon = true;
		
	}
}

