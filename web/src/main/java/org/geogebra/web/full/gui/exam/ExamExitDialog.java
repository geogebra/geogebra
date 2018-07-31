package org.geogebra.web.full.gui.exam;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.util.StandardButton;
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
public class ExamExitDialog extends DialogBoxW
		implements FastClickHandler, SetLabels {
	private AppW appW;
	private FlowPanel dialog;
	// components of title panel
	private FlowPanel titlePanel;
	private Label calcType;
	private NoDragImage alertImg;
	private Label examTitle;
	// components of content panel
	private ScrollPanel scrollPanel;
	private FlowPanel contentPanel;
	private Label teacherText = new Label("");
	private Label durationLbl = new Label("");
	private Label duration = new Label("");
	private Label dateLbl = new Label("");
	private Label date = new Label("");
	private Label startTimeLbl = new Label("");
	private Label startTime = new Label("");
	private Label endTimeLbl = new Label("");
	private Label endTime = new Label("");
	private Label activityLbl = new Label("");
	private FlowPanel activityPanel;
	// components of button panel
	private FlowPanel buttonPanel;
	private StandardButton okBtn;

	/**
	 * @param app
	 *            application
	 */
	public ExamExitDialog(AppW app) {
		super(app.getPanel(), app);
		this.appW = app;
		this.addStyleName("examExitDialog");
		buildGUI();
	}

	private void buildGUI() {
		dialog = new FlowPanel();
		// build title panel
		titlePanel = new FlowPanel();
		titlePanel.setStyleName("titlePanel");
		calcType = new Label("");
		calcType.setStyleName("calcType");
		examTitle = new Label("");
		examTitle.setStyleName("examTitle");
		titlePanel.add(calcType);
		if (appW.getExam().isCheating()) {
			titlePanel.addStyleName("cheating");
			alertImg = new NoDragImage(
					MaterialDesignResources.INSTANCE.exam_error(), 24);
			titlePanel.add(LayoutUtilW.panelRowIndent(alertImg, examTitle));
		} else {
			titlePanel.add(examTitle);
		}
		// build content panel
		scrollPanel = new ScrollPanel();
		contentPanel = new FlowPanel();
		contentPanel.setStyleName("contentPanel");
		buildContent();
		scrollPanel.add(contentPanel);
		// build button panel
		buttonPanel = new FlowPanel();
		buttonPanel.setStyleName("DialogButtonPanel");
		if (appW.getExam().isCheating()) {
			buttonPanel.addStyleName("withDivider");
		}
		okBtn = new StandardButton("", appW);
		okBtn.addFastClickHandler(this);
		buttonPanel.add(okBtn);
		// build whole dialog
		dialog.add(titlePanel);
		dialog.add(scrollPanel);
		dialog.add(buttonPanel);
		this.add(dialog);
		setLabels();
	}

	private void buildContent() {
		teacherText.setStyleName("textStyle");
		contentPanel.add(teacherText);
		contentPanel.add(buildBlock(durationLbl, duration));
		contentPanel.add(buildBlock(dateLbl, date));
		contentPanel.add(buildBlock(startTimeLbl, startTime));
		contentPanel.add(buildBlock(endTimeLbl, endTime));
		if (appW.getExam().isCheating()) {
			activityPanel = buildActivityPanel();
			contentPanel.add(buildBlock(activityLbl, activityPanel));
		}
	}

	private FlowPanel buildActivityPanel() {
		activityPanel = new FlowPanel();
		Label currActivity = new Label("");
		String activityStr = appW.getExam().getLogTimes(appW.getLocalization());
		String[] activityVector = activityStr.split("\\r?\\n");
		for (String currActivityStr : activityVector) {
			currActivity = new Label(currActivityStr);
			currActivity.setStyleName("textStyle");
			activityPanel.add(currActivity);
		}
		return activityPanel;
	}

	private static FlowPanel buildBlock(Widget caption, Widget text) {
		FlowPanel block = new FlowPanel();
		caption.setStyleName("captionStyle");
		text.setStyleName("textStyle");
		block.add(caption);
		block.add(text);
		return block;
	}

	public void setLabels() {
		// title panel
		calcType.setText("GeoGebra "
				+ appW.getLocalization().getMenu("exam_calctype_graphing"));
		examTitle.setText(appW.getLocalization().getMenu("exam_menu_entry")
				+ ": " + (appW.getExam().isCheating()
						? appW.getLocalization().getMenu("exam_alert")
						: appW.getLocalization().getMenu("OK")));
		// content panel
		teacherText.setText(appW.getLocalization()
				.getMenu("exam_log_show_screen_to_teacher"));
		durationLbl.setText(appW.getLocalization().getMenu("Duration"));
		duration.setText(appW.getExam().getElapsedTimeLocalized());
		dateLbl.setText(appW.getLocalization().getMenu("exam_start_date"));
		date.setText(appW.getExam().getDate());
		startTimeLbl.setText(appW.getLocalization().getMenu("exam_start_time"));
		startTime.setText(appW.getExam().getStartTime());
		endTimeLbl.setText(appW.getLocalization().getMenu("exam_end_time"));
		endTime.setText(appW.getExam().getEndTime());
		activityLbl.setText(appW.getLocalization().getMenu("exam_activity"));
		// button panel
		okBtn.setText(appW.getLocalization().getMenu("Exit"));
	}

	public void onClick(Widget source) {
	}

	@Override
	public void show() {
		super.show();
		super.center();
	}
}
