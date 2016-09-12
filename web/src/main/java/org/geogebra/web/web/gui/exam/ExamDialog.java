package org.geogebra.web.web.gui.exam;

import java.util.Date;

import org.geogebra.common.gui.Layout;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.gui.GuiManagerInterfaceW;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.main.AppW;
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

	public ExamDialog(AppW app) {
		this.app = app;
	}

	public void show() {
		Localization loc = app.getLocalization();
		final GuiManagerInterfaceW guiManager = app.getGuiManager();
		final DialogBoxW box = new DialogBoxW(false, true, null,
				app.getPanel());
		VerticalPanel mainWidget = new VerticalPanel();
		FlowPanel btnPanel = new FlowPanel();
		FlowPanel cbxPanel = new FlowPanel();

		Button btnOk = new Button();
		Button btnCancel = new Button();
		Button btnHelp = new Button();
		// mainWidget.add(btnPanel);

		btnPanel.add(btnOk);
		btnPanel.add(btnCancel);
		btnPanel.add(btnHelp);

		btnOk.setText(loc.getMenu("exam_start_button"));
		btnCancel.setText(loc.getMenu("Cancel"));
		btnHelp.setText(loc.getMenu("Help"));

		// description.addStyleName("padding");
		box.addStyleName("boxsize");
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
					app.getSettings().getCasSettings()
							.setEnabled(cas.getValue());
					guiManager.updateToolbarActions();
				}
			});
		}
		if (!app.getSettings().getEuclidian(-1).isEnabledSet()) {
			checkboxes++;
			final CheckBox allow3D = new CheckBox(
					loc.getMenu("Perspective.3DGraphics"));
			allow3D.addStyleName("examCheckbox");
			allow3D.setValue(true);

			app.getSettings().getEuclidian(-1).setEnabled(true);

			cbxPanel.add(allow3D);
			allow3D.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					app.getSettings().getEuclidian(-1)
							.setEnabled(allow3D.getValue());
					guiManager.updateToolbarActions();

				}
			});
		}
		guiManager.updateToolbarActions();
		if (checkboxes > 0) {
			Label description = new Label(
					loc.getMenu("exam_custom_description"));
			mainWidget.add(description);
			mainWidget.add(cbxPanel);
			cbxPanel.addStyleName("ExamCheckboxPanel");
			btnPanel.addStyleName("DialogButtonPanel");
		}

		mainWidget.add(btnPanel);
		box.setWidget(mainWidget);
		box.getCaption().setText(loc.getMenu("exam_custom_header"));
		box.center();

		// start exam button
		btnOk.addStyleName("examStartButton");
		btnOk.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (app.getLAF().supportsFullscreen()) {
					ExamUtil.toggleFullscreen(true);
				}
				StyleInjector
						.inject(GuiResources.INSTANCE.examStyleLTR().getText());
				Date date = new Date();
				guiManager.updateToolbarActions();
				app.getLAF().removeWindowClosingHandler();
				app.fileNew();
				app.updateRounding();
				guiManager.setGeneralToolBarDefinition(
						ToolBar.getAllToolsNoMacros(true, true));
				app.getKernel().getAlgebraProcessor().reinitCommands();
				app.getExam().setStart(date.getTime());
				app.fireViewsChangedEvent();
				guiManager.updateToolbar();
				guiManager.updateToolbarActions();
				Layout.initializeDefaultPerspectives(app, 0.2);
				guiManager.updateMenubar();
				guiManager.resetMenu();
				DockPanelW dp = ((DockManagerW) guiManager.getLayout()
						.getDockManager()).getPanelForKeyboard();
				if (dp != null && dp.getKeyboardListener().needsAutofocus()) { // dp.getKeyboardListener().setFocus(true);

					app.showKeyboard(dp.getKeyboardListener(), true);
				}
				box.hide();

			}
		});
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
				guiManager.setGeneralToolBarDefinition(
						ToolBar.getAllToolsNoMacros(true, false));
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
				ToolTipManagerW
						.openWindow("https://www.geogebra.org/tutorial/exam");
			}
		});
	}
}
