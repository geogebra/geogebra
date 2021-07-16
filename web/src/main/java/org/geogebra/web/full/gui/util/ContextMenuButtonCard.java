package org.geogebra.web.full.gui.util;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;

/**
 * Context Menu Popup for Cards
 * 
 * Tasks in subclass: add Menu Items, override show() to show the popup at the
 * correct position
 */
public class ContextMenuButtonCard extends StandardButton
		implements SetLabels, CloseHandler<GPopupPanel> {
	/** visible component */
	protected GPopupMenuW wrappedPopup;
	/** localization */
	protected Localization loc;
	/** application */
	protected AppW app;
	/** geogebra frame */
	protected GeoGebraFrameFull frame;

	/**
	 * @param app
	 *            application
	 */
	public ContextMenuButtonCard(AppW app) {
		super(MaterialDesignResources.INSTANCE.more_vert_black(), null, 24);
		this.app = app;
		loc = app.getLocalization();
		frame = ((AppWFull) app).getAppletFrame();
		initButton();
	}

	private void initButton() {
		addStyleName("mowMoreButton");

		ClickStartHandler.init(this, new ClickStartHandler(true, true) {
			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				if (isShowing()) {
					hide();
				} else {
					show();
				}
			}
		});
	}

	private SVGResource getActiveImageResource() {
		SVGResource resource = MaterialDesignResources.INSTANCE.more_vert_black();
		return resource.withFill(app.getVendorSettings().getPrimaryColor().toString());
	}

	/**
	 * adds a menu item
	 * 
	 * @param img
	 *            icon image
	 * @param text
	 *            menu item text
	 * @param cmd
	 *            command to execute
	 */
	protected AriaMenuItem addItem(SVGResource img, String text,
			ScheduledCommand cmd) {
		AriaMenuItem mi = new AriaMenuItem(
				MainMenu.getMenuBarHtml(img, text), true, cmd);
		wrappedPopup.addItem(mi);
		return mi;
	}

	/**
	 * init the popup
	 */
	protected void initPopup() {
		wrappedPopup = new GPopupMenuW(app);
		wrappedPopup.getPopupPanel().addCloseHandler(this);
		wrappedPopup.getPopupPanel().addAutoHidePartner(this.getElement());
		wrappedPopup.getPopupPanel().addStyleName("mowMatMenu");
	}

	@Override
	public void setLabels() {
		initPopup();
		setAltText(loc.getMenu("Options"));
	}

	/**
	 * @return true if context menu is showing
	 */
	protected boolean isShowing() {
		return wrappedPopup != null && wrappedPopup.isMenuShown();
	}

	/**
	 * show the context menu
	 */
	protected void show() {
		if (wrappedPopup == null) {
			initPopup();
		}
		wrappedPopup.getPopupMenu().focusDeferred();
		wrappedPopup.setMenuShown(true);
		toggleIcon(true);
	}

	/**
	 * hide the context menu
	 */
	public void hide() {
		wrappedPopup.hide();
		wrappedPopup.setMenuShown(false);
		toggleIcon(false);
	}

	@Override
	public void onClose(CloseEvent<GPopupPanel> event) {
		toggleIcon(false);
	}

	/**
	 * @param toggle
	 *            true if active
	 */
	protected void toggleIcon(boolean toggle) {
		if (toggle) {
			setIcon(getActiveImageResource());
			addStyleName("active");
		} else {
			setIcon(MaterialDesignResources.INSTANCE.more_vert_black());
			removeStyleName("active");
		}
	}
}