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

package org.geogebra.common.properties.remembered.handlers;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.draw.DrawInlineTable;
import org.geogebra.common.euclidian.draw.DrawInlineText;
import org.geogebra.common.euclidian.inline.InlineTableController;
import org.geogebra.common.euclidian.inline.InlineTextController;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoInlineTable;
import org.geogebra.common.kernel.geos.GeoInlineText;
import org.geogebra.common.kernel.geos.GeoMindMapNode;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.properties.PropertyKey;
import org.geogebra.common.properties.impl.objects.FontProperty;
import org.geogebra.common.properties.impl.objects.FontProperty.FontFamily;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RememberedFontFamilyHandlerTest extends BaseAppTestSetup {
	private final RememberedFontFamilyHandler handler = new RememberedFontFamilyHandler();

	@BeforeEach
	void setUp() {
		setupClassicApp();
	}

	@Test
	void testPropertyKey() {
		assertEquals(PropertyKey.of(FontProperty.class), handler.propertyKey());
	}

	@Test
	void testSupports() {
		Construction construction = getConstruction();
		assertAll(
				() -> assertTrue(handler.supports(new GeoInlineText(construction, null))),
				() -> assertTrue(handler.supports(new GeoMindMapNode(construction, null))),
				() -> assertTrue(handler.supports(new GeoInlineTable(construction, null))),
				() -> assertFalse(handler.supports(new GeoText(construction, null))),
				() -> assertFalse(handler.supports(new GeoAngle(construction, 0)))
		);
	}

	@Test
	void testApplyOnUnsupported() {
		Construction construction = getConstruction();
		assertAll(
				() -> assertFalse(handler.apply(new GeoText(construction, null),
						FontFamily.COURIER)),
				() -> assertFalse(handler.apply(new GeoAngle(construction, 0),
						FontFamily.COMIC_SANS))
		);
	}

	@Test
	void testApplyOnInlineText() {
		GeoInlineText inlineText = new GeoInlineText(getConstruction(), new GPoint2D(0, 0));
		inlineText.setLabel("text");
		DrawableND drawable = getApp().getEuclidianView1().getDrawableFor(inlineText);
		assertNotNull(drawable);
		InlineTextController controller = mock(InlineTextController.class);
		((DrawInlineText) drawable).setTextController(controller);
		assertTrue(handler.apply(inlineText, FontFamily.COMIC_SANS));
		verify(controller).formatFont(FontFamily.COMIC_SANS.cssName());
	}

	@Test
	void testApplyOnInlineTable() {
		GeoInlineTable inlineTable = new GeoInlineTable(getConstruction(), new GPoint2D(0, 0));
		inlineTable.setLabel("table1");
		DrawableND drawable = getApp().getEuclidianView1().getDrawableFor(inlineTable);
		assertNotNull(drawable);
		InlineTableController controller = mock(InlineTableController.class);
		((DrawInlineTable) drawable).setTextController(controller);
		assertTrue(handler.apply(inlineTable, FontFamily.COMIC_SANS));
		verify(controller).formatFont(FontFamily.COMIC_SANS.cssName());
	}

	@Test
	void testApplyBeforeFormatterIsAvailable() {
		GeoInlineText inlineText = new GeoInlineText(getConstruction(), new GPoint2D(0, 0));
		assertFalse(handler.apply(inlineText, FontFamily.ARIAL));
	}

}
