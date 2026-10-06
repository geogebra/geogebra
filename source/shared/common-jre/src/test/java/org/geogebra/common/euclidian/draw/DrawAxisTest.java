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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.DrawAxis;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.editor.share.util.Unicode;
import org.geogebra.test.SnapshotChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestReporter;

class DrawAxisTest extends BaseUnitTest {
	private EuclidianView view;
	private SnapshotChecker snapshotChecker;

	@BeforeEach
	void initView(TestReporter reporter) {
		view = getApp().getActiveEuclidianView();
		this.snapshotChecker = new SnapshotChecker(reporter, this.getClass());
	}

	@Test
	void testDegreeLabelsWithPi() {
		GeoNumeric distance = add(Unicode.PI_STRING);
		view.getSettings().setAxisNumberingDistance(0, distance);
		assertEquals("3" + Unicode.PI_STRING, DrawAxis.tickDescription(view, 3, 0));
	}

	@Test
	void testDegreeLabelsContainNoPi() {
		GeoNumeric distance = add("60deg");
		view.getSettings().setAxisNumberingDistance(0, distance);
		assertEquals("180" + Unicode.DEGREE_STRING, DrawAxis.tickDescription(view, 3, 0));
	}

	@Test
	void testDecimalDescription() {
		GeoNumeric distance = add("0.3");
		view.getSettings().setAxisNumberingDistance(0, distance);
		assertEquals("0.9", DrawAxis.tickDescription(view, 3, 0));
	}

	@Test
	void testFractionDescription() {
		GeoNumeric distance = add("3/10");
		view.getSettings().setAxisNumberingDistance(0, distance);
		assertEquals("9 / 10", DrawAxis.tickDescription(view, 3, 0));
	}

	@Test
	void testPaintTicksDefaultArrows() {
		view.getSettings().showGrid(false);
		snapshotChecker.assertMatch("drawAxis1.svg", getSnapshot());
	}

	@Test
	void testPaintTicksWithArrows() {
		view.getSettings().showGrid(false);
		view.getSettings().setAxesLineStyle(EuclidianStyleConstants.AXES_LINE_TYPE_TWO_ARROWS);
		view.setCoordSystem(655, 395, 50, 50);
		snapshotChecker.assertMatch("drawAxis2.svg", getSnapshot());
	}

	@Test
	void testPaintTicksNoArrows() {
		view.getSettings().showGrid(false);
		view.getSettings().setAxesLineStyle(EuclidianStyleConstants.AXES_LINE_TYPE_FULL);
		view.setCoordSystem(655, 359, 50, 50);
		snapshotChecker.assertMatch("drawAxis3.svg", getSnapshot());
	}

	private String getSnapshot() {
		EuclidianView view = getApp().getActiveEuclidianView();
		GGraphics2D svg = new SvgGraphics(800, 600);
		view.paint(svg);
		return svg.toString();
	}
}
