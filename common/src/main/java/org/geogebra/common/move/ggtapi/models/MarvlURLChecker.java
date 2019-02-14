package org.geogebra.common.move.ggtapi.models;

import org.geogebra.common.move.ggtapi.operations.URLChecker;
import org.geogebra.common.move.ggtapi.operations.URLStatus;
import org.geogebra.common.util.AsyncOperation;

public class MarvlURLChecker implements URLChecker {

	@Override
	public void checkURL(String url, AsyncOperation<URLStatus> callback) {
		// implement me
	}

}
