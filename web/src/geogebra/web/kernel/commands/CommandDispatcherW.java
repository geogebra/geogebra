package geogebra.web.kernel.commands;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.AsyncProxy;
import com.google.gwt.user.client.AsyncProxy.ConcreteType;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.commands.CommandDispatcher;
import geogebra.common.kernel.commands.CommandDispatcherAdvanced;
import geogebra.common.kernel.commands.CommandDispatcherCAS;
import geogebra.common.kernel.commands.CommandDispatcherDiscrete;
import geogebra.common.kernel.commands.CommandDispatcherInterface;
import geogebra.common.kernel.commands.CommandDispatcherScripting;

public class CommandDispatcherW extends CommandDispatcher {

	public CommandDispatcherW(Kernel kernel) {
	    super(kernel);
    }
	
	/*@ConcreteType(CommandDispatcherDiscrete.class)
	@com.google.gwt.user.client.AsyncProxy.AllowNonVoid
	interface CommandDispatcherDiscreteProxy extends AsyncProxy<CommandDispatcherInterface>, CommandDispatcherInterface {}
	
	@Override
    protected CommandDispatcherInterface getDiscreteDispatcher() {
		if(discreteDispatcher == null) {
			discreteDispatcher = GWT.create(CommandDispatcherDiscreteProxy.class);
		}
		return discreteDispatcher;
	}
	
	@ConcreteType(CommandDispatcherScripting.class)
	@com.google.gwt.user.client.AsyncProxy.AllowNonVoid
	interface CommandDispatcherScriptingProxy extends AsyncProxy<CommandDispatcherInterface>, CommandDispatcherInterface {}
	
	
	@Override
    protected CommandDispatcherInterface getScriptingDispatcher() {
		if(scriptingDispatcher == null) {
			scriptingDispatcher = GWT.create(CommandDispatcherScripting.class);
		}
		return scriptingDispatcher;
	}
	
	@ConcreteType(CommandDispatcherAdvanced.class)
	@com.google.gwt.user.client.AsyncProxy.AllowNonVoid
	interface CommandDispatcherAdvancedProxy extends AsyncProxy<CommandDispatcherInterface>, CommandDispatcherInterface {}
		
	@Override
    protected CommandDispatcherInterface getAdvancedDispatcher() {
		if(advancedDispatcher == null) {
			advancedDispatcher = GWT.create(CommandDispatcherAdvanced.class);
		}
		return advancedDispatcher;
	}
	
	@ConcreteType(CommandDispatcherCAS.class)
	@com.google.gwt.user.client.AsyncProxy.AllowNonVoid
	interface CommandDispatcherCASProxy extends AsyncProxy<CommandDispatcherInterface>, CommandDispatcherInterface {}
	
	@Override
    protected CommandDispatcherCAS getCASDispatcher() {
		if(casDispatcher == null) {
			casDispatcher = GWT.create(CommandDispatcherCAS.class);
		}
		return casDispatcher;
	}*/

}
