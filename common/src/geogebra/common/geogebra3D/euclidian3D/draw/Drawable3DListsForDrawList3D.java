package geogebra.common.geogebra3D.euclidian3D.draw;

import geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;

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

	
	/**
	 * draw texts
	 * 
	 * @param renderer
	 *            opengl context
	 */
	public void drawTexts(Renderer renderer){

		// texts
		for (Drawable3D d : lists[Drawable3D.DRAW_TYPE_TEXTS]) {
			d.drawLabel(renderer);
		}

		// lists
		for (Drawable3D d : lists[Drawable3D.DRAW_TYPE_LISTS]) {
			((DrawList3D) d).getDrawable3DLists().drawLabel(renderer);
		}

				
	}
}
