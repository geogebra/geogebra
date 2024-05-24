package org.geogebra.common.spreadsheet.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.spreadsheet.kernel.KernelTabularDataAdapter;
import org.junit.Before;
import org.junit.Test;

public class CellReferenceHandlerTest extends BaseUnitTest {

	private TabularData<GeoElement> tabularData;

	@Before
	public void setUp() {
		tabularData = new KernelTabularDataAdapter(getSettings().getSpreadsheet());
	}

	@Test
	public void testContainsDynamicReference1() {
		assertContainsDynamicReference("=B1 + 2");
	}

	@Test
	public void testContainsDynamicReference2() {
		assertContainsDynamicReference("=B1 - C2");
	}

	@Test
	public void testContainsDynamicReference3() {
		assertContainsDynamicReference("=3T2 * 2");
	}

	@Test
	public void testContainsNoDynamicReference1() {
		assertContainsNoDynamicReference("=A + 3");
	}

	@Test
	public void testContainsNoDynamicReference2() {
		assertContainsNoDynamicReference("=AA 1 / 3");
	}

	@Test
	public void testContainsNoDynamicReference3() {
		assertContainsNoDynamicReference("=C 1 + 3");
	}

	@Test
	public void testCorrectReferenceSubstitution1() {
		tabularData.setContent(1, 1, add("=12"));
		tabularData.setContent(2, 1, add("=B2 + 3"));
		assertSubstitutedReferenceEquals("=B3+3",
				tabularData.contentAt(2, 1).getDefinitionForEditor(),
				2, 3, 1, 1);
	}

	@Test
	public void testCorrectReferenceSubstitution2() {
		tabularData.setContent(1, 1, add("=243"));
		tabularData.setContent(2, 1, add("=B2 / 3"));
		assertSubstitutedReferenceEquals("=(B5)/(3)",
				tabularData.contentAt(2, 1).getDefinitionForEditor(),
				2, 5, 1, 1);
	}

	@Test
	public void testCorrectReferenceSubstitution3() {
		tabularData.setContent(1, 2, add("=15"));
		tabularData.setContent(1, 3, add("=C2 * 2"));
		assertSubstitutedReferenceEquals("=D2*2",
				tabularData.contentAt(1, 3).getDefinitionForEditor(),
				1, 1, 3, 4);
	}

	@Test
	public void testCorrectReferenceSubstitution4() {
		tabularData.setContent(2, 2, add("=15"));
		tabularData.setContent(1, 2, add("=C3 - 3"));
		assertSubstitutedReferenceEquals("=C2-3",
				tabularData.contentAt(1, 2).getDefinitionForEditor(),
				1, 0, 2, 2);
	}

	private void assertContainsDynamicReference(String definition) {
		assertTrue("There should be a dynamic reference!",
				CellReferenceHandler.containsDynamicReference(definition));
	}

	private void assertContainsNoDynamicReference(String definition) {
		assertFalse("There should be no dynamic references being detected!",
				CellReferenceHandler.containsDynamicReference(definition));
	}

	/**
	 * @param definition The definition of the GeoElement containing the dynamic reference, which
	 * is being copied somewhere
	 * Others: Copy from / to
	 */
	private void assertSubstitutedReferenceEquals(String expected, String definition,
			int sourceRow, int targetRow, int sourceColumn, int targetColumn) {
		String actual = CellReferenceHandler.getDefinitionWithSubstitutedDynamicReferences(
				definition, sourceRow, targetRow, sourceColumn, targetColumn);
		assertEquals("The values do not match!", expected, actual.substring(actual.indexOf('=')));
	}
}
