package org.geogebra.common.exam.restrictions.realschule;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.exam.BaseExamTests;
import org.geogebra.common.gui.view.algebra.GeoElementValueConverter;
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

import com.himamis.retex.editor.share.util.Unicode;

public class RealschuleValueConverterTests extends BaseExamTests {

	@BeforeEach
	public void setup() {
		setInitialApp(SuiteSubApp.GRAPHING);
	}

	@Test
	public void testValueRestrictions() {
		ToStringConverter converter = new ProtectiveGeoElementValueConverter(
				new RealschuleAlgebraOutputFilter(null));

		GeoElement line = evaluateGeoElement("Line((0, 0), (1, 2))");
		assertEquals("Line((0, 0), (1, 2))", converter.convert(line));

		GeoElement ray = evaluateGeoElement("Ray((0, 0), (1, 2))");
		assertEquals("Ray((0, 0), (1, 2))", converter.convert(ray));

		GeoElement circle = evaluateGeoElement("Circle((0, 0), 1)");
		assertEquals("Circle((0, 0), 1)", converter.convert(circle));

		GeoElement fitLine = evaluateGeoElement("FitLine((1,1), (2,3))");
		assertEquals("y = 2x - 1", converter.convert(fitLine));

		GeoElement fitExp = evaluateGeoElement("FitExp((1,1),(2,4))");
		assertEquals("0.25ℯ^(1.39x)", converter.convert(fitExp));

		GeoElement fitGrowth = evaluateGeoElement("FitGrowth((1,2),(3,4))");
		assertEquals("1.41 * 1.41^x", converter.convert(fitGrowth));

		GeoElement fitLogistics = evaluateGeoElement("FitLogistic((1,2),(3,4),(5,6))");
		assertEquals("8 / (1 + 5.2ℯ^(-0.55x))", converter.convert(fitLogistics));

		GeoElement fitPow = evaluateGeoElement("FitPow((1,2),(3,4))");
		assertEquals("2x^0.63", converter.convert(fitPow));

		GeoElement fitSin = evaluateGeoElement("FitSin((3,3),(4,4))");
		assertEquals("3.5 + 0.5sin(" + Unicode.PI_STRING + "x - 11)", converter.convert(fitSin));

		GeoElement implicitCurve = evaluateGeoElement("FitImplicit((1…10, 1 / (1…10)), 3)");
		assertEquals("-0.06x² y - 0.7x y + 0.06x = -0.7", converter.convert(implicitCurve));

		GeoElement function = evaluateGeoElement("FitPoly({(-2, 1), (-1, 0), (0, 1), (1, 0)}, 3)");
		assertEquals("-0.67x³ - x² + 0.67x + 1", converter.convert(function));

		GeoElement implicitCircle = evaluateGeoElement("xx+yy=1");
		assertEquals("x² + y² = 1", converter.convert(implicitCircle));
	}

	@Test
	public void testLabelRestrictions() throws NotApplicablePropertyException {
		ToStringConverter converter = new ProtectiveGeoElementValueConverter(
				new RealschuleAlgebraOutputFilter(null));

		Localization localization = app.getLocalization();
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