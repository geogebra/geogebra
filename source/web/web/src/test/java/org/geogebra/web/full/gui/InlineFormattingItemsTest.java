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

package org.geogebra.web.full.gui;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.draw.DrawInlineTable;
import org.geogebra.common.euclidian.draw.DrawInlineText;
import org.geogebra.common.euclidian.inline.InlineTableController;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInlineTable;
import org.geogebra.common.kernel.geos.GeoInlineText;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.web.awt.MyImageW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gwtmockito.WithClassesToStub;

@RunWith(GgbMockitoTestRunner.class)
@WithClassesToStub(MyImageW.class)
public class InlineFormattingItemsTest {

	private static final String LINK_URL = "www.foo.bar";
	private ContextMenuMock contextMenu;
	private Construction construction;
	private AppW app;
	private GPoint2D point;
	private InlineTextControllerMock controllerMockWithLink;

	@Before
	public void setUp() {
		app = AppMocker.mockNotes();
		construction = app.getKernel().getConstruction();
		point = new GPoint2D(0, 0);
		controllerMockWithLink = new InlineTextControllerMock(LINK_URL);
		contextMenu = new ContextMenuMock(app);
		enableSettingsItem();
	}

	private void enableSettingsItem() {
		app.setShowMenuBar(true);
		app.setRightClickEnabled(true);
	}

	@Test
	public void testSingleInlineTextContextMenu() {
		ArrayList<GeoElement> geos = new ArrayList<>();
		geos.add(createTextInline("text1", new InlineTextControllerMock()));
		List<String> expected = Arrays.asList(
				"TEXTTOOLBAR", "ContextMenu.Font", "Link",
				"SEPARATOR", "Cut", "Copy", "Paste",
				"SEPARATOR", "General.Order",
				"SEPARATOR",
				"FixObject", "Settings"
		);

		assertEquals(expected, contextMenu.getEntriesFor(geos));
	}

	@Test
	public void testEditModeInlineTableContextMenu() {
		ArrayList<GeoElement> geos = new ArrayList<>();
		geos.add(createTableInline(InlineTableControllerMock.getWithSelection(true)));
		List<String> expected = Arrays.asList(
				"TEXTTOOLBAR",
				"ContextMenu.Font",
				"Link",
				"ContextMenu.textWrapping",
				"ContextMenu.textRotation",
				"ContextMenu.Heading",
				"SEPARATOR",
				"ContextMenu.CreateChart",
				"SEPARATOR",
				"Cut", "Copy", "Paste",
				"SEPARATOR",
				"ContextMenu.insertRowAbove",
				"ContextMenu.insertRowBelow",
				"ContextMenu.insertColumnLeft",
				"ContextMenu.insertColumnRight",
				"SEPARATOR",
				"ContextMenu.deleteRow",
				"ContextMenu.deleteColumn"
		);

		assertEquals(expected, contextMenu.getEntriesFor(geos));
	}

	@Test
	public void testMultiCellEditContextMenu() {
		ArrayList<GeoElement> geos = new ArrayList<>();
		geos.add(createTableInline(InlineTableControllerMock.getWithSelection(false)));
		List<String> expected = Arrays.asList(
				"TEXTTOOLBAR",
				"ContextMenu.Font",
				"Link",
				"ContextMenu.textWrapping",
				"ContextMenu.textRotation",
				"ContextMenu.Heading",
				"SEPARATOR",
				"ContextMenu.CreateChart"
		);

		assertEquals(expected, contextMenu.getEntriesFor(geos));
	}

	@Test
	public void testSingleInlineTableContextMenu() {
		ArrayList<GeoElement> geos = new ArrayList<>();
		geos.add(createTableInline(InlineTableControllerMock.get()));
		List<String> expected = Arrays.asList(
				"ContextMenu.Font",
				"ContextMenu.textWrapping",
				"ContextMenu.textRotation",
				"ContextMenu.Heading",
				"SEPARATOR",
				"ContextMenu.CreateChart",
				"SEPARATOR", "Cut", "Copy", "Paste",
				"SEPARATOR", "General.Order",
				"SEPARATOR",
				"FixObject", "Settings"
		);

		assertEquals(expected, contextMenu.getEntriesFor(geos));
	}

	@Test
	public void testTextAndTableContextMenu() {
		ArrayList<GeoElement> geos = new ArrayList<>();
		geos.add(createTextInline("text1", new InlineTextControllerMock()));
		geos.add(createTableInline(InlineTableControllerMock.get()));
		List<String> expected = Arrays.asList(
				"ContextMenu.Font",
				"SEPARATOR", "Cut", "Copy", "Paste",
				"SEPARATOR", "General.Order",
				"SEPARATOR",
				"FixObject", "Settings"
		);

		assertEquals(expected, contextMenu.getEntriesFor(geos));
	}

