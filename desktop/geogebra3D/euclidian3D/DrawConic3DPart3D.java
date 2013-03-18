package geogebra3D.euclidian3D;

import geogebra.common.kernel.Matrix.Coords;
import geogebra3D.euclidian3D.opengl.PlotterBrush;
import geogebra3D.euclidian3D.opengl.PlotterSurface;
import geogebra3D.kernel3D.GeoConic3DPart;

public class DrawConic3DPart3D extends DrawConic3D {

	public DrawConic3DPart3D(EuclidianView3D view3d, GeoConic3DPart conic) {
		super(view3d, conic);
	}
	
	
	
	
	
	protected double getStart(int i){
		return ((GeoConic3DPart) getGeoElement()).getParameterStart(i);
	}
	
	protected double getExtent(int i){
		return ((GeoConic3DPart) getGeoElement()).getParameterExtent(i);
	}
	

	@Override
	protected void updateCircle(PlotterBrush brush){
		
		updateEllipse(brush);
	}
	
	@Override
	protected void updateEllipse(PlotterBrush brush){
		
		
		double start0 = getStart(0);
		double extent0 = getExtent(0);
		double start1 = getStart(1);
		double extent1 = getExtent(1);
		
		
		if(!Double.isNaN(start0)){ // there is at least one hole
			
			GeoConic3DPart conic = (GeoConic3DPart) getGeoElement();
			Coords m = conic.getMidpoint3D();
			Coords ev0 = conic.getEigenvec3D(0); 
			Coords ev1 = conic.getEigenvec3D(1);
			double r0 = conic.getHalfAxis(0);
			double r1 = conic.getHalfAxis(1);
			
			brush.arcEllipse(m, ev0, ev1, r0, r1,start0,extent0);
			
			
			if(!Double.isNaN(start1)){ // there is two holes
				brush.setAffineTexture(0.5f,  0.25f);
				brush.segment(ellipsePoint(m, ev0, ev1, r0, r1, start0+extent0),
						ellipsePoint(m, ev0, ev1, r0, r1, start1));
				brush.arcEllipse(m, ev0, ev1, r0, r1,start1,extent1);
				brush.setAffineTexture(0.5f,  0.25f);
				brush.segment(ellipsePoint(m, ev0, ev1, r0, r1, start1+extent1),
						ellipsePoint(m, ev0, ev1, r0, r1, start0));
			}else{
				brush.setAffineTexture(0.5f,  0.25f);
				brush.segment(ellipsePoint(m, ev0, ev1, r0, r1, start0+extent0),
						ellipsePoint(m, ev0, ev1, r0, r1, start0));
			}
			
		}else{ // no hole
			super.updateEllipse(brush);
		}
		
		//updateSectorSegments(brush, conic.getConicPartType(), m, ev0, ev1, r0, r1, start0, start0+extent0);
	}
	
	private Coords ellipsePoint(Coords m, Coords ev0, Coords ev1, double r0, double r1, double parameter){
		return m.add(ev0.mul(r0*Math.cos(parameter))).add(ev1.mul(r1*Math.sin(parameter)));
	}

	
	@Override
	protected void updateEllipse(PlotterSurface surface){
		
		double start0 = getStart(0);
		double extent0 = getExtent(0);
		double start1 = getStart(1);
		double extent1 = getExtent(1);
		
		
		if(!Double.isNaN(start0)){ // there is at least one hole
			
			GeoConic3DPart conic = (GeoConic3DPart) getGeoElement();
			Coords m = conic.getMidpoint3D();
			Coords ev0 = conic.getEigenvec3D(0); 
			Coords ev1 = conic.getEigenvec3D(1);
			double r0 = conic.getHalfAxis(0);
			double r1 = conic.getHalfAxis(1);
			
			surface.ellipsePart(m, ev0, ev1, r0, r1, start0, extent0, false);

			
			if(!Double.isNaN(start1)){ // there is two holes
				surface.ellipsePart(m, ev0, ev1, r0, r1, start1, extent1, false);
				surface.drawQuad(
						ellipsePoint(m, ev0, ev1, r0, r1, start0), 
						ellipsePoint(m, ev0, ev1, r0, r1, start0+extent0), 
						ellipsePoint(m, ev0, ev1, r0, r1, start1), 
						ellipsePoint(m, ev0, ev1, r0, r1, start1+extent1) 
						);
			}
			
		}else{ // no hole
			super.updateEllipse(surface);
		}

	}

}
