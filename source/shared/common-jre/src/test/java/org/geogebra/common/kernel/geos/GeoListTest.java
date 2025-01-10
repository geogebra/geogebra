package org.geogebra.common.kernel.geos;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.Objects;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GGraphicsCommon;
import org.geogebra.common.euclidian.draw.dropdown.DrawDropDownList;
import org.geogebra.common.kernel.StringTemplate;
import org.junit.Before;
import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;

public class GeoListTest extends BaseUnitTest {

	private StringTemplate latexTemplate;
	private StringTemplate engineeringNotationTemplate;

	@Before
	public  void setupTemplate() {
		latexTemplate = StringTemplate.latexTemplate;
		engineeringNotationTemplate = StringTemplate.defaultTemplate
				.deriveWithEngineeringNotation();
	}

	@Test
	public void latexValueStringShouldContainValues() {
		add("a=1");
		GeoList matrix = add("{{a,2},{a+2,4}}");
		assertEquals("\\left(\\begin{array}{rr}1&2\\\\3&4\\\\ \\end{array}\\right)",
				matrix.toLaTeXString(false, latexTemplate));
	}

	@Test
	public void latexDefinitionStringShouldContainLabels() {
		add("a=1");
		GeoList matrix = add("{{a,2},{a+2,4}}");
		assertEquals("\\left(\\begin{array}{rr}a&2\\\\a + 2&4\\\\ \\end{array}\\right)",
				matrix.toLaTeXString(true, latexTemplate));
	}

	@Test
	public void matrixDefinitionShouldWorkForSequenceOperator() {
		add("a=3");
		GeoList matrix = add("{0..a}");
		assertEquals("\\left(\\begin{array}{rrrr}0&1&2&3\\\\ \\end{array}\\right)",
				matrix.toLaTeXString(true, latexTemplate));
		assertEquals("\\left\\{0" + Unicode.ELLIPSIS + "a\\right\\}",
				matrix.getDefinition().unwrap().toString(latexTemplate));
	}

	@Test
	public void setShouldCopyLabeledElements() {
		GeoList allLists = add("allLists={}");
		add("c=1");
		allLists.set(add("{{c}}")); // equivalent to SetValue(allLists,{{c}})
		add("SetValue(c,42)");
		StringBuilder sb = new StringBuilder();
		allLists.getExpressionXML(sb);
		assertThat(sb.toString(), is("<expression label=\"allLists\" exp=\"{{1}}\"/>\n"));
	}

	@Test
	public void shouldBeDrawableIfNotSelected() {
		add("a=5");
		GeoList list = add("Sequence(a)");
		list.setDrawAsComboBox(true);
		list.setEuclidianVisible(true);
		list.setSelectedIndex(4);
		list.updateRepaint();
		DrawDropDownList drawList = (DrawDropDownList) getDrawable(list);
		Objects.requireNonNull(drawList).toggleOptions();
		add("SetValue(a,1)");
		drawList.draw(new GGraphicsCommon());
		assertThat(list.getSelectedElement(), hasValue("1"));
	}

	@Test
	public void listShouldDisplayCorrectEngineeringNotation1() {
		GeoList list = add("{1, 2, 3}");
		assertThat(list.get(0).toValueString(engineeringNotationTemplate),
				is("1 " + Unicode.CENTER_DOT + " 10" + Unicode.SUPERSCRIPT_0));
	}

	@Test
	public void listShouldDisplayCorrectEngineeringNotation2() {
		GeoList list = add("{1 / 2, 2 / 4}");
		assertThat(list.get(1).toValueString(engineeringNotationTemplate),
				is("500 " + Unicode.CENTER_DOT + " 10"
						+ Unicode.SUPERSCRIPT_MINUS + Unicode.SUPERSCRIPT_3));
	}

	@Test
	public void listShouldDisplayCorrectEngineeringNotation3() {
		GeoList list = add("{3, ?}");
		assertThat(list.get(1).toValueString(engineeringNotationTemplate), is("?"));
	}
}
