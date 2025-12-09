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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.geogebra.common.jre.util.UtilFactoryJre;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@SuppressWarnings("checkstyle:RegexpSinglelineCheck") // Tabs in CsvSources
public class ChartErrorTest {

	@BeforeAll
	public static void setupOnce() {
		UtilFactoryJre.setupRegexFactory();
	}

	@ParameterizedTest
	@CsvSource(delimiterString = "->", value = {
			"A1:A10         -> NONE",
			"A1:A10,B1:B10  -> NONE",
			"A1:A3,B1:D1    -> NONE",
			"A1:B3          -> NONE",			// 3x2 rectangular range
			"A1:C2          -> NONE",			// 2x3 rectangular range
			"               -> NoData",
			"A1             -> NoData",			// single cell
			"A1:C3          -> InvalidData",	// 3x3 rectangular range
			"A1:A10,B1:B11  -> InvalidData",	// different sizes
			"A1:A3,B1:E1    -> InvalidData",	// different sizes
	})
	public void testValidateRangesForBoxPlot(String rangeSpec, String expectedResult) {
		List<TabularRange> ranges = makeRanges(rangeSpec);
		assertEquals(expectedResult, ChartError.validateRangesForBoxPlot(ranges).name());
	}

	/**
	 * Test utility method for creating ranges from strings. DO NOT USE ELSEWHERE.
	 * @param rangeSpec A valid range specifier, e.g. "A1", or "A1:B3".
	 * @return A range matching the input string.
	 */
	private List<TabularRange> makeRanges(String rangeSpec) {
		if (rangeSpec == null || rangeSpec.isBlank()) {
			return List.of();
		}
		String[] rangeSpecs = rangeSpec.split(",");
		return Arrays.stream(rangeSpecs)
				.map(this::makeRange)
				.collect(Collectors.toList());
	}

	private TabularRange makeRange(String spec) {
		SpreadsheetReference reference = SpreadsheetReferenceParsing.parseReference(spec);
		SpreadsheetCellReference fromCell = reference.fromCell;
		SpreadsheetCellReference toCell =
				reference.toCell != null ? reference.toCell : fromCell;
		return new TabularRange(
				fromCell.rowIndex, fromCell.columnIndex,
				toCell.rowIndex, toCell.columnIndex);
	}
}
