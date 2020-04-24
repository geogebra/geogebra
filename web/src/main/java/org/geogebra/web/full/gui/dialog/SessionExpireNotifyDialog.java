package org.geogebra.web.full.gui.dialog;

import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class SessionExpireNotifyDialog extends GPopupPanel implements FastClickHandler {

	private StandardButton cancelBtn;
	private StandardButton saveBtn;

	public SessionExpireNotifyDialog(AppW app) {
		super(app.getPanel(), app);
		setGlassEnabled(true);
		this.setStyleName("sessionExpireNotifyDialog");
		buildGUI();
	}

	private void buildGUI() {
		FlowPanel dialoContent = new FlowPanel();

		Label sessionExpireNotifyTxt = new Label("sessionExpireNotify");
		sessionExpireNotifyTxt.addStyleName("sessionExpireTxt");
		dialoContent.add(sessionExpireNotifyTxt);

		addButtonPanel(dialoContent);

		this.add(dialoContent);
	}

	private void addButtonPanel(FlowPanel dialogContent) {
		FlowPanel buttonPanel = new FlowPanel();
		buttonPanel.addStyleName("DialogButtonPanel");

		cancelBtn = createButton("Cancel", "cancelBtn", buttonPanel);
		saveBtn = createButton("Save", "saveBtn", buttonPanel);

		dialogContent.add(buttonPanel);
	}

	private StandardButton createButton(String transKey, String styleName, FlowPanel buttonPanel) {
		StandardButton button = new StandardButton(transKey, app);
		button.setStyleName(styleName);
		button.addFastClickHandler(this);
		buttonPanel.add(button);
		return button;
	}

	@Override
	public void show() {
		super.show();
		super.center();
	}

	@Override
	public void onClick(Widget source) {
		if (source.equals(saveBtn)) {
			hide();
		} else if (source.equals(cancelBtn)) {
			hide();
		}
	}
}
