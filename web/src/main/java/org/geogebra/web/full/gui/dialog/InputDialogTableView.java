package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.DialogBoxW;

import com.google.gwt.user.client.ui.FlowPanel;

/**
 * @author csilla
 *
 */
public class InputDialogTableView extends DialogBoxW implements SetLabels {
	private AppW appW;
	private FlowPanel contentPanel;
	private FlowPanel buttonPanel;
	private StandardButton cancelBtn;
	private StandardButton okBtn;

	/**
	 * @param app
	 *            see {@link AppW}
	 */
	public InputDialogTableView(AppW app) {
		super(app.getPanel(), app);
		this.appW = app;
		buildGui();
	}

	private void buildGui() {
		addStyleName("tableOfValuesDialog");
		contentPanel = new FlowPanel();
		buttonPanel = new FlowPanel();
		buttonPanel.setStyleName("DialogButtonPanel");
		cancelBtn = createTxtButton(buttonPanel, "cancelBtn", true);
		okBtn = createTxtButton(buttonPanel, "okBtn", false);
		contentPanel.add(buttonPanel);
		this.add(contentPanel);
		setLabels();
	}

	private StandardButton createTxtButton(FlowPanel root, String styleName,
			boolean isEnabled) {
		StandardButton btn = new StandardButton("", appW);
		btn.addStyleName(styleName);
		btn.setEnabled(isEnabled);
		root.add(btn);
		return btn;
	}

	public void setLabels() {
		getCaption().setText(appW.getLocalization().getMenu("TableOfValues"));
		cancelBtn.setText(appW.getLocalization().getMenu("Cancel"));
		okBtn.setText(appW.getLocalization().getMenu("OK"));
	}

	@Override
	public void show() {
		super.show();
		super.center();
	}
}
