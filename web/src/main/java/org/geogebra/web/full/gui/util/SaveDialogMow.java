package org.geogebra.web.full.gui.util;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.MaterialVisibility;
import org.geogebra.common.main.SaveController.SaveListener;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.FormLabel;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.DialogBoxW;
import org.geogebra.web.shared.DialogUtil;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author csilla
 * 
 *         simplified save dialog with material design
 *
 */
public class SaveDialogMow extends DialogBoxW
		implements SetLabels, FastClickHandler, SaveListener, SaveDialogI {
	private FlowPanel dialogContent;
	private FlowPanel inputPanel;
	private FormLabel titleLbl;
	private InputPanelW titleField;
	private FlowPanel buttonPanel;
	private StandardButton cancelBtn;
	private StandardButton saveBtn;

	/**
	 * @param app see {@link AppW}
	 */
	public SaveDialogMow(AppW app) {
		super(app.getPanel(), app);
		this.addStyleName("saveDialogMow");
		initGUI();
		initActions();
		DialogUtil.hideOnLogout(app, this);
	}

	private void initGUI() {
		dialogContent = new FlowPanel();
		inputPanel = new FlowPanel();
		inputPanel.setStyleName("mowMediaDialogContent");
		inputPanel.addStyleName("emptyState");
		titleField = new InputPanelW("", app, 1, 25, false);
		titleLbl = new FormLabel().setFor(titleField.getTextComponent());
		titleLbl.addStyleName("inputLabel");
		titleField.getTextComponent().getTextBox().getElement().setAttribute(
				"placeholder", app.getLocalization().getMenu("Untitled"));
		titleField.addStyleName("inputText");
		inputPanel.add(titleLbl);
		inputPanel.add(titleField);
		buttonPanel = new FlowPanel();
		buttonPanel.setStyleName("DialogButtonPanel");
		cancelBtn = new StandardButton("", app);
		cancelBtn.addFastClickHandler(this);
		saveBtn = new StandardButton("", app);
		saveBtn.addFastClickHandler(this);
		buttonPanel.add(cancelBtn);
		buttonPanel.add(saveBtn);
		dialogContent.add(inputPanel);
		dialogContent.add(buttonPanel);
		setLabels();
		this.add(dialogContent);
	}

	private void initActions() {
		// set focus to input field!
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				getInputField().getTextComponent().setFocus(true);
			}
		});
		addFocusBlurHandlers();
		addHoverHandlers();
	}

	/**
	 * Add mouse over/ out handlers
	 */
	private void addHoverHandlers() {
		titleField.getTextComponent().getTextBox()
				.addMouseOverHandler(new MouseOverHandler() {

					@Override
					public void onMouseOver(MouseOverEvent event) {
						getInputPanel().addStyleName("hoverState");
					}
				});
		titleField.getTextComponent().getTextBox()
				.addMouseOutHandler(new MouseOutHandler() {

					@Override
					public void onMouseOut(MouseOutEvent event) {
						getInputPanel().removeStyleName("hoverState");
					}
				});
	}

	private void addFocusBlurHandlers() {
		titleField.getTextComponent().getTextBox()
				.addFocusHandler(new FocusHandler() {

					@Override
					public void onFocus(FocusEvent event) {
						setFocusState();
					}
				});
		titleField.getTextComponent().getTextBox()
				.addBlurHandler(new BlurHandler() {

					@Override
					public void onBlur(BlurEvent event) {
						resetInputField();
					}
				});
	}

	/**
	 * sets the style of InputPanel to focus state
	 */
	protected void setFocusState() {
		getInputPanel().setStyleName("mowMediaDialogContent");
		getInputPanel().addStyleName("focusState");
	}

	/**
	 * Resets input style on blur
	 */
	protected void resetInputField() {
		getInputPanel().removeStyleName("focusState");
		getInputPanel().addStyleName("emptyState");
	}

	/**
	 * Input changed (paste or key event happened)
	 */
	protected void onInput() {
		getInputPanel().addStyleName("focusState");
		getInputPanel().removeStyleName("emptyState");
	}

	/**
	 * @return panel holding input with label and error label
	 */
	public FlowPanel getInputPanel() {
		return inputPanel;
	}

	/**
	 * @return input text field
	 */
	public InputPanelW getInputField() {
		return titleField;
	}

	@Override
	public void onClick(Widget source) {
		if (source == cancelBtn) {
			hide();
			app.getSaveController().cancel();
		} else if (source == saveBtn) {
			MaterialVisibility visibility = ((AppW) app)
					.getActiveMaterial() != null ? MaterialVisibility.value(
									((AppW) app).getActiveMaterial()
											.getVisibility())
							: MaterialVisibility.Private;
			app.getSaveController().saveAs(getInputField().getText(),
					visibility, this);
		}
	}

	@Override
	public void setLabels() {
		defaultSaveCaption();
		titleLbl.setText(app.getLocalization().getMenu("Title"));
		saveBtn.setLabel(app.getLocalization().getMenu("Save"));
		cancelBtn.setLabel(app.getLocalization().getMenu("Cancel"));
		titleField.getTextComponent().getTextBox().getElement().setAttribute(
				"placeholder", app.getLocalization().getMenu("Untitled"));
	}

	private void defaultSaveCaption() {
		getCaption().setText(app.getLocalization().getMenu("Save"));
	}

	@Override
	public void show() {
		defaultSaveCaption();
		super.show();
		center();
		this.setTitle();
		Material mat = ((AppW) app).getActiveMaterial();
		if (mat != null) {
			getInputField().getTextComponent().setText(mat.getTitle());
		}
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				getInputField().getTextComponent().setFocus(true);
			}
		});
	}

	/**
	 * Sets initial title for the material to save.
	 */
	@Override
	public void setTitle() {
		app.getSaveController()
				.updateSaveTitle(getInputField().getTextComponent(), "");
	}

	/**
	 * Sets material type to be saved.
	 * 
	 * @param saveType
	 *            for the dialog.
	 */
	@Override
	public void setSaveType(MaterialType saveType) {
		app.getSaveController().setSaveType(saveType);
	}

	@Override
	public SaveDialogI setDefaultVisibility(MaterialVisibility visibility) {
		return this;
	}

	/**
	 * shows the {@link SaveDialogW} if there are unsaved changes before editing
	 * another file or creating a new one
	 * 
	 * Never shown in embedded LAF (Mix, SMART)
	 * 
	 * @param runnable
	 *            runs either after saved successfully or immediately if dialog
	 *            not needed {@link Runnable}
	 */
	@Override
	public void showIfNeeded(AsyncOperation<Boolean> runnable) {
		showIfNeeded(runnable, !app.isSaved(), null);
		getCaption().setText(app.getLocalization().getMenu("DoYouWantToSaveYourChanges"));
	}

	/**
	 * @param runnable
	 *            callback
	 * @param needed
	 *            whether it's needed to save (otherwise just run callback)
	 * @param anchor
	 *            relative element
	 */
	@Override
	public void showIfNeeded(AsyncOperation<Boolean> runnable, boolean needed,
			Widget anchor) {
		if (needed && !((AppW) app).getLAF().isEmbedded()) {
			app.getSaveController().setRunAfterSave(runnable);
			if (anchor == null) {
				center();
			} else {
				showRelativeTo(anchor);
			}
		} else {
			app.getSaveController().setRunAfterSave(null);
			runnable.callback(true);
		}
	}
}
