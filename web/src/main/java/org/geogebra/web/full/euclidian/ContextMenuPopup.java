package org.geogebra.web.full.euclidian;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.util.MyCJButton;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.ImgResourceHelper;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;

/**
 * context menu
 */
public class ContextMenuPopup extends MyCJButton
		implements CloseHandler<GPopupPanel>, MouseOverHandler, ResizeHandler {

	private final GPoint location;
	private final AppW app;

	/**
	 * context menu
	 */
	private GPopupMenuW popup;

	/**
	 * @param app
	 *            - application
	 */
	public ContextMenuPopup(AppW app, GPopupMenuW popup) {
		super();
		this.app = app;
		this.popup = popup;
		ImgResourceHelper.setIcon(MaterialDesignResources.INSTANCE.more_vert_black(), this);
		location = new GPoint();
		updateLocation();
		initPopup();
		addStyleName("MyCanvasButton-borderless");
		Window.addResizeHandler(this);
	}

	@Override
	public void onResize(ResizeEvent event) {
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

		addMouseOverHandler(event -> switchIcon(true));
		addMouseOutHandler(event -> switchIcon(false));
	}

	/**
	 * switch img on hover
	 * 
	 * @param isActive
	 *            is hover
	 */
	protected void switchIcon(boolean isActive) {
		if (isMenuShown()) {
			return;
		}

		if (isActive) {
			this.addStyleName("noOpacity");
			ImgResourceHelper
					.setIcon(getActiveMoreVert(), this);
		} else {
			this.removeStyleName("noOpacity");
			ImgResourceHelper
					.setIcon(MaterialDesignResources.INSTANCE.more_vert_black(),
							this);
		}
	}

	/**
	 * show the menu
	 */
	public void showMenu() {
		updateLocation();
		updatePopup();
		popup.show(location.x, location.y);
		ImgResourceHelper.setIcon(getActiveMoreVert(), this);
		popup.setMenuShown(true);
	}

	public void updatePopup() {
		// override if the menu needs updating when opened
	}

	/**
	 * hide the menu
	 */
	public void hideMenu() {
		popup.setMenuShown(false);
		popup.hide();
		switchIcon(false);
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

	@Override
	public void onMouseOver(MouseOverEvent event) {
		ImgResourceHelper.setIcon(getActiveMoreVert(), this);
	}

	private SVGResource getActiveMoreVert() {
		SVGResource resource = MaterialDesignResources.INSTANCE.more_vert_black();
		return resource.withFill(app.getVendorSettings().getPrimaryColor().toString());
	}
}
