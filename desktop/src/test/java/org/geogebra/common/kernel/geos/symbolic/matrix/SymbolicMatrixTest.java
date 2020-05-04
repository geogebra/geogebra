package org.geogebra.common.kernel.geos.symbolic.matrix;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.kernel.geos.GeoSymbolicTest;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class SymbolicMatrixTest extends GeoSymbolicTest {

	@Test
	public void testCreationWithLabel() {
		GeoSymbolic matrix = add("m={{1,2},{3,4}}");
		assertThat(matrix.getTwinGeo(), CoreMatchers.<GeoElementND>instanceOf(GeoList.class));
	}

	@Test
	public void testMatrixDefinitionForIndependent() {
		GeoSymbolic matrix = add("m={{1,2},{3,4}}");
		assertThat(
				matrix.getDefinition(StringTemplate.editorTemplate),
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
				matrix.getDefinition(StringTemplate.editorTemplate),
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
}
