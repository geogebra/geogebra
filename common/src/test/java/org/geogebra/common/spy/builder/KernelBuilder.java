package org.geogebra.common.spy.builder;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.ConstructionCompanion;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeEvaluator;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Creates a Kernel mock.
 */
public class KernelBuilder extends SpyBuilder<Kernel>{

	private SpyBuilder<App> appBuilder;
	private Localization localization;
	private ExpressionNodeEvaluator expressionNodeEvaluator;

	/**
	 * @param appBuilder
	 * The Kernel needs an App in order to be mocked properly,
	 * so this app builder will provide an App mock for the Kernel mock.
	 */
	public KernelBuilder(SpyBuilder<App> appBuilder) {
		this.appBuilder = appBuilder;
	}

	@Override
	Kernel createSpy() {
		final App app = appBuilder.getSpy();
		final Kernel kernel = mock(Kernel.class);

		when(kernel.getApplication()).then(new Answer<App>() {
			@Override
			public App answer(InvocationOnMock invocationOnMock) {
				return app;
			}
		});
		when(kernel.getLocalization()).then(new Answer<Localization>() {
			@Override
			public Localization answer(InvocationOnMock invocation) {
				return getLocalization();
			}
		});
		when(kernel.getExpressionNodeEvaluator()).then(new Answer<ExpressionNodeEvaluator>() {
			@Override
			public ExpressionNodeEvaluator answer(InvocationOnMock invocation) {
				return getExpressionNodeEvaluator(kernel);
			}
		});
		when(kernel.createConstructionCompanion((Construction) any()))
				.then(new Answer<ConstructionCompanion>() {
			@Override
			public ConstructionCompanion answer(InvocationOnMock invocation) {
				return mock(ConstructionCompanion.class);
			}
		});

		when(app.getKernel()).then(new Answer<Kernel>() {
			@Override
			public Kernel answer(InvocationOnMock invocation) {
				return kernel;
			}
		});

		return kernel;
	}

	private Localization getLocalization() {
		if (localization == null) {
			localization = createLocalization();
		}
		return localization;
	}

	private Localization createLocalization() {
		return mock(Localization.class);
	}

	private ExpressionNodeEvaluator getExpressionNodeEvaluator(Kernel kernel) {
		if (expressionNodeEvaluator == null) {
			expressionNodeEvaluator = createExpressionNodeEvaluator(kernel);
		}
		return expressionNodeEvaluator;
	}

	private ExpressionNodeEvaluator createExpressionNodeEvaluator(Kernel kernel) {
		return spy(new ExpressionNodeEvaluator(getLocalization(), kernel));
	}
}
