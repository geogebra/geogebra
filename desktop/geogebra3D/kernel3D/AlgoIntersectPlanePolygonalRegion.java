package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPolygon;
import geogebra.common.kernel.kernelND.GeoSegmentND;

import java.awt.Color;



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
	        
	        input[0] = plane;
	        input[1] = p;
	        input[0].addAlgorithm(this);
	        input[1].addAlgorithm(this);
	}

	@Override
	public Commands getClassName() {
		return Commands.IntersectionPaths;
	}
	
	//try this
	@Override
	protected void setInputOutput() {
		input = new GeoElement[0]; //set in constructor of this algo
		setDependencies();
	}
	
	@Override
	public String toString(StringTemplate tpl) {
        return app.getPlain("IntersectionOfAandB",((GeoElement) plane).getLabel(tpl),p.getLabel(tpl));
    }
	

	@Override
	protected void setStyle(GeoSegmentND segment) {
		//TODO use default intersection style for lines
		segment.setObjColor(new geogebra.awt.GColorD(Color.red));
	}
	
	@Override
	protected void calcLineInPlaneOfPolygon() {
		
    	lineInPlaneOfPolygon = true;
		
	}
}

