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
 
package org.geogebra.cloud;

import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;

public class TestAsyncOperation<S> implements AsyncOperation<S> {

	private boolean done;
	private S response;

	@Override
	public void callback(S obj) {
		done = true;
		response = obj;
	}

	/**
	 * Periodically check if it's done, if not fail after timeout.
	 * 
	 * @param time
	 *            timeout in seconds
	 * @return response
	 */
	public S await(int time) {
		for (int i = 0; i < time * 5; i++) {
			if (done) {
				return response;
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				Log.warn("cannot sleep");
			}
		}
		return response;
	}

}
