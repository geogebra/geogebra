package org.geogebra.common.spy;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.main.App;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.spy.builder.AlgebraProcessorBuilder;
import org.geogebra.common.spy.builder.AppBuilder;
import org.geogebra.common.spy.builder.ConstructionBuilder;
import org.geogebra.common.spy.builder.ErrorHandlerBuilder;
import org.geogebra.common.spy.builder.KernelBuilder;
import org.geogebra.common.spy.builder.SpyBuilder;

/**
 * Provides every kind of spies that are needed for unit testing.
 */
public class SpyProvider {

	private SpyBuilder<Construction> constructionBuilder;
	private SpyBuilder<AlgebraProcessor> algebraProcessorBuilder;
	private SpyBuilder<ErrorHandler> errorHandlerBuilder;

	/**
	 * Creates a specific spy builder for each class that are needed for testing.
	 */
	public SpyProvider() {
		SpyBuilder<App> appBuilder = new AppBuilder();
		SpyBuilder<Kernel> kernelBuilder = new KernelBuilder(appBuilder);
		constructionBuilder = new ConstructionBuilder(kernelBuilder);
		algebraProcessorBuilder = new AlgebraProcessorBuilder(constructionBuilder);
		errorHandlerBuilder = new ErrorHandlerBuilder();
	}

	public Construction getConstruction() {
		return constructionBuilder.getSpy();
	}

	public AlgebraProcessor getAlgebraProcessor() {
		return algebraProcessorBuilder.getSpy();
	}

	public ErrorHandler getErrorHandler() {
		return errorHandlerBuilder.getSpy();
	}
}
