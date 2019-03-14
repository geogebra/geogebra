package org.geogebra.common.spy;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.ConstructionCompanion;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeEvaluator;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class KernelBuilder {

	private Kernel kernel;
	private AppBuilder appBuilder;
	private Localization localization;
	private ExpressionNodeEvaluator expressionNodeEvaluator;

	KernelBuilder(AppBuilder appBuilder) {
		this.appBuilder = appBuilder;
	}

	Kernel getKernel() {
		if (kernel == null) {
			kernel = createKernel(appBuilder.getApp());
		}
		return kernel;
	}

	private Kernel createKernel(final App app) {
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
		when(kernel.createConstructionCompanion((Construction) anyObject())).then(new Answer<ConstructionCompanion>() {
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
