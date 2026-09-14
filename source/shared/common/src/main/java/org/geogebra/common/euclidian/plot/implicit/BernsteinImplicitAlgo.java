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
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.arithmetic.BoundsRectangle;
import org.geogebra.common.kernel.arithmetic.bernstein.BernsteinPolynomial2D;
import org.geogebra.common.kernel.arithmetic.bernstein.BernsteinPolynomialConverter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.implicit.ContourAssembler;
import org.jspecify.annotations.NonNull;

/**
 * This algorithm implements efficient plotting of 2D implicit curves using
 * Bernstein polynomial representation.
 * <p>
 * It operates by converting the original curve into a Bernstein polynomial using a converter,
 * and then subdividing the plotting area recursively into smaller cells. Cells that may contain
 * parts of the curve are further subdivided until a pixel threshold is reached, at which point
 * marching squares-style logic is used to extract contour segments.
 * <p>
 * This approach leverages the strong bounding and subdivision properties of Bernstein polynomials,
 * making it suitable for fast and robust rendering of algebraic curves.
 *
 */
public class BernsteinImplicitAlgo implements PlotterAlgo {

	public static final BoundsRectangle UNIT_SQUARE = new BoundsRectangle(0, 1, 0, 1);
	private final EuclidianViewBounds bounds;
	private final GeoElement curve;
	private List<BernsteinPlotCell> cells;
	private final BernsteinPolynomialConverter converter;
	private final ContourAssembler assembler;
	private final int minCellSizeInPixels;
	BernsteinPolynomial2D polynomial;

	/**
	 * @param bounds {@link EuclidianViewBounds}
	 * @param curve the curve geo.
	 * @param cells the cells as intermediate result of the algo.
	 * @param assembler to make segments as the final result of the algo.
	 * @param minCellSizeInPixels cell size that should not split further.
	 */
	public BernsteinImplicitAlgo(@NonNull EuclidianViewBounds bounds, @NonNull GeoElement curve,
			@NonNull List<BernsteinPlotCell> cells, @NonNull ContourAssembler assembler,
			int minCellSizeInPixels) {
		this.bounds = bounds;
		this.curve = curve;
		this.cells = cells;
		this.assembler = assembler;
		this.minCellSizeInPixels = minCellSizeInPixels;
		converter = new BernsteinPolynomialConverter();
	}

	/**
	 * Computes the plot by resetting the assembler and cell list, creating the root plot cell,
	 * and recursively finding the parts of the domain that intersect the implicit curve.
	 */
	@Override
	public void compute() {
		assembler.reset();
		cells.clear();
		BernsteinPlotCell rootCell = createRootCell();
		List<BernsteinPlotCell> algoCells = new ArrayList<>();
		Collections.addAll(algoCells, rootCell.split());
		algoCells.forEach(this::findSolutions);
	}

	/**
	 * Evaluates the implicit polynomial at a given point and checks its sign.
	 *
	 * @param point the point to test
	 * @return true if the value at the point is positive, false otherwise
	 */
	@Override
	public boolean isInside(MyPoint point) {
		return isInside(point.x, point.y);
	}

	/**
	 * Evaluates the polynomial at a screen-space coordinate (px, py),
	 * normalized relative to the view bounds.
	 *
	 * @param px the x-coordinate in screen space
	 * @param py the y-coordinate in screen space
	 * @return true if the evaluated value is positive, false otherwise
	 */
	public boolean isInside(double px, double py) {
		double dx = bounds.getXmax() - bounds.getXmin();
		double dy = bounds.getYmax() - bounds.getYmin();
		if (dx == 0 || dy == 0) {
			return false;
		}

		double x = (px - bounds.getXmin()) / dx;
		double y = (py - bounds.getYmin()) / dy;
		boolean result = polynomial.evaluate(x, y) >= 0;
		return result;
	}

	private BernsteinPlotCell createRootCell() {
		double mx = bounds.getInvXscale() * BernsteinPlotterSettings.MARGIN_IN_PX;
		double my = bounds.getInvYscale() * BernsteinPlotterSettings.MARGIN_IN_PX;
		BoundsRectangle limits = new BoundsRectangle(bounds, mx, my);
		polynomial = converter.bernsteinPolynomial2DFrom(curve, limits);
		BernsteinBoundingBox box = new BernsteinBoundingBox(limits);
		return new BernsteinPlotCell(box, polynomial);
	}

	private void findSolutions(BernsteinPlotCell cell) {
		findSolutionsInFaces(cell);
	}

	private void findSolutionsInFaces(BernsteinPlotCell cell) {
		// Stack to replace recursion
		Stack<BernsteinPlotCell> stack = new Stack<>();
		stack.push(cell);

		while (!stack.isEmpty()) {
			BernsteinPlotCell currentCell = stack.pop();
			if (currentCell == null) {
				continue;
			}
			if (isBoxSmallEnough(currentCell.boundingBox)) {
				addToOutput(currentCell);
				cells.add(currentCell);
			} else {
				for (BernsteinPlotCell c : currentCell.split()) {
					if (c != null && c.mightHaveSolution()) {
						stack.push(c);
					}
				}
				currentCell.release();
			}
		}
	}

	private void addToOutput(BernsteinPlotCell currentCell) {
		BernsteinMarchingConfigProvider provider =
				new BernsteinMarchingConfigProvider(currentCell);
		assembler.add(provider.getMarchingRect(), provider);

	}

	private boolean isBoxSmallEnough(BernsteinBoundingBox box) {
		double width = Math.abs(bounds.toScreenCoordXd(box.x2())
				- bounds.toScreenCoordXd(box.x1()));
		double height =
				Math.abs(bounds.toScreenCoordYd(box.y1()) - bounds.toScreenCoordYd(box.y2()));
		return width <= minCellSizeInPixels || height <= minCellSizeInPixels;
	}
}
