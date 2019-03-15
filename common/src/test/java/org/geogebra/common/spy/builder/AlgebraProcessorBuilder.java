package org.geogebra.common.spy.builder;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.CommandDispatcher;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Creates an AlgebraProcessor spy.
 */
public class AlgebraProcessorBuilder extends SpyBuilder<AlgebraProcessor> {

	private SpyBuilder<Construction> constructionBuilder;

	/**
	 * @param constructionBuilder
	 * The AlgebraProcessor needs a Construction in order to be mocked properly,
	 * so this construction builder will provide a Construction spy for the AlgebraProcessor spy.
	 */
	public AlgebraProcessorBuilder(SpyBuilder<Construction> constructionBuilder) {
		this.constructionBuilder = constructionBuilder;
	}

	@Override
	AlgebraProcessor createSpy() {
		Construction construction = constructionBuilder.getSpy();
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
