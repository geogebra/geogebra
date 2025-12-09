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

package org.geogebra.common.gui.toolcategorization.impl;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.geogebra.common.gui.toolcategorization.GraphingToolSet;
import org.geogebra.common.gui.toolcategorization.ToolCategory;
import org.geogebra.common.gui.toolcategorization.ToolCollection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Test class for GraphingTools.
 */
@RunWith(MockitoJUnitRunner.class)
public class GraphingGraphingToolCollectionFactoryTest {

	private ToolCollection toolCollection;

	@Before
	public void setupTest() {
		toolCollection = new GraphingToolCollectionFactory(false)
				.createToolCollection();
	}

	@Test
	public void testGraphingTools() {
		List<ToolCategory> categories = toolCollection.getCategories();
		for (int i = 0; i < categories.size(); i++) {
			for (int tool : toolCollection.getTools(i)) {
				assertTrue("Should be available: " + tool, GraphingToolSet.isAvailable(tool));
			}
		}
	}
}
