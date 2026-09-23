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

package org.geogebra.web.html5.io;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianPen;
import org.geogebra.common.main.settings.PenToolsSettings;
import org.geogebra.web.awt.JLMContext2D;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.gwtproject.user.client.ui.RootPanel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwtmockito.WithClassesToStub;

@RunWith(GgbMockitoTestRunner.class)
@WithClassesToStub({JLMContext2D.class, RootPanel.class})
public class TemplateLoadTest {
	private static AppWFull app;
	private static PenToolsSettings settings;

	@Before
	public void init() {
		AppletParameters articleElement = new AppletParameters("notes");
		app = AppMocker.mockApplet(articleElement);
		app.setXML("""
						<?xml version="1.0" encoding="utf-8"?>
						<geogebra format="5.0" \
						version="5.0.570.0" app="notes" platform="w" \
						id="F3D7E07C-758A-4337-A238-9A7EC72361E1"  \
						xsi:noNamespaceSchemaLocation="http://www.geogebra.org/ggb.xsd" xmlns="" \
						xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" >
						<gui>
						\t<window width="953" height="787" />
						\t<labelingStyle  val="0"/>
						\t<font size="16"/>
						</gui>
						<euclidianView>
						\t<viewNumber viewNo="1"/>
						\t<size width="953" height="787"/>
						\t<coordSystem xZero="476.4999999999999" \
						yZero="393.5" scale="49.99999999999999" yscale="50"/>
						\t<evSettings axes="false" grid="false" gridIsBold="false"\
						pointCapturing="3" rightAngleStyle="1" checkboxSize="26" gridType="3"/>
						\t<bgColor r="255" g="255" b="255"/>
						\t<axesColor r="0" g="0" b="0"/>
						\t<gridColor r="192" g="192" b="192"/>
						\t<rulerType val="2" bold="true"/>
						\t<rulerColor r="128" g="0" b="128"/>
						\t<penSize val="30"/>
						\t<penColor r="204" g="0" b="153"/>
						\t<highlighterSize val="1"/>
						\t<highlighterColor r="219" g="97" b="20"/>
						\t<eraserSize val="61"/>
						\t<language val="hu"/>
						</euclidianView>
						<kernel>
						\t<continuous val="false"/>
						\t<usePathAndRegionParameters val="true"/>
						\t<decimals val="2"/>
						\t<angleUnit val="degree"/>
						\t<algebraStyle val="0" spreadsheet="0"/>
						\t<coordStyle val="0"/>
						</kernel>
						<tableview min="-2" max="2" step="1"/>
						<scripting blocked="false" disabled="false"/>
						<construction title="templateTest" author="" date="">
						</construction>
						</geogebra>""", false);
		settings = app.getSettings().getPenTools();
	}

	@Test
	public void testLoadTemplatePenThickness() {
		assertEquals(60, settings.getLastPenThickness());
	}

	@Test
	public void testLoadTemplatePenColor() {
		GColor penColor = GColor.newColor(204, 0, 153);
		assertEquals(penColor, settings.getLastSelectedPenColor());
		getPen().updateMode();
		assertEquals(penColor, getPen().getPenColor());
	}

	@Test
	public void testLoadTemplateHighlighterThickness() {
		assertEquals(2, settings.getLastHighlighterThickness());
	}

	@Test
	public void testLoadTemplateHighlighterColor() {
		GColor highlighterColor = GColor.newColor(219, 97, 20);
		assertEquals(highlighterColor, settings.getLastSelectedHighlighterColor());
		app.setMode(EuclidianConstants.MODE_HIGHLIGHTER);
		getPen().updateMode();
		assertEquals(highlighterColor, getPen().getPenColor());
	}

	private EuclidianPen getPen() {
		return app.getActiveEuclidianView().getEuclidianController().getPen();
	}

	@Test
	public void testLoadTemplateEraserSize() {
		assertEquals(61, settings.getDeleteToolSize());
	}
}
