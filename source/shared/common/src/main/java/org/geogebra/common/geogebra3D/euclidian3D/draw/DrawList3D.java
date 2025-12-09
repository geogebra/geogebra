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

import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer.PickingType;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;

/**
 * Class for drawing GeoList (3D part)
 * 
 * @author matthieu
 *
 */
public class DrawList3D extends DrawComposite3D {

	private GeoList geoList;

	/**
	 * common constructor
	 * 
	 * @param view3D
	 *            view
	 * @param geo
	 *            list
	 */
	public DrawList3D(EuclidianView3D view3D, GeoList geo) {
		super(view3D, geo);
		this.geoList = geo;

		setPickingType(PickingType.POINT_OR_CURVE);
	}

	@Override
	protected int size() {
		return geoList.size();
	}

	@Override
	protected GeoElement getElement(int i) {
		return geoList.get(i);
	}

	@Override
	public boolean isVisible() {
		if (getGeoElement().isLabelSet() && createdByDrawList()) {
			return false;
		}
		return super.isVisible();
	}
}
