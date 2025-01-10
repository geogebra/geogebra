package org.geogebra.common.gui.view.table;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

import java.util.Locale;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.undo.UndoManager;
import org.geogebra.test.annotation.Issue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class TableValuesInputProcessorTest extends BaseUnitTest {

	private TableValues view;
	private TableValuesModel model;
	private TableValuesInputProcessor processor;
	private GeoList list;

	@Before
	public void setUp() {
		list = new GeoList(getConstruction());

		getApp().getSettings().getTable().setValueList(list);
		view = new TableValuesView(getKernel());
		model = view.getTableValuesModel();
		getKernel().attach(view);
		processor = (TableValuesInputProcessor) view.getProcessor();
	}

	@Test
	public void testValidInput() {
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
		assertEmptyInput(4);
		assertEmptyInput(5);
		assertValue("0.2", 6);
		assertValue("10", 10);
	}

	private void assertValue(String value, int index) {
		GeoElement element = list.get(index);
		assertThat(element, instanceOf(GeoNumeric.class));
		assertThat(element.toString(StringTemplate.defaultTemplate), is(value));
	}

	private void assertEmptyInput(int index) {
		GeoElement element = list.get(index);
		assertTrue(model.isEmptyValue(element));
	}

	@Test
	public void testInvalidInputWithComma() {
		processor.processInput("10,,", list, 0);
		assertEmptyInput("10,,");
	}

	@Test
	public void testInputWithComma() {
		processor.processInput("10,300", list, 0);
		assertValue("10.3", 0);
	}

	private void assertEmptyInput(String input) {
		GeoElement element = list.get(0);
		assertNotNull(element + " should be dependent", element.getParentAlgorithm());
		GeoElementND parent = element.getParentAlgorithm().getInput(0);
		assertThat(parent, instanceOf(GeoText.class));
		assertEquals(input, ((GeoText) parent).getTextString());
	}

	@Test
	public void testInputWithOperators() {
		processor.processInput("10 + 2", list, 0);
		assertValue("12", 0);
	}

	@Test
	public void testInvalidInputWithLetters() {
		processor.processInput("a", list, 0);
		assertEmptyInput("a");
	}

	@Test
	public void testProcessorWithEmptyList() {
		processor.processInput("1", null, 2);
		assertEquals("1", model.getCellAt(2, 1).getInput());
		assertEquals("", model.getCellAt(0, 1).getInput());
		assertEquals("", model.getCellAt(1, 1).getInput());
		assertEquals("", model.getCellAt(0, 0).getInput());
		assertEquals("", model.getCellAt(1, 0).getInput());
		assertEquals("", model.getCellAt(2, 0).getInput());
		assertEquals(3, model.getRowCount());
		assertEquals(2, model.getColumnCount());
	}

	@Test
	public void testEmptyInputAtTheEnd() {
		processor.processInput("", view.getValues(), 0);
		assertEquals(0, model.getRowCount());
		processor.processInput("", null, 0);
		assertEquals(1, model.getColumnCount());
	}

	@Test
	public void testClearValuesFromColumn() {
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
		// emptying last row should remove all the empty rows on the bottom for the table
		assertEquals(0, model.getRowCount());
		assertEquals(1, model.getColumnCount());
	}

	@Test
	public void testClearRowsAndColumns() {
		processor.processInput("1", null, 0);
		processor.processInput("2", null, 1);
		processor.processInput("3", null, 2);
		assertEquals(4, model.getColumnCount());
		assertEquals(3, model.getRowCount());

		processor.processInput("", (GeoList) view.getEvaluatable(3), 2);
		assertEquals(3, model.getColumnCount());
		assertEquals(2, model.getRowCount());

		processor.processInput("", (GeoList) view.getEvaluatable(1), 0);
		assertEquals(2, model.getColumnCount());
		assertEquals(2, model.getRowCount());

		processor.processInput("", (GeoList) view.getEvaluatable(1), 1);
		assertEquals(1, model.getColumnCount());
		assertEquals(0, model.getRowCount());
	}

	@Test
	public void testClearLastRow() {
		processor.processInput("1", null, 0);
		GeoList c1 = (GeoList) view.getEvaluatable(1);
		processor.processInput("1", c1, 1);

		processor.processInput("2", null, 0);
		GeoList c2 = (GeoList) view.getEvaluatable(2);
		processor.processInput("2", c2, 1);

		processor.processInput("3", null, 0);
		processor.processInput("0", view.getValues(), 1);
		// x    c1    c2    c3
		//       1     2     3
		// 1     1     2
		assertEquals(2, model.getRowCount());
		assertEquals(4, model.getColumnCount());

		processor.processInput("", c1, 1);
		// x    c1    c2    c3
		//       1     2     3
		// 1           2
		assertEquals(2, model.getRowCount());
		assertEquals(4, model.getColumnCount());

		processor.processInput("", c2, 1);
		// x    c1    c2    c3
		//       1     2     3
		// 1
		assertEquals(2, model.getRowCount());
		assertEquals(4, model.getColumnCount());

		processor.processInput("", view.getValues(), 1);
		// x    c1    c2    c3
		//       1     2     3
		assertEquals(1, model.getRowCount());
		assertEquals(4, model.getColumnCount());
	}

	@Test
	public void testEnterXValue() {
		processor.processInput("1", null, 0);
		processor.processInput("2", null, 1);
		processor.processInput("0", view.getValues(), 0);

		assertThat(model.getCellAt(1, 2).getInput(), equalTo("2"));
	}

	@Test
	public void testUndoRedoOnXColumn() {
		getApp().fileNew();
		getApp().setUndoActive(true);
		UndoManager undoManager = getApp().getUndoManager();

		processor.processInput("1", view.getValues(), 0);
		undoManager.storeUndoInfo();
		assertThat(model.getRowCount(), is(1));
		assertThat(model.getColumnCount(), is(1));
		String cellContent = model.getCellAt(0, 0).getInput();
		assertThat(cellContent, equalTo("1"));

		undoManager.undo();
		assertThat(model.getRowCount(), is(0));
		assertThat(model.getColumnCount(), is(1));

		undoManager.redo();
		assertThat(model.getRowCount(), is(1));
		assertThat(model.getColumnCount(), is(1));
		cellContent = model.getCellAt(0, 0).getInput();
		assertThat(cellContent, equalTo("1"));

		processor.processInput("2", view.getValues(), 0);
		undoManager.storeUndoInfo();
		undoManager.undo();
		cellContent = model.getCellAt(0, 0).getInput();
		assertThat(cellContent, equalTo("1"));

		undoManager.redo();
		cellContent = model.getCellAt(0, 0).getInput();
		assertThat(cellContent, equalTo("2"));

		processor.processInput("invalid", view.getValues(), 0);

		undoManager.storeUndoInfo();
		undoManager.undo();
		TableValuesCell cell = model.getCellAt(0, 0);
		assertThat(cell.getInput(), equalTo("2"));
		assertThat(cell.isErroneous(), is(false));

		undoManager.redo();
		cell = model.getCellAt(0, 0);
		assertThat(cell.getInput(), equalTo("invalid"));
		assertThat(cell.isErroneous(), is(true));
	}

	@Test
	@Issue("APPS-5545")
	public void testInvalidInput() {
		processor.processInput("1", view.getValues(), 0);
		processor.processInput("2", view.getValues(), 1);
		processor.processInput("3", null, 1);
		TableValuesListener mockListener = Mockito.mock(TableValuesListener.class);
		model.registerListener(mockListener);
		processor.processInput("invalid", (GeoList) view.getEvaluatable(1), 1);
		Mockito.verify(mockListener).notifyCellChanged(any(), any(), anyInt(), anyInt());
	}

	@Test
	@Issue("APPS-5553")
	public void testExpressionInput() {
		processor.processInput("1", view.getValues(), 0);
		processor.processInput("2", view.getValues(), 1);
		processor.processInput("5/2", null, 0);
		GeoList data = (GeoList) view.getEvaluatable(1);
		processor.processInput("1+1/2", data, 1);
		processor.processInput("1++", data, 2);
		StringBuilder sb = new StringBuilder();
		data.getExpressionXML(sb);
		assertThat(sb.toString(), equalTo("<expression label=\"y_{1}\" exp=\""
				+ "{5 / 2,1 + 1 / 2,ParseToNumber[&quot;1++&quot;]}\" type=\"list\"/>\n"));
	}

	@Test
	public void testUndoRedoOnYColumn() {
		getApp().fileNew();
		getApp().setUndoActive(true);
		UndoManager undoManager = getApp().getUndoManager();

		processor.processInput("1", null, 0);
		undoManager.storeUndoInfo();
		assertThat(model.getRowCount(), is(1));
		assertThat(model.getColumnCount(), is(2));
		String cellContent = model.getCellAt(0, 1).getInput();
		assertThat(cellContent, equalTo("1"));

		undoManager.undo();
		assertThat(model.getRowCount(), is(0));
		assertThat(model.getColumnCount(), is(1));

		undoManager.redo();
		assertThat(model.getRowCount(), is(1));
		assertThat(model.getColumnCount(), is(2));
		cellContent = model.getCellAt(0, 1).getInput();
		assertThat(cellContent, equalTo("1"));

		processor.processInput("2", (GeoList) view.getEvaluatable(1), 0);
		undoManager.storeUndoInfo();
		undoManager.undo();
		cellContent = model.getCellAt(0, 1).getInput();
		assertThat(cellContent, equalTo("1"));

		undoManager.redo();
		cellContent = model.getCellAt(0, 1).getInput();
		assertThat(cellContent, equalTo("2"));

		processor.processInput("invalid", (GeoList) view.getEvaluatable(1), 0);
		undoManager.storeUndoInfo();
		undoManager.undo();
		TableValuesCell cell = model.getCellAt(0, 1);
		assertThat(cell.getInput(), equalTo("2"));
		assertThat(cell.isErroneous(), is(false));

		undoManager.redo();
		cell = model.getCellAt(0, 1);
		assertThat(cell.getInput(), equalTo("invalid"));
		assertThat(cell.isErroneous(), is(true));
	}

	@Test
	public void processedValuesUsableInCommands() {
		processor.processInput("1", list, 0);
		processor.processInput("2", list, 1);
		processor.processInput("1", list, 2);

		GeoElement unique = add("Unique(x_1)");
		assertThat(unique, hasValue("{1, 2}"));
	}
}
