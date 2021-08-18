package org.geogebra.web.full.gui.exam.classic;

import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.gui.layout.DockManagerW;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.layout.LayoutW;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.StyleInjector;
import org.geogebra.web.shared.components.ComponentDialog;
import org.geogebra.web.shared.components.DialogData;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import elemental2.dom.DomGlobal;
import elemental2.dom.KeyboardEvent;

/**
 * Exam start dialog
 */
public class ExamClassicStartDialog extends ComponentDialog implements ClickHandler {
	private static boolean examStyle;
	/** Application */
	protected AppW app;
	/** Wrapped box */
	private CheckBox cas;

	/**
	 * @param app
	 *            application
	 */
	public ExamClassicStartDialog(AppW app, DialogData data) {
		super(app, data, false, true);
		this.app = app;
		buildGUI();
		setOnPositiveAction(() -> startExam(app));
		setOnNegativeAction(() -> {
			if (!app.getAppletParameters().getParamLockExam()) {
				cancelExam();
			}
		});
	}

	private void buildGUI() {
		ensureExamStyle();
		Localization loc = app.getLocalization();
		final GuiManagerInterfaceW guiManager = app.getGuiManager();

		VerticalPanel mainWidget = new VerticalPanel();
		FlowPanel cbxPanel = new FlowPanel();
		// start dialog content with checkboxes
		FlowPanel startPanel = new FlowPanel();

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
			mainWidget.add(startPanel);
			cbxPanel.addStyleName("ExamCheckboxPanel");
		}

		setDialogContent(mainWidget);
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
	}

	/**
	 * @param app
	 *            application
	 */
	public static void startExam(AppW app) {
		final GuiManagerInterfaceW guiManager = app.getGuiManager();
		app.getLAF().toggleFullscreen(true);
		ensureExamStyle();
		blockEscTab(app);

		guiManager.updateToolbarActions();
		app.getLAF().removeWindowClosingHandler();
		app.fileNew();
		app.updateRounding();
		// do this *before* perspective so that we have CAS toolbar for CAS
		guiManager.setGeneralToolBarDefinition(
				ToolBar.getAllToolsNoMacros(true, true, app));
		LayoutW.resetPerspectives(app);

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
	}

	////////////////////////////////////
	// ANDROID TABLETS
	////////////////////////////////////

	/**
	 * In electron this is done by kiosk mode, but on Chromebook it still
	 * matters.
	 */
	public static void blockEscTab(AppW app) {
		DomGlobal.document.body.addEventListener("keyup", evt -> {
			KeyboardEvent e = (KeyboardEvent) evt;
			if ("Escape".equals(e.code) && app.isExam()) {
				e.preventDefault();
			}
		});
		DomGlobal.document.body.addEventListener("keydown", evt -> {
			KeyboardEvent e = (KeyboardEvent) evt;
			if (("Tab".equals(e.code) || "Escape".equals(e.code)) && app.isExam()) {
				e.preventDefault();
			}
		});
	}

	private static void ensureExamStyle() {
		if (examStyle) {
			return;
		}
		StyleInjector.inject("css", "exam");
		examStyle = true;
	}

	@Override
	public void onClick(ClickEvent event) {
		Object source = event.getSource(); 
		if (source == cas) {
			onCasChecked();
		}
	}

	private void onCasChecked() {
		app.getExam().setCasEnabled(cas.getValue(), app.getSettings().getCasSettings());
		app.getGuiManager().updateToolbarActions();
	}

	@Override
	protected void onEscape() {
		if (!app.getAppletParameters().getParamLockExam()) {
			hide();
		}
	}
}