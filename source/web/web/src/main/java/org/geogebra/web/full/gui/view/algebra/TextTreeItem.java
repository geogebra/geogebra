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

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;

/**
 * ReTeX based implementation of AV Text
 *
 */
public class TextTreeItem extends LaTeXTreeItem {

	/**
	 * @param geo0
	 *            text geo
	 */
	public TextTreeItem(GeoElement geo0) {
		super(geo0);
	}

	@Override
	protected void doUpdate() {
		setNeedsUpdate(false);
		if (typeChanged()) {
			updateTreeItemAfterTypeChanged();
			return;
		}
		if (hasMarblePanel()) {
			marblePanel.update();
		}

		content.clear();

		((GeoText) geo).getDescriptionForAV(
				new DOMIndexHTMLBuilder(getDefinitionValuePanel(), app));
		content.add(getDefinitionValuePanel());
		getDefinitionValuePanel().getElement().addClassName("textWrap");
	}

	@Override
	public boolean isTextItem() {
		return true;
	}

	@Override
	public boolean isInputTreeItem() {
		return false;
	}
}
