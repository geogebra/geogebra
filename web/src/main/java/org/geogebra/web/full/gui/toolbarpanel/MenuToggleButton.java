package org.geogebra.web.full.gui.toolbarpanel;

import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.util.ToggleButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.Persistable;
import org.geogebra.web.shared.GlobalHeader;
import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.ui.RootPanel;

import elemental2.dom.KeyboardEvent;

/**
 * Toggle button for main menu
 */
public class MenuToggleButton extends ToggleButton
		implements Persistable {
	private AppW appW;

	/**
	 * @param app
	 *            application
	 */
	public MenuToggleButton(AppW app) {
		super(MaterialDesignResources.INSTANCE.toolbar_menu_black(),
				MaterialDesignResources.INSTANCE.toolbar_menu_black());
		removeStyleName("ToggleButton");
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
		setImageAltText(title);
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
	 * Update style for internal / external use
	 * 
	 * @param external
	 *            whether the button is out of the applet
	 */
	public void setExternal(boolean external) {
		Dom.toggleClass(this, "flatButtonHeader", "flatButton", external);
		Dom.toggleClass(this, "menuBtn", "menu", external);
	}

}