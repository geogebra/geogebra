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

package org.geogebra.desktop.awt;

import java.awt.Dimension;

import org.geogebra.common.awt.GDimension;

public class GDimensionD extends GDimension {
	private Dimension impl;

	public GDimensionD(Dimension dim) {
		impl = dim;
	}

	public GDimensionD(int a, int b) {
		impl = new Dimension(a, b);
	}

	public GDimensionD() {
		impl = new Dimension();
	}

	@Override
	public int getWidth() {
		return impl.width;
	}

	@Override
	public int getHeight() {
		return impl.height;
	}

	/**
	 * @param d
	 *            dimension, must be of the type geogebra.awt.Dimension
	 * @return AWT implementation wrapped in d
	 */
	public static Dimension getAWTDimension(GDimension d) {

		if (!(d instanceof GDimensionD)) {
			return null;
		}

		return ((GDimensionD) d).impl;
	}

	@Override
	public final boolean equals(Object e) {
		if (e instanceof GDimension) {
			return getWidth() == ((GDimension) e).getWidth()
					&& getHeight() == ((GDimension) e).getHeight();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getHeight() + 37 * getWidth();
	}

}
