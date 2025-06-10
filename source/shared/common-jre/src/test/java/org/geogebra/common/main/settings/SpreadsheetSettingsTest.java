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
