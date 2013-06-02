package geogebra3D.euclidian3D;

import geogebra.common.euclidian.DrawableND;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;

/**
 * Class for drawing GeoList (3D part)
 * 
 * @author matthieu
 *
 */
public class DrawList3D extends Drawable3DList {
	
	private GeoList geoList;	
	private boolean isVisible;

	/**
	 * common constructor
	 * @param view3D
	 * @param geo
	 */
	public DrawList3D(EuclidianView3D view3D, GeoList geo) {
		super(view3D, geo);
		this.geoList = geo;
	}

	@Override
	protected boolean updateForItSelf() {
		
		
		
		isVisible = geoList.isEuclidianVisible();
    	if (!isVisible) return true;    	
    	
    	// go through list elements and create and/or update drawables
    	int size = geoList.size();
    	drawables.ensureCapacity(size);
    	int oldDrawableSize = drawables.size();
    	
    	int drawablePos = 0;
    	for (int i=0; i < size; i++) {    		
    		GeoElement listElement = geoList.get(i);
    		
    		//Application.debug(listElement.toString()+", "+listElement.hasDrawable3D());
    		
    		//if (!listElement.isDrawable())  continue;
    		
    		// only new 3D elements are drawn 
    		if (!listElement.hasDrawable3D())
    			continue;
    		
    		// add drawable for listElement
    		if (drawables.addToDrawableList(listElement, drawablePos, oldDrawableSize, this))
    			drawablePos++;
    		
    	}    
    	
    	// remove end of list
    	for (int i=drawables.size()-1; i >= drawablePos; i--) {      		 
    		//view3D.remove(drawables.get(i).getGeoElement());
    		DrawableND d = drawables.remove(i);
    		if (d.createdByDrawList()) //sets the drawable to not visible
    			d.setCreatedByDrawListVisible(false);
    	}
    	
    	return true;
	}

}
