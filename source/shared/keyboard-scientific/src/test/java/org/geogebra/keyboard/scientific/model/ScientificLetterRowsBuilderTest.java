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

package org.geogebra.keyboard.scientific.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ScientificLetterRowsBuilderTest {

	private ScientificLetterRowsBuilder rowsBuilder;

	@Before
	public void setUp() {
		rowsBuilder = new ScientificLetterRowsBuilder();
	}

	@Test
	public void rowsFrom() {
		String topRow = "qwertyuiop";
		String middleRow = "asdfghjkl";
		String bottomRow = "zxcvbnm";
		String[] rows = rowsBuilder.rowsFrom(topRow, middleRow, bottomRow);
		assertEquals("abcdefghi", rows[0]);
		assertEquals("jklmnopqr", rows[1]);
		assertEquals("stuvwxyz", rows[2]);
	}
}