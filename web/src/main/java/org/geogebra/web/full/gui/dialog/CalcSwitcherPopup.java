package org.geogebra.web.full.gui.dialog;

import org.geogebra.web.full.gui.images.SvgPerspectiveResources;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.view.button.header.AppPickerButton;
import org.geogebra.web.resources.SVGResource;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

public class CalcSwitcherPopup extends GPopupPanel {

	final AppWFull app;
	private final FlowPanel popupPanel;
	private boolean popupShowing = false;
	AppPickerButton appPickerButton;

	SvgPerspectiveResources res = SvgPerspectiveResources.INSTANCE;

	/**
	 * @param app
	 *            - application
	 * @param pickerButton
	 *            - button for popup
	 */
	public CalcSwitcherPopup(AppWFull app, AppPickerButton pickerButton) {
		super(true, app.getPanel(), app);
		this.app = app;
		this.appPickerButton = pickerButton;
		setGlassEnabled(false);
		addStyleName("calcPickerPopup");
		this.popupPanel = new FlowPanel();
		popupPanel.removeStyleName("dialogContent");
		popupPanel.addStyleName("calcPickerPanel");
		add(popupPanel);
	}

	/**
		shows the popup
	 */
	public void showCalcSwitcherPopup() {
		if (!popupShowing) {
			createElements(app);
			popupShowing = true;
			this.setPopupPosition(getLeft(), 0);
			show();
		} else {
			popupShowing = false;
			closeCalcSwitcherPopup();
		}
	}

	private void createElements(AppWFull app) {
		popupPanel.clear();
		addElement(app, res.menu_icon_algebra_transparent(), "GraphingCalculator.short");
		addElement(app, res.menu_icon_graphics3D_transparent(), "GeoGebra3DGrapher.short");
		addElement(app, res.menu_icon_geometry_transparent(), "Geometry");
		addElement(app, res.menu_icon_cas_transparent(), "CAS");
	}

	private void addElement(AppWFull app, SVGResource icon, String key) {
		FlowPanel rowPanel = new FlowPanel();
		SimplePanel imgPanel = new SimplePanel();
		imgPanel.addStyleName("appIcon");
		imgPanel.add(new NoDragImage(icon, 24, 24));
		rowPanel.add(imgPanel);
		Label label = new Label(app.getLocalization().getMenu(key));
		label.addStyleName("appPickerLabel");
		rowPanel.add(label);
		rowPanel.setStyleName("appPickerRow");
		rowPanel.addDomHandler(event -> {
				// open app
			closeCalcSwitcherPopup();
			popupShowing = false;
			appPickerButton.setIconAndLabel(icon, key);
		}, ClickEvent.getType());
		popupPanel.add(rowPanel);
	}

	private void closeCalcSwitcherPopup() {
		hide();
	}

	private int getLeft() {
		return appPickerButton.getAbsoluteLeft() - 8 ;
	}
}
