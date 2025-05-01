package org.geogebra.common.euclidian.plot.implicit;

import org.geogebra.common.kernel.arithmetic.Splittable;
import org.geogebra.common.kernel.arithmetic.bernstein.BernsteinPolynomial2D;
import org.geogebra.common.kernel.implicit.MarchingConfig;

/**
 * BernsteinPlotCell is the basic building block of the algorithm.
 * A cell consists of a bounding box, a Bernstein polynomial limited to that bounding box.
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
	final BernsteinPolynomial2D polynomial;
	private final BernsteinPlotCellKind kind;
	private MarchingConfig marchingConfig;

	/**
	 *
	 * @param box bounding box of the cell
	 * @param polynomial restricted to the cell.
	 */
	public BernsteinPlotCell(BernsteinBoundingBox box, BernsteinPolynomial2D polynomial) {
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

		if (polynomial.onlyOnePartialDerivateHasSolution()) {
			return BernsteinPlotCellKind.CELL1;
		}

		return BernsteinPlotCellKind.CELL0;
	}

	@Override
	public BernsteinPlotCell[] split() {
		BernsteinPolynomial2D[][] polynomials = polynomial.split();
		BernsteinBoundingBox[] boxes = boundingBox.split();
		BernsteinPlotCell[] cells = new BernsteinPlotCell[4];
		BernsteinPolynomial2D[] poly0 = polynomials[0];
		if (poly0 != null) {
			cells[0] = createCell(boxes[0], poly0[0]);
			cells[2] = createCell(boxes[2], poly0[1]);
		} else {
			cells[0] = null;
			cells[2] = null;
		}
		BernsteinPolynomial2D[] poly1 = polynomials[1];
		if (poly1 != null) {
			cells[1] = createCell(boxes[1], poly1[0]);
			cells[3] = createCell(boxes[3], poly1[1]);
		} else {
			cells[1] = null;
			cells[3] = null;
		}
		return cells;
	}

	private BernsteinPlotCell createCell(BernsteinBoundingBox box,
			BernsteinPolynomial2D polynomial) {
		if (polynomial == null) {
			return null;
		}

		if (polynomial.hasNoSolution()) {
			return null;
		}
		return new BernsteinPlotCell(box, polynomial);
	}

	@Override
	public String toString() {
		return "CurvePlotContext{"
				+ "boundingBox=" + boundingBox
				+ ", polynomial=" + polynomial
				+ ", contextCass=" + kind
				+ '}';
	}

	/**
	 * @return whether a solution might exist in this cell
	 */
	public boolean mightHaveSolution() {
		return kind != BernsteinPlotCellKind.CELL2;
	}

	public MarchingConfig getMarchingConfig() {
		return marchingConfig;
	}

	public void setMarchingConfig(MarchingConfig marchingConfig) {
		this.marchingConfig = marchingConfig;
	}

	/**
	 * Release the associated objects for reuse.
	 */
	public void release() {
		boundingBox.release();
	}
}
