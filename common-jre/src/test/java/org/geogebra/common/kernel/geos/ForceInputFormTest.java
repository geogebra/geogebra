package org.geogebra.common.kernel.geos;

import static org.junit.Assert.fail;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.GeoElementFactory;
import org.geogebra.common.gui.dialog.options.model.ConicEqnModel;
import org.geogebra.common.gui.dialog.options.model.LineEqnModel;
import org.geogebra.common.kernel.LinearEquationRepresentable;
import org.geogebra.common.kernel.QuadraticEquationRepresentable;
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
		GeoLine line = factory.createGeoLine();
		GeoLine lineWithCommand = factory.createGeoLineWithCommand();
		GeoConic parabola = (GeoConic) factory.create("y=xx");
		GeoConic hyperbola = (GeoConic) factory.create("yy-xx=1");
		GeoRay geoRay = factory.createGeoRayWithCommand();

		Assert.assertEquals(LinearEquationRepresentable.Form.USER,
				line.getEquationForm());
		Assert.assertEquals(LinearEquationRepresentable.Form.EXPLICIT,
				lineWithCommand.getEquationForm());
		Assert.assertEquals(LinearEquationRepresentable.Form.USER,
				geoRay.getEquationForm());
		Assert.assertEquals(QuadraticEquationRepresentable.Form.IMPLICIT,
				parabola.getEquationForm());
		Assert.assertEquals(QuadraticEquationRepresentable.Form.IMPLICIT,
				hyperbola.getEquationForm());
	}

	@Test
	public void testLinesLoadedFromXMLGraphing() {
		getApp().setConfig(new AppConfigGraphing());

		GeoElementFactory factory = getElementFactory();
		GeoLine line = factory.createGeoLine();
		line.setLabel("line");

		GeoLine lineWithCommand = factory.createGeoLineWithCommand();
		lineWithCommand.setLabel("lineCmd");
		lineWithCommand.setEquationForm(LinearEquationRepresentable.Form.PARAMETRIC);
		Assert.assertEquals(LinearEquationRepresentable.Form.PARAMETRIC,
				lineWithCommand.getEquationForm());

		getApp().setXML(getApp().getXML(), true);

		GeoLine loadedLine = (GeoLine) lookup("line");
		GeoLine loadedLineWithCommand = (GeoLine) lookup("lineCmd");

		Assert.assertEquals(LinearEquationRepresentable.Form.USER, loadedLine.getEquationForm());
		Assert.assertEquals(LinearEquationRepresentable.Form.PARAMETRIC,
				loadedLineWithCommand.getEquationForm());
	}

	@Test
	public void testLinesConicsRaysToStringModeInGeometry() {
		getApp().setConfig(new AppConfigGeometry());

		GeoElementFactory factory = getElementFactory();
		GeoLine line = factory.createGeoLine();
		GeoLine lineWithCommand = factory.createGeoLineWithCommand();
		GeoRay ray = factory.createGeoRayWithCommand();
		GeoConic parabola = (GeoConic) factory.create("y=xx");
		GeoConic hyperbola = (GeoConic) factory.create("yy-xx=1");

		Assert.assertEquals(LinearEquationRepresentable.Form.USER,
				line.getEquationForm());
		Assert.assertEquals(LinearEquationRepresentable.Form.EXPLICIT,
				lineWithCommand.getEquationForm());
		Assert.assertEquals(LinearEquationRepresentable.Form.EXPLICIT,
				ray.getEquationForm());
		Assert.assertEquals(QuadraticEquationRepresentable.Form.USER,
				parabola.getEquationForm());
		Assert.assertEquals(QuadraticEquationRepresentable.Form.USER,
				hyperbola.getEquationForm());
	}

	@Test
	public void testHideOutputRowGraphing() {
		getApp().setGraphingConfig();
		GeoRay ray = getElementFactory().createGeoRayWithCommand();

		Assert.assertFalse(ray.isAllowedToShowValue());
		Assert.assertEquals(DescriptionMode.DEFINITION, ray.getDescriptionMode());
	}

	@Test
	public void testShowOutputRowGeometry() {
		getApp().setConfig(new AppConfigGeometry());
		GeoRay ray = getElementFactory().createGeoRayWithCommand();

		Assert.assertTrue(ray.isAllowedToShowValue());
		Assert.assertEquals(ray.getDescriptionMode(), DescriptionMode.DEFINITION_VALUE);
	}

	@Test
	public void testEquationPropertyIsHiddenGraphing() {
		getApp().setConfig(new AppConfigGraphing());
		getApp().getSettings().getCasSettings().setEnabled(getApp().getConfig().isCASEnabled());

		GeoElementFactory factory = getElementFactory();
		GeoLine line = factory.createGeoLine();
		GeoRay rayWithCommand = getElementFactory().createGeoRayWithCommand();
		GeoConic parabola = (GeoConic) factory.create("y=xx");
		GeoConic hyperbola = (GeoConic) factory.create("yy-xx=1");

		Assert.assertTrue(LineEqnModel.forceInputForm(line));
		Assert.assertTrue(LineEqnModel.forceInputForm(rayWithCommand));
		Assert.assertTrue(ConicEqnModel.forceInputForm(parabola));
		Assert.assertTrue(ConicEqnModel.forceInputForm(hyperbola));
	}

	@Test
	public void testEquationPropertyIsHiddenGeometry() {
		getApp().setConfig(new AppConfigGeometry());
		getApp().getSettings().getCasSettings().setEnabled(getApp().getConfig().isCASEnabled());

		GeoElementFactory factory = getElementFactory();
		GeoLine line = (GeoLine) factory.create("Line((0,0),(1,1))");
		GeoRay rayWithCommand = getElementFactory().createGeoRayWithCommand();
		GeoConic parabola = (GeoConic) factory.create("y=xx");
		GeoConic hyperbola = (GeoConic) factory.create("yy-xx=1");

		Assert.assertFalse(LineEqnModel.forceInputForm(line));
		Assert.assertFalse(LineEqnModel.forceInputForm(rayWithCommand));
		Assert.assertFalse(ConicEqnModel.forceInputForm(parabola));
		Assert.assertFalse(ConicEqnModel.forceInputForm(hyperbola));

		try {
			new EquationFormProperty(getLocalization(), line);
			new EquationFormProperty(getLocalization(), rayWithCommand);
		} catch (NotApplicablePropertyException e) {
			fail(e.getMessage());
		}
	}
}
