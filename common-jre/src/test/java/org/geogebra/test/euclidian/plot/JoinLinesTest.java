package org.geogebra.test.euclidian.plot;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.euclidian.plot.TupleNeighbours;
import org.geogebra.common.euclidian.plot.interval.EuclidianViewBoundsMock;
import org.geogebra.common.euclidian.plot.interval.IntervalPathPlotterMock;
import org.geogebra.common.euclidian.plot.interval.JoinLines;
import org.junit.Test;

public class JoinLinesTest {
	private static final IntervalPathPlotterMock gpExpected = new IntervalPathPlotterMock();
	private static final IntervalPathPlotterMock gp = new IntervalPathPlotterMock();
	private static EuclidianViewBoundsMock bounds;

	@Test
	public void topToRight() {
		JoinLines join = createJoinLines(-0.5, 3.8);

		// ln(ln(csc(x))) data around x 0.
		TupleNeighbours data = new TupleNeighbours(
				Tuples.undefined(-0.017313779261777358, -0.00619708088593479),
				Tuples.inverted(
						-0.00619708088593479, 0.004919617489907779,
						Double.NEGATIVE_INFINITY, 1.6704443025404654),
				Tuples.normal(0.004919617489907779, 0.01603631586575035,
						1.4189895620576354, 1.6704443025404656));

		gpExpected.moveTo(data.rightXHigh(), bounds.getYmax());
		gpExpected.lineTo(data.rightXHigh(), data.currentYHigh());

		join.toTop(data);
		assertSamePlot();
	}

	@Test
	public void topToLeft() {
		// ln(ln(csc(x))) sample around x 3.1.
		JoinLines join = createJoinLines(-0.5, 3.8);
		TupleNeighbours neighbours = new TupleNeighbours(
				Tuples.normal(3.1199999999998065, 3.1399999999998065, 1.3442945487757987,
						1.8628940248860604),
				Tuples.inverted(3.1399999999998065, 3.1599999999998065,
					Double.NEGATIVE_INFINITY, 1.8628940248860604),
				Tuples.undefined(3.1599999999998065, 3.1799999999998065));

		gpExpected.moveTo(neighbours.rightXHigh(), bounds.getYmax());
		gpExpected.lineTo(neighbours.currentXLow(), neighbours.leftYHigh());

		join.toTop(neighbours);
		assertSamePlot();
	}

	@Test
	public void toTopStraight() {
		// -2/(9+tan(x)) around 4.85
		JoinLines join = createJoinLines(1.7, 4.85);
		TupleNeighbours neighbours = new TupleNeighbours(
				Tuples.normal(4.799999999999791, 4.819999999999791,
						0.8386199043620873, 7.787235877787062) ,
				Tuples.inverted(4.819999999999791, 4.83999999999979,
						-1.6580044069143518, 7.7872358777869) ,
				Tuples.normal(4.83999999999979, 4.85999999999979,
						-1.6580044069143591, -0.8792316723432034));

		gpExpected.moveTo(neighbours.rightXHigh(), bounds.getYmax());
		gpExpected.lineTo(neighbours.currentXLow(), neighbours.currentYHigh());

		join.toTop(neighbours);
		assertSamePlot();
	}

	@Test
	public void toBottomStraight() {
		// -2/(9+tan(x)) around 1.65
		JoinLines join = createJoinLines(1.6, 3);
		TupleNeighbours neighbours = new TupleNeighbours(
				Tuples.normal(1.6599999999998059, 1.6799999999998059,
						0.9171996398890068, 16.56027577582161) ,
				Tuples.inverted(1.6799999999998059, 1.6999999999998059,
						-1.5344508845426863, 16.560275775820877),
				Tuples.normal(1.6999999999998059, 1.719999999999806,
						-1.5344508845426927, -0.8519481655142178));
		gpExpected.moveTo(neighbours.rightXHigh(), neighbours.currentYLow());
		gpExpected.lineTo(neighbours.currentXHigh(), bounds.getYmin());
		join.toBottom(neighbours);
		assertSamePlot();
	}

