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
