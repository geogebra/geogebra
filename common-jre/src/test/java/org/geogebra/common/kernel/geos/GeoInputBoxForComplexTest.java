package org.geogebra.common.kernel.geos;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;

public class GeoInputBoxForComplexTest extends BaseUnitTest {

	@Test
	public void rootOfMinusOneShouldBeImaginaryWithComplexNumber() {
		add("z_1 = 3 + 2i");
		shouldBeUpdatedAs("z_1", "sqrt(-1)", Unicode.IMAGINARY + "");
//		shouldBeUpdatedAs("z_1", "sqrt(11)", "sqrt(11)");
//		shouldBeUpdatedAs("z_1", "sqrt(   -1)", Unicode.IMAGINARY + "");
	}

	@Test
	public void rootOfMinusOneShouldBeImaginaryComplexWithVector() {
		add("u = (3, 2i)");
		shouldBeUpdatedAs("u", "(1, sqrt(-1))", "(1, sqrt("
				+ Unicode.IMAGINARY + "))");
	}


	private void shouldBeUpdatedAs(String linkedGeo, String updatedText, String expected) {
		GeoInputBox inputBox = addAvInput("ib = InputBox(" + linkedGeo + ")");
		inputBox.updateLinkedGeo(updatedText);
		assertEquals(expected, inputBox.getText());
	}
}
