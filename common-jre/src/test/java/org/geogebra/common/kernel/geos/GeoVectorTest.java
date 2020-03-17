package org.geogebra.common.kernel.geos;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class GeoVectorTest extends BaseUnitTest {

	@Test
	public void testDefinitionForIndependent() {
		GeoVector vector = addAvInput("v = (1, 2)");
		assertThat(
				vector.getDefinition(StringTemplate.editorTemplate),
				is("{{1}, {2}}"));
		assertThat(
				vector.getDefinition(StringTemplate.latexTemplate),
				is("\\left( \\begin{align}1 \\\\ 2 \\end{align} \\right)"));
	}

	@Test
	public void testDefinitionForDependent() {
		addAvInput("a = 1");
		GeoVector vector = addAvInput("v = (a, 2)");
		assertThat(
				vector.getDefinition(StringTemplate.editorTemplate),
				is("{{a}, {2}}"));
		assertThat(
				vector.getDefinition(StringTemplate.latexTemplate),
				is("\\left( \\begin{align}a \\\\ 2 \\end{align} \\right)"));
	}
}
