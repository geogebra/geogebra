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

	private boolean popupShowing = false;
	AppPickerButton appPickerButton;

	final int X_COORDINATE_OFFSET = 8;

	/**
	 * @param app
	 *            - application
	 * @param pickerButton
	 *            - button for popup
	 */
	public CalcSwitcherPopup(AppWFull app, AppPickerButton pickerButton) {
		super(true, app.getPanel(), app);
		this.appPickerButton = pickerButton;
		setGlassEnabled(false);
		//addStyleName("calcPickerPopup");
		buildGUI(app);
	}

	@Override
	public void show() {
		if (!popupShowing) {
			popupShowing = true;
			this.setPopupPosition(getLeft(), 0);
			super.show();
		} else {
			hide();
		}
	}

	@Override
	public void hide() {
		super.hide();
		popupShowing = false;
	}

	private void buildGUI(AppWFull app) {
		FlowPanel contentPanel = new FlowPanel();
		SvgPerspectiveResources res = SvgPerspectiveResources.INSTANCE;
		addElement(app, res.menu_icon_algebra_transparent(), "GraphingCalculator.short", contentPanel);
		addElement(app, res.menu_icon_graphics3D_transparent(), "GeoGebra3DGrapher.short", contentPanel);
		addElement(app, res.menu_icon_geometry_transparent(), "Geometry", contentPanel);
		addElement(app, res.menu_icon_cas_transparent(), "CAS", contentPanel);
		add(contentPanel);
	}

	private void addElement(AppWFull app, SVGResource icon, String key, FlowPanel contentPanel) {
		FlowPanel rowPanel = new FlowPanel();
		NoDragImage img = new NoDragImage(icon, 24, 24);
		img.addStyleName("appIcon");
		rowPanel.add(img);

		Label label = new Label(app.getLocalization().getMenu(key));
		label.addStyleName("appPickerLabel");
		rowPanel.add(label);
		rowPanel.setStyleName("appPickerRow");
		rowPanel.addDomHandler(event -> {
				// open app
			hide();
			appPickerButton.setIconAndLabel(icon, key);
		}, ClickEvent.getType());
		contentPanel.add(rowPanel);
	}

	private int getLeft() {
		return appPickerButton.getAbsoluteLeft() - X_COORDINATE_OFFSET ;
	}
}
