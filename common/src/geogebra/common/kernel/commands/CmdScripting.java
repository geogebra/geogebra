package geogebra.common.kernel.commands;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoScriptAction;
import geogebra.common.main.MyError;
/**
 * Common processor for scripting commands -- the execution 
 * is delayed (GeoScriptAction is created and the command is not executed until
 * you call {@link GeoScriptAction#perform()}) so that they work nicely with If.
 * @author kondr
 *
 */
public abstract class CmdScripting extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdScripting(Kernel kernel) {
		super(kernel);
		// TODO Auto-generated constructor stub
	}
	/**
	 * Perform the actual command
	 * @param c command
	 */
	public abstract void perform(Command c);
		
	
	@Override
	public final GeoElement[] process (Command c) throws MyError,
			CircularDefinitionException {
		GeoScriptAction sa =  new GeoScriptAction(cons,this,c);	
		return new GeoElement[] {sa};
	}

}
