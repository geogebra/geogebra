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
 
package org.geogebra.common.kernel.geos.symbolic.matrix;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.BaseSymbolicTest;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

public class SymbolicMatrixTest extends BaseSymbolicTest {

	@Test
	public void testCreationWithLabel() {
		GeoSymbolic matrix = add("m={{1,2},{3,4}}");
		assertThat(matrix.getTwinGeo(), CoreMatchers.<GeoElementND>instanceOf(GeoList.class));
	}

	@Test
	public void testMatrixDefinitionForIndependent() {
		GeoSymbolic matrix = add("m={{1,2},{3,4}}");
		assertThat(
				matrix.getDefinition(StringTemplate.editTemplate),
				equalTo("{{1, 2}, {3, 4}}"));
		assertThat(
				matrix.getDefinition(StringTemplate.latexTemplate),
				equalTo("\\left(\\begin{array}{rr}1&2\\\\3&4\\\\ \\end{array}\\right)"));
	}

	@Test
	public void testMatrixDefinitionForDependent() {
		add("a = 1");
		GeoSymbolic matrix = add("m={{a,2},{3,4}}");
		assertThat(
				matrix.getDefinition(StringTemplate.editTemplate),
				equalTo("{{a, 2}, {3, 4}}"));
		assertThat(
				matrix.toLaTeXString(false, StringTemplate.latexTemplate),
				equalTo("\\left(\\begin{array}{rr}1&2\\\\3&4\\\\ \\end{array}\\right)"));
	}

	@Test
	public void testIsMatrix() {
		GeoSymbolic matrix = add("m={{a,b},{c,d}}");
		assertThat(matrix.isMatrix(), is(true));
	}

	@Test
	public void testMatrixLatexStringForDependent() {
		GeoSymbolic vector = add("m={{a,b},{c,d}}");
		assertThat(
				vector.toLaTeXString(false, StringTemplate.latexTemplate),
				equalTo("\\left(\\begin{array}{rr}a&b\\\\c&d\\\\ \\end{array}\\right)"));
	}

	@Test
	public void testMatrixLatexStringForDependentWithTwin() {
		add("b=3");
		GeoSymbolic vector = add("m={{b,b}}");
		assertThat(
				vector.toLaTeXString(false, StringTemplate.latexTemplate),
				equalTo("\\left(\\begin{array}{rr}3&3\\\\ \\end{array}\\right)"));
		assertThat(
				vector.toLaTeXString(true, StringTemplate.latexTemplate),
				equalTo("\\left(\\begin{array}{rr}b&b\\\\ \\end{array}\\right)"));
	}

	@Test
	public void testEigenvectorsAsSymbolic() {
		GeoSymbolic eigenvectors = add("e = Eigenvectors({{1,2},{3,4}})");
		StringTemplate template = app.getConfig().getOutputStringTemplate();
		assertThat(
				eigenvectors.getLaTeXDescriptionRHS(true, template),
				equalTo("\\left(\\begin{array}{rr}\\sqrt{33} - 3&-\\sqrt{33} - 3\\\\"
						+ "6&6\\\\ \\end{array}\\right)"));
	}

	@Test
	public void testEigenvectorsAsNonSymbolic() {
		GeoSymbolic eigenvectors = add("e = Eigenvectors({{1,2},{3,4}})");
		eigenvectors.setSymbolicMode(false, false);
		StringTemplate template = app.getConfig().getOutputStringTemplate();
		assertThat(
				eigenvectors.getLaTeXDescriptionRHS(true, template),
				equalTo("\\left(\\begin{array}{rr}2.7445626465&-8.7445626465\\\\"
						+ "6&6\\\\ \\end{array}\\right)"));
	}

	@Test
	public void testIsMatrixNested() {
		GeoSymbolic geo = add("SVD({{1,0},{0,4}})");
		assertThat(geo.isMatrix(), is(false));
		assertThat(geo.toValueString(StringTemplate.latexTemplate),
				is("\\left\\{\\left(\\begin{array}{rr}1&0\\\\0&1\\\\ \\end{array}\\right),\\;"
						+ "\\left(\\begin{array}{rr}1&0\\\\0&4\\\\ \\end{array}\\right),\\;"
						+ "\\left(\\begin{array}{rr}1&0\\\\0&1\\\\ \\end{array}\\right)\\right\\}")
		);
		geo = add("Identity(2)");
		assertThat(geo.isMatrix(), is(true));
		assertThat(geo.toValueString(StringTemplate.latexTemplate),
				is("\\left(\\begin{array}{rr}1&0\\\\0&1\\\\ \\end{array}\\right)"));
		geo = add("Identity(2)*g");
		assertThat(geo.isMatrix(), is(true));
		assertThat(geo.toValueString(StringTemplate.latexTemplate),
				is("\\left(\\begin{array}{rr}g&0\\\\0&g\\\\ \\end{array}\\right)"));
		geo = add("{Identity(2)}");
		assertThat(geo.isMatrix(), is(false));
		assertThat(geo.toValueString(StringTemplate.latexTemplate),
				is("\\left\\{\\left(\\begin{array}{rr}1&0\\\\0&1\\\\"
						+ " \\end{array}\\right)\\right\\}"));
	}

	@Test
	public void testIsMatrixNumeric() {
		GeoSymbolic matrixList = add("{{{1/3}}}");
		matrixList.setSymbolicMode(false, false);
		String input = matrixList.getLaTeXAlgebraDescription(false, StringTemplate.latexTemplate);
		assertThat(input, is("l1\\, = \\,\\left\\{\\left(\\begin{array}{r}\\frac{1}{3}\\\\"
				+ " \\end{array}\\right)\\right\\}"));
		String output = matrixList.getLaTeXAlgebraDescription(true, StringTemplate.latexTemplate);
		assertThat(output, is("l1\\, = \\,\\left\\{\\left(\\begin{array}{r}0.3333333333\\\\"
				+ " \\end{array}\\right)\\right\\}"));
		String plain = matrixList.toValueString(StringTemplate.defaultTemplate);
		assertThat(plain, is("{{{0.3333333333}}}"));
	}
}
