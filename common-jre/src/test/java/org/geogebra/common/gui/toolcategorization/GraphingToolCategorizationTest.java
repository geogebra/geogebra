package org.geogebra.common.gui.toolcategorization;

import org.geogebra.common.BaseUnitTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

/**
 * Test class for ToolCategorization.
 */
@RunWith(MockitoJUnitRunner.class)
public class GraphingToolCategorizationTest extends BaseUnitTest {

    private ToolCategorization toolCategorization;

    @Before
    public void setupTest() {
        getApp().getSettings().getToolbarSettings().setType(ToolCategorization.AppType.GRAPHING_CALCULATOR);
        toolCategorization = getApp().createToolCategorization();
        toolCategorization.resetTools();
    }

    @Test
    public void testGraphingTools() {
        ArrayList<ToolCategorization.Category> categories = toolCategorization.getCategories();
        for (int i = 0; i < categories.size(); i++) {
            for (int j = 0; j < toolCategorization.getTools(i).size(); j++) {
                Assert.assertFalse(GraphingToolSet.isInGraphingToolSet(toolCategorization.getTools(i).get(j)));
            }
        }
    }
}
