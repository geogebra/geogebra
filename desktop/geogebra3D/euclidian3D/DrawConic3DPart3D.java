package geogebra3D.euclidian3D;

import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.kernelND.GeoConicNDConstants;
import geogebra3D.euclidian3D.opengl.PlotterBrush;
import geogebra3D.kernel3D.GeoConic3DPart;

public class DrawConic3DPart3D extends DrawConic3D {

	public DrawConic3DPart3D(EuclidianView3D view3d, GeoConic3DPart conic) {
		super(view3d, conic);
	}
	
	
	
	
	
	protected double getStart(){
		//return 0;
		return ((GeoConic3DPart) getGeoElement()).getParameterStart(0);
	}
	
	protected double getExtent(){
		return ((GeoConic3DPart) getGeoElement()).getParameterExtent(0);
	}
	

	protected void updateCircle(PlotterBrush brush){
		
		GeoConic3DPart conic = (GeoConic3DPart) getGeoElement();
		Coords m = conic.getMidpoint3D();
		Coords ev0 = conic.getEigenvec3D(0); 
		Coords ev1 = conic.getEigenvec3D(1);
		double radius = conic.getHalfAxis(0);
		double start = getStart();
		double extent = getExtent();
		brush.arc(m, ev0, ev1, radius,start,extent);
		
		updateSectorSegments(brush, conic.getConicPartType(), m, ev0, ev1, radius, radius, start, start+extent);
	}
	
	protected void updateEllipse(PlotterBrush brush){
		
		GeoConic3DPart conic = (GeoConic3DPart) getGeoElement();
		Coords m = conic.getMidpoint3D();
		Coords ev0 = conic.getEigenvec3D(0); 
		Coords ev1 = conic.getEigenvec3D(1);
		double r0 = conic.getHalfAxis(0);
		double r1 = conic.getHalfAxis(1);
		double start = getStart();
		double extent = getExtent();
		brush.arcEllipse(m, ev0, ev1, r0, r1,start,extent);
		
		updateSectorSegments(brush, conic.getConicPartType(), m, ev0, ev1, r0, r1, start, start+extent);
	}

	private void updateSectorSegments(PlotterBrush brush, int type, Coords m, Coords ev0, Coords ev1, double r0, double r1, double start, double end){
			
		//if sector draws segments
		if (type==GeoConicNDConstants.CONIC_PART_SECTOR){
			brush.setAffineTexture(0.5f,  0.25f);
			brush.segment(m, m.add(ev0.mul(r0*Math.cos(start))).add(ev1.mul(r1*Math.sin(start))));
			brush.segment(m, m.add(ev0.mul(r0*Math.cos(end))).add(ev1.mul(r1*Math.sin(end))));
		}
	}

}
