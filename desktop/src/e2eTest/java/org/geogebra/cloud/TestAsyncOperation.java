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
