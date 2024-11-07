package org.geogebra.common.kernel.geos;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.GeoElementFactory;
import org.geogebra.common.gui.dialog.options.model.LineEqnModel;
import org.geogebra.common.kernel.EquationLinear;
import org.geogebra.common.kernel.EquationQuadric;
import org.geogebra.common.main.settings.config.AppConfigGeometry;
import org.geogebra.common.main.settings.config.AppConfigGraphing;
import org.geogebra.common.properties.impl.objects.EquationFormProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.junit.Assert;
import org.junit.Test;

public class ForceInputFormTest extends BaseUnitTest {

	@Test
	public void testLinesConicsRaysToStringModeInGraphing() {
		getApp().setConfig(new AppConfigGraphing());

		GeoElementFactory factory = getElementFactory();
		GeoLine geoLine = factory.createGeoLine();
		GeoLine geoLineWithCommand = factory.createGeoLineWithCommand();
		GeoConic parabola = (GeoConic) factory.create("y=xx");
		GeoConic hyperbola = (GeoConic) factory.create("yy-xx=1");
		GeoRay geoRay = factory.createGeoRay();

		Assert.assertEquals(EquationLinear.Form.USER, geoLine.getEquationForm());
		Assert.assertEquals(EquationLinear.Form.EXPLICIT, geoLineWithCommand.getEquationForm());
		Assert.assertEquals(EquationLinear.Form.USER, geoRay.getEquationForm());
		Assert.assertEquals(EquationQuadric.Form.IMPLICIT, parabola.getEquationForm());
		Assert.assertEquals(EquationQuadric.Form.IMPLICIT, hyperbola.getEquationForm());
	}

	@Test
	public void testLinesLoadedFromXMLGraphing() {
		getApp().setConfig(new AppConfigGraphing());

		GeoElementFactory factory = getElementFactory();
		GeoLine geoLine = factory.createGeoLine();
		geoLine.setLabel("line");

		GeoLine geoLineWithCommand = factory.createGeoLineWithCommand();
		geoLineWithCommand.setLabel("lineCmd");
		geoLineWithCommand.setEquationForm(EquationLinear.Form.PARAMETRIC);
		Assert.assertEquals(EquationLinear.Form.PARAMETRIC, geoLineWithCommand.getEquationForm());

		getApp().setXML(getApp().getXML(), true);

		GeoLine loadedLine = (GeoLine) lookup("line");
		GeoLine loadedLineWithCommand = (GeoLine) lookup("lineCmd");

		Assert.assertEquals(EquationLinear.Form.USER, loadedLine.getEquationForm());
		Assert.assertEquals(EquationLinear.Form.PARAMETRIC,
				loadedLineWithCommand.getEquationForm());
	}

	@Test
	public void testLinesConicsRaysToStringModeInGeometry() {
		getApp().setConfig(new AppConfigGeometry());

		GeoElementFactory factory = getElementFactory();
		GeoLine geoLine = factory.createGeoLine();
		GeoLine geoLineWithCommand = factory.createGeoLineWithCommand();
		GeoRay geoRay = factory.createGeoRay();
		GeoConic parabola = (GeoConic) factory.create("y=xx");
		GeoConic hyperbola = (GeoConic) factory.create("yy-xx=1");

		Assert.assertEquals(EquationLinear.Form.USER, geoLine.getEquationForm());
		Assert.assertEquals(EquationLinear.Form.EXPLICIT, geoLineWithCommand.getEquationForm());
		Assert.assertEquals(EquationLinear.Form.EXPLICIT, geoRay.getEquationForm());
		Assert.assertEquals(EquationQuadric.Form.USER, parabola.getEquationForm());
		Assert.assertEquals(EquationQuadric.Form.USER, hyperbola.getEquationForm());
	}

	@Test
	public void testHideOutputRowGraphing() {
		getApp().setGraphingConfig();
		GeoRay ray = getElementFactory().createGeoRay();

		Assert.assertFalse(ray.isAllowedToShowValue());
		Assert.assertEquals(DescriptionMode.DEFINITION, ray.getDescriptionMode());
	}

	@Test
	public void testShowOutputRowGeometry() {
		getApp().setConfig(new AppConfigGeometry());
		GeoRay ray = getElementFactory().createGeoRay();

		Assert.assertTrue(ray.isAllowedToShowValue());
		Assert.assertEquals(ray.getDescriptionMode(), DescriptionMode.DEFINITION_VALUE);
	}

	@Test
	public void testEquationPropertyIsHiddenGraphing() {
		getApp().setConfig(new AppConfigGraphing());
		getApp().getSettings().getCasSettings().setEnabled(getApp().getConfig().isCASEnabled());

		GeoElementFactory factory = getElementFactory();
		GeoLine geoLine = factory.createGeoLine();
		GeoRay ray = getElementFactory().createGeoRay();
		GeoConic parabola = (GeoConic) factory.create("y=xx");
		GeoConic hyperbola = (GeoConic) factory.create("yy-xx=1");

		GeoElement[] geos = new GeoElement[]{geoLine, parabola, hyperbola, ray};

		for (GeoElement geo : geos) {
			Assert.assertTrue(LineEqnModel.forceInputForm(getApp(), geo));
			assertThrows(NotApplicablePropertyException.class,
					() -> new EquationFormProperty(getLocalization(), geo));
		}
	}

	@Test
	public void testEquationPropertyIsHiddenGeometry() {
		getApp().setConfig(new AppConfigGeometry());
		getApp().getSettings().getCasSettings().setEnabled(getApp().getConfig().isCASEnabled());

		GeoElementFactory factory = getElementFactory();
		GeoLine geoLine = (GeoLine) factory.create("Line((0,0),(1,1))");
		GeoRay ray = getElementFactory().createGeoRay();
		GeoConic parabola = (GeoConic) factory.create("y=xx");
		GeoConic hyperbola = (GeoConic) factory.create("yy-xx=1");

		GeoElement[] geos = new GeoElement[]{geoLine, ray, parabola, hyperbola};

		for (GeoElement geo : geos) {
			Assert.assertFalse(LineEqnModel.forceInputForm(getApp(), geo));
		}

		try {
			new EquationFormProperty(getLocalization(), geoLine);
			new EquationFormProperty(getLocalization(), ray);
		} catch (NotApplicablePropertyException e) {
			fail(e.getMessage());
		}
	}
}
