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

package org.geogebra.common.gui;

import com.google.j2objc.annotations.ObjectiveCName;

@ObjectiveCName("GGBEdgeInsets")
public class EdgeInsets {
	private final int left;
	private final int top;
	private final int right;
	private final int bottom;

	/**
	 * Creates an empty edge inset
	 */
	public EdgeInsets() {
		this(0);
	}

	/**
	 * Construct an edge inset object.
	 * @param all inset from all directions
	 */
	public EdgeInsets(int all) {
		this(all, all, all, all);
	}

	/**
	 * Construct an edge inset object.
	 * @param left left inset
	 * @param top top inset
	 * @param right right inset
	 * @param bottom bottom inset
	 */
	public EdgeInsets(int left, int top, int right, int bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}

	public int getLeft() {
		return left;
	}

	public int getTop() {
		return top;
	}

	public int getRight() {
		return right;
	}

	public int getBottom() {
		return bottom;
	}
}
