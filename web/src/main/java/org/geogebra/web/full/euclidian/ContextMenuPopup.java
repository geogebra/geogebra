package org.geogebra.web.full.euclidian;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.ContextMenuGeoElementW;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.full.gui.util.MyCJButton;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.ClickEndHandler;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.ImgResourceHelper;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
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

	private EuclidianController ec;
	private GPoint location;
	private AppW app;
	/**
	 * context menu
	 */
	ContextMenuGeoElementW popup;

	/**
	 * @param app
	 *            - application
	 */
	public ContextMenuPopup(AppW app) {
		super();
		this.app = app;
		ImgResourceHelper.setIcon(app.isUnbundledOrWhiteboard()
				? MaterialDesignResources.INSTANCE.more_vert_black()
				: AppResources.INSTANCE.dots(), this);
		ec = app.getActiveEuclidianView().getEuclidianController();
		location = new GPoint();
		updateLocation();
		createPopup();
		addStyleName("MyCanvasButton-borderless");
		Window.addResizeHandler(this);
	}

	@Override
	public void onResize(ResizeEvent event) {
		if (!popup.isMenuShown()) {
			return;
		}
		updateLocation();
		popup.show(location);
	}

	private void updateLocation() {
		int x = getAbsoluteLeft();
		int y = getAbsoluteTop() + getOffsetHeight();
		location.setLocation(x, y);
	}

	private void createPopup() {
		popup = ((GuiManagerW) app.getGuiManager())
				.getPopupMenu(ec.getAppSelectedGeos());
		popup.getWrappedPopup().getPopupPanel()
				.addAutoHidePartner(getElement());
		popup.getWrappedPopup().getPopupPanel().addCloseHandler(this);
		// addClickHandler(this);
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
		ClickEndHandler.init(this, new ClickEndHandler(false, true) {

			@Override
			public void onClickEnd(int x, int y, PointerEventType type) {
				// only stop

			}
		});
		this.addMouseOverHandler(new MouseOverHandler() {
			
			@Override
			public void onMouseOver(MouseOverEvent event) {
				switchIcon(true);
			}
		});
		this.addMouseOutHandler(new MouseOutHandler() {
			
			@Override
			public void onMouseOut(MouseOutEvent event) {
				switchIcon(false);
			}
		});
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
			ImgResourceHelper
					.setIcon(getActiveMoreVert(), this);
		} else {
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
		popup.update();
		popup.show(location);
		ImgResourceHelper.setIcon(getActiveMoreVert(), this);
		this.addStyleName("noOpacity");
		popup.setMenuShown(true);
	}

	/**
	 * hide the menu
	 */
	public void hideMenu() {
		popup.setMenuShown(false);
		popup.getWrappedPopup().hide();
		unselectButton();
	}

	private void unselectButton() {
		ImgResourceHelper.setIcon(app.isUnbundledOrWhiteboard()
				? MaterialDesignResources.INSTANCE.more_vert_black()
				: AppResources.INSTANCE.dots(), this);
		this.removeStyleName("noOpacity");
	}

	@Override
	public void onClose(CloseEvent<GPopupPanel> event) {
		unselectButton();
		popup.setMenuShown(false);
	}
	
	/**
	 * @return in the menu open
	 */
	public boolean isMenuShown() {
		return popup.isMenuShown();
	}

	/**
	 * close popup
	 */
	public void close() {
		popup.getWrappedPopup().hide();
		hideMenu();
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
