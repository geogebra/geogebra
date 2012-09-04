package geogebra.web.kernel;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.commands.AlgebraProcessor;
import geogebra.common.main.App;
import geogebra.web.kernel.commands.CommandDispatcherW;

/**
 * @author gabor
 * 
 * For GWT.runAsync calls
 *
 */
public class KernelW extends Kernel {

	public KernelW(App app) {
	    super(app);
    }

	@Override
    public AlgebraProcessor newAlgebraProcessor(Kernel kernel) {
		return new AlgebraProcessor(kernel, new CommandDispatcherW(kernel));
	}
	
}
