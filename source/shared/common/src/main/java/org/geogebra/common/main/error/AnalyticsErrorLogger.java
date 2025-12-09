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
