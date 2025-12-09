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

package org.geogebra.common.kernel.algos;

import static org.geogebra.common.BaseUnitTest.hasValue;
import static org.geogebra.test.TestStringUtil.unicode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.IneqTree;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.MoveGeos;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.script.GgbScript;
import org.geogebra.test.BaseAppTestSetup;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class AlgoElementTest extends BaseAppTestSetup {
	@BeforeEach
	void setupApp() {
		setupApp(SuiteSubApp.G3D);
	}

	@ParameterizedTest
	@CsvSource(value = {"Integral(x,1,2);\\int\\limits_{1}^{2}x\\,\\mathrm{d}x",
			"Integral(x);\\int x\\,\\mathrm{d}x",
			"Integral(f,1,2);\\int\\limits_{1}^{2}f\\,\\mathrm{d}n",
			"Integral(x, 1, 2, false);\\int\\limits_{1}^{2}x\\,\\mathrm{d}x",
			"Integral(f);\\int f\\,\\mathrm{d}n",
			"Sequence(Integral(x^k),k,1,2);"
					+ "Sequence\\left(\\int x^{k}\\,\\mathrm{d}x, k, 1, 2 \\right)"},
			delimiterString = ";")
	@Issue("APPS-4732")
	public void latexIntegral(String cmd, String latex) {
		evaluate("f(n)=n^2");
		assertEquals(latex, evaluateGeoElement(cmd).getDefinition(StringTemplate.latexTemplate));
	}

	@ParameterizedTest
	@CsvSource(value = {"Sum(n-k,n,1,3);\\sum_{n=1}^{3}\\left(n - k \\right)",
			"Sum(n,n,1,3);\\sum_{n=1}^{3}n",
			"Sum(n^k,n,1,3);\\sum_{n=1}^{3}n^{k}",
			"Sum(sin(n),n,1,3);\\sum_{n=1}^{3}\\operatorname{sin} \\left( n \\right)"},
			delimiterString = ";")
	public void latexSum(String cmd, String latex) {
		getKernel().setSymbolicMode(SymbolicMode.SYMBOLIC_AV);
		assertEquals(latex, evaluateGeoElement(cmd).getDefinition(StringTemplate.latexTemplate));
	}

	@Test
	public void testSequenceInequality() {
		evaluate("a: y<=x^(2) && y>= 0 && x>=0 && x<=1");
		GeoList list = evaluateGeoElement("{a(x+1,y), a(x+2,y)}");
		GeoList seq = evaluateGeoElement("l1=Sequence(a(x+i,y),i,1,2)");
		GeoList elements = evaluateGeoElement("{Element[l1,1], Element[l1,2]}");
		String listValue = list.toValueString(StringTemplate.defaultTemplate);
		assertThat(seq, hasValue(listValue));
		assertThat(elements, hasValue(listValue));
	}

	@Test
	public void testSequenceIneqTree() {
		evaluate("a: y<=x^(2) && y>= 0 && x>=0 && x<=1");
		GeoList sequence = evaluateGeoElement("Sequence(a(x+i,y),i,1,2)");
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
			"Integral(t-x,a,b);\\int\\limits_{a}^{b}t - x\\,\\mathrm{d}x",
			"Integral(t,a,b);\\int\\limits_{a}^{b}t\\,\\mathrm{d}t"},
			delimiterString = ";")
	@Issue("APPS-5345")
	public void latexIntegralShouldHaveCorrectDerivativeVariable(String cmd, String latex) {
		getKernel().setSymbolicMode(SymbolicMode.SYMBOLIC_AV);
		assertEquals(latex, evaluateGeoElement(cmd).getDefinition(StringTemplate.latexTemplate));
	}

	@Test
	@Issue("APPS-5423")
	public void testAlgoDependentPointShouldNotUpdateEndlessly() {
		GeoPoint p = evaluateGeoElement("(1, 1)");
		AlgoDependentPoint algo = new AlgoDependentPoint(p.getConstruction(),
				p.getDefinition(), false);
		p.getLocateableList().add(new GeoNumeric(getKernel().getConstruction(), 0));
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
		GeoPoint pointA = evaluateGeoElement("A = (1, 1)");
		evaluate("B = (2, 2)");
		GeoElement poly = evaluateGeoElement("Polygon(A, B, 4)");
		poly.setUpdateScript(new GgbScript(getApp(), "SetValue(A, (1,1))\nSetValue(B, (2, 2))"));
		MoveGeos.moveObjects(List.of(pointA), new Coords(1, 1),
				null, null, getApp().getActiveEuclidianView());
		assertThat(poly, hasValue("2"));
	}
}
