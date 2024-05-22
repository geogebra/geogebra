package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.exam.ExamListener;
import org.geogebra.common.exam.ExamState;
import org.geogebra.common.main.App;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.util.debug.Analytics;
import org.geogebra.web.full.gui.util.SuiteHeaderAppPicker;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.UserPreferredLanguage;
import org.geogebra.web.shared.GlobalHeader;
import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

public class AppSwitcherPopup extends GPopupPanel implements ExamListener {

	SuiteHeaderAppPicker appPickerButton;
	private final static int X_COORDINATE_OFFSET = 8;
	private FlowPanel contentPanel;

	/**
	 * @param app
	 *            - application
	 * @param pickerButton
	 *            - button for popup
	 */
	public AppSwitcherPopup(AppWFull app, SuiteHeaderAppPicker pickerButton) {
		super(true, app.getAppletFrame(), app);
		this.appPickerButton = pickerButton;
		this.app = app;
		addAutoHidePartner(appPickerButton.getElement());
		setGlassEnabled(false);
		addStyleName("appPickerPopup");
		GlobalScope.examController.addListener(this);
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
		if (GlobalScope.examController.isExamActive()
				&& GlobalScope.examController.isDisabledSubApp(subAppCode)) {
			return;
		}

		FlowPanel rowPanel = new FlowPanel();
		AppDescription description = AppDescription.get(subAppCode);
		NoDragImage img = new NoDragImage(description.getIcon(), 24, 24);
		img.addStyleName("appIcon");
		rowPanel.add(img);

		String key = description.getNameKey();
		Label label = BaseWidgetFactory.INSTANCE.newPrimaryText(app.getLocalization().getMenu(key),
				"appPickerLabel");
		AriaHelper.setAttribute(label, "data-trans-key", key);
		rowPanel.add(label);
		rowPanel.setStyleName("appPickerRow");
		rowPanel.addDomHandler(event -> switchToSubApp(subAppCode), ClickEvent.getType());
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

	private int getLeft() {
		return appPickerButton.getAbsoluteLeft() - X_COORDINATE_OFFSET ;
	}

	private void updateLanguage(App app) {
		UserPreferredLanguage.translate(app, ".popupPanelForTranslation");
	}

	@Override
	public void examStateChanged(ExamState newState) {
		if (newState == ExamState.ACTIVE || newState == ExamState.IDLE) {
			updateGUI();
		}
	}

//	@Override
//	public void examClearCurrentApp() {
//		app.fileNew();
//	}
//
//	@Override
//	public void examClearOtherApps() {
//		((AppWFull) app).clearSubAppCons();
//	}
//
//	@Override
//	public void examClearClipboard() {
//		app.getCopyPaste().clearClipboard();
//		app.getCopyPaste().copyTextToSystemClipboard("");
//	}
//
//	@Override
//	public void examSetActiveMaterial(@Nullable Material material) {
//		app.setActiveMaterial(material);
//	}
//
//	@CheckForNull
//	@Override
//	public Material examGetActiveMaterial() {
//		return app.getActiveMaterial();
//	}
//
//	@CheckForNull
//	@Override
//	public SuiteSubApp examGetCurrentSubApp() {
//		String subAppCode = app.getConfig().getSubAppCode();
//		if (subAppCode == null) {
//			return null;
//		}
//		switch (subAppCode) {
//		case GeoGebraConstants.CAS_APPCODE:
//			return SuiteSubApp.CAS;
//		case GeoGebraConstants.GEOMETRY_APPCODE:
//			return SuiteSubApp.GEOMETRY;
//		case GeoGebraConstants.GRAPHING_APPCODE:
//			return SuiteSubApp.GRAPHING;
//		case GeoGebraConstants.G3D_APPCODE:
//			return SuiteSubApp.G3D;
//		case GeoGebraConstants.PROBABILITY_APPCODE:
//			return SuiteSubApp.PROBABILITY;
//		case GeoGebraConstants.SCIENTIFIC_APPCODE:
//			return SuiteSubApp.SCIENTIFIC;
//		default:
//			return null;
//		}
//	}
//
//	@Override
//	public void examSwitchSubApp(@Nonnull SuiteSubApp subApp) {
//		switch (subApp) {
//		case CAS:
//			switchToSubApp(GeoGebraConstants.CAS_APPCODE);
//			return;
//		case GEOMETRY:
//			switchToSubApp(GeoGebraConstants.GEOMETRY_APPCODE);
//			return;
//		case GRAPHING:
//			switchToSubApp(GeoGebraConstants.GRAPHING_APPCODE);
//			return;
//		case G3D:
//			switchToSubApp(GeoGebraConstants.G3D_APPCODE);
//			return;
//		case PROBABILITY:
//			switchToSubApp(GeoGebraConstants.PROBABILITY_APPCODE);
//			return;
//		case SCIENTIFIC:
//			switchToSubApp(GeoGebraConstants.SCIENTIFIC_APPCODE);
//		}
//	}
}
