package org.geogebra.common.exam.restrictions.cvte;

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

public class CvteValueConverterTests extends BaseAppTestSetup {

	@BeforeEach
	public void setup() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@Test
	public void testValueRestrictions() {
		ToStringConverter converter = new ProtectiveGeoElementValueConverter(
				new CvteAlgebraOutputFilter(null));

		// For Lines, Rays, Conics, Implicit Equations and Functions created with command or tool:
		// When using the ANS button the "Definition" is inserted into the AV inputBar.
		GeoElement line = evaluateGeoElement("Line((0, 0), (1, 2))");
		assertEquals("Line((0, 0), (1, 2))", converter.convert(line));

		GeoElement ray = evaluateGeoElement("Ray((0, 0), (1, 2))");
		assertEquals("Ray((0, 0), (1, 2))", converter.convert(ray));

		GeoElement circle = evaluateGeoElement("Circle((0, 0), 1)");
		assertEquals("Circle((0, 0), 1)", converter.convert(circle));

		GeoElement implicitCurve = evaluateGeoElement("FitImplicit((1…10, 1 / (1…10)), 3)");
		assertEquals("FitImplicit((1…10, 1 / (1…10)), 3)", converter.convert(implicitCurve));

		GeoElement function = evaluateGeoElement("FitPoly({(-2, 1), (-1, 0), (0, 1), (1, 0)}, 3)");
		assertEquals("FitPoly({(-2, 1), (-1, 0), (0, 1), (1, 0)}, 3)", converter.convert(function));

		// negative example
		GeoElement implicitCircle = evaluateGeoElement("xx+yy=1");
		assertEquals("x² + y² = 1", converter.convert(implicitCircle));
	}

	@Test
	public void testLabelRestrictions() throws NotApplicablePropertyException {
		ToStringConverter converter = new ProtectiveGeoElementValueConverter(
				new CvteAlgebraOutputFilter(null));

		Localization localization = getApp().getLocalization();
		StringTemplate defaultTemplate = StringTemplate.defaultTemplate;

		// For Lines, Rays, Conics, Implicit Equations and Functions created with command or tool:
		// When choosing "Value" or "Name & Value" for the caption style the "definition" is shown.
		GeoElement line = evaluateGeoElement("Line((0, 0), (1, 2))");
		CaptionStyleProperty captionStyleProperty = new CaptionStyleProperty(localization, line);
		captionStyleProperty.setValue(GeoElementND.LABEL_VALUE);
		assertEquals(line.getDefinition(defaultTemplate), converter.toLabelAndDescription(line));
		captionStyleProperty.setValue(GeoElementND.LABEL_NAME_VALUE);
		assertEquals("f: " + line.getDefinition(defaultTemplate),
				converter.toLabelAndDescription(line));
		captionStyleProperty.setValue(GeoElementND.LABEL_CAPTION_VALUE);
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
		assertEquals(implicitCurve.getDefinition(defaultTemplate),
				converter.toLabelAndDescription(implicitCurve));
		captionStyleProperty.setValue(GeoElementND.LABEL_NAME_VALUE);
		assertEquals("eq1: " + implicitCurve.getDefinition(defaultTemplate),
				converter.toLabelAndDescription(implicitCurve));
		captionStyleProperty.setValue(GeoElementND.LABEL_NAME);
		assertEquals(implicitCurve.getLabel(defaultTemplate),
				converter.toLabelAndDescription(implicitCurve));
		captionStyleProperty.setValue(GeoElementND.LABEL_CAPTION);
		assertEquals(implicitCurve.getCaption(defaultTemplate),
				converter.toLabelAndDescription(implicitCurve));

		// functions: any of the FitPoly / FitLog / ... commands
		GeoElement fitPoly = evaluateGeoElement("f(x)=FitPoly({(-2,1),(-1,0),(0,1),(1,0)},3)");
		captionStyleProperty = new CaptionStyleProperty(localization, fitPoly);
		captionStyleProperty.setValue(GeoElementND.LABEL_VALUE);
		assertEquals(fitPoly.getDefinition(defaultTemplate),
				converter.toLabelAndDescription(fitPoly));
		captionStyleProperty.setValue(GeoElementND.LABEL_NAME_VALUE);
		assertEquals("f(x) = " + fitPoly.getDefinition(defaultTemplate),
				converter.toLabelAndDescription(fitPoly));
		captionStyleProperty.setValue(GeoElementND.LABEL_CAPTION_VALUE);
		assertEquals("f(x) = " + fitPoly.getDefinition(defaultTemplate),
				converter.toLabelAndDescription(fitPoly));
		captionStyleProperty.setValue(GeoElementND.LABEL_NAME);
		assertEquals(fitPoly.getLabel(defaultTemplate),
				converter.toLabelAndDescription(fitPoly));
		captionStyleProperty.setValue(GeoElementND.LABEL_CAPTION);
		assertEquals(fitPoly.getCaption(defaultTemplate),
				converter.toLabelAndDescription(fitPoly));
	}
}