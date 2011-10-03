package geogebra3D.euclidian3D;

import geogebra.euclidian.DrawListArray;
import geogebra.euclidian.DrawableND;
import geogebra.euclidian.EuclidianViewInterface;
import geogebra.kernel.GeoElement;

/**
 * Class for storing 3D drawables includes in a DrawList
 * 
 * @author matthieu
 *
 */
public class DrawList3DArray extends DrawListArray {

	private static final long serialVersionUID = 1L;

	/**
	 * common constructor
	 * @param view
	 */
	public DrawList3DArray(EuclidianViewInterface view) {
		super(view);
	}
	

    protected void update(DrawableND d){
		d.setWaitForUpdate();
		if (d.createdByDrawList())
			d.setCreatedByDrawListVisible(true);
    }
	
	
    protected DrawableND createDrawableND(GeoElement listElement){
    	//Application.debug(listElement.toString());
    	DrawableND d = super.createDrawableND(listElement);
    	((EuclidianView3D) view).addToDrawable3DLists((Drawable3D) d);
    	return d;  
    }

}
