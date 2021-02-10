package org.geogebra.web.full.gui.exam;

import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.gui.layout.DockManagerW;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.layout.LayoutW;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.DialogBoxW;

import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Exam start dialog
 */
public class ExamDialog implements ClickHandler {
	private static boolean examStyle;
	/** Application */
	protected AppW app;
	/** Wrapped box */
	protected DialogBoxW box;
	private CheckBox cas;
	private Button btnCancel;

	/**
	 * @param app
	 *            application
	 */
	public ExamDialog(AppW app) {
		this.app = app;
	}

	/**
	 * Show the wrapped dialog
	 */
	public void show() {
		ensureExamStyle();
		Localization loc = app.getLocalization();
		final GuiManagerInterfaceW guiManager = app.getGuiManager();
		final boolean hasGraphing = app.getAppletParameters()
				.hasDataParamEnableGraphing();
		box = new DialogBoxW(false, true, null, app.getPanel(), app) {
			@Override
			protected void onCancel() {
				if (!hasGraphing) {
					cancelExam();
				}
			}
		};

		VerticalPanel mainWidget = new VerticalPanel();
		FlowPanel btnPanel = new FlowPanel();
		FlowPanel cbxPanel = new FlowPanel();
		// start dialog content with checkboxes
		FlowPanel startPanel = new FlowPanel();

		Button btnOk = new Button();
		btnCancel = new Button();
		Button btnHelp = new Button();

		btnPanel.add(btnOk);
		// we don't need cancel and help buttons for tablet exam apps
		if (!hasGraphing) {
			btnPanel.add(btnCancel);
			btnPanel.add(btnHelp);
			box.addStyleName("boxsize");
			btnCancel.setText(loc.getMenu("Cancel"));
			btnHelp.setText(loc.getMenu("Help"));
			// help button not needed for tablet
			if (app.getLAF().isTablet()) {
				btnHelp.setVisible(false);
			}
		} else {
			box.addStyleName("ExamTabletBoxsize");
		}

		int checkboxes = 0;

		if (!app.getSettings().getCasSettings().isEnabledSet()) {
			checkboxes++;
			cas = new CheckBox(loc.getMenu("Perspective.CAS"));
			cas.addStyleName("examCheckbox");
			cas.setValue(true);

			app.getExam().setCasEnabled(true, app.getSettings().getCasSettings());
			cbxPanel.add(cas);
			cas.addClickHandler(this); 
		}
		
		if (!app.getAppletParameters().hasDataParamEnable3D()) {
			checkboxes++;
			final CheckBox allow3D = new CheckBox(loc.getMenu("Perspective.3DGraphics"));
			allow3D.addStyleName("examCheckbox");
			allow3D.setValue(true);

			app.getSettings().getEuclidian(-1).setEnabled(true);

			cbxPanel.add(allow3D);
			allow3D.addClickHandler(event -> {
				app.getSettings().getEuclidian(-1).setEnabled(allow3D.getValue());
				guiManager.updateToolbarActions();
			});
		}
		guiManager.updateToolbarActions();
		if (checkboxes > 0) {
			Label description = new Label(loc.getMenu("exam_custom_description"));
			startPanel.add(description);
			startPanel.add(cbxPanel);
			// hide start dialog content until
			// airplane mode and pinning happened
			startPanel.setVisible(false);
			mainWidget.add(startPanel);
			cbxPanel.addStyleName("ExamCheckboxPanel");
			btnPanel.addStyleName("DialogButtonPanel");
			box.getCaption().setText(loc.getMenu("exam_custom_header"));
		} else {
			if (app.getAppletParameters().hasDataParamEnableGraphing()) {
				boolean supportsCAS = app.getSettings().getCasSettings().isEnabled();
				boolean supports3D = app.getSettings().getEuclidian(-1).isEnabled();
				Log.debug(supportsCAS + "," + supports3D + "," + app.enableGraphing());
				if (!supports3D) {
					// CAS EXAM: cas && !3d && ev
					if (supportsCAS) {
						// set CAS background view for Exam CAS
						app.getGgbApi().setPerspective("4");
						box.getCaption().setText(loc.getMenu("ExamCAS"));
					}
					// GRAPH EXAM: !cas && !3d && ev
					else {
						if (app.enableGraphing()) {
							box.getCaption().setText(loc.getMenu("ExamGraphingCalc.long"));
						} else {
							// set algebra view in background of start dialog
							// for tablet Exam Simple Calc
							// needed for GGB-1176
							app.getGgbApi().setPerspective("A");
							box.getCaption().setText(loc.getMenu("ExamSimpleCalc.long"));
							// disable context menu in AV
							app.setRightClickEnabledForAV(false);
						}
					}
				}
			} else {
				box.getCaption().setText(loc.getMenu("exam_custom_header"));
			}
		}

		// start exam button
		startPanel.setVisible(true);
		btnOk.setText(loc.getMenu("exam_start_button"));
		btnOk.addClickHandler(event -> startExam(box, app));

		mainWidget.add(btnPanel);
		box.setWidget(mainWidget);

		if ((app.getAppletParameters().hasDataParamEnableGraphing())) {
			btnOk.addStyleName("ExamTabletStartButton");
		}
		app.invokeLater(() -> box.center());

		// Cancel button
		btnCancel.addStyleName("cancelBtn");
		btnCancel.addClickHandler(this);
		// Help button
		btnHelp.addStyleName("cancelBtn");
		btnHelp.addClickHandler(
				event -> app.getFileManager().open("https://www.geogebra.org/tutorial/exam"));
	}

