package org.geogebra.common.kernel.geos;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;

public class GeoListTest extends BaseUnitTest {

	@Test
	public void latexValueStringShouldContainValues() {
		add("a=1");
		GeoList matrix = add("{{a,2},{a+2,4}}");
		assertEquals("\\left(\\begin{array}{rr}1&2\\\\3&4\\\\ \\end{array}\\right)",
				matrix.toLaTeXString(false, StringTemplate.latexTemplate));
	}

	@Test
	public void latexDefinitionStringShouldContainLabels() {
		add("a=1");
		GeoList matrix = add("{{a,2},{a+2,4}}");
		assertEquals("\\left(\\begin{array}{rr}a&2\\\\a + 2&4\\\\ \\end{array}\\right)",
				matrix.toLaTeXString(true, StringTemplate.latexTemplate));
	}

	@Test
	public void matrixDefinitionShouldWorkForSequenceOperator() {
		add("a=3");
		GeoList matrix = add("{0..a}");
		assertEquals("\\left(\\begin{array}{rrrr}0&1&2&3\\\\ \\end{array}\\right)",
				matrix.toLaTeXString(true, StringTemplate.latexTemplate));
		assertEquals("\\left\\{0" + Unicode.ELLIPSIS + "a\\right\\}",
				matrix.getDefinition().unwrap()
						.toString(StringTemplate.latexTemplate));
	}
}
