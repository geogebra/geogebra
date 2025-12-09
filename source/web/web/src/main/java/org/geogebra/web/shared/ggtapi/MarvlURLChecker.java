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

package org.geogebra.web.shared.ggtapi;

import org.geogebra.common.move.ggtapi.operations.URLChecker;
import org.geogebra.common.move.ggtapi.operations.URLStatus;
import org.geogebra.common.util.AsyncOperation;

public class MarvlURLChecker implements URLChecker {

	private final URLValidator validator;

	public MarvlURLChecker() {
		validator = new URLValidator();
	}

	@Override
	public void check(String url, AsyncOperation<URLStatus> callback) {
		URLStatus status = new URLStatus();
		if (validator.isValid(url)) {
			status.withUrl(url);
		} else {
			status.setErrorKey("InvalidInput");
		}
		callback.callback(status);
	}

	@Override
	public boolean hasFrameOptionCheck() {
		return false;
	}
}
