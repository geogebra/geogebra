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

import javax.annotation.Nonnull;

import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.arithmetic.BoundsRectangle;
import org.geogebra.common.kernel.arithmetic.bernstein.BernsteinPolynomial2D;
import org.geogebra.common.kernel.arithmetic.bernstein.BernsteinPolynomialConverter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.implicit.LinkSegments;

public class BernsteinImplicitAlgo implements PlotterAlgo {

	public static final BoundsRectangle UNIT_SQUARE = new BoundsRectangle(0, 1, 0, 1);
	private final EuclidianViewBounds bounds;
	private final GeoElement curve;
	private List<BernsteinPlotCell> cells;
	private final BernsteinPolynomialConverter converter;
	private final LinkSegments segments;
	private final int minCellSizeInPixels;
	BernsteinPolynomial2D polynomial;

	/**
	 * @param bounds {@link EuclidianViewBounds}
	 * @param curve the curve geo.
	 * @param cells the cells as intermediate result of the algo.
	 * @param segments to make segments as the final result of the algo.
	 * @param minCellSizeInPixels cell size that should not split further.
	 */
	public BernsteinImplicitAlgo(@Nonnull EuclidianViewBounds bounds, @Nonnull GeoElement curve,
			@Nonnull List<BernsteinPlotCell> cells, @Nonnull LinkSegments segments,
			int minCellSizeInPixels) {
		this.bounds = bounds;
		this.curve = curve;
		this.cells = cells;
		this.segments = segments;
		this.minCellSizeInPixels = minCellSizeInPixels;
		converter = new BernsteinPolynomialConverter();
	}

	@Override
	public void compute() {
		cells.clear();
		BernsteinPlotCell rootCell = createRootCell();
		List<BernsteinPlotCell> algoCells = new ArrayList<>();
		Collections.addAll(algoCells, rootCell.split());
		algoCells.forEach(this::findSolutions);
		segments.flush();
	}

	private BernsteinPlotCell createRootCell() {
		BoundsRectangle limits = new BoundsRectangle(bounds);
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
		segments.add(provider.getMarchingRect(), provider);

	}

	private boolean isBoxSmallEnough(BernsteinBoundingBox box) {
		double width = bounds.toScreenCoordXd(box.x2()) - bounds.toScreenCoordXd(box.x1());
		double height = bounds.toScreenCoordYd(box.y1()) - bounds.toScreenCoordYd(box.y2());
		return width <= minCellSizeInPixels || height <= minCellSizeInPixels;
	}
}