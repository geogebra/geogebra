package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;

/**
 * list of drawables stored by lists
 * 
 * @author mathieu
 *
 */
public class Drawable3DListsForDrawList3D extends Drawable3DLists {

	private EuclidianView3D view3D;

	/**
	 * constructor
	 * 
	 * @param view3D
	 *            3D view
	 * 
	 */
	public Drawable3DListsForDrawList3D(EuclidianView3D view3D) {
		super();
		this.view3D = view3D;
	}

	@Override
	public void add(Drawable3D drawable) {
		super.add(drawable);
		if (!(drawable instanceof DrawList3D)) {
			view3D.addOneGeoToPick();
		}
	}

	@Override
	protected void remove(Drawable3D drawable) {

		super.remove(drawable);
		if (drawable != null && !(drawable instanceof DrawList3D)) {
			view3D.removeOneGeoToPick();
		}

	}

	


}
