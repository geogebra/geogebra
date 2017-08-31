package org.geogebra.common.kernel.stepbystep.steptree;

public class ArbitraryConstantFactory {
	private String label;
	private int counter;
	private StepArbitraryConstant.ConstantType type;

	public ArbitraryConstantFactory(String label, StepArbitraryConstant.ConstantType type) {
		this.label = label;
		this.type = type;
		this.counter = 1;
	}

	public StepArbitraryConstant getNext() {
		return new StepArbitraryConstant(label, counter++, type);
	}
}
