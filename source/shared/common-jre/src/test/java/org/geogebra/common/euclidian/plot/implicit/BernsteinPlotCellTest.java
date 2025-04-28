package org.geogebra.common.euclidian.plot.implicit;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.arithmetic.BoundsRectangle;
import org.geogebra.common.kernel.arithmetic.bernstein.BernsteinPolynomial2D;
import org.geogebra.common.kernel.arithmetic.bernstein.BernsteinPolynomialConverter;
import org.geogebra.common.util.debug.Log;
import org.junit.Test;

public class BernsteinPlotCellTest extends BaseUnitTest {
	private BernsteinPlotCell cell;
	private final BernsteinPolynomialConverter converter = new BernsteinPolynomialConverter();

	//@Ignore
	@Test
	public void testSpitCells() {
		BernsteinPolynomial2D bernstein =
				converter.bernsteinPolynomial2DFrom(add("x^3 - y^3 - 0.6 = 0"), defaultLimits());
		cell = new BernsteinPlotCell(getDefaultBoundingBox(),
				bernstein);
		BernsteinPlotCell[] splitCells = cell.split();

		for (BernsteinPlotCell plotCell: splitCells)  {
			// plotCell is null if it has no solution.
			if (plotCell != null) {
				checkEvalOnContext(bernstein, plotCell);
			}
			Log.debug(plotCell);
		}
	}

	private BoundsRectangle defaultLimits() {
		return new BoundsRectangle(0, 1, 0, 1);
	}

	private void checkEvalOnContext(BernsteinPolynomial2D bernstein2D, BernsteinPlotCell cell) {
		double offsetX = cell.boundingBox.x1();
		double offsetY = cell.boundingBox.y1();
		assertSameValue(bernstein2D, offsetX + 0, offsetY + 0, cell, 0, 0);
		assertSameValue(bernstein2D, offsetX + 0.25, offsetY + 0, cell, 0.5, 0);
		assertSameValue(bernstein2D, offsetX + 0.5, offsetY + 0, cell, 1, 0);
		assertSameValue(bernstein2D, offsetX + 0, offsetY + 0.25, cell, 0, 0.5);
		assertSameValue(bernstein2D, offsetX + 0.25, offsetY + 0.25, cell, 0.5, 0.5);
		assertSameValue(bernstein2D, offsetX + 0.25, offsetY + 0.5, cell, 0.5, 1);
		assertSameValue(bernstein2D, offsetX + 0.5, offsetY + 0.5, cell, 1, 1);
	}

	private static void assertSameValue(BernsteinPolynomial2D bernstein2D, double x0, double y0,
			BernsteinPlotCell context, double x, double y) {
		assertEquals(bernstein2D.evaluate(x0, y0), context.polynomial.evaluate(x, y),
				1E-12);
	}

	private BernsteinBoundingBox getDefaultBoundingBox() {
		return new BernsteinBoundingBox(0, 0, 1, 1);
	}
}