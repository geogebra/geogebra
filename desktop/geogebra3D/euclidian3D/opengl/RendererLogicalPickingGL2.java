package geogebra3D.euclidian3D.opengl;

import geogebra.common.awt.GPoint;
import geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import geogebra.common.geogebra3D.euclidian3D.EuclidianController3D.IntersectionCurve;
import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.geogebra3D.euclidian3D.Hitting;
import geogebra.common.geogebra3D.euclidian3D.HittingSphere;
import geogebra.common.geogebra3D.euclidian3D.draw.Drawable3D;
import geogebra.common.kernel.geos.GeoElement;

import java.util.ArrayList;

public class RendererLogicalPickingGL2 extends RendererGL2 {

	public RendererLogicalPickingGL2(EuclidianView3D view, boolean useCanvas) {
		
		super(view, useCanvas);
		
		if (((EuclidianController3D) view3D.getEuclidianController()).useInputDepthForHitting()){
			hitting = new HittingSphere(view3D);
		}else{
			hitting = new Hitting(view3D);
		}
	}
	
	private Hitting hitting;

    @Override
	public Hitting getHitting(){
    	return hitting;
    }


    @Override
    public void setHits(GPoint mouseLoc, int threshold){

    	if (mouseLoc == null){
    		return;
    	}

    	hitting.setHits(mouseLoc, threshold);

    }
    


    @Override
    public GeoElement getLabelHit(GPoint mouseLoc){
    	if (mouseLoc == null){
    		return null;
    	}

//    	return hitting.getLabelHit(mouseLoc);
    	
    	return null;
    }

    @Override
    public void pickIntersectionCurves() {

    	ArrayList<IntersectionCurve> curves = ((EuclidianController3D) view3D
    			.getEuclidianController()).getIntersectionCurves();

    	// picking objects
    	for (IntersectionCurve intersectionCurve : curves) {			
    		Drawable3D d = intersectionCurve.drawable;
    		d.updateForHitting(); // we may need an update
    		if(!d.hit(hitting) || d.getPickingType() != PickingType.POINT_OR_CURVE){ // we assume that hitting infos are updated from last mouse move
    			d.setZPick(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
    		}

    	}

    }
    
    @Override
	protected void doPick() {
    	// no need here
    }
    
    @Override
	public boolean useLogicalPicking(){
		return true;
	}




}
