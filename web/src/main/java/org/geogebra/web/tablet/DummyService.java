package org.geogebra.web.tablet;

import java.util.List;
import java.util.logging.LogRecord;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.gwtphonegap.client.log.shared.PhoneGapLogService;
import com.googlecode.gwtphonegap.client.log.shared.PhoneGapLogServiceAsync;

public class DummyService
		implements PhoneGapLogService, PhoneGapLogServiceAsync {

	public String logOnServer(String clientId, List<LogRecord> record) {
		return "";
	}

	public void logOnServer(String clientId, List<LogRecord> record,
			AsyncCallback<String> callback) {
		// ignore

	}

}
