package org.geogebra.web.full.gui.dialog.template;

import org.geogebra.common.main.App;
import org.geogebra.common.move.ggtapi.models.Chapter;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.ggtapi.models.MaterialCallback;

import java.util.ArrayList;
import java.util.List;

public class TemplateChooserController {
    private ArrayList<TemplatePreviewCard> templates;
    private TemplatePreviewCard selected;
    private MaterialCallbackI requestTemplatesCB;
    private AsyncOperation<List<Material>> templatesLoadedCB;

    /**
     * @param app see {@link AppW}
     */
    public TemplateChooserController(AppW app, AsyncOperation<List<Material>> templatesLoadedCB) {
        templates = new ArrayList<>();
        this.templatesLoadedCB = templatesLoadedCB;
        populateTemplates(app);
        setSelected(templates.get(0));
    }

    private void fillTemplates(AppW appW, List<Material> templates) {
        for (Material material : templates) {
            getTemplates().add(new TemplatePreviewCard(appW, material, true,
                    new AsyncOperation<TemplatePreviewCard>() {

                @Override
                public void callback(TemplatePreviewCard card) {
                    setSelected(card);
                }
            }));
        }
        templatesLoadedCB.callback(templates);
    }

    private void populateTemplates(AppW app) {
        templates.add(new TemplatePreviewCard(app, null, false,
                new AsyncOperation<TemplatePreviewCard>() {

                    @Override
                    public void callback(TemplatePreviewCard card) {
                        setSelected(card);
                    }
                }));
        requestTemplatesCB = getTemplatesCB(app);
        app.getLoginOperation().getGeoGebraTubeAPI().getTemplateMaterials(requestTemplatesCB);
    }

    private MaterialCallback getTemplatesCB(final AppW appW) {
        return new MaterialCallback() {

            @Override
            public void onLoaded(final List<Material> parseResponse, ArrayList<Chapter> meta) {
                fillTemplates(appW, parseResponse);
            }
        };
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
