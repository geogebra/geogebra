package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.gui.openfileview.MaterialCardI;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.gui.util.FormLabel;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
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
		inputField.getTextComponent().addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				validate(event.getNativeKeyCode() == GWTKeycodes.KEY_ENTER);
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
				|| inputField.getText().trim().equals(card.getMaterialTitle());
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
		getCaption().setText(appW.getLocalization().getMenu("Rename"));
		updateButtonLabels("OK");
	}

}
