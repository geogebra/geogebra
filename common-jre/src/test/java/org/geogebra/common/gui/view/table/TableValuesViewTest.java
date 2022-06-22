package org.geogebra.common.gui.view.table;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.GeoElementFactory;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.main.App;
import org.geogebra.common.scientific.LabelController;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.test.RegexpMatch;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Test class for TableValuesView.
 */
@RunWith(MockitoJUnitRunner.class)
public class TableValuesViewTest extends BaseUnitTest {

	protected TableValuesView view;
	protected TableValuesModel model;
	protected TableValuesProcessor processor;

	@Mock
	private TableValuesListener listener;

	private TableValuesPointsImpl tablePoints;

	/**
	 * Clear construction & initialize table view
	 */
	@Before
	public void setupTest() {
		view = new TableValuesView(getKernel());
		getKernel().attach(view);
		model = view.getTableValuesModel();
		view.clearView();
		processor = view.getProcessor();
	}

	protected void showColumn(GeoElement element) {
		assertTrue(element instanceof GeoEvaluatable);
		view.add(element);
		view.showColumn((GeoEvaluatable) element);
	}

	protected void hideColumn(GeoEvaluatable geoLine) {
		view.hideColumn(geoLine);
	}

	protected void setValuesSafe(double valuesMin, double valuesMax, double valuesStep) {
		try {
			view.setValues(valuesMin, valuesMax, valuesStep);
		} catch (InvalidValuesException exception) {
			// ignore
		}
	}

	@Test
	public void testInvalidValuesThrowException() {
		try {
			view.setValues(0, 10, -1);
			Assert.fail("This should have thrown an exception");
		} catch (InvalidValuesException exception) {
			// expected
		}
		try {
			view.setValues(10, 0, 1);
			Assert.fail("This should have thrown an exception");
		} catch (InvalidValuesException exception) {
			// expected
		}
		try {
			view.setValues(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1);
			Assert.fail("This should have thrown an exception");
		} catch (InvalidValuesException exception) {
			// expected
		}
	}

	@Test
	public void testValues() {
		setValuesSafe(0, 10, 1);
		assertEquals(11, model.getRowCount());

		setValuesSafe(0, 10, 3);
		assertEquals(5, model.getRowCount());

		setValuesSafe(0, 1.5, 0.7);
		assertEquals(4, model.getRowCount());
		assertTrue(DoubleUtil.isEqual(view.getValuesMin(), 0));
		assertTrue(DoubleUtil.isEqual(view.getValuesMax(), 1.5));
		assertTrue(DoubleUtil.isEqual(view.getValuesStep(), 0.7));
	}

	@Test
	public void testShowColumn() {
		GeoElementFactory factory = getElementFactory();
		assertEquals(1, model.getColumnCount());
		showColumn(factory.createGeoLine());

		assertEquals(2, model.getColumnCount());
		showColumn(factory.createGeoLine());

		assertEquals(3, model.getColumnCount());
	}

	@Test
	public void testHideColumn() {
		GeoElementFactory factory = getElementFactory();
		GeoLine firstLine = factory.createGeoLine();
		GeoLine secondLine = factory.createGeoLine();
		showColumn(firstLine);
		showColumn(secondLine);
		assertEquals(3, model.getColumnCount());

		hideColumn(firstLine);
		assertEquals(2, model.getColumnCount());
		hideColumn(secondLine);
		assertEquals(1, model.getColumnCount());
	}

	@Test
	public void testConics() {
		GeoElementFactory factory = getElementFactory();
		GeoConic parabola = (GeoConic) factory.create("y=xx");
		GeoConic hyperbola = (GeoConic) factory.create("yy-xx=1");
		showColumn(parabola);
		// parabola added, columns (x,f)
		assertEquals(2, model.getColumnCount());
		showColumn(hyperbola);
		// hyperbola NOT added, columns still just (x,f)
		assertEquals(2, model.getColumnCount());
	}

