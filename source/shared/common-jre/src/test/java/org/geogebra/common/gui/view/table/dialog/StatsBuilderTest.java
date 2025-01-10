package org.geogebra.common.gui.view.table.dialog;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.gui.view.table.TableValuesModel;
import org.geogebra.common.gui.view.table.TableValuesProcessor;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.kernel.geos.GeoList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StatsBuilderTest extends BaseUnitTest {

	protected TableValuesView view;
	protected TableValuesModel model;
	protected TableValuesProcessor processor;

	@Before
	public void setupTest() {
		view = new TableValuesView(getKernel());
		getKernel().attach(view);
		model = view.getTableValuesModel();
		view.clearView();
		processor = view.getProcessor();
	}

	@Test
	public void testBugAPPS3753() {
		processor.processInput("1", view.getValues(), 0);
		processor.processInput("2", view.getValues(), 1);
		processor.processInput("1", null, 0);
		GeoList column = (GeoList) view.getEvaluatable(1);
		processor.processInput("2", column, 1);
		processor.processInput("", column, 1);
		try {
			view.getStatistics1Var(1);
		} catch (Exception e) {
			Assert.fail("Should not throw an exception");
		}
	}
}
