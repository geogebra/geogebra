package org.geogebra.common.spy.builder;

import org.geogebra.common.main.error.ErrorHandler;

import static org.mockito.Mockito.mock;

/**
 * Creates an ErrorHandler mock.
 */
public class ErrorHandlerBuilder extends SpyBuilder<ErrorHandler> {

	@Override
	ErrorHandler createSpy() {
		return mock(ErrorHandler.class);
	}
}
