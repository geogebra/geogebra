package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterSurface;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoTriangulatedSurface3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoTriangulatedSurface3D.SurfaceMover;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoTriangulatedSurface3D.Triangle;
import org.geogebra.common.geogebra3D.kernel3D.implicit3D.GeoImplicitSurface;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * 
 * @author Shamshad Alam
 *
 */
public class DrawImplicitSurface3D extends Drawable3DSurfaces {

	/**
	 * create a new {@link DrawImplicitSurface3D} object
	 * 
	 * @param a_view3d
	 *            {@link EuclidianView3D}
	 * @param a_geo
	 *            {@link GeoElement}
	 */
	public DrawImplicitSurface3D(EuclidianView3D a_view3d,
			GeoImplicitSurface a_geo) {
		super(a_view3d, a_geo);
	}

	@Override
	void drawGeometryHiding(Renderer renderer) {
		drawSurfaceGeometry(renderer);
	}

	@Override
	protected void drawSurfaceGeometry(Renderer renderer) {
		renderer.getGeometryManager().draw(getSurfaceIndex());
	}

	@Override
	public void drawGeometry(Renderer renderer) {
		renderer.getGeometryManager().draw(getGeometryIndex());
	}

	@Override
	public void drawGeometryHidden(Renderer renderer) {
		// do nothing
	}

	@Override
	public void drawOutline(Renderer renderer) {
		// unknown
	}

	@Override
	protected boolean updateForItSelf() {
		GeoImplicitSurface implicitSurface = (GeoImplicitSurface) getGeoElement();
		EuclidianView3D v3d = getView3D();
		implicitSurface.updateSurface(new double[] { v3d.xmin, v3d.xmax,
				v3d.ymin, v3d.ymax, v3d.getZmin(), v3d.getZmax(),
				v3d.getXscale(), v3d.getYscale(), v3d.getZscale() });
		GeoTriangulatedSurface3D surf = implicitSurface.getSurface3D();
		SurfaceMover surfaceMover = surf.getSurfaceMover();

		if (!surfaceMover.hasNext()) {
			setSurfaceIndex(-1);
			setGeometryIndex(-1);
			return true;
		}

		Manager m = getView3D().getRenderer().getGeometryManager();
		PlotterSurface s = m.getSurface();

		s.start(getReusableSurfaceIndex());

		while (surfaceMover.hasNext()) {
			s.startTriangles();
			Triangle tri = surfaceMover.next();
			s.triangle(tri.v1, tri.v2, tri.v3);
			s.endGeometry();
		}

		setGeometryIndex(s.end());
		setSurfaceIndex(s.end());

		return true;
	}

	@Override
	protected void updateForView() {
		if (getView3D().viewChangedByTranslate()
				|| getView3D().viewChangedByZoom()) {
			setWaitForUpdate();
		}
	}

	// @Override
	// public void addToDrawable3DLists(Drawable3DLists lists) {
	// super.addToDrawable3DLists(lists, DRAW_TYPE_CLIPPED_SURFACES);
	// }
	//
	// @Override
	// public void removeFromDrawable3DLists(Drawable3DLists lists) {
	// super.removeFromDrawable3DLists(lists, DRAW_TYPE_CLIPPED_SURFACES);
	// }

	@Override
	public int getPickOrder() {
		return DRAW_PICK_ORDER_SURFACE;
	}


}
