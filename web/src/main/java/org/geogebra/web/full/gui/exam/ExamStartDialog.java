package org.geogebra.web.full.gui.exam;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.DialogBoxW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * @author csilla
 * 
 *         dialog to enter in graphing calc exam mode
 *
 */
public class ExamStartDialog extends DialogBoxW implements SetLabels {
	private FlowPanel mainPanel;
	private Label startText;
	private FlowPanel buttonPanel;
	private StandardButton cancelBtn;
	private StandardButton startBtn;

	/**
	 * @param app
	 *            application
	 */
	public ExamStartDialog(AppW app) {
		super(app.getPanel(), app);
		buildGUI();
	}

	private void buildGUI() {
		// init start dialog text
		mainPanel = new FlowPanel();
		startText = new Label("");
		startText.addStyleName("examStartText");
		// create button panel
		buttonPanel = new FlowPanel();
		buttonPanel.setStyleName("DialogButtonPanel");
		cancelBtn = new StandardButton("", app);
		startBtn = new StandardButton("", app);
		buttonPanel.add(cancelBtn);
		buttonPanel.add(startBtn);
		// build main panel
		mainPanel.add(startText);
		mainPanel.add(buttonPanel);
		mainPanel.addStyleName("examStartDialog");
		add(mainPanel);
		setGlassEnabled(true);
		setLabels();
	}

	public void setLabels() {
		getCaption().setText(app.getLocalization().getMenu("exam_menu_enter"));
		startText.setText(
				app.getLocalization().getMenu("exam_start_dialog_text"));
		startBtn.setLabel(app.getLocalization().getMenu("exam_start_button"));
		cancelBtn.setLabel(app.getLocalization().getMenu("Cancel"));
	}

	@Override
	public void show() {
		super.show();
		center();
	}

}
