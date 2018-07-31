package org.geogebra.cloud;

import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;

public class TestAsyncOperation<S> implements AsyncOperation<S> {

	private boolean done;

	@Override
	public void callback(S obj) {
		done = true;
	}
	
	public void await(int time) {
		for (int i = 0; i < time * 5; i++) {
			if (done) {
				return;
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				Log.warn("cannot sleep");
			}
		}
	}

}
