package org.geogebra.common.exam.restrictions.realschule;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.BaseAppTestSetup;
import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.gui.view.algebra.ProtectiveGeoElementValueConverter;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.objects.CaptionStyleProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.util.ToStringConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class RealschuleValueConverterTests extends BaseAppTestSetup {
	private final ToStringConverter converter = new ProtectiveGeoElementValueConverter(
			new RealschuleAlgebraOutputFilter(null));

	@BeforeEach
	public void setup() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@SuppressWarnings({"checkstyle:RegexpSinglelineCheck", "checkstyle:LineLength"})
	@ParameterizedTest
	@CsvSource(delimiterString = "->", value = {
			"Line((0, 0), (1, 2)) 							-> Line((0, 0), (1, 2))",
			"Ray((0, 0), (1, 2)) 							-> Ray((0, 0), (1, 2))",
			"Circle((0, 0), 1) 								-> Circle((0, 0), 1)",
			"FitLine((1,1), (2,3)) 							-> y = 2x - 1",
			"FitExp((1,1),(2,4)) 							-> 0.25ℯ^(1.3862943611199x)",
			"FitGrowth((1,2),(3,4)) 						-> 1.4142135623731 * 1.4142135623731^x",
			"FitLogistic((1,2),(3,4),(5,6)) 				-> 8 / (1 + 5.1961524227066ℯ^(-0.5493061443341x))",
			"FitPow((1,2),(3,4)) 							-> 2x^0.6309297535715",
			"FitSin((3,3),(4,4)) 							-> 3.5 + 0.5sin(πx - 10.9955742875643)",
			"FitImplicit((1…10, 1 / (1…10)), 3) 			-> -0.0578620807846x² y - 0.001697474198x y² - 0.7047333525445x y + 0.0578620807846x + 0.001697474198y = -0.7047333525445",
			"FitPoly({(-2, 1), (-1, 0), (0, 1), (1, 0)}, 3) -> -0.6666666666667x³ - x² + 0.6666666666667x + 1",
			"xx+yy=1 										-> x² + y² = 1",
	})
	public void testValueRestrictions(String expression, String expectedOutput) {
		GeoElement geoElement = evaluateGeoElement(expression);
		assertEquals(expectedOutput, converter.convert(geoElement));
	}

	@Test
	public void testLabelRestrictions() throws NotApplicablePropertyException {
		Localization localization = getApp().getLocalization();
		StringTemplate defaultTemplate = StringTemplate.defaultTemplate;

		GeoElement line = evaluateGeoElement("Line((0, 0), (1, 2))");
		CaptionStyleProperty captionStyleProperty = new CaptionStyleProperty(localization, line);
		captionStyleProperty.setValue(GeoElementND.LABEL_VALUE);
		assertEquals(line.getDefinition(defaultTemplate), converter.toLabelAndDescription(line));
		captionStyleProperty.setValue(GeoElementND.LABEL_NAME_VALUE);
		assertEquals("f: " + line.getDefinition(defaultTemplate),
				converter.toLabelAndDescription(line));
		captionStyleProperty.setValue(GeoElementND.LABEL_NAME);
		assertEquals(line.getLabel(defaultTemplate), converter.toLabelAndDescription(line));
		captionStyleProperty.setValue(GeoElementND.LABEL_CAPTION);
		assertEquals(line.getCaption(defaultTemplate), converter.toLabelAndDescription(line));

		GeoElement ray = evaluateGeoElement("Ray((0, 0), (1, 2))");
		captionStyleProperty = new CaptionStyleProperty(localization, ray);
		captionStyleProperty.setValue(GeoElementND.LABEL_VALUE);
		assertEquals(ray.getDefinition(defaultTemplate), converter.toLabelAndDescription(ray));
		captionStyleProperty.setValue(GeoElementND.LABEL_NAME_VALUE);
		assertEquals("g: " + ray.getDefinition(defaultTemplate),
				converter.toLabelAndDescription(ray));
		captionStyleProperty.setValue(GeoElementND.LABEL_NAME);
		assertEquals(ray.getLabel(defaultTemplate), converter.toLabelAndDescription(ray));
		captionStyleProperty.setValue(GeoElementND.LABEL_CAPTION);
		assertEquals(ray.getCaption(defaultTemplate), converter.toLabelAndDescription(ray));

		GeoElement circle = evaluateGeoElement("Circle((0, 0), 1)");
		captionStyleProperty = new CaptionStyleProperty(localization, circle);
		captionStyleProperty.setValue(GeoElementND.LABEL_VALUE);
		assertEquals(circle.getDefinition(defaultTemplate),
				converter.toLabelAndDescription(circle));
		captionStyleProperty.setValue(GeoElementND.LABEL_NAME_VALUE);
		assertEquals("c: " + circle.getDefinition(defaultTemplate),
				converter.toLabelAndDescription(circle));
		captionStyleProperty.setValue(GeoElementND.LABEL_NAME);
		assertEquals(circle.getLabel(defaultTemplate), converter.toLabelAndDescription(circle));
		captionStyleProperty.setValue(GeoElementND.LABEL_CAPTION);
		assertEquals(circle.getCaption(defaultTemplate), converter.toLabelAndDescription(circle));

		GeoElement implicitCurve = evaluateGeoElement("FitImplicit((1...10,(1/(1...10))),3)");
		captionStyleProperty = new CaptionStyleProperty(localization, implicitCurve);
		captionStyleProperty.setValue(GeoElementND.LABEL_VALUE);
		assertEquals(implicitCurve.toValueString(defaultTemplate),
				converter.toLabelAndDescription(implicitCurve));
		captionStyleProperty.setValue(GeoElementND.LABEL_NAME_VALUE);
		assertEquals(implicitCurve.toString(defaultTemplate),
				converter.toLabelAndDescription(implicitCurve));
		captionStyleProperty.setValue(GeoElementND.LABEL_NAME);
		assertEquals(implicitCurve.getLabel(defaultTemplate),
				converter.toLabelAndDescription(implicitCurve));
		captionStyleProperty.setValue(GeoElementND.LABEL_CAPTION);
		assertEquals(implicitCurve.getCaption(defaultTemplate),
				converter.toLabelAndDescription(implicitCurve));

		GeoElement fitPoly = evaluateGeoElement("f(x)=FitPoly({(-2,1),(-1,0),(0,1),(1,0)},3)");
		captionStyleProperty = new CaptionStyleProperty(localization, fitPoly);
		captionStyleProperty.setValue(GeoElementND.LABEL_VALUE);
		assertEquals(fitPoly.toValueString(defaultTemplate),
				converter.toLabelAndDescription(fitPoly));
		captionStyleProperty.setValue(GeoElementND.LABEL_NAME_VALUE);
		assertEquals(fitPoly.toString(defaultTemplate), converter.toLabelAndDescription(fitPoly));
		captionStyleProperty.setValue(GeoElementND.LABEL_NAME);
		assertEquals(fitPoly.getLabel(defaultTemplate), converter.toLabelAndDescription(fitPoly));
		captionStyleProperty.setValue(GeoElementND.LABEL_CAPTION);
		assertEquals(fitPoly.getCaption(defaultTemplate), converter.toLabelAndDescription(fitPoly));

		GeoElement fitGrowth = evaluateGeoElement("FitGrowth((1,2),(3,4))");
		captionStyleProperty = new CaptionStyleProperty(localization, fitGrowth);
		captionStyleProperty.setValue(GeoElementND.LABEL_VALUE);
		assertEquals(fitGrowth.toValueString(defaultTemplate),
				converter.toLabelAndDescription(fitGrowth));
		captionStyleProperty.setValue(GeoElementND.LABEL_NAME_VALUE);
		assertEquals(fitGrowth.toString(defaultTemplate),
				converter.toLabelAndDescription(fitGrowth));
		captionStyleProperty.setValue(GeoElementND.LABEL_NAME);
		assertEquals(fitGrowth.getLabel(defaultTemplate),
				converter.toLabelAndDescription(fitGrowth));
		captionStyleProperty.setValue(GeoElementND.LABEL_CAPTION);
		assertEquals(fitGrowth.getCaption(defaultTemplate),
				converter.toLabelAndDescription(fitGrowth));

		GeoElement fitPow = evaluateGeoElement("FitPow((1,2),(3,4))");
		captionStyleProperty = new CaptionStyleProperty(localization, fitPow);
		captionStyleProperty.setValue(GeoElementND.LABEL_VALUE);
		assertEquals(fitPow.toValueString(defaultTemplate),
				converter.toLabelAndDescription(fitPow));
		captionStyleProperty.setValue(GeoElementND.LABEL_NAME_VALUE);
		assertEquals(fitPow.toString(defaultTemplate),
				converter.toLabelAndDescription(fitPow));
		captionStyleProperty.setValue(GeoElementND.LABEL_NAME);
		assertEquals(fitPow.getLabel(defaultTemplate),
				converter.toLabelAndDescription(fitPow));
		captionStyleProperty.setValue(GeoElementND.LABEL_CAPTION);
		assertEquals(fitPow.getCaption(defaultTemplate),
				converter.toLabelAndDescription(fitPow));
	}
}