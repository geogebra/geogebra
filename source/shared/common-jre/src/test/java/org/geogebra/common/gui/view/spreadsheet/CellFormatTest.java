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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.spreadsheet.core.TabularRange;
import org.geogebra.common.spreadsheet.style.CellFormat;
import org.geogebra.test.annotation.Issue;
import org.junit.BeforeClass;
import org.junit.Test;

public class CellFormatTest {
	private static AppCommon app;

	@BeforeClass
	public static void setup() {
		app = AppCommonFactory.create3D();
	}

	@Test
	public void tableTextRowsColumnsMatch() {
		t("A1=1");
		t("A2=1");
		t("B1=1");
		t("B2=1");
		CellRangeProcessor cp = new CellRangeProcessor(null, app);
		GeoElementND table = cp.createTableText(0, 1, 0, 1, false, false);
		assertEquals("TableText({{A1, B1}, {A2, B2}}, \"|_ll\")",
				table.getDefinition(StringTemplate.defaultTemplate));
	}

	@Test
	@Issue("APPS-5552")
	public void testGetCellFormatRange() {
		TabularRange fullRange = new TabularRange(-1, -1, -1, -1);
		CellFormat format = new CellFormat(null);
		TabularRange rectRange = new TabularRange(1, 2, 3, 4);
		format.setFormat(fullRange, CellFormat.FORMAT_ALIGN, CellFormat.ALIGN_LEFT);
		format.setFormat(rectRange, CellFormat.FORMAT_ALIGN, CellFormat.ALIGN_CENTER);
		TabularRange subrange = new TabularRange(1, 2, 1, 2);
		assertThat(format.getCellFormat(subrange, CellFormat.FORMAT_ALIGN),
				equalTo(CellFormat.ALIGN_CENTER));
		TabularRange superRange = new TabularRange(1, 2, 4, 5);
		assertThat(format.getCellFormat(superRange, CellFormat.FORMAT_ALIGN), nullValue());
		// in this case return value is not important, just check there is no runtime exception
		assertThat(format.getCellFormat(fullRange, CellFormat.FORMAT_ALIGN),
				equalTo(CellFormat.ALIGN_LEFT));
	}

	private void t(String string) {
		app.getKernel().getAlgebraProcessor().processAlgebraCommand(string,
				false);
	}
}