	@Test
	public void toBottomLeft() {
		// -ln(ln(csc(x))) sample around x 0.
		JoinLines join = createJoinLines(-0.5, 3.8);
		TupleNeighbours neighbours = new TupleNeighbours(
				Tuples.undefined(-0.030000000000410212, -0.010000000000410209),
				Tuples.inverted(-0.010000000000410209, 0.009999999999589795,
						-1.5271832449430147, Double.POSITIVE_INFINITY),
				Tuples.normal(0.009999999999589795, 0.0299999999995898,
						-1.527183244943015, -1.2546776776504265));
		gpExpected.moveTo(neighbours.rightXHigh(), neighbours.currentYLow());
		gpExpected.lineTo(neighbours.rightXLow(), bounds.getYmin());
		join.toBottom(neighbours);
		assertSamePlot();
	}

	@Test
	public void toBottomRight() {
		// -ln(ln(csc(x))) sample around x 3.1.
		JoinLines join = createJoinLines(-0.5, 3.8);
		TupleNeighbours neighbours = new TupleNeighbours(
				Tuples.normal(3.109999999999592, 3.129999999999592,
						-1.4945670029068763, -1.2398215951550768),
				Tuples.inverted(3.129999999999592, 3.1499999999995922,
						-1.4945670029068763, Double.POSITIVE_INFINITY),
				Tuples.undefined(3.1499999999995922, 3.1699999999995923));
		gpExpected.moveTo(neighbours.currentXLow(), neighbours.currentYLow());
		gpExpected.lineTo(neighbours.currentXLow(), bounds.getYmin());
		join.toBottom(neighbours);
		assertSamePlot();
	}

	private void assertSamePlot() {
		// assertEquals(gpExpected, gp)) could be used here, but the diff of logs is lost
		// in that case. Any idea is welcomed.

		assertEquals(gpExpected.getLog(), gp.getLog());
	}

	private JoinLines createJoinLines(double xmin, double xmax) {
		bounds = new EuclidianViewBoundsMock(xmin, xmax, -10, 10);
		gp.setBounds(bounds);
		gpExpected.setBounds(bounds);
		return new JoinLines(bounds, gp);
	}

	@Test
	public void noBottomLineToSingletonInfinity() {
		// csc(-4/ln(x)) sample around x 0.
		JoinLines join = createJoinLines(-0.1, 0.24);
		TupleNeighbours neighbours = new TupleNeighbours(
				Tuples.undefined(-0.02000000000019567, -1.9566986919627993e-13) ,
				Tuples.inverted(-1.9566986919627993e-13, 0.01999999999980433,
						-1.6331239353195366E16, 1.1717725061486761) ,
				Tuples.normal(0.01999999999980433, 0.03999999999980433,
						1.0563590908668452, 1.1717725061486766));
		gpExpected.moveTo(0.03999999999980433, 10.0);
		gpExpected.lineTo(0.03999999999980433, 1.1717725061486761);
		join.inverted(neighbours);
		assertSamePlot();
	}

	@Test
	public void noTopLineToSingletonInfinity() {
		// -csc(-4/ln(x)) sample around x 0.
		JoinLines join = createJoinLines(-0.1, 0.24);
		TupleNeighbours neighbours = new TupleNeighbours(
				Tuples.undefined(-0.02000000000019567, -1.9566986919627993e-13),
				Tuples.inverted(-1.9566986919627993e-13, 0.01999999999980433,
						-1.1717725061486761, 1.63312393531953661E6),
				Tuples.normal(0.01999999999980433, 0.03999999999980433,
						-1.1717725061486766, -1.0563590908668452));
		gpExpected.moveTo(neighbours.rightXHigh(), neighbours.currentYLow());
		gpExpected.lineTo(neighbours.rightXLow(), -0.1);
		join.inverted(neighbours);
		assertSamePlot();
	}
}