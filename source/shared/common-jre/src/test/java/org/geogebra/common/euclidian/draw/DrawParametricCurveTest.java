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

package org.geogebra.common.euclidian.draw;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GGraphicsCommon;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.geogebra3D.main.settings.EuclidianSettingsForPlane;
import org.geogebra.common.jre.headless.EuclidianViewNoGui;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.kernelND.CurveEvaluable;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.EuclidianSettings3D;
import org.junit.Test;

public class DrawParametricCurveTest extends BaseUnitTest {

	@Test
	public void conditionalShouldHaveLabel() {
		add("ZoomIn(-5,-5,5,5)");
		GeoFunction f = add("If(x<1,1/0,x-1)");
		f.setLabelVisible(true);
		f.updateRepaint();
		Drawable drawable = getDrawable(f);
		assertNotNull(drawable);
		assertThat(drawable.getLabelX(), equalTo(480.0));
		assertThat(drawable.getLabelY(), equalTo(300.0));
	}

	@Test
	public void viewFromPlaneShouldNotPlotFunctions() {
		EuclidianView view = new EuclidianViewNoGui(getApp().newEuclidianController(getKernel()),
				0, new EuclidianSettingsForPlane(getApp()), new GGraphicsCommon());
		assertTrue(testFunctions(false, view,
				"sin(x)", "cos(x)", "x^2", "abs(x)", "tan(t)", "x + 3", "x / 2"));
	}

	@Test
	public void view3DShouldPlotFunctions() {
		EuclidianView view = new EuclidianViewNoGui(getApp().newEuclidianController(getKernel()),
				0, new EuclidianSettings3D(getApp()), new GGraphicsCommon());
		assertTrue(testFunctions(true, view,
				"sin(x)", "cos(x)", "x^2", "abs(x)", "tan(t)", "x + 3", "x / 2"));
	}

	@Test
	public void viewShouldPlotFunctions() {
		EuclidianView view = new EuclidianViewNoGui(getApp().newEuclidianController(getKernel()),
				0, new EuclidianSettings(getApp()), new GGraphicsCommon());
		assertTrue(testFunctions(true, view,
				"sin(x)", "cos(x)", "x^2", "abs(x)", "tan(t)", "x + 3", "x / 2"));
	}

	private boolean testFunctions(boolean shouldEnableIntervalPlotter,
			EuclidianView view, String... inputs) {
		return Arrays.stream(inputs).allMatch(input -> shouldEnableIntervalPlotter
				== isIntervalPlotterEnabledForInput(input, view));
	}

	private boolean isIntervalPlotterEnabledForInput(String input, EuclidianView view) {
		CurveEvaluable function = add(input);
		DrawParametricCurve drawParametricCurve = new DrawParametricCurve(view, function);
		drawParametricCurve.update();
		return drawParametricCurve.isIntervalPlotterEnabled();
	}
}
