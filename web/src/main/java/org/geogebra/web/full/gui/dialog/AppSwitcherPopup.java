package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.main.App;
import org.geogebra.common.main.exam.restriction.ExamRestrictionModel;
import org.geogebra.common.main.exam.restriction.Restrictable;
import org.geogebra.common.util.debug.Analytics;
import org.geogebra.web.full.gui.util.SuiteHeaderAppPicker;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.UserPreferredLanguage;
import org.geogebra.web.shared.GlobalHeader;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class AppSwitcherPopup extends GPopupPanel implements Restrictable {

	SuiteHeaderAppPicker appPickerButton;
	private final static int X_COORDINATE_OFFSET = 8;
	private ExamRestrictionModel restrictionModel;
	private FlowPanel contentPanel;

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
		contentPanel = new FlowPanel();
		contentPanel.addStyleName("popupPanelForTranslation");
		updateGUI();
		add(contentPanel);
	}

	private void updateGUI() {
		contentPanel.clear();
		addElement(GeoGebraConstants.GRAPHING_APPCODE);
		if (app.getSettings().getEuclidian(-1).isEnabled()) {
			addElement(GeoGebraConstants.G3D_APPCODE);
		}
		addElement(GeoGebraConstants.GEOMETRY_APPCODE);
		if (app.getSettings().getCasSettings().isEnabled()) {
			addElement(GeoGebraConstants.CAS_APPCODE);
		}
		addElement(GeoGebraConstants.PROBABILITY_APPCODE);
	}

	private void addElement(final String subAppCode) {
		if (hasRestrictions() && restrictionModel.isAppRestricted(subAppCode)) {
			return;
		}

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
			switchToSubApp(subAppCode);
		}, ClickEvent.getType());
		contentPanel.add(rowPanel);
	}

	private void switchToSubApp(String subAppCode) {
		hide();
		appPickerButton.setIconAndLabel(subAppCode);
		GlobalHeader.onResize();
		app.hideMenu();
		((AppWFull) app).switchToSubapp(subAppCode);
		Analytics.logEvent(Analytics.Event.APP_SWITCHED, Analytics.Param.SUB_APP,
				Analytics.Param.convertToSubAppParam(subAppCode));
	}

	private boolean hasRestrictions() {
		return restrictionModel != null;
	}

	private int getLeft() {
		return appPickerButton.getAbsoluteLeft() - X_COORDINATE_OFFSET ;
	}

	private void updateLanguage(App app) {
		UserPreferredLanguage.translate(app, ".popupPanelForTranslation");
	}

	@Override
	public boolean isExamRestrictionModelAccepted(ExamRestrictionModel model) {
		return model.hasSubApps();
	}

	@Override
	public void setExamRestrictionModel(ExamRestrictionModel model) {
		restrictionModel = model;
	}

	@Override
	public void applyExamRestrictions() {
		updateGUI();
		if (restrictionModel != null
				&& restrictionModel.isAppRestricted(app.getConfig().getSubAppCode())) {
			switchToSubApp(restrictionModel.getDefaultAppCode());
		}
	}
}
