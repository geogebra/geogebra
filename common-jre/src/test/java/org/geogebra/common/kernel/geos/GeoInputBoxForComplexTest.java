package org.geogebra.common.kernel.geos;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;

public class GeoInputBoxForComplexTest extends BaseUnitTest {

	public static final String IMAGINARY_UNIT = String.valueOf(Unicode.IMAGINARY);

	@Test
	public void rootOfMinusOneShouldBeImaginaryWithComplexNumber() {
		add("z_1 = 3 + 2i");
		shouldBeUpdatedAs("sqrt(-1)", IMAGINARY_UNIT);
	}

	@Test
	public void rootOfMinusOneShouldBeUsedInExpression() {
		add("z_1 = 1 + 6i");
		shouldBeUpdatedAs("2 + 3sqrt(-1)", "2 + 3" + IMAGINARY_UNIT);
	}

	@Test
	public void sinShouldBeTyped() {
		add("z_1 = 3+2i");
		shouldBeUpdatedAs("sin45", "sin(45)");
	}

	private void shouldBeUpdatedAs(String updatedText, String expected) {
		GeoInputBox inputBox = addAvInput("ib = InputBox(z_1)");
		inputBox.updateLinkedGeo(updatedText);
		assertEquals(expected, inputBox.getText());
	}
}
