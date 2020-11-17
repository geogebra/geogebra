package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.main.App;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.UserPreferredLanguage;
import org.geogebra.web.html5.util.Dom;
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
		buildGUI();
		app.registerAutoclosePopup(this);
	}

	/**
	 * Show/hide popup on appSwitcher btn click
	 */
	public void showPopup() {
		if (isShowing()) {
			hide();
		} else {
			setPopupPosition(getLeft(), 0);
			super.show();
			updateLanguage(app);
		}
	}

	private void buildGUI() {
		FlowPanel contentPanel = new FlowPanel();
		contentPanel.addStyleName("popupPanelForTranslation");
		addElement(GeoGebraConstants.GRAPHING_APPCODE, contentPanel);
		addElement(GeoGebraConstants.G3D_APPCODE, contentPanel);
		addElement(GeoGebraConstants.GEOMETRY_APPCODE, contentPanel);
		addElement(GeoGebraConstants.CAS_APPCODE, contentPanel);
		add(contentPanel);
	}

	private void addElement(final String subAppCode,
			FlowPanel contentPanel) {
		FlowPanel rowPanel = new FlowPanel();
		AppDescription description = AppDescription.get(subAppCode);
		NoDragImage img = new NoDragImage(description.getIcon(), 24, 24);
		img.addStyleName("appIcon");
		rowPanel.add(img);

		String key = description.getNameKey();
		Label label = new Label(app.getLocalization().getMenu(key));
		label.addStyleName("appPickerLabel");
		AriaHelper.setAttribute(label, "data-trans-key", key);
		rowPanel.add(label);
		rowPanel.setStyleName("appPickerRow");
		rowPanel.addDomHandler(event -> {
			hide();
			appPickerButton.setIconAndLabel(subAppCode);
			appPickerButton.checkButtonVisibility();
			((AppWFull) app).switchToSubapp(subAppCode);
		}, ClickEvent.getType());
		contentPanel.add(rowPanel);
	}

	private int getLeft() {
		return appPickerButton.getAbsoluteLeft() - X_COORDINATE_OFFSET ;
	}

	private void updateLanguage(App app) {
		Element suitePopup = Dom.querySelector("popupPanelForTranslation");
		if (suitePopup != null) {
			UserPreferredLanguage.translate(app, suitePopup);
		}
	}
}
