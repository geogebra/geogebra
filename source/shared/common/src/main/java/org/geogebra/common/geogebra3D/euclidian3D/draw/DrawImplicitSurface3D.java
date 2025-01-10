package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.Hitting;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Manager;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.ManagerShaders.TypeElement;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.PlotterSurface;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoTriangulatedSurface3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoTriangulatedSurface3D.SurfaceMover;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoTriangulatedSurface3D.Triangle;
import org.geogebra.common.geogebra3D.kernel3D.implicit3D.GeoImplicitSurface;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.util.MyMath;

/**
 * 
 * @author Shamshad Alam
 *
 */
public class DrawImplicitSurface3D extends Drawable3DSurfaces {
	private final static double[] hitTestParams = new double[] { 0.5, 0.25,
			0.75, 0.125, 0.375, 0.625, 0.875 };

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
		drawSurfaceGeometry(renderer);
	}

	@Override
	public void drawOutline(Renderer renderer) {
		drawGeometry(renderer);
	}

	@Override
	protected boolean updateForItSelf() {
		GeoImplicitSurface geo = (GeoImplicitSurface) getGeoElement();
		EuclidianView3D v3d = getView3D();
		geo.updateSurface(new double[] { v3d.getXmin(), v3d.getXmax(),
				v3d.getYmin(), v3d.getYmax(), v3d.getZmin(), v3d.getZmax(),
				v3d.getXscale(), v3d.getYscale(), v3d.getZscale() });
		GeoTriangulatedSurface3D surf = geo.getSurface3D();
		SurfaceMover surfaceMover = surf.getSurfaceMover();

		if (!surfaceMover.hasNext()) {
			setSurfaceIndex(-1);
			setGeometryIndex(-1);
			return true;
		}

		Manager m = getView3D().getRenderer().getGeometryManager();
		PlotterSurface s = m.getSurface();
		setPackSurface(true);
		s.start(getReusableSurfaceIndex());

		s.startTriangles();
		while (surfaceMover.hasNext()) {
			Triangle tri = surfaceMover.next();
			s.triangle(tri.v1, tri.v2, tri.v3, tri.n1, tri.n2, tri.n3);
        }
        m.endGeometry(surfaceMover.getTrianglesCount(),
                TypeElement.TRIANGLES);
        setSurfaceIndex(s.end());
        endPacking();
		return true;
	}

	@Override
	protected void updateForView() {
		if (getView3D().viewChangedByTranslate()
				|| getView3D().viewChangedByZoom()) {
			setWaitForUpdate();
		}
	}

	@Override
	public int getPickOrder() {
		return DRAW_PICK_ORDER_SURFACE;
	}

	@Override
	public boolean hit(Hitting hitting) {

		if (waitForReset) { // prevent NPE
			return false;
		}

		if (getGeoElement()
				.getAlphaValue() < EuclidianController.MIN_VISIBLE_ALPHA_VALUE) {
			return false;
		}

		GeoImplicitSurface gs = (GeoImplicitSurface) this.getGeoElement();
		hitting.calculateClippedValues();
		if (Double.isNaN(hitting.x0)) { // hitting doesn't intersect
										// clipping box
			return false;
		}
		double v0 = gs.evaluateAt(hitting.x0, hitting.y0, hitting.z0);
		double v1 = gs.evaluateAt(hitting.x1, hitting.y1, hitting.z1);
		if (MyMath.changedSign(v0, v1)) {
			return true;
		}
		for (int i = 0; i < hitTestParams.length; i++) {
			double p = hitTestParams[i];
			double q = 1 - p;
			v1 = gs.evaluateAt(hitting.x1 * p + hitting.x0 * q,
					hitting.y1 * p + hitting.y0 * q,
					hitting.z1 * p + hitting.z0 * q);
			if (MyMath.changedSign(v0, v1)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void updateForViewVisible() {
		updateGeometriesVisibility();
		updateForView();
	}

    @Override
    public void setWaitForUpdateVisualStyle(GProperty prop) {
        super.setWaitForUpdateVisualStyle(prop);
        if (prop == GProperty.LINE_STYLE) {
            // also update for line width (e.g when translated)
            setWaitForUpdate();
        } else if (prop == GProperty.COLOR) {
            setWaitForUpdateColor();
        } else if (prop == GProperty.HIGHLIGHT) {
            setWaitForUpdateColor();
        } else if (prop == GProperty.VISIBLE) {
            setWaitForUpdateVisibility();
        }
    }

	@Override
	public void addToDrawable3DLists(Drawable3DLists lists) {
		addToDrawable3DLists(lists, DRAW_TYPE_CLIPPED_SURFACES);
	}

	@Override
	public void removeFromDrawable3DLists(Drawable3DLists lists) {
		removeFromDrawable3DLists(lists, DRAW_TYPE_CLIPPED_SURFACES);
	}

}
