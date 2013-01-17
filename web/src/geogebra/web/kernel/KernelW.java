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
public class KernelW extends Kernel implements KernelWInterface {

	public KernelW() {
		super();
		MAX_SPREADSHEET_COLUMNS = 26;//1..26
		MAX_SPREADSHEET_ROWS = 200;//1..200
	}

	public KernelW(App app) {
	    super(app);
		MAX_SPREADSHEET_COLUMNS = 26;
		MAX_SPREADSHEET_ROWS = 200;
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
