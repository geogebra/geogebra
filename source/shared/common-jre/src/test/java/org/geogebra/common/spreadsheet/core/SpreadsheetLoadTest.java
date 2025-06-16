package org.geogebra.common.spreadsheet.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.BaseAppTestSetup;
import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.main.settings.SpreadsheetSettings;
import org.geogebra.common.spreadsheet.kernel.KernelTabularDataAdapter;
import org.geogebra.common.spreadsheet.settings.SpreadsheetSettingsAdapter;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SpreadsheetLoadTest extends BaseAppTestSetup {

	@BeforeEach
	public void setup() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	@Test
	@Issue("APPS-6566")
	public void initialSize() {
		KernelTabularDataAdapter tabularData = new KernelTabularDataAdapter(getApp());
		SpreadsheetSettings spreadsheetSettings = getApp().getSettings().getSpreadsheet();
		spreadsheetSettings.setColumnsNoFire(3);
		spreadsheetSettings.getColumnWidths().put(1, 500.0);
		Spreadsheet spreadsheet = new Spreadsheet(tabularData,
				new SpreadsheetTest.TestCellRenderableFactory(),
				null);
		new SpreadsheetSettingsAdapter(spreadsheet, getApp()).registerListeners();
		assertEquals(500 + 2 * 120 + 52, spreadsheet.getTotalWidth());
	}
}
