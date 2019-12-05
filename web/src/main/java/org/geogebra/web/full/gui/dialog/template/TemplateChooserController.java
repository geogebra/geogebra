package org.geogebra.web.full.gui.dialog.template;

import org.geogebra.common.main.App;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.html5.main.AppW;

import java.util.ArrayList;

public class TemplateChooserController {
    private ArrayList<TemplatePreviewCard> templates;
    private TemplatePreviewCard selected;

    public TemplateChooserController(AppW app) {
        templates = new ArrayList<>();
        templates.add(new TemplatePreviewCard(app, null, false,
                new AsyncOperation<TemplatePreviewCard>() {

                    @Override
                    public void callback(TemplatePreviewCard card) {
                        setSelected(card);
                    }
                }));
        templates.add(new TemplatePreviewCard(app, null, true,
                new AsyncOperation<TemplatePreviewCard>() {

            @Override
            public void callback(TemplatePreviewCard card) {
                setSelected(card);
            }
        }));
        templates.add(new TemplatePreviewCard(app, null, true,
                new AsyncOperation<TemplatePreviewCard>() {

            @Override
            public void callback(TemplatePreviewCard card) {
                setSelected(card);
            }
        }));
        templates.add(new TemplatePreviewCard(app, null, true,
                new AsyncOperation<TemplatePreviewCard>() {

            @Override
            public void callback(TemplatePreviewCard card) {
                setSelected(card);
            }
        }));
        templates.add(new TemplatePreviewCard(app, null, true,
                new AsyncOperation<TemplatePreviewCard>() {

            @Override
            public void callback(TemplatePreviewCard card) {
                setSelected(card);
            }
        }));
        setSelected(templates.get(0));
    }

    public TemplatePreviewCard getSelected() {
        return selected;
    }

    public void setSelected(TemplatePreviewCard newSelected) {
        if (selected != null) {
            this.selected.setSelected(false);
        }
        newSelected.setSelected(true);
        this.selected = newSelected;
    }

    public ArrayList<TemplatePreviewCard> getTemplates() {
        return templates;
    }

    public void onCreate(App app) {
        if (selected.getMaterial() == null) {
            app.fileNew();
        } else {
            selected.getController().loadOnlineFile();
        }
    }
}
