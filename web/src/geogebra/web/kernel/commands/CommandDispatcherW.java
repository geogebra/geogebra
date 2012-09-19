package geogebra.web.kernel.commands;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.AsyncProxy;
import com.google.gwt.user.client.AsyncProxy.ConcreteType;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.commands.CommandDispatcher;
import geogebra.common.kernel.commands.CommandDispatcherAdvanced;
import geogebra.common.kernel.commands.CommandDispatcherCAS;
import geogebra.common.kernel.commands.CommandDispatcherDiscrete;
import geogebra.common.kernel.commands.CommandDispatcherInterface;
import geogebra.common.kernel.commands.CommandDispatcherScripting;
import geogebra.common.main.App;

public class CommandDispatcherW extends CommandDispatcher {

	public CommandDispatcherW(Kernel kernel) {
	    super(kernel);
    }
	
	
    protected CommandDispatcherInterface getDiscreteDispatcher() {
		if(discreteDispatcher == null) {
			GWT.runAsync(new RunAsyncCallback() {
				
				public void onSuccess() {
					discreteDispatcher = new CommandDispatcherDiscrete();		
					initCmdTable();
					kernel.getApplication().getActiveEuclidianView().repaintView();
				}
				
				public void onFailure(Throwable reason) {
					App.debug("CommandDispatcherDiscrete loading failed");
				}
			});	
		}
		return discreteDispatcher;
	}
	
	@Override
    protected CommandDispatcherInterface getScriptingDispatcher() {
		if(scriptingDispatcher == null) {
			GWT.runAsync(new RunAsyncCallback() {
				
				public void onSuccess() {
					scriptingDispatcher = new CommandDispatcherScripting();
					initCmdTable();
					kernel.getApplication().getActiveEuclidianView().repaintView();
				}
				
				public void onFailure(Throwable reason) {
					App.debug("CommandDispatcherScripting loading failed");
				}
			});
			
		}
		return scriptingDispatcher;
	}
	
	@Override
    protected CommandDispatcherInterface getAdvancedDispatcher() {
		if(advancedDispatcher == null) {
			GWT.runAsync(new RunAsyncCallback() {
				
				public void onSuccess() {
					advancedDispatcher = new CommandDispatcherAdvanced();
					initCmdTable();
					kernel.getApplication().getActiveEuclidianView().repaintView();
				}
				
				public void onFailure(Throwable reason) {
					App.debug("CommandDispatcherAdvanced loading failed");
				}
			});
			
		}
		return advancedDispatcher;
	}
	
	@Override
    protected CommandDispatcherCAS getCASDispatcher() {
		if(casDispatcher == null) {
			GWT.runAsync(new RunAsyncCallback() {
				
				public void onSuccess() {
					casDispatcher = new CommandDispatcherCAS();
					initCmdTable();
					kernel.getApplication().getActiveEuclidianView().repaintView();
				}
				
				public void onFailure(Throwable reason) {
					App.debug("CommandDispatcherCAS loading failed");
				}
			});
			
		}
		return casDispatcher;
	}

}
