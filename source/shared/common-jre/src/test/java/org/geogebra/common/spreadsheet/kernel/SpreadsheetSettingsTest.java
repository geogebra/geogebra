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

package org.geogebra.common.spreadsheet.kernel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashSet;
import java.util.Set;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.awt.GFont;
import org.geogebra.common.awt.GGraphicsCommon;
import org.geogebra.common.spreadsheet.core.Spreadsheet;
import org.geogebra.common.util.shape.Rectangle;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SpreadsheetSettingsTest extends BaseAppTestSetup {

	@BeforeEach
	public void setup() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@Test
	public void testSpreadsheetFontSize() {
		Spreadsheet spreadsheet = getApp().getSpreadsheet();
		assertEquals(12.0, getApp().getFontSizeDouble(), 0);
		spreadsheet.setViewport(new Rectangle(0, 200, 0, 200));
		Set<Double> usedFonts = new HashSet<>();
		evaluate("A1=1");
		GGraphicsCommon graphics = new GGraphicsCommon() {
			@Override
			public void setFont(GFont font) {
				super.setFont(font);
				usedFonts.add(font.getSize());
			}
		};
		assertNotNull(spreadsheet);
		spreadsheet.draw(graphics);
		assertEquals(Set.of(12.0), usedFonts);
		usedFonts.clear();
		getApp().setFontSize(32, true);
		spreadsheet.draw(graphics);
		assertEquals(Set.of(32.0), usedFonts);
	}
}
