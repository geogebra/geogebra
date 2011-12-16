package geogebra.kernel.commands;

import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CmdScriptingInterface;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoScriptAction;
import geogebra.common.main.MyError;

public abstract class CmdScripting extends CommandProcessor implements CmdScriptingInterface{

	

	public CmdScripting(AbstractKernel kernel) {
		super(kernel);
		// TODO Auto-generated constructor stub
	}
	public abstract void perform(Command c);
		
	
	@Override
	public final GeoElement[] process (Command c) throws MyError,
			CircularDefinitionException {
		GeoScriptAction sa =  new GeoScriptAction(cons,this,c);	
		return new GeoElement[] {sa};
	}

}
