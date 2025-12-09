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

package org.geogebra.common.euclidian;

import org.geogebra.common.awt.MyImage;

public class MeasurementToolBoundingBox extends MediaBoundingBox {

	/**
	 * Ruler bounding box
	 * @param rotationImage - rotation icon
	 */
	public MeasurementToolBoundingBox(MyImage rotationImage) {
		super(rotationImage);
	}

	@Override
	protected void updateHandlers() {
		double width = geo.getWidth();
		double height = geo.getHeight();
		setHandlerTransformed(0, 0, 0);
		setHandlerTransformed(1, 0, height);
		setHandlerTransformed(2, width, height);
		setHandlerTransformed(3, width, 0);
		setHandlerTransformed(8, width / 2,
				height + BoundingBox.ROTATION_HANDLER_DISTANCE);
	}
}
