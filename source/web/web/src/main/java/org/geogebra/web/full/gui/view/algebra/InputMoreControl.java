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

import org.geogebra.common.util.StringUtil;

/**
 * Input control with more button
 */
public class InputMoreControl implements InputItemControl {

	private RadioTreeItem item;
	
	/**
	 * @param item
	 *            algebra view item
	 */
	public InputMoreControl(RadioTreeItem item) {
		this.item = item;
	}

	@Override
	public void ensureInputMoreMenu() {
		if (!item.isInputTreeItem()) {
			return;
		}

		item.controls.setMoreButtonVisible(!StringUtil.empty(item.getText()));
	}

	@Override
	public void hideInputMoreButton() {
		if (item.isInputTreeItem() && item.controls != null) {
			item.controls.setMoreButtonVisible(false);
		}
	}
	
	@Override
	public void ensureControlVisibility() {
		if (item.controls == null) {
			return;
		}
		
		if (item.isInputTreeItem()) {
			item.controls.setMoreButtonVisible(false);
		} else {
			item.controls.setVisible(true);
		}
	}

	@Override
	public void addClearButtonIfSupported() {
		// no clear buttons here
	}

	@Override
	public void addInputControls() {
		item.addControls();
		item.controls.setMoreButtonVisible(false);
	}

	@Override
	public boolean hasMoreMenu() {
		return true;
	}
}