	/**
	 * Cancel button handler
	 */
	private void cancelExam() {
		app.getExam().exit();
		app.setExam(null);
		app.getLAF().toggleFullscreen(false);
		app.fireViewsChangedEvent();
		GuiManagerInterfaceW guiManager = app.getGuiManager();
		guiManager.updateToolbarActions();
		guiManager.setGeneralToolBarDefinition(
				ToolBar.getAllToolsNoMacros(true, false, app));
		guiManager.updateToolbar();
		guiManager.resetMenu();
		box.hide();

	}

	private void startExam() {
		startExam(box, app);
	}

	/**
	 * @param box
	 *            wrapped box
	 * @param app
	 *            application
	 */
	public static void startExam(DialogBoxW box, AppW app) {
		final GuiManagerInterfaceW guiManager = app.getGuiManager();
		app.getLAF().toggleFullscreen(true);
		ensureExamStyle();
		blockEscTab();

		guiManager.updateToolbarActions();
		app.getLAF().removeWindowClosingHandler();
		app.fileNew();
		app.updateRounding();
		// do this *before* perspective so that we have CAS toolbar for CAS
		guiManager.setGeneralToolBarDefinition(
				ToolBar.getAllToolsNoMacros(true, true, app));
		LayoutW.resetPerspectives(app);
		if (app.enableGraphing()) {
			// don't check for CAS supported but for data param
			if (app.getAppletParameters().getDataParamEnableCAS(false)) {
				// set CAS start view for Exam CAS
				app.getGgbApi().setPerspective("4");
			} else {
				app.getGgbApi().setPerspective("1");
			}
		} else {
			app.getGgbApi().setPerspective("A");
		}

		app.getKernel().getAlgebraProcessor().reinitCommands();
		app.startExam();
		app.fireViewsChangedEvent();
		guiManager.updateToolbar();
		guiManager.updateToolbarActions();
		guiManager.updateMenubar();
		guiManager.resetMenu();
		DockPanelW dp = ((DockManagerW) guiManager.getLayout().getDockManager())
				.getPanelForKeyboard();
		MathKeyboardListener listener = guiManager.getKeyboardListener(dp);
		if (listener != null && listener.needsAutofocus()) {
			app.showKeyboard(listener, true);
		}
		if (box != null) {
			box.hide();
		}
	}

	////////////////////////////////////
	// ANDROID TABLETS
	////////////////////////////////////

	/**
	 * In electron this is done by kiosk mode, but on Chromebook it still
	 * matters.
	 */
	private static native void blockEscTab() /*-{
		$doc.body.addEventListener("keyup", function(e) {
			if (e && e.keyCode == 27 && $doc.querySelector(".examToolbar")) {
				e.preventDefault()
			}
		});
		$doc.body.addEventListener("keydown", function(e) {
			if (e && (e.keyCode == 9 || e.keyCode == 27)
					&& $doc.querySelector(".examToolbar")) {
				e.preventDefault()
			}
		});
	}-*/;

	private static void ensureExamStyle() {
		if (examStyle) {
			return;
		}
		StyleInjector.inject(GuiResources.INSTANCE.examStyle().getText());
		examStyle = true;
	}

	/**
	 * Android exam OK button pressed
	 */
	protected void onButtonOk() {
		startExam();
	}

	@Override
	public void onClick(ClickEvent event) {
		Object source = event.getSource(); 
		if (source == cas) {
			onCasChecked();
		} else if (source == btnCancel) {
			onCancel();
		}
	}
	
	private void onCancel() {
		cancelExam();
	}

	private void onCasChecked() {
		app.getExam().setCasEnabled(cas.getValue(), app.getSettings().getCasSettings());
		app.getGuiManager().updateToolbarActions();
	}
}