	@Test
	public void testSingleInlineTextWithLinkContextMenu() {
		ArrayList<GeoElement> geos = new ArrayList<>();
		geos.add(createTextInline("text1", controllerMockWithLink));
		List<String> expected = Arrays.asList(
				"TEXTTOOLBAR", "ContextMenu.Font", "editLink", "removeLink",
				"SEPARATOR", "Cut", "Copy", "Paste",
				"SEPARATOR", "General.Order",
				"SEPARATOR",
				"FixObject", "Settings"
		);

		assertEquals(expected, contextMenu.getEntriesFor(geos));
	}

	@Test
	public void testMultipleInlineTextContextMenu() {
		ArrayList<GeoElement> geos = new ArrayList<>();
		geos.add(createTextInline("text1", controllerMockWithLink));
		geos.add(createTextInline("text2", controllerMockWithLink));
		List<String> expected = Arrays.asList(
				"TEXTTOOLBAR", "ContextMenu.Font",
				"SEPARATOR", "Cut", "Copy", "Paste",
				"SEPARATOR", "General.Order",
				"SEPARATOR",
				"FixObject", "Settings"
		);

		assertEquals(expected, contextMenu.getEntriesFor(geos));
	}

	@Test
	public void testInlineTextAndPolygonContextMenu() {
		ArrayList<GeoElement> geos = new ArrayList<>();
		geos.add(createTextInline("text1", new InlineTextControllerMock(LINK_URL)));
		geos.add(createPolygon("poly1"));
		List<String> expected = Arrays.asList(
				"Cut", "Copy", "Paste",
				"SEPARATOR", "General.Order",
				"SEPARATOR",
				"FixObject", "Settings"
		);

		assertEquals(expected, contextMenu.getEntriesFor(geos));
	}

	@Test
	public void testPolygonContextMenu() {
		ArrayList<GeoElement> geos = new ArrayList<>();
		geos.add(createPolygon("Poly1"));
		List<String> expected = Arrays.asList(
				"Cut", "Copy", "Paste", "SEPARATOR", "General.Order", "SEPARATOR",
				"FixObject", "Settings"
		);

		assertEquals(expected, contextMenu.getEntriesFor(geos));
	}

	@Test
	public void testMaskContextMenu() {
		ArrayList<GeoElement> geos = new ArrayList<>();
		geos.add(createMask());
		List<String> expected = Arrays.asList(
				"Cut", "Copy", "Paste", "SEPARATOR",
				"FixObject", "Settings"
		);

		assertEquals(expected, contextMenu.getEntriesFor(geos));
	}

	private GeoElement createMask() {
		GeoPolygon polygon = (GeoPolygon) createPolygon("mask1");
		polygon.setIsMask(true);
		return polygon;
	}

	private GeoElement createPolygon(String label) {
		GeoPolygon poly = new GeoPolygon(construction);
		poly.setLabel(label);
		return poly;
	}

	private GeoInlineText createTextInline(String label,
			InlineTextControllerMock inlineTextControllerMock) {
		GeoInlineText text = new GeoInlineText(construction, point);
		text.setLabel(label);
		DrawInlineText drawInlineText = (DrawInlineText) app.getActiveEuclidianView()
				.getDrawableFor(text);
		assertNotNull(drawInlineText);
		drawInlineText.setTextController(inlineTextControllerMock);
		return text;
	}

	private GeoInlineTable createTableInline(InlineTableController inlineTextController) {
		GeoInlineTable table = new GeoInlineTable(construction, point);
		table.setLabel("table1");
		DrawInlineTable drawInlineTable = (DrawInlineTable) app.getActiveEuclidianView()
				.getDrawableFor(table);
		assertNotNull(drawInlineTable);
		drawInlineTable.setTextController(inlineTextController);
		return table;
	}

	@Test
	public void testGroupMultipleTextSingleSelectContextMenu() {
		ArrayList<GeoElement> geos = new ArrayList<>();
		geos.add(createTextInline("text1", controllerMockWithLink));
		geos.add(createTextInline("text2", controllerMockWithLink));
		construction.createGroup(geos);
		app.getSelectionManager().setFocusedGroupElement(geos.get(0));
		List<String> expected = Arrays.asList(
				"TEXTTOOLBAR", "ContextMenu.Font",
				"editLink", "removeLink",
				"SEPARATOR", "General.Order"
		);

		assertEquals(expected, contextMenu.getEntriesFor(geos));
	}

	@Test
	public void testGroupTextAndPolygonSingleSelectContextMenu() {
		ArrayList<GeoElement> geos = new ArrayList<>();
		geos.add(createTextInline("text1", new InlineTextControllerMock(LINK_URL)));
		geos.add(createPolygon("poly1"));
		app.getSelectionManager().setFocusedGroupElement(geos.get(1));
		construction.createGroup(geos);
		List<String> expected = Collections.singletonList("General.Order");

		assertEquals(expected, contextMenu.getEntriesFor(geos));
	}
}