	@Test
	public void testHeaders() {
		assertEquals("x", model.getHeaderAt(0));

		GeoLine[] lines = createLines(2);

		lines[0].setLabel("f");
		showColumn(lines[0]);
		assertEquals("f(x)", model.getHeaderAt(1));

		lines[1].setLabel("h");
		showColumn(lines[1]);
		assertEquals("h(x)", model.getHeaderAt(2));

		hideColumn(lines[0]);
		assertEquals("h(x)", model.getHeaderAt(1));
	}

	protected GeoLine[] createLines(int number) {
		return getElementFactory().createLines(number);
	}

	@Test
	public void testClearView() {
		for (int i = 0; i < 5; i++) {
			showColumn(getElementFactory().createGeoLine());
		}
		view.clearView();
		assertEquals(1, model.getColumnCount());
		assertEquals(0, model.getRowCount());
		assertEquals("x", model.getHeaderAt(0));
		assertEquals(0.0, view.getValuesMin(), .1);
		assertEquals(0.0, view.getValuesMax(), .1);
		assertEquals(0.0, view.getValuesStep(), .1);
	}

	@Test
	public void testGetValues() {
		setValuesSafe(0, 10, 2);
		assertEquals("0", model.getCellAt(0, 0).getInput());
		assertEquals("2", model.getCellAt(1, 0).getInput());
		assertEquals("10", model.getCellAt(5, 0).getInput());

		GeoElementFactory factory = getElementFactory();
		GeoFunction function = factory.createFunction("f(x) = x^2");
		showColumn(function);
		assertEquals("0", model.getCellAt(0, 1).getInput());
		assertEquals("4", model.getCellAt(1, 1).getInput());
		assertEquals("100", model.getCellAt(5, 1).getInput());

		function = factory.createFunction("g(x) = sqrt(x)");
		showColumn(function);
		assertEquals("0", model.getCellAt(0, 2).getInput());
		assertEquals("1.41", model.getCellAt(1, 2).getInput());
		assertEquals("3.16", model.getCellAt(5, 2).getInput());
	}

	@Test
	public void testInvalidGetValues() {
		setValuesSafe(-10, 10, 2);

		GeoElementFactory factory = getElementFactory();
		GeoFunction function = factory.createFunction("f(x) = sqrt(x)");
		showColumn(function);

		assertEquals("", model.getCellAt(0, 1).getInput());
	}

	@Test
	public void testGetValuesChangingValues() {
		setValuesSafe(0, 10, 2);
		GeoElementFactory factory = getElementFactory();
		GeoFunction function = factory.createFunction("g(x) = sqrt(x)");
		showColumn(function);
		assertEquals("1.41", model.getCellAt(1, 1).getInput());

		setValuesSafe(2.5, 22.3, 1.3);
		assertEquals("1.95", model.getCellAt(1, 1).getInput());
	}

	@Test
	public void testCachingOfGetValues() {
		ExpressionNode expr = new ExpressionNode(getKernel(), 0);
		Function slowFunction = spy(new Function(getKernel(), expr));
		setValuesSafe(1, 2, 1);

		GeoElementFactory factory = getElementFactory();
		GeoFunction geoFunction = factory.createFunction(slowFunction);
		showColumn(geoFunction);

		model.getCellAt(0, 1);
		verify(slowFunction, times(1)).value(anyDouble());

		model.getCellAt(0, 1);
		model.getCellAt(0, 1);
		verify(slowFunction, times(1)).value(anyDouble());
	}

	@Test
	public void testNotifyDatasetChangedCalled() {
		model.registerListener(listener);
		setValuesSafe(0, 2, 1);
		verify(listener).notifyDatasetChanged(model);
	}

	@Test
	public void testNotifyColumnAddedCalled() {
		model.registerListener(listener);
		GeoLine[] lines = createLines(2);
		showColumn(lines[0]);
		showColumn(lines[1]);
		verify(listener, never()).notifyDatasetChanged(model);
		verify(listener).notifyColumnAdded(model, lines[0], 1);
		verify(listener).notifyColumnAdded(model, lines[1], 2);
	}

	@Test
	public void testNotifyColumnRemovedCalled() {
		model.registerListener(listener);
		GeoLine[] lines = createLines(1);
		showColumn(lines[0]);
		hideColumn(lines[0]);
		verify(listener, never()).notifyDatasetChanged(model);
		verify(listener).notifyColumnRemoved(model, lines[0], 1);
	}

