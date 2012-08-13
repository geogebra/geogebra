/**
 * GeoGebra - Dynamic Mathematics for Everyone 
 * http://www.geogebra.org
 * 
 * This file is part of GeoGebra.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 */

package geogebra.common.kernel.commands;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoFunctionFreehand;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.Equation;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.MySpecialDouble;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.arithmetic.Traversing.Replacer;
import geogebra.common.kernel.arithmetic.Variable;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.App;
import geogebra.common.main.MyError;
import geogebra.common.plugin.GeoClass;
import geogebra.common.util.Unicode;

import java.util.ArrayList;

/**
 * Resolves arguments of the command, checks their validity and creates
 * resulting geos via appropriate Kernel methods
 */
public abstract class CommandProcessor {

	/** application */
	protected App app;
	/** kernel */
	protected Kernel kernelA;
	/** construction */
	protected Construction cons;
	private AlgebraProcessor algProcessor;

	/**
	 * Creates new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CommandProcessor(Kernel kernel) {
		this.kernelA = kernel;
		cons = kernel.getConstruction();
		app = kernel.getApplication();
		algProcessor = kernel.getAlgebraProcessor();
	}

	/**
	 * Every CommandProcessor has to implement this method
	 * 
	 * @param c
	 *            command
	 * @return list of resulting geos
	 * @throws MyError for wrong number / type of parameters
	 * @throws CircularDefinitionException if circular definition occurs
	 */
	public abstract GeoElement[] process(Command c) throws MyError,
			CircularDefinitionException;

	/**
	 * Resolves arguments. When argument produces mor geos, only first is taken.
	 * 
	 * @param c command
	 * @return array of arguments
	 * @throws MyError if processing of some argument causes error (i.e. wrong syntax of subcommand)
	 */
	protected final GeoElement[] resArgs(Command c) throws MyError {
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		// resolve arguments to get GeoElements
		ExpressionNode[] arg = c.getArguments();
		GeoElement[] result = new GeoElement[arg.length];

		for (int i = 0; i < arg.length; ++i) {
			// resolve variables in argument expression
			arg[i].resolveVariables(arg[i].getLeft() instanceof Equation);

			// resolve i-th argument and get GeoElements
			// use only first resolved argument object for result
			result[i] = resArg(arg[i])[0];
		}

		cons.setSuppressLabelCreation(oldMacroMode);
		return result;
	}

	/**
	 * Resolves argument
	 * 
	 * @param arg argument
	 * @return array of arguments
	 * @throws MyError if processing argument causes error (i.e. wrong syntax of subcommand)
	 */
	protected final GeoElement[] resArg(ExpressionNode arg) throws MyError {
		GeoElement[] geos = algProcessor.processExpressionNode(arg);

		if (geos != null) {
			return geos;
		}
		String[] str = { "IllegalArgument", arg.toString(StringTemplate.defaultTemplate) };
		throw new MyError(app, str);
	}

	/**
	 * Resolve arguments of a command that has a local numeric variable at the
	 * position varPos. Initializes the variable with the NumberValue at
	 * initPos.
	 * 
	 * @param c command
	 * @param varPos position of variable
	 * @param initPos position of initial value
	 * @return Array of arguments
	 */
	protected final GeoElement[] resArgsLocalNumVar(Command c, int varPos,
			int initPos) {
		// check if there is a local variable in arguments
		String localVarName = c.getVariableName(varPos);
		if (localVarName == null) {
			throw argErr(app, c.getName(), c.getArgument(varPos));
		}
		// imaginary unit as local variable name
		else if (localVarName.equals(Unicode.IMAGINARY)) {
			// replace all imaginary unit objects in command arguments by a
			// variable "i"object
			localVarName = "i";
			Variable localVar = new Variable(kernelA, localVarName);
			c.traverse(Replacer.getReplacer(kernelA.getImaginaryUnit(), localVar));
		}
		// Euler constant as local variable name
		else if (localVarName.equals(Unicode.EULER_STRING)) {
			// replace all imaginary unit objects in command arguments by a
			// variable "i"object
			localVarName = "e";
			Variable localVar = new Variable(kernelA, localVarName);
			c.traverse(Replacer.getReplacer(MySpecialDouble.getEulerConstant(kernelA), localVar));
		}

		// add local variable name to construction
		Construction cmdCons = c.getKernel().getConstruction();
		GeoNumeric num = new GeoNumeric(cmdCons);
		cmdCons.addLocalVariable(localVarName, num);

		// initialize first value of local numeric variable from initPos
		if (initPos != varPos) {
			boolean oldval = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);
			NumberValue initValue = (NumberValue) resArg(c.getArgument(initPos))[0];
			cons.setSuppressLabelCreation(oldval);
			num.setValue(initValue.getDouble());
		}

