package org.geogebra.common.kernel.geos;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.properties.TextAlignment;
import org.geogebra.common.main.App;
import org.geogebra.common.util.TextObject;
import org.junit.Assert;
import org.junit.Test;

public class GeoInputBoxTest extends BaseUnitTest {

	private TextObject textObject = new TextObject() {
		String content;

		@Override
		public String getText() {
			return content;
		}

		@Override
		public void setText(String s) {
			content = s;
		}

		@Override
		public void setVisible(boolean b) {

		}

		@Override
		public void setEditable(boolean b) {

		}
	};

	@Test
	public void symbolicInputBoxUseDefinitionForFunctions() {
		add("f = x+1");
		add("g = 2f(x+2)+1");
		GeoInputBox inputBox1 = (GeoInputBox) add("InputBox(f)");
		GeoInputBox inputBox2 = (GeoInputBox) add("InputBox(g)");
		inputBox2.setSymbolicMode(true, false);
		Assert.assertEquals("x + 1", inputBox1.getText());
		Assert.assertEquals("2f(x + 2) + 1", inputBox2.getTextForEditor());
	}

	@Test
	public void symbolicInputBoxUseDefinitionForFunctionsNVar() {
		add("f = x*y+1");
		add("g = 2f(x+2, y)+1");
		GeoInputBox inputBox1 = (GeoInputBox) add("InputBox(f)");
		GeoInputBox inputBox2 = (GeoInputBox) add("InputBox(g)");
		inputBox2.setSymbolicMode(true, false);
		Assert.assertEquals("x y + 1", inputBox1.getText());
		Assert.assertEquals("2f(x + 2, y) + 1", inputBox2.getTextForEditor());
		Assert.assertEquals("2 \\; f\\left(x + 2, y \\right) + 1",
				inputBox2.getText());
	}

    @Test
    public void symbolicInputBoxTextShouldBeInLaTeX() {
        add("f = x + 12");
        add("g = 2f(x + 1) + 2");
        GeoInputBox inputBox2 = (GeoInputBox) add("InputBox(g)");
        inputBox2.setSymbolicMode(true, false);
        Assert.assertEquals("2 \\; f\\left(x + 1 \\right) + 2", inputBox2.getText());
    }

    @Test
    public void inputBoxTextAlignmentIsInXMLTest() {
        App app = getApp();
        add("A = (1,1)");
        GeoInputBox inputBox = (GeoInputBox) add("B = Inputbox(A)");
        Assert.assertEquals(TextAlignment.LEFT, inputBox.getAlignment());
        inputBox.setAlignment(TextAlignment.CENTER);
        Assert.assertEquals(TextAlignment.CENTER, inputBox.getAlignment());
        String appXML = app.getXML();
        app.setXML(appXML, true);
        inputBox = (GeoInputBox) lookup("B");
        Assert.assertEquals(TextAlignment.CENTER, inputBox.getAlignment());
    }

	@Test
	public void testForSimpleUndefinedGeo() {
		add("a=?");
		GeoInputBox inputBox = (GeoInputBox) add("InputBox(a)");
		inputBox.setSymbolicMode(true, false);
		Assert.assertEquals("", inputBox.getText());
		Assert.assertEquals("", inputBox.getTextForEditor());

		inputBox.setSymbolicMode(false, false);
		Assert.assertEquals("", inputBox.getText());

	}

	@Test
	public void testForComplexUndefinedGeo() {
		add("a=1");
		add("b=?a");

		GeoInputBox inputBox = (GeoInputBox) add("InputBox(b)");
		inputBox.setSymbolicMode(true, false);
		Assert.assertEquals("? \\; a", inputBox.getText());
		Assert.assertEquals("?a", inputBox.getTextForEditor());

		inputBox.setSymbolicMode(false, false);
		Assert.assertEquals("?a", inputBox.getText());
	}

	@Test
	public void testForEmptyInput() {
		add("a=1");

		GeoInputBox inputBox = (GeoInputBox) add("InputBox(a)");
		inputBox.setSymbolicMode(true, false);

		textObject.setText("");
		inputBox.textObjectUpdated(textObject);

		Assert.assertEquals("", inputBox.getText());
		Assert.assertEquals("", inputBox.getTextForEditor());

		inputBox.setSymbolicMode(false, false);

		Assert.assertEquals("", inputBox.getText());
	}

	@Test
	public void testForUndefinedInputInput() {
		add("a=1");

		GeoInputBox inputBox = (GeoInputBox) add("InputBox(a)");
		inputBox.setSymbolicMode(true, false);

		textObject.setText("?");
		inputBox.textObjectUpdated(textObject);

		Assert.assertEquals("", inputBox.getText());
		Assert.assertEquals("", inputBox.getTextForEditor());

		inputBox.setSymbolicMode(false, false);

		Assert.assertEquals("", inputBox.getText());
	}

	@Test
	public void testInputForGeoText() {
		add("text = \"?\" ");
		GeoInputBox inputBox = (GeoInputBox) add("InputBox(text)");

		inputBox.setSymbolicMode(true, false);
		Assert.assertEquals("?", inputBox.getText());
		Assert.assertEquals("?", inputBox.getTextForEditor());

		inputBox.setSymbolicMode(false, false);
		Assert.assertEquals("?", inputBox.getText());
	}

	@Test
	public void testCanBeSymbolicForNVarFunction() {
		add("f(x, y) = x + y");
		GeoInputBox inputBox = (GeoInputBox) add("InputBox(f)");
		Assert.assertTrue(inputBox.canBeSymbolic());
	}

	@Test
	public void testCanBeSymbolicForBooleanFunction() {
		add("f(x, y) = x == y");
		GeoInputBox inputBox = (GeoInputBox) add("InputBox(f)");
		Assert.assertTrue(inputBox.canBeSymbolic());
	}

	@Test
	public void testCanBeSymbolicForLine() {
		add("A = (0,0)");
		add("B = (2,2)");
		add("f:Line(A,B)");
		GeoInputBox inputBox = (GeoInputBox) add("InputBox(f)");
		Assert.assertTrue(inputBox.canBeSymbolic());
	}
}
