/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.dialog.template;

import org.geogebra.common.contextmenu.ContextMenuItem;
import org.geogebra.web.full.gui.contextmenu.ImageMap;
import org.geogebra.web.full.gui.openfileview.MaterialCardI;
import org.geogebra.web.full.gui.util.ContextMenuButtonCard;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;

public class ContextMenuButtonDeleteCard extends ContextMenuButtonCard {
    private MaterialCardI card;

    /**
     * @param app application
     * @param card material card
     */
    public ContextMenuButtonDeleteCard(AppWFull app, MaterialCardI card) {
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
        for (ContextMenuItem item : app.getContextMenuFactory().makeMaterialContextMenu()) {
            wrappedPopup.addItem(new AriaMenuItem(item.getLocalizedTitle(loc),
					ImageMap.get(item.getIcon()), this::onDelete));
        }
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
