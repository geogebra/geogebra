package geogebra.web.kernel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;

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
public class KernelW extends Kernel implements KernelWInterface {

	public KernelW() {
		super();
	}
	
	public KernelW(App app) {
	    super(app);
    }


    @Override
    public AlgebraProcessor newAlgebraProcessor(final Kernel kernel) {
    	//if (!kernel.hasAlgebraProcessor()) {
    		//GWT.runAsync(new RunAsyncCallback() {
			
    		//	public void onSuccess() {
    		//		kernel.setAlgebraProcessor(new AlgebraProcessor(kernel, new CommandDispatcherW(kernel)));
    		//		kernel.getApplication().getActiveEuclidianView().repaintView();
    		//	}
			
    		//	public void onFailure(Throwable reason) {
    		//		App.debug("Algebra processor loading failed");
    		//	}
    		//});
    	//}
    	//return kernel.getAlgPForAsync();
    	return new AlgebraProcessor(kernel, new CommandDispatcherW(kernel));
		
	}
	
}
