package org.geogebra.web.full.gui.exam;

import java.util.ArrayList;

import org.geogebra.common.exam.ExamController;
import org.geogebra.common.exam.ExamType;
import org.geogebra.common.main.Localization;
import org.geogebra.common.ownership.GlobalScope;
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
	private final ExamController examController = GlobalScope.examController;

	/**
	 * @param app application
	 * @param data dialog transkeys
	 */
	public ExamStartDialog(AppWFull app, DialogData data) {
		super(app, data, false, true);
		addStyleName("examStartDialog");
		buildContent();
		setOnNegativeAction(examController::cancelExam);
	}

	private void buildContent() {
		Localization localization = app.getLocalization();
		Label startText = BaseWidgetFactory.INSTANCE.newSecondaryText(
				localization.getMenu("exam_start_dialog_text"), "examStartText");
		addDialogContent(startText);
		if (mayChoseType((AppW) app)) {
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

	private boolean mayChoseType(AppW app) {
		return app.isSuite() && (!app.isLockedExam()
				|| ExamType.CHOOSE.equals(app.getAppletParameters().getParamExamMode()));
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