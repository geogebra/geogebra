package geogebra.common.kernel.commands;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
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

		boolean[] ok = new boolean[n];

		// create local variable at position 1 and resolve arguments
		GeoElement[] arg;
		arg = resArgsForZip(c);

		if ((ok[0] = arg[0].isGeoElement()) && (ok[2] = arg[2].isGeoList())) {
			return kernelA.Zip(c.getLabel(), arg[0], vars, over);
		} 
		throw argErr(app,c.getName(),getBadArg(ok,arg));
		
	}

	private GeoElement[] vars;
	private GeoList[] over;

	/**
	 * Resolves arguments, creates local variables and fills the vars and
	 * overlists
	 * 
	 * @param c zip command
	 * @return list of arguments
	 */
	protected final GeoElement[] resArgsForZip(Command c) {
		// check if there is a local variable in arguments
		int numArgs = c.getArgumentNumber();
		vars = new GeoElement[numArgs / 2];
		over = new GeoList[numArgs / 2];
		Construction cmdCons = c.getKernel().getConstruction();

		for (int varPos = 1; varPos < numArgs; varPos += 2) {
			String localVarName = c.getVariableName(varPos);
			if (localVarName == null) {
				throw argErr(app, c.getName(), c.getArgument(varPos));
			}

			// add local variable name to construction

			GeoElement num = null;

			// initialize first value of local numeric variable from initPos

			boolean oldval = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);
			GeoList gl = (GeoList) resArg(c.getArgument(varPos + 1))[0];
			cons.setSuppressLabelCreation(oldval);
			num = gl.get(0).copyInternal(cons);

			cmdCons.addLocalVariable(localVarName, num);
			// set local variable as our varPos argument
			c.setArgument(varPos, new ExpressionNode(c.getKernel(), num));
			vars[varPos / 2] = num.toGeoElement();
			over[varPos / 2] = gl;
			// resolve all command arguments including the local variable just
			// created

			// remove local variable name from kernel again

		}
		GeoElement[] arg = resArgs(c);
		for (GeoElement localVar : vars)
			cmdCons.removeLocalVariable(localVar.getLabel(StringTemplate.defaultTemplate));
		return arg;
	}
}
