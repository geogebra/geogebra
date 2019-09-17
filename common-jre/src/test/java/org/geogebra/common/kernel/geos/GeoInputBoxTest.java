package org.geogebra.common.kernel.geos;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.properties.TextAlignment;
import org.geogebra.common.main.App;
import org.junit.Assert;
import org.junit.Test;

public class GeoInputBoxTest extends BaseUnitTest {

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
		Assert.assertEquals("", inputBox.getTextForEditor());
	}

	@Test
	public void testForComplexUndefinedGeo() {
		add("a=1");
		add("b=?a");
		GeoInputBox inputBox = (GeoInputBox) add("InputBox(b)");
		inputBox.setSymbolicMode(true, false);
		Assert.assertEquals("?a", inputBox.getText());
		Assert.assertEquals("?a", inputBox.getTextForEditor());


		inputBox.setSymbolicMode(false, false);
		Assert.assertEquals("?a", inputBox.getText());
		Assert.assertEquals("?a", inputBox.getTextForEditor());
	}

	@Test
	public void testForEmptyInput() {

	}

	@Test
	public void testForUndefinedInputInput() {

	}

	@Test
	public void testInputForGeoText() {
		add("text = \"?\" ");
		GeoInputBox inputBox = (GeoInputBox) add("InputBox(text)");

		inputBox.setSymbolicMode(true, false);
		Assert.assertEquals("\"?\"", inputBox.getText());
		Assert.assertEquals("\"?\"", inputBox.getTextForEditor());


		inputBox.setSymbolicMode(false, false);
		Assert.assertEquals("\"?\"", inputBox.getText());
		Assert.assertEquals("\"?\"", inputBox.getTextForEditor());
	}
}
