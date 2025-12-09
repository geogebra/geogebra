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
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DPart;
import org.geogebra.common.kernel.geos.FromMeta;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoQuadricNDConstants;

/**
 * draws a quadric part
 * 
 * @author mathieu
 *
 */
public class DrawQuadric3DPart extends DrawQuadric3D {

	/**
	 * @param view
	 *            view
	 * @param quadric
	 *            quadric part
	 */
	public DrawQuadric3DPart(EuclidianView3D view, GeoQuadric3DPart quadric) {
		super(view, quadric);
	}

	@Override
	protected double[] getMinMax() {

		GeoQuadric3DPart quadric = (GeoQuadric3DPart) getGeoElement();

		return new double[] { quadric.getMinParameter(1),
				quadric.getMaxParameter(1) };
	}

	@Override
	protected void updateForView() {
		GeoQuadric3D quadric = (GeoQuadric3D) getGeoElement();

		switch (quadric.getType()) {
		default:
			// do nothing
			break;
		case GeoQuadricNDConstants.QUADRIC_HYPERBOLIC_CYLINDER:
		case GeoQuadricNDConstants.QUADRIC_PARABOLIC_CYLINDER:
			if (getView3D().viewChangedByZoom()) {
				updateForItSelf();
			}
			break;
		case GeoQuadricNDConstants.QUADRIC_CONE:
			if (getView3D().viewChanged()) {
				updateForItSelf();
			}
			break;
		case GeoQuadricNDConstants.QUADRIC_CYLINDER:
			if (getView3D().viewChangedByZoom()) {
				updateForItSelf();
			}
			break;
		}
	}

	@Override
	public boolean doHighlighting() {

		// if it depends on a limited quadric, look at the meta' highlighting

		if (getGeoElement().getMetasLength() > 0) {
			for (GeoElement meta : ((FromMeta) getGeoElement()).getMetas()) {
				if (meta != null && meta.doHighlighting()) {
					return true;
				}
			}
		}

		return super.doHighlighting();
	}
}
