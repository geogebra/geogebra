package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.exam.ExamController;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.util.debug.Analytics;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.Persistable;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.RequiresResize;

/**
 * Calculator chooser for suite
 */
public class CalculatorSwitcherDialog extends GPopupPanel implements Persistable,
		RequiresResize {
	private FlowPanel contentPanel;
	private final ExamController examController = GlobalScope.examController;

	/**
	 * constructor
	 * @param app see {@link AppW}
	 */
	public CalculatorSwitcherDialog(AppW app, boolean autoHide) {
		super(autoHide, app.getAppletFrame(), app);
		setGlassEnabled(true);
		addStyleName("calcChooser");
		app.registerPopup(this);
		updateDialogWidth();
		buildGUI();
		app.addWindowResizeListener(this);
	}

	/**
	 * build switcher dialog
	 */
	public void buildGUI() {
		clear();
		contentPanel = new FlowPanel();
		Label title = new Label(app.getLocalization().getMenu("ChooseCalculator"));
		title.addStyleName("title");
		contentPanel.add(title);

		addButtons();
	}

	private void addButtons() {
		buildAndAddCalcButton(GeoGebraConstants.GRAPHING_APPCODE, contentPanel);
		if (app.getSettings().getEuclidian(-1).isEnabled()) {
			buildAndAddCalcButton(GeoGebraConstants.G3D_APPCODE, contentPanel);
		}
		buildAndAddCalcButton(GeoGebraConstants.GEOMETRY_APPCODE, contentPanel);
		if (app.getSettings().getCasSettings().isEnabled()) {
			buildAndAddCalcButton(GeoGebraConstants.CAS_APPCODE, contentPanel);
		}
		buildAndAddCalcButton(GeoGebraConstants.PROBABILITY_APPCODE, contentPanel);
		//buildAndAddCalcButton(GeoGebraConstants.SCIENTIFIC_APPCODE, contentPanel);
		add(contentPanel);
	}

	private void buildAndAddCalcButton(String subAppCode, FlowPanel contentPanel) {
		if (examController.isExamActive() && examController.isDisabledSubApp(subAppCode)) {
			return;
		}
		AppDescription description = AppDescription.get(subAppCode) ;
		String appNameKey = description.getNameKey();
		StandardButton button =  new StandardButton(72, description.getIcon(),
				app.getLocalization().getMenu(appNameKey));
		button.setStyleName("calcBtn");
		if (subAppCode.equals(app.getConfig().getSubAppCode())) {
			button.addStyleName("selected");
		}

		button.addFastClickHandler(source -> {
			hide();
			((AppWFull) app).setSuiteHeaderButton(subAppCode);
			((AppWFull) app).switchToSubapp(subAppCode);
			Analytics.logEvent(Analytics.Event.APP_SWITCHED, Analytics.Param.SUB_APP,
					Analytics.Param.convertToSubAppParam(subAppCode));
		});

		contentPanel.add(button);
	}

	@Override
	public void show() {
		super.show();
		super.center();
		((AppW) app).registerPopup(this);
	}

	@Override
	public void hide() {
		super.hide();
		((AppW) app).unregisterPopup(this);
	}

	@Override
	public void onResize() {
		if (isShowing()) {
			updateDialogWidth();
			super.centerAndResize(((AppW) app).getAppletFrame().getKeyboardHeight());
		}
	}

	private void updateDialogWidth() {
		Dom.toggleClass(this, "twoRows", app.getWidth() < 984
				&& app.getWidth() >= 480);
		Dom.toggleClass(this, "threeRows", app.getWidth() < 480);
	}
}
