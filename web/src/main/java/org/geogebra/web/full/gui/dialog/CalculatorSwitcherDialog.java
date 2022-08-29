package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.main.exam.restriction.ExamRestrictionModel;
import org.geogebra.common.main.exam.restriction.Restrictable;
import org.geogebra.common.util.debug.Analytics;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.Persistable;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 * Calculator chooser for suite
 */
public class CalculatorSwitcherDialog extends GPopupPanel implements Persistable,
		RequiresResize, Restrictable {
	private FlowPanel contentPanel;
	private ExamRestrictionModel restrictionModel;

	/**
	 * constructor
	 * @param app see {@link AppW}
	 */
	public CalculatorSwitcherDialog(AppW app, boolean autoHide) {
		super(autoHide, app.getPanel(), app);
		setGlassEnabled(true);
		addStyleName("calcChooser");
		Dom.toggleClass(this, "smallScreen", app.getWidth() < 914);
		app.registerPopup(this);
		app.registerRestrictable(this);
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

		add(contentPanel);
	}

	private void buildAndAddCalcButton(String subAppCode, FlowPanel contentPanel) {
		if (hasRestrictions() && restrictionModel.isAppRestricted(subAppCode)) {
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
	}

	@Override
	public void onResize() {
		if (isShowing()) {
			Dom.toggleClass(this, "smallScreen", app.getWidth() < 914);
			super.centerAndResize(((AppW) app).getAppletFrame().getKeyboardHeight());
		}
	}

	private boolean hasRestrictions() {
		return restrictionModel != null;
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
		buildGUI();
	}
}
