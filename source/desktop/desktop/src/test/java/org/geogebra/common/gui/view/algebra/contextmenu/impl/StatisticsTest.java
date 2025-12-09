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

package org.geogebra.common.gui.view.algebra.contextmenu.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.gui.view.algebra.Suggestion;
import org.geogebra.common.gui.view.algebra.SuggestionStatistics;
import org.geogebra.common.kernel.geos.BaseSymbolicTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.Test;

public class StatisticsTest extends BaseSymbolicTest {

	@Test
	public void testIsAvailable() {
		GeoElement list1 = add("l1={1,2,3}");
		assertNotNull(SuggestionStatistics.get(list1));

		GeoElement list2 = add("l2={1,1}");
		assertNotNull(SuggestionStatistics.get(list2));

		GeoElement list3 = add("l3={1}");
		assertNotNull(SuggestionStatistics.get(list3));

		GeoElement list4 = add("l4={}");
		assertNull(SuggestionStatistics.get(list4));

		add("A=(1,2)");
		GeoElement list5 = add("l5={A,1,2,3}");
		assertNull(SuggestionStatistics.get(list5));
	}

	@Test
	public void testLabelAdded() {
		GeoElement list1 = add("{1,2,3}");
		Suggestion suggestion = SuggestionStatistics.get(list1);

		assertNotNull(suggestion);

		suggestion.execute(list1);
		list1 = lookup("l1");
		assertTrue(list1.isAlgebraLabelVisible());
	}

}
