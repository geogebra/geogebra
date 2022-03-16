package org.geogebra.common.gui.view.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TableValuesViewUndoRedoTests extends TableValuesViewTest {

	@Before
	public void setupUndoRedo() {
		getApp().setUndoRedoEnabled(true);
		getApp().setUndoActive(true);
		getKernel().getConstruction().initUndoInfo();
	}

	@Test
	public void testProcessingCreatesUndoPoint() {
		processor.processInput("1", view.getValues(), 0);
		assertTrue(getApp().getUndoManager().undoPossible());
	}

	@Test
	public void testUndoAddFirst() {
		GeoLine[] lines = createLines(2);
		getApp().storeUndoInfo();
		shouldHaveUndoPointsAndColumns(1, 1);
		showColumn(lines[0]);
		getKernel().undo();
		assertTrue(view.getTableValuesModel().getColumnCount() == 1);
		getKernel().redo();
		shouldHaveUndoPointsAndColumns(2, 2);
	}

	@Test
	public void testUndoAddSecond() {
		GeoLine[] lines = createLines(2);
		getApp().storeUndoInfo();
		shouldHaveUndoPointsAndColumns(1, 1);
		showColumn(lines[0]);
		showColumn(lines[1]);
		shouldHaveUndoPointsAndColumns(3, 3);
		getKernel().undo();
		assertEquals(2, 2);
		getKernel().redo();
		shouldHaveUndoPointsAndColumns(3, 3);
	}

	@Test
	public void testUndoDeleteFirst() {
		GeoLine[] lines = createLines(2);
		getApp().storeUndoInfo();
		shouldHaveUndoPointsAndColumns(1, 1);
		showColumn(lines[0]);
		hideColumn(lines[0]);
		assertTrue(view.getTableValuesModel().getColumnCount() == 1);
		getKernel().undo();
		assertFalse(view.isEmpty());
		getKernel().redo();
		assertTrue(view.getTableValuesModel().getColumnCount() == 1);
		shouldHaveUndoPointsAndColumns(3, 1);
	}

	@Test
	public void testUndoDeleteSecond() {
		GeoLine[] lines = createLines(2);
		getApp().storeUndoInfo();
		shouldHaveUndoPointsAndColumns(1, 1);
		showColumn(lines[0]);
		showColumn(lines[1]);
		hideColumn(lines[1]);
		shouldHaveUndoPointsAndColumns(4, 2);
		getKernel().undo();
		shouldHaveUndoPointsAndColumns(3, 3);
		getKernel().redo();
		shouldHaveUndoPointsAndColumns(4, 2);
	}

	@Test
	public void testUndoRange() {
		GeoLine[] lines = createLines(2);
		getApp().storeUndoInfo();
		shouldHaveUndoPointsAndColumns(1, 1);
		setValuesSafe(0, 10, 2);
		showColumn(lines[0]);
		setValuesSafe(5, 20, 3);
		shouldHaveUndoPointsAndColumns(4, 2);
		assertEquals(5, view.getValuesMin(), .1);
		getKernel().undo();
		assertEquals(0, view.getValuesMin(), .1);
		getKernel().redo();
		assertEquals(5, view.getValuesMin(), .1);
		shouldHaveUndoPointsAndColumns(4, 2);
	}

	@Test
	public void testUndoShowPoints() {
		TableValuesPoints points = setupPointListener();
		GeoLine[] lines = createLines(2);
		getApp().storeUndoInfo();
		shouldHaveUndoPointsAndColumns(1, 1);
		showColumn(lines[0]);
		points.setPointsVisible(1, false);
		points.setPointsVisible(1, true);
		shouldHaveUndoPointsAndColumns(4, 2);
		assertTrue(points.arePointsVisible(1));
		getKernel().undo();
		assertFalse(points.arePointsVisible(1));
		getKernel().undo();
		assertTrue(points.arePointsVisible(1));
		getKernel().redo();
		assertFalse(points.arePointsVisible(1));
		getKernel().redo();
		assertTrue(points.arePointsVisible(1));
	}

	@Test
	public void testUndoAddRow() {
		processor.processInput("1", view.getValues(), 0);
		processor.processInput("2", null, 1);
		processor.processInput("3", null, 2);
		getKernel().undo();
		try {
			processor.processInput("2", (GeoList) view.getEvaluatable(1), 2);
		} catch (Throwable e) {
			Assert.fail("Should not throw exception");
		}
	}

	@Test
	public void testClearValuesIsUndoable() {
		processor.processInput("1", view.getValues(), 0);
		view.clearValues();
		getKernel().undo();
		assertEquals(1, model.getRowCount());
		assertEquals(1, model.getColumnCount());
		assertEquals("1", model.getCellAt(0, 0).getInput());
	}

	@Test
	public void testUndoRegression() {
		setupPointListener();
		processor.processInput("1", null, 0);
		GeoList list = (GeoList) view.getEvaluatable(1);
		processor.processInput("1", list, 1);
		getKernel().undo();
		assertEquals(1, model.getRowCount());
		assertEquals(2, model.getColumnCount());
		assertEquals(1, model.getValueAt(0, 1), Kernel.STANDARD_PRECISION);
		assertEquals(Double.NaN, model.getValueAt(1, 1), Kernel.STANDARD_PRECISION);
		assertEquals("1", model.getCellAt(0, 1).getInput());
		assertEquals("", model.getCellAt(1, 1).getInput());
	}

	@Test
	public void testUndoHideColumnWithPlot() {
		GeoList list = add("{1,2,3}");
		GeoList listY = add("{4,5,6}");
		getApp().getSettings().getTable().updateValueList(list);
		view.add(listY);
		view.showColumn(listY);
		GeoElement plot = view.plotRegression(1,
				RegressionSpecification.getForListSize(3).get(0));
		getApp().storeUndoInfo();
		assertEquals(plot.toString(StringTemplate.defaultTemplate), "f(x) = x + 3");
		assertTrue("plot in construction initially", isInGraphics(plot));
		view.hideColumn(listY);
		assertFalse("plot removed when column hidden", isInGraphics(plot));
		getKernel().undo();
		assertTrue("plot appears again on undo", isInGraphics(lookup("f")));
		getKernel().redo();
		assertFalse("plot disappears on redo", isInGraphics(lookup("f")));
	}

	private boolean isInGraphics(GeoElement plot) {
		return getApp().getEuclidianView1().getDrawableFor(plot) != null;
	}

	private void shouldHaveUndoPointsAndColumns(int expected, int expectCols) {
		assertEquals(expected, getKernel().getConstruction()
				.getUndoManager().getHistorySize());
		assertEquals(expectCols, model.getColumnCount());
	}
}
