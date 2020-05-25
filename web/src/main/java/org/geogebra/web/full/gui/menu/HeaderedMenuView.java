package org.geogebra.web.full.gui.menu;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.geogebra.web.full.gui.HeaderView;

import com.google.gwt.user.client.ui.FlowPanel;

class HeaderedMenuView extends FlowPanel {

	private static final String HEADERED_MENU_STLYE = "headeredMenuView";
	private static final String HEADER_DIVIDER_STYLE = "headerDivider";
	private static final String TITLE_HEADER_STYLE = "titleHeader";

	private HeaderView headerView;

	HeaderedMenuView(@Nonnull MenuView menuView) {
		setStyleName(HEADERED_MENU_STLYE);
		add(menuView);
	}

	HeaderView getHeaderView() {
		return headerView;
	}

	void setHeaderView(@Nullable HeaderView headerView) {
		removeHeaderView();
		this.headerView = headerView;
		addHeaderView();
		styleHeaderView();
	}

	void setTitleHeader(boolean titleHeader) {
		if (headerView != null) {
			headerView.setStyleName(TITLE_HEADER_STYLE, titleHeader);
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
			headerView.addStyleName(HEADER_DIVIDER_STYLE);
		}
	}
}
