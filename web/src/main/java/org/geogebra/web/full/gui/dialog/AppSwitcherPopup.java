package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.web.full.gui.images.SvgPerspectiveResources;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.UserPreferredLanguage;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.shared.SuiteHeaderAppPicker;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class AppSwitcherPopup extends GPopupPanel {

	SuiteHeaderAppPicker appPickerButton;

	private final static int X_COORDINATE_OFFSET = 8;

	/**
	 * @param app
	 *            - application
	 * @param pickerButton
	 *            - button for popup
	 */
	public AppSwitcherPopup(AppWFull app, SuiteHeaderAppPicker pickerButton) {
		super(true, app.getPanel(), app);
		this.appPickerButton = pickerButton;
		this.app = app;
		addAutoHidePartner(appPickerButton.getElement());
		setGlassEnabled(false);
		addStyleName("appPickerPopup");
		buildGUI(app);
		app.registerAutoclosePopup(this);
	}

	/**
	 * @param app
	 *            - application
	 */
	public void showPopup(AppW app) {
		if (isShowing()) {
			hide();
		} else {
			setPopupPosition(getLeft(), 0);
			super.show();
			updateLanguage(app);
		}
	}

	private void buildGUI(AppWFull app) {
		FlowPanel contentPanel = new FlowPanel();
		contentPanel.addStyleName("popupPanelForTranslation");
		SvgPerspectiveResources res = SvgPerspectiveResources.INSTANCE;
		addElement(app, res.menu_icon_algebra_transparent(), "GraphingCalculator.short",
				GeoGebraConstants.GRAPHING_APPCODE,
				contentPanel);
		addElement(app, res.menu_icon_graphics3D_transparent(), "GeoGebra3DGrapher.short",
				GeoGebraConstants.G3D_APPCODE,
				contentPanel);
		addElement(app, res.menu_icon_geometry_transparent(), "Geometry",
				GeoGebraConstants.GEOMETRY_APPCODE, contentPanel);
		addElement(app, res.cas_white_bg(), "CAS", GeoGebraConstants.CAS_APPCODE,
				contentPanel);
		add(contentPanel);
	}

	private void addElement(AppWFull app, SVGResource icon, String key, String subAppCode, FlowPanel contentPanel) {
		FlowPanel rowPanel = new FlowPanel();
		NoDragImage img = new NoDragImage(icon, 24, 24);
		img.addStyleName("appIcon");
		rowPanel.add(img);

		Label label = new Label(app.getLocalization().getMenu(key));
		label.addStyleName("appPickerLabel");
		AriaHelper.setAttribute(label, "data-trans-key", key);
		rowPanel.add(label);
		rowPanel.setStyleName("appPickerRow");
		rowPanel.addDomHandler(event -> {
			hide();
			appPickerButton.setIconAndLabel(icon, key, app);
			appPickerButton.checkButtonVisibility();
			app.switchToSubapp(subAppCode);
		}, ClickEvent.getType());
		contentPanel.add(rowPanel);
	}

	private int getLeft() {
		return appPickerButton.getAbsoluteLeft() - X_COORDINATE_OFFSET ;
	}

	private void updateLanguage(AppW app) {
		Element suitePopup = Dom.querySelector("popupPanelForTranslation");
		if (suitePopup != null) {
			UserPreferredLanguage.translate(app, suitePopup);
		}
	}
}
