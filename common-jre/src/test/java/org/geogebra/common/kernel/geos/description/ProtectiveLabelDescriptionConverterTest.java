package org.geogebra.common.kernel.geos.description;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ProtectiveLabelDescriptionConverterTest extends BaseUnitTest {

	private ProtectiveLabelDescriptionConverter converter;

	@Before
	public void setUp() {
		converter = new ProtectiveLabelDescriptionConverter();
	}

	@Test
	public void testDoesNotFilterCaption() {
		String functionString = "g(x) = x";
		GeoFunction function = addAvInput(functionString);
		checkCaption(function, GeoElementND.LABEL_NAME_VALUE, functionString);

		GeoConic conicWithUserEquation = addAvInput("c: x^2 + y^2 = 1");
		checkCaption(conicWithUserEquation, GeoElementND.LABEL_NAME_VALUE,
				"c: x² + y² = 1");
	}

	@Test
	public void testFiltersCaption() {
		GeoLine line = createLineWithCommand();

		checkCaption(line, GeoElementND.LABEL_NAME, "f");
		checkCaption(line, GeoElementND.LABEL_NAME_VALUE, "f: Line(A, B)");
		checkCaption(line, GeoElementND.LABEL_VALUE, "Line(A, B)");
		checkCaption(line, GeoElementND.LABEL_CAPTION, "f");
		checkCaption(line, GeoElementND.LABEL_CAPTION_VALUE, "f: Line(A, B)");
	}

	private GeoLine createLineWithCommand() {
		addAvInput("A = (1, 2)");
		addAvInput("B = (2, 3)");
		return addAvInput("f = Line(A, B)");
	}

	private void checkCaption(GeoElement element, int labelMode, String expectedLabelText) {
		element.setLabelMode(labelMode);
		assertThat(converter.convert(element), is(expectedLabelText));
	}
}