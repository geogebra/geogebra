/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */
 
package org.geogebra.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.geogebra.common.io.XmlTestUtil;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.desktop.headless.AppDNoGui;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.desktop.util.UtilD;
import org.geogebra.test.annotation.Issue;
import org.junit.BeforeClass;
import org.junit.Test;

public class XmlTest {

	static AppDNoGui app;
	private static AlgebraProcessor ap;

	/** Set up app */
	@BeforeClass
	public static void setup() {
		app = new AppDNoGui(new LocalizationD(3), false);
		ap = app.getKernel().getAlgebraProcessor();
		app.setLanguage(Locale.US);
	}

	@Test
	public void emptyAppTest() {
		XmlTestUtil.checkCurrentXML(app);
	}

	@Test
	public void pointReloadTest() {
		GeoElementND p = processAlgebraCommand("P=(1,1)");
		((GeoPoint) p).setAnimationStep(0.01);
		app.setXML(app.getXML(), true);
		assertEquals(0.01, app.getKernel().lookupLabel("P").getAnimationStep(), 1E-8);
	}

	@Test
	public void specialPointsLoadTest() {
		app.setXML(UtilD.loadFileIntoString("src/test/resources/specialpoints.xml"), true);
		assertEquals(20, app.getGgbApi().getAllObjectNames().length);
	}

	@Test
	@Issue("APPS-6072")
	public void checkAnimationSpeedsOnFileSaveAndLoad() {
		// Some random input
		processAlgebraCommand("A = (1, 2)");
		processAlgebraCommand("l = {A, (3, 4)}");
		processAlgebraCommand("a = 3");
		processAlgebraCommand("A1 = 2");
		processAlgebraCommand("B1 = 3");
		processAlgebraCommand("C1 = Sum(A1:B1)");

		// Some elements with modified animation speed
		GeoElementND numeric = processAlgebraCommand("b = 5");
		numeric.toGeoElement().setAnimationSpeed(2.5);
		GeoElementND slider = processAlgebraCommand("Slider(-5, 5, 1)");
		slider.setAuxiliaryObject(true);
		slider.toGeoElement().setAnimationSpeed(0.5);
		GeoElementND spreadsheetElement = processAlgebraCommand("D1 = 4");
		spreadsheetElement.toGeoElement().setAnimationSpeed(3.0);

		String xml = app.getXML();

		assertTrue(xml.contains("speed=\"2.5\""));
		assertTrue(xml.contains("speed=\"0.5\""));
		assertTrue(xml.contains("speed=\"3\""));
		assertFalse(xml.contains("speed=\"1\""));

		// Reload
		app.setXML(xml, true);
		xml = app.getXML();

		assertTrue(xml.contains("speed=\"2.5\""));
		assertTrue(xml.contains("speed=\"0.5\""));
		assertTrue(xml.contains("speed=\"3\""));
		assertFalse(xml.contains("speed=\"1\""));
	}

	@Test
	@Issue("APPS-1470")
	public void elementShouldNotBeReloadedAsVectorIfLocalVariableExists() {
		processAlgebraCommand("l1 = {(1, 2), (3, 4)}");
		processAlgebraCommand("l2 = {(0, 2), (1, 0)}");
		processAlgebraCommand("l3 = Zip(Vector(aa, bb), aa, l1, bb, l2)");
		processAlgebraCommand("KeepIf(Length(vv) < 2,vv,l3)");

		String xml = app.getXML();
		assertFalse(xml.contains("Vector[vv]"));

		app.setXML(xml, true);
		assertFalse(app.getXML().contains("Vector[vv]"));
	}

	private GeoElementND processAlgebraCommand(String input) {
		return ap.processAlgebraCommand(input, true)[0];
	}
}
