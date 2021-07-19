package org.geogebra.web.html5.util;

import org.geogebra.web.html5.util.debug.LoggerW;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.core.client.JavaScriptException;

public class SuperDevUncaughtExceptionHandler
		implements UncaughtExceptionHandler {
	/**
	 * Registers handler for UnhandledExceptions that are wrapped by GWT by
	 * default
	 */
	public static void register() {
		GWT.setUncaughtExceptionHandler(new SuperDevUncaughtExceptionHandler());
	}
	
	@Override
	public void onUncaughtException(Throwable t) {
		Throwable cause = t;
		while (cause.getCause() != null) {
			cause = cause.getCause();
		}
		Object nativeCause = cause instanceof JavaScriptException
				&& ((JavaScriptException) cause).getThrown() != null
						? ((JavaScriptException) cause).getThrown()
						: cause;
		LoggerW.printErrorMessage(nativeCause);
	}
}
