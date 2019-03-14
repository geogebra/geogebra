package org.geogebra.common.spy;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.main.error.ErrorHandler;

public class SpyBuilder {

	private ConstructionBuilder constructionBuilder;
	private AlgebraProcessorBuilder algebraProcessorBuilder;
	private ErrorHandlerBuilder errorHandlerBuilder;

	public SpyBuilder() {
		AppBuilder appBuilder = new AppBuilder();
		KernelBuilder kernelBuilder = new KernelBuilder(appBuilder);
		constructionBuilder = new ConstructionBuilder(kernelBuilder);
		algebraProcessorBuilder = new AlgebraProcessorBuilder(constructionBuilder);
		errorHandlerBuilder = new ErrorHandlerBuilder();
	}

	public Construction getConstruction() {
		return constructionBuilder.getConstruction();
	}

	public AlgebraProcessor getAlgebraProcessor() {
		return algebraProcessorBuilder.getAlgebraProcessor();
	}

	public ErrorHandler getErrorHandler() {
		return errorHandlerBuilder.getErrorHandler();
	}
}
