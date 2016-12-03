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

package org.geogebra.common.kernel.commands;

import java.util.ArrayList;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoDispatcher;
import org.geogebra.common.kernel.algos.AlgoFunctionFreehand;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.Traversing.CommandFunctionReplacer;
import org.geogebra.common.kernel.arithmetic.Traversing.CommandReplacer;
import org.geogebra.common.kernel.arithmetic.Traversing.GeoDummyReplacer;
import org.geogebra.common.kernel.arithmetic.Traversing.Replacer;
import org.geogebra.common.kernel.arithmetic.Variable;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.Unicode;
import org.geogebra.common.util.debug.Log;

/**
 * Resolves arguments of the command, checks their validity and creates
 * resulting geos via appropriate Kernel methods
 */
public abstract class CommandProcessor {

	/** application */
	protected App app;
	/** localization */
	protected Localization loc;
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
		loc = app.getLocalization();
		algProcessor = kernel.getAlgebraProcessor();
	}

	/**
	 * Every CommandProcessor has to implement this method
	 * 
	 * @param c
	 *            command
	 * @return list of resulting geos
	 * @throws MyError
	 *             for wrong number / type of parameters
	 * @throws CircularDefinitionException
	 *             if circular definition occurs
	 */
	public GeoElement[] process(Command c)
			throws MyError, CircularDefinitionException {
		return process(c, null);
	}

	/**
	 * Every CommandProcessor has to implement this method
	 * 
	 * @param c
	 *            command
	 * @param info
	 *            flags for geo labeling
	 * @return list of resulting geos
	 * @throws MyError
	 *             for wrong number / type of parameters
	 * @throws CircularDefinitionException
	 *             if circular definition occurs
	 */
	public GeoElement[] process(Command c, EvalInfo info)
			throws MyError, CircularDefinitionException {
		return process(c);
	}

	/**
	 * Resolves arguments. When argument produces mor geos, only first is taken.
	 * 
	 * @param c
	 *            command
	 * @return array of arguments
	 * @throws MyError
	 *             if processing of some argument causes error (i.e. wrong
	 *             syntax of subcommand)
	 */
	protected final GeoElement[] resArgs(Command c) throws MyError {
		return resArgs(c, new EvalInfo(false));
	}
	
	/**
	 * Resolves arguments. When argument produces mor geos, only first is taken.
	 * 
	 * @param c
	 *            command
	 * @param info
	 *            context for evaluation -- labelling is overridden to false in
	 *            this method
	 * @return array of arguments
	 * @throws MyError
	 *             if processing of some argument causes error (i.e. wrong
	 *             syntax of subcommand)
	 */
	protected final GeoElement[] resArgs(Command c, EvalInfo info)
			throws MyError {

		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		// resolve arguments to get GeoElements
		ExpressionNode[] arg = c.getArguments();
		// name of replace variable of "x"/"y"
		EvalInfo argInfo = info.withLabels(false);
		String[] newXYZ = replaceXYarguments(arg);
		GeoElement[] result = new GeoElement[arg.length];

		for (int i = 0; i < arg.length; ++i) {
			// resolve variables in argument expression
			arg[i].resolveVariables(argInfo);

			// resolve i-th argument and get GeoElements
			// use only first resolved argument object for result
			result[i] = resArg(arg[i], argInfo)[0];
		}

		// remove added variables from construction
		if (newXYZ != null) {
			for (int i = 0; i < 3; i++) {
				cons.removeLocalVariable(newXYZ[i]);
			}
		}
		cons.setSuppressLabelCreation(oldMacroMode);
		return result;
	}

	/**
	 * @param arg
	 *            arguments
	 * @return local variable names
	 */
	protected String[] replaceXYarguments(ExpressionNode[] arg) {
		return null;
	}

	/**
	 * @param arg
	 *            arguments
	 * @param i
	 *            local variable position
	 * @param var
	 *            variable name
	 * @param subst
	 *            new variable name
	 * @param argsToCheck
	 *            number of arguments that need replacing
	 * @return new variable name (subst or subst + index if subst is used by
	 *         another object)
	 */
	protected String checkReplaced(ExpressionNode[] arg, int i, String var,
			String subst, int argsToCheck) {
		if (arg[i] != null && arg[i].unwrap() instanceof GeoNumeric
				&& ((GeoNumeric) arg[i].getLeft()).getLabelSimple() != null
				&& ((GeoNumeric) arg[i].getLeft()).getLabelSimple().equals(var)) {
			// get free variable to replace "x" with
			String newXVarStr = ((GeoElement) arg[i].getLeft())
					.getFreeLabel(subst);
			Variable newVar = new Variable(cons.getKernel(), newXVarStr);
			GeoNumeric gn = new GeoNumeric(cons);
			kernelA.getConstruction().addLocalVariable(newXVarStr, gn);
			GeoDummyReplacer replacer = GeoDummyReplacer.getReplacer(var,
					newVar, true);
			// replace "x" in expressions
			for (int j = 0; j < argsToCheck; j++) {
				arg[j].traverse(replacer);
			}

			arg[i].setLeft(gn);
			return newXVarStr;
		}
		return null;
	}

	/**
	 * @param c
	 *            command
	 * @param keepCAScells
	 *            false = replace CAS cells by twin geos, true = keep cells
	 * @param info
	 *            evaluation flags
	 * @return processed arguments
	 * @throws MyError
	 *             when arguments contain errors, eg. invalid operation in exp
	 *             node
	 */
	protected final GeoElement[] resArgs(Command c, boolean keepCAScells,
			EvalInfo info)
			throws MyError {
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		// resolve arguments to get GeoElements
		ExpressionNode[] arg = c.getArguments();
		GeoElement[] result = new GeoElement[arg.length];
		EvalInfo argInfo = info.withLabels(false);
		for (int i = 0; i < arg.length; ++i) {
			// resolve variables in argument expression
			arg[i].resolveVariables(argInfo);
			if (keepCAScells && arg[i].unwrap() instanceof GeoCasCell) {
				result[i] = (GeoElement) arg[i].unwrap();
			} else {

				// resolve i-th argument and get GeoElements
				// use only first resolved argument object for result
				result[i] = resArg(arg[i], argInfo)[0];

			}
		}

		cons.setSuppressLabelCreation(oldMacroMode);
		return result;
	}

	/**
	 * Resolves argument
	 * 
	 * @param arg
	 *            argument
	 * @param info
	 *            context for evaluation, labelling is overridden here
	 * @return array of arguments
	 * @throws MyError
	 *             if processing argument causes error (i.e. wrong syntax of
	 *             subcommand)
	 */
	protected final GeoElement[] resArg(ExpressionNode arg, EvalInfo info)
			throws MyError {
		GeoElement[] geos = algProcessor.processExpressionNode(arg,
				info.withLabels(false));
		if (geos != null) {
			return geos;
		}
		String[] str = { "IllegalArgument",
				arg.toString(StringTemplate.defaultTemplate) };
		throw new MyError(loc, str);
	}

	/**
	 * Resolve arguments of a command that has a local numeric variable at the
	 * position varPos. Initializes the variable with the NumberValue at
	 * initPos.
	 * 
	 * @param c
	 *            command
	 * @param varPos
	 *            position of variable
	 * @param initPos
	 *            position of initial value
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
			c.traverse(Replacer.getReplacer(kernelA.getImaginaryUnit(),
					localVar));
		}
		// Euler constant as local variable name
		else if (localVarName.equals(Unicode.EULER_STRING)) {
			// replace all imaginary unit objects in command arguments by a
			// variable "i"object
			localVarName = "e";
			Variable localVar = new Variable(kernelA, localVarName);
			c.traverse(Replacer.getReplacer(
kernelA.getEulerNumber(), localVar));
		}

		// add local variable name to construction
		Construction cmdCons = c.getKernel().getConstruction();
		GeoNumeric num = new GeoNumeric(cmdCons);
		cmdCons.addLocalVariable(localVarName, num);
		replaceZvarIfNeeded(localVarName, c, varPos);
		// initialize first value of local numeric variable from initPos
		if (initPos != varPos) {
			boolean oldval = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);
			NumberValue initValue = (NumberValue) resArg(c.getArgument(initPos),
					new EvalInfo(false))[0];
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

	private void replaceZvarIfNeeded(String name, Command c, int argsToReplace) {
		if (name.equals("z")) {
			// parse again to undo z*z -> Function
			try {
				for (int i = 0; i < argsToReplace; i++) {
					c.setArgument(
							i,
						kernelA.getParser()
								.parseGeoGebraExpression(
											c.getArgument(i).toString(
												StringTemplate.xmlTemplate))
								.wrap());
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * Resolves arguments, creates local variables and fills the vars and
	 * overlists
	 * 
	 * @param c
	 *            zip command
	 * @param vars
	 *            variables
	 * @param over
	 *            lists from which the vars should be taken
	 * @return list of arguments
	 */
	protected final GeoElement resArgsForZip(Command c, GeoElement[] vars,
			GeoList[] over) {
		// check if there is a local variable in arguments
		int numArgs = c.getArgumentNumber();

		Construction cmdCons = c.getKernel().getConstruction();
		EvalInfo argInfo = new EvalInfo(false);
		for (int varPos = 1; varPos < numArgs; varPos += 2) {
			String localVarName = c.getVariableName(varPos);
			if (localVarName == null
					&& c.getArgument(varPos).isTopLevelCommand()) {
				localVarName = c.getArgument(varPos).getTopLevelCommand()
						.getVariableName(0);
			}

			if (localVarName == null) {
				throw argErr(app, c.getName(), c.getArgument(varPos));
			}

			// add local variable name to construction

			GeoElement num = null;

			// initialize first value of local numeric variable from initPos

			GeoList gl = null;
			if (c.getArgumentNumber() > varPos + 1) {
				gl = (GeoList) resArg(c.getArgument(varPos + 1),
						argInfo)[0];
			}

			if (gl == null) {
				num = new GeoNumeric(cons);
			} else {
				num = gl.createTemplateElement();
			}

			cmdCons.addLocalVariable(localVarName, num);
			replaceZvarIfNeeded(localVarName, c, 1);
			// set local variable as our varPos argument
			c.setArgument(varPos, new ExpressionNode(c.getKernel(), num));
			vars[varPos / 2] = num.toGeoElement();
			if (gl != null) {
				over[varPos / 2] = gl;
			}
			// resolve all command arguments including the local variable just
			// created

			// remove local variable name from kernel again

		}
		GeoElement[] arg = resArg(
				c.getArgument(0)
						.traverse(CommandReplacer.getReplacer(kernelA,
								false))
						.wrap(),
				argInfo);

		return arg[0];
	}

	/**
	 * Resolve argument for Iteration command
	 * 
	 * @param c
	 *            command to use in the iteration
	 * @param vars
	 *            variables
	 * @param over
	 *            list from which the variables should be taken
	 * @param number
	 *            number of iterations
	 * @return arguments for Iteration(List)
	 */
	protected final GeoElement resArgsForIteration(Command c,
			GeoElement[] vars, GeoList[] over, GeoNumeric[] number) {
		// check if there is a local variable in arguments
		int numArgs = c.getArgumentNumber();

		Construction cmdCons = c.getKernel().getConstruction();
		EvalInfo argInfo = new EvalInfo(false);
		GeoElement geo = resArg(c.getArgument(numArgs - 2), argInfo)[0];
		if (geo != null && !(geo instanceof GeoList)) {
			throw argErr(app, c.getName(), c.getArgument(numArgs - 2));
		}
		GeoList gl = (GeoList) geo;
		GeoElement num = null;
		if (gl == null) {
			num = new GeoNumeric(cons);
		} else if (gl.size() == 0) {
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
		over[0] = gl;
		for (int varPos = 1; varPos < numArgs - 2; varPos += 1) {
			String localVarName = c.getVariableName(varPos);

			if (localVarName == null
					&& c.getArgument(varPos).isTopLevelCommand()) {
				localVarName = c.getArgument(varPos).getTopLevelCommand()
						.getVariableName(0);
			}

			if (localVarName == null) {
				throw argErr(app, c.getName(), c.getArgument(varPos));
			}

			// add local variable name to construction



			// initialize first value of local numeric variable from initPos




			cmdCons.addLocalVariable(localVarName, num);
			replaceZvarIfNeeded(localVarName, c, 1);
			// set local variable as our varPos argument
			c.setArgument(varPos, new ExpressionNode(c.getKernel(), num));
			vars[varPos - 1] = num.toGeoElement();
			// replace for Iteration[f(1/(1-x)),f,{x},21]
			if (!isCmdName(localVarName)) {
				c.getArgument(0)
					.traverse(
							CommandFunctionReplacer.getReplacer(app,
									localVarName, num));
			}
			if (varPos < numArgs - 3) {
				num = num.copy();
			}
			// resolve all command arguments including the local variable just
			// created

			// remove local variable name from kernel again

		}



		number[0] = (GeoNumeric) resArg(c.getArgument(numArgs - 1), argInfo)[0];

		GeoElement[] arg = resArg(c.getArgument(0), argInfo);

		return arg[0];
	}


	/**
	 * @param cmdName
	 *            command name
	 * @return whether such command exists
	 */
	public static boolean isCmdName(String cmdName) {
		Throwable t = null;
		try {
			Commands.valueOf(cmdName);
		} catch (Throwable t1) {
			t = t1;
		}
		return t == null;
	}

	/**
	 * Resolve arguments of a command that has a several local numeric variable
	 * at the position varPos. Initializes the variable with the NumberValue at
	 * initPos.
	 * 
	 * @param c
	 *            command
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
			// imaginary unit as local variable name
			else if (localVarName[i].equals(Unicode.IMAGINARY)) {
				// replace all imaginary unit objects in command arguments by a
				// variable "i"object
				localVarName[i] = "i";
				Variable localVar = new Variable(kernelA, localVarName[i]);
				c.traverse(Replacer.getReplacer(kernelA.getImaginaryUnit(),
						localVar));
			}
			// Euler constant as local variable name
			else if (localVarName[i].equals(Unicode.EULER_STRING)) {
				// replace all imaginary unit objects in command arguments by a
				// variable "i"object
				localVarName[i] = "e";
				Variable localVar = new Variable(kernelA, localVarName[i]);
				c.traverse(Replacer.getReplacer(kernelA.getEulerNumber(),
						localVar));
			}
		}

		// add local variable name to construction
		Construction cmdCons = c.getKernel().getConstruction();
		GeoNumeric[] num = new GeoNumeric[varPos.length];
		for (int i = 0; i < varPos.length; i++) {
			num[i] = new GeoNumeric(cmdCons);
			cmdCons.addLocalVariable(localVarName[i], num[i]);
			replaceZvarIfNeeded(localVarName[i], c, varPos[0]);
		}
		EvalInfo argInfo = new EvalInfo(false);
		// initialize first value of local numeric variable from initPos
		for (int i = 0; i < varPos.length; i++) {
			if (initPos[i] != varPos[i]) {
				boolean oldval = cons.isSuppressLabelsActive();
				cons.setSuppressLabelCreation(true);
				NumberValue initValue = (NumberValue) resArg(c
						.getArgument(initPos[i]), argInfo)[0];
				cons.setSuppressLabelCreation(oldval);
				num[i].setValue(initValue.getDouble());
			}
		}

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
	 * @param app1
	 *            application
	 * @param cmd
	 *            command name
	 * @param arg
	 *            faulty argument
	 * @return wrong argument error
	 */
	public final MyError argErr(App app1, String cmd, ExpressionValue arg) {
		return argErr(app1.getLocalization(), cmd, arg);

	}

	/**
	 * Creates wrong argument error
	 * 
	 * @param app1
	 *            application
	 * @param cmd
	 *            command name
	 * @param arg
	 *            faulty argument
	 * @return wrong argument error
	 */
	protected final MyError argErr(Localization app1, String cmd,
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
		} else {
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
		return new MyError(app1, sb.toString(), cmd, null);
	}

	/**
	 * Creates wrong parameter count error
	 * 
	 * @param app1
	 *            application
	 * @param cmd
	 *            command name
	 * @param argNumber
	 *            (-1 for just show syntax)
	 * @return wrong parameter count error
	 */
	protected final MyError argNumErr(App app1, String cmd, int argNumber) {
		if (sb == null)
			sb = new StringBuilder();
		else
			sb.setLength(0);
		getCommandSyntax(sb, app1.getLocalization(), cmd, argNumber);
		return new MyError(app1.getLocalization(), sb.toString(), cmd, null);
	}

	/**
	 * Copies error syntax into a StringBuilder
	 * 
	 * @param sb
	 *            string builder to store result
	 * @param app
	 *            application
	 * @param cmd
	 *            command name (internal)
	 * @param argNumber
	 *            (-1 for just show syntax)
	 */
	public static void getCommandSyntax(StringBuilder sb, Localization app,
			String cmd, int argNumber) {

		final boolean reverseOrder = app.isReverseNameDescriptionLanguage();
		if (!reverseOrder) {
			// standard order: "Command ..."
			sb.append(app.getCommand("Command"));
			sb.append(' ');
			sb.append(app.getCommand(cmd));
		} else {
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
	 * @param app1
	 *            application
	 * @param geo
	 *            dependent geo
	 * @return change dependent error
	 */
	final static MyError chDepErr(App app1, GeoElement geo) {
		String[] strs = { "ChangeDependent", geo.getLongDescription() };
		return new MyError(app1.getLocalization(), strs);
	}

	/**
	 * Returns bad argument (according to ok array) and throws error if no was
	 * found.
	 * 
	 * @param ok
	 *            array of "bad" flags
	 * @param arg
	 *            array of arguments
	 * @return bad argument
	 */
	protected static GeoElement getBadArg(boolean[] ok, GeoElement[] arg) {
		for (int i = 0; i < ok.length; i++) {
			if (!ok[i])
				return arg[i];
		}
		return arg[arg.length - 1];
	}

	/**
	 * Creates a dependent list with all GeoElement objects from the given
	 * array.
	 * 
	 * @param args
	 *            array of arguments
	 * @param type
	 *            -1 for any GeoElement object type; GeoElement.GEO_CLASS_ANGLE,
	 *            etc. for specific types
	 * @return null if GeoElement objects did not have the correct type
	 * @author Markus Hohenwarter
	 * @param kernel
	 *            kernel
	 * @param length
	 *            number of arguments
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
			list = kernel.getAlgoDispatcher().List(null, geoElementList, false);
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
	 * 
	 * @param kernelA
	 *            kernel
	 * @param algo
	 *            function's parent algorithm
	 * @return list of points on the function
	 */
	public static GeoList wrapFreehandFunctionArgInList(Kernel kernelA,
			AlgoFunctionFreehand algo) {

		Construction cons = kernelA.getConstruction();

		GeoList list = (GeoList) algo.getInput()[0];

		// first 2 points in list are start and end, rest are y-coordinates
		double start = ((NumberValue) list.get(0)).getDouble();
		double end = ((NumberValue) list.get(1)).getDouble();
		int size = list.size() - 2;

		double step = (end - start) / (size - 1);

		ArrayList<GeoElement> geoElementList = new ArrayList<GeoElement>();
		for (int i = 0; i < size; i++) {
			GeoPoint p = new GeoPoint(cons, start + i * step,
					((NumberValue) list.get(2 + i)).getDouble(), 1.0);
			geoElementList.add(p);
		}

		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);
		list = kernelA.getAlgoDispatcher().List(null, geoElementList, false);
		cons.setSuppressLabelCreation(oldMacroMode);

		return list;
	}

	/**
	 * Check if arg(i) depends on arg(j) and either throw an error or write
	 * warning see #2552
	 * 
	 * @param arg
	 *            arguments
	 * @param name
	 *            argument name
	 * @param i
	 *            index of possibly dependent argument
	 * @param j
	 *            index of independent argument
	 */
	protected void checkDependency(GeoElement[] arg, String name, int i, int j) {
		if (arg[i].isChildOrEqual(arg[j])) {
			if (kernelA.getConstruction().isFileLoading()) {
				// make sure old files can be loaded (and fixed)
				Log.warn("wrong dependency in " + name);
			} else {
				throw argErr(app, name, arg[i]);
			}
		}

	}

	/**
	 * @return algo dispatcher
	 */
	protected AlgoDispatcher getAlgoDispatcher() {
		return kernelA.getAlgoDispatcher();
	}

	/**
	 * Reduces the command to expression node or gives null if not possible
	 * 
	 * @param c
	 *            command
	 * @return command output
	 */
	public ExpressionValue simplify(Command c) {
		return null;
	}

	/**
	 * @param geo
	 *            problematic geo
	 * @param c
	 *            command
	 * @return throws error
	 */
	protected final MyError argErr(GeoElement geo, Command c) {
		return argErr(app, c.getName(), geo);
	}

	/**
	 * @param n
	 *            argument number
	 * @param c
	 *            command
	 * @return throws error
	 */
	protected final MyError argNumErr(int n, Command c) {
		return argNumErr(app, c.getName(), n);
	}

}
