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
import org.geogebra.common.util.ToStringConverter;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItem;

public class DuplicateOutputAction implements MenuAction<GeoElement> {

	private final AlgebraViewW algebraView;

	public DuplicateOutputAction(AlgebraViewW algebraView) {
		this.algebraView = algebraView;
	}

	@Override
	public void execute(GeoElement item) {
		ToStringConverter converter = algebraView.getApp()
				.getGeoElementValueConverter();
		RadioTreeItem input = algebraView.getInputTreeItem();
		String geoString = converter.toOutputValueString(item,
				algebraView.getApp().getKernel().getAlgebraTemplate());

		RadioTreeItem currentNode = algebraView.getNode(item);
		if (currentNode != null) {
			currentNode.selectItem(false);
		}
		if (input != null) {
			input.setText(geoString);
			input.setFocus(true);
		}
	}

	@Override
	public boolean isAvailable(GeoElement item) {
		return item.isAlgebraDuplicateable() && AlgebraItem.shouldShowBothRows(item,
				algebraView.getApp().getSettings().getAlgebra());
	}
}
