package org.geogebra.common.spy;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class AlgebraProcessorBuilder {

	private AlgebraProcessor algebraProcessor;
	private ConstructionBuilder constructionBuilder;

	AlgebraProcessorBuilder(ConstructionBuilder constructionBuilder) {
		this.constructionBuilder = constructionBuilder;
	}

	AlgebraProcessor getAlgebraProcessor() {
		if (algebraProcessor == null) {
			algebraProcessor = createAlgebraProcessor(constructionBuilder.getConstruction());
		}
		return algebraProcessor;
	}

	private AlgebraProcessor createAlgebraProcessor(final Construction construction) {
		final AlgebraProcessor algebraProcessor =
				spy(new AlgebraProcessor(construction, mock(CommandDispatcher.class)));

		final Kernel kernel = construction.getKernel();
		when(kernel.getAlgebraProcessor()).then(new Answer<AlgebraProcessor>() {
			@Override
			public AlgebraProcessor answer(InvocationOnMock invocation) {
				return algebraProcessor;
			}
		});
		return algebraProcessor;
	}
}
