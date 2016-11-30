package org.geogebra.web.web.gui.exam;

import java.util.Date;

import org.geogebra.common.gui.Layout;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.GTimer;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.ExamEnvironmentW;
import org.geogebra.web.html5.main.ExamUtil;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.gui.dialog.DialogBoxW;
import org.geogebra.web.web.gui.layout.DockManagerW;
import org.geogebra.web.web.gui.layout.DockPanelW;

import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ExamDialog {
	private AppW app;

	private DialogBoxW box;

	private Localization loc;

	private Label instruction;

	private Button btnOk;

	public ExamDialog(AppW app) {
		this.app = app;
	}

	public void show() {
		loc = app.getLocalization();
		final GuiManagerInterfaceW guiManager = app.getGuiManager();
		box = new DialogBoxW(false, true, null, app.getPanel());
		VerticalPanel mainWidget = new VerticalPanel();
		FlowPanel btnPanel = new FlowPanel();
		FlowPanel cbxPanel = new FlowPanel();

		btnOk = new Button();
		Button btnCancel = new Button();
		Button btnHelp = new Button();
		// mainWidget.add(btnPanel);

		btnPanel.add(btnOk);
		// we don't need cancel and help buttons for tablet exam apps
		if (!(app.getArticleElement().hasDataParamEnableGraphing())) {
			btnPanel.add(btnCancel);
			btnPanel.add(btnHelp);
			box.addStyleName("boxsize");
			btnCancel.setText(loc.getMenu("Cancel"));
			btnHelp.setText(loc.getMenu("Help"));
		} else {
			box.addStyleName("ExamTabletBoxsize");
		}



		// description.addStyleName("padding");

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
			mainWidget.add(description);
			mainWidget.add(cbxPanel);
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
					}
				}
			} else {
				box.getCaption().setText(loc.getMenu("exam_custom_header"));
			}
		}


		if (app.has(Feature.BIND_ANDROID_TO_EXAM_APP) && runsOnAndroid()) {
			startExamForAndroidWebview(mainWidget);
		} else {
			// start exam button
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
		box.center();

		// Cancel button
		btnCancel.addStyleName("cancelBtn");
		btnCancel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				app.getExam().exit();
				app.setExam(null);
				ExamUtil.toggleFullscreen(false);
				app.fireViewsChangedEvent();
				guiManager.updateToolbarActions();
				guiManager.setGeneralToolBarDefinition(ToolBar.getAllToolsNoMacros(true, false, app));
				guiManager.updateToolbar();
				guiManager.resetMenu();
				box.hide();
			}
		});
		// Help button
		btnHelp.addStyleName("cancelBtn");
		btnHelp.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ToolTipManagerW.openWindow("https://www.geogebra.org/tutorial/exam");
			}
		});
	}


	private void startExam(boolean needsFullscreen) {
		startExam(box, app, needsFullscreen);
	}

	public static void startExam(DialogBoxW box, AppW app) {
		startExam(box, app, true);
	}

	public static void startExam(DialogBoxW box, AppW app, boolean needsFullscreen) {
		final GuiManagerInterfaceW guiManager = app.getGuiManager();
		if (needsFullscreen && app.getLAF().supportsFullscreen()) {
			ExamUtil.toggleFullscreen(true);
		}
		StyleInjector.inject(GuiResources.INSTANCE.examStyleLTR().getText());
		Date date = new Date();
		guiManager.updateToolbarActions();
		app.getLAF().removeWindowClosingHandler();
		app.fileNew();
		app.updateRounding();
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
		guiManager.setGeneralToolBarDefinition(ToolBar.getAllToolsNoMacros(true, true, app));
		app.getKernel().getAlgebraProcessor().reinitCommands();
		app.getExam().setStart(date.getTime());
		app.fireViewsChangedEvent();
		guiManager.updateToolbar();
		guiManager.updateToolbarActions();
		Layout.initializeDefaultPerspectives(app, 0.2);
		guiManager.updateMenubar();
		guiManager.resetMenu();
		DockPanelW dp = ((DockManagerW) guiManager.getLayout().getDockManager()).getPanelForKeyboard();
		if (dp != null && dp.getKeyboardListener().needsAutofocus()) {

			app.showKeyboard(dp.getKeyboardListener(), true);
		}
		if (box != null) {
			box.hide();
		}
	}

	////////////////////////////////////
	// ANDROID TABLETS
	////////////////////////////////////

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
		lockTaskIsAvailable = checkLockTaskAvailable();
		Log.debug("Task locking available: " + lockTaskIsAvailable);

		// start airplane mode / lock task check
		startCheckAirplaneMode();
	}

	private boolean lockTaskIsAvailable;

	private enum DialogState {WAIT_FOR_AIRPLANE_MODE, WAIT_FOR_TASK_LOCK, CAN_START_EXAM}

	private DialogState dialogState;

	private void onButtonOk() {
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
				if (lockTaskIsAvailable && !checkTaskLocked()) {
					setLockTaskDialog();
					return;
				}
				// go to full screen
				updateFullscreenStatusOn();
				// set wifi off if needed
				setWifiOffIfNeeded();
				// all set: start exam
				ExamEnvironmentW.setJavascriptTargetToNone();
				// dont go to fullscreen if lock task is available
				startExam(!lockTaskIsAvailable);
				break;
		}
	}

	private boolean wasAirplaneModeOn;

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

		instruction.setText(loc.getMenu("exam_accept_pin"));
		instruction.setVisible(true);

		btnOk.setText(loc.getMenu("exam_pin"));
		btnOk.setFocus(false);
		btnOk.setVisible(true);

		box.center();

		dialogState = DialogState.WAIT_FOR_TASK_LOCK;
	}

	private void setStartExamDialog() {
		instruction.setVisible(false);

		btnOk.setText(loc.getMenu("exam_start_button"));
		btnOk.setFocus(false);
		btnOk.setVisible(true);

		box.center();

		dialogState = DialogState.CAN_START_EXAM;
	}

	private GTimer checkTaskLockTimer = null;

	private void askForTaskLock() {
		Log.debug("ask for task lock");
		startLockTask();

		// set timer to check continuously if task is locked
		if (checkTaskLockTimer != null && checkTaskLockTimer.isRunning()) {
			checkTaskLockTimer.stop();
		}
		checkTaskLockTimer = app.newTimer(new GTimer.GTimerListener() {
			@Override
			public void onRun() {
				Log.debug("check task lock");
				if (!isAirplaneModeOn()) {
					Log.debug("(check) airplane mode off");
					checkTaskLockTimer.stop();
					setAirplaneModeDialog();
					return;
				}
				if (checkTaskLocked()) {
					Log.debug("(check) task is locked");
					checkTaskLockTimer.stop();
					setStartExamDialog();
				} else {
					Log.debug("(check) task is NOT locked");
				}
			}
		}, 100);
		checkTaskLockTimer.startRepeat();
	}

	private static native boolean updateFullscreenStatusOn() /*-{
		return $wnd.GeoGebraExamAndroidJsBinder.updateFullscreenStatusOn();
	}-*/;

	private static native boolean checkLockTaskAvailable() /*-{
		return $wnd.GeoGebraExamAndroidJsBinder.checkLockTaskAvailable();
	}-*/;

	private static native boolean checkTaskLocked() /*-{
		return $wnd.GeoGebraExamAndroidJsBinder.checkTaskLocked();
	}-*/;

	private static native void startLockTask() /*-{
		$wnd.GeoGebraExamAndroidJsBinder.startLockTask();
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
			that.@org.geogebra.web.web.gui.exam.ExamDialog::airplaneModeTurnedOn()();
		});
		$wnd.examDialog_airplaneModeTurnedOff = $entry(function() {
			that.@org.geogebra.web.web.gui.exam.ExamDialog::airplaneModeTurnedOff()();
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

	public static void exitApp() {
		if (checkLockTaskAvailable()){
			stopLockTask();
		}
		exitAppJs();
	}


	private static native void setWifiOffIfNeeded() /*-{
		$wnd.GeoGebraExamAndroidJsBinder.setWifiOffIfNeeded();
	}-*/;

	public static native void exitAppJs()/*-{
		$wnd.GeoGebraExamAndroidJsBinder.exitApp();
	}-*/;

}
