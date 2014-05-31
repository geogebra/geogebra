package geogebra.common.geogebra3D.euclidian3D.draw;

import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;


/**
 * list of drawables stored by lists 
 * @author mathieu
 *
 */
public class Drawable3DListsForDrawList3D extends Drawable3DLists{

	private EuclidianView3D view3D;
	
	/**
	 * constructor
	 * @param view3D 3D view
	 * 
	 */
	public Drawable3DListsForDrawList3D(EuclidianView3D view3D) {
		super();
		this.view3D = view3D;
	}
	
	/**
	 * removes geos to pick count
	 */
	public void removeGeosToPick(){
		
		int n = size() - lists[Drawable3D.DRAW_TYPE_LISTS].size();
		
		view3D.getRenderer().removeGeosToPick(n);
		
		for (Drawable3D d : lists[Drawable3D.DRAW_TYPE_LISTS]){
			((DrawList3D) d).getDrawable3DLists().removeGeosToPick();
		}
	}

}
