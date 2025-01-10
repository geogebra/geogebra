package org.geogebra.common.euclidian.plot.implicit;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.arithmetic.BoundsRectangle;
import org.geogebra.common.kernel.arithmetic.bernstein.BernsteinPolynomial;
import org.geogebra.common.kernel.arithmetic.bernstein.BernsteinPolynomialConverter;
import org.geogebra.common.util.debug.Log;
import org.junit.Ignore;
import org.junit.Test;

public class BernsteinPlotCellTest extends BaseUnitTest {
	private BernsteinPlotCell context;
	private final BernsteinPolynomialConverter converter = new BernsteinPolynomialConverter();

	@Ignore
	@Test
	public void testSpitContext() {
		BernsteinPolynomial bernstein = converter.from(add("x^3 - y^3 - 0.6 = 0"), defaultLimits());
		context = new BernsteinPlotCell(getDefaultBoundingBox(),
				bernstein);
		BernsteinPlotCell[] contexts = context.split();

		for (BernsteinPlotCell ctx: contexts)  {
			checkEvalOnContext(bernstein, ctx);
			Log.debug(ctx);
		}
	}

	private BoundsRectangle defaultLimits() {
		return new BoundsRectangle(0, 1, 0, 1);
	}

	private void checkEvalOnContext(BernsteinPolynomial bernstein, BernsteinPlotCell context) {
		double offsetX = context.boundingBox.x1();
		double offsetY = context.boundingBox.y1();
		assertSameValue(bernstein, offsetX + 0, offsetY + 0, context, 0, 0);
		assertSameValue(bernstein, offsetX + 0.25, offsetY + 0, context, 0.5, 0);
		assertSameValue(bernstein, offsetX + 0.5, offsetY + 0, context, 1, 0);
		assertSameValue(bernstein, offsetX + 0, offsetY + 0.25, context, 0, 0.5);
		assertSameValue(bernstein, offsetX + 0.25, offsetY + 0.25, context, 0.5, 0.5);
		assertSameValue(bernstein, offsetX + 0.25, offsetY + 0.5, context, 0.5, 1);
		assertSameValue(bernstein, offsetX + 0.5, offsetY + 0.5, context, 1, 1);
	}

	private static void assertSameValue(BernsteinPolynomial bernstein, double x0, double y0,
			BernsteinPlotCell context, double x, double y) {
		assertEquals(bernstein.evaluate(x0, y0), context.polynomial.evaluate(x, y),
				0);
	}

	private BernsteinBoundingBox getDefaultBoundingBox() {
		return new BernsteinBoundingBox(0, 0, 1, 1);
	}

	@Test
	public void testEdges() {
		BernsteinPolynomial bernstein = converter.from(add("x^3 - y^3 - 0.6 = 0"), defaultLimits());
		context = new BernsteinPlotCell(getDefaultBoundingBox(),
				bernstein);
	}
}