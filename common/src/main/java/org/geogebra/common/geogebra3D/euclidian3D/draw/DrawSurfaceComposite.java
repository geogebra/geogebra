package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.kernel3D.implicit3D.GeoImplicitSurface;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Surface drawable delegating parts of drawing to DrawImplicitSurface3D and
 * DrawSurface3DElements
 */
public class DrawSurfaceComposite extends DrawComposite3D {

	private GeoImplicitSurface geoSurface;
	private GeoElement surfaceCopy;

	/**
	 * @param view3d
	 *            view
	 * @param geo
	 *            surface
	 */
	public DrawSurfaceComposite(EuclidianView3D view3d,
			GeoImplicitSurface geo) {
		super(view3d, geo);
		this.geoSurface = geo;
	}

	@Override
	protected GeoElement getElement(int i) {
		if (geoSurface.getParametric() == null) {
			return surfaceCopy();
		}
		geoSurface.getParametric()
				.setAllVisualPropertiesExceptEuclidianVisible(geoSurface, true, true);
		geoSurface.getParametric().setLineThickness(1);
		return geoSurface.getParametric();
	}

	private GeoElement surfaceCopy() {
		if (surfaceCopy == null) {
			surfaceCopy = geoSurface.copy();
		} else {
			surfaceCopy.set(geoSurface);
		}
		return surfaceCopy;
	}

	@Override
	protected int size() {
		return 1;
	}

	@Override
	public DrawableND createDrawableND(GeoElement geo) {
		if (geo instanceof GeoImplicitSurface) {
			return new DrawImplicitSurface3D(getView3D(),
					(GeoImplicitSurface) geo);
		}
		return super.createDrawableND(geo);
	}

}
