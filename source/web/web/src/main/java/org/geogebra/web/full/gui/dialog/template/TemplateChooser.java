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

import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.user.client.ui.FlowPanel;

public class TemplateChooser extends ComponentDialog {
    private TemplateChooserController controller;

    /**
     * @param app see {@link AppW}
     * @param data dialog transkeys
     * @param controller template chooser controller
     */
    public TemplateChooser(AppW app, DialogData data, TemplateChooserController controller) {
        super(app, data, false, true);
        this.controller = controller;
        this.addStyleName("templateChooser");
        buildContent();
        setOnPositiveAction(() -> controller.onCreate(app));
    }

    private void buildContent() {
        FlowPanel templatesPanel = new FlowPanel();
        templatesPanel.addStyleName("templatesPanel");
        for (TemplatePreviewCard templateCard : controller.getTemplates()) {
            templatesPanel.add(templateCard);
        }
        addDialogContent(templatesPanel);

        if (controller.getTemplates().size() > 6) {
            templatesPanel.getElement().getParentElement().addClassName("withBorder");
        } else {
            templatesPanel.getElement().getParentElement().removeClassName("withBorder");
        }
    }
}
