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

package org.geogebra.web.full.main;

import org.gwtproject.dom.client.Element;

/**
 * Null Object implementation of header resizer.
 */
public final class NullHeaderResizer implements HeaderResizer {
	private static NullHeaderResizer INSTANCE = null;

	/**
	 *
	 * @return the NullHeaderResizer as a singleton.
	 */
	public static NullHeaderResizer get() {
		if (INSTANCE == null) {
			INSTANCE = new NullHeaderResizer();
		}

		return INSTANCE;
	}

	private NullHeaderResizer() {
		// singleton constructor
	}

	@Override
	public void resizeHeader() {
		// nothing to do.
	}

	@Override
	public int getSmallScreenHeight() {
		return 0;
	}

	@Override
	public void reset(Element header) {
		// nothing to do
	}

	@Override
	public int getHeaderHeight() {
		return 0;
	}
}
