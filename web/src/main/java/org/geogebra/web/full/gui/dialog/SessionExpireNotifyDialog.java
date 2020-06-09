package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.move.ggtapi.models.AuthenticationModel;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.GTimer;
import org.geogebra.common.util.GTimerListener;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class SessionExpireNotifyDialog extends GPopupPanel implements FastClickHandler,
		ResizeHandler, GTimerListener {

	private StandardButton cancelBtn;
	private StandardButton saveBtn;

	/**
	 * constructor
	 * @param app see {@link AppW}
	 */
	public SessionExpireNotifyDialog(AppW app) {
		super(app.getPanel(), app);
		setGlassEnabled(true);
		this.setStyleName("sessionExpireNotifyDialog");
		buildGUI();
		Window.addResizeHandler(this);
	}

	private void buildGUI() {
		FlowPanel dialoContent = new FlowPanel();

		Label sessionExpireNotifyTxt = new Label();
		sessionExpireNotifyTxt.setText(app.getLocalization().getMenu("sessionExpireNotify"));
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
	public void onResize(ResizeEvent resizeEvent) {
		if (isShowing()) {
			super.center();
		}
	}

	@Override
	public void show() {
		super.show();
		super.center();
		startLogOutTimer();
	}

	@Override
	public void onClick(Widget source) {
		if (source.equals(saveBtn)) {
			hide();
			((DialogManagerW) app.getDialogManager()).showSaveDialog();
		} else if (source.equals(cancelBtn)) {
			hide();
		}
	}

	private void startLogOutTimer() {
		GTimer logOutTimer = app.newTimer(this, AuthenticationModel.LOG_OUT_TIME);
		app.getLoginOperation().getModel().setLogOutTimer(logOutTimer);
		logOutTimer.start();
	}

	@Override
	public void onRun() {
		// send logout event, mow-front takes care of logout and UI update
		app.dispatchEvent(new Event(EventType.SESSION_EXPIRED, null, null));
	}
}
