package org.geogebra.web.full.gui.exam;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.exam.ExamLogBuilder;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.DialogBoxW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author csilla
 * 
 *         exam exit dialog with the whole information
 *
 */
public class ExamLogAndExitDialog extends DialogBoxW implements SetLabels {
	private final Runnable returnHandler;
	// components of title panel
	private FlowPanel titlePanel;
	private Label calcType;
	private Label examTitle;
	private FlowPanel contentPanel;
	private final Label teacherText = new Label("");
	private final Label durationLbl = new Label("");
	private final Label duration = new Label("");
	private final Label dateLbl = new Label("");
	private final Label date = new Label("");
	private final Label startTimeLbl = new Label("");
	private final Label startTime = new Label("");
	private final Label endTimeLbl = new Label("");
	private final Label endTime = new Label("");
	private final Label activityLbl = new Label("");
	private FlowPanel activityPanel;
	private StandardButton positiveBtn;
	private final Widget anchor;
	private final String positiveKey;

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
		this.positiveKey = positiveKey;
		this.addStyleName(isLogDialog ? "examLogDialog" : "examExitDialog");
		setGlassEnabled(false);
		buildGUI(isLogDialog);
	}

	private void buildGUI(boolean isLogDialog) {
		// build title panel
		buildTitlePanel();
		// build content panel
		// components of content panel
		ScrollPanel scrollPanel = new ScrollPanel();
		contentPanel = new FlowPanel();
		contentPanel.setStyleName(app.getExam().isCheating() && isLogDialog
				? "contentPanel cheating" : "contentPanel");
		buildContent(isLogDialog);
		scrollPanel.add(contentPanel);
		// build button panel
		// components of button panel
		FlowPanel buttonPanel = new FlowPanel();
		buttonPanel.setStyleName("DialogButtonPanel");
		if ((app.getExam().isCheating() && !isLogDialog)
				|| (isLogDialog && activityPanel != null
						&& activityPanel.getWidgetCount() > 7)) {
			buttonPanel.addStyleName("withDivider");
		}
		positiveBtn = new StandardButton("");
		if (isLogDialog) {
			positiveBtn.addFastClickHandler(ignored -> hide());
		} else {
			positiveBtn.addFastClickHandler(ignored -> hideAndExit());
		}
		buttonPanel.add(positiveBtn);
		// build whole dialog
		FlowPanel dialog = new FlowPanel();
		dialog.add(titlePanel);
		dialog.add(scrollPanel);
		dialog.add(buttonPanel);
		this.add(dialog);
		setLabels();
	}

	private void buildTitlePanel() {
		titlePanel = new FlowPanel();
		titlePanel.setStyleName("titlePanel");
		calcType = new Label("");
		calcType.setStyleName("calcType");
		examTitle = new Label("");
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
	}

	private void buildContent(boolean isLogDialog) {
		teacherText.setStyleName("textStyle");
		if (!isLogDialog) {
			contentPanel.add(teacherText);
			contentPanel.add(buildBlock(durationLbl, duration));
		}
		contentPanel.add(buildBlock(dateLbl, date));
		contentPanel.add(buildBlock(startTimeLbl, startTime));
		if (!isLogDialog) {
			contentPanel.add(buildBlock(endTimeLbl, endTime));
		}
		if (app.getExam().isCheating()) {
			activityPanel = buildActivityPanel(isLogDialog);
			contentPanel.add(buildBlock(activityLbl, activityPanel));
		}
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
	public void setLabels() {
		// title panel
		calcType.setText(app.getLocalization().getMenu(app.getConfig().getAppTransKey()));
		examTitle.setText(ExamUtil.status((AppW) app));
		// content panel
		teacherText.setText(app.getLocalization()
				.getMenu("exam_log_show_screen_to_teacher"));
		durationLbl.setText(app.getLocalization().getMenu("Duration"));
		duration.setText(app.getExam().getElapsedTimeLocalized());
		dateLbl.setText(app.getLocalization().getMenu("exam_start_date"));
		date.setText(app.getExam().getDate());
		startTimeLbl.setText(app.getLocalization().getMenu("exam_start_time"));
		startTime.setText(app.getExam().getStartTime());
		endTimeLbl.setText(app.getLocalization().getMenu("exam_end_time"));
		endTime.setText(app.getExam().getEndTime());
		activityLbl.setText(app.getLocalization().getMenu("exam_activity"));
		// button panel
		positiveBtn.setText(app.getLocalization().getMenu(positiveKey));
	}

	@Override
	public void show() {
		super.show();
		super.center();
		if (anchor != null) {
			anchor.addStyleName("selected");
		}
	}

	@Override
	public void onCancel() {
		if (returnHandler == null) {
			hide(); // just a log: hide
		} else if (!((AppW) app).getAppletParameters().getParamLockExam()) {
			hideAndExit();
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
