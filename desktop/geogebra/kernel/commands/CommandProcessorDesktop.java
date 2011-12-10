package geogebra.kernel.commands;

import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.kernel.Kernel;

public abstract class CommandProcessorDesktop extends CommandProcessor{
	protected Kernel kernel;
	public CommandProcessorDesktop(Kernel kernel) {
		super(kernel);
		kernel = (Kernel)kernelA;
	}

	

}
