package org.geogebra.web.full.gui.dialog.template;

import com.google.gwt.user.client.ui.FlowPanel;
import org.geogebra.common.main.Localization;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.gui.dialog.OptionDialog;
import org.geogebra.web.html5.main.AppW;

import java.util.List;

public class TemplateChooser extends OptionDialog {
    private Localization loc;
    private TemplateChooserController controller;

    /**
     * @param app see {@link AppW}
     */
    public TemplateChooser(AppW app) {
        super(app.getPanel(), app);
        loc = app.getLocalization();
        this.controller = new TemplateChooserController(app, templatesLoadedCB());
        setGlassEnabled(true);
    }

    private void buildGUI() {
        this.getCaption().setText(loc.getMenu("New.Mebis"));
        this.addStyleName("templateChooser");
        FlowPanel dialogContent = new FlowPanel();
        dialogContent.addStyleName("templateChooserContent");
        FlowPanel templatesPanel = new FlowPanel();
        templatesPanel.addStyleName("templatesPanel");
        for (TemplatePreviewCard templateCard : controller.getTemplates()) {
            templatesPanel.add(templateCard);
        }
        dialogContent.add(templatesPanel);
        updateButtonLabels("Create");
        dialogContent.add(getButtonPanel());
        setPrimaryButtonEnabled(true);
        this.add(dialogContent);
        show();
    }

    public AsyncOperation<List<Material>> templatesLoadedCB() {
        return new AsyncOperation<List<Material>>() {
            @Override
            public void callback(List<Material> obj) {
                // build dialog once the templates are loaded
                buildGUI();
            }
        };
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
