package geogebra.common.geogebra3D.euclidian3D;

import geogebra.common.awt.GPoint;
import geogebra.common.kernel.Matrix.Coords;

/**
 * class for rays, spheres, etc. that can hit 3D objects in 3D view
 * @author Proprietaire
 *
 */
public class Hitting {
	
	/**
	 *  origin of the ray
	 */
	public Coords origin;

	/**
	 *  direction of the ray
	 */
	public Coords direction;
	
	private EuclidianView3D view;
	
	/**
	 * current threshold
	 */
	private int threshold;

	/**
	 * constructor
	 * @param view 3D view
	 */
	public Hitting(EuclidianView3D view){
		this.view = view;
	}
	
	/**
	 * set the hits
	 * @param mouseLoc mouse location
	 * @param threshold threshold
	 */
	public void setHits(GPoint mouseLoc, int threshold){
		
		Hits3D hits = view.getHits3D();
		hits.init();

		origin = view.getPickPoint(mouseLoc); 
		if (view.getProjection() == EuclidianView3D.PROJECTION_PERSPECTIVE 
				|| view.getProjection() ==  EuclidianView3D.PROJECTION_GLASSES) {
			origin = view.getRenderer().getPerspEye().copyVector();
		}
		view.toSceneCoords3D(origin); 
		direction = view.getViewDirection();
		
		this.threshold = threshold;

		for (int i = 0; i < 3; i++) {
			view.getAxisDrawable(i).hitIfVisibleAndPickable(this, hits); 
		}
		view.getDrawList3D().hit(this, hits);

		hits.sort();
	}
	
	/**
	 * 
	 * @param p point coords
	 * @return true if the point is inside the clipping box (if used)
	 */
	final public boolean isInsideClipping(Coords p){
		if (view.useClippingCube()){
			return view.isInside(p);
		}
		
		return true;
		
	}
	
	/**
	 * 
	 * @return current threshold
	 */
	public int getThreshold(){
		return threshold;
	}
}
