package geogebra3D.euclidian3D;

import geogebra.common.kernel.geos.GeoElement;
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

		int index = 0;
		index = addToDrawableList(geo.getBottom(), index);
		index = addToDrawableList(geo.getTop(), index);
		addToDrawableList(geo.getSide(), index);
			

	}
	
	private int addToDrawableList(GeoElement geo, int index){
		if (geo.isLabelSet()){
			return index;
		}
		
		drawables.addToDrawableList(geo, 0, 0, this);		
		return index + 1;
		
	}


	
	@Override
	protected boolean updateForItSelf() {
		
		//no update here : bottom, top and side will update appart
 	  	return true;
	}
	
	


}
