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

package org.geogebra.common.gui.view.spreadsheet;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CopyPasteAdapterTest extends BaseAppTestSetup {

	@BeforeEach
	void setupApp() {
		super.setupApp(SuiteSubApp.GRAPHING);
	}

	@Test
	void testRedefine() {
		evaluate("A1=\"h\"");
		String csv = """
				"Index", "Eruption length (mins)","Eruption wait (mins)"
				1, 3.600, 79
				""".stripIndent();
		String[][] data = DataImport.parseExternalData(getApp(), csv, true);
		int maxColumn = data.length > 0 ? data[0].length - 1 : 0;
		new CopyPasteAdapter(getApp(), getApp().getSpreadsheetTableModel())
				.pasteExternal(data, 0, 0, maxColumn, data.length);
		assertEquals("79", lookup("C2").toValueString(StringTemplate.testTemplate));
	}
}
