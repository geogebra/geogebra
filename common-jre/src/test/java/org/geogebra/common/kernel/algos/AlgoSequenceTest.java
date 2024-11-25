package org.geogebra.common.kernel.algos;

import static org.geogebra.test.OrderingComparison.greaterThan;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.util.Objects;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GGraphicsCommon;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoLocus;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;
import org.geogebra.test.annotation.Issue;
import org.junit.Test;

public class AlgoSequenceTest extends BaseUnitTest {

	@Test
	public void testAngleVisibility() {
		addAvInput("A = (0, 0)");
		addAvInput("B = (1, 1)");
		addAvInput("C = (0, 1)");
		addAvInput("list = Sequence(Angle(A,B,C), i, 1, 1)");
		GeoAngle angle = addAvInput("list(1)");
		assertThat(angle.showInEuclidianView(), is(true));
	}

	@Test
	public void elementsShouldBeDeepCopied() {
		addAvInput("l1 = {{(0,0), (1,0), (2,0), (3,0)},{(1,1), (2,1)}}");
		GeoList seq = addAvInput("Sequence(LineGraph(x(Element(l1, k)), "
				+ "y(Element(l1, k))), k, 1, Length(l1))");
		assertThat(functionPoints(seq.get(0)), is("{0, 1, 2, 3}, {0, 0, 0, 0}"));
		assertThat(functionPoints(seq.get(1)), is("{1, 2}, {1, 1}"));
	}

	@Test
	public void implicitCurveBoundsInSequence() {
		GeoList list = add("Sequence(sin(x)+sin(y)=-k,k,1,5)");
		for (int i = 0; i < list.size(); i++) {
			double val = ((GeoImplicitCurve) list.get(i)).evaluateImplicitCurve(0, 0, 0);
			assertThat(val, is(i + 1.0));
		}
	}

	@Test
	public void sequenceOfLociShouldChangeOnZoom() {
		add("ZoomIn(-5,-5,5,5)");
		add("A=Point(0x+1)");
		GeoList seq = add("Sequence(Locus(k*A,A),k,1,4)");
		GeoLocus loc = (GeoLocus) seq.get(3);
		long ptsBefore = loc.getPoints().stream().filter(pt -> Math.abs(pt.getX()) < 5).count();
		add("ZoomIn(-100,-100,100,100)");
		long ptsAfter = loc.getPoints().stream().filter(pt -> Math.abs(pt.getX()) < 5).count();
		assertThat(ptsBefore, greaterThan(ptsAfter * 10));
	}

	@Test
	public void closestPointToSequenceOfPolygons() {
		add("l1=Sequence(Polygon((p, 1), (p, 2), 4), p, 1, 5)");
		assertThat(add("ClosestPoint(l1, (6, 1))"), hasValue("(5, 1)"));
	}

	@Test
	public void pieChartSequenceTest() {
		GGraphicsCommon graphics = createGraphicsWithDrawable(
				"s1=Sequence(PieChart({1,2,3},(k,1),.5),k,1,3)");
		verify(graphics, atLeast(5)).setColor(any());
		verify(graphics, atLeast(5)).fill(any());
	}

	@Test
	public void chartSequenceTest() {
		GGraphicsCommon graphics = createGraphicsWithDrawable(
				"s2=Sequence(BarChart({1,2,3},{4,5,6}/k),k,1,3)");
		verify(graphics, atLeast(5)).fill(any());
	}

	@Test
	@Issue("APPS-6096")
	public void shouldNotKeepDefinitionsWithVariableReference() {
		add("l1={{{1,2},{3,4}},{{5,6},{7,8}},{{9,10},{11,12}}}");
		GeoList seq = add("Sequence(Sequence(Element(l1,p)+m Identity(2),p,1,3),m,-1,1)");
		assertThat(seq, hasValue("{{{{0, 2}, {3, 3}}, {{4, 6}, {7, 7}}, {{8, 10}, {11, 11}}},"
				+ " {{{1, 2}, {3, 4}}, {{5, 6}, {7, 8}}, {{9, 10}, {11, 12}}}, {{{2, 2}, {3, 5}},"
				+ " {{6, 6}, {7, 9}}, {{10, 10}, {11, 13}}}}"));
	}

	private GGraphicsCommon createGraphicsWithDrawable(String def) {
		GeoList charts = add(def);
		Drawable drawCharts = getDrawable(charts);
		GGraphicsCommon graphics = spy(new GGraphicsCommon());
		Objects.requireNonNull(drawCharts).draw(graphics);
		return graphics;

	}

	private String functionPoints(GeoElement geoElement) {
		ExpressionNode functionExpression = ((GeoFunction) geoElement).getFunctionExpression();
		return Objects.requireNonNull(functionExpression).getRight().toValueString(
				StringTemplate.defaultTemplate);
	}
}