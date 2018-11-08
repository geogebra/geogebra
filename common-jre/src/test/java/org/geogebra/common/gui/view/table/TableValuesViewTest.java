package org.geogebra.common.gui.view.table;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.GeoElementFactory;
import org.geogebra.common.Stopwatch;
import org.geogebra.common.kernel.arithmetic.Evaluatable;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.test.OrderingComparison;
import org.geogebra.test.RegexpMatch;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.stubbing.Answer;

/**
 * Test class for TableValuesView.
 */
public class TableValuesViewTest extends BaseUnitTest {

    private TableValues view;
    private TableValuesModel model;

    @Mock
    private TableValuesListener listener;

    @Mock
    private Function function;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setupTest() {
        view = new TableValuesView(getKernel());
        model = view.getTableValuesModel();
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
        Assert.assertEquals(11, model.getRowCount());

        setValuesSafe(0, 10, 3);
        Assert.assertEquals(5, model.getRowCount());

        setValuesSafe(0, 1.5, 0.7);
        Assert.assertEquals(4, model.getRowCount());
        Assert.assertTrue(DoubleUtil.isEqual(view.getValuesMin(), 0));
        Assert.assertTrue(DoubleUtil.isEqual(view.getValuesMax(), 1.5));
        Assert.assertTrue(DoubleUtil.isEqual(view.getValuesStep(), 0.7));
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
        Assert.assertEquals(1, model.getColumnCount());
        showColumn(factory.createGeoLine());

        Assert.assertEquals(2, model.getColumnCount());
        showColumn(factory.createGeoLine());

        Assert.assertEquals(3, model.getColumnCount());
    }

    private void showColumn(GeoElement element) {
        Assert.assertTrue(element instanceof Evaluatable);
        view.add(element);
        view.showColumn((Evaluatable) element);
    }

    @Test
    public void testHideColumn() {
        GeoElementFactory factory = getElementFactory();
        GeoLine firstLine = factory.createGeoLine();
        GeoLine secondLine = factory.createGeoLine();
        showColumn(firstLine);
        showColumn(secondLine);
        Assert.assertEquals(3, model.getColumnCount());

        view.hideColumn(firstLine);
        Assert.assertEquals(2, model.getColumnCount());
        view.hideColumn(secondLine);
        Assert.assertEquals(1, model.getColumnCount());
    }

    @Test
    public void testHeaders() {
        Assert.assertEquals("x", model.getHeaderAt(0));

        GeoLine[] lines = createLines(2);

        lines[0].setLabel("f");
        showColumn(lines[0]);
        Assert.assertEquals("f(x)", model.getHeaderAt(1));

        lines[1].setLabel("h");
        showColumn(lines[1]);
        Assert.assertEquals("h(x)", model.getHeaderAt(2));

        view.hideColumn(lines[0]);
        Assert.assertEquals("h(x)", model.getHeaderAt(1));
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
        Assert.assertEquals(1, model.getColumnCount());
        Assert.assertEquals(5, model.getRowCount());
        Assert.assertEquals("x", model.getHeaderAt(0));
    }

    @Test
    public void testGetValues() {
        setValuesSafe(0, 10, 2);
        Assert.assertEquals("0", model.getCellAt(0, 0));
        Assert.assertEquals("2", model.getCellAt(1, 0));
        Assert.assertEquals("10", model.getCellAt(5, 0));

        GeoElementFactory factory = getElementFactory();
        GeoFunction function = factory.createFunction("f(x) = x^2");
        showColumn(function);
        Assert.assertEquals("0", model.getCellAt(0, 1));
        Assert.assertEquals("4", model.getCellAt(1, 1));
        Assert.assertEquals("100", model.getCellAt(5, 1));

        function = factory.createFunction("g(x) = sqrt(x)");
        showColumn(function);
        Assert.assertEquals("0", model.getCellAt(0, 2));
        Assert.assertEquals("1.41", model.getCellAt(1, 2));
        Assert.assertEquals("3.16", model.getCellAt(5, 2));
    }

    @Test
    public void testInvalidGetValues() {
        setValuesSafe(-10, 10, 2);

        GeoElementFactory factory = getElementFactory();
        GeoFunction function = factory.createFunction("f(x) = sqrt(x)");
        showColumn(function);

        Assert.assertEquals("?", model.getCellAt(0, 1));
    }

    @Test
    public void testGetValuesChaningValues() {
        setValuesSafe(0, 10, 2);
        GeoElementFactory factory = getElementFactory();
        GeoFunction function = factory.createFunction("g(x) = sqrt(x)");
        showColumn(function);
        Assert.assertEquals("1.41", model.getCellAt(1, 1));

        setValuesSafe(2.5, 22.3, 1.3);
        Assert.assertEquals("1.95", model.getCellAt(1, 1));
    }

    @Test
    public void testCachingOfGetValues() {
		final long sleepTime = 10;
        Mockito.when(function.value(1.0)).then(new Answer<Double>() {
            @Override
            public Double answer(InvocationOnMock invocation) throws Throwable {
                Thread.sleep(sleepTime);
                return 0.0;
            }
        });
        setValuesSafe(1, 2, 1);

        GeoElementFactory factory = getElementFactory();
        GeoFunction geoFunction = factory.createFunction(function);
        showColumn(geoFunction);

        Stopwatch stopwatch = new Stopwatch();

        stopwatch.start();
        model.getCellAt(0, 1);
        long elapsed = stopwatch.stop();

        stopwatch.start();
        model.getCellAt(0, 1);
        long cachedElapsed = stopwatch.stop();

		Assert.assertThat(
				"Querying with the cache is not at least 10 times faster",
				elapsed, OrderingComparison.greaterThan(cachedElapsed * 10));
    }

    @Test
    public void testListeners() {
        model.registerListener(listener);
        GeoLine[] lines = createLines(2);
        showColumn(lines[0]);
        Mockito.verify(listener).notifyColumnAdded(model, 1);
        showColumn(lines[1]);
        Mockito.verify(listener).notifyColumnAdded(model, 2);

        view.hideColumn(lines[1]);
        Mockito.verify(listener).notifyColumnRemoved(model, 2);

        view.update(lines[0]);
        Mockito.verify(listener).notifyColumnChanged(model, 1);

        view.clearView();
        Mockito.verify(listener).notifyDatasetChanged(model);
    }

    @Test
    public void testUpdate() {
        setValuesSafe(0, 10, 2);

        GeoElementFactory factory = getElementFactory();
        GeoFunction fn = factory.createFunction("x^2");
        showColumn(fn);
        Assert.assertEquals("0", model.getCellAt(0, 0));

        view.update(fn);
        Assert.assertEquals("0", model.getCellAt(0, 0));
    }

	@Test
	public void testXML() {
		setValuesSafe(0, 10, 2);

		GeoElementFactory factory = getElementFactory();
		GeoFunction fn = factory.createFunction("f:x^2");
		showColumn(fn);
		Assert.assertThat(new MyXMLioCommon(getKernel(), getConstruction()).getFullXML(),
				RegexpMatch.matches(
						".*<tableview min=\"0.0\" max=\"10.0\".*"
								+ "<column label=\"f\" points=\"true\"\\/>.*"));
	}

	private String getXML() {
		StringBuilder sb = new StringBuilder();
		getApp().getSettings().getTable().getXML(sb);
		return sb.toString();
	}
}
