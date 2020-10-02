package org.geogebra.web.full.gui.dialog;

import org.geogebra.web.full.gui.images.SvgPerspectiveResources;
import org.geogebra.web.full.gui.menubar.PerspectivesMenuW;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.shared.DialogBoxW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

public class CalcSwitcherPopup {

	/** dialog */
	final DialogBoxW box;
	/** application */
	final AppWFull app;
	private FlowPanel popupPanel;

	public CalcSwitcherPopup(AppWFull app) {
		this.app = app;
		box = new DialogBoxW(true, false, null, app.getPanel(), app) {
			@Override
			public void setPopupPosition(int left, int top) {
				super.setPopupPosition(472,0);
			}
		};
		box.setGlassEnabled(false);

		this.popupPanel = new FlowPanel();
		popupPanel.removeStyleName("dialogContent");
		popupPanel.addStyleName("calcChooserPanel");

		box.setWidget(popupPanel);
		box.addStyleName("calcChooserPopup");

	}

	public void showCalcPopup() {
		createElements(app);
		box.show();
	}

	private void createElements(AppWFull app) {
		SvgPerspectiveResources res = SvgPerspectiveResources.INSTANCE;
		popupPanel.clear();
		addElement(app, res.menu_icon_algebra_transparent(), "GraphingCalculator.short");
		addElement(app, res.menu_icon_graphics3D_transparent(), "GeoGebra3DGrapher.short");
		addElement(app, res.menu_icon_geometry_transparent(), "Geometry");
		addElement(app, res.menu_icon_cas_transparent(), "CAS");
	}

	private void addElement(AppWFull app, SVGResource icon, String key) {
		HorizontalPanel rowPanel = new HorizontalPanel();
		rowPanel.add(new NoDragImage(icon, 24, 24));
		Label label = new Label(app.getLocalization().getMenu(key));
		label.addStyleName("appChooserLabel");
		rowPanel.add(label);
		rowPanel.setStyleName("appChooserRow");
		rowPanel.addDomHandler(event -> {
				// open app
			closeCalcPopup();
		}, ClickEvent.getType());
		popupPanel.add(rowPanel);
	}

	private void closeCalcPopup() {
		box.hide();
	}
}
