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
        super(false);
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
