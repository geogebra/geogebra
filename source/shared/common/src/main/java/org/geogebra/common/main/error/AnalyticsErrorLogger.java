package org.geogebra.common.main.error;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.main.MyError;
import org.geogebra.common.util.debug.Analytics;

/**
 * Logs command errors as analytics events.
 */
public interface AnalyticsErrorLogger {

	/**
	 * Log the command as analytics event.
	 * @param error error
	 * @param command command
	 */
	default void logAnalytics(MyError error, String command) {
		if (error.getErrorType() == null) {
			return;
		}
		Map<String, Object> params = new HashMap<>();
		params.put(Analytics.Param.COMMAND, error.getcommandName());
		params.put(Analytics.Param.AV_INPUT, command);
		params.put(Analytics.Param.ERROR_TYPE, error.getErrorType().name());
		Analytics.logEvent(Analytics.Event.COMMAND_ERROR, params);
	}
}
