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

package org.geogebra.common.util.shape;

import java.util.Objects;

/**
 * Size object.
 */
public final class Size {

	public final double width;
	public final double height;

	/**
	 * Create a size object.
	 * @param width width
	 * @param height height
	 */
	public Size(double width, double height) {
		this.width = width;
		this.height = height;
	}

	/**
	 * Return the width
	 * @return width
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * Return the height
	 * @return height
	 */
	public double getHeight() {
		return height;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Size)) {
			return false;
		}
		Size other = (Size) object;
		return Double.compare(width, other.width) == 0
				&& Double.compare(height, other.height) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(width, height);
	}
}
