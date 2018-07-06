package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.web.full.gui.openfileview.MaterialCardI;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.FormLabel;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.DialogBoxW;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.himamis.retex.editor.share.util.GWTKeycodes;

/**
 * Material rename dialog
 * 
 * @author judit
 *
 */
public class MaterialRenameDialog extends DialogBoxW {

	private FlowPanel mainPanel;
	private FlowPanel inputPanel;
	private InputPanelW inputField;
	private FormLabel inputLabel;
	private StandardButton okBtn;
	private StandardButton cancelBtn;
	private FlowPanel buttonPanel;
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
		// panel for buttons
		okBtn = new StandardButton("", appW);
		okBtn.addStyleName("okBtn");
		okBtn.setEnabled(false);
		cancelBtn = new StandardButton("", app);
		cancelBtn.addStyleName("cancelBtn");
		buttonPanel = new FlowPanel();
		buttonPanel.setStyleName("DialogButtonPanel");
		buttonPanel.add(cancelBtn);
		buttonPanel.add(okBtn);
		// add panels
		add(mainPanel);
		mainPanel.add(inputPanel);
		mainPanel.add(buttonPanel);
		// style
		addStyleName("GeoGebraPopup");
		addStyleName("renameDialog");
		setGlassEnabled(true);
		setLabels();
		inputField.getTextComponent().addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				validate(event.getNativeKeyCode() == GWTKeycodes.KEY_ENTER);
			}

		});
		okBtn.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				rename();
			}
		});

		cancelBtn.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				hide();
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
		if (inputField.getText().length() < Material.MIN_TITLE_LENGTH) {
			// TODO show hint
			okBtn.setEnabled(false);
		} else {
			okBtn.setEnabled(true);
			if (enter) {
				rename();
			}
		}
	}

	protected void rename() {
		card.rename(inputField.getText());
		hide();
	}

	/**
	 * set button labels
	 */
	public void setLabels() {
		getCaption().setText(appW.getLocalization().getMenu("Rename"));
		okBtn.setText(appW.getLocalization().getMenu("OK")); // OK
		cancelBtn.setText(appW.getLocalization().getMenu("Cancel")); // cancel
	}

}
