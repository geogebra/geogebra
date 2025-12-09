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

package org.geogebra.web.full.gui.dialog.template;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.main.App;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.TemplateChooserControllerI;

public class TemplateChooserController implements TemplateChooserControllerI {
    private final ArrayList<TemplatePreviewCard> templates;
    private final AppWFull app;
    private TemplatePreviewCard selected;

    /** Controller for the template chooser dialog
     */
    public TemplateChooserController(AppWFull app) {
        templates = new ArrayList<>();
        this.app = app;
    }

    @Override
    public void fillTemplates(List<Material> templates) {
        getTemplates().clear();
        getTemplates().add(buildCard(app, null, false));
        for (Material material : templates) {
            getTemplates().add(buildCard(app, material, true));
        }
        setSelected(getTemplates().get(0));
    }

    private TemplatePreviewCard buildCard(AppWFull appW, Material material, boolean hasMoreButton) {
        return new TemplatePreviewCard(appW, material, hasMoreButton,
				this::setSelected);
    }

    /**
     * @return currently selected card
     */
    public TemplatePreviewCard getSelected() {
        return selected;
    }

    /**
     * Store selected template card
     * @param newSelected currently selected card
     */
    public void setSelected(TemplatePreviewCard newSelected) {
        if (selected != null) {
            this.selected.setSelected(false);
        }
        newSelected.setSelected(true);
        this.selected = newSelected;
    }

    /**
     * @return list of template cards
     */
    public ArrayList<TemplatePreviewCard> getTemplates() {
        return templates;
    }

    /**
     * Action to take when create btn clicked
     * @param app see {@link AppW}
     */
    public void onCreate(App app) {
        if (selected.getMaterial() == null) {
            app.fileNew();
        } else {
            selected.getController().loadOnlineFile();
        }
    }
}
