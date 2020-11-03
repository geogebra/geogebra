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
				matrix.getDefinition(StringTemplate.latexTemplate),
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
}
