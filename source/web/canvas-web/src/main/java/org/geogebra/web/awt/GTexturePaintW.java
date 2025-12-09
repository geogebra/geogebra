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

package org.geogebra.web.awt;

import org.geogebra.common.awt.GPaint;
import org.geogebra.common.awt.GRectangle;

public class GTexturePaintW implements GPaint {

	private final GRectangle anchor;
	// GRectangleW anchor = null;
	private GBufferedImageW bi;

	/**
	 * @param image
	 *            image
	 */
	public GTexturePaintW(GBufferedImageW image, GRectangle rect) {
		this.bi = image;
		this.anchor = rect;
	}

	/**
	 * @return wrapped image
	 */
	public GBufferedImageW getImg() {
		return bi;
	}

	public GRectangle getAnchor() {
		return anchor;
	}

}
