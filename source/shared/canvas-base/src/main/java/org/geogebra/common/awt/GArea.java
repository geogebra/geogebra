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

package org.geogebra.common.awt;

/**
 * 2D area supporting boolean operations.
 */
public interface GArea extends GShape {

	/**
	 * Subtract other shape from this
	 * @param shape other shape
	 */
	void subtract(GArea shape);

	/**
	 * Intersect this with other shape
	 * @param shape other shape
	 */
	void intersect(GArea shape);

	/**
	 * XOR other shape with this
	 * @param shape other shape
	 */
	void exclusiveOr(GArea shape);

	/**
	 * Add other shape to this
	 * @param shape other shape
	 */
	void add(GArea shape);

	/**
	 * @return whether the area is empty
	 */
	boolean isEmpty();

	@Override
	public GPathIterator getPathIterator(GAffineTransform t);

}
