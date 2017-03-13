package org.geogebra.web.web.euclidian;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.ContextMenuGeoElementW;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.images.ImgResourceHelper;
import org.geogebra.web.web.gui.util.MyCJButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class ContextMenuPopup extends MyCJButton implements ClickHandler {

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
		popup = ((GuiManagerW) app.getGuiManager())
				.getPopupMenu(ec.getAppSelectedGeos(), location);
		addClickHandler(this);
		addStyleName("MyCanvasButton-borderless");
	}


	private void updateLocation() {
		int x = getAbsoluteLeft();
		int y = getAbsoluteTop() + getOffsetHeight() + GAP_Y;
		location.setLocation(x, y);
	}

	@Override
	public void onClick(ClickEvent event) {

		if (menuShown) {
			hideMenu();
		} else {
			showMenu();
		}
	}



	public void showMenu() {
		updateLocation();
		popup.show(location);
		ImgResourceHelper.setIcon(AppResources.INSTANCE.dots_active(), this);
		menuShown = true;

	}

	public void hideMenu() {
		menuShown = false;
		ImgResourceHelper.setIcon(AppResources.INSTANCE.dots(), this);
		app.closePopups();

	}

}
