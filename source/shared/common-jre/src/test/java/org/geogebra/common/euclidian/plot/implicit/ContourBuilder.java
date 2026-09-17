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

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.arithmetic.BoundsRectangle;
import org.geogebra.common.kernel.arithmetic.bernstein.BernsteinPolynomial2D;
import org.geogebra.common.kernel.arithmetic.bernstein.BernsteinPolynomialConverter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.implicit.CompleteContourLinker;
import org.geogebra.common.kernel.implicit.ContourAssembler;
import org.geogebra.common.kernel.implicit.ContourLinker;

/**
 * Test helper that builds contour data from a {@link GeoElement} and view bounds.
 * <p>
 * Can run the marching-based implicit algorithm or convert directly to a
 * {@link BernsteinPolynomial2D} and returns a {@link ContourInfo} bundle for tests.
 * </p>
 */
public class ContourBuilder {
	private GeoElement geo;
	private EuclidianViewBounds bounds;
	private final BernsteinPolynomialConverter converter = new BernsteinPolynomialConverter();
	private BernsteinPolynomial2D polynomial;
	private ContourAssembler assembler;

	/**
	 * Sets the implicit curve to use and clears any previously prepared polynomial.
	 *
	 * @param geo source implicit curve element
	 * @return this builder for chaining
	 */
	public ContourBuilder withImplicitCurve(GeoElement geo) {
		this.geo = geo;
		this.polynomial = null;
		return this;
	}

	/**
	 * Sets the view bounds from world limits and screen size.
	 *
	 * @param xmin   min x in world units
	 * @param xmax   max x in world units
	 * @param ymin   min y in world units
	 * @param ymax   max y in world units
	 * @param width  screen width in pixels
	 * @param height screen height in pixels
	 * @return this builder for chaining
	 */
	public ContourBuilder withBounds(
			double xmin, double xmax, double ymin, double ymax, int width, int height) {
		return withBounds(BaseContourTestSetup.newBounds(xmin, xmax, ymin, ymax, width, height));
	}

	/**
	 * Sets the view bounds to use for building contours.
	 *
	 * @param bounds prepared bounds instance
	 * @return this builder for chaining
	 */
	ContourBuilder withBounds(EuclidianViewBounds bounds) {
		this.bounds = bounds;
		return this;
	}

	/**
	 * Builds the contour bundle. Runs the implicit algorithm when the polynomial
	 * has not been prepared yet; otherwise returns info with the existing polynomial.
	 *
	 * @return {@link ContourInfo} containing bounds, polynomial, and optional assembler
	 */
	public ContourInfo build() {
		if (polynomial == null) {
			runImplicitCurveAlgo();
			return new ContourInfo(bounds, polynomial, assembler);
		}
		return new ContourInfo(bounds, polynomial);
	}

	/**
	 * Computes the polynomial and contours using the marching-based implicit algorithm.
	 * Populates {@link #polynomial} and {@link #assembler}.
	 */
	private void runImplicitCurveAlgo() {
		ContourLinker linker = new CompleteContourLinker();
		assembler = new ContourAssembler(linker);
		List<BernsteinPlotCell> cells = new ArrayList<>();
		BernsteinImplicitAlgo algo = new BernsteinImplicitAlgo(bounds, geo, cells, assembler, 4);
		algo.compute();
		polynomial = algo.polynomial;
	}

	/**
	 * Builds the Bernstein polynomial by direct conversion without marching.
	 *
	 * @return this builder for chaining
	 */
	public ContourBuilder withDirectConvert() {
		polynomial = converter.bernsteinPolynomial2DFrom(geo, new BoundsRectangle(bounds, 10, 10));
		return this;
	}

	/**
	 * Returns the bounds configured for this builder.
	 *
	 * @return view bounds
	 */
	public EuclidianViewBounds getBounds() {
		return bounds;
	}
}
