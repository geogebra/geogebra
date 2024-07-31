package org.geogebra.common.kernel.cas;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.kernel.geos.BaseSymbolicTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.spreadsheet.kernel.DefaultSpreadsheetCellProcessor;
import org.geogebra.common.spreadsheet.kernel.KernelTabularDataAdapter;
import org.junit.Before;
import org.junit.Test;

public class DefaultSpreadsheetCellProcessorSymbolicTest extends BaseSymbolicTest {
	private DefaultSpreadsheetCellProcessor processor;

	@Before
	public void setUp() {
		ErrorHandler errorHandler = app.getDefaultErrorHandler();
		processor =
				new DefaultSpreadsheetCellProcessor(kernel.getAlgebraProcessor(),
						errorHandler);
		kernel.attach(new KernelTabularDataAdapter(
				app.getSettings().getSpreadsheet(), kernel));
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
}
