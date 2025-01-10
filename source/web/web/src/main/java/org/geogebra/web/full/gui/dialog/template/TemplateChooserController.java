package org.geogebra.web.full.gui.dialog.template;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.main.App;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.TemplateChooserControllerI;

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
        getTemplates().add(buildCard(appW, null, false));
        for (Material material : templates) {
            getTemplates().add(buildCard(appW, material, true));
        }
        setSelected(getTemplates().get(0));
    }

    private TemplatePreviewCard buildCard(AppW appW, Material material, boolean hasMoreButton) {
        return new TemplatePreviewCard(appW, material, hasMoreButton,
                card -> setSelected(card));
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
