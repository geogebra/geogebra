package org.geogebra.web.full.gui.dialog.template;

import org.geogebra.common.main.App;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.TemplateChooserControllerI;

import java.util.ArrayList;
import java.util.List;

public class TemplateChooserController implements TemplateChooserControllerI {
    private ArrayList<TemplatePreviewCard> templates;
    private TemplatePreviewCard selected;

    /** Controller for the template chooser dialog
     */
    public TemplateChooserController() {
        templates = new ArrayList<>();
    }

    @Override
    public void fillTemplates(AppW appW, List<Material> templates) {
        getTemplates().clear();
        getTemplates().add(new TemplatePreviewCard(appW, null, false,
                new AsyncOperation<TemplatePreviewCard>() {

                    @Override
                    public void callback(TemplatePreviewCard card) {
                        setSelected(card);
                    }
                }));
        for (Material material : templates) {
            getTemplates().add(new TemplatePreviewCard(appW, material, true,
                    new AsyncOperation<TemplatePreviewCard>() {

                @Override
                public void callback(TemplatePreviewCard card) {
                    setSelected(card);
                }
            }));
        }
        setSelected(getTemplates().get(0));
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
