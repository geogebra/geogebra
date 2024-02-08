package org.geogebra.common.kernel.geos;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.junit.Assert;
import org.junit.Test;

public class GeoTextTest extends BaseUnitTest {

	@Test
	public void definitionForEditorShouldBeTheStringItself() {
		String value = "GeoGebra rocks";
		GeoText text = new GeoText(getConstruction(), value);
		Assert.assertEquals(value, text.getDefinitionForEditor());
	}

	@Test
	public void copyAbsLocFlag() {
		GeoText text = new GeoText(getConstruction(), "text");
		text.setAbsoluteScreenLocActive(true);
		GeoText textCopy = new GeoText(getConstruction(), "textCopy");
		textCopy.setAllVisualPropertiesExceptEuclidianVisible(text, true, true);
		Assert.assertTrue(textCopy.isAbsoluteScreenLocActive());
	}

	@Test
	public void alignmentForUndefinedText() {
		GeoText text = add("Text(If(false,7),(1,1),false,false,0,0)");
		assertThat(getDrawable(text), notNullValue());
	}
}