	@Test
	public void testRemovingColumnWithMoreRowsCallsNotifyRowRemoved() {
		processor.processInput("1", view.getValues(), 0);
		processor.processInput("1", null, 0);
		GeoList list = (GeoList) view.getEvaluatable(1);
		processor.processInput("1", list, 1);
		processor.processInput("1", list, 2);
		model.registerListener(listener);
		hideColumn(list);
		verify(listener).notifyColumnRemoved(model, list, 1);
		verify(listener).notifyRowsRemoved(model, 1, 2);
	}

	@Test
	public void testNotifyColumnRemovedCalledFromProcessor() {
		processor.processInput("0", view.getValues(), 0);
		processor.processInput("1", null, 0);
		model.registerListener(listener);
		GeoList columnToRemove = (GeoList) view.getEvaluatable(1);
		processor.processInput("", columnToRemove, 0);
		verify(listener, never()).notifyDatasetChanged(model);
		verify(listener, never()).notifyColumnChanged(model, columnToRemove, 1);
		verify(listener, never()).notifyCellChanged(model, columnToRemove, 1, 0);
		verify(listener).notifyColumnRemoved(model, columnToRemove, 1);
	}

	@Test
	public void testNotifyColumnChangedCalled() {
		model.registerListener(listener);
		GeoLine[] lines = createLines(1);
		showColumn(lines[0]);
		view.update(lines[0]);
		verify(listener, never()).notifyDatasetChanged(model);
		verify(listener).notifyColumnChanged(model, lines[0], 1);
	}

	@Test
	public void testNotifyColumnChangedCalledFromProcessor() {
		GeoFunction function = getElementFactory().createFunction("x^2");
		showColumn(function);
		model.registerListener(listener);
		GeoFunction anotherFunction = getElementFactory().createFunction("x^3");
		function.set(anotherFunction);
		function.notifyUpdate();
		verify(listener, never()).notifyDatasetChanged(model);
		verify(listener).notifyColumnChanged(model, function, 1);
	}

	@Test
	public void testOnlyNotifyColumnRemovedCalled() {
		processor.processInput("1", view.getValues(), 0);
		processor.processInput("2", view.getValues(), 1);

		processor.processInput("3", null, 0);
		GeoList y = (GeoList) view.getEvaluatable(1);
		processor.processInput("4", y, 1);

		GeoFunction function = getElementFactory().createFunction("x");
		showColumn(function);

		processor.processInput("", y, 1);
		model.registerListener(listener);
		processor.processInput("", y, 0);
		verify(listener, never()).notifyDatasetChanged(model);
		verify(listener, never()).notifyCellChanged(model, y, 1, 0);
		verify(listener, never()).notifyRowChanged(model, 0);
		verify(listener, never()).notifyRowsRemoved(model, 0, 0);
		verify(listener, never()).notifyColumnChanged(model, y, 1);
		verify(listener).notifyColumnRemoved(model, y, 1);
	}

	@Test
	public void testClearViewCallsNotifyDatasetChanged() {
		model.registerListener(listener);
		GeoLine[] lines = createLines(1);
		showColumn(lines[0]);
		view.clearView();
		verify(listener).notifyDatasetChanged(model);
	}

	@Test
	public void testNotifyRowAddedCalled() {
		model.registerListener(listener);
		processor.processInput("1", view.getValues(), 0);
		verify(listener, never()).notifyDatasetChanged(model);
		verify(listener, never()).notifyColumnChanged(model, view.getValues(), 0);
		verify(listener).notifyRowsAdded(model, 0, 0);
	}

	@Test
	public void testNotifyRowAddedCalledWithExistingColumn() {
		GeoLine[] lines = createLines(1);
		showColumn(lines[0]);
		model.registerListener(listener);
		processor.processInput("1", view.getValues(), 0);
		verify(listener, never()).notifyDatasetChanged(model);
		verify(listener).notifyRowsAdded(model, 0, 0);
	}

