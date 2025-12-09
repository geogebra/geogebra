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

package org.geogebra.common.main.settings;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.junit.Test;

public class SpreadsheetSettingsTest {
	AppCommon app = AppCommonFactory.create3D();

	@Test
	public void testSpreadsheetSettingsShouldBeUpdatedOnFileLoad() {
		app.getSettings().getSpreadsheet().addWidth(2, 42);
		String xml = app.getXML();
		app.getSettings().getSpreadsheet().addWidthNoFire(2, 50);
		app.setXML(xml, true);
		assertEquals(42, app.getSettings().getSpreadsheet().getColumnWidths().get(2), 0.1);
	}
}
