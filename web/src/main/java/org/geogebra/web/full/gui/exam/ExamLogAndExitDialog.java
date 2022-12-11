package org.geogebra.web.full.gui.exam;

import org.geogebra.common.main.exam.ExamLogBuilder;
import org.geogebra.common.main.exam.restriction.ExamRegion;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.ScrollPanel;
import org.gwtproject.user.client.ui.Widget;

/**
 * Exam exit dialog with the whole information
 */
public class ExamLogAndExitDialog extends GPopupPanel {
	private final Runnable returnHandler;
	private FlowPanel contentPanel;
	private FlowPanel activityPanel;
	private final Widget anchor;

	public ExamLogAndExitDialog(AppW app, boolean isLogDialog,
			Widget anchor) {
		this(app, isLogDialog, null, anchor, "OK");
	}

	/**
	 * @param app
	 *            application
	 * @param isLogDialog
	 *            true if need to build log dialog
	 * @param returnHandler
	 *            return handler
	 * @param anchor
	 *            anchor
	 */
	public ExamLogAndExitDialog(AppW app, boolean isLogDialog,
			Runnable returnHandler, Widget anchor, String positiveKey) {
		super(app.getPanel(), app);
		this.returnHandler = returnHandler;
		this.anchor = anchor;
		this.setStyleName("dialogComponent");
		this.addStyleName(isLogDialog ? "examLogDialog" : "examExitDialog");
		buildGUI(isLogDialog, positiveKey);
	}

	private void buildGUI(boolean isLogDialog, String positiveKey) {
		FlowPanel titlePanel = buildTitlePanel();

		ScrollPanel scrollPanel = new ScrollPanel();
		contentPanel = new FlowPanel();
		contentPanel.setStyleName(app.getExam().isCheating() && isLogDialog
				? "contentPanel cheating" : "contentPanel");
		buildContent(isLogDialog);
		scrollPanel.add(contentPanel);

		FlowPanel buttonPanel = buildButtonPanel(isLogDialog, positiveKey);

		FlowPanel dialog = new FlowPanel();
		dialog.add(titlePanel);
		if ((app.getExam().isCheating() && !isLogDialog)
				|| (isLogDialog && activityPanel != null
				&& activityPanel.getWidgetCount() > 7)) {
			scrollPanel.addStyleName("withDivider");
		}
		dialog.add(scrollPanel);
		dialog.add(buttonPanel);
		this.add(dialog);
	}

	private FlowPanel buildButtonPanel(boolean isLogDialog, String positiveKey) {
		FlowPanel buttonPanel = new FlowPanel();
		buttonPanel.setStyleName("dialogPanel");

		StandardButton positiveBtn = new StandardButton(app.getLocalization()
				.getMenu(positiveKey));
		positiveBtn.addStyleName("dialogTextButton");
		positiveBtn.addFastClickHandler(ignored -> {
			if (isLogDialog) {
				hide();
			} else {
				hideAndExit();
			}
		});
		buttonPanel.add(positiveBtn);

		return buttonPanel;
	}

	private FlowPanel buildTitlePanel() {
		FlowPanel titlePanel = new FlowPanel();
		titlePanel.setStyleName("titlePanel");
		ExamRegion examRegion = app.isExam() ? app.getExam().getExamRegion() : ExamRegion.GENERIC;
		String calcStr = examRegion.getDisplayName(app.getLocalization(), app.getConfig());
		Label calcType = new Label(calcStr);
		calcType.setStyleName("calcType");
		Label examTitle = new Label(ExamUtil.status((AppW) app));
		examTitle.setStyleName("examTitle");
		titlePanel.add(calcType);
		if (app.getExam().isCheating()) {
			titlePanel.addStyleName("cheating");
			NoDragImage alertImg = new NoDragImage(
					MaterialDesignResources.INSTANCE.exam_error(), 24);
			titlePanel.add(LayoutUtilW.panelRowIndent(alertImg, examTitle));
		} else {
			if (((AppW) app).getAppletParameters().getParamLockExam()) {
				titlePanel.addStyleName("locked");
			}
			titlePanel.add(examTitle);
		}
		return titlePanel;
	}

	private void buildContent(boolean isLogDialog) {
		Label teacherText = new Label(app.getLocalization()
				.getMenu("exam_log_show_screen_to_teacher"));
		teacherText.setStyleName("textStyle");
		if (!isLogDialog) {
			contentPanel.add(teacherText);
			addBlock("Duration", app.getExam().getElapsedTimeLocalized());
		}
		addBlock("exam_start_date", app.getExam().getDate());
		addBlock("exam_start_time", app.getExam().getStartTime());
		if (!isLogDialog) {
			addBlock("exam_end_time", app.getExam().getEndTime());
		}
		if (app.getExam().isCheating()) {
			activityPanel = buildActivityPanel(isLogDialog);
			Label activityLbl = new Label(app.getLocalization().getMenu("exam_activity"));
			contentPanel.add(buildBlock(activityLbl, activityPanel));
		}
	}

	private void addBlock(String labelStr, String timeStr) {
		Label label = new Label(app.getLocalization().getMenu(labelStr));
		Label time = new Label(timeStr);
		contentPanel.add(buildBlock(label, time));
	}

	private FlowPanel buildActivityPanel(boolean isLogDialog) {
		activityPanel = new FlowPanel();
		app.getExam()
				.appendLogTimes(app.getLocalization(), new ExamLogBuilder() {
					@Override
					public void addLine(StringBuilder sb) {
						addActivity(new Label(sb.toString()));
					}
				}, !isLogDialog);
		return activityPanel;
	}

	/**
	 * @param label
	 *            activity row
	 */
	protected void addActivity(Label label) {
		label.setStyleName("textStyle");
		activityPanel.add(label);
	}

	private static FlowPanel buildBlock(Widget caption, Widget text) {
		FlowPanel block = new FlowPanel();
		caption.setStyleName("captionStyle");
		text.setStyleName("textStyle");
		block.add(caption);
		block.add(text);
		return block;
	}

	@Override
	public void show() {
		super.show();
		super.center();
		if (anchor != null) {
			anchor.addStyleName("selected");
		}
	}

	private void hideAndExit() {
		if (app.getGuiManager() instanceof GuiManagerW
				&& ((GuiManagerW) app.getGuiManager())
				.getUnbundledToolbar() != null) {
			((GuiManagerW) app.getGuiManager()).getUnbundledToolbar()
					.resetHeaderStyle();
		}
		((AppW) app).getLAF().toggleFullscreen(false);
		hide();
		returnHandler.run();
	}

	/**
	 * remove selected style of anchor btn
	 */
	public void removeSelection() {
		if (anchor != null) {
			anchor.removeStyleName("selected");
		}
	}

	@Override
	public void hide() {
		super.hide();
		removeSelection();
	}
}
