package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.kernel.Matrix.Coords;

/**
 * list of drawables stored by the view (will also draw the view axes, plane,
 * etc.)
 * 
 * @author mathieu
 *
 */
public class Drawable3DListsForView extends Drawable3DLists {

	private EuclidianView3D view3D;

	/**
	 * constructor
	 * 
	 * @param view3D
	 *            3D view
	 */
	public Drawable3DListsForView(EuclidianView3D view3D) {
		super();

		this.view3D = view3D;
	}


	@Override
	protected void remove(Drawable3D drawable) {

		super.remove(drawable);

		// TODO fix it
		if (drawable != null) {
			if (drawable.getGeoElement() != null
					&& drawable.getGeoElement().isPickable()) {
				drawable.removeFromGL();
			}
		}
	}


	@Override
	public void drawHiddenTextured(Renderer renderer) {

		super.drawHiddenTextured(renderer);
		view3D.drawHidden(renderer);

	}

	@Override
	public void drawTransp(Renderer renderer) {

		super.drawTransp(renderer);
		view3D.drawTransp(renderer);

	}

	@Override
	public void draw(Renderer renderer) {

		super.draw(renderer);
		view3D.draw(renderer);

	}

	@Override
	public void drawLabel(Renderer renderer) {

		super.drawLabel(renderer);
		view3D.drawLabel(renderer);

	}

	@Override
	public void drawSurfacesForHiding(Renderer renderer) {

		super.drawSurfacesForHiding(renderer);
		view3D.drawHiding(renderer);

	}


	/**
	 * enlarge min and max values to enclose all objects
	 * 
	 * @param min
	 *            (x,y,z) min
	 * @param max
	 *            (x,y,z) max
	 */
	public void enlargeBounds(Coords min, Coords max) {
		for (Drawable3DList l : lists) {
			for (Drawable3D d : l) {
				if (d.isVisible()) {
					d.enlargeBounds(min, max);
				}
			}
		}
	}

}
