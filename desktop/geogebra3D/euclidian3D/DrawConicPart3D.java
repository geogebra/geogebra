package geogebra3D.euclidian3D;

import geogebra.common.kernel.geos.GeoConicPart;
import geogebra.common.kernel.kernelND.GeoConicNDConstants;
import geogebra3D.euclidian3D.opengl.PlotterBrush;

public class DrawConicPart3D extends DrawConic3D {

	public DrawConicPart3D(EuclidianView3D view3d, GeoConicPart conic) {
		super(view3d, conic);
	}
	
	
	
	
	
	@Override
	protected double getStart(){
		//return 0;
		return ((GeoConicPart) getGeoElement()).getParameterStart();
	}
	
	@Override
	protected double getExtent(){
		return ((GeoConicPart) getGeoElement()).getParameterExtent();
	}
	

	@Override
	protected void updateCircle(PlotterBrush brush){
		
		double start = getStart();
		double extent = getExtent();
		brush.arc(m, ev1, ev2, e1,start,extent);
		
		updateSectorSegments(brush, start, start+extent);
	}
	
	@Override
	protected void updateEllipse(PlotterBrush brush){
		

		double start = getStart();
		double extent = getExtent();
		brush.arcEllipse(m, ev1, ev2, e1, e2, start, extent);
		
		updateSectorSegments(brush, start, start+extent);
	}

	private void updateSectorSegments(PlotterBrush brush, double start, double end){
			
		//if sector draws segments
		if (((GeoConicPart) getGeoElement()).getConicPartType()==GeoConicNDConstants.CONIC_PART_SECTOR){
			brush.setAffineTexture(0.5f,  0.25f);
			brush.segment(m, m.add(ev1.mul(e1*Math.cos(start))).add(ev2.mul(e2*Math.sin(start))));
			brush.segment(m, m.add(ev1.mul(e1*Math.cos(end))).add(ev2.mul(e2*Math.sin(end))));
		}
	}

}
