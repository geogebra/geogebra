package geogebra.common.kernel.scripting;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
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
	/** array of arguments */
	GeoElement[] arg;
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
	protected abstract void perform(Command c);
		
	/**
	 * Perform the actual command and remove all unlabeled inputs
	 * @param c command
	 */
	public void performAndClean(Command c){
		perform(c);
		for(int i=0;arg!=null && i<arg.length;i++)
			if(arg[i]!=null && !arg[i].isLabelSet() && !arg[i].isGeoCasCell())
				arg[i].remove();
	}
	@Override
	public final GeoElement[] process (Command c) throws MyError,
			CircularDefinitionException {
		GeoScriptAction sa =  new GeoScriptAction(cons,this,c);	
		return new GeoElement[] {sa};
	}

}
