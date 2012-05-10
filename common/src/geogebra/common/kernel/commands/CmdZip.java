package geogebra.common.kernel.commands;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.MyError;

/**
 * Sequence[ <expression>, <number-var>, <from>, <to> ] Sequence[ <expression>,
 * <number-var>, <from>, <to>, <step> ] Sequence[ <number-var>]
 */
public class CmdZip extends CommandProcessor {
	/**
	 * Creates new zip command
	 * 
	 * @param kernel kernel
	 */
	public CmdZip(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();

		// avoid
		// "Command Sequence not known eg Sequence[If[Element[list1,i]=="b",0,1]]
		if (n < 3 || n % 2 == 0)
			throw argNumErr(app, c.getName(), n);

		// create local variable at position 1 and resolve arguments
		GeoElement arg = null;
		GeoElement[] vars = new GeoElement[n / 2];
		GeoList[] over = new GeoList[n / 2];
		boolean oldval = cons.isSuppressLabelsActive();
		try{
			cons.setSuppressLabelCreation(true);	
			arg = resArgsForZip(c,vars,over);
		}finally{
			cons.setSuppressLabelCreation(oldval);
		}
		return kernelA.Zip(c.getLabel(), arg, vars, over);
		
	}

	/**
	 * Resolves arguments, creates local variables and fills the vars and
	 * overlists
	 * 
	 * @param c zip command
	 * @param vars variables
	 * @param over lists from which the vars should be taken
	 * @return list of arguments
	 */
	protected final GeoElement resArgsForZip(Command c,GeoElement[] vars, GeoList[] over) {
		// check if there is a local variable in arguments
		int numArgs = c.getArgumentNumber();
		
		Construction cmdCons = c.getKernel().getConstruction();
		
		for (int varPos = 1; varPos < numArgs; varPos += 2) {
			String localVarName = c.getVariableName(varPos);
			if(localVarName==null && c.getArgument(varPos).isTopLevelCommand()){
				localVarName = c.getArgument(varPos).getTopLevelCommand().getVariableName(0);
			}
			
			if (localVarName == null) {
				throw argErr(app, c.getName(), c.getArgument(varPos));
			}

			// add local variable name to construction

			GeoElement num = null;

			// initialize first value of local numeric variable from initPos

		
			GeoList gl = (GeoList) resArg(c.getArgument(varPos + 1))[0];
			
			num = gl.size()==0?new GeoNumeric(cons):gl.get(0).copyInternal(cons);

			cmdCons.addLocalVariable(localVarName, num);
			// set local variable as our varPos argument
			c.setArgument(varPos, new ExpressionNode(c.getKernel(), num));
			vars[varPos / 2] = num.toGeoElement();
			over[varPos / 2] = gl;
			// resolve all command arguments including the local variable just
			// created

			// remove local variable name from kernel again

		}
		GeoElement[] arg = resArg(c.getArgument(0));
		for (GeoElement localVar : vars) 
			cmdCons.removeLocalVariable(localVar.getLabel(StringTemplate.defaultTemplate));
		
		return arg[0];
	}
}
