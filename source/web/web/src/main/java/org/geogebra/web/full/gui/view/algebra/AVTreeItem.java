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

package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.web.html5.gui.util.Dom;
import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.ui.TreeItem;
import org.gwtproject.user.client.ui.Widget;

/**
 * General AV item (group header or radio item)
 */
public class AVTreeItem extends TreeItem {

	/**
	 * Empty item
	 */
	public AVTreeItem() {
		super();
	}

	/**
	 * @param w
	 *            item content
	 */
	public AVTreeItem(Widget w) {
		super(w);
	}

	@Override
	public void setSelected(boolean selected) {
		super.setSelected(selected);

		Element w = Dom.querySelectorForElement(this.getElement(),
				".gwt-TreeItem-selected");
		if (w != null) {
			w.getStyle().setBackgroundColor("#FFFFFF");
		}
	}

}
