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

package org.geogebra.common.euclidian.modes;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.euclidian.BaseEuclidianControllerTest;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.jre.util.ScientificFormat;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.settings.config.AppConfigGraphing;
import org.geogebra.common.main.settings.config.AppConfigNotes;
import org.junit.Test;

public class ModeDeleteTest extends BaseEuclidianControllerTest {

	@Test
	public void eraserTest() {
		setMode(EuclidianConstants.MODE_ERASER);
		add("stroke = PenStroke((2,-4),(2,-2),(4,-2),(4,-4))");
		click(140, 90);

		assertEquals("PenStroke[2.0000E0,-4.0000E0,1.8720E0,-3.5232E0,"
						+ "1.7760E0,-3.0656E0,1.7440E0,-2.6464E0,1.8080E0,-2.2848E0,"
						+ "2.0000E0,-2.0000E0,2.3360E0,-1.8080E0,2.6000E0,-1.7365E0,"
						+ "?,?,3.0000E0,-1.7000E0,3.2320E0,-1.7120E0,"
						+ "3.6640E0,-1.8080E0,4.0000E0,-2.0000E0,4.1920E0,-2.2848E0,"
						+ "4.2560E0,-2.6464E0,4.2240E0,-3.0656E0,4.1280E0,-3.5232E0,"
						+ "4.0000E0,-4.0000E0,?,?]",
				getDefinition());

		add("stroke = PenStroke((2,-4),(2,-2),(4,-2),(4,-4))");
		click(100, 100);

		assertEquals("PenStroke[2.0000E0,-4.0000E0,1.8720E0,-3.5232E0,"
						+ "1.7760E0,-3.0656E0,1.7440E0,-2.6464E0,1.8080E0,-2.2848E0,"
						+ "1.8456E0,-2.2000E0,?,?,2.2000E0,-1.8664E0,"
						+ "2.3360E0,-1.8080E0,2.7680E0,-1.7120E0,3.2320E0,-1.7120E0,"
						+ "3.6640E0,-1.8080E0,4.0000E0,-2.0000E0,4.1920E0,-2.2848E0,"
						+ "4.2560E0,-2.6464E0,4.2240E0,-3.0656E0,4.1280E0,-3.5232E0,"
						+ "4.0000E0,-4.0000E0,?,?]",
				getDefinition());
	}

	@Test
	public void eraserTestStraight() {
		getApp().setUndoActive(true);
		getApp().setConfig(new AppConfigNotes());
		getConstruction().getUndoManager().setAllowCheckpoints(false);
		setMode(EuclidianConstants.MODE_ERASER);
		// delete the middle
		add("stroke = PenStroke((2,-2),(3,-2),(4,-2))");
		int x = 145;
		int y = 95;
		dragStart(x, y);
		dragEnd(x + 5, y + 5);
		assertEquals("PenStroke[2.0000E0,-2.0000E0,2.8000E0,-2.0000E0,?,?,"
						+ "3.2000E0,-2.0000E0,4.0000E0,-2.0000E0,?,?]",
				getDefinition());
		// delete the start
		add("stroke = PenStroke((2,-2),(3,-2),(4,-2))");
		x = 95;
		y = 95;
		dragStart(x, y);
		dragEnd(x + 5, y + 5);
		assertEquals("PenStroke[2.2000E0,-2.0000E0,3.0000E0,-2.0000E0,"
						+ "4.0000E0,-2.0000E0,?,?]",
				getDefinition());
		getKernel().undo();
		assertEquals("PenStroke[2.0000E0,-2.0000E0,3.0000E0,-2.0000E0,"
						+ "4.0000E0,-2.0000E0,?,?]",
				getDefinition());
	}

	@Test
	public void eraserTestLongDragGraphing() {
		eraserTestLongDrag(new AppConfigGraphing());
	}

	@Test
	public void eraserTestLongDragNotes() {
		eraserTestLongDrag(new AppConfigNotes());
	}

	private void eraserTestLongDrag(AppConfig config) {
		getApp().setUndoActive(true);
		getApp().setConfig(config);
		getConstruction().getUndoManager().setAllowCheckpoints(false);
		setMode(EuclidianConstants.MODE_ERASER);
		// delete the start
		add("stroke = PenStroke((2,-2),(3,-2),(4,-2))");
		dragLinear(95, 95, 130, 105);
		assertEquals("PenStroke[2.8000E0,-2.0000E0,3.0000E0,-2.0000E0,"
						+ "4.0000E0,-2.0000E0,?,?]",
				getDefinition());
		getKernel().undo();
		assertEquals("PenStroke[2.0000E0,-2.0000E0,3.0000E0,-2.0000E0,"
						+ "4.0000E0,-2.0000E0,?,?]",
				getDefinition());
	}

	@Test
	public void eraseMultipleTestGraphing() {
		eraseMultipleTest(new AppConfigGraphing());
	}

	@Test
	public void eraseMultipleTestNotes() {
		eraseMultipleTest(new AppConfigNotes());
	}

	private void eraseMultipleTest(AppConfig config) {
		getApp().setUndoActive(true);
		getApp().setConfig(config);
		setMode(EuclidianConstants.MODE_ERASER);
		add("A=(2,-2)");
		add("B=(4,-2)");
		add("C=(2,-4)");
		add("D=(4,-4)");
		getApp().storeUndoInfo();
		dragLinear(50, 200, 250, 200);
		checkContentLabels("A", "B");
		dragLinear(51, 100, 251, 100);
		checkContentLabels();
		getKernel().undo();
		checkContentLabels("A", "B");
		getKernel().undo();
		checkContentLabels("A", "B", "C", "D");
	}

	private void dragLinear(int x1, int y1, int x2, int y2) {
		dragStart(x1, y1);
		int steps = 20;
		for (int i = 0; i < 20; i++) {
			drag(x1 + (i * (x2 - x1)) / steps, y1 + (i * (y2 - y1)) / steps);
		}
		dragEnd(x2, y2);
	}

	@Test
	public void shouldNotDeleteFixedObjects() {
		getApp().setAppletFlag(true);
		setMode(EuclidianConstants.MODE_DELETE);
		add("a:x=1");
		add("SetFixed(a,true)");
		click(50, 50);
		checkContent("a: x = 1");
		getApp().setAppletFlag(false);
		resetMouseLocation();
		click(50, 50);
		checkContent();
	}

	@Test
	public void shouldDeleteAngles() {
		getApp().setAppletFlag(true);
		add("Angle((1,-2),(1,-1),(2,-1))");
		setMode(EuclidianConstants.MODE_DELETE);
		click(60, 60);
		checkContent();
	}

	@Test
	public void shouldNotDeleteFixedSliders() {
		getApp().setAppletFlag(true);
		setMode(EuclidianConstants.MODE_DELETE);
		GeoElement slider = add("a=Slider(-1,1)");
		((GeoNumeric) slider).setSliderFixed(true);
		add("SetCoords(a,50,50)");
		click(50, 50);
		checkContent("a = 0");
		getApp().setAppletFlag(false);
		resetMouseLocation();
		click(50, 50);
		checkContent();
	}

	private String getDefinition() {
		String nanString = new ScientificFormat(1, 10, false).format(Double.NaN);
		String definition1 = lookup("stroke").getDefinition(StringTemplate.defaultTemplate);
		return definition1.replace(nanString, "?");
	}
}
