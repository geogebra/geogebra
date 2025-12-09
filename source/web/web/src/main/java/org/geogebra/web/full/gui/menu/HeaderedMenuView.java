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

package org.geogebra.web.full.gui.menu;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.web.full.gui.HeaderView;
import org.gwtproject.user.client.ui.FlowPanel;

class HeaderedMenuView extends FlowPanel {

	private HeaderView headerView;

	HeaderedMenuView(@Nonnull MenuView menuView) {
		setStyleName("headeredMenuView");
		add(menuView);
	}

	HeaderView getHeaderView() {
		return headerView;
	}

	void setHeaderView(@CheckForNull HeaderView headerView) {
		removeHeaderView();
		this.headerView = headerView;
		addHeaderView();
		styleHeaderView();
	}

	void setTitleHeader(boolean titleHeader) {
		if (headerView != null) {
			headerView.setStyleName("titleHeader", titleHeader);
		}
	}

	private void removeHeaderView() {
		if (headerView != null) {
			remove(headerView);
		}
	}

	private void addHeaderView() {
		if (headerView != null) {
			insert(headerView, 0);
		}
	}

	private void styleHeaderView() {
		if (headerView != null) {
			headerView.addStyleName("headerDivider");
		}
	}
}
