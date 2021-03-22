package org.geogebra.common.gui.view.table;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.GeoElementFactory;
import org.geogebra.common.Stopwatch;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.main.settings.TableSettings;
import org.geogebra.common.scientific.LabelController;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.test.OrderingComparison;
import org.geogebra.test.RegexpMatch;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Test class for TableValuesView.
 */
@RunWith(MockitoJUnitRunner.class)
public class TableValuesViewTest extends BaseUnitTest {

    private TableValues view;
    private TableValuesModel model;

    @Mock
    private TableValuesListener listener;

	private TableValuesPointsImpl tablePoints;

	/**
	 * Clear construction & initialize table view
	 */
    @Before
    public void setupTest() {
		getKernel().clearConstruction(true);
		getKernel().detach(view);
        view = new TableValuesView(getKernel());
		getKernel().attach(view);
        model = view.getTableValuesModel();
		view.clearView();
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

    private void setValuesSafe(double valuesMin, double valuesMax, double valuesStep) {
        try {
            view.setValues(valuesMin, valuesMax, valuesStep);
        } catch (InvalidValuesException exception) {
            // ignore
        }
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

	private void showColumn(GeoElement element) {
		assertTrue(element instanceof GeoEvaluatable);
        view.add(element);
		view.showColumn((GeoEvaluatable) element);
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

    private GeoLine[] createLines(int number) {
        GeoElementFactory factory = getElementFactory();
        GeoLine[] lines = new GeoLine[number];
        for (int i = 0; i < number; i++) {
            lines[i] = factory.createGeoLine();
        }
        return lines;
    }

    @Test
    public void testClearView() {
        for (int i = 0; i < 5; i++) {
            showColumn(getElementFactory().createGeoLine());
        }
        view.clearView();
		assertEquals(1, model.getColumnCount());
		assertEquals(5, model.getRowCount());
		assertEquals("x", model.getHeaderAt(0));
		assertEquals(TableSettings.DEFAULT_MIN, view.getValuesMin(), .1);
		assertEquals(TableSettings.DEFAULT_MAX, view.getValuesMax(), .1);
		assertEquals(TableSettings.DEFAULT_STEP, view.getValuesStep(), .1);
    }

    @Test
    public void testGetValues() {
	    setValuesSafe(0, 10, 2);
		assertEquals("0", model.getCellAt(0, 0));
		assertEquals("2", model.getCellAt(1, 0));
		assertEquals("10", model.getCellAt(5, 0));

        GeoElementFactory factory = getElementFactory();
        GeoFunction function = factory.createFunction("f(x) = x^2");
        showColumn(function);
		assertEquals("0", model.getCellAt(0, 1));
		assertEquals("4", model.getCellAt(1, 1));
		assertEquals("100", model.getCellAt(5, 1));

        function = factory.createFunction("g(x) = sqrt(x)");
        showColumn(function);
		assertEquals("0", model.getCellAt(0, 2));
		assertEquals("1.41", model.getCellAt(1, 2));
		assertEquals("3.16", model.getCellAt(5, 2));
    }

    @Test
    public void testInvalidGetValues() {
        setValuesSafe(-10, 10, 2);

        GeoElementFactory factory = getElementFactory();
        GeoFunction function = factory.createFunction("f(x) = sqrt(x)");
        showColumn(function);

		assertEquals("?", model.getCellAt(0, 1));
    }

	@Test
    public void testGetValuesChaningValues() {
        setValuesSafe(0, 10, 2);
        GeoElementFactory factory = getElementFactory();
        GeoFunction function = factory.createFunction("g(x) = sqrt(x)");
        showColumn(function);
		assertEquals("1.41", model.getCellAt(1, 1));

        setValuesSafe(2.5, 22.3, 1.3);
		assertEquals("1.95", model.getCellAt(1, 1));
    }

    @Test
    public void testCachingOfGetValues() {
		final long sleepTime = 10;
		Function slowFunction = mock(Function.class);
        Mockito.when(slowFunction.value(1.0)).then(invocation -> {
			Thread.sleep(sleepTime);
			return 0.0;
		});
		Mockito.when(slowFunction.getExpression()).thenReturn(new ExpressionNode(getKernel(), 0));
        setValuesSafe(1, 2, 1);

        GeoElementFactory factory = getElementFactory();
        GeoFunction geoFunction = factory.createFunction(slowFunction);
        showColumn(geoFunction);

        Stopwatch stopwatch = new Stopwatch();

        stopwatch.start();
        model.getCellAt(0, 1);
        long elapsed = stopwatch.stop();

        stopwatch.start();
        model.getCellAt(0, 1);
        long cachedElapsed = stopwatch.stop();

		assertThat(
				"Querying with the cache is not at least 10 times faster",
				elapsed, OrderingComparison.greaterThan(cachedElapsed * 10));
    }

    @Test
    public void testListeners() {
        model.registerListener(listener);
        GeoLine[] lines = createLines(2);
        showColumn(lines[0]);
        Mockito.verify(listener).notifyColumnAdded(model, lines[0], 1);
        showColumn(lines[1]);
		Mockito.verify(listener).notifyColumnAdded(model, lines[1], 2);

		hideColumn(lines[1]);
		Mockito.verify(listener).notifyColumnRemoved(model, lines[1], 2);

        view.update(lines[0]);
        Mockito.verify(listener).notifyColumnChanged(model, lines[0], 1);

        view.clearView();
        Mockito.verify(listener).notifyDatasetChanged(model);
    }

    @Test
    public void testUpdate() {
        setValuesSafe(0, 10, 2);

        GeoElementFactory factory = getElementFactory();
        GeoFunction fn = factory.createFunction("x^2");
        showColumn(fn);
		assertEquals("0", model.getCellAt(0, 0));

        view.update(fn);
		assertEquals("0", model.getCellAt(0, 0));
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

	private void reload() {
		getApp().setXML(getApp().getXML(), true);
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
		assertEquals(2, view.getValuesMax(), .1);
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

	private TableValuesPoints setupPointListener() {
		tablePoints = new TableValuesPointsImpl(getConstruction(),
				model);
		model.registerListener(tablePoints);
		return tablePoints;
	}

	@Test
	public void testUndoAddFirst() {
		setupUndo();
		GeoLine[] lines = createLines(2);
		getApp().storeUndoInfo();
		shouldHaveUndoPointsAndColumns(1, 1);
		showColumn(lines[0]);
		getKernel().undo();
		assertTrue(view.isEmpty());
		getKernel().redo();
		shouldHaveUndoPointsAndColumns(2, 2);
	}

	private void setupUndo() {
		getApp().setUndoRedoEnabled(true);
		getApp().setUndoActive(true);
		getKernel().getConstruction().initUndoInfo();
	}

	@Test
	public void testUndoAddSecond() {
		setupUndo();
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
		setupUndo();
		GeoLine[] lines = createLines(2);
		getApp().storeUndoInfo();
		shouldHaveUndoPointsAndColumns(1, 1);
		showColumn(lines[0]);
		hideColumn(lines[0]);
		assertTrue(view.isEmpty());
		getKernel().undo();
		assertFalse(view.isEmpty());
		getKernel().redo();
		assertTrue(view.isEmpty());
		shouldHaveUndoPointsAndColumns(3, 1);
	}

	@Test
	public void testUndoDeleteSecond() {
		setupUndo();
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
		setupUndo();
		GeoLine[] lines = createLines(2);
		getApp().storeUndoInfo();
		shouldHaveUndoPointsAndColumns(1, 1);
		setValuesSafe(0, 10, 2);
		showColumn(lines[0]);
		setValuesSafe(5, 20, 3);
		shouldHaveUndoPointsAndColumns(3, 2);
		assertEquals(5, view.getValuesMin(), .1);
		getKernel().undo();
		assertEquals(0, view.getValuesMin(), .1);
		getKernel().redo();
		assertEquals(5, view.getValuesMin(), .1);
		shouldHaveUndoPointsAndColumns(3, 2);
	}

	@Test
	public void testUndoShowPoints() {
		setupUndo();
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

	private void hideColumn(GeoEvaluatable geoLine) {
		view.hideColumn(geoLine);
	}

	private void shouldHaveUndoPointsAndColumns(int expected, int expectCols) {
		assertEquals(expected, getKernel().getConstruction()
				.getUndoManager().getHistorySize());
		assertEquals(expectCols, model.getColumnCount());
	}

    @Test
    public void testRemoveLastColumnResetsValues() {
        GeoLine[] lines = createLines(1);
        setValuesSafe(-5, 5, 2);
        showColumn(lines[0]);
        hideColumn(lines[0]);
		assertEquals(TableSettings.DEFAULT_MIN, view.getValuesMin(), .1);
		assertEquals(TableSettings.DEFAULT_MAX, view.getValuesMax(), .1);
		assertEquals(TableSettings.DEFAULT_STEP, view.getValuesStep(), .1);
    }

	@Test
	public void reloadShouldPreservePointOrder() {
		GeoLine[] lines = createLines(3);
		setValuesSafe(-5, 5, 2);
		setupPointListener();
		showColumn(lines[1]);
		showColumn(lines[0]);
		showColumn(lines[2]);
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

}
