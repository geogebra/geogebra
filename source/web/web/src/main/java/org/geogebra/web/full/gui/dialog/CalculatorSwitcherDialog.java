/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.ownership.SuiteScope;
import org.geogebra.common.util.debug.Analytics;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.Persistable;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.user.client.ui.RequiresResize;

/**
 * Calculator chooser for suite
 */
public class CalculatorSwitcherDialog extends ComponentDialog implements Persistable,
		RequiresResize {
	private final SuiteScope suiteScope;
	private StandardButton selectedSubAppButton;

	/**
	 * Creates a dialog to switch between the available sub-apps.
	 * @param app see {@link AppW}
	 * @param autoHide if dialog should be closed on canvas click
	 */
	public CalculatorSwitcherDialog(AppW app, boolean autoHide) {
		super(app, new DialogData("ChooseCalculator", null, null),
				autoHide, true);
		suiteScope = GlobalScope.getSuiteScope(app);
		addStyleName("calcChooser");
		Dom.toggleClass(this, "smallScreen", app.getWidth() < 914);
		buildGUI();
		app.addWindowResizeListener(this);
	}

	/**
	 * Build switcher dialog content
	 */
	public void buildGUI() {
		clearDialogContent();
		selectedSubAppButton = null;
		addButtons();
	}

	private void addButtons() {
		buildAndAddCalcButton(SuiteSubApp.GRAPHING);
		if (app.getSettings().getEuclidian(-1).isEnabled()) {
			buildAndAddCalcButton(SuiteSubApp.G3D);
		}
		buildAndAddCalcButton(SuiteSubApp.GEOMETRY);
		if (app.getSettings().getCasSettings().isEnabled()) {
			buildAndAddCalcButton(SuiteSubApp.CAS);
		}
		if (app.getSettings().getProbCalcSettings().isEnabled()) {
			buildAndAddCalcButton(SuiteSubApp.PROBABILITY);
		}
		buildAndAddCalcButton(SuiteSubApp.SCIENTIFIC);
	}

	private void buildAndAddCalcButton(SuiteSubApp subAppCode) {
		if (suiteScope.examController.isExamActive()
				&& suiteScope.restrictionsController.isDisabledSubApp(subAppCode)) {
			return;
		}
		AppDescription description = AppDescription.get(subAppCode) ;
		String appNameKey = description.getNameKey();
		StandardButton button =  new StandardButton(72, description.getIcon(),
				app.getLocalization().getMenu(appNameKey));
		button.getElement().setTabIndex(0);
		button.setStyleName("calcBtn");
		if (subAppCode.equals(app.getConfig().getSubApp())) {
			button.addStyleName("selected");
			selectedSubAppButton = button;
		}

		button.addFastClickHandler(source -> {
			hide();
			((AppWFull) app).setSuiteHeaderButton(subAppCode);
			((AppWFull) app).switchToSubapp(subAppCode);
			Analytics.logEvent(Analytics.Event.APP_SWITCHED, Analytics.Param.SUB_APP,
					Analytics.Param.convertToSubAppParam(subAppCode));
		});

		addDialogContent(button);
	}

	@Override
	public void onResize() {
		if (isShowing()) {
			Dom.toggleClass(this, "smallScreen", app.getWidth() < 914);
			super.centerAndResize(((AppW) app).getAppletFrame().getKeyboardHeight());
		}
	}

	@Override
	protected void initialFocusWidget() {
		if (selectedSubAppButton != null) {
			selectedSubAppButton.getElement().focus();
			updateFocusIndex(selectedSubAppButton);
		}
	}
}
