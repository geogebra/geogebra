package org.geogebra.common.gui.view.table.dialog;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.gui.view.table.TableValuesModel;
import org.geogebra.common.gui.view.table.TableValuesProcessor;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.statistics.Statistic;
import org.junit.Before;
import org.junit.Test;

public class StatisticGroupsBuilderTest extends BaseUnitTest {

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
			throw new AssertionError("Should not throw an exception", e);
		}
	}

	@Test
	public void testFiltering() {
		StatisticGroupsBuilder builder = new StatisticGroupsBuilder();
		GeoList list = add("{1, 2, 3, 4, 5}");
		builder.setStatisticsFilter(statistic -> statistic == Statistic.MEAN);
		List<StatisticGroup> groups = builder.buildOneVariableStatistics(list, "x");
		assertAll(
				() -> assertEquals(1, groups.size()),
				() -> assertEquals(
						getLocalization().getMenu(Statistic.MEAN.getMenuLocalizationKey()),
						groups.get(0).getHeading()),
				() -> assertEquals(1, groups.get(0).getValues().length),
				() -> assertThat(groups.get(0).getValues()[0], containsString("= 3"))
		);
	}
}
