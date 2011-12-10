package geogebra.kernel.commands;

import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.kernel.Kernel;

public abstract class CommandProcessorDesktop extends CommandProcessor{
	protected Kernel kernel;
	/**
	 * Creates new command processor
	 * @param kernel Kernel
	 */
	public CommandProcessorDesktop(Kernel kernel) {
		super(kernel);
		this.kernel = (Kernel)kernelA;
	}

	

}
