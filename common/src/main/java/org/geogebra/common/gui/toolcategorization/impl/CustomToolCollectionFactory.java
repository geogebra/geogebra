package org.geogebra.common.gui.toolcategorization.impl;

import java.util.List;

import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.gui.toolbar.ToolbarItem;
import org.geogebra.common.gui.toolcategorization.ToolCollection;
import org.geogebra.common.gui.toolcategorization.ToolsetLevel;
import org.geogebra.common.main.App;

import com.google.j2objc.annotations.Weak;

/**
 * Creates custom ToolCollections specified by toolbar strings.
 */
public class CustomToolCollectionFactory extends AbstractToolCollectionFactory {

    @Weak
    private App app;
    private String toolbarDefinition;

    /**
     * Creates a CustomToolCollectionFactory.
     *
     * @param app app
     * @param toolbarDefinition toolbar definition
     */
    public CustomToolCollectionFactory(App app, String toolbarDefinition) {
        this.app = app;
        this.toolbarDefinition = toolbarDefinition;
    }

    /**
     * Creates a CustomToolCollectionFactory with an empty definition.
     *
     * @param app app
     */
    public CustomToolCollectionFactory(App app) {
        this(app, "");
    }

    /**
     * Set the definition of the toolbar.
     *
     * @param toolbarDefinition toolbar definition
     */
    public void setToolbarDefinition(String toolbarDefinition) {
        this.toolbarDefinition = toolbarDefinition;
    }

    @Override
    public ToolCollection createToolCollection() {
        ToolCollectionImpl impl = new ToolCollectionImpl();
        impl.addLevel(ToolsetLevel.STANDARD);

        List<ToolbarItem> items = getToolbarItems();
        for (ToolbarItem item : items) {
            impl.addCategory(null, item.getMenu());
        }
        return impl;
    }

    private List<ToolbarItem> getToolbarItems() {
        List<ToolbarItem> toolbarVec;
        try {
            toolbarVec = ToolBar.parseToolbarString(toolbarDefinition);
        } catch (Exception e) {
            toolbarVec = ToolBar.parseToolbarString(ToolBar.getAllTools(app));
        }
        return toolbarVec;
    }
}
