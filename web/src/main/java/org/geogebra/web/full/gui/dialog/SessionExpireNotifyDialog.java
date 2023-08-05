package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.move.ggtapi.models.AuthenticationModel;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.GTimer;
import org.geogebra.common.util.GTimerListener;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.user.client.ui.Label;

public class SessionExpireNotifyDialog extends ComponentDialog
		implements GTimerListener {

	/**
	 * dialog to notify user that will be logged out when session expires
	 * @param app see {@link AppW}
	 * @param data dialog transkeys
	 */
	public SessionExpireNotifyDialog(AppW app, DialogData data) {
		super(app, data, false, true);
		addStyleName("sessionExpireNotifyDialog");
		buildContent();
		setOnPositiveAction(() -> ((DialogManagerW) app.getDialogManager()).showSaveDialog());
	}

	private void buildContent() {
		Label sessionExpireNotifyTxt = BaseWidgetFactory.INSTANCE.newSecondaryText(
				app.getLocalization().getMenu("sessionExpireNotify"), "sessionExpireTxt");
		addDialogContent(sessionExpireNotifyTxt);
	}

	@Override
	public void show() {
		super.show();
		startLogOutTimer();
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
