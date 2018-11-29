package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.gui.openfileview.MaterialCardI;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.gui.util.FormLabel;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.himamis.retex.editor.share.util.GWTKeycodes;

/**
 * Material rename dialog
 * 
 * @author judit
 *
 */
public class MaterialRenameDialog extends OptionDialog {

	private FlowPanel mainPanel;
	private FlowPanel inputPanel;
	private InputPanelW inputField;
	private FormLabel inputLabel;
	private boolean inputChanged;

	/**
	 * application
	 */
	protected AppW appW;
	private MaterialCardI card;

	/**
	 * @param root
	 *            popup root
	 * @param app
	 *            app
	 * @param card
	 *            card of file being renamed
	 */
	public MaterialRenameDialog(Panel root, AppW app, MaterialCardI card) {
		super(root, app);
		appW = app;
		this.card = card;
		initGui();
	}

	private void initGui() {
		mainPanel = new FlowPanel();
		inputPanel = new FlowPanel();
		inputPanel.setStyleName("mowRenameDialogContent");
		inputPanel.addStyleName("emptyState");
		inputField = new InputPanelW("", appW, 1, 25, false);
		inputLabel = new FormLabel().setFor(inputField.getTextComponent());
		inputLabel.addStyleName("inputLabel");
		inputPanel.add(inputLabel);
		inputPanel.add(inputField);
		// add panels
		add(mainPanel);
		mainPanel.add(inputPanel);
		mainPanel.add(getButtonPanel());
		// style
		addStyleName("GeoGebraPopup");
		setLabels();
		inputField.getTextComponent().setText(card.getMaterialTitle());
		initInputFieldActions();
		addStyleName("mebis");
	}

	/**
	 * @return input text field of dialog
	 */
	public InputPanelW getInputField() {
		return inputField;
	}

	private void initInputFieldActions() {
		// on input change
		inputField.getTextComponent().addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				validate(event.getNativeKeyCode() == GWTKeycodes.KEY_ENTER);
			}
		});
		// set focus to input field!
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				getInputField().getTextComponent().setFocus(true);
			}
		});
		addHoverHandlers();
		addFocusHandlers();
	}

	/**
	 * Add focus / blur handlers
	 */
	private void addFocusHandlers() {
		inputField.getTextComponent().getTextBox()
				.addFocusHandler(new FocusHandler() {

					@Override
					public void onFocus(FocusEvent event) {
						getInputField().setStyleName("mowRenameDialogContent");
						getInputField().addStyleName("focusState");
					}
				});
		inputField.getTextComponent().getTextBox()
				.addBlurHandler(new BlurHandler() {

					@Override
					public void onBlur(BlurEvent event) {
						getInputField().removeStyleName("focusState");
						getInputField().addStyleName("emptyState");
					}
				});
	}

	/**
	 * Add mouse over/ out handlers
	 */
	private void addHoverHandlers() {
		inputField.getTextComponent().getTextBox()
				.addMouseOverHandler(new MouseOverHandler() {

					@Override
					public void onMouseOver(MouseOverEvent event) {
						getInputField().addStyleName("hoverState");
					}
				});
		inputField.getTextComponent().getTextBox()
				.addMouseOutHandler(new MouseOutHandler() {

					@Override
					public void onMouseOut(MouseOutEvent event) {
						getInputField().removeStyleName("hoverState");
					}
				});
	}

	/**
	 * Enable or disable
	 * 
	 * @param enter
	 *            enter pressed
	 */
	protected void validate(boolean enter) {
		inputChanged = inputChanged
				|| !inputField.getText().trim().equals(card.getMaterialTitle());
		if (StringUtil.emptyTrim(inputField.getText())
				|| inputField.getText().length() > Material.MAX_TITLE_LENGTH
				|| !inputChanged) {
			setPrimaryButtonEnabled(false);
		} else {
			setPrimaryButtonEnabled(true);
			if (enter) {
				processInput();
			}
		}
	}

	@Override
	protected void processInput() {
		card.rename(inputField.getText().trim());
		hide();
	}

	/**
	 * set button labels
	 */
	public void setLabels() {
		getCaption().setText(appW.getLocalization().getMenu("rename.resource"));
		updateButtonLabels("Rename");
	}
}
