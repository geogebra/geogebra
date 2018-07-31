package org.geogebra.web.full.gui.exam;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.DialogBoxW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
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
	private Label examTitle;
	// components of content panel
	private FlowPanel contentPanel;
	private Label teacherText;
	private Label durationLbl;
	private Label duration;
	private Label dateLbl;
	private Label date;
	private Label startTimeLbl;
	private Label startTime;
	private Label endTimeLbl;
	private Label endTime;
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
		titlePanel.add(examTitle);
		// build content panel
		contentPanel = new FlowPanel();
		contentPanel.setStyleName("contentPanel");
		teacherText = new Label("");
		teacherText.setStyleName("textStyle");
		durationLbl = new Label("");
		durationLbl.setStyleName("captionStyle");
		duration = new Label("");
		duration.setStyleName("textStyle");
		dateLbl = new Label("");
		dateLbl.setStyleName("captionStyle");
		date = new Label("");
		date.setStyleName("textStyle");
		contentPanel.add(teacherText);
		contentPanel.add(durationLbl);
		contentPanel.add(duration);
		contentPanel.add(dateLbl);
		contentPanel.add(date);
		// build button panel
		buttonPanel = new FlowPanel();
		buttonPanel.setStyleName("DialogButtonPanel");
		okBtn = new StandardButton("", appW);
		okBtn.addFastClickHandler(this);
		buttonPanel.add(okBtn);
		// build whole dialog
		dialog.add(titlePanel);
		dialog.add(contentPanel);
		dialog.add(buttonPanel);
		this.add(dialog);
		setLabels();
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
