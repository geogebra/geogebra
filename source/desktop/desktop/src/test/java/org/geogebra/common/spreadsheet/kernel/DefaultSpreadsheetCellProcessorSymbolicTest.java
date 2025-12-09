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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.BaseSymbolicTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.GeoClass;
import org.junit.Before;
import org.junit.Test;

public class DefaultSpreadsheetCellProcessorSymbolicTest extends BaseSymbolicTest {
	private DefaultSpreadsheetCellProcessor processor;

	@Before
	public void setUp() {
		processor = new DefaultSpreadsheetCellProcessor(kernel.getAlgebraProcessor());
		kernel.attach(new KernelTabularDataAdapter(app));
	}

	@Test
	public void testNumbersShouldBeLockedByDefault() {
		GeoElement geo = add("7");
		assertTrue("Numbers should be locked in CAS View", geo.isLocked());
	}

	@Test
	public void testNumbersInSpreadsheetShouldBeModifiedInCas() {
		processor.process("7", "A1");
		assertFalse("Numbers should not be locked in Spreadsheet CAS", lookup("A1")
				.isLocked());
	}

	@Test
	public void testOperationsOnNumberCellsShouldBeModifiedInCas() {
		processor.process("7", "A1");
		processor.process("3", "A2");
		processor.process("A1 + A2", "A3");
		assertFalse("Numbers should not be locked in Spreadsheet CAS", lookup("A3")
				.isLocked());
	}

	@Test
	public void testInvalidInput() {
		processor.process("=atan", "A1");
		assertThat(lookup("A1").getGeoClassType(), equalTo(GeoClass.SYMBOLIC));
		processor.process("=1+", "A2");
		assertThat(lookup("A2").getGeoClassType(), equalTo(GeoClass.NUMERIC));
		AlgoElement parentAlgorithm = lookup("A2").getParentAlgorithm();
		assertNotNull(parentAlgorithm);
		assertThat(parentAlgorithm.getClassName(), equalTo(Commands.ParseToNumber));
	}

	@Test
	public void shouldCreateEmptyCells() {
		processor.process("=A1+1", "B1");
		assertThat(lookup("A1"), hasValue("0"));
		processor.process("=1", "A1");
		assertThat(lookup("B1"), hasValue("2"));
	}

	@Test
	public void shouldNotChangeReferencingCellAfterReEditing() {
		processor.process("=B1", "A1");
		processor.process("=B1", "A1");
		assertThat(lookup("A1"), hasValue("0"));
	}
}
