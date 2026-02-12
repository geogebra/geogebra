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

package org.geogebra.web.full.gui.exam;

import java.util.ArrayList;

import org.geogebra.common.exam.ExamController;
import org.geogebra.common.exam.ExamType;
import org.geogebra.common.main.Localization;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.gui.components.radiobutton.RadioButtonData;
import org.geogebra.web.full.gui.components.radiobutton.RadioButtonPanel;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.user.client.ui.Label;

/**
 * Dialog to enter in graphing or cas calc exam mode
 */
public class ExamStartDialog extends ComponentDialog {

	private ExamType selectedRegion = ExamType.GENERIC;
	private final ExamController examController;

	/**
	 * @param app application
	 * @param data dialog translation keys
	 */
	public ExamStartDialog(AppWFull app, DialogData data) {
		super(app, data, false, true);
		examController = GlobalScope.getExamController(app);
		if (examController == null) {
			return;
		}
		addStyleName("examStartDialog");
		buildContent();
		setOnNegativeAction(examController::cancelExam);
	}

	private void buildContent() {
		Localization localization = app.getLocalization();
		Label startText = BaseWidgetFactory.INSTANCE.newSecondaryText(
				localization.getMenu("exam_start_dialog_text"), "examStartText");
		addDialogContent(startText);
		if (mayChooseType((AppW) app)) {
			ArrayList<RadioButtonData<ExamType>> data = new ArrayList<>();
			for (ExamType region : ExamType.getAvailableValues(localization, app.getConfig())) {
				String displayName = region.getDisplayName(localization,
						app.getConfig());
				data.add(new RadioButtonData<>(displayName, region));
			}
			RadioButtonPanel<ExamType> regionPicker = new RadioButtonPanel<>(
					localization, data, ExamType.GENERIC, (selectedRegion) ->
				this.selectedRegion = selectedRegion);
			addDialogContent(regionPicker);
		} else if (app.isSuite()) {
			selectedRegion = ((AppWFull) app).getForcedExamType();
		}
	}

	/**
	 * @param app application
	 * @return whether it's possible to choose multiple exam types in the app
	 */
	public static boolean mayChooseType(AppW app) {
		String featureSet = app.getAppletParameters().getParamFeatureSet();
		return app.isSuite()
				&& (StringUtil.empty(featureSet) || ExamType.CHOOSE.equals(featureSet));
	}

	@Override
	public void onEscape() {
		if (!((AppW) app).isLockedExam()) {
			examController.cancelExam();
			hide();
		}
	}

	public ExamType getSelectedRegion() {
		return selectedRegion;
	}
}