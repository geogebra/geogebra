package org.geogebra.common.kernel.algos;

import static org.geogebra.common.BaseUnitTest.hasProperty;
import static org.geogebra.common.BaseUnitTest.hasValue;
import static org.geogebra.test.TestStringUtil.unicode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.IneqTree;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.MoveGeos;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.script.GgbScript;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class AlgoElementTest {

	private AppCommon app = AppCommonFactory.create3D();

	@ParameterizedTest
	@CsvSource(value = {"Integral(x,1,2);\\int\\limits_{1}^{2}x\\,\\mathrm{d}x",
			"Integral(x);\\int x\\,\\mathrm{d}x",
			"Integral(f,1,2);\\int\\limits_{1}^{2}f\\,\\mathrm{d}n",
			"Integral(f);\\int f\\,\\mathrm{d}n",
			"Sequence(Integral(x^k),k,1,2);"
					+ "Sequence\\left(\\int x^{k}\\,\\mathrm{d}x, k, 1, 2 \\right)"},
			delimiterString = ";")
	public void latexIntegral(String cmd, String latex) {
		add("f(n)=n^2");
		assertEquals(latex, add(cmd).getDefinition(StringTemplate.latexTemplate));
	}

	@ParameterizedTest
	@CsvSource(value = {"Sum(n-k,n,1,3);\\sum_{n=1}^{3}\\left(n - k \\right)",
			"Sum(n,n,1,3);\\sum_{n=1}^{3}n",
			"Sum(n^k,n,1,3);\\sum_{n=1}^{3}n^{k}",
			"Sum(sin(n),n,1,3);\\sum_{n=1}^{3}\\operatorname{sin} \\left( n \\right)"},
			delimiterString = ";")
	public void latexSum(String cmd, String latex) {
		app.getKernel().setSymbolicMode(SymbolicMode.SYMBOLIC_AV);
		assertEquals(latex, add(cmd).getDefinition(StringTemplate.latexTemplate));
	}

	@Test
	public void testSequenceInequality() {
		add("a: y<=x^(2) && y>= 0 && x>=0 && x<=1");
		GeoList list = add("{a(x+1,y), a(x+2,y)}");
		GeoList seq = add("l1=Sequence(a(x+i,y),i,1,2)");
		GeoList elements = add("{Element[l1,1], Element[l1,2]}");
		String listValue = list.toValueString(StringTemplate.defaultTemplate);
		assertThat(seq, hasValue(listValue));
		assertThat(elements, hasValue(listValue));
	}

	@Test
	public void testSequenceIneqTree() {
		add("a: y<=x^(2) && y>= 0 && x>=0 && x<=1");
		GeoList sequence = add("Sequence(a(x+i,y),i,1,2)");
		IneqTree inequalities1 = ((GeoFunctionNVar) sequence.get(0)).getIneqs();
		IneqTree inequalities2 = ((GeoFunctionNVar) sequence.get(1)).getIneqs();
		assertThat(inequalities1.getLeft().getLeft().getLeft().getIneq().getBorder(),
				hasValue(unicode("((x + 1)^2 - 0) / 1")));
		assertThat(inequalities2.getLeft().getLeft().getLeft().getIneq().getBorder(),
				hasValue(unicode("((x + 2)^2 - 0) / 1")));
	}

	@ParameterizedTest
	@CsvSource(value = {"Integral(x-d,a,b);\\int\\limits_{a}^{b}x - d\\,\\mathrm{d}x",
			"Integral(t-d,a,b);\\int\\limits_{a}^{b}t - d\\,\\mathrm{d}d",
			"Integral(s-d,a,b);\\int\\limits_{a}^{b}s - d\\,\\mathrm{d}d",
			"Integral(s-r,a,b);\\int\\limits_{a}^{b}s - r\\,\\mathrm{d}r",
			"Integral(t-x,a,b);\\int\\limits_{a}^{b}t - x\\,\\mathrm{d}x"},
			delimiterString = ";")
	public void latexIntegralShouldHaveCorrectDerivativeVariable(String cmd, String latex) {
		app.getKernel().setSymbolicMode(SymbolicMode.SYMBOLIC_AV);
		assertEquals(latex, add(cmd).getDefinition(StringTemplate.latexTemplate));
	}

	@Test
	@Issue("APPS-5423")
	public void testAlgoDependentPointShouldNotUpdateEndlessly() {
		GeoPoint p = add("(1, 1)");
		AlgoDependentPoint algo = new AlgoDependentPoint(p.getConstruction(),
				p.getDefinition(), false);
		p.getLocateableList().add(new GeoNumeric(app.getKernel().getConstruction(),
				0));
		p.getLocateableList().get(0).getAlgoUpdateSet().add(algo);
		algo.setOutput(new GeoElement[]{p});

		try {
			algo.update();
		} catch (StackOverflowError e) {
			fail("This StackOverflowError should not be possible!");
		}
	}

	@Test
	@Issue("APPS-5423")
	public void testUnlabeledRandomGeosCanBeUpdatedMultipleTimesWithinAlgoUpdate() {
		GeoPoint pointA = add("A = (1, 1)");
		add("B = (2, 2)");
		GeoElement poly = add("Polygon(A, B, 4)");
		poly.setUpdateScript(new GgbScript(app,
				"SetValue(A, (1,1))\nSetValue(B, (2, 2))"));
		MoveGeos.moveObjects(List.of(pointA), new Coords(1, 1),
				null, null, app.getActiveEuclidianView());
		assertThat(poly, hasValue("2"));
	}

	private <T extends GeoElementND> T add(String cmd) {
		return (T) app.getKernel().getAlgebraProcessor()
				.processAlgebraCommand(cmd, false)[0];
	}
}
