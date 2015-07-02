package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterSurface;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterSurfaceElements;
import org.geogebra.common.kernel.Matrix.Coords3;
import org.geogebra.common.kernel.kernelND.SurfaceEvaluable;

/**
 * Draw 3D surface with GL drawElements()
 * 
 * @author mathieu
 *
 */
public class DrawSurface3DElements extends DrawSurface3D {


	/**
	 * constructor
	 * 
	 * @param a_view3d
	 *            view
	 * @param surface
	 *            surface
	 */
	public DrawSurface3DElements(EuclidianView3D a_view3d,
			SurfaceEvaluable surface) {
		super(a_view3d, surface);
	}

	@Override
	protected void drawTriangle(PlotterSurface surface,
			CornerAndCenter cc, Corner c1, Corner c2) {

		if (!checkIdsAreShort(cornerListIndex + cc.id, c1.id, c2.id)) {
			return;
		}

		((PlotterSurfaceElements) surface).drawIndex(cornerListIndex + cc.id);
		((PlotterSurfaceElements) surface).drawIndex(c2.id);
		((PlotterSurfaceElements) surface).drawIndex(c1.id);
	}

	private int lastIndex;

	@Override
	protected void drawCornersAndCenters(PlotterSurface surface) {
		for (int i = 0; i < cornerListIndex; i++) {
			Corner c = cornerList[i];
			surface.normalDirect(c.normal);
			surface.vertexDirect(c.p);
		}
		for (int i = 0; i < drawListIndex; i++) {
			CornerAndCenter cc = drawList[i];
			surface.normalDirect(cc.centerNormal);
			surface.vertexDirect(cc.center);
		}

		lastIndex = cornerListIndex + drawListIndex;

	}


	@Override
	protected void drawTriangle(PlotterSurface surface, Coords3 p0, Coords3 n0,
			Corner c1, Corner c2) {

		if (!checkIdsAreShort(lastIndex, c1.id, c2.id)) {
			return;
		}


		// add normal and vertex, and create new index
		draw(surface, p0, n0);
		draw(surface, c2);
		draw(surface, c1);


	}

	final private void draw(PlotterSurface surface, Coords3 p0, Coords3 n0) {
		// add normal and vertex
		surface.normalDirect(n0);
		surface.vertexDirect(p0);

		// set indices for new triangle
		((PlotterSurfaceElements) surface).drawIndex(lastIndex);
		lastIndex++;
	}

	final private void draw(PlotterSurface surface, Corner c) {

		if (c.id < 0) { // needs new id
			draw(surface, c.p, c.normal);
		} else {
			((PlotterSurfaceElements) surface).drawIndex(c.id);
		}

	}

	final private static boolean checkIdsAreShort(int id1, int id2, int id3) {
		if (checkIdIsNotShort(id1)) {
			return false;
		}

		if (checkIdIsNotShort(id2)) {
			return false;
		}

		if (checkIdIsNotShort(id3)) {
			return false;
		}

		return true;

	}

	final private static boolean checkIdIsNotShort(int id) {

		if (id > Short.MAX_VALUE) {
			return true;
		}

		return false;
	}

}
