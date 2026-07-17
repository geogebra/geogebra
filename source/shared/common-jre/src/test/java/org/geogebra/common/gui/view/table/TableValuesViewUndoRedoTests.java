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

package org.geogebra.common.gui.view.table;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.gui.view.table.regression.RegressionSpecificationBuilder;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TableValuesViewUndoRedoTests extends BaseUnitTest {

	protected TableValuesView view;
	protected TableValuesModel model;
	protected TableValuesProcessor processor;

	@BeforeEach
	void setupUndoRedo() {
		view = new TableValuesView(getKernel());
		getKernel().attach(view);
		model = view.getTableValuesModel();
		view.clearView();
		processor = view.getProcessor();
		activateUndo();
		getKernel().getConstruction().initUndoInfo();
	}

	@Test
	void testProcessingCreatesUndoPoint() {
		processor.processInput("1", view.getValues(), 0);
		assertTrue(getApp().getUndoManager().undoPossible());
	}

	@Test
	void testUndoAddFirst() {
		GeoLine[] lines = getElementFactory().createLines(2);
		getApp().storeUndoInfo();
		shouldHaveUndoPointsAndColumns(1, 1);
		view.addAndShow(lines[0]);
		getKernel().undo();
		assertEquals(1, view.getTableValuesModel().getColumnCount());
		getKernel().redo();
		shouldHaveUndoPointsAndColumns(2, 2);
	}

	@Test
	void testUndoAddSecond() {
		GeoLine[] lines = getElementFactory().createLines(2);
		getApp().storeUndoInfo();
		shouldHaveUndoPointsAndColumns(1, 1);
		view.addAndShow(lines[0]);
		view.addAndShow(lines[1]);
		shouldHaveUndoPointsAndColumns(3, 3);
		getKernel().undo();
		shouldHaveUndoPointsAndColumns(2, 2);
		getKernel().redo();
		shouldHaveUndoPointsAndColumns(3, 3);
	}

	@Test
	void testUndoDeleteFirst() {
		GeoLine[] lines = getElementFactory().createLines(2);
		getApp().storeUndoInfo();
		shouldHaveUndoPointsAndColumns(1, 1);
		view.addAndShow(lines[0]);
		view.hideColumn(lines[0]);
		assertEquals(1, view.getTableValuesModel().getColumnCount());
		getKernel().undo();
		assertFalse(view.isEmpty());
		getKernel().redo();
		assertEquals(1, view.getTableValuesModel().getColumnCount());
		shouldHaveUndoPointsAndColumns(3, 1);
	}

	@Test
	void testUndoDeleteSecond() {
		GeoLine[] lines = getElementFactory().createLines(2);
		getApp().storeUndoInfo();
		shouldHaveUndoPointsAndColumns(1, 1);
		view.addAndShow(lines[0]);
		view.addAndShow(lines[1]);
		view.hideColumn(lines[1]);
		shouldHaveUndoPointsAndColumns(4, 2);
		getKernel().undo();
		shouldHaveUndoPointsAndColumns(3, 3);
		getKernel().redo();
		shouldHaveUndoPointsAndColumns(4, 2);
	}

	@Test
	void testUndoRange() throws InvalidValuesException {
		GeoLine[] lines = getElementFactory().createLines(2);
		getApp().storeUndoInfo();
		shouldHaveUndoPointsAndColumns(1, 1);
		view.setValues(0, 10, 2);
		view.addAndShow(lines[0]);
		view.setValues(5, 20, 3);
		shouldHaveUndoPointsAndColumns(4, 2);
		assertEquals(5, view.getValuesMin(), .1);
		getKernel().undo();
		assertEquals(0, view.getValuesMin(), .1);
		getKernel().redo();
		assertEquals(5, view.getValuesMin(), .1);
		shouldHaveUndoPointsAndColumns(4, 2);
	}

	@Test
	void testUndoShowPoints() {
		TableValuesPoints points = TableValuesPointsImpl.create(getKernel(),
				getConstruction(), view);
		GeoLine[] lines = getElementFactory().createLines(2);
		getApp().storeUndoInfo();
		shouldHaveUndoPointsAndColumns(1, 1);
		view.addAndShow(lines[0]);
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
	void testUndoAddRow() {
		processor.processInput("1", view.getValues(), 0);
		processor.processInput("2", null, 1);
		processor.processInput("3", null, 2);
		getKernel().undo();
		try {
			processor.processInput("2", (GeoList) view.getEvaluatable(1), 2);
		} catch (Throwable t) {
			throw new AssertionError(t);
		}
	}

	@Test
	void testClearValuesIsUndoable() {
		processor.processInput("1", view.getValues(), 0);
		view.clearValues();
		getKernel().undo();
		assertEquals(1, model.getRowCount());
		assertEquals(1, model.getColumnCount());
		assertEquals("1", model.getCellAt(0, 0).getInput());
	}

	@Test
	void testUndoRegression() {
		TableValuesPointsImpl.create(getKernel(), getConstruction(), view);
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
	void testUndoHideColumnWithPlot() {
		GeoList list = add("{1,2,3}");
		GeoList listY = add("{4,5,6}");
		getApp().getSettings().getTable().updateValueList(list);
		view.add(listY);
		view.showColumn(listY);
		GeoElement plot = view.plotRegression(1,
				new RegressionSpecificationBuilder().getForListSize(3).get(0));
		getApp().storeUndoInfo();
		assertEquals("f(x) = x + 3", plot.toString(StringTemplate.defaultTemplate));
		assertTrue(isInGraphics(plot), "plot in construction initially");
		view.hideColumn(listY);
		assertFalse(isInGraphics(plot), "plot removed when column hidden");
		getKernel().undo();
		assertTrue(isInGraphics(lookup("f")), "plot appears again on undo");
		getKernel().redo();
		assertFalse(isInGraphics(lookup("f")), "plot disappears on redo");
	}

	private boolean isInGraphics(GeoElement plot) {
		return getDrawable(plot) != null;
	}

	private void shouldHaveUndoPointsAndColumns(int expected, int expectCols) {
		assertEquals(expected, getKernel().getConstruction()
				.getUndoManager().getHistorySize());
		assertEquals(expectCols, model.getColumnCount());
	}
}
