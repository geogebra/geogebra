package geogebra.web.kernel.commands;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.AsyncProxy;
import com.google.gwt.user.client.AsyncProxy.ConcreteType;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.commands.CommandDispatcher;
import geogebra.common.kernel.commands.CommandDispatcherDiscrete;
import geogebra.common.kernel.commands.CommandDispatcherInterface;

public class CommandDispatcherW extends CommandDispatcher {

	public CommandDispatcherW(Kernel kernel) {
	    super(kernel);
    }
	
	@ConcreteType(CommandDispatcherDiscrete.class)
	@com.google.gwt.user.client.AsyncProxy.AllowNonVoid
	interface CommandDispatcherDiscrateProxy extends AsyncProxy<CommandDispatcherInterface>, CommandDispatcherInterface {}
	
	@Override
    protected CommandDispatcherInterface getDiscreteDispatcher() {
		if(discreteDispatcher == null) {
			discreteDispatcher = GWT.create(CommandDispatcherDiscrateProxy.class);
		}
		return discreteDispatcher;
	}

}
