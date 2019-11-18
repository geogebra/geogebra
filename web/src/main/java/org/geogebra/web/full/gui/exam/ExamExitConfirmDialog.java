package org.geogebra.web.full.gui.exam;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.DialogBoxW;
import org.geogebra.web.shared.GlobalHeader;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author csilla
 * 
 *         exit exam confirmation dialog
 *
 */
public class ExamExitConfirmDialog extends DialogBoxW
		implements SetLabels, FastClickHandler {
	private AsyncOperation<String> returnHandler;
	private FlowPanel mainPanel;
	private Label confirmText;
	private FlowPanel buttonPanel;
	private StandardButton cancelBtn;
	private StandardButton exitBtn;

	/**
	 * @param app
	 *            application
	 * @param returnHandler
	 *            return handler
	 */
	public ExamExitConfirmDialog(AppW app,
			AsyncOperation<String> returnHandler) {
		super(app.getPanel(), app);
		this.returnHandler = returnHandler;
		buildGUI();
	}

	private void buildGUI() {
		// init start dialog text
		mainPanel = new FlowPanel();
		confirmText = new Label("");
		confirmText.addStyleName("exitConfText");
		// create button panel
		buttonPanel = new FlowPanel();
		buttonPanel.setStyleName("DialogButtonPanel");
		cancelBtn = new StandardButton("", app);
		cancelBtn.addFastClickHandler(this);
		exitBtn = new StandardButton("", app);
		exitBtn.addFastClickHandler(this);
		buttonPanel.add(cancelBtn);
		buttonPanel.add(exitBtn);
		// build main panel
		mainPanel.add(confirmText);
		mainPanel.add(buttonPanel);
		mainPanel.addStyleName("examExitConfDialog");
		add(mainPanel);
		setGlassEnabled(true);
		setLabels();
	}

	@Override
	public void setLabels() {
		confirmText.setText(
				app.getLocalization().getMenu("exam_exit_confirmation"));
		exitBtn.setLabel(app.getLocalization().getMenu("Exit"));
		cancelBtn.setLabel(app.getLocalization().getMenu("Cancel"));
	}

	@Override
	public void show() {
		super.show();
		center();
	}

	@Override
	public void onClick(Widget source) {
		if (source == exitBtn) {
			app.getExam().exit();
			GlobalHeader.INSTANCE.resetAfterExam();
			new ExamLogAndExitDialog((AppW) app, false, returnHandler, null).show();
			hide();
		} else if (source == cancelBtn) {
			hide();
		}
	}
}
