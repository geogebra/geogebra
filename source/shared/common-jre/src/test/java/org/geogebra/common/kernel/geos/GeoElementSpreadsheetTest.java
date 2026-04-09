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

package org.geogebra.common.kernel.geos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.geogebra.common.spreadsheet.core.SpreadsheetCoords;
import org.geogebra.regexp.server.JavaRegExpFactory;
import org.geogebra.regexp.shared.RegExpFactory;
import org.junit.jupiter.api.Test;

public class GeoElementSpreadsheetTest {

	static {
		RegExpFactory.setPrototypeIfNull(new JavaRegExpFactory());
	}

	@Test
	void testSpreadsheetCoords() {
		SpreadsheetCoords lastCell = GeoElementSpreadsheet.getSpreadsheetCoordsForLabel("NTO9999");
		assertNotNull(lastCell);
		assertEquals(9998, lastCell.row);
		assertEquals(9998, lastCell.column);
	}
}
