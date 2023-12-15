package org.geogebra.web.full.gui.dialog.template;

import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.openfileview.MaterialCardI;
import org.geogebra.web.full.gui.openfileview.RemoveDialog;
import org.geogebra.web.full.gui.util.ContextMenuButtonCard;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;

public class ContextMenuButtonDeleteCard extends ContextMenuButtonCard {
    private MaterialCardI card;
    private boolean requireConfirm = false;

    /**
     * @param app application
     * @param card material card
     */
    public ContextMenuButtonDeleteCard(AppW app, MaterialCardI card) {
        super(app);
        this.card = card;
        initPopup();
    }

    /**
     * @param app application
     * @param card material card
     * @param requireConfirm whether should ask for confirmation
     */
    public ContextMenuButtonDeleteCard(AppW app, MaterialCardI card, boolean requireConfirm) {
        super(app);
        this.card = card;
        this.requireConfirm = requireConfirm;
        initPopup();
    }


    @Override
    protected void initPopup() {
        super.initPopup();
        addDeleteItem();
    }

    private void addDeleteItem() {
        addItem(MaterialDesignResources.INSTANCE.delete_black(),
                loc.getMenu("Delete"), this::onDelete);
    }

    private void onDelete() {
        if (requireConfirm) {
            DialogData data = new DialogData(null, "Cancel", "Delete");
            ComponentDialog removeDialog = new RemoveDialog(app, data, card);
            removeDialog.show();
            removeDialog.setOnPositiveAction(this::onConfirmDelete);
        } else {
            onConfirmDelete();
        }
    }

    private void onConfirmDelete() {
        card.getController().onConfirmDelete(card);
    }

    @Override
    protected void show() {
        super.show();
        wrappedPopup.show(this, 0, 0);
    }
}
