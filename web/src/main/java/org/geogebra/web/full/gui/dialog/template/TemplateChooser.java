package org.geogebra.web.full.gui.dialog.template;

import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.ComponentDialog;
import org.geogebra.web.shared.components.DialogData;

import com.google.gwt.user.client.ui.FlowPanel;

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
        FlowPanel dialogContent = new FlowPanel();
        dialogContent.addStyleName("templateChooserContent");
        FlowPanel templatesPanel = new FlowPanel();
        templatesPanel.addStyleName("templatesPanel");
        if (controller.getTemplates().size() > 6) {
            templatesPanel.addStyleName("withBorder");
        }
        for (TemplatePreviewCard templateCard : controller.getTemplates()) {
            templatesPanel.add(templateCard);
        }
        dialogContent.add(templatesPanel);
        addDialogContent(dialogContent);
    }
}
