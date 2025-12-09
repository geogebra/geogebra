/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
		setOnPositiveAction(() -> app.getDialogManager().showSaveDialog());
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
