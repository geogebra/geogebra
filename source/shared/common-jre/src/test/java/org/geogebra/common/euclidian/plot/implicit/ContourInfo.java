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

package org.geogebra.common.euclidian.plot.implicit;

import java.util.List;

import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.arithmetic.bernstein.BernsteinPolynomial2D;
import org.geogebra.common.kernel.implicit.ContourAssembler;
import org.geogebra.common.kernel.implicit.PointList;

/**
 * Test helper that bundles view bounds, a polynomial, and an optional
 * {@link ContourAssembler}. Also prepares a {@link ClipRect} for clipping
 * tests.
 */
public class ContourInfo {
	private final EuclidianViewBounds bounds;
	private final BernsteinPolynomial2D polynomial;
	private final ContourAssembler assembler;
	private final ClipRect clipRect;

	/**
	 * Creates an info bundle without an assembler.
	 *
	 * @param bounds      view bounds used for tests
	 * @param polynomial  polynomial under test
	 */
	public ContourInfo(EuclidianViewBounds bounds, BernsteinPolynomial2D polynomial) {
		this(bounds, polynomial, null);
	}

	/**
	 * Creates an info bundle with an assembler and a prebuilt {@link ClipRect}.
	 *
	 * @param bounds      view bounds used for tests
	 * @param polynomial  polynomial under test
	 * @param assembler   contour assembler (may be {@code null})
	 */
	public ContourInfo(EuclidianViewBounds bounds, BernsteinPolynomial2D polynomial,
			ContourAssembler assembler) {
		this.bounds = bounds;
		this.polynomial = polynomial;
		this.assembler = assembler;
		clipRect = new ClipRect(bounds, 10);
	}

	/**
	 * Returns the view bounds for this bundle.
	 *
	 * @return test view bounds
	 */
	public EuclidianViewBounds getBounds() {
		return bounds;
	}

	/**
	 * Returns the polynomial under test.
	 *
	 * @return Bernstein polynomial
	 */
	public BernsteinPolynomial2D getPolynomial() {
		return polynomial;
	}

	/**
	 * Returns the current contours from the assembler, if present.
	 *
	 * @return contour list, or {@code null} if no assembler was supplied
	 */
	public List<PointList> getContours() {
		if (assembler == null) {
			return null;
		}
		return assembler.getContours();
	}

	/**
	 * Returns the assembler used to build contours.
	 *
	 * @return assembler (may be {@code null})
	 */
	public ContourAssembler getAssembler() {
		return assembler;
	}

	/**
	 * Returns the clip rectangle created from the bounds for tests.
	 *
	 * @return clip rectangle
	 */
	public ClipRect getClipRect() {
		return clipRect;
	}
}
