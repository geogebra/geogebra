package org.geogebra.common.kernel.geos.description;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
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

		String pointString = "A = (1, 2)";
		GeoPoint point = addAvInput(pointString);
		checkCaption(point, GeoElementND.LABEL_NAME_VALUE, pointString);

		String dependentCopyString = "eq1 = c";
		GeoConic line = addAvInput(dependentCopyString);
		checkCaption(line, GeoElementND.LABEL_NAME_VALUE, "eq1: x² + y² = 1");
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

	@Test
	public void testFiltersCaptionDependentCopy() {
		GeoLine line = createLineWithCommand();
		String dependentCopyString = "g:f";
		GeoLine lineCopy = addAvInput(dependentCopyString);

		checkCaption(lineCopy, GeoElementND.LABEL_NAME, "g");
		checkCaption(lineCopy, GeoElementND.LABEL_NAME_VALUE, "g: f");
		checkCaption(lineCopy, GeoElementND.LABEL_VALUE, "f");
		checkCaption(lineCopy, GeoElementND.LABEL_CAPTION, "g");
		checkCaption(lineCopy, GeoElementND.LABEL_CAPTION_VALUE, "g: f");
	}

	@Test
	public void testFiltersCaptionDependentCopySecondLevel() {
		createLineWithCommand();
		addAvInput("g:f");
		GeoLine lineSecondLevel = addAvInput("h:g");

		checkCaption(lineSecondLevel, GeoElementND.LABEL_NAME, "h");
		checkCaption(lineSecondLevel, GeoElementND.LABEL_NAME_VALUE, "h: g");
		checkCaption(lineSecondLevel, GeoElementND.LABEL_VALUE, "g");
		checkCaption(lineSecondLevel, GeoElementND.LABEL_CAPTION, "h");
		checkCaption(lineSecondLevel, GeoElementND.LABEL_CAPTION_VALUE, "h: g");
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