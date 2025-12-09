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

package org.geogebra.web.full.gui.dialog.tools;

import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.util.FastClickHandler;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.user.client.ui.FlowPanel;

class MultiSelectButtonsPanel extends FlowPanel {
	public interface ButtonsListener {

		void moveSelection(boolean b);

		void deleteSelection();
	}

	public MultiSelectButtonsPanel(ButtonsListener widgets) {
		addStyleName("toolListButtons");

		addIconButton(MaterialDesignResources.INSTANCE.arrow_drop_up(),
				w -> widgets.moveSelection(true));
		addIconButton(MaterialDesignResources.INSTANCE.arrow_drop_down(),
				w -> widgets.moveSelection(false));
		addIconButton(MaterialDesignResources.INSTANCE.delete_black(),
				w -> widgets.deleteSelection());
	}

	private void addIconButton(SVGResource img,
			FastClickHandler clickHandler) {
		StandardButton btn = new StandardButton(img, null, 24);
		btn.addFastClickHandler(clickHandler);
		btn.addStyleName("IconButton");
		add(btn);
	}
}