	@Test
	public void testNotifyRowChangedCalled() {
		processor.processInput("1", view.getValues(), 0);
		model.registerListener(listener);
		processor.processInput("10", view.getValues(), 0);
		verify(listener, never()).notifyDatasetChanged(model);
		verify(listener, never()).notifyRowsAdded(model, 0, 0);
		verify(listener).notifyRowChanged(model, 0);
	}

	@Test
	public void testNotifyRowChangedCalledForLastRow() {
		setValuesSafe(-2, 2, 1);
		GeoFunction function = getElementFactory().createFunction("x");
		showColumn(function);
		// Add extra column with element in the last row to keep the row in view
		processor.processInput("3", null, 4);
		model.registerListener(listener);
		// Delete last element in x column
		processor.processInput("", view.getValues(), 4);
		verify(listener, never()).notifyDatasetChanged(model);
		verify(listener, never()).notifyRowsRemoved(model, 4, 4);
		verify(listener, never()).notifyCellChanged(model, view.getValues(), 0, 4);
		verify(listener, never()).notifyColumnChanged(model, view.getValues(), 0);
		verify(listener).notifyRowChanged(model, 4);
	}

	@Test
	public void testNotifyRowAddedCalledForSecondRow() {
		processor.processInput("10", view.getValues(), 0);
		processor.processInput("11", null, 0);
		GeoList list = (GeoList) view.getEvaluatable(1);
		model.registerListener(listener);
		processor.processInput("11", list, 1);
		verify(listener, never()).notifyDatasetChanged(model);
		verify(listener, never()).notifyColumnAdded(model, list, 1);
		verify(listener).notifyRowsAdded(model, 1, 1);
	}

	@Test
	public void testNotifyCellChangedCalled() {
		processor.processInput("10", view.getValues(), 0);
		processor.processInput("11", view.getValues(), 1);
		processor.processInput("12", null, 0);
		GeoList list = (GeoList) view.getEvaluatable(1);
		processor.processInput("13", list, 1);

		model.registerListener(listener);
		processor.processInput("", list, 0);
		verify(listener, never()).notifyColumnAdded(model, view.getValues(), 0);
		verify(listener, never()).notifyRowsRemoved(model, 0, 0);
		verify(listener, never()).notifyRowChanged(model, 0);
		verify(listener).notifyCellChanged(model, list, 1, 0);
	}

	@Test
	public void testRowAndColumnRemoved() {
		processor.processInput("10", view.getValues(), 0);
		processor.processInput("11", null, 1);
		processor.processInput("", view.getValues(), 0);
		GeoList list = (GeoList) view.getEvaluatable(1);
		model.registerListener(listener);
		processor.processInput("", list, 1);
		verify(listener, never()).notifyDatasetChanged(model);
		verify(listener).notifyRowsRemoved(model, 0, 1);
		verify(listener).notifyColumnRemoved(model, list, 1);
	}

	@Test
	public void testNotifyRowRemovedCalled() {
		processor.processInput("10", view.getValues(), 0);
		processor.processInput("10", view.getValues(), 1);
		model.registerListener(listener);
		processor.processInput("", view.getValues(), 1);
		verify(listener, never()).notifyDatasetChanged(model);
		verify(listener, never()).notifyRowChanged(model, 1);
		verify(listener, never()).notifyCellChanged(model, view.getValues(), 0, 1);
		verify(listener).notifyRowsRemoved(model, 1, 1);
	}

	@Test
	public void testUpdate() {
		setValuesSafe(0, 10, 2);

		GeoElementFactory factory = getElementFactory();
		GeoFunction fn = factory.createFunction("x^2");
		showColumn(fn);
		assertEquals("0", model.getCellAt(0, 0).getInput());

		view.update(fn);
		assertEquals("0", model.getCellAt(0, 0).getInput());
	}

	@Test
	public void testOrdering() {
		setValuesSafe(0, 10, 2);
		GeoElementFactory factory = getElementFactory();
		GeoFunction fn = factory.createFunction("f:x^1");
		showColumn(fn);
		GeoFunction fn2 = factory.createFunction("f2:x^2");
		showColumn(fn2);
		GeoFunction fn3 = factory.createFunction("f3:x^3");
		showColumn(fn3);
		assertEquals(1, fn.getTableColumn());
		assertEquals(2, fn2.getTableColumn());
		assertEquals(3, fn3.getTableColumn());
		hideColumn(fn2);
		view.showColumn(fn2);
		assertEquals(1, fn.getTableColumn());
		assertEquals(3, fn2.getTableColumn());
		assertEquals(2, fn3.getTableColumn());
	}

