package org.geogebra.common.kernel.geos;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.junit.Test;

public class GeoVectorTest extends BaseUnitTest {

	@Test
	public void testEditorDefinitionForIndependent() {
		GeoVector vector = addAvInput("v = (1, 2)");
		assertThat(
				vector.getDefinition(StringTemplate.editorTemplate),
				is("{{1}, {2}}"));
	}

	@Test
	public void testLatexDefinitionForIndependent() {
		GeoVector vector = addAvInput("v = (1, 2)");
		assertThat(
				vector.getDefinition(StringTemplate.latexTemplate),
				is("\\left( \\begin{align}1 \\\\ 2 \\end{align} \\right)"));
	}

	@Test
	public void testEditorDefinitionForDependent() {
		addAvInput("a = 1");
		GeoVector vector = addAvInput("v = (a, 2)");
		assertThat(
				vector.getDefinition(StringTemplate.editorTemplate),
				is("{{a}, {2}}"));
	}

	@Test
	public void testLatexDefinitionForDependent() {
		addAvInput("a = 1");
		GeoVector vector = addAvInput("v = (a, 2)");
		assertThat(
				vector.getDefinition(StringTemplate.latexTemplate),
				is("\\left( \\begin{align}a \\\\ 2 \\end{align} \\right)"));
	}

	@Test
	public void testDefaultHeadShouldNotAppearInXml() {
		GeoVector vector = addAvInput("v = (1, 2)");
		assertThat(vector.getXML(), not(containsString("\t<headStyle")));
	}

	@Test
	public void testGetXmlWithArrowHead() {
		GeoVector vector = addAvInput("v = (1, 2)");
		vector.setHeadStyle(VectorHeadStyle.ARROW);
		assertThat(vector.getXML(), containsString("\t<headStyle val=\"1\"/>"));
	}
}
