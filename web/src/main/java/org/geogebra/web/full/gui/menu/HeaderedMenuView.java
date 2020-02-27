package org.geogebra.web.full.gui.menu;

import com.google.gwt.user.client.ui.FlowPanel;
import org.geogebra.web.full.gui.HeaderView;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HeaderedMenuView extends FlowPanel {

	private static final String HEADER_DIVIDER_STYLE = "headerDivider";

	private HeaderView headerView;

	public HeaderedMenuView(@Nonnull  MenuView menuView) {
		setStyleName("headeredMenuView");
		add(menuView);
	}

	public HeaderView getHeaderView() {
		return headerView;
	}

	public void setHeaderView(@Nullable HeaderView headerView) {
		removeHeaderView();
		this.headerView = headerView;
		addHeaderView();
		styleHeaderView();
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
