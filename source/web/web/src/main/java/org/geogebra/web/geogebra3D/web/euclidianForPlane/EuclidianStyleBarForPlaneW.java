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

package org.geogebra.web.geogebra3D.web.euclidianForPlane;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.geogebra3D.euclidianForPlane.EuclidianViewForPlaneCompanion;
import org.geogebra.common.main.OptionType;
import org.geogebra.web.full.euclidian.EuclidianStyleBarW;

/**
 * Style bar for view for plane
 * 
 * @author Mathieu
 */
public class EuclidianStyleBarForPlaneW extends EuclidianStyleBarW {

	/**
	 * @param ev
	 *            view
	 * @param viewID
	 *            view ID
	 */
	public EuclidianStyleBarForPlaneW(EuclidianView ev, int viewID) {
		super(ev, viewID);
	}

	@Override
	protected void setOptionType() {
		optionType = OptionType.EUCLIDIAN_FOR_PLANE;
	}

	@Override
	protected void setEvStandardView() {
		EuclidianViewForPlaneCompanion companion = (EuclidianViewForPlaneCompanion) getView()
				.getCompanion();
		companion.updateCenterAndOrientationRegardingView();
		companion.updateScaleRegardingView();
	}
}
