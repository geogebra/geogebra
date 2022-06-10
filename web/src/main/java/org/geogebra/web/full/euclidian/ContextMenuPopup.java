package org.geogebra.web.full.euclidian;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 * context menu
 */
public class ContextMenuPopup extends StandardButton
		implements CloseHandler<GPopupPanel>, RequiresResize {

	private final GPoint location;
	private final AppW app;
	private GPopupMenuW popup;

	/**
	 * @param app - application
	 * @param popup - context menu popup
	 */
	public ContextMenuPopup(AppW app, GPopupMenuW popup) {
		super(MaterialDesignResources.INSTANCE.more_vert_black(), 24);
		this.app = app;
		this.popup = popup;
		location = new GPoint();
		updateLocation();
		initPopup();
		addStyleName("MyCanvasButton");
		addStyleName("MyCanvasButton-borderless");
		app.addWindowResizeListener(this);
	}

	@Override
	public void onResize() {
		if (!popup.isMenuShown()) {
			return;
		}
		updateLocation();
		popup.show(location.x, location.y);
	}

	private void updateLocation() {
		int x = getAbsoluteLeft();
		int y = getAbsoluteTop() + getOffsetHeight();
		location.setLocation(x, y);
	}

	private void initPopup() {
		popup.getPopupPanel().addAutoHidePartner(getElement());
		popup.getPopupPanel().addCloseHandler(this);

		ClickStartHandler.init(this, new ClickStartHandler(true, true) {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				if (isMenuShown()) {
					hideMenu();
				} else {
					showMenu();
				}
				app.hideKeyboard();
			}
		});
	}

	/**
	 * show the menu
	 */
	public void showMenu() {
		updateLocation();
		updatePopup();
		popup.show(location.x, location.y);
	}

	public void updatePopup() {
		// override if the menu needs updating when opened
	}

	/**
	 * hide the menu
	 */
	public void hideMenu() {
		popup.hide();
	}

	@Override
	public void onClose(CloseEvent<GPopupPanel> event) {
		hideMenu();
	}
	
	/**
	 * @return in the menu open
	 */
	public boolean isMenuShown() {
		return popup.isMenuShown();
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		hideMenu();
	}
}