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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.properties.impl.general.RoundingIndexProperty;
import org.geogebra.test.annotation.Issue;
import org.junit.Before;
import org.junit.Test;

public class TableValuesPointsImplTest extends BaseUnitTest {

	private TableValuesView view;
	private TableValuesProcessor processor;
	private TableValuesPointsImpl points;
	private GeoList listColumn;
	private GeoFunction functionColumn;

	@Before
	public void setUp() {
		view = new TableValuesView(getKernel());
		getKernel().attach(view);
		view.clearView();
		processor = view.getProcessor();
		points = TableValuesPointsImpl.create(getKernel(), getConstruction(), view);

		// Initialize table view with some data
		processor.processInput("1", view.getValues(), 0);
		processor.processInput("2", view.getValues(), 1);
		processor.processInput("3", view.getValues(), 2);

		processor.processInput("2", null, 0);
		listColumn = (GeoList) view.getEvaluatable(1);
		processor.processInput("3", listColumn, 1);
		processor.processInput("4", listColumn, 2);

		functionColumn = add("f(x)=x^2");
		view.add(functionColumn);
		view.showColumn(functionColumn);
	}

	@Test
	public void testInitialPoints() {
		assertPointCoordinates(1.0, 2.0, 0, 1);
		assertPointCoordinates(2.0, 3.0, 1, 1);
		assertPointCoordinates(3.0, 4.0, 2, 1);

		assertPointCoordinates(1.0, 1.0, 0, 2);
		assertPointCoordinates(2.0, 4.0, 1, 2);
		assertPointCoordinates(3.0, 9.0, 2, 2);
	}

	@Test
	public void testNotifyCellChanged() {
		processor.processInput("10.0", view.getValues(), 1);
		processor.processInput("20.0", listColumn, 1);

		assertPointCoordinates(10, 20, 1, 1);
	}

	private void assertPointCoordinates(double x, double y, int row, int column) {
		GeoList element = points.getPointListForColumn(column);
		GeoPoint point = (GeoPoint) element.get(row);
		assertEquals(x, point.getX(), 0.001);
		assertEquals(y, point.getY(), 0.001);
	}

	private GeoPoint getPoint(int row, int column) {
		GeoList element = points.getPointListForColumn(column);
		return (GeoPoint) element.get(row);
	}

	@Test
	public void testRemoveRow() {
		processor.processInput("", view.getValues(), 0);
		processor.processInput("", listColumn, 0);

		GeoPoint point = getPoint(0, 1);
		assertFalse(point.isDefined());
	}

	@Test
	public void testRemoveColumn() {
		listColumn.remove();
		functionColumn.remove();
		assertNull(points.getPointListForColumn(1));
	}

	@Test
	public void testUpdateFunction() {
		add("f(x)=x^3");
		assertPointCoordinates(1.0, 1.0, 0, 2);
		assertPointCoordinates(2.0, 8.0, 1, 2);
		assertPointCoordinates(3.0, 27.0, 2, 2);
	}

	@Test
	@Issue("APPS-6700")
	public void testUpdatingDependentObjectDoesNotCrash() {
		processor.processInput("0", view.getValues(), 0);
		processor.processInput("1/3", view.getValues(), 1);
		processor.processInput("2/3", null, 0);
		GeoList list = (GeoList) view.getEvaluatable(1);
		processor.processInput("5/3", list, 1);
		String label = points.getPointListForColumn(1).getLabelSimple();
		GeoPoint a = add("Element(" + label + ",1)");
		GeoPoint b = add("Element(" + label + ",2)");
		GeoLine line = getKernel().getAlgoDispatcher().line("L", a, b);

		assertEquals("L: 0.33x + 0.33y = 0.67", line.toString(StringTemplate.defaultTemplate));
		new RoundingIndexProperty(getApp(), getLocalization()).setIndex(1);
		assertEquals("L: 0.3x + 0.3y = 0.7", line.toString(StringTemplate.defaultTemplate));
	}

	@Test
	public void pointOnListShouldNotBeFree() {
		processor.processInput("0", view.getValues(), 0);
		processor.processInput("1/3", view.getValues(), 1);
		processor.processInput("2/3", null, 0);
		GeoList list = (GeoList) view.getEvaluatable(1);
		processor.processInput("5/3", list, 1);
		GeoPoint pt = getKernel().getAlgoDispatcher().point("P", points.getPointListForColumn(1),
				1 / 3.0, 5 / 3.0, true, false, false);
		assertEquals("Element(TableValuesPoints, 2)",
				pt.getDefinition(StringTemplate.testTemplate));
	}
}
