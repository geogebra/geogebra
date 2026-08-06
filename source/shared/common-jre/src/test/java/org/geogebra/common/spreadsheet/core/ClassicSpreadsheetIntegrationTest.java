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

package org.geogebra.common.spreadsheet.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Objects;

import org.geogebra.test.BaseAppTestSetup;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClassicSpreadsheetIntegrationTest extends BaseAppTestSetup {

	@BeforeEach
	void setup() {
		setupClassicApp();
	}

	@Test
	@Issue("APPS-7767")
	void xmlShouldNotSquashCells() {
		getApp().getGgbApi().setXML("""
				<geogebra format="5.0" version="5.2.871.0" app="classic">
				<spreadsheetView>
					<size  width="864" height="900"/>
					<prefCellSize height="0"/>
					<selection  hScroll="0" vScroll="0" column="2" row="1"/>
				</spreadsheetView>
				</geogebra>
				""");
		assertEquals(2525.0,
				Objects.requireNonNull(getApp().getSpreadsheet()).getTotalHeight(), .1);
	}
}
