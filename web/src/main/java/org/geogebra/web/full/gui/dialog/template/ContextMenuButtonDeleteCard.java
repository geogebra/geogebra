package org.geogebra.web.full.gui.dialog.template;

import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.openfileview.MaterialCardI;
import org.geogebra.web.full.gui.util.ContextMenuButtonCard;
import org.geogebra.web.html5.main.AppW;

public class ContextMenuButtonDeleteCard extends ContextMenuButtonCard {
    private MaterialCardI card;

    /**
     * @param app application
     * @param card material card
     */
    public ContextMenuButtonDeleteCard(AppW app, MaterialCardI card) {
        super(app);
        this.card = card;
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
        card.onDelete();
    }

    @Override
    protected void show() {
        super.show();
        wrappedPopup.show(this, 0, 0);
    }
}
