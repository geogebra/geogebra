package org.geogebra.web.tablet;

import java.util.List;
import java.util.logging.LogRecord;

import com.googlecode.gwtphonegap.client.log.shared.PhoneGapLogService;

public class DummyService implements PhoneGapLogService {

	public String logOnServer(String clientId, List<LogRecord> record) {
		return "";
	}

}
