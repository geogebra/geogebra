package org.geogebra.common.gui.view.table;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoList;
import org.junit.Before;
import org.junit.Test;

public class TableValuesInputProcessorTest extends BaseUnitTest {

	private TableValues view;
	private TableValuesModel model;
	private TableValuesInputProcessor processor;
	private GeoList list;

	@Before
	public void setUp() {
		view = new TableValuesView(getKernel());
		getKernel().attach(view);
		model = view.getTableValuesModel();
		processor = new TableValuesInputProcessor(getConstruction(), view);
		list = new GeoList(getConstruction());
	}

	@Test
	public void testValidInput() {
		try {
			processor.processInput("1", list, 0);
			processor.processInput("2", list, 1);
			processor.processInput("99.9", list, 2);
			processor.processInput("0.01", list, 3);
			processor.processInput("", list, 4);
			processor.processInput("20e-2", list, 6);
			processor.processInput("10", list, 10);
			assertThat(list.size(), is(11));
			assertValue("1", 0);
			assertValue("2", 1);
			assertValue("99.9", 2);
			assertValue("0.01", 3);
			assertValue("?", 4);
			assertValue("?", 5);
			assertValue("0.2", 6);
			assertValue("10", 10);
		} catch (InvalidInputException e) {
			fail("Must not throw an exception");
		}
	}

	private void assertValue(String value, int index) {
		assertThat(list.getListElement(index).toString(StringTemplate.defaultTemplate), is(value));
	}

	@Test(expected = NullPointerException.class)
	public void testNullInput() throws InvalidInputException {
		processor.processInput(null, list, 0);
	}

	@Test(expected = InvalidInputException.class)
	public void testInvalidInputWithComma() throws InvalidInputException {
		processor.processInput("10,2", list, 0);
	}

	@Test(expected = InvalidInputException.class)
	public void testInvalidInputWithOperators() throws InvalidInputException {
		processor.processInput("10 + 2", list, 0);
	}

	@Test(expected = InvalidInputException.class)
	public void testInvalidInputWithLetters() throws InvalidInputException {
		processor.processInput("a", list, 0);
	}

	@Test
	public void testProcessorWithEmptyList() throws InvalidInputException {
		processor.processInput("1", null, 2);
		assertEquals("1", model.getCellAt(2, 1));
		assertEquals("", model.getCellAt(0, 1));
		assertEquals("", model.getCellAt(1, 1));
		assertEquals("", model.getCellAt(0, 0));
		assertEquals("", model.getCellAt(1, 0));
		assertEquals("", model.getCellAt(2, 0));
		assertEquals(3, model.getRowCount());
		assertEquals(2, model.getColumnCount());
	}

	@Test
	public void testEmptyInputAtTheEnd() throws InvalidInputException {
		processor.processInput("", view.getValues(), 0);
		assertEquals(0, model.getRowCount());
		processor.processInput("", null, 0);
		assertEquals(1, model.getColumnCount());
	}

	@Test
	public void testRemoveLastValueFromRow() throws InvalidInputException {
		processor.processInput("0", null, 0);
		GeoList column = (GeoList) view.getEvaluatable(1);
		processor.processInput("1", column, 1);
		processor.processInput("2", column, 2);
		assertEquals(3, model.getRowCount());
		assertEquals(2, model.getColumnCount());

		processor.processInput("", column, 0);
		// emptying any row above the last row shouldn't reduce the row count
		assertEquals(3, model.getRowCount());
		assertEquals(2, model.getColumnCount());

		processor.processInput("", column, 2);
		// emptying last row should reduce the row count
		assertEquals(2, model.getRowCount());
		assertEquals(2, model.getColumnCount());

		processor.processInput("", column, 1);
		// emptying last row should remove all the empty rows on the bottom fo the table
		assertEquals(0, model.getRowCount());
		assertEquals(1, model.getColumnCount());
	}

	@Test
	public void testRemoveSingleLastValueFromColumns() throws InvalidInputException {
		processor.processInput("1", null, 0);
		processor.processInput("2", null, 0);
		processor.processInput("3", null, 0);
		assertEquals(4, model.getColumnCount());
		assertEquals(1, model.getRowCount());

		processor.processInput("", (GeoList) view.getEvaluatable(3), 0);
		assertEquals(3, model.getColumnCount());

		processor.processInput("", (GeoList) view.getEvaluatable(1), 0);
		assertEquals(2, model.getColumnCount());

		processor.processInput("", (GeoList) view.getEvaluatable(1), 0);
		assertEquals(1, model.getColumnCount());

		assertEquals(0, model.getRowCount());
	}
}
