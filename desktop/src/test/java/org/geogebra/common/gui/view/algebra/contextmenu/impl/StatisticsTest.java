package org.geogebra.common.gui.view.algebra.contextmenu.impl;

import org.geogebra.common.gui.view.algebra.Suggestion;
import org.geogebra.common.gui.view.algebra.SuggestionStatistics;
import org.geogebra.common.kernel.geos.BaseSymbolicTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.Assert;
import org.junit.Test;


public class StatisticsTest extends BaseSymbolicTest {

	@Test
	public void testIsAvailable() {
		GeoElement list1 = add("l1={1,2,3}");
		Assert.assertNotNull(SuggestionStatistics.get(list1));

		GeoElement list2 = add("l2={1,1}");
		Assert.assertNotNull(SuggestionStatistics.get(list2));

		GeoElement list3 = add("l3={1}");
		Assert.assertNotNull(SuggestionStatistics.get(list3));

		GeoElement list4 = add("l4={}");
		Assert.assertNull(SuggestionStatistics.get(list4));

		add("A=(1,2)");
		GeoElement list5 = add("l5={A,1,2,3}");
		Assert.assertNull(SuggestionStatistics.get(list5));
	}

	@Test
	public void testLabelAdded() {
		GeoElement list1 = add("{1,2,3}");
		Suggestion suggestion = SuggestionStatistics.get(list1);

		Assert.assertNotNull(suggestion);

		suggestion.execute(list1);
		list1 = lookup("l1");
		Assert.assertTrue(list1.isAlgebraLabelVisible());
	}

}
