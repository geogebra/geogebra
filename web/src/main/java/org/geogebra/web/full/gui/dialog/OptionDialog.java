package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.shared.DialogBoxW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public abstract class OptionDialog extends DialogBoxW
		implements FastClickHandler {
	private StandardButton insertBtn;
	private StandardButton cancelBtn;
	private FlowPanel buttonPanel;

	/**
	 * @param root
	 *            panel for positioning
	 * @param app
	 *            application
	 */
	public OptionDialog(Panel root, App app) {
		super(root, app);
		initButtonPanel();
		addStyleName("optionDialog");
	}

	private void initButtonPanel() {
		insertBtn = new StandardButton("", app);
		insertBtn.addStyleName("insertBtn");
		insertBtn.setEnabled(false);
		cancelBtn = new StandardButton("", app);
		cancelBtn.addStyleName("cancelBtn");
		buttonPanel = new FlowPanel();
		buttonPanel.setStyleName("DialogButtonPanel");
		buttonPanel.add(cancelBtn);
		buttonPanel.add(insertBtn);
		insertBtn.addFastClickHandler(this);
		cancelBtn.addFastClickHandler(this);
	}

	@Override
	public void onClick(Widget source) {
		if (source == cancelBtn) {
			hide();
		} else if (source == insertBtn) {
			processInput();
		}
	}

	protected abstract void processInput();

	protected void updateButtonLabels(String key) {
		insertBtn.setText(app.getLocalization().getMenu(key)); // insert
		cancelBtn.setText(app.getLocalization().getMenu("Cancel")); // cancel
	}

	protected void enablePrimaryButton(boolean b) {
		insertBtn.setEnabled(b);
	}

	protected Panel getButtonPanel() {
		return buttonPanel;
	}

}
