package org.geogebra.web.shared.mow.header;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.GlobalHeader;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.SimplePanel;

public class NotesTopbar extends FlowPanel implements SetLabels {
	private final AppW appW;

	/**
	 * constructor
	 */
	public NotesTopbar(AppW appW) {
		this.appW = appW;
		addStyleName("topbar");
		buildGui();
	}

	private void buildGui() {
		addMenuButton();
	}

	private void addMenuButton() {
		if (!GlobalHeader.isInDOM() && appW.getAppletParameters().getDataParamShowMenuBar(false)) {
			IconButton menuBtn = new IconButton(appW.getLocalization(), MaterialDesignResources
					.INSTANCE.toolbar_menu_black(), "Menu", null);
			menuBtn.addFastClickHandler((event) -> toggleMenu());
			menuBtn.addStyleName("small");
			add(menuBtn);
			addDivider();
		}
	}

	/**
	 * Toggle open/closed state of the menu
	 */
	protected void toggleMenu() {
		appW.hideKeyboard();
		appW.toggleMenu();
	}

	private void addDivider() {
		SimplePanel divider = new SimplePanel();
		divider.addStyleName("divider");
		add(divider);
	}

	@Override
	public void setLabels() {
		// for later
	}
}
