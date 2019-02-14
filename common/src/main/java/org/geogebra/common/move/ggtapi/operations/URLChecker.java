package org.geogebra.common.move.ggtapi.operations;

import org.geogebra.common.util.AsyncOperation;

public interface URLChecker {
	/**
	 * @param url      URL
	 * @param callback webpage status handler handler
	 */
	void checkURL(final String url, final AsyncOperation<URLStatus> callback);
}
