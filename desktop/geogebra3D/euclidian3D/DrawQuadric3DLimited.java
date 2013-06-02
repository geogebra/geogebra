package geogebra3D.euclidian3D;

import geogebra3D.kernel3D.GeoQuadric3DLimited;

/**
 * Class for drawing quadrics.
 * @author matthieu
 *
 */
public class DrawQuadric3DLimited extends Drawable3DList {

	/**
	 * common constructor
	 * @param view3d 3D view
	 * @param geo limited quadric
	 */
	public DrawQuadric3DLimited(EuclidianView3D view3d, GeoQuadric3DLimited geo) {
		super(view3d, geo);

		drawables.ensureCapacity(3);
		
  		drawables.addToDrawableList(geo.getBottom(), 0, 0, this);
 	  	drawables.addToDrawableList(geo.getTop(),    1, 0, this);
 	  	drawables.addToDrawableList(geo.getSide(),   2, 0, this);

	}


	
	@Override
	protected boolean updateForItSelf() {
		
		//no update here : bottom, top and side will update appart
 	  	return true;
	}
	
	


}