	@Test
	public void testOrderingReload() {
		setValuesSafe(0, 10, 2);
		GeoElementFactory factory = getElementFactory();
		GeoFunction fn = factory.createFunction("f:x^1");
		showColumn(fn);
		GeoFunction fn2 = factory.createFunction("f2:x^2");
		showColumn(fn2);
		GeoFunction fn3 = factory.createFunction("f3:x^3");
		showColumn(fn3);
		hideColumn(fn2);
		view.showColumn(fn2);
		reload();
		GeoEvaluatable fnReload = lookupFunction("f");
		GeoEvaluatable fn2Reload = lookupFunction("f2");
		GeoEvaluatable fn3Reload = lookupFunction("f3");
		assertEquals(1, fnReload.getTableColumn());
		assertEquals(3, fn2Reload.getTableColumn());
		assertEquals(2, fn3Reload.getTableColumn());
	}

	@Test
	public void testXML() {
		setValuesSafe(0, 10, 2);

		GeoElementFactory factory = getElementFactory();
		GeoFunction fn = factory.createFunction("f:x^2");
		showColumn(fn);
		assertThat(getApp().getXML(),
				RegexpMatch.matches(
						".*<tableview min=\"0.0\" max=\"10.0\".*"
								+ "<tableview column=\"1\" points=\"true\"\\/>.*"));
	}

	@Test
	public void testReload() {
		setValuesSafe(0, 10, 2);

		GeoElementFactory factory = getElementFactory();
		GeoFunction fn = factory.createFunction("f:x^2");
		showColumn(fn);
		assertEquals(1, view.getColumn(fn));
		String xml = getApp().getXML();
		setValuesSafe(10, 20, 2);
		getKernel().clearConstruction(true);
		assertEquals(-1, view.getColumn(fn));
		assertEquals(0.0, view.getValuesMax(), .1);
		getApp().setXML(xml, true);
		GeoEvaluatable fnReload = lookupFunction("f");
		assertEquals(10, view.getValuesMax(), .1);
		assertEquals(1, view.getColumn(fnReload));
	}

	private GeoEvaluatable lookupFunction(String string) {
		return (GeoEvaluatable) getKernel().lookupLabel(string);
	}

	@Test
	public void testReloadOrder() {
		setValuesSafe(0, 10, 2);

		GeoElementFactory factory = getElementFactory();
		GeoFunction fn = factory.createFunction("f:x^2");
		GeoFunction fn2 = factory.createFunction("f2:x^2");
		showColumn(fn2);
		showColumn(fn);
		assertEquals(2, view.getColumn(fn));

		String xml = getApp().getXML();

		getKernel().clearConstruction(true);
		assertEquals(-1, view.getColumn(fn));
		getApp().setXML(xml, true);
		GeoEvaluatable fnReload = lookupFunction("f");

		assertEquals(2, view.getColumn(fnReload));
	}

	@Test
	public void testReloadPoints() {
		setValuesSafe(0, 10, 2);

		GeoElementFactory factory = getElementFactory();
		GeoFunction fn = factory.createFunction("f:x^2");
		fn.setPointsVisible(false);
		showColumn(fn);
		assertFalse(fn.isPointsVisible());

		String xml = getApp().getXML();

		getKernel().clearConstruction(true);
		getApp().setXML(xml, true);
		GeoEvaluatable fnReload = lookupFunction("f");

		assertFalse(fnReload.isPointsVisible());
	}

