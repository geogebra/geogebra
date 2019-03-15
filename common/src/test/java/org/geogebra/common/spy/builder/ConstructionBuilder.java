package org.geogebra.common.spy.builder;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoDispatcher;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class ConstructionBuilder extends SpyBuilder<Construction> {

	private SpyBuilder<Kernel> kernelBuilder;
	private AlgoDispatcher algoDispatcher;

	public ConstructionBuilder(SpyBuilder<Kernel> kernelBuilder) {
		this.kernelBuilder = kernelBuilder;
	}

	@Override
	Construction createSpy() {
		Kernel kernel = kernelBuilder.getSpy();
		final Construction construction = spy(new Construction(kernel));
		construction.setConstructionDefaults(spy(new ConstructionDefaults(construction)));

		when(kernel.getConstruction()).then(new Answer<Construction>() {
			@Override
			public Construction answer(InvocationOnMock invocationOnMock) {
				return construction;
			}
		});
		when(kernel.getAlgoDispatcher()).then(new Answer<AlgoDispatcher>() {
			@Override
			public AlgoDispatcher answer(InvocationOnMock invocation) {
				return getAlgoDispatcher(construction);
			}
		});
		return construction;
	}

	private AlgoDispatcher getAlgoDispatcher(Construction construction) {
		if (algoDispatcher == null) {
			algoDispatcher = createAlgoDispatcher(construction);
		}
		return algoDispatcher;
	}

	private AlgoDispatcher createAlgoDispatcher(final Construction construction) {
		return spy(new AlgoDispatcher(construction));
	}
}
