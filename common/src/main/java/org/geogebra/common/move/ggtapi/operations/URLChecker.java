package org.geogebra.common.move.ggtapi.operations;

import org.geogebra.common.util.AsyncOperation;

/**
 * URL checking utility
 */
public interface URLChecker {
	/**
	 * @param url      URL
	 * @param callback webpage status handler handler
	 */
	void check(final String url, final AsyncOperation<URLStatus> callback);

	/**
	 * @return whether X-Frame / CSP headers are read by this checker
	 */
	boolean hasFrameOptionCheck();
}
