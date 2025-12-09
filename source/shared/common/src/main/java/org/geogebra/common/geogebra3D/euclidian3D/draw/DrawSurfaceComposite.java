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
