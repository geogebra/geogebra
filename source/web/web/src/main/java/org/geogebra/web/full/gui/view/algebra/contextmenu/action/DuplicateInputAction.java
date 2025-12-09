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

package org.geogebra.web.full.gui.view.algebra.contextmenu.action;

import org.geogebra.common.gui.view.algebra.AlgebraItem;
import org.geogebra.common.gui.view.algebra.contextmenu.MenuAction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItem;

public class DuplicateInputAction implements MenuAction<GeoElement> {

	private AlgebraViewW algebraView;

	public DuplicateInputAction(AlgebraViewW algebraView) {
		this.algebraView = algebraView;
	}

	@Override
	public void execute(GeoElement item) {
		RadioTreeItem input = algebraView.getInputTreeItem();
		String dup = AlgebraItem.getDuplicateFormulaForGeoElement(item);
		RadioTreeItem currentNode = algebraView.getNode(item);
		if (currentNode != null) {
			currentNode.selectItem(false);
		}
		if (input != null) {
			input.setText(dup);
			input.setFocus(true);
		}
	}

	@Override
	public boolean isAvailable(GeoElement item) {
		return item.isAlgebraDuplicateable();
	}
}
