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
	 * constructor
	 * @param view 3D view
	 */
	public Hitting(EuclidianView3D view){
		this.view = view;
	}
	
	/**
	 * set the hitting ray
	 * @param origin origin
	 * @param direction direction
	 */
	public void set(Coords origin, Coords direction){
		this.origin = origin;
		this.direction = direction;
	}
	
	/**
	 * set the hits
	 * @param mouseLoc mouse location
	 */
	public void setHits(GPoint mouseLoc){
		
		Hits3D hits = view.getHits3D();
		hits.init();

		origin = view.getPickPoint(mouseLoc); 
		view.toSceneCoords3D(origin); 
		direction = view.getViewDirection();

		for (int i = 0; i < 3; i++) {
			view.getAxisDrawable(i).hitIfVisibleAndPickable(this, hits); 
		}
		view.getDrawList3D().hit(this, hits);

		hits.sort();
	}
}
