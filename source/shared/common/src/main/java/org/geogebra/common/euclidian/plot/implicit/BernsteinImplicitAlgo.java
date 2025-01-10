package org.geogebra.common.euclidian.plot.implicit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.arithmetic.BoundsRectangle;
import org.geogebra.common.kernel.arithmetic.bernstein.BernsteinPolynomial;
import org.geogebra.common.kernel.arithmetic.bernstein.BernsteinPolynomialConverter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.implicit.LinkSegments;

public class BernsteinImplicitAlgo implements PlotterAlgo {

	public static final BoundsRectangle UNIT_SQUARE = new BoundsRectangle(0, 1, 0, 1);
	private final EuclidianViewBounds bounds;
	private final GeoElement curve;
	private final List<BernsteinPlotCell> cells;
	private final BernsteinImplicitAlgoSettings settings;
	private final BernsteinPolynomialConverter converter;
	private final LinkSegments segments;
	BernsteinPolynomial polynomial;

	/**
	 * @param bounds {@link EuclidianViewBounds}
	 * @param curve the curve geo.
	 * @param cells the cells as itermediate result of the algo.
	 * @param segments to make segments as the final result of the algo.
	 * @param settings {@link BernsteinImplicitAlgoSettings}
	 */
	public BernsteinImplicitAlgo(EuclidianViewBounds bounds, GeoElement curve,
			List<BernsteinPlotCell> cells, LinkSegments segments,
			BernsteinImplicitAlgoSettings settings) {
		this.bounds = bounds;
		this.curve = curve;
		this.cells = cells;
		this.segments = segments;
		this.settings = settings;
		converter = new BernsteinPolynomialConverter();
	}

	@Override
	public void compute() {
		List<BernsteinPlotCell> cells = initialSplit(createRootCell());
		cells.forEach(this::findSolutions);
		segments.flush();
	}

	private List<BernsteinPlotCell> initialSplit(BernsteinPlotCell rootCell) {
		List<BernsteinPlotCell> list = new ArrayList<>();
		Collections.addAll(list, rootCell.split());
		return cellsWithPossibleSolution(list);
	}

	private BernsteinPlotCell createRootCell() {
		BoundsRectangle limits = new BoundsRectangle(bounds);
		polynomial = converter.from(curve, limits);
		BernsteinBoundingBox box = new BernsteinBoundingBox(limits);
		return new BernsteinPlotCell(box, polynomial);
	}

	private void findSolutions(BernsteinPlotCell cell) {
		findSolutionsInFaces(cell);
	}

	private static List<BernsteinPlotCell> cellsWithPossibleSolution(List<BernsteinPlotCell> list) {
		return list.stream().filter(BernsteinPlotCell::mightHaveSolution)
				.collect(Collectors.toList());
	}

	private void findSolutionsInFaces(BernsteinPlotCell cell) {
		// Stack to replace recursion
		Stack<BernsteinPlotCell> stack = new Stack<>();
		stack.push(cell);

		while (!stack.isEmpty()) {
			BernsteinPlotCell currentCell = stack.pop();

			if (!currentCell.mightHaveSolution()) {
				return;
			}

			BernsteinMarchingConfigProvider provider =
					new BernsteinMarchingConfigProvider(currentCell);

			if (isBoxSmallEnough(currentCell.boundingBox)) {
				addToOutput(currentCell, provider);
			} else {
				for (BernsteinPlotCell c : currentCell.split()) {
					if (c.mightHaveSolution()) {
						stack.push(c);
					}
				}
			}
		}
	}

	private void addToOutput(BernsteinPlotCell currentCell,
			BernsteinMarchingConfigProvider provider) {
		segments.add(provider.getMarchingRect(), provider);
		cells.add(currentCell);
	}

	private boolean isBoxSmallEnough(BernsteinBoundingBox box) {
		double width = bounds.toScreenCoordXd(box.x2()) - bounds.toScreenCoordXd(box.x1());
		double height = bounds.toScreenCoordYd(box.y1()) - bounds.toScreenCoordYd(box.y2());
		int maxWidth = settings.minBoxWidthInPixels();
		int maxHeight = settings.minBoxHeightInPixels();
		return width < maxWidth && height < maxHeight;
	}
}