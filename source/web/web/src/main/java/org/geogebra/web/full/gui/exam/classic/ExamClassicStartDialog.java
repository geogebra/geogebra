package org.geogebra.web.full.gui.exam.classic;

import org.geogebra.common.exam.ExamController;
import org.geogebra.common.exam.ExamOptions;
import org.geogebra.common.exam.ExamType;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.main.Localization;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.web.full.gui.components.ComponentCheckbox;
import org.geogebra.web.full.gui.layout.DockManagerW;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.layout.LayoutW;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.StyleInjector;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

import com.google.gwt.core.client.GWT;

import elemental2.dom.DomGlobal;
import elemental2.dom.KeyboardEvent;

/**
 * Exam start dialog
 */
public class ExamClassicStartDialog extends ComponentDialog {
	private static boolean examStyle;
	protected AppW app;
	private static final ExamController examController = GlobalScope.examController;

	/**
	 * @param app
	 *            application
	 */
	public ExamClassicStartDialog(AppW app, DialogData data) {
		super(app, data, false, true);
		this.app = app;
		examController.prepareExam();
		addStyleName("classicExamStartDialog");
		buildGUI();
		setOnPositiveAction(() -> startExam(app));
		setOnNegativeAction(this::cancelExam);
	}

	private void buildGUI() {
		ensureExamStyle();
		Localization loc = app.getLocalization();
		final GuiManagerInterfaceW guiManager = app.getGuiManager();

		// start dialog content with checkboxes
		FlowPanel startPanel = new FlowPanel();
		Label description = new Label(loc.getMenu("exam_custom_description"));
		description.addStyleName("description");
		startPanel.add(description);

		if (!app.getSettings().getCasSettings().isEnabledSet()) {
			ComponentCheckbox cas = new ComponentCheckbox(app.getLocalization(), true,
					"Perspective.CAS", selected -> {
				app.getSettings().getCasSettings().setEnabled(selected);
				guiManager.updateToolbarActions();
			});
			app.getSettings().getCasSettings().setEnabled(true);
			startPanel.add(cas);
		}

		if (!app.getSettings().getEuclidian(-1).isEnabledSet()) {
			final ComponentCheckbox allow3D = new ComponentCheckbox(app.getLocalization(), true,
					"Perspective.3DGraphics", selected -> {
				app.getSettings().getEuclidian(-1).setEnabled(selected);
				guiManager.updateToolbarActions();
			});
			app.getSettings().getEuclidian(-1).setEnabled(true);
			startPanel.add(allow3D);
		}
		guiManager.updateToolbarActions();

		setDialogContent(startPanel);
	}

	/**
	 * Cancel button handler
	 */
	private void cancelExam() {
		examController.cancelExam();
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
		((LayoutW) app.getGuiManager().getLayout()).resetPerspectives(app);

		app.getKernel().getAlgebraProcessor().reinitCommands();
		((AppWFull) app).startExam(ExamType.GENERIC,
				new ExamOptions(app.getSettings().getCasSettings().isEnabled()));

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
			if ("Escape".equals(e.code) && !examController.isIdle()) {
				e.preventDefault();
			}
		});
		DomGlobal.document.body.addEventListener("keydown", evt -> {
			KeyboardEvent e = (KeyboardEvent) evt;
			if (("Tab".equals(e.code) || "Escape".equals(e.code))
					&& !examController.isIdle()) {
				e.preventDefault();
			}
		});
	}

	private static void ensureExamStyle() {
		if (examStyle) {
			return;
		}
		new StyleInjector(GWT.getModuleBaseURL()).inject("css", "exam");
		examStyle = true;
	}

	@Override
	protected void onEscape() {
		if (!app.isLockedExam()) {
			examController.cancelExam();
			hide();
		}
	}
}
