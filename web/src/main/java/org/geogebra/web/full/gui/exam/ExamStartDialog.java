package org.geogebra.web.full.gui.exam;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.full.gui.GuiManagerW;
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
 *         dialog to enter in graphing or cas calc exam mode
 *
 */
public class ExamStartDialog extends DialogBoxW
		implements SetLabels, FastClickHandler {
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
		cancelBtn.addFastClickHandler(this);
		startBtn = new StandardButton("", app);
		startBtn.addFastClickHandler(this);
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

	@Override
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

	@Override
	public void onClick(Widget source) {
		if (source == startBtn) {
			// ensure fullscreen: we may have lost it when handling unsaved
			// changes
			((AppW) app).getLAF().toggleFullscreen(true);
			app.setNewExam();
			app.startExam();
			if (app.getGuiManager() instanceof GuiManagerW
					&& ((GuiManagerW) app.getGuiManager())
							.getUnbundledToolbar() != null) {
				((GuiManagerW) app.getGuiManager()).setUnbundledHeaderStyle("examOk");
				((GuiManagerW) app.getGuiManager()).resetMenu();
				GlobalHeader.INSTANCE.addExamTimer();
				new ExamUtil((AppW) app).visibilityEventMain();
				((GuiManagerW) app.getGuiManager()).initInfoBtnAction();
			}			
		} else if (source == cancelBtn) {
			((AppW) app).getLAF().toggleFullscreen(false);
		}
		hide();
	}
}
