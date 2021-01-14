package org.geogebra.web.full.gui.toolbarpanel;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.view.button.MyToggleButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.html5.util.Persistable;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.impl.ImageResourcePrototype;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.himamis.retex.editor.share.util.GWTKeycodes;

/**
 * Toggle button for main menu
 */
public class MenuToggleButton extends MyToggleButton
		implements Persistable, KeyDownHandler {

	private AppW appW;

	/**
	 * @param app
	 *            application
	 */
	public MenuToggleButton(AppW app) {
		super(getImage(), app);
		this.appW = app;
		buildUI();
	}

	private static Image getImage() {
		ImageResource menuImgRec = new ImageResourcePrototype(null,
				MaterialDesignResources.INSTANCE.toolbar_menu_black()
						.getSafeUri(),
				0, 0, 24, 24, false, false);
		return new Image(menuImgRec);
	}

	private void buildUI() {
		ClickStartHandler.init(this, new ClickStartHandler(true, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				toggleMenu();
			}
		});
		addKeyDownHandler(this);
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		int key = event.getNativeKeyCode();
		if (key != GWTKeycodes.KEY_ENTER && key != GWTKeycodes.KEY_SPACE) {
			return;
		}
		toggleMenu();
	}

	/**
	 * Toggle open/closed state of the menu
	 */
	protected void toggleMenu() {
		appW.hideKeyboard();
		appW.toggleMenu();
	}

	@Override
	public void setTitle(String title) {
		AriaHelper.setTitle(this, title);
	}

	/**
	 * Remove from DOM and insert into global header.
	 */
	public void addToGlobalHeader() {
		removeFromParent();
		RootPanel root = RootPanel.get("headerID");
		Element dummy = Dom.querySelectorForElement(root.getElement(),
				".menuBtn");
		Event.sinkEvents(root.getElement(), Event.KEYEVENTS);
		Event.setEventListener(root.getElement(),
				appW.getGlobalKeyDispatcher().getGlobalShortcutHandler());
		if (dummy != null) {
			dummy.removeFromParent();
		}
		root.insert(this, 0);

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