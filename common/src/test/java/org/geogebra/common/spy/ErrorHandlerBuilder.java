package org.geogebra.common.spy;

import org.geogebra.common.main.error.ErrorHandler;

import static org.mockito.Mockito.mock;

class ErrorHandlerBuilder {

	private ErrorHandler errorHandler;

	ErrorHandler getErrorHandler() {
		if (errorHandler == null) {
			errorHandler = createErrorHandler();
		}
		return errorHandler;
	}

	private ErrorHandler createErrorHandler() {
		return mock(ErrorHandler.class);
	}
}
