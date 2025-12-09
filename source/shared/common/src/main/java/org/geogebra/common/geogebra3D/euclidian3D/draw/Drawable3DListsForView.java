/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.geogebra3D.euclidian3D.draw;

import java.util.List;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * list of drawables stored by the view (will also draw the view axes, plane,
 * etc.)
 * 
 * @author mathieu
 *
 */
public class Drawable3DListsForView extends Drawable3DLists {

	private boolean waitForResetManagerBuffers = false;

	/**
	 * constructor
	 * 
	 * @param view3D
	 *            3D view
	 */
	public Drawable3DListsForView(EuclidianView3D view3D) {
		super(view3D);
	}

	@Override
	protected void remove(Drawable3D drawable) {

		super.remove(drawable);

		// TODO fix it
		if (drawable != null) {
			if (drawable.getGeoElement() != null) {
				if (drawable.shouldBePacked() || drawable.getGeoElement().isPickable()) {
					drawable.removeFromGL();
				}
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
	 * @param dontExtend
	 *            set to true if clipped curves/surfaces should not be larger
	 *            than the view itself; and when point radius should extend
	 */
	public void enlargeBounds(Coords min, Coords max,
			boolean dontExtend) {
		for (Drawable3DList l : lists) {
			for (Drawable3D d : l) {
				if (d != null && d.isVisible()) {
					d.enlargeBounds(min, max, dontExtend);
				}
			}
		}
	}

	/**
	 * update (reset) manager buffers if needed
	 * 
	 * @param renderer
	 *            openGL renderer
	 */
	public void updateManagerBuffers(Renderer renderer) {
		renderer.getGeometryManager().update(waitForResetManagerBuffers);
		if (waitForResetManagerBuffers) {
			waitForResetManagerBuffers = false;
		}
	}

	/**
	 * says all buffers have to be reset
	 * 
	 */
	public void setWaitForResetManagerBuffers() {
		waitForResetManagerBuffers = true;
	}

	/**
	 * 
	 * @return list of drawables for points
	 */
	public List<Drawable3D> getDrawPoints() {
		return getList(Drawable3D.DRAW_TYPE_POINTS);
	}

}
