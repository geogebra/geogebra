package org.geogebra.web.web.euclidian;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.ContextMenuGeoElementW;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.images.ImgResourceHelper;
import org.geogebra.web.web.gui.util.MyCJButton;
import org.geogebra.web.web.gui.util.PopupPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;

public class ContextMenuPopup extends MyCJButton
		implements ClickHandler, CloseHandler<PopupPanel> {

	private static final int GAP_Y = 5;
	private EuclidianController ec;
	private GPoint location;
	private boolean menuShown = false;
	private AppW app;
	ContextMenuGeoElementW popup;
	public ContextMenuPopup(AppW app) {
		super();
		this.app = app;
		ImgResourceHelper.setIcon(AppResources.INSTANCE.dots(), this);
		ec = app.getActiveEuclidianView().getEuclidianController();
		location = new GPoint();
		updateLocation();
		createPopup();
		addStyleName("MyCanvasButton-borderless");

	}


	private void updateLocation() {
		int x = getAbsoluteLeft();
		int y = getAbsoluteTop() + getOffsetHeight() + GAP_Y;
		location.setLocation(x, y);
	}

	private void createPopup() {
		popup = ((GuiManagerW) app.getGuiManager())
				.getPopupMenu(ec.getAppSelectedGeos(), location);
		popup.getWrappedPopup().getPopupPanel().addCloseHandler(this);
		addClickHandler(this);

	}
	@Override
	public void onClick(ClickEvent event) {

		if (menuShown) {
			hideMenu();
			app.closePopups();
		} else {
			showMenu();
		}
	}



	public void showMenu() {
		updateLocation();
		popup.update();
		popup.show(location);
		ImgResourceHelper.setIcon(AppResources.INSTANCE.dots_active(), this);
		menuShown = true;

	}

	public void hideMenu() {
		menuShown = false;
		ImgResourceHelper.setIcon(AppResources.INSTANCE.dots(), this);

	}

	public void onClose(CloseEvent<PopupPanel> event) {
		hideMenu();
	}

}
