package org.geogebra.common.kernel.algos;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Objects;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
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

	private String functionPoints(GeoElement geoElement) {
		ExpressionNode functionExpression = ((GeoFunction) geoElement).getFunctionExpression();
		return Objects.requireNonNull(functionExpression).getRight().toValueString(
				StringTemplate.defaultTemplate);
	}
}