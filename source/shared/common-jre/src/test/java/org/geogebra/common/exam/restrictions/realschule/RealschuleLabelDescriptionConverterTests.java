package org.geogebra.common.exam.restrictions.realschule;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.exam.BaseExamTests;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.description.DefaultLabelDescriptionConverter;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.objects.CaptionStyleProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.util.ToStringConverter;
import org.junit.Before;
import org.junit.Test;

public class RealschuleLabelDescriptionConverterTests extends BaseExamTests {

	@Before
	public void setup() {
		setInitialApp(SuiteSubApp.GRAPHING);
	}

	@Test
	public void testLabelRestrictions() throws NotApplicablePropertyException {
		ToStringConverter<GeoElement> converter = new RealschuleLabelDescriptionConverter(
				new DefaultLabelDescriptionConverter());

		Localization localization = app.getLocalization();
		StringTemplate defaultTemplate = StringTemplate.defaultTemplate;

		GeoElement line = evaluateGeoElement("Line((0, 0), (1, 2))");
		CaptionStyleProperty captionStyleProperty = new CaptionStyleProperty(localization, line);
		captionStyleProperty.setValue(GeoElementND.LABEL_VALUE);
		assertEquals(line.getDefinition(defaultTemplate), converter.convert(line));
		captionStyleProperty.setValue(GeoElementND.LABEL_NAME_VALUE);
		assertEquals("f: " + line.getDefinition(defaultTemplate), converter.convert(line));
		captionStyleProperty.setValue(GeoElementND.LABEL_NAME);
		assertEquals(line.getLabel(defaultTemplate), converter.convert(line));
		captionStyleProperty.setValue(GeoElementND.LABEL_CAPTION);
		assertEquals(line.getCaption(defaultTemplate), converter.convert(line));

		GeoElement ray = evaluateGeoElement("Ray((0, 0), (1, 2))");
		captionStyleProperty = new CaptionStyleProperty(localization, ray);
		captionStyleProperty.setValue(GeoElementND.LABEL_VALUE);
		assertEquals(ray.getDefinition(defaultTemplate), converter.convert(ray));
		captionStyleProperty.setValue(GeoElementND.LABEL_NAME_VALUE);
		assertEquals("g: " + ray.getDefinition(defaultTemplate), converter.convert(ray));
		captionStyleProperty.setValue(GeoElementND.LABEL_NAME);
		assertEquals(ray.getLabel(defaultTemplate), converter.convert(ray));
		captionStyleProperty.setValue(GeoElementND.LABEL_CAPTION);
		assertEquals(ray.getCaption(defaultTemplate), converter.convert(ray));

		GeoElement circle = evaluateGeoElement("Circle((0, 0), 1)");
		captionStyleProperty = new CaptionStyleProperty(localization, circle);
		captionStyleProperty.setValue(GeoElementND.LABEL_VALUE);
		assertEquals(circle.getDefinition(defaultTemplate), converter.convert(circle));
		captionStyleProperty.setValue(GeoElementND.LABEL_NAME_VALUE);
		assertEquals("c: " + circle.getDefinition(defaultTemplate), converter.convert(circle));
		captionStyleProperty.setValue(GeoElementND.LABEL_NAME);
		assertEquals(circle.getLabel(defaultTemplate), converter.convert(circle));
		captionStyleProperty.setValue(GeoElementND.LABEL_CAPTION);
		assertEquals(circle.getCaption(defaultTemplate), converter.convert(circle));

		GeoElement implicitCurve = evaluateGeoElement("FitImplicit((1...10,(1/(1...10))),3)");
		captionStyleProperty = new CaptionStyleProperty(localization, implicitCurve);
		captionStyleProperty.setValue(GeoElementND.LABEL_VALUE);
		assertEquals(implicitCurve.toValueString(defaultTemplate),
				converter.convert(implicitCurve));
		captionStyleProperty.setValue(GeoElementND.LABEL_NAME_VALUE);
		assertEquals(implicitCurve.toString(defaultTemplate), converter.convert(implicitCurve));
		captionStyleProperty.setValue(GeoElementND.LABEL_NAME);
		assertEquals(implicitCurve.getLabel(defaultTemplate), converter.convert(implicitCurve));
		captionStyleProperty.setValue(GeoElementND.LABEL_CAPTION);
		assertEquals(implicitCurve.getCaption(defaultTemplate), converter.convert(implicitCurve));

		GeoElement fitPoly = evaluateGeoElement("f(x)=FitPoly({(-2,1),(-1,0),(0,1),(1,0)},3)");
		captionStyleProperty = new CaptionStyleProperty(localization, fitPoly);
		captionStyleProperty.setValue(GeoElementND.LABEL_VALUE);
		assertEquals(fitPoly.toValueString(defaultTemplate), converter.convert(fitPoly));
		captionStyleProperty.setValue(GeoElementND.LABEL_NAME_VALUE);
		assertEquals(fitPoly.toString(defaultTemplate), converter.convert(fitPoly));
		captionStyleProperty.setValue(GeoElementND.LABEL_NAME);
		assertEquals(fitPoly.getLabel(defaultTemplate), converter.convert(fitPoly));
		captionStyleProperty.setValue(GeoElementND.LABEL_CAPTION);
		assertEquals(fitPoly.getCaption(defaultTemplate), converter.convert(fitPoly));

		GeoElement fitGrowth = evaluateGeoElement("FitGrowth((1,2),(3,4))");
		captionStyleProperty = new CaptionStyleProperty(localization, fitGrowth);
		captionStyleProperty.setValue(GeoElementND.LABEL_VALUE);
		assertEquals(fitGrowth.toValueString(defaultTemplate), converter.convert(fitGrowth));
		captionStyleProperty.setValue(GeoElementND.LABEL_NAME_VALUE);
		assertEquals(fitGrowth.toString(defaultTemplate), converter.convert(fitGrowth));
		captionStyleProperty.setValue(GeoElementND.LABEL_NAME);
		assertEquals(fitGrowth.getLabel(defaultTemplate), converter.convert(fitGrowth));
		captionStyleProperty.setValue(GeoElementND.LABEL_CAPTION);
		assertEquals(fitGrowth.getCaption(defaultTemplate), converter.convert(fitGrowth));

		GeoElement fitPow = evaluateGeoElement("FitPow((1,2),(3,4))");
		captionStyleProperty = new CaptionStyleProperty(localization, fitPow);
		captionStyleProperty.setValue(GeoElementND.LABEL_VALUE);
		assertEquals(fitPow.toValueString(defaultTemplate), converter.convert(fitPow));
		captionStyleProperty.setValue(GeoElementND.LABEL_NAME_VALUE);
		assertEquals(fitPow.toString(defaultTemplate), converter.convert(fitPow));
		captionStyleProperty.setValue(GeoElementND.LABEL_NAME);
		assertEquals(fitPow.getLabel(defaultTemplate), converter.convert(fitPow));
		captionStyleProperty.setValue(GeoElementND.LABEL_CAPTION);
		assertEquals(fitPow.getCaption(defaultTemplate), converter.convert(fitPow));
	}
}