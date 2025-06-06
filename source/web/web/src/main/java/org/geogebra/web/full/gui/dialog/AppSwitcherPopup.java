package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.exam.ExamController;
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
	private FlowPanel contentPanel;
	private final ExamController examController = GlobalScope.examController;

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
		app.getExamEventBus().add(this);
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
			showRelativeTo(appPickerButton);
			updateLanguage(app);
			app.hideKeyboard();
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
		addElement(SuiteSubApp.GRAPHING);
		if (app.getSettings().getEuclidian(-1).isEnabled()) {
			addElement(SuiteSubApp.G3D);
		}
		addElement(SuiteSubApp.GEOMETRY);
		if (app.getSettings().getCasSettings().isEnabled()) {
			addElement(SuiteSubApp.CAS);
		}
		addElement(SuiteSubApp.PROBABILITY);
		addElement(SuiteSubApp.SCIENTIFIC);
	}

	private void addElement(final SuiteSubApp subAppCode) {
		if (examController.isExamActive()
				&& examController.isDisabledSubApp(subAppCode)) {
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

	private void switchToSubApp(SuiteSubApp subAppCode) {
		hide();
		app.hideMenu();
		((AppWFull) app).switchToSubapp(subAppCode);
		GlobalHeader.onResize();
		Analytics.logEvent(Analytics.Event.APP_SWITCHED, Analytics.Param.SUB_APP,
				Analytics.Param.convertToSubAppParam(subAppCode));
	}

	private void updateLanguage(App app) {
		UserPreferredLanguage.translate(app, ".popupPanelForTranslation");
	}

	@Override
	public void examStateChanged(ExamState newState) {
		if (newState == ExamState.ACTIVE || newState == ExamState.IDLE) {
			boolean shouldShowAppsPicker = GlobalScope.getEnabledSubApps().size() > 1;
			if (shouldShowAppsPicker) {
				updateGUI();
			}
			appPickerButton.setVisible(shouldShowAppsPicker);
		}
	}
}
