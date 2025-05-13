package org.geogebra.web.full.gui.util;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.core.client.Scheduler.ScheduledCommand;
import org.gwtproject.event.logical.shared.CloseEvent;
import org.gwtproject.event.logical.shared.CloseHandler;

/**
 * Context menu that can be opened by an icon button.
 */
public class IconButtonCardContextMenu
		implements SetLabels, CloseHandler<GPopupPanel> {
	protected AppWFull appW;
	protected Localization loc;
	protected GPopupMenuW wrappedPopup;
	private final IconButton button;

	/**
	 * @param appW application
	 */
	public IconButtonCardContextMenu(AppWFull appW) {
		button = new IconButton(appW, null, new ImageIconSpec(MaterialDesignResources.INSTANCE
						.more_vert_black()), null);
		this.appW = appW;
		loc = appW.getLocalization();
		button.addFastClickHandler((event) -> {
			button.setActive(!isShowing());
			if (isShowing()) {
				hide();
			} else {
				show();
			}
		});
		button.addStyleName("cardContextMenuIconButton");
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
		AriaMenuItem mi = MainMenu.getMenuBarItem(img, text, cmd);
		wrappedPopup.addItem(mi);
		return mi;
	}

	/**
	 * init the popup
	 */
	protected void initPopup() {
		wrappedPopup = new GPopupMenuW(appW);
		wrappedPopup.getPopupPanel().addCloseHandler(this);
		wrappedPopup.getPopupPanel().addAutoHidePartner(button.getElement());
	}

	@Override
	public void setLabels() {
		initPopup();
		button.setAltText(loc.getMenu("Options"));
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
		wrappedPopup.show(button, -122, 36);
	}

	/**
	 * hide the context menu
	 */
	public void hide() {
		wrappedPopup.hide();
	}

	@Override
	public void onClose(CloseEvent<GPopupPanel> event) {
		button.setActive(false);
	}

	public IconButton getTriggerButton() {
		return button;
	}
}