		// set local variable as our varPos argument
		c.setArgument(varPos, new ExpressionNode(c.getKernel(), num));

		// resolve all command arguments including the local variable just
		// created
		GeoElement[] arg = resArgs(c);

		// remove local variable name from kernel again
		cmdCons.removeLocalVariable(localVarName);
		return arg;
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
			
			if (gl.size() == 0) {
				if (gl.getTypeStringForXML() != null) {
					num = kernelA.createGeoElement(cons, gl.getTypeStringForXML());
				} else {
					// guess
					num = new GeoNumeric(cons);
				}
			} else {
				// list not zero length
				num = gl.get(0).copyInternal(cons);
			}

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
	/**
	 * Resolve arguments of a command that has a several local numeric variable
	 * at the position varPos. Initializes the variable with the NumberValue at
	 * initPos.
	 * 
	 * @param c command
	 * @param varPos
	 *            positions of local variables
	 * @param initPos
	 *            positions of vars to be initialized
	 * @return array of arguments
	 */
	protected final GeoElement[] resArgsLocalNumVar(Command c, int varPos[],
			int initPos[]) {

		String[] localVarName = new String[varPos.length];

		for (int i = 0; i < varPos.length; i++) {
			// check if there is a local variable in arguments
			localVarName[i] = c.getVariableName(varPos[i]);
			if (localVarName[i] == null) {
				throw argErr(app, c.getName(), c.getArgument(varPos[i]));
			}
		}

		// add local variable name to construction
		Construction cmdCons = c.getKernel().getConstruction();
		GeoNumeric[] num = new GeoNumeric[varPos.length];
		for (int i = 0; i < varPos.length; i++) {
			num[i] = new GeoNumeric(cmdCons);
			cmdCons.addLocalVariable(localVarName[i], num[i]);
		}

		// initialize first value of local numeric variable from initPos
		boolean oldval = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);
		NumberValue[] initValue = new NumberValue[varPos.length];
		for (int i = 0; i < varPos.length; i++)
			initValue[i] = (NumberValue) resArg(c.getArgument(initPos[i]))[0];
		cons.setSuppressLabelCreation(oldval);
		for (int i = 0; i < varPos.length; i++)
			num[i].setValue(initValue[i].getDouble());

		// set local variable as our varPos argument
		for (int i = 0; i < varPos.length; i++)
			c.setArgument(varPos[i], new ExpressionNode(c.getKernel(), num[i]));

		// resolve all command arguments including the local variable just
		// created
		GeoElement[] arg = resArgs(c);

		// remove local variable name from kernel again
		for (int i = 0; i < varPos.length; i++)
			cmdCons.removeLocalVariable(localVarName[i]);
		return arg;
	}

	private StringBuilder sb;

	/**
	 * Creates wrong argument error
	 * 
	 * @param app1 application
	 * @param cmd command name
	 * @param arg faulty argument
	 * @return wrong argument error
	 */
	protected final MyError argErr(App app1, String cmd,
			ExpressionValue arg) {
		String localName = app1.getCommand(cmd);
		if (sb == null)
			sb = new StringBuilder();
		else
			sb.setLength(0);

		final boolean reverseOrder = app1.isReverseNameDescriptionLanguage();
		if (!reverseOrder) {
			// standard order: "Command ..."
			sb.append(app1.getCommand("Command"));
			sb.append(' ');
			sb.append(localName);
			}
		else {
			// reverse order: "... command"
			sb.append(localName);
			sb.append(' ');
			sb.append(app1.getCommand("Command").toLowerCase());
			}
		
		sb.append(":\n");
		sb.append(app1.getError("IllegalArgument"));
		sb.append(": ");
		if (arg instanceof GeoElement)
			sb.append(((GeoElement) arg).getNameDescription());
		else if (arg != null)
			sb.append(arg.toString(StringTemplate.defaultTemplate));
		sb.append("\n\n");
		sb.append(app1.getPlain("Syntax"));
		sb.append(":\n");
		sb.append(app1.getCommandSyntax(cmd));
		return new MyError(app1, sb.toString(), cmd);
	}

	/**
	 * Creates wrong parameter count error
	 * 
	 * @param app1 application
	 * @param cmd command name
	 * @param argNumber
	 *            (-1 for just show syntax)
	 * @return wrong parameter count error
	 */
	protected final MyError argNumErr(App app1, String cmd,
			int argNumber) {
		if (sb == null)
			sb = new StringBuilder();
		else
			sb.setLength(0);
		getCommandSyntax(sb, app1, cmd, argNumber);
		return new MyError(app1, sb.toString(), cmd);
	}

	/**
	 * Copies error syntax into a StringBuilder
	 * 
	 * @param sb string builder to store result
	 * @param app application
	 * @param cmd command name (internal)
	 * @param argNumber
	 *            (-1 for just show syntax)
	 */
	public static void getCommandSyntax(StringBuilder sb,
			App app, String cmd, int argNumber) {

		final boolean reverseOrder = app.isReverseNameDescriptionLanguage();
		if (!reverseOrder) {
			// standard order: "Command ..."
			sb.append(app.getCommand("Command"));
			sb.append(' ');
			sb.append(app.getCommand(cmd));
		}
		else {
			// reverse order: "... command"
			sb.append(app.getCommand(cmd));
			sb.append(' ');
			sb.append(app.getCommand("Command").toLowerCase());
		}

		if (argNumber > -1) {
			sb.append(":\n");
			sb.append(app.getError("IllegalArgumentNumber"));
			sb.append(": ");
			sb.append(argNumber);
		}
		sb.append("\n\n");
		sb.append(app.getPlain("Syntax"));
		sb.append(":\n");
		sb.append(app.getCommandSyntax(cmd));

	}

	/**
	 * Creates change dependent error
	 * 
	 * @param app1 application
	 * @param geo dependent geo
	 * @return change dependent error
	 */
	final static MyError chDepErr(App app1, GeoElement geo) {
		String[] strs = { "ChangeDependent", geo.getLongDescription() };
		return new MyError(app1, strs);
	}

	/**
	 * Returns bad argument (according to ok array) and throws error if no was
	 * found.
	 * 
	 * @param ok array of "bad" flags
	 * @param arg array of arguments
	 * @return bad argument
	 */
	protected static GeoElement getBadArg(boolean[] ok, GeoElement[] arg) {
		for (int i = 0; i < ok.length; i++) {
			if (!ok[i])
				return arg[i];
		}
		return arg[arg.length-1];
	}

	/**
	 * Creates a dependent list with all GeoElement objects from the given
	 * array.
	 * 
	 * @param args array of arguments
	 * @param type
	 *            -1 for any GeoElement object type; GeoElement.GEO_CLASS_ANGLE,
	 *            etc. for specific types
	 * @return null if GeoElement objects did not have the correct type
	 * @author Markus Hohenwarter
	 * @param kernel kernel
	 * @param length number of arguments
	 * @date Jan 26, 2008
	 */
	public static GeoList wrapInList(Kernel kernel, GeoElement[] args,
			int length, GeoClass type) {
		Construction cons = kernel.getConstruction();
		boolean correctType = true;
		ArrayList<GeoElement> geoElementList = new ArrayList<GeoElement>();
		for (int i = 0; i < length; i++) {
			if (type.equals(GeoClass.DEFAULT)
					|| args[i].getGeoClassType() == type)
				geoElementList.add(args[i]);
			else {
				correctType = false;
				break;
			}
		}
		
		GeoList list = null;
		if (correctType) {
			boolean oldMacroMode = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);
			list = kernel.List(null, geoElementList, false);
			cons.setSuppressLabelCreation(oldMacroMode);
		}

		// list of zero size is not wanted
		if (list != null && list.size() == 0)
			list = null;

		return list;
	}
	
	/**
	 * Used by eg FitSin to allow a freehand function to be passed as an arg
	 * 
	 * converts a list of y-coordinates into a list of GeoPoints
	 * @param kernelA kernel
	 * @param algo function's parent algorithm
	 * @return list of points on the function
	 */
	public static  GeoList wrapFreehandFunctionArgInList(Kernel kernelA, AlgoFunctionFreehand algo) {

		Construction cons = kernelA.getConstruction();


		GeoList list = (GeoList) algo.getInput()[0];

		// first 2 points in list are start and end, rest are y-coordinates
		double start = ((NumberValue)list.get(0)).getDouble();
		double end = ((NumberValue)list.get(1)).getDouble();
		int size = list.size() - 2;

		double step = (end - start) / (size -1);

		ArrayList<GeoElement> geoElementList = new ArrayList<GeoElement>();
		for (int i = 0; i < size; i++) {
			GeoPoint p = new GeoPoint(cons, start + i * step, ((NumberValue)list.get(2 + i)).getDouble(), 1.0);
			geoElementList.add(p);
		}

		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);
		list = kernelA.List(null, geoElementList, false);
		cons.setSuppressLabelCreation(oldMacroMode);

		return list;
	}

	/**
	 *  see #2552
	 *  
	 * @param arg
	 * @param name
	 * @param i
	 * @param j
	 */
	protected void checkDependency(GeoElement[] arg, String name, int i, int j) {
		if (arg[i].isChildOrEqual(arg[j])) {
			if (kernelA.getConstruction().isFileLoading()) {
				// make sure old files can be loaded (and fixed)
				App.warn("wrong dependency in "+name);
			} else {
				throw argErr(app, name, arg[i]);	
			}
		}

	}


}
