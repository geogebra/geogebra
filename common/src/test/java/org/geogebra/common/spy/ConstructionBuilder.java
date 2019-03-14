package org.geogebra.common.spy;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoDispatcher;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class ConstructionBuilder {

	private Construction construction;
	private KernelBuilder kernelBuilder;
	private AlgoDispatcher algoDispatcher;

	ConstructionBuilder(KernelBuilder kernelBuilder) {
		this.kernelBuilder = kernelBuilder;
	}

	Construction getConstruction() {
		if (construction == null) {
			construction = createConstruction(kernelBuilder.getKernel());
		}
		return construction;
	}

	private Construction createConstruction(final Kernel kernel) {
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
