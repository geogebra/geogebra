package org.geogebra.common.kernel.geos.description;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.settings.config.AppConfigGraphing;
import org.junit.Before;
import org.junit.Test;

public class ProtectiveLabelDescriptionConverterTest extends BaseUnitTest {

	private ProtectiveLabelDescriptionConverter converter;

	@Before
	public void setUp() {
		converter = new ProtectiveLabelDescriptionConverter();
	}

	@Test
	public void testDoesNotFilterCaption() {
		getApp().setConfig(new AppConfigGraphing());
		getApp().getKernel().setPrintDecimals(2);
		String functionString = "g(x) = x";
		GeoFunction function = addAvInput(functionString);
		checkCaption(function, GeoElementND.LABEL_NAME_VALUE, functionString);

		GeoConic conicWithUserEquation = addAvInput("c: x^2 + y^2 = 1");
		checkCaption(conicWithUserEquation, GeoElementND.LABEL_NAME_VALUE,
				"c: x² + y² = 1");

		String pointString = "A = (1, 2)";
		GeoPoint point = addAvInput(pointString);
		checkCaption(point, GeoElementND.LABEL_NAME_VALUE, pointString);

		String lineString = "line : Line((-2, 1), (1, 2))";
		GeoLine geoLine = addAvInput(lineString);
		checkCaption(geoLine, GeoElementND.LABEL_NAME_VALUE, "line: y = 0.33x + 1.67");

		String dependentCopyString = "eq1 = c";
		GeoConic line = addAvInput(dependentCopyString);
		checkCaption(line, GeoElementND.LABEL_NAME_VALUE, "eq1: x² + y² = 1");

		String fitLineXString = "f : FitLineX({(-2, 1), (1, 2), (2, 4), (4, 3), (5, 4)})";
		GeoLine fitLineX = addAvInput(fitLineXString);
		checkCaption(fitLineX, GeoElementND.LABEL_NAME_VALUE, "f: y = 0.57x + 1.67");

		String fitLineString = "h : FitLine({(-2, 1), (1, 2), (2, 4), (4, 3), (5, 4)})";
		GeoLine fitLine = addAvInput(fitLineString);
		checkCaption(fitLine, GeoElementND.LABEL_NAME_VALUE, "h: y = 0.4x + 2");

		String fitPolyString = "i(x)=FitPoly({(-1,-1),(0,1),(1,1),(2,5)},3)";
		GeoFunction fitPoly = addAvInput(fitPolyString);
		checkCaption(fitPoly, GeoElementND.LABEL_NAME_VALUE, "i(x) = x³ - x² + 0x + 1");

		String fitLogString = "j(x)=FitLog({(ℯ,1),(ℯ^(2),4)})";
		GeoFunction fitLog = addAvInput(fitLogString);
		checkCaption(fitLog, GeoElementND.LABEL_NAME_VALUE, "j(x) = -2 + 3ln(x)");

		String fitPowString = "k(x)=FitPow({(1,1),(3,2),(7,4)})";
		GeoFunction fitPow = addAvInput(fitPowString);
		checkCaption(fitPow, GeoElementND.LABEL_NAME_VALUE, "k(x) = 0.97x^0.71");
	}

	@Test
	public void testFiltersCaption() {
		GeoLine line = createRayWithCommand();

		checkCaption(line, GeoElementND.LABEL_NAME, "f");
		checkCaption(line, GeoElementND.LABEL_NAME_VALUE, "f: Ray(A, B)");
		checkCaption(line, GeoElementND.LABEL_VALUE, "Ray(A, B)");
		checkCaption(line, GeoElementND.LABEL_CAPTION, "f");
		checkCaption(line, GeoElementND.LABEL_CAPTION_VALUE, "f: Ray(A, B)");
	}

	@Test
	public void testFiltersCaptionDependentCopy() {
		createRayWithCommand();
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
		createRayWithCommand();
		addAvInput("g:f");
		GeoLine lineSecondLevel = addAvInput("h:g");

		checkCaption(lineSecondLevel, GeoElementND.LABEL_NAME, "h");
		checkCaption(lineSecondLevel, GeoElementND.LABEL_NAME_VALUE, "h: g");
		checkCaption(lineSecondLevel, GeoElementND.LABEL_VALUE, "g");
		checkCaption(lineSecondLevel, GeoElementND.LABEL_CAPTION, "h");
		checkCaption(lineSecondLevel, GeoElementND.LABEL_CAPTION_VALUE, "h: g");
	}

	private GeoLine createRayWithCommand() {
		addAvInput("A = (1, 2)");
		addAvInput("B = (2, 3)");
		return addAvInput("f = Ray(A, B)");
	}

	private void checkCaption(GeoElement element, int labelMode, String expectedLabelText) {
		element.setLabelMode(labelMode);
		assertThat(converter.convert(element), is(expectedLabelText));
	}
}