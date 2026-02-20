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
import org.geogebra.web.full.gui.view.algebra.compositefocus.AVItemFocusAccess;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Widget;

public class RadioTreeItemFocusAccess implements AVItemFocusAccess {

	private final RadioTreeItem item;

	public RadioTreeItemFocusAccess(RadioTreeItem item) {
		this.item = item;
	}

	@Override
	public Widget canvas() {
		return item.canvas;
	}

	@Override
	public FlowPanel definitionValuePanel() {
		return item.getDefinitionValuePanel();
	}

	@Override
	public Widget moreButton() {
		return item.controls != null ? item.controls.getMoreButton() : null;
	}

	@Override
	public Widget outputFormatButton() {
		return AlgebraOutputPanel.getSymbolicButtonIfExists(item.controls);
	}

	@Override
	public boolean isCheckboxItem() {
		return item.isCheckBoxItem();
	}

	@Override
	public Widget checkbox() {
		return isCheckboxItem() ? ((CheckboxTreeItem) item).checkBox : null;
	}

	@Override
	public boolean hasTwoRows() {
		return item.shouldBuildItemWithTwoRows();
	}

	@Override
	public Widget inputRow() {
		Widget canvas = canvas();
		return canvas != null ? canvas : definitionValuePanel();
	}

	@Override
	public Widget outputRow() {
		return item.outputPanel != null ? item.outputPanel.getValuePanel() : null;
	}

	@Override
	public GeoElement geo() {
		return item.geo;
	}

	@Override
	public boolean isInputItem() {
		return item.isInputTreeItem();
	}

	@Override
	public boolean isLinearNotation() {
		return false;
	}
}
