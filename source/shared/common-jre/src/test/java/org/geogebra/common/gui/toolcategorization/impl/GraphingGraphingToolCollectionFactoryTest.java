package org.geogebra.common.gui.toolcategorization.impl;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.geogebra.common.gui.toolcategorization.GraphingToolSet;
import org.geogebra.common.gui.toolcategorization.ToolCategory;
import org.geogebra.common.gui.toolcategorization.ToolCollection;
import org.junit.Assert;
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
