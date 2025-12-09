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

package org.geogebra.web.full.gui.toolbarpanel;

import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.geogebra.web.html5.gui.zoompanel.FocusableWidget;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.Persistable;
import org.geogebra.web.shared.GlobalHeader;
import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.ui.RootPanel;

import elemental2.dom.KeyboardEvent;

/**
 * Toggle button for main menu
 */
public class MenuToggleButton extends IconButton
		implements Persistable {
	private final AppW appW;

	/**
	 * @param app {@link AppW}
	 */
	public MenuToggleButton(AppW app) {
		super(app, () -> {}, new ImageIconSpec(MaterialDesignResources.INSTANCE
				.toolbar_menu_black()), "Menu");
		new FocusableWidget(AccessibilityGroup.MENU, null, this).attachTo(app);
		this.appW = app;
		buildUI();
	}

	private void buildUI() {
		addFastClickHandler(event -> toggleMenu());
		Dom.addEventListener(this.getElement(), "keydown", event -> {
			KeyboardEvent e = (KeyboardEvent) event;
			if (!"Enter".equals(e.code) && !"Space".equals(e.code)) {
				return;
			}
			toggleMenu();
			event.preventDefault();
			event.stopPropagation();
		});
	}

	/**
	 * Toggle open/closed state of the menu
	 */
	protected void toggleMenu() {
		appW.hideKeyboard();
		appW.toggleMenu();
	}

	/**
	 * update on language change
	 */
	public void setLabel() {
		String title = appW.getLocalization().getMenu("Menu");
		AriaHelper.setTitle(this, title);
	}

	/**
	 * Remove from DOM and insert into global header.
	 */
	public void addToGlobalHeader() {
		removeFromParent();
		Element root = RootPanel.get("logoID").getElement().getParentElement();
		Element dummy = Dom.querySelectorForElement(root, ".menuBtn");
		if (dummy != null) {
			dummy.removeFromParent();
		}
		onAttach();
		root.insertFirst(getElement());
		GlobalHeader.INSTANCE.setMenuBtn(this);
	}

	/**
	 * Update style for internal in {@link NavigationRail} / external use in {@link GlobalHeader}
	 * @param external whether the button is out of the applet
	 */
	public void setExternal(boolean external) {
		Dom.toggleClass(this, "menuBtn", "menu", external);
	}

}