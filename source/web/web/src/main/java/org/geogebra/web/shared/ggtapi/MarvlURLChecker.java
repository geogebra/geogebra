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
