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

package org.geogebra.common.kernel.geos;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.stream.Collectors;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.main.settings.AlgebraStyle;
import org.geogebra.common.plugin.Operation;
import org.geogebra.test.annotation.Issue;
import org.junit.Test;

public class GeoFunctionTest extends BaseUnitTest {

	@Test
	public void testEquals() {
		GeoFunction func1 = addAvInput("f(x)=x+2");
		GeoFunction func2 = addAvInput("g(x)=2+x");
		assertThat(func1.isEqual(func2), is(true));
		assertThat(func2.isEqual(func1), is(true));
		addAvInput("SetValue(f,?)");
		assertThat(func1.isEqual(func2), is(false));
		assertThat(func2.isEqual(func1), is(false));
	}

	@Test
	public void testIntervalsOnesided() {
		ExpressionNode less = new ExpressionNode(getKernel(), new FunctionVariable(getKernel()),
				Operation.LESS, new MyDouble(getKernel(), 4));
		ExpressionNode more = new ExpressionNode(getKernel(), new FunctionVariable(getKernel()),
				Operation.GREATER, new MyDouble(getKernel(), 3));
		double[] bounds = new double[2];
		GeoIntervalUtil.updateBoundaries(less, bounds);
		assertArrayEquals(new double[]{Double.NEGATIVE_INFINITY, 4}, bounds, .01);
		GeoIntervalUtil.updateBoundaries(more, bounds);
		assertArrayEquals(new double[]{3, Double.POSITIVE_INFINITY}, bounds, .01);
	}

	@Test
	public void testIntervals() {
		ExpressionNode less = new ExpressionNode(getKernel(), new FunctionVariable(getKernel()),
				Operation.LESS, new MyDouble(getKernel(), 4));
		ExpressionNode more = new ExpressionNode(getKernel(), new FunctionVariable(getKernel()),
				Operation.GREATER, new MyDouble(getKernel(), 3));
		ExpressionNode interval = new ExpressionNode(getKernel(), less, Operation.AND, more);
		double[] bounds = new double[2];
		GeoIntervalUtil.updateBoundaries(interval, bounds);
		assertArrayEquals(new double[]{3, 4}, bounds, .01);
	}

	@Test
	public void formulaShouldBeUndefinedForUndefinedFunctions() {
		GeoFunction fn = add("h(x)=Element({x^(3)-3 x},2)");
		assertEquals("?", fn.getFormulaString(StringTemplate.latexTemplate, false));
		assertEquals("?", fn.getFormulaString(StringTemplate.latexTemplate, true));
	}

	@Test
	public void shouldRegisterParentFunctionVar() {
		add("f(k)=k^2");
		assertThat(add("f'(k)"), hasValue("2k"));
		assertThat(add("f(k+1)"), hasValue("(k + 1)²"));
	}

	@Test
	public void shouldNotRegisterParentFunctionVarIfExplicit() {
		add("f(n)=n^2");
		GeoFunction g = add("g(t)=If(t<1,f(t)+1,7)");
		assertThat(g, hasValue("If(t < 1, t² + 1, 7)"));
		assertThat(g.getVarString(StringTemplate.defaultTemplate), equalTo("t"));
	}

	@Test
	public void conditionalFunctionShouldBeDefined() {
		add("a=1");
		add("c=?");
		add("d=1");
		GeoFunction f = add("f(x)=d+If(x>a,3,c)");
		t("f(5)", "4");
		t("f(-5)", "NaN");
		assertEquals("d", f.getFunctionExpression().getUnconditionalVars(new HashSet<>())
				.stream().map(GeoElement::getLabelSimple).collect(Collectors.joining()));
	}

	@Test
	@Issue("APPS-6871")
	public void testSqrt() {
		add("f(x)=sqrt(x)");
		GeoElement sum = add("g:f+f");
		getApp().getSettings().getAlgebra().setStyle(AlgebraStyle.LINEAR_NOTATION);
		assertEquals("g(x) = sqrt(x) + sqrt(x)", sum.getAlgebraDescriptionDefault());
		assertEquals("sqrt(x) + sqrt(x)", sum.getAlgebraDescriptionRHS());
	}

