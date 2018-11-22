package org.geogebra.web.full.gui.exam;

import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.GTimer;
import org.geogebra.common.util.GTimerListener;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.gui.dialog.InputDialogW.DialogBoxKbW;
import org.geogebra.web.full.gui.layout.DockManagerW;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.layout.LayoutW;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.ExamEnvironmentW;
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
public class ExamDialog {
	private static boolean examStyle;
	/** Application */
	protected AppW app;
	/** Wrapped box */
	protected DialogBoxKbW box;

	private Localization loc;

	private Label instruction;

	private Button btnOk;
	private boolean lockTaskIsAvailable;
	private DialogState dialogState;
	private boolean wasAirplaneModeOn;
	private GTimer checkTaskLockTimer = null;
	// start dialog content with checkboxes
	private FlowPanel startPanel;

	private enum DialogState {
		WAIT_FOR_AIRPLANE_MODE, WAIT_FOR_TASK_LOCK, CAN_START_EXAM
	}

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
		loc = app.getLocalization();
		final GuiManagerInterfaceW guiManager = app.getGuiManager();
		final boolean hasGraphing = app.getArticleElement()
				.hasDataParamEnableGraphing();
		box = new DialogBoxKbW(false, true, null, app.getPanel(), app) {
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
		startPanel = new FlowPanel();

		btnOk = new Button();
		Button btnCancel = new Button();
		Button btnHelp = new Button();
		// mainWidget.add(btnPanel);

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
			final CheckBox cas = new CheckBox(loc.getMenu("Perspective.CAS"));
			cas.addStyleName("examCheckbox");
			cas.setValue(true);
			app.getSettings().getCasSettings().setEnabled(true);
			cbxPanel.add(cas);
			cas.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					app.getSettings().getCasSettings().setEnabled(cas.getValue());
					guiManager.updateToolbarActions();
				}
			});
		}
		if (!app.getSettings().getEuclidian(-1).isEnabledSet()) {
			checkboxes++;
			final CheckBox allow3D = new CheckBox(loc.getMenu("Perspective.3DGraphics"));
			allow3D.addStyleName("examCheckbox");
			allow3D.setValue(true);

			app.getSettings().getEuclidian(-1).setEnabled(true);

			cbxPanel.add(allow3D);
			allow3D.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					app.getSettings().getEuclidian(-1).setEnabled(allow3D.getValue());
					guiManager.updateToolbarActions();
				}
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
			if (app.getArticleElement().hasDataParamEnableGraphing()) {
				boolean supportsCAS = app.getSettings().getCasSettings().isEnabled();
				boolean supports3D = app.getSettings().getEuclidian(-1).isEnabled();
				Log.debug(supportsCAS + "," + supports3D + "," + app.enableGraphing());
				// CAS EXAM: cas && !3d && ev
				if (!supports3D && supportsCAS) {
					// set CAS background view for Exam CAS
					app.getGgbApi().setPerspective("4");
					box.getCaption().setText(loc.getMenu("ExamCAS"));
				}
				// GRAPH EXAM: !cas && !3d && ev
				else if (!supports3D && !supportsCAS) {
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
			} else {
				box.getCaption().setText(loc.getMenu("exam_custom_header"));
			}
		}

		if (runsOnAndroid()) {
			startExamForAndroidWebview(mainWidget);
		} else {
			// start exam button
			startPanel.setVisible(true);
			btnOk.setText(loc.getMenu("exam_start_button"));
			btnOk.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					startExam(box, app);
				}
			});
		}

		mainWidget.add(btnPanel);
		box.setWidget(mainWidget);

		if ((app.getArticleElement().hasDataParamEnableGraphing())) {
			btnOk.addStyleName("ExamTabletStartButton");
		}
		app.invokeLater(new Runnable() {
			@Override
			public void run() {
				box.center();
			}
		});

		// Cancel button
		btnCancel.addStyleName("cancelBtn");
		btnCancel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				cancelExam();
			}
		});
		// Help button
		btnHelp.addStyleName("cancelBtn");
		btnHelp.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				app.getFileManager().open("https://www.geogebra.org/tutorial/exam");
			}
		});
	}

	/**
	 * Cancel button handler
	 */
	protected void cancelExam() {
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

	private void startExam(boolean needsFullscreen) {
		startExam(box, app, needsFullscreen);
	}

	/**
	 * @param box
	 *            dialog
	 * @param app
	 *            application
	 */
	public static void startExam(DialogBoxW box, AppW app) {
		startExam(box, app, true);
	}

	/**
	 * @param box
	 *            wrapped box
	 * @param app
	 *            application
	 * @param needsFullscreen
	 *            whether switch to fullscreen needs to be called
	 */
	public static void startExam(DialogBoxW box, AppW app, boolean needsFullscreen) {
		final GuiManagerInterfaceW guiManager = app.getGuiManager();
		if (needsFullscreen) {
			app.getLAF().toggleFullscreen(true);
		}
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
			if (app.getArticleElement().getDataParamEnableCAS(false)) {
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
		StyleInjector.inject(GuiResources.INSTANCE.examStyleLTR().getText());
		examStyle = true;
	}

	final private boolean runsOnAndroid() {
		return app.getVersion().isAndroidWebview();
	}

	final private void startExamForAndroidWebview(VerticalPanel mainWidget) {
		// bind methods to javascript
		setJavascriptTargetToExamDialog();
		exportGeoGebraAndroidMethods();

		// needs a label for airplane mode / lock task instructions
		instruction = new Label();
		mainWidget.add(instruction);

		// button
		btnOk.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onButtonOk();
			}
		});

		// task locking available?
		lockTaskIsAvailable = ExamEnvironmentW.checkLockTaskAvailable();
		Log.debug("Task locking available: " + lockTaskIsAvailable);

		// start airplane mode / lock task check
		startCheckAirplaneMode();
	}

	/**
	 * Android exam OK button pressed
	 */
	protected void onButtonOk() {
		switch (dialogState) {
		default:
			case WAIT_FOR_TASK_LOCK:
				// airplane mode off: ask again
				if (!isAirplaneModeOn()) {
					setAirplaneModeDialog();
					return;
				}
				// ask Android to lock
				askForTaskLock();
				break;
			case CAN_START_EXAM:
				// airplane mode off: ask again
				if (!isAirplaneModeOn()) {
					setAirplaneModeDialog();
					return;
				}
				// task not locked: ask again
				if (lockTaskIsAvailable && !ExamEnvironmentW.checkTaskLocked()) {
					setLockTaskDialog();
					return;
				}
				// go to full screen also block screen saver
				updateFullscreenStatusOn();
				// set wifi & bluetooth off if needed
				setWifiOffIfNeeded();
				setBluetoothOffIfNeeded();
				// all set: start exam
				ExamEnvironmentW.setJavascriptTargetToNone();
				// dont go to fullscreen if lock task is available
				startExam(!lockTaskIsAvailable);
				break;
		}
	}

	private void startCheckAirplaneMode() {
		if (isAirplaneModeOn()) {
			Log.debug("Airplane mode is on");
			wasAirplaneModeOn = true;
			setLockTaskDialog();
		} else {
			Log.debug("Airplane mode is off");
			wasAirplaneModeOn = false;
			setAirplaneModeDialog();
		}
	}

	private void setAirplaneModeDialog() {
		instruction.setText(loc.getMenu("exam_set_airplane_mode_on"));
		instruction.setVisible(true);

		btnOk.setVisible(false);

		box.center();

		dialogState = DialogState.WAIT_FOR_AIRPLANE_MODE;
	}

	private void setLockTaskDialog() {
		// if task locking is not available, go to start exam dialog
		if (!lockTaskIsAvailable) {
			setStartExamDialog();
			return;
		}
		// we do not want the own pinning dialog anymore
		// only the native pin dialog
		/*instruction.setText(loc.getMenu("exam_accept_pin"));
		instruction.setVisible(true);

		btnOk.setText(loc.getMenu("exam_pin"));
		btnOk.setFocus(false);
		btnOk.setVisible(true);

		box.center();*/
		// airplane mode off: ask again
		if (!isAirplaneModeOn()) {
			setAirplaneModeDialog();
			return;
		}
		// ask Android to lock
		askForTaskLock();
		dialogState = DialogState.WAIT_FOR_TASK_LOCK;
	}

	private void setStartExamDialog() {
		instruction.setVisible(false);
		// show start dialog content
		startPanel.setVisible(true);
		btnOk.setText(loc.getMenu("exam_start_button"));
		btnOk.setFocus(false);
		btnOk.setVisible(true);

		box.center();

		dialogState = DialogState.CAN_START_EXAM;
	}

	private void askForTaskLock() {
		Log.debug("ask for task lock");
		ExamEnvironmentW.startLockTask();

		// set timer to check continuously if task is locked
		if (checkTaskLockTimer != null && checkTaskLockTimer.isRunning()) {
			checkTaskLockTimer.stop();
		}
		checkTaskLockTimer = app.newTimer(new GTimerListener() {
			@Override
			public void onRun() {
				checkTaskLock();
			}
		}, 100);
		checkTaskLockTimer.startRepeat();
	}

	/**
	 * Regular check for airplane mode
	 */
	protected void checkTaskLock() {
		Log.debug("check task lock");
		if (!isAirplaneModeOn()) {
			Log.debug("(check) airplane mode off");
			checkTaskLockTimer.stop();
			setAirplaneModeDialog();
			return;
		}
		if (ExamEnvironmentW.checkTaskLocked()) {
			Log.debug("(check) task is locked");
			checkTaskLockTimer.stop();
			setStartExamDialog();
		} else {
			Log.debug("(check) task is NOT locked");
		}

	}

	private static native boolean updateFullscreenStatusOn() /*-{
		return $wnd.GeoGebraExamAndroidJsBinder.updateFullscreenStatusOn();
	}-*/;

	private static native void stopLockTask() /*-{
		$wnd.GeoGebraExamAndroidJsBinder.stopLockTask();
	}-*/;

	private static native boolean setJavascriptTargetToExamDialog() /*-{
		return $wnd.GeoGebraExamAndroidJsBinder
				.setJavascriptTargetToExamDialog();
	}-*/;

	private static native boolean isAirplaneModeOn() /*-{
		return $wnd.GeoGebraExamAndroidJsBinder.isAirplaneModeOn();
	}-*/;

	private native void exportGeoGebraAndroidMethods() /*-{
		var that = this;
		$wnd.examDialog_airplaneModeTurnedOn = $entry(function() {
			that.@org.geogebra.web.full.gui.exam.ExamDialog::airplaneModeTurnedOn()();
		});
		$wnd.examDialog_airplaneModeTurnedOff = $entry(function() {
			that.@org.geogebra.web.full.gui.exam.ExamDialog::airplaneModeTurnedOff()();
		});
	}-*/;

	/**
	 * this method is called through js (see exportGeoGebraAndroidMethods())
	 */
	public void airplaneModeTurnedOn() {
		Log.debug("airplane mode turned on");
		if (!wasAirplaneModeOn) {
			setLockTaskDialog();
			wasAirplaneModeOn = true;
		}
	}

	/**
	 * this method is called through js (see exportGeoGebraAndroidMethods())
	 */
	public void airplaneModeTurnedOff() {
		Log.debug("airplane mode turned off");
		if (wasAirplaneModeOn) {
			setAirplaneModeDialog();
			wasAirplaneModeOn = false;
		}
	}

	/**
	 * Exit the app
	 */
	public static void exitApp() {
		if (ExamEnvironmentW.checkLockTaskAvailable()) {
			stopLockTask();
		}
		exitAppJs();
	}

	private static native void setWifiOffIfNeeded() /*-{
		$wnd.GeoGebraExamAndroidJsBinder.setWifiOffIfNeeded();
	}-*/;

	private static native void setBluetoothOffIfNeeded() /*-{
		$wnd.GeoGebraExamAndroidJsBinder.setBluetoothOffIfNeeded();
	}-*/;

	private static native void exitAppJs()/*-{
		$wnd.GeoGebraExamAndroidJsBinder.exitApp();
	}-*/;
}