	@Test
	public void testTableValuesPointsVisibility() {
		TableValuesPoints points = setupPointListener();

		GeoLine[] lines = createLines(2);

		showColumn(lines[0]);
		assertTrue(points.arePointsVisible(1));

		showColumn(lines[1]);
		assertTrue(points.arePointsVisible(2));
		points.setPointsVisible(2, false);
		assertFalse(points.arePointsVisible(2));

		hideColumn(lines[0]);
		assertFalse(points.arePointsVisible(1));

		points.setPointsVisible(1, true);
		assertTrue(points.arePointsVisible(1));

		// Possible to set visibility without adding to view
		points.setPointsVisible(1, false);
		assertFalse(points.arePointsVisible(1));
	}

	protected TableValuesPoints setupPointListener() {
		tablePoints = TableValuesPointsImpl.create(getConstruction(), view, model);
		return tablePoints;
	}

	@Test
	public void reloadShouldPreservePointOrder() {
		GeoLine[] lines = createLines(3);
		setValuesSafe(-5, 5, 2);
		setupPointListener();
		showColumn(lines[1]); // column 1
		showColumn(lines[0]); // column 2
		showColumn(lines[2]); // column 3
		lines[1].setPointsVisible(false);
		reload();
		assertFalse(tablePoints.arePointsVisible(1));
		assertTrue(tablePoints.arePointsVisible(2));
		assertTrue(tablePoints.arePointsVisible(3));
		// remove the first column: shift flags to the left
		lookupFunction(lines[1].getLabelSimple()).remove();
		assertTrue(tablePoints.arePointsVisible(1));
		assertTrue(tablePoints.arePointsVisible(2));
	}

	@Test
	public void replaceShouldPreserveTableContent() {
		GeoElementFactory factory = getElementFactory();
		GeoFunction f = factory.createFunction("f(x)=x");
		GeoLine g = factory.createGeoLine();
		g.setLabel("g");
		factory.create("a=1");
		setupPointListener();
		showColumn(f);
		g.setPointsVisible(false);
		showColumn(g);
		assertTrue(tablePoints.arePointsVisible(1));
		factory.create("f(x)=x+a");
		assertTrue(tablePoints.arePointsVisible(1));
		factory.create("g:x+a+y=0");
		assertTrue(tablePoints.arePointsVisible(1));
		assertFalse(tablePoints.arePointsVisible(2));
		assertFalse(tablePoints.arePointsVisible(3));
	}

	@Test
	public void addToTableShouldEnforceLabel() {
		GeoElement line = getElementFactory().createLineNoLabel();
		setValuesSafe(-5, 5, 2);
		showColumn(line);
		// line added
		assertEquals(2, model.getColumnCount());
		assertTrue(line.isAlgebraLabelVisible());
		new LabelController().hideLabel(line);
		// line removed
		assertEquals(1, model.getColumnCount());
		assertFalse(line.isAlgebraLabelVisible());
	}

	@Test
	public void testList() {
		GeoList list = (GeoList) getElementFactory().create("A = {4 ,7, 11}");
		setValuesSafe(0, 2, 1);
		showColumn(list);

		assertEquals(2, model.getColumnCount());
		assertEquals(3, model.getRowCount());
		assertEquals(model.getCellAt(0, 1).getInput(), "4");
		assertEquals(model.getCellAt(1, 1).getInput(), "7");
		assertEquals(model.getCellAt(2, 1).getInput(), "11");

		GeoNumeric numeric = (GeoNumeric) getElementFactory().create("99");
		list.setListElement(0, numeric);
		list.notifyUpdate();
		assertEquals("99", model.getCellAt(0, 1).getInput());
	}

	@Test
	public void testChangingValues() {
		GeoList list = (GeoList) getElementFactory().create("A = {4 ,7, 11}");
		setValuesSafe(0, 2, 1);
		showColumn(list);

		assertEquals(2, model.getColumnCount());
		assertEquals(3, model.getRowCount());
		assertEquals(model.getCellAt(0, 1).getInput(), "4");
		assertEquals(model.getCellAt(1, 1).getInput(), "7");
		assertEquals(model.getCellAt(2, 1).getInput(), "11");

		GeoNumeric numeric = (GeoNumeric) getElementFactory().create("99");
		list.setListElement(0, numeric);
		list.notifyUpdate();
		assertEquals("99", model.getCellAt(0, 1).getInput());
	}

