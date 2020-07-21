package org.geogebra.web.full.gui.dialog.template;

import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.util.ContextMenuButtonCard;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.Command;

public class ContextMenuButtonTemplateCard extends ContextMenuButtonCard {
    private  TemplatePreviewCard templateCard;

    /**
     * @param app application
     */
    public ContextMenuButtonTemplateCard(AppW app, TemplatePreviewCard templateCard) {
        super(app);
        this.templateCard = templateCard;
        initPopup();
    }

    @Override
    protected void initPopup() {
        super.initPopup();
        addDeleteItem();
    }

    private void addDeleteItem() {
        addItem(MaterialDesignResources.INSTANCE.delete_black(),
                loc.getMenu("Delete"), new Command() {
                    @Override
                    public void execute() {
                        onDelete();
                    }
                });
    }

    private void onDelete() {
        templateCard.getController().onConfirmDelete(templateCard);
    }

    @Override
    protected void show() {
        super.show();
        wrappedPopup.show(this, 0, 0);
    }
}
