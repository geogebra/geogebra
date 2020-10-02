package org.geogebra.web.full.gui.view.algebra.contextmenu;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.web.full.gui.view.algebra.contextmenu.action.StatisticsAction;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.test.AppMocker;
import org.geogebra.web.test.GgbMockitoTestRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GgbMockitoTestRunner.class)
public class AlgebraMenuItemCollectionTest {

	private StatisticsAction statisticsAction;
	private AppWFull app;

	@Before
	public void setUp() {
		app = AppMocker.mockGraphing(getClass());
		statisticsAction = new StatisticsAction();
	}

	@Test
	public void testStatisticsAvailable() {
		Construction construction = app.getKernel().getConstruction();

		GeoPoint point = new GeoPoint(construction, "A", 0, 0, 1);
		GeoFunction function = new GeoFunction(construction);
		GeoNumeric num1 = new GeoNumeric(construction, 1);
		GeoNumeric num2 = new GeoNumeric(construction, 2);
		GeoNumeric num3 = new GeoNumeric(construction, 3);
		GeoList emptyList = new GeoList(construction);
		GeoList numberList = new GeoList(construction);
		GeoList notNumberList = new GeoList(construction);

		numberList.add(num1);
		numberList.add(num2);
		numberList.add(num3);

		notNumberList.add(point);
		notNumberList.add(function);
		notNumberList.add(num1);

		Assert.assertFalse(statisticsAction.isAvailable(point));
		Assert.assertFalse(statisticsAction.isAvailable(function));
		Assert.assertFalse(statisticsAction.isAvailable(num1));
		Assert.assertFalse(statisticsAction.isAvailable(emptyList));
		Assert.assertFalse(statisticsAction.isAvailable(notNumberList));
		Assert.assertTrue(statisticsAction.isAvailable(numberList));
	}
}