	@Test
	public void testEmptyCell() {
		GeoList list = (GeoList) getElementFactory().create("A = {4 ,7, 11}");
		setValuesSafe(0, 2, 1);
		showColumn(list);
		view.getProcessor().processInput("", list, 0);
		assertEquals("", model.getCellAt(0, 1).getInput());
	}

	@Test
	public void testProcessFirstInput() {
		view.getProcessor().processInput("1", view.getValues(), 0);
		assertEquals("1", model.getCellAt(0, 0).getInput());
	}

	@Test
	public void testOverwriteCachedValue() {
		view.getProcessor().processInput("1", view.getValues(), 0);
		assertEquals("1", model.getCellAt(0, 0).getInput());
		view.getProcessor().processInput("2", view.getValues(), 0);
		assertEquals("2", model.getCellAt(0, 0).getInput());
	}

	@Test
	public void testCachedValuesAreOverwrittenOnUpdate() {
		GeoFunction function = getElementFactory().createFunction("x^2");
		showColumn(function);
		view.getProcessor().processInput("2", view.getValues(), 0);
		view.getProcessor().processInput("1", view.getValues(), 1);
		assertEquals("4", model.getCellAt(0, 1).getInput());
		view.getProcessor().processInput("3", view.getValues(), 0);
		assertEquals("9", model.getCellAt(0, 1).getInput());
		view.getProcessor().processInput("", view.getValues(), 0);
		assertEquals("", model.getCellAt(0, 1).getInput());
	}

	@Test
	public void testCachedValuesAreOverwrittenOnUpdateWithTwoColumns() {
		GeoFunction function = getElementFactory().createFunction("x^2");
		showColumn(function);
		GeoFunction function2 = getElementFactory().createFunction("x^3");
		showColumn(function2);
		view.getProcessor().processInput("2", view.getValues(), 0);
		view.getProcessor().processInput("1", view.getValues(), 1);
		view.getProcessor().processInput("", view.getValues(), 0);
		assertEquals("", model.getCellAt(0, 1).getInput());
	}

	@Test
	public void testFunctionAtUndefinedValues() {
		setValuesSafe(-2, 2, 1);
		assertEquals("-2", model.getCellAt(0, 0).getInput());
		assertEquals("0", model.getCellAt(2, 0).getInput());
		assertEquals("2", model.getCellAt(4, 0).getInput());

		GeoElementFactory factory = getElementFactory();
		GeoFunction function = factory.createFunction("g(x) = sqrt(x)");
		showColumn(function);
		assertEquals("", model.getCellAt(0, 1).getInput());
		assertEquals("0", model.getCellAt(2, 1).getInput());
		assertEquals("1.41", model.getCellAt(4, 1).getInput());

		view.getProcessor().processInput("", view.getValues(), 4);
		assertEquals(model.getRowCount(), 4);
	}

	@Test
	public void testUndefinedXValueDoesNotThrowException() {
		setValuesSafe(-2, 2, 1);
		GeoFunction function = getElementFactory().createFunction("x^2");
		showColumn(function);
		processor.processInput("5", null, 5);
		// Get the value for function for an undefined x value
		try {
			model.getCellAt(5, 1);
		} catch (Throwable t) {
			Assert.fail("Should not throw exception for a function in undefined x");
		}
	}

	@Test
	public void testChangingValuesCallsNotifyRowsRemoved() {
		setValuesSafe(-2, 2, 1);
		processor.processInput("1", null, 0);
		model.registerListener(listener);
		view.clearValues();
		verify(listener).notifyRowsRemoved(model, 1, 4);
	}

	@Test
	public void testChangingValuesCallsNotifyRowsAdded() {
		setValuesSafe(-2, 2, 1);
		processor.processInput("1", null, 0);
		model.registerListener(listener);
		view.clearValues();
		verify(listener).notifyRowsRemoved(model, 1, 4);
	}

	@Test
	public void testRegressionApps3867() {
		App app = getApp();

		processor.processInput("1", view.getValues(), 0);
		String xmlWithXValues = app.getXML();

		app.clearConstruction();
		processor.processInput("1", view.getValues(), 0);
		String xml = app.getXML();

		assertEquals(xmlWithXValues, xml);
	}
}
