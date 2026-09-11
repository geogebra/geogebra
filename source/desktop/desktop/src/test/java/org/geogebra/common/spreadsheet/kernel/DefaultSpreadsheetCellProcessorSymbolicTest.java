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

import static org.geogebra.common.BaseUnitTest.hasValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.BaseSymbolicTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.GeoClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultSpreadsheetCellProcessorSymbolicTest extends BaseSymbolicTest {
	private DefaultSpreadsheetCellProcessor processor;

	@BeforeEach
	void setUp() {
		processor = new DefaultSpreadsheetCellProcessor(kernel.getAlgebraProcessor());
		kernel.attach(new KernelTabularDataAdapter(app));
	}

	@Test
	void testNumbersShouldBeLockedByDefault() {
		GeoElement geo = add("7");
		assertTrue(geo.isLocked(), "Numbers should be locked in CAS View");
	}

	@Test
	void testNumbersInSpreadsheetShouldBeModifiedInCas() {
		processor.process("7", "A1");
		assertFalse(lookup("A1")
				.isLocked(), "Numbers should not be locked in Spreadsheet CAS");
	}

	@Test
	void testOperationsOnNumberCellsShouldBeModifiedInCas() {
		processor.process("7", "A1");
		processor.process("3", "A2");
		processor.process("A1 + A2", "A3");
		assertFalse(lookup("A3")
				.isLocked(), "Numbers should not be locked in Spreadsheet CAS");
	}

	@Test
	void testInvalidInput() {
		processor.process("=atan", "A1");
		assertThat(lookup("A1").getGeoClassType(), equalTo(GeoClass.SYMBOLIC));
		processor.process("=1+", "A2");
		assertThat(lookup("A2").getGeoClassType(), equalTo(GeoClass.NUMERIC));
		AlgoElement parentAlgorithm = lookup("A2").getParentAlgorithm();
		assertNotNull(parentAlgorithm);
		assertThat(parentAlgorithm.getClassName(), equalTo(Commands.ParseToNumber));
	}

	@Test
	void shouldCreateEmptyCells() {
		processor.process("=A1+1", "B1");
		assertThat(lookup("A1"), hasValue("0"));
		processor.process("=1", "A1");
		assertThat(lookup("B1"), hasValue("2"));
	}

	@Test
	void shouldNotChangeReferencingCellAfterReEditing() {
		processor.process("=B1", "A1");
		processor.process("=B1", "A1");
		assertThat(lookup("A1"), hasValue("0"));
	}
}