	@Test
	@Issue("APPS-7282")
	public void testSimplifyZeroCoefficient() {
		GeoFunction f = add("f(x)=x^2+0x-0x+1");
		assertEquals("x² + 1", f.toValueString(StringTemplate.defaultTemplate));
	}

	@Test
	@Issue("APPS-7282")
	public void zeroCoefficientsShouldStayWhenSimplificationDisabled() {
		GeoFunction f = add("f(x)=x^2+0x-0x+1");
		f.setSimplifyCoefficients(false);
		assertEquals("x² + 0x - 0x + 1", f.toValueString(StringTemplate.defaultTemplate));
	}

	@Test
	@Issue("APPS-7282")
	public void testSimplifyUnitCoefficientsInValueString() {
		GeoFunction f = add("f(x)=1x-1x^2");
		assertEquals("x - x²", f.toValueString(StringTemplate.defaultTemplate));
	}

	@Test
	@Issue("APPS-7282")
	public void reloadShouldSimplifyCoefficients() {
		add("f(x)=x^2+0x-1x+1");
		reload();
		GeoElement reloaded = lookup("f");
		assertEquals("x² - x + 1", reloaded.toValueString(StringTemplate.defaultTemplate));
	}

	@Test
	@Issue("APPS-7282")
	public void reloadShouldNotSimplifyCoefficients() {
		GeoFunction f = add("f(x)=x^2+0x-1x+1");
		f.setSimplifyCoefficients(false);
		reload();
		GeoElement reloaded = lookup("f");
		assertEquals("x² + 0x - 1x + 1", reloaded.toValueString(StringTemplate.defaultTemplate));
		assertThat(getApp().getXML(), containsString("<simplifyCoefficients val=\"false\"/>"));
	}

	@Test
	@Issue("APPS-7282")
	public void nonPolynomialShouldNotBeSimplified() {
		GeoFunction f = add("f(x)=1x+0*ln(x)");
		assertEquals("x + 0ln(x)", f.toValueString(StringTemplate.defaultTemplate));
	}

	@Test
	@Issue("APPS-7282")
	public void polynomialWithinArgumentShouldBeSimplified() {
		GeoFunction f = add("f(x)=sqrt(0x)");
		GeoFunction g = add("g(x)=sqrt(0x + 1)");
		assertEquals("sqrt(0)", f.toValueString(StringTemplate.defaultTemplate));
		assertEquals("sqrt(1)", g.toValueString(StringTemplate.defaultTemplate));
	}

	@Test
	@Issue("APPS-7282")
	public void multipleZeroCoefficientsShouldBeSimplified() {
		GeoFunction f = add("f(x)=0x-0x+0x*0x+2x");
		assertEquals("2x", f.toValueString(StringTemplate.defaultTemplate));
	}

	@Test
	@Issue("APPS-7282")
	public void fractionsShouldBeSimplifiedCorrectly() {
		GeoFunction f = add("f(x)=(0x)/(0x)");
		GeoFunction g = add("g(x)=(1x)/(1x)");
		assertEquals("(0) / (0)", f.toValueString(StringTemplate.defaultTemplate));
		assertEquals("(x) / (x)", g.toValueString(StringTemplate.defaultTemplate));
	}

	@Test
	@Issue("APPS-7282")
	public void multipleNegationsShouldBeSimplifiedCorrectly() {
		GeoFunction f = add("f(x)=-(-1x)");
		GeoFunction g = add("g(x)=--1x");
		GeoFunction h = add("h(x)=---1x + 3x");
		GeoFunction i = add("i(x)=-(-(-(-1x))) + 1x");
		assertEquals("x", f.toValueString(StringTemplate.defaultTemplate));
		assertEquals("x", g.toValueString(StringTemplate.defaultTemplate));
		assertEquals("-x + 3x", h.toValueString(StringTemplate.defaultTemplate));
		assertEquals("x + x", i.toValueString(StringTemplate.defaultTemplate));
	}

	@Test
	@Issue("APPS-7282")
	public void zeroCoefficientShouldNotBeOmittedIfNotEqualToZero() {
		GeoFunction f = add("f(x)=1x+0.001x^7");
		assertEquals("x + 0x⁷", f.toValueString(StringTemplate.defaultTemplate));
	}
}
