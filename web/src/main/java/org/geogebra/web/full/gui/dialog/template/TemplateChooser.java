package org.geogebra.web.full.gui.dialog.template;

import org.geogebra.common.main.Localization;
import org.geogebra.web.full.gui.dialog.OptionDialog;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;

public class TemplateChooser extends OptionDialog {
    private Localization loc;
    private TemplateChooserController controller;

    /**
     * @param app see {@link AppW}
     */
    public TemplateChooser(AppW app, TemplateChooserController controller) {
        super(app.getPanel(), app);
        loc = app.getLocalization();
        this.controller = controller;
        buildGUI();
        setGlassEnabled(true);
    }

    private void buildGUI() {
        this.getCaption().setText(loc.getMenu("New.Mebis"));
        this.addStyleName("templateChooser");
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
        updateButtonLabels("Create");
        dialogContent.add(getButtonPanel());
        setPrimaryButtonEnabled(true);
        this.add(dialogContent);
    }

    @Override
    public void show() {
        super.show();
        super.center();
    }

    @Override
    protected void processInput() {
        hide();
        controller.onCreate(app);
    }
}
