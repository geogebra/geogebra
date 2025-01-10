package org.geogebra.common.euclidian.plot.implicit;

import org.geogebra.common.kernel.arithmetic.Splittable;
import org.geogebra.common.kernel.arithmetic.bernstein.BernsteinPolynomial;
import org.geogebra.common.kernel.implicit.MarchingConfig;

/**
 * BernsteinPlotCell is the basic building block of the algorithm.
 * A cell consist a bounding box, a Bernstein polynomial limited to that bounding box.
 * The algo splits the screen into these cells, decides if there might be a solution in
 * that cell, and if there might be, splits it further until a given box size. If that minimal size
 * is reached, we declare that there is a solution in that cell.
 *
 */
public class BernsteinPlotCell implements Splittable<BernsteinPlotCell> {
	enum BernsteinPlotCellKind {
		CELL0,
		CELL1,
		CELL2
	}

	final BernsteinBoundingBox boundingBox;
	final BernsteinPolynomial polynomial;
	private final BernsteinPlotCellKind kind;
	private MarchingConfig marchingConfig;

	/**
	 *
	 * @param box bounding box of the cell
	 * @param polynomial restricted to the cell.
	 */
	public BernsteinPlotCell(BernsteinBoundingBox box, BernsteinPolynomial polynomial) {
		boundingBox = box;
		this.polynomial = polynomial;
		kind = classify();
	}

	/**
	 *
	 * @return kind: top, left, bottom, right,
	 */
	BernsteinPlotCellKind getKind() {
		return kind;
	}

	private BernsteinPlotCellKind classify() {
		if (polynomial.hasNoSolution()) {
			return BernsteinPlotCellKind.CELL2;
		}

		BernsteinPolynomial dx = polynomial.derivative("x");
		BernsteinPolynomial dy = polynomial.derivative("y");

		if (dx.hasNoSolution() != dy.hasNoSolution()) {
			return BernsteinPlotCellKind.CELL1;
		}

		return BernsteinPlotCellKind.CELL0;
	}

	@Override
	public BernsteinPlotCell[] split() {
		BernsteinPolynomial[][] polynomials = polynomial.split2D();
		BernsteinBoundingBox[] boxes = boundingBox.split();
		BernsteinPlotCell[] cells = new BernsteinPlotCell[4];
		cells[0] = new BernsteinPlotCell(boxes[0], polynomials[0][0]);
		cells[1] = new BernsteinPlotCell(boxes[1], polynomials[1][0]);
		cells[2] = new BernsteinPlotCell(boxes[2], polynomials[0][1]);
		cells[3] = new BernsteinPlotCell(boxes[3], polynomials[1][1]);
		return cells;
	}

	@Override
	public String toString() {
		return "CurvePlotContext{"
				+ "boundingBox=" + boundingBox
				+ ", polynomial=" + polynomial
				+ ", contextCass=" + kind
				+ '}';
	}

	public boolean mightHaveSolution() {
		return kind != BernsteinPlotCellKind.CELL2;
	}

	public MarchingConfig getMarchingConfig() {
		return marchingConfig;
	}

	public void setMarchingConfig(MarchingConfig marchingConfig) {
		this.marchingConfig = marchingConfig;
	}
}
