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

package geogebra.kernel.commands;

import geogebra.euclidian.EuclidianController;
import geogebra.euclidian.EuclidianView;
import geogebra.euclidian.EuclidianViewInterface;
import geogebra.gui.GuiManager;
import geogebra.gui.RenameInputHandler;
import geogebra.gui.view.spreadsheet.SpreadsheetView;
import geogebra.kernel.AlgoCellRange;
import geogebra.kernel.AlgoDependentList;
import geogebra.kernel.AlgoDependentNumber;
import geogebra.kernel.CasEvaluableFunction;
import geogebra.kernel.CircularDefinitionException;
import geogebra.kernel.Construction;
import geogebra.kernel.Dilateable;
import geogebra.kernel.GeoAngle;
import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoButton;
import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoCurveCartesian;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoFunctionNVar;
import geogebra.kernel.GeoFunctionable;
import geogebra.kernel.GeoImage;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoLocus;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPolyLineInterface;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.GeoSegment;
import geogebra.kernel.GeoText;
import geogebra.kernel.GeoVec2D;
import geogebra.kernel.GeoVec3D;
import geogebra.kernel.GeoVector;
import geogebra.kernel.Kernel;
import geogebra.kernel.MyPoint;
import geogebra.kernel.Path;
import geogebra.kernel.Region;
import geogebra.kernel.Translateable;
import geogebra.kernel.arithmetic.BooleanValue;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.FunctionalNVar;
import geogebra.kernel.arithmetic.MySpecialDouble;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.arithmetic.Variable;
import geogebra.kernel.implicit.GeoImplicitPoly;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.statistics.SetRandomValue;
import geogebra.main.Application;
import geogebra.main.GeoGebraColorConstants;
import geogebra.main.MyError;
import geogebra.sound.SoundManager;
import geogebra.util.ImageManager;
import geogebra.util.Unicode;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Matcher;

/**
 * Resolves arguments of the command, checks their validity and creates
 * resulting geos via appropriate Kernel methods
 */
public abstract class CommandProcessor {

	/** application */
	protected Application app;
	/** kernel */
	protected Kernel kernel;
	/** construction */
	Construction cons;
	private AlgebraProcessor algProcessor;

	/**
	 * Creates new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CommandProcessor(Kernel kernel) {
		this.kernel = kernel;
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
	 * @throws MyError
	 * @throws CircularDefinitionException
	 */
	public abstract GeoElement[] process(Command c) throws MyError,
			CircularDefinitionException;

	/**
	 * Resolves arguments. When argument produces mor geos, only first is taken.
	 * 
	 * @param c
	 * @return array of arguments
	 * @throws MyError
	 */
	protected final GeoElement[] resArgs(Command c) throws MyError {
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		// resolve arguments to get GeoElements
		ExpressionNode[] arg = c.getArguments();
		GeoElement[] result = new GeoElement[arg.length];

		for (int i = 0; i < arg.length; ++i) {
			// resolve variables in argument expression
			arg[i].resolveVariables();

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
	 * @param arg
	 * @return array of arguments
	 * @throws MyError
	 */
	final GeoElement[] resArg(ExpressionNode arg) throws MyError {
		GeoElement[] geos = algProcessor.processExpressionNode(arg);

		if (geos != null)
			return geos;
		else {
			String[] str = { "IllegalArgument", arg.toString() };
			throw new MyError(app, str);
		}
	}

	/**
	 * Resolve arguments of a command that has a local numeric variable at the
	 * position varPos. Initializes the variable with the NumberValue at
	 * initPos.
	 * 
	 * @param c
	 * @param varPos
	 * @param initPos
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
			// replace all imaginary unit objects in command arguments by a variable "i"object
			localVarName = "i";
			Variable localVar = new Variable(kernel, localVarName);
			c.replace(kernel.getImaginaryUnit(), localVar);			
		}
		// Euler constant as local variable name
		else if (localVarName.equals(Unicode.EULER_STRING)) {
			// replace all imaginary unit objects in command arguments by a variable "i"object
			localVarName = "e";
			Variable localVar = new Variable(kernel, localVarName);
			c.replace(MySpecialDouble.getEulerConstant(kernel), localVar);
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
	 * Resolve arguments of a command that has a several local numeric variable
	 * at the position varPos. Initializes the variable with the NumberValue at
	 * initPos.
	 * 
	 * @param c
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
	 * @param app
	 * @param cmd
	 * @param arg
	 * @return wrong argument error
	 */
	protected final MyError argErr(Application app, String cmd, Object arg) {
		String localName = app.getCommand(cmd);
		if (sb == null)
			sb = new StringBuilder();
		else
			sb.setLength(0);
		sb.append(app.getCommand("Command"));
		sb.append(' ');
		sb.append(localName);
		sb.append(":\n");
		sb.append(app.getError("IllegalArgument"));
		sb.append(": ");
		if (arg instanceof GeoElement)
			sb.append(((GeoElement) arg).getNameDescription());
		else if (arg != null)
			sb.append(arg.toString());
		sb.append("\n\n");
		sb.append(app.getPlain("Syntax"));
		sb.append(":\n");
		sb.append(app.getCommandSyntax(cmd));
		return new MyError(app, sb.toString(), cmd);
	}

	/**
	 * Creates wrong parameter count error
	 * 
	 * @param app
	 * @param cmd
	 * @param argNumber
	 *            (-1 for just show syntax)
	 * @return wrong parameter count error
	 */
	protected final MyError argNumErr(Application app, String cmd, int argNumber) {
		if (sb == null)
			sb = new StringBuilder();
		else
			sb.setLength(0);
		getCommandSyntax(sb, app, cmd, argNumber);
		return new MyError(app, sb.toString(), cmd);
	}

	/**
	 * Copies error syntax into a StringBuilder
	 * 
	 * @param sb
	 * @param app
	 * @param cmd
	 * @param argNumber
	 *            (-1 for just show syntax)
	 */
	public static void getCommandSyntax(StringBuilder sb, Application app,
			String cmd, int argNumber) {
		sb.append(app.getCommand("Command"));
		sb.append(' ');
		sb.append(app.getCommand(cmd));
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
	 * @param app
	 * @param geo
	 * @return change dependent error
	 */
	final MyError chDepErr(Application app, GeoElement geo) {
		String[] strs = { "ChangeDependent", geo.getLongDescription() };
		return new MyError(app, strs);
	}

	/**
	 * Returns bad argument (according to ok array) and throws error if no was
	 * found.
	 * 
	 * @param ok
	 * @param arg
	 * @return bad argument
	 */
	protected static GeoElement getBadArg(boolean[] ok, GeoElement[] arg) {
		for (int i = 0; i < ok.length; i++) {
			if (!ok[i])
				return arg[i];
		}
		throw new Error("no bad arg");
	}

	/**
	 * Creates a dependent list with all GeoElement objects from the given
	 * array.
	 * 
	 * @param args
	 * @param type
	 *            -1 for any GeoElement object type; GeoElement.GEO_CLASS_ANGLE,
	 *            etc. for specific types
	 * @return null if GeoElement objects did not have the correct type
	 * @author Markus Hohenwarter
	 * @param kernel
	 * @param length
	 * @date Jan 26, 2008
	 */
	public static GeoList wrapInList(Kernel kernel, GeoElement[] args,
			int length, int type) {
		Construction cons = kernel.getConstruction();
		boolean correctType = true;
		ArrayList<GeoElement> geoElementList = new ArrayList<GeoElement>();
		for (int i = 0; i < length; i++) {
			if (type < 0 || args[i].getGeoClassType() == type)
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
}

/* *****************************************
 * Command classes used by CommandDispatcher
 * ****************************************
 */

/**
 * Center[ <GeoConic> ] Center[ <GeoPoint>, <GeoPoint> ]
 */
class CmdCenter extends CmdMidpoint {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCenter(Kernel kernel) {
		super(kernel);
	}
}



/**
 * LineBisector[ <GeoPoint>, <GeoPoint> ] LineBisector[ <GeoSegment> ]
 */
class CmdLineBisector extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdLineBisector(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1: // segment
			arg = resArgs(c);
			// line through point orthogonal to segment
			if (ok[0] = (arg[0].isGeoSegment())) {
				GeoElement[] ret = { kernel.LineBisector(c.getLabel(),
						(GeoSegment) arg[0]) };
				return ret;
			}

			// syntax error
			else
				throw argErr(app, "LineBisector", arg[0]);

		case 2: // two points
			arg = resArgs(c);

			// line through point orthogonal to vector
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))) {
				GeoElement[] ret = { kernel.LineBisector(c.getLabel(),
						(GeoPoint) arg[0], (GeoPoint) arg[1]) };
				return ret;
			}

			// syntax error
			else {
				if (!ok[0])
					throw argErr(app, "LineBisector", arg[0]);
				else
					throw argErr(app, "LineBisector", arg[1]);
			}

		default:
			throw argNumErr(app, "LineBisector", n);
		}
	}
}

/**
 * AngularBisector[ <GeoPoint>, <GeoPoint>, <GeoPoint> ] AngularBisector[
 * <GeoLine>, <GeoLine> ]
 */
class CmdAngularBisector extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdAngularBisector(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);

			// angular bisector of 2 lines
			if ((ok[0] = (arg[0].isGeoLine()))
					&& (ok[1] = (arg[1].isGeoLine())))
				return kernel.AngularBisector(c.getLabels(), (GeoLine) arg[0],
						(GeoLine) arg[1]);
			else {
				if (!ok[0])
					throw argErr(app, "AngularBisector", arg[0]);
				else
					throw argErr(app, "AngularBisector", arg[1]);
			}

		case 3:
			arg = resArgs(c);

			// angular bisector of three points
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))
					&& (ok[2] = (arg[2].isGeoPoint()))) {
				GeoElement[] ret = { kernel
						.AngularBisector(c.getLabel(), (GeoPoint) arg[0],
								(GeoPoint) arg[1], (GeoPoint) arg[2]) };
				return ret;
			}

			// syntax error
			else {
				if (!ok[0])
					throw argErr(app, "AngularBisector", arg[0]);
				else if (!ok[1])
					throw argErr(app, "AngularBisector", arg[1]);
				else
					throw argErr(app, "AngularBisector", arg[2]);
			}

		default:
			throw argNumErr(app, "AngularBisector", n);
		}
	}
}

/**
 * Direction[ <GeoLine> ]
 */
class CmdDirection extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdDirection(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (ok[0] = (arg[0].isGeoLine())) {
				GeoElement[] ret = { kernel.Direction(c.getLabel(),
						(GeoLine) arg[0]) };
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, "Direction", arg[0]);
			}

		default:
			throw argNumErr(app, "Direction", n);
		}
	}
}

/**
 * Slope[ <GeoLine> ] Slope[ <GeoFunction> ]
 */
class CmdSlope extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSlope(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoLine()) {
				GeoElement[] ret = { kernel.Slope(c.getLabel(),
						(GeoLine) arg[0]) };
				return ret;
			} else
				throw argErr(app, "Slope", arg[0]);

		default:
			throw argNumErr(app, "Slope", n);
		}
	}
}

/**
 * UnitVector[ <GeoLine> ] UnitVector[ <GeoVector> ]
 */
class CmdUnitVector extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdUnitVector(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (ok[0] = (arg[0].isGeoLine())) {
				GeoElement[] ret = { kernel.UnitVector(c.getLabel(),
						(GeoLine) arg[0]) };
				return ret;
			} else if (ok[0] = (arg[0].isGeoVector())) {
				GeoElement[] ret = { kernel.UnitVector(c.getLabel(),
						(GeoVector) arg[0]) };
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, "UnitVector", arg[0]);
			}

		default:
			throw argNumErr(app, "UnitVector", n);
		}
	}
}



/**
 * Length[ <GeoVector> ] Length[ <GeoPoint> ] Length[ <GeoList> ] Victor Franco
 * 18-04-2007: add Length[ <Function>, <Number>, <Number> ] add Length[
 * <Function>, <Point>, <Point> ] add Length[ <Curve>, <Number>, <Number> ] add
 * Length[ <Curve>, <Point>, <Point> ]
 */
class CmdLength extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdLength(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoVector() || arg[0].isGeoPoint()) {
				GeoElement[] ret = { kernel.Length(c.getLabel(),
						(GeoVec3D) arg[0]) };
				return ret;
			} else if (arg[0].isGeoList()) {
				GeoElement[] ret = { kernel.Length(c.getLabel(),
						(GeoList) arg[0]) };
				return ret;
			} else if (arg[0].isGeoText()) {
				GeoElement[] ret = { kernel.Length(c.getLabel(),
						(GeoText) arg[0]) };
				return ret;
			} else if (arg[0].isGeoLocus()) {
				GeoElement[] ret = { kernel.Length(c.getLabel(),
						(GeoLocus) arg[0]) };
				return ret;
			} else {
				throw argErr(app, c.getName(), arg[0]);
			}

			// Victor Franco 18-04-2007
		case 3:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoFunctionable()))
					&& (ok[1] = (arg[1].isGeoNumeric()))
					&& (ok[2] = (arg[2].isGeoNumeric()))) {
				GeoElement[] ret = { kernel.FunctionLength(c.getLabel(),
						(GeoFunction) arg[0], (GeoNumeric) arg[1],
						(GeoNumeric) arg[2]) };
				return ret;
			}

			else if ((ok[0] = (arg[0].isGeoFunctionable()))
					&& (ok[1] = (arg[1].isGeoPoint()))
					&& (ok[2] = (arg[2].isGeoPoint()))) {

				GeoElement[] ret = { kernel.FunctionLength2Points(c.getLabel(),
						(GeoFunction) arg[0], (GeoPoint) arg[1],
						(GeoPoint) arg[2]) };
				return ret;
			}

			else if ((ok[0] = (arg[0].isGeoCurveCartesian()))
					&& (ok[1] = (arg[1].isGeoNumeric()))
					&& (ok[2] = (arg[2].isGeoNumeric()))) {

				GeoElement[] ret = { kernel.CurveLength(c.getLabel(),
						(GeoCurveCartesian) arg[0], (GeoNumeric) arg[1],
						(GeoNumeric) arg[2]) };
				return ret;

			}

			else if ((ok[0] = (arg[0].isGeoCurveCartesian()))
					&& (ok[1] = (arg[1].isGeoPoint()))
					&& (ok[2] = (arg[2].isGeoPoint()))) {

				GeoElement[] ret = { kernel.CurveLength2Points(c.getLabel(),
						(GeoCurveCartesian) arg[0], (GeoPoint) arg[1],
						(GeoPoint) arg[2]) };
				return ret;
			}

			else {

				if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				if (!ok[1])
					throw argErr(app, c.getName(), arg[1]);
				else
					throw argErr(app, c.getName(), arg[2]);
			}

			// Victor Franco 18-04-2007 (end)
		default:
			throw argNumErr(app, "Length", n);
		}
	}
}

/**
 * Distance[ <GeoPoint>, <GeoPoint> ] Distance[ <GeoPoint>, <GeoLine> ]
 * Distance[ <GeoLine>, <GeoPoint> ] Distance[ <GeoLine>, <GeoLine> ]
 */
class CmdDistance extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdDistance(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);

			// distance between two points
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))) {
				GeoElement[] ret = { kernel.Distance(c.getLabel(),
						(GeoPointND) arg[0], (GeoPointND) arg[1]) };
				return ret;
			}

			// distance between point and line
			else if (arg[0].isGeoPoint()) {
				GeoElement[] ret = { kernel.Distance(c.getLabel(),
						(GeoPoint) arg[0], arg[1]) };
				return ret;
			}

			// distance between line and point
			else if (arg[1].isGeoPoint()) {
				GeoElement[] ret = { kernel.Distance(c.getLabel(),
						(GeoPoint) arg[1], arg[0]) };
				return ret;
			}

			// distance between line and line
			else if ((ok[0] = (arg[0].isGeoLine()))
					&& (ok[1] = (arg[1].isGeoLine()))) {
				GeoElement[] ret = { kernel.Distance(c.getLabel(),
						(GeoLine) arg[0], (GeoLine) arg[1]) };
				return ret;
			}

			// syntax error
			else {
				if (ok[0] && !ok[1])
					throw argErr(app, "Distance", arg[1]);
				else
					throw argErr(app, "Distance", arg[0]);
			}

		default:
			throw argNumErr(app, "Distance", n);
		}
	}
}

/**
 * ClosestPoint[Point,Path] ClosestPoint[Path,Point]
 */
class CmdClosestPoint extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdClosestPoint(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);

			// distance between two points
			if ((ok[0] = (arg[0] instanceof Path))
					&& (ok[1] = (arg[1].isGeoPoint()))) {
				GeoElement[] ret = { kernel.ClosestPoint(c.getLabel(),
						(Path) arg[0], (GeoPoint) arg[1]) };
				return ret;
			}

			// distance between point and line
			else if ((ok[1] = (arg[1] instanceof Path))
					&& (ok[0] = (arg[0].isGeoPoint()))) {
				GeoElement[] ret = { kernel.ClosestPoint(c.getLabel(),
						(Path) arg[1], (GeoPoint) arg[0]) };
				return ret;
			}

			// syntax error
			else {
				if (ok[0] && !ok[1])
					throw argErr(app, c.getName(), arg[1]);
				else
					throw argErr(app, c.getName(), arg[0]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 * Angle[ number ] Angle[ <GeoPolygon> ] Angle[ <GeoConic> ] Angle[ <GeoVector>
 * ] Angle[ <GeoPoint> ] Angle[ <GeoVector>, <GeoVector> ] Angle[ <GeoLine>,
 * <GeoLine> ] Angle[ <GeoPoint>, <GeoPoint>, <GeoPoint> ] Angle[ <GeoPoint>,
 * <GeoPoint>, <Number> ]
 */
class CmdAngle extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdAngle(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		/**
		 * // Anlge[ constant number ] // get number value ExpressionNode en =
		 * null; ExpressionValue eval; double value = 0.0; // check if we got
		 * number: // ExpressionNode && NumberValue || Assignment // build
		 * ExpressionNode from one of these cases ok[0] = false; Object ob =
		 * c.getArgument(0); if (ob instanceof ExpressionNode) { en =
		 * (ExpressionNode) ob; eval = en.evaluate(); if (eval .isNumberValue()
		 * && !(eval .isGeoPolygon())) { value = ((NumberValue)
		 * eval).getDouble(); ok[0] = true; } } else if (ob instanceof
		 * Assignment) { GeoElement geo = cons.lookupLabel(((Assignment)
		 * ob).getVariable()); if (geo .isGeoNumeric()) { // wrap GeoNumeric int
		 * ExpressionNode for // kernel.DependentNumer() en = new
		 * ExpressionNode(kernel, (NumberValue) geo,
		 * ExpressionNode.NO_OPERATION, null); ok[0] = true; } }
		 */
		case 1:
			arg = resArgs(c);

			// wrap angle as angle (needed to avoid ambiguities between numbers
			// and angles in XML)
			if (arg[0].isGeoAngle()) {
				// maybe we have to set a label here
				if (!cons.isSuppressLabelsActive() && !arg[0].isLabelSet()) {
					arg[0].setLabel(c.getLabel());

					// make sure that arg[0] is in construction list
					if (arg[0].isIndependent())
						cons.addToConstructionList(arg[0], true);
					else
						cons.addToConstructionList(arg[0].getParentAlgorithm(),
								true);
				}
				GeoElement[] ret = { arg[0] };
				return ret;
			}
			// angle from number
			else if (arg[0].isGeoNumeric()) {
				GeoElement[] ret = { kernel.Angle(c.getLabel(),
						(GeoNumeric) arg[0]) };
				return ret;
			}
			// angle from number
			else if (arg[0].isGeoPoint() || arg[0].isGeoVector()) {
				GeoElement[] ret = { kernel.Angle(c.getLabel(),
						(GeoVec3D) arg[0]) };
				return ret;
			}
			// angle of conic or polygon
			else {
				if (arg[0].isGeoConic()) {
					GeoElement[] ret = { kernel.Angle(c.getLabel(),
							(GeoConic) arg[0]) };
					return ret;
				} else if (arg[0].isGeoPolygon())
					return kernel.Angles(c.getLabels(), (GeoPolygon) arg[0]);
			}

			throw argErr(app, "Angle", arg[0]);

		case 2:
			arg = resArgs(c);

			// angle between vectors
			if ((ok[0] = (arg[0].isGeoVector()))
					&& (ok[1] = (arg[1].isGeoVector()))) {
				GeoElement[] ret = { kernel.Angle(c.getLabel(),
						(GeoVector) arg[0], (GeoVector) arg[1]) };
				return ret;
			}
			// angle between lines
			else if ((ok[0] = (arg[0].isGeoLine()))
					&& (ok[1] = (arg[1].isGeoLine()))) {
				GeoElement[] ret = { kernel.Angle(c.getLabel(),
						(GeoLine) arg[0], (GeoLine) arg[1]) };
				return ret;
			}
			// syntax error
			else {
				if (ok[0] && !ok[1])
					throw argErr(app, "Angle", arg[1]);
				else
					throw argErr(app, "Angle", arg[0]);
			}

		case 3:
			arg = resArgs(c);

			// angle between three points
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))
					&& (ok[2] = (arg[2].isGeoPoint()))) {
				GeoElement[] ret = { kernel
						.Angle(c.getLabel(), (GeoPoint) arg[0],
								(GeoPoint) arg[1], (GeoPoint) arg[2]) };
				return ret;
			}
			// fixed angle
			else if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))
					&& (ok[2] = (arg[2].isNumberValue())))
				return kernel.Angle(c.getLabels(), (GeoPoint) arg[0],
						(GeoPoint) arg[1], (NumberValue) arg[2]);
			else
				throw argErr(app, "Angle", arg[0]);

		default:
			throw argNumErr(app, "Angle", n);
		}
	}
}

class CmdKimberling extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdKimberling(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 4:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isGeoPoint()) &&
					(ok[1] = arg[1].isGeoPoint()) &&
					(ok[2] = arg[2].isGeoPoint()) &&
					(ok[3] = arg[3].isNumberValue())) {
				GeoElement[] ret = { kernel.Kimberling(c.getLabel(),
						(GeoPoint)arg[0], (GeoPoint)arg[1], (GeoPoint)arg[2],
						(NumberValue) arg[3])} ;
				return ret;
				
			} else{
				if(!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				if(!ok[1])
					throw argErr(app, c.getName(), arg[1]);
				if(!ok[2])
					throw argErr(app, c.getName(), arg[2]);
				throw argErr(app, c.getName(), arg[3]);
			}
		default:
			throw argNumErr(app, "Centroid", n);
		}
	}
}
/**
 * Area[ <GeoPoint>, ..., <GeoPoint> ] Area[ <GeoConic> ] Area[ <Polygon> ]
 * (returns Polygon directly)
 */
class CmdArea extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdArea(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		if (n == 1) {
			arg = resArgs(c);

			// area of conic
			if (arg[0].isGeoConic()) {
				GeoElement[] ret = { kernel.Area(c.getLabel(),
						(GeoConic) arg[0]) };
				return ret;
			}
			// area of polygon = polygon variable
			else if (arg[0].isGeoPolygon()) {
				GeoElement[] ret = { arg[0] };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[0]);
		}

		// area of points
		else if (n > 2) {
			arg = resArgs(c);
			GeoPoint[] points = new GeoPoint[n];
			// check arguments
			for (int i = 0; i < n; i++) {
				if (!(arg[i].isGeoPoint()))
					throw argErr(app, "Area", arg[i]);
				else {
					points[i] = (GeoPoint) arg[i];
				}
			}
			// everything ok
			GeoElement[] ret = { kernel.Area(c.getLabel(), points) };
			return ret;
		} else
			throw argNumErr(app, "Area", n);
	}
}

/**
 * Focus[ <GeoConic> ]
 */
class CmdFocus extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFocus(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (ok[0] = (arg[0].isGeoConic()))
				return kernel.Focus(c.getLabels(), (GeoConic) arg[0]);
			else
				throw argErr(app, "Focus", arg[0]);

		default:
			throw argNumErr(app, "Focus", n);
		}
	}
}

/**
 * Vertex[ <GeoConic> ]
 */
class CmdVertex extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdVertex(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		// Vertex[ <GeoConic> ]
		case 1:
			arg = resArgs(c);
			if (ok[0] = (arg[0].isGeoConic()))
				return kernel.Vertex(c.getLabels(), (GeoConic) arg[0]);
			if (ok[0] = (arg[0] instanceof GeoPolyLineInterface))
				return kernel.Vertex(c.getLabels(), (GeoPolyLineInterface) arg[0]);
			else if (ok[0] = (arg[0].isNumberValue())) {
				GeoElement[] ret = { kernel.CornerOfDrawingPad(c.getLabel(),
						(NumberValue) arg[0], null) };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[0]);

			// Corner[ <Image>, <number> ]
		case 2:
			arg = resArgs(c);
			if ((ok[0] = (arg[0] instanceof GeoPolyLineInterface))
					&& (ok[1] = (arg[1].isNumberValue()))) {
				GeoElement[] ret = { kernel.Vertex(c.getLabel(),
						(GeoPolyLineInterface) arg[0], (NumberValue) arg[1]) };
				return ret;
			} else if ((ok[0] = (arg[0].isGeoImage()))
					&& (ok[1] = (arg[1].isNumberValue()))) {
				GeoElement[] ret = { kernel.Corner(c.getLabel(),
						(GeoImage) arg[0], (NumberValue) arg[1]) };
				return ret;
			}
			// Michael Borcherds 2007-11-26 BEGIN Corner[] for textboxes
			// Corner[ <Text>, <number> ]
			else if ((ok[0] = (arg[0].isGeoText()))
					&& (ok[1] = (arg[1].isNumberValue()))) {
				GeoElement[] ret = { kernel.Corner(c.getLabel(),
						(GeoText) arg[0], (NumberValue) arg[1]) };
				return ret;
				// Michael Borcherds 2007-11-26 END
			} else if ((ok[0] = (arg[0].isNumberValue()))
					&& (ok[1] = (arg[1].isNumberValue()))) {
				GeoElement[] ret = { kernel.CornerOfDrawingPad(c.getLabel(),
						(NumberValue) arg[1], (NumberValue) arg[0]) };
				return ret;
				
			} else {
				if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				else
					throw argErr(app, c.getName(), arg[1]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 * Corner[ <Image>, <number> ]
 */
class CmdCorner extends CmdVertex {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCorner(Kernel kernel) {
		super(kernel);
	}

}

/**
 * Semicircle[ <GeoPoint>, <GeoPoint> ]
 */
class CmdSemicircle extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSemicircle(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))) {
				GeoElement[] ret = { kernel.Semicircle(c.getLabel(),
						(GeoPoint) arg[0], (GeoPoint) arg[1]) };
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, "Semicircle", arg[0]);
				else
					throw argErr(app, "Semicircle", arg[1]);
			}

		default:
			throw argNumErr(app, "Semicircle", n);
		}
	}
}

/**
 * Locus[ <GeoPoint Q>, <GeoPoint P> ]
 * or
 * Locus[ <GeoPoint Q>, <GeoNumeric P> ]
 */
class CmdLocus extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdLocus(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			// second argument has to be point on path
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))) {
				
				GeoPoint p1 = (GeoPoint) arg[0];
				GeoPoint p2 = (GeoPoint) arg[1];
				
				if (p2.isPointOnPath()) {
				
				GeoElement[] ret = { kernel.Locus(c.getLabel(),
						p1, p2) };
				return ret;
				} else {
					GeoElement[] ret = { kernel.Locus(c.getLabel(),
							p2, p1) };
					return ret;
					
				}
			} else if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoNumeric()))) {
				GeoPoint p1 = (GeoPoint) arg[0];
				GeoNumeric p2 = (GeoNumeric) arg[1];
				
				GeoElement[] ret = { kernel.Locus(c.getLabel(),
						p1, p2) };
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, "Locus", arg[0]);
				else
					throw argErr(app, "Locus", arg[1]);
			}

		default:
			throw argNumErr(app, "Locus", n);
		}
	}
}

/**
 * Arc[ <GeoConic>, <Number>, <Number> ] Arc[ <GeoConic>, <GeoPoint>, <GeoPoint>
 * ]
 */
class CmdArc extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdArc(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 3:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoConic()))
					&& (ok[1] = (arg[1].isNumberValue()))
					&& (ok[2] = (arg[2].isNumberValue()))) {
				GeoElement[] ret = { kernel.ConicArc(c.getLabel(),
						(GeoConic) arg[0], (NumberValue) arg[1],
						(NumberValue) arg[2]) };
				return ret;
			} else if ((ok[0] = (arg[0].isGeoConic()))
					&& (ok[1] = (arg[1].isGeoPoint()))
					&& (ok[2] = (arg[2].isGeoPoint()))) {
				GeoElement[] ret = { kernel
						.ConicArc(c.getLabel(), (GeoConic) arg[0],
								(GeoPoint) arg[1], (GeoPoint) arg[2]) };
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, "Arc", arg[0]);
				else if (!ok[1])
					throw argErr(app, "Arc", arg[1]);
				else
					throw argErr(app, "Arc", arg[2]);
			}

		default:
			throw argNumErr(app, "Arc", n);
		}
	}
}

/**
 * Sector[ <GeoConic>, <Number>, <Number> ] Sector[ <GeoConic>, <GeoPoint>,
 * <GeoPoint> ]
 */
class CmdSector extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSector(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 3:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoConic()))
					&& (ok[1] = (arg[1].isNumberValue()))
					&& (ok[2] = (arg[2].isNumberValue()))) {
				GeoElement[] ret = { kernel.ConicSector(c.getLabel(),
						(GeoConic) arg[0], (NumberValue) arg[1],
						(NumberValue) arg[2]) };
				return ret;
			} else if ((ok[0] = (arg[0].isGeoConic()))
					&& (ok[1] = (arg[1].isGeoPoint()))
					&& (ok[2] = (arg[2].isGeoPoint()))) {
				GeoElement[] ret = { kernel
						.ConicSector(c.getLabel(), (GeoConic) arg[0],
								(GeoPoint) arg[1], (GeoPoint) arg[2]) };
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, "Sector", arg[0]);
				else if (!ok[1])
					throw argErr(app, "Sector", arg[1]);
				else
					throw argErr(app, "Sector", arg[2]);
			}

		default:
			throw argNumErr(app, "Sector", n);
		}
	}
}

/**
 * CircleArc[ <GeoPoint center>, <GeoPoint>, <GeoPoint> ]
 */
class CmdCircleArc extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCircleArc(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 3:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))
					&& (ok[2] = (arg[2].isGeoPoint()))) {
				GeoElement[] ret = { kernel
						.CircleArc(c.getLabel(), (GeoPoint) arg[0],
								(GeoPoint) arg[1], (GeoPoint) arg[2]) };
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				else if (!ok[1])
					throw argErr(app, c.getName(), arg[1]);
				else
					throw argErr(app, c.getName(), arg[2]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 * CircleSector[ <GeoPoint center>, <GeoPoint>, <GeoPoint> ]
 */
class CmdCircleSector extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCircleSector(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 3:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))
					&& (ok[2] = (arg[2].isGeoPoint()))) {
				GeoElement[] ret = { kernel
						.CircleSector(c.getLabel(), (GeoPoint) arg[0],
								(GeoPoint) arg[1], (GeoPoint) arg[2]) };
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				else if (!ok[1])
					throw argErr(app, c.getName(), arg[1]);
				else
					throw argErr(app, c.getName(), arg[2]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 * CircumcircleArc[ <GeoPoint center>, <GeoPoint>, <GeoPoint> ]
 */
class CmdCircumcircleArc extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCircumcircleArc(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 3:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))
					&& (ok[2] = (arg[2].isGeoPoint()))) {
				GeoElement[] ret = { kernel
						.CircumcircleArc(c.getLabel(), (GeoPoint) arg[0],
								(GeoPoint) arg[1], (GeoPoint) arg[2]) };
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				else if (!ok[1])
					throw argErr(app, c.getName(), arg[1]);
				else
					throw argErr(app, c.getName(), arg[2]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 * CircumcircleSector[ <GeoPoint center>, <GeoPoint>, <GeoPoint> ]
 */
class CmdCircumcircleSector extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCircumcircleSector(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 3:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))
					&& (ok[2] = (arg[2].isGeoPoint()))) {
				GeoElement[] ret = { kernel
						.CircumcircleSector(c.getLabel(), (GeoPoint) arg[0],
								(GeoPoint) arg[1], (GeoPoint) arg[2]) };
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				else if (!ok[1])
					throw argErr(app, c.getName(), arg[1]);
				else
					throw argErr(app, c.getName(), arg[2]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 * Parabola[ <GeoPoint>, <GeoLine> ]
 */
class CmdParabola extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdParabola(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoLine()))) {
				GeoElement[] ret = { kernel.Parabola(c.getLabel(),
						(GeoPoint) arg[0], (GeoLine) arg[1]) };
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, "Parabola", arg[0]);
				else
					throw argErr(app, "Parabola", arg[1]);
			}

		default:
			throw argNumErr(app, "Parabola", n);
		}
	}
}

/**
 * Div[ a, b ]
 */
class CmdDiv extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdDiv(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isNumberValue()))
					&& (ok[1] = (arg[1].isNumberValue()))) {
				GeoElement[] ret = { kernel.Div(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1]) };
				return ret;
			} else if ((ok[0] = (arg[0].isGeoFunction()))
					&& (ok[1] = (arg[1].isGeoFunction()))) {
				GeoElement[] ret = { kernel.Div(c.getLabel(),
						(GeoFunction) arg[0], (GeoFunction) arg[1]) };
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, "Div", arg[0]);
				else
					throw argErr(app, "Div", arg[1]);
			}

		default:
			throw argNumErr(app, "Div", n);
		}
	}
}

/**
 * Mod[a, b]
 */
class CmdMod extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdMod(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isNumberValue()))
					&& (ok[1] = (arg[1].isNumberValue()))) {
				GeoElement[] ret = { kernel.Mod(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1]) };
				return ret;
			} else if ((ok[0] = (arg[0].isGeoFunction()))
					&& (ok[1] = (arg[1].isGeoFunction()))) {
				GeoElement[] ret = { kernel.Mod(c.getLabel(),
						(GeoFunction) arg[0], (GeoFunction) arg[1]) };
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, "Mod", arg[0]);
				else
					throw argErr(app, "Mod", arg[1]);
			}

		default:
			throw argNumErr(app, "Mod", n);
		}
	}
}

/**
 * Ellipse[ <GeoPoint>, <GeoPoint>, <NumberValue> ]
 */
class CmdEllipse extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdEllipse(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 3:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))
					&& (ok[2] = (arg[2].isNumberValue()))) {
				GeoElement[] ret = { kernel.Ellipse(c.getLabel(),
						(GeoPoint) arg[0], (GeoPoint) arg[1],
						(NumberValue) arg[2]) };
				return ret;
			} else if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))
					&& (ok[2] = (arg[2].isGeoPoint()))) {
				GeoElement[] ret = { kernel
						.Ellipse(c.getLabel(), (GeoPoint) arg[0],
								(GeoPoint) arg[1], (GeoPoint) arg[2]) };
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, "Ellipse", arg[0]);
				else
					throw argErr(app, "Ellipse", arg[1]);
			}

		default:
			throw argNumErr(app, "Ellipse", n);
		}
	}
}

/**
 * Hyperbola[ <GeoPoint>, <GeoPoint>, <NumberValue> ]
 */
class CmdHyperbola extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdHyperbola(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 3:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))
					&& (ok[2] = (arg[2].isNumberValue()))) {
				GeoElement[] ret = { kernel.Hyperbola(c.getLabel(),
						(GeoPoint) arg[0], (GeoPoint) arg[1],
						(NumberValue) arg[2]) };
				return ret;
			} else if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))
					&& (ok[2] = (arg[2].isGeoPoint()))) {
				GeoElement[] ret = { kernel
						.Hyperbola(c.getLabel(), (GeoPoint) arg[0],
								(GeoPoint) arg[1], (GeoPoint) arg[2]) };
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, "Hyperbola", arg[0]);
				else
					throw argErr(app, "Hyperbola", arg[1]);
			}

		default:
			throw argNumErr(app, "Hyperbola", n);
		}
	}
}

/**
 * Conic[ <List> ]
 * Conic[ five GeoPoints ]
 */
class CmdConic extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdConic(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg = resArgs(c);
		switch (n) {
		case 1:
			if (arg[0].isGeoList())
				return kernel.Conic(c.getLabel(), (GeoList) arg[0]);
		case 5:
			for (int i=0;i<5;i++){
				if (!arg[i].isGeoPoint()){
					throw argErr(app,"Conic",arg[i]);
				}
			}
			GeoPoint[] points = { (GeoPoint) arg[0], (GeoPoint) arg[1],
					(GeoPoint) arg[2], (GeoPoint) arg[3], (GeoPoint) arg[4] };
			GeoElement[] ret = { kernel.Conic(c.getLabel(), points) };
			return ret;
		default:
			if (arg[0].isNumberValue()) {
				// try to create list of numbers
				GeoList list = wrapInList(kernel, arg, arg.length,
						GeoElement.GEO_CLASS_NUMERIC);
				if (list != null) {
					ret = kernel.Conic(c.getLabel(), list);
					return ret;
				}
			}
			throw argNumErr(app, "Conic", n);
		}
	}
}

/**
 * Polar[ <GeoPoint>, <GeoConic> ]
 */
class CmdPolar extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdPolar(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);

			// polar line to point relative to conic
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoConic()))) {
				GeoElement[] ret = { kernel.PolarLine(c.getLabel(),
						(GeoPoint) arg[0], (GeoConic) arg[1]) };
				return ret;
			}

			// syntax error
			else {
				if (!ok[0])
					throw argErr(app, "Polar", arg[0]);
				else
					throw argErr(app, "Polar", arg[1]);
			}

		default:
			throw argNumErr(app, "Polar", n);
		}
	}
}

/**
 * Diameter[ <GeoVector>, <GeoConic> ] Diameter[ <GeoLine>, <GeoConic> ]
 */
class CmdDiameter extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdDiameter(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);

			// diameter line conjugate to vector relative to conic
			if ((ok[0] = (arg[0].isGeoVector()))
					&& (ok[1] = (arg[1].isGeoConic()))) {
				GeoElement[] ret = { kernel.DiameterLine(c.getLabel(),
						(GeoVector) arg[0], (GeoConic) arg[1]) };
				return ret;
			}

			// diameter line conjugate to line relative to conic
			if ((ok[0] = (arg[0].isGeoLine()))
					&& (ok[1] = (arg[1].isGeoConic()))) {
				GeoElement[] ret = { kernel.DiameterLine(c.getLabel(),
						(GeoLine) arg[0], (GeoConic) arg[1]) };
				return ret;
			}

			// syntax error
			else {
				if (!ok[0])
					throw argErr(app, "Diameter", arg[0]);
				else
					throw argErr(app, "Diameter", arg[1]);
			}

		default:
			throw argNumErr(app, "Diameter", n);
		}
	}
}

/**
 * Tangent[ <GeoPoint>, <GeoConic> ] Tangent[ <GeoLine>, <GeoConic> ] Tangent[
 * <NumberValue>, <GeoFunction> ] Tangent[ <GeoPoint>, <GeoFunction> ] Tangent[
 * <GeoPoint>, <GeoCurveCartesian> ] Tangent[<GeoPoint>,<GeoImplicitPoly>]
 * Tangent[ <GeoLine>, <GeoImplicitPoly>]
 */
class CmdTangent extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdTangent(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);

			// tangents through point
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoConic())))
				return kernel.Tangent(c.getLabels(), (GeoPoint) arg[0],
						(GeoConic) arg[1]);
			else if ((ok[0] = (arg[0].isGeoConic()))
					&& (ok[1] = (arg[1].isGeoPoint())))
				return kernel.Tangent(c.getLabels(), (GeoPoint) arg[1],
						(GeoConic) arg[0]);
			else if ((ok[0] = (arg[0].isGeoLine()))
					&& (ok[1] = (arg[1].isGeoConic())))
				return kernel.Tangent(c.getLabels(), (GeoLine) arg[0],
						(GeoConic) arg[1]);
			else if ((ok[0] = (arg[0].isNumberValue()))
					&& (ok[1] = (arg[1].isGeoFunctionable()))) {
				GeoElement[] ret = { kernel.Tangent(c.getLabel(),
						(NumberValue) arg[0], ((GeoFunctionable) arg[1])
								.getGeoFunction()) };
				return ret;
			}

			// tangents of function at x = x(Point P)
			else if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoFunctionable()))) {
				GeoElement[] ret = { kernel.Tangent(c.getLabel(),
						(GeoPoint) arg[0], ((GeoFunctionable) arg[1])
								.getGeoFunction()) };
				return ret;
			} else if ((ok[0] = (arg[0].isGeoFunctionable()))
					&& (ok[1] = (arg[1].isGeoPoint()))) {
				GeoElement[] ret = { kernel.Tangent(c.getLabel(),
						(GeoPoint) arg[1], ((GeoFunctionable) arg[0])
								.getGeoFunction()) };
				return ret;
			}
			// Victor Franco 11-02-2007: for curve's
			else if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoCurveCartesian()))) {

				GeoElement[] ret = { kernel.Tangent(c.getLabel(),
						(GeoPoint) arg[0], (GeoCurveCartesian) arg[1]) };
				return ret;
			}
			// Victor Franco 11-02-2007: end for curve's
			else if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoImplicitPoly()))) {
				GeoElement[] ret = kernel.Tangent(c.getLabels(),
						(GeoPoint) arg[0], (GeoImplicitPoly) arg[1]);
				return ret;
			} else if ((ok[1] = (arg[1].isGeoPoint()))
					&& (ok[0] = (arg[0].isGeoImplicitPoly()))) {
				GeoElement[] ret = kernel.Tangent(c.getLabels(),
						(GeoPoint) arg[1], (GeoImplicitPoly) arg[0]);
				return ret;
			} else if ((ok[0] = (arg[0].isGeoLine()))
					&& (ok[1] = (arg[1].isGeoImplicitPoly()))) {
				GeoElement[] ret = kernel.Tangent(c.getLabels(),
						(GeoLine) arg[0], (GeoImplicitPoly) arg[1]);
				return ret;
			} else if ((ok[0] = (arg[0].isGeoConic()))
					&& (ok[1] = (arg[1].isGeoConic()))) {
				return kernel.CommonTangents(c.getLabels(), (GeoConic) arg[0], (GeoConic) arg[1]);
			}

			// syntax error
			else {
				if (!ok[0])
					throw argErr(app, "Tangent", arg[0]);
				else
					throw argErr(app, "Tangent", arg[1]);
			}

		default:
			throw argNumErr(app, "Tangent", n);
		}
	}
}

/**
 * Asymptote[ <GeoConic> ]
 */
class CmdAsymptote extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdAsymptote(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);

			// asymptotes to conic
			if (arg[0].isGeoConic())
				return kernel.Asymptote(c.getLabels(), (GeoConic) arg[0]);
			else if (arg[0].isGeoFunction())
			{
				GeoElement[] ret = { kernel.AsymptoteFunction(c.getLabel(),
						(GeoFunction) arg[0]) };
				return ret;
			}
			else if (arg[0].isGeoImplicitPoly()) {
				GeoElement[] ret =  {kernel.AsymptoteImplicitpoly(c.getLabel(),
						(GeoImplicitPoly) arg[0])} ;
				return ret;
			}
			throw argErr(app, "Asymptote", arg[0]);

		default:
			throw argNumErr(app, "Asymptote", n);
		}
	}
}

/**
 * Numerator[ <Function> ]
 */
class CmdNumerator extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdNumerator(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);

			if (arg[0].isGeoFunction()) {
				GeoElement[] ret = { kernel.Numerator(c.getLabel(),
						(GeoFunction) arg[0]) };
				return ret;

			}
			throw argErr(app, "Numerator", arg[0]);

		default:
			throw argNumErr(app, "Numerator", n);
		}
	}
}

/**
 * Denominator[ <Function> ]
 */
class CmdDenominator extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdDenominator(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);

			if (arg[0].isGeoFunction()) {
				GeoElement[] ret = { kernel.Denominator(c.getLabel(),
						(GeoFunction) arg[0]) };
				return ret;

			}
			throw argErr(app, "Denominator", arg[0]);

		default:
			throw argNumErr(app, "Denominator", n);
		}
	}
}

/**
 * Axes[ <GeoConic> ]
 */
class CmdAxes extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdAxes(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);

			// asymptotes to conic
			if (arg[0].isGeoConic())
				return kernel.Axes(c.getLabels(), (GeoConic) arg[0]);
			else
				throw argErr(app, "Axes", arg[0]);

		default:
			throw argNumErr(app, "Axes", n);
		}
	}
}

/**
 * FirstAxis[ <GeoConic> ]
 */
class CmdFirstAxis extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFirstAxis(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);

			// asymptotes to conic
			if (arg[0].isGeoConic()) {
				GeoElement[] ret = { kernel.FirstAxis(c.getLabel(),
						(GeoConic) arg[0]) };
				return ret;
			} else
				throw argErr(app, "FirstAxis", arg[0]);

		default:
			throw argNumErr(app, "FirstAxis", n);
		}
	}
}

/**
 * SecondAxis[ <GeoConic> ]
 */
class CmdSecondAxis extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSecondAxis(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);

			// asymptotes to conic
			if (arg[0].isGeoConic()) {
				GeoElement[] ret = { kernel.SecondAxis(c.getLabel(),
						(GeoConic) arg[0]) };
				return ret;
			} else
				throw argErr(app, "SecondAxis", arg[0]);

		default:
			throw argNumErr(app, "SecondAxis", n);
		}
	}
}

/**
 * FirstAxisLength[ <GeoConic> ]
 */
class CmdFirstAxisLength extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFirstAxisLength(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);

			// asymptotes to conic
			if (arg[0].isGeoConic()) {
				GeoElement[] ret = { kernel.FirstAxisLength(c.getLabel(),
						(GeoConic) arg[0]) };
				return ret;
			} else
				throw argErr(app, "FirstAxisLength", arg[0]);

		default:
			throw argNumErr(app, "FirstAxisLength", n);
		}
	}
}

/**
 * SecondAxisLength[ <GeoConic> ]
 */
class CmdSecondAxisLength extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSecondAxisLength(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);

			// asymptotes to conic
			if (arg[0].isGeoConic()) {
				GeoElement[] ret = { kernel.SecondAxisLength(c.getLabel(),
						(GeoConic) arg[0]) };
				return ret;
			} else
				throw argErr(app, "SecondAxisLength", arg[0]);

		default:
			throw argNumErr(app, "SecondAxisLength", n);
		}
	}
}

/**
 * LinearEccentricity[ <GeoConic> ] Excentricity[ <GeoConic> ]
 */
class CmdExcentricity extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdExcentricity(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);

			// asymptotes to conic
			if (arg[0].isGeoConic()) {
				GeoElement[] ret = { kernel.Excentricity(c.getLabel(),
						(GeoConic) arg[0]) };
				return ret;
			} else
				throw argErr(app, "Excentricity", arg[0]);

		default:
			throw argNumErr(app, "Excentricity", n);
		}
	}
}

/**
 * Eccentricity[ <GeoConic> ]
 */
class CmdEccentricity extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdEccentricity(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);

			// asymptotes to conic
			if (arg[0].isGeoConic()) {
				GeoElement[] ret = { kernel.Eccentricity(c.getLabel(),
						(GeoConic) arg[0]) };
				return ret;
			} else
				throw argErr(app, "Eccentricity", arg[0]);

		default:
			throw argNumErr(app, "Eccentricity", n);
		}
	}
}

/**
 * Parameter[ <GeoConic> ]
 */
class CmdParameter extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdParameter(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);

			// asymptotes to conic
			if (arg[0].isGeoConic()) {
				GeoElement[] ret = { kernel.Parameter(c.getLabel(),
						(GeoConic) arg[0]) };
				return ret;
			} else
				throw argErr(app, "Parameter", arg[0]);

		default:
			throw argNumErr(app, "Parameter", n);
		}
	}
}

/**
 * Radius[ <GeoConic> ]
 */
class CmdRadius extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdRadius(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);

			// asymptotes to conic
			if (arg[0].isGeoConic()) {
				GeoElement[] ret = { kernel.Radius(c.getLabel(),
						(GeoConic) arg[0]) };
				return ret;
			} else
				throw argErr(app, "Radius", arg[0]);

		default:
			throw argNumErr(app, "Radius", n);
		}
	}
}

/**
 * Directrix[ <GeoConic> ]
 */
class CmdDirectrix extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdDirectrix(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);

			// asymptotes to conic
			if (arg[0].isGeoConic()) {
				GeoElement[] ret = { kernel.Directrix(c.getLabel(),
						(GeoConic) arg[0]) };
				return ret;
			} else
				throw argErr(app, "Directrix", arg[0]);

		default:
			throw argNumErr(app, "Directrix", n);
		}
	}
}

/**
 * Translate[ <GeoPoint>, <GeoVector> ] Translate[ <GeoLine>, <GeoVector> ]
 * Translate[ <GeoConic>, <GeoVector> ] Translate[ <GeoFunction>, <GeoVector> ]
 * Translate[ <GeoVector>, <GeoPoint> ] // set start point Translate[
 * <GeoPolygon>, <GeoVector> ]
 * 
 */
class CmdTranslate extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdTranslate(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError,
			CircularDefinitionException {
		String label = c.getLabel();
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		GeoElement[] ret = new GeoElement[1];

		switch (n) {
		case 2:
			arg = resArgs(c);

			// translate object

			if ((ok[0] = (arg[0].isGeoVector()))
					&& (ok[1] = (arg[1].isGeoPoint()))) {
				GeoVector v = (GeoVector) arg[0];
				GeoPoint P = (GeoPoint) arg[1];

				ret[0] = kernel.Translate(label, v, P);

				return ret;
			} else if ((ok[0] = (arg[0] instanceof Translateable
					|| arg[0] instanceof GeoPolygon || arg[0].isGeoList()))
					&& (ok[1] = (arg[1].isGeoVector() || arg[1].isGeoPoint()))) {
				GeoVec3D v = (GeoVec3D) arg[1];
				ret = kernel.Translate(label, arg[0], v);
				return ret;
			}

			// syntax error
			else {
				if (!ok[0])
					throw argErr(app, "Translate", arg[0]);
				else
					throw argErr(app, "Translate", arg[1]);
			}

		default:
			throw argNumErr(app, "Translate", n);
		}
	}
}

/**
 * Rotate[ <GeoPoint>, <NumberValue> ] Rotate[ <GeoVector>, <NumberValue> ]
 * Rotate[ <GeoLine>, <NumberValue> ] Rotate[ <GeoConic>, <NumberValue> ]
 * Rotate[ <GeoPolygon>, <NumberValue> ]
 * 
 * Rotate[ <GeoPoint>, <NumberValue>, <GeoPoint> ] Rotate[ <GeoLine>,
 * <NumberValue>, <GeoPoint> ] Rotate[ <GeoConic>, <NumberValue>, <GeoPoint> ]
 * Rotate[ <GeoPolygon>, <NumberValue>, <GeoPoint> ]
 */
class CmdRotate extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdRotate(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		String label = c.getLabel();
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		GeoElement[] ret;

		switch (n) {
		case 2:
			// ROTATE AROUND CENTER (0,0)
			arg = resArgs(c);

			// rotate point, line or conic
			if ((ok[0] = true) && (ok[1] = (arg[1].isNumberValue()))) {
				NumberValue phi = (NumberValue) arg[1];

				ret = kernel.Rotate(label, arg[0], phi);
				return ret;
			}

			else {
				if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				else
					throw argErr(app, c.getName(), arg[1]);
			}

		case 3:
			// ROTATION AROUND POINT
			arg = resArgs(c);

			// rotate point, line or conic
			if ((ok[0] = true) && (ok[1] = (arg[1].isNumberValue()))
					&& (ok[2] = (arg[2].isGeoPoint()))) {

				NumberValue phi = (NumberValue) arg[1];
				GeoPoint Q = (GeoPoint) arg[2];

				ret = kernel.Rotate(label, arg[0], phi, Q);
				return ret;
			}

			// rotate polygon

			else {
				if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				else
					throw argErr(app, c.getName(), arg[1]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 * Dilate[ <GeoPoint>, <NumberValue>, <GeoPoint> ] Dilate[ <GeoLine>,
 * <NumberValue>, <GeoPoint> ] Dilate[ <GeoConic>, <NumberValue>, <GeoPoint> ]
 * Dilate[ <GeoPolygon>, <NumberValue>, <GeoPoint> ]
 */
class CmdDilate extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdDilate(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		String label = c.getLabel();
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		GeoElement[] ret;

		switch (n) {
		case 2:
			arg = resArgs(c);

			// dilate point, line or conic
			if ((ok[0] = (arg[0] instanceof Dilateable || arg[0].isGeoPolygon() || arg[0]
					.isGeoList()))
					&& (ok[1] = (arg[1].isNumberValue()))) {
				NumberValue phi = (NumberValue) arg[1];
				ret = kernel.Dilate(label, arg[0], phi);
				return ret;
			}

			else {
				if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				else
					throw argErr(app, c.getName(), arg[1]);
			}

		case 3:
			arg = resArgs(c);

			// dilate point, line or conic
			if ((ok[0] = (arg[0] instanceof Dilateable || arg[0].isGeoPolygon() || arg[0]
					.isGeoList()))
					&& (ok[1] = (arg[1].isNumberValue()))
					&& (ok[2] = (arg[2].isGeoPoint()))) {
				NumberValue phi = (NumberValue) arg[1];
				GeoPoint Q = (GeoPoint) arg[2];
				ret = kernel.Dilate(label, arg[0], phi, Q);
				return ret;
			}

			else {
				if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				else
					throw argErr(app, c.getName(), arg[1]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 * ApplyMatrix[<Matrix>,<Object>]
 */
class CmdApplyMatrix extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdApplyMatrix(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		String label = c.getLabel();
		int n = c.getArgumentNumber();

		GeoElement[] arg;
		GeoElement[] ret;

		switch (n) {
		case 2:
			arg = resArgs(c);

			if (arg[0].isGeoList()) {

				if (arg[1].isMatrixTransformable() || arg[1].isGeoFunction()
						|| arg[1].isGeoPolygon() || arg[0].isGeoList()) {
					ret = kernel.ApplyMatrix(label, arg[1], (GeoList) arg[0]);
					return ret;
				} else
					throw argErr(app, c.getName(), arg[1]);
			} else
				throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 * Shear[<Object>,<Line>,<Ratio>]
 */
class CmdShear extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdShear(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		String label = c.getLabel();
		int n = c.getArgumentNumber();

		GeoElement[] arg;
		GeoElement[] ret;

		switch (n) {
		case 3:
			arg = resArgs(c);

			if ((arg[1] instanceof GeoVec3D) && arg[2].isGeoNumeric()) {

				if (arg[0].isMatrixTransformable() || arg[0].isGeoFunction()
						|| arg[0].isGeoPolygon() || arg[0].isGeoList()) {

					ret = kernel.Shear(label, arg[0], (GeoVec3D) arg[1],
							(GeoNumeric) arg[2]);
					return ret;

				} else
					throw argErr(app, c.getName(), arg[0]);
			} else {
				if (!(arg[1] instanceof GeoVec3D))
					throw argErr(app, c.getName(), arg[1]);
				throw argErr(app, c.getName(), arg[2]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 * Stretch[<Object>,<Line>,<Ratio>]
 */
class CmdStretch extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdStretch(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		String label = c.getLabel();
		int n = c.getArgumentNumber();

		GeoElement[] arg;
		GeoElement[] ret;

		switch (n) {
		case 2:
			arg = resArgs(c);

			if (arg[1] instanceof GeoVector) {

				if (arg[0].isMatrixTransformable() || arg[0].isGeoFunction()
						|| arg[0].isGeoPolygon() || arg[0].isGeoList()) {

					ret = kernel.Stretch(label, arg[0], (GeoVec3D) arg[1],
							null);
					return ret;

				} else
					throw argErr(app, c.getName(), arg[0]);
			}
			else
				throw argErr(app, c.getName(), arg[1]);
		case 3:
			arg = resArgs(c);

			if ((arg[1] instanceof GeoLine) && arg[2].isGeoNumeric()) {

				if (arg[0].isMatrixTransformable() || arg[0].isGeoFunction()
						|| arg[0].isGeoPolygon() || arg[0].isGeoList()) {

					ret = kernel.Stretch(label, arg[0], (GeoVec3D) arg[1],
							(GeoNumeric) arg[2]);
					return ret;

				} else
					throw argErr(app, c.getName(), arg[0]);
			} else {
				if (!(arg[1] instanceof GeoVec3D))
					throw argErr(app, c.getName(), arg[1]);
				throw argErr(app, c.getName(), arg[2]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 * (2nd argument is the mirror) Mirror[ <GeoPoint>, <GeoPoint> ]
 * Mirror[<GeoLine>, <GeoPoint> ] Mirror[ <GeoConic>, <GeoPoint> ]
 * Mirror[<GeoPolygon>, <GeoPoint> ]
 * 
 * Mirror[ <GeoPoint>, <GeoLine> ] Mirror[ <GeoLine>, <GeoLine> ] Mirror[
 * <GeoConic>, <GeoLine> ] Mirror[ <GeoPolygon>, <GeoLine> ]
 */
class CmdMirror extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdMirror(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		String label = c.getLabel();
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		GeoElement[] ret = new GeoElement[1];

		switch (n) {
		case 2:
			arg = resArgs(c);

			if (arg[1].isGeoConic()) { // mirror point in circle Michael
										// Borcherds 2008-02-10
				GeoConic conic1 = (GeoConic) arg[1];

				if (conic1.getType() == GeoConic.CONIC_CIRCLE
						&& arg[0].isGeoConic() || arg[0].isGeoPoint()
						|| arg[0] instanceof GeoCurveCartesian
						|| arg[0] instanceof GeoLine
						|| arg[0] instanceof GeoPolyLineInterface
						|| arg[0] instanceof GeoFunction
						|| arg[0] instanceof GeoList
						|| arg[0] instanceof GeoImplicitPoly) {
					ret = kernel.Mirror(label, arg[0], conic1);
					return ret;
				}

			}

			// mirror object
			if (ok[0] = true) {

				// GeoElement geo = p.toGeoElement();

				// mirror at point
				if (ok[1] = (arg[1].isGeoPoint())) {
					GeoPoint Q = (GeoPoint) arg[1];

					ret = kernel.Mirror(label, arg[0], Q);
					return ret;
				}
				// mirror is line
				else if (ok[1] = (arg[1].isGeoLine())) {
					GeoLine line = (GeoLine) arg[1];

					ret = kernel.Mirror(label, arg[0], line);
					return ret;
				}
			}

			// syntax error
			else {
				if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				else
					throw argErr(app, c.getName(), arg[1]);
			}

		default:
			throw argNumErr(app, "Mirror", n);
		}
	}
}

/**
 * Relation[ <GeoElement>, <GeoElement> ]
 */
class CmdRelation extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdRelation(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);

			// show relation string in a message dialog
			if ((ok[0] = (arg[0].isGeoElement()))
					&& (ok[1] = (arg[1].isGeoElement()))) {
				app.showRelation((GeoElement) arg[0], (GeoElement) arg[1]);
				return new GeoElement[] {};
			}

			// syntax error
			else {
				if (!ok[0])
					throw argErr(app, "Relation", arg[0]);
				else
					throw argErr(app, "Relation", arg[1]);
			}

		default:
			throw argNumErr(app, "Relation", n);
		}
	}
}

/**
 * Delete[ <GeoElement> ]
 */
class CmdDelete extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdDelete(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (ok[0] = (arg[0].isGeoElement())) {
				GeoElement geo = (GeoElement) arg[0];
				GeoElement[] ret = { geo };
				// delete object
				geo.removeOrSetUndefinedIfHasFixedDescendent();
				return ret;
			} else
				throw argErr(app, "Delete", arg[0]);

		default:
			throw argNumErr(app, "Delete", n);
		}
	}
}

/**
 * Derivative[ <GeoFunction> ] Derivative[ <GeoFunctionNVar>, <var> ]
 * Derivative[ <GeoCurveCartesian> ]
 */
class CmdDerivative extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdDerivative(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		String label = c.getLabel();
		GeoElement[] arg, arg2;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0] instanceof CasEvaluableFunction) {
				CasEvaluableFunction f = (CasEvaluableFunction) arg[0];
				if (label == null)
					label = getDerivLabel(f.toGeoElement(), 1);
				GeoElement[] ret = { kernel.Derivative(label, f, null, null) };
				return ret;
			} else
				throw argErr(app, "Derivative", arg[0]);

		case 2:
			arg = resArgs(c);
			// Derivative[ f(x), 2]
			if ((arg[0].isGeoFunction()||arg[0].isGeoCurveCartesian())
					&& arg[1].isNumberValue()) {
				double order = ((NumberValue) arg[1]).getDouble();

				CasEvaluableFunction f = (CasEvaluableFunction) arg[0];
				if (label == null) {
					int iorder = (int) Math.round(order);
					label = getDerivLabel(f.toGeoElement(), iorder);
				}
				GeoElement[] ret = { kernel.Derivative(label, f, null,
						(NumberValue) arg[1]) };
				return ret;

			}
			
			// Derivative[ f(a,b), a ]
			try {
				arg2 = resArgsLocalNumVar(c, 1, 1);
				if (arg2[0] instanceof CasEvaluableFunction
						&& arg2[1].isGeoNumeric()) {
					GeoElement[] ret = { kernel.Derivative(label,
							(CasEvaluableFunction) arg2[0], // function
							(GeoNumeric) arg2[1], null) }; // var
					return ret;
				}
			} catch (Throwable t) {
			}

			// Derivative[ f(x, y), x]
			if (arg[0] instanceof CasEvaluableFunction
					&& arg[1].isGeoFunction()) {
				GeoNumeric var = new GeoNumeric(cons);
				var.setLocalVariableLabel(arg[1].toString());
				GeoElement[] ret = { kernel.Derivative(label,
						(CasEvaluableFunction) arg[0], // function
						(GeoNumeric) var, null) }; // var
				return ret;
			}

			// if we get here, the first argument must have been wrong
			throw argErr(app, "Derivative", arg[0]);

		case 3:
			// Derivative[ f(a,b), a, 2 ]
			try {
				arg = resArgsLocalNumVar(c, 1, 1);
				if (arg[0] instanceof GeoFunctionNVar && arg[1].isGeoNumeric()
						&& arg[2].isNumberValue()) {
					GeoElement[] ret = { kernel.Derivative(label,
							(GeoFunctionNVar) arg[0], // function
							(GeoNumeric) arg[1], (NumberValue) arg[2]) }; // var
					return ret;
				}
			} catch (Throwable t) {
			}

			arg = resArgs(c);
			// Derivative[ f(x, y), x, 2]
			if (arg[0] instanceof GeoFunctionNVar && arg[1].isGeoFunction()
					&& arg[2].isNumberValue()) {
				GeoNumeric var = new GeoNumeric(cons);
				var.setLocalVariableLabel(arg[1].toString());
				GeoElement[] ret = { kernel.Derivative(label,
						(GeoFunctionNVar) arg[0], // function
						(GeoNumeric) var, (NumberValue) arg[2]) }; // var
				return ret;
			}
			// if we get here, the first argument must have been wrong
			throw argErr(app, "Derivative", arg[0]);

		default:
			throw argNumErr(app, "Derivative", n);
		}

	}

	private String getDerivLabel(GeoElement geo, int order) {
		String label = null;

		if (geo.isLabelSet()) {
			label = geo.getLabel();
			for (int i = 0; i < order; i++)
				label = label + "'";
		}

		return geo.getFreeLabel(label);
	}
}

/**
 * Integral[ <GeoFunction> ] Integral[ <GeoFunction>, <Number a>, <Number b> ]
 * Integral[ <GeoFunction f>, <GeoFunction g>, <Number a>, <Number b> ]
 */
class CmdIntegral extends CommandProcessor {

	
	// from GeoGebra 4.0, Integral has been split into Integral and IntegralBetween
	// old syntax and files will still work
	private String internalCommandName;

	/**
	 * Create new command processor
	 * @param between if true IntegralBetween instead of Integral is created
	 * @param kernel
	 *            kernel
	 */
	public CmdIntegral(Kernel kernel, boolean between) {
		super(kernel);
		internalCommandName = between ? "IntegralBetween" : "Integral";
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (ok[0] = (arg[0].isGeoFunctionable())) {
				GeoElement[] ret = { kernel.Integral(c.getLabel(),
						((GeoFunctionable) arg[0]).getGeoFunction(), null) };
				return ret;
			} else
				throw argErr(app, internalCommandName, arg[0]);

		case 2:
			// Integral[ f(x,y), x]
			arg = resArgsLocalNumVar(c, 1, 1);
			if (arg[0] instanceof CasEvaluableFunction && arg[1].isGeoNumeric()) {
				GeoElement[] ret = { kernel.Integral(c.getLabel(),
						(CasEvaluableFunction) arg[0], // function
						(GeoNumeric) arg[1]) }; // var
				return ret;
			} else
				throw argErr(app, internalCommandName, arg[0]);

		case 3:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoFunctionable()))
					&& (ok[1] = (arg[1].isNumberValue()))
					&& (ok[2] = (arg[2].isNumberValue()))) {
				GeoElement[] ret = { kernel.Integral(c.getLabel(),
						((GeoFunctionable) arg[0]).getGeoFunction(),
						(NumberValue) arg[1], (NumberValue) arg[2]) };
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, internalCommandName, arg[0]);
				else if (!ok[1])
					throw argErr(app, internalCommandName, arg[1]);
				else
					throw argErr(app, internalCommandName, arg[2]);
			}

		case 4:
			arg = resArgs(c);
			// difference of two functions
			if ((ok[0] = (arg[0].isGeoFunctionable()))
					&& (ok[1] = (arg[1].isGeoFunctionable()))
					&& (ok[2] = (arg[2].isNumberValue()))
					&& (ok[3] = (arg[3].isNumberValue() && !arg[3]
							.isBooleanValue()))) {
				GeoElement[] ret = { kernel.Integral(c.getLabel(),
						((GeoFunctionable) arg[0]).getGeoFunction(),
						((GeoFunctionable) arg[1]).getGeoFunction(),
						(NumberValue) arg[2], (NumberValue) arg[3]) };
				return ret;

			}
			// single function integral with evaluate option
			else if ((ok[0] = (arg[0].isGeoFunctionable()))
					&& (ok[1] = (arg[1].isNumberValue()))
					&& (ok[2] = (arg[2].isNumberValue()))
					&& (ok[3] = (arg[3].isGeoBoolean()))) {
				GeoElement[] ret = { kernel.Integral(c.getLabel(),
						((GeoFunctionable) arg[0]).getGeoFunction(),
						(NumberValue) arg[1], (NumberValue) arg[2],
						(GeoBoolean) arg[3]) };
				return ret;

			} else {
				if (!ok[0])
					throw argErr(app, internalCommandName, arg[0]);
				else if (!ok[1])
					throw argErr(app, internalCommandName, arg[1]);
				else if (!ok[2])
					throw argErr(app, internalCommandName, arg[2]);
				else
					throw argErr(app, internalCommandName, arg[3]);
			}

		case 5:
			arg = resArgs(c);
			// difference of two functions with evaluate option
			if ((ok[0] = (arg[0].isGeoFunctionable()))
					&& (ok[1] = (arg[1].isGeoFunctionable()))
					&& (ok[2] = (arg[2].isNumberValue()))
					&& (ok[3] = (arg[3].isNumberValue())
							&& (ok[4] = (arg[4].isGeoBoolean())))) {
				GeoElement[] ret = { kernel.Integral(c.getLabel(),
						((GeoFunctionable) arg[0]).getGeoFunction(),
						((GeoFunctionable) arg[1]).getGeoFunction(),
						(NumberValue) arg[2], (NumberValue) arg[3],
						(GeoBoolean) arg[4]) };
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, internalCommandName, arg[0]);
				else if (!ok[1])
					throw argErr(app, internalCommandName, arg[1]);
				else if (!ok[2])
					throw argErr(app, internalCommandName, arg[2]);
				else if (!ok[3])
					throw argErr(app, internalCommandName, arg[3]);
				else
					throw argErr(app, internalCommandName, arg[4]);
			}

		default:
			throw argNumErr(app, internalCommandName, n);
		}
	}
}

/**
 * UpperSum[ <GeoFunction>, <Number>, <Number>, <Number> ]
 */
class CmdUpperSum extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdUpperSum(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 4:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoFunctionable()))
					&& (ok[1] = (arg[1].isNumberValue()))
					&& (ok[2] = (arg[2].isNumberValue()))
					&& (ok[3] = (arg[3].isNumberValue()))) {
				GeoElement[] ret = { kernel.UpperSum(c.getLabel(),
						((GeoFunctionable) arg[0]).getGeoFunction(),
						(NumberValue) arg[1], (NumberValue) arg[2],
						(NumberValue) arg[3]) };
				return ret;
			} else
				throw argErr(app, "UpperSum", null);

		default:
			throw argNumErr(app, "UpperSum", n);
		}
	}
}

/**
 * BarChart[ <Number>, <Number>, <List> ]
 */
class CmdBarChart extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdBarChart(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoList()))
					&& (ok[1] = (arg[1].isGeoNumeric()))) {
				GeoElement[] ret = { kernel.BarChart(c.getLabel(),
						(GeoList) arg[0], (GeoNumeric) arg[1]) };
				return ret;
			} else if ((ok[0] = (arg[0].isGeoList()))
					&& (ok[1] = (arg[1].isGeoList()))) {
				GeoElement[] ret = { kernel.BarChart(c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1]) };
				return ret;
			} else
				throw argErr(app, c.getName(), null);
		case 3:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isNumberValue()))
					&& (ok[1] = (arg[1].isNumberValue()))
					&& (ok[2] = (arg[2].isGeoList()))) {
				GeoElement[] ret = { kernel.BarChart(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1],
						(GeoList) arg[2]) };
				return ret;
			} else if ((ok[0] = (arg[0].isGeoList()))
					&& (ok[1] = (arg[1].isGeoList()))
					&& (ok[2] = (arg[2].isNumberValue()))) {
				GeoElement[] ret = { kernel.BarChart(c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1],
						(NumberValue) arg[2]) };
				return ret;
			} else
				throw argErr(app, c.getName(), null);
		case 6:
			// create local variable at position 3 and resolve arguments
			arg = resArgsLocalNumVar(c, 3, 4);
			if ((ok[0] = (arg[0].isNumberValue()))
					&& (ok[1] = (arg[1].isNumberValue()))
					&& ((ok[2] = arg[2].isGeoElement()))
					&& (ok[3] = arg[3].isGeoNumeric())
					&& (ok[4] = arg[4].isNumberValue())
					&& (ok[5] = arg[5].isNumberValue())) {
				GeoElement[] ret = { kernel.BarChart(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1], arg[2],
						(GeoNumeric) arg[3], (NumberValue) arg[4],
						(NumberValue) arg[5], null) };
				return ret;
			} else
				throw argErr(app, c.getName(), null);

		case 7:
			// create local variable at position 3 and resolve arguments
			arg = resArgsLocalNumVar(c, 3, 4);
			if ((ok[0] = (arg[0].isNumberValue()))
					&& (ok[1] = (arg[1].isNumberValue()))
					&& ((ok[2] = arg[2].isGeoElement()))
					&& (ok[3] = arg[3].isGeoNumeric())
					&& (ok[4] = arg[4].isNumberValue())
					&& (ok[5] = arg[5].isNumberValue())
					&& (ok[6] = arg[6].isNumberValue())) {
				GeoElement[] ret = { kernel.BarChart(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1], arg[2],
						(GeoNumeric) arg[3], (NumberValue) arg[4],
						(NumberValue) arg[5], (NumberValue) arg[6]) };
				return ret;
			} else
				throw argErr(app, c.getName(), null);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 * BarChart[ <Number>, <Number>, <List> ]
 */
class CmdBoxPlot extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdBoxPlot(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 3:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isNumberValue()))
					&& (ok[1] = (arg[1].isNumberValue()))
					&& (ok[2] = (arg[2].isGeoList()))) {
				GeoElement[] ret = { kernel.BoxPlot(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1],
						(GeoList) arg[2]) };
				return ret;
			} else
				throw argErr(app, c.getName(), null);

		case 7:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isNumberValue())
					&& (ok[1] = (arg[1].isNumberValue()))
					&& (ok[2] = (arg[2].isNumberValue()))
					&& (ok[3] = (arg[3].isNumberValue()))
					&& (ok[4] = (arg[4].isNumberValue()))
					&& (ok[5] = (arg[5].isNumberValue()))
					&& (ok[6] = (arg[6].isNumberValue())))) {

				GeoElement[] ret = { kernel.BoxPlot(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1],
						(NumberValue) arg[2], (NumberValue) arg[3],
						(NumberValue) arg[4], (NumberValue) arg[5],
						(NumberValue) arg[6]) };
				return ret;

			}
			// else continue:

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 * Histogram[ <List>, <List> ]
 */
class CmdHistogram extends CommandProcessor {
	private boolean right;
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdHistogram(Kernel kernel) {		
		this(kernel,false);
	}
	
	public CmdHistogram(Kernel kernel,boolean right) {
		super(kernel);
		this.right = right;
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoList()))
					&& (ok[1] = (arg[1].isGeoList()))) {
				GeoElement[] ret = { kernel.Histogram(c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1],right) };
				return ret;
			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else
				throw argErr(app, c.getName(), arg[1]);


		case 3:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoList()))
					&& (ok[1] = (arg[1].isGeoList()))
					&& (ok[2] = (arg[2].isGeoBoolean()))) {
				GeoElement[] ret = { kernel.Histogram(c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1],
						(GeoBoolean) arg[2], null,right) };
				return ret;
			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else
				throw argErr(app, c.getName(), arg[2]);


		case 4:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoList()))
					&& (ok[1] = (arg[1].isGeoList()))
					&& (ok[2] = (arg[2].isGeoBoolean()))
					&& (ok[3] = (arg[3].isGeoNumeric()))) {
				GeoElement[] ret = { kernel.Histogram(c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1],
						(GeoBoolean) arg[2], (GeoNumeric) arg[3],right) };
				return ret;
			}
			
			else if ((ok[0] = (arg[0].isGeoBoolean()))
					&& (ok[1] = (arg[1].isGeoList()))
					&& (ok[2] = (arg[2].isGeoList()))
					&& (ok[3] = (arg[3].isGeoBoolean()))) {
				GeoElement[] ret = { kernel.Histogram(c.getLabel(),
						(GeoBoolean) arg[0], (GeoList) arg[1],
						(GeoList) arg[2], (GeoBoolean) arg[3],right) };
				return ret;
			}
			
			else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg[2]);
			else
				throw argErr(app, c.getName(), arg[3]);

		case 5:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoBoolean()))
					&& (ok[1] = (arg[1].isGeoList()))
					&& (ok[2] = (arg[2].isGeoList()))
					&& (ok[3] = (arg[3].isGeoBoolean()))
					&& (ok[4] = (arg[4].isGeoNumeric()))) {
				GeoElement[] ret = { kernel.Histogram(c.getLabel(),
						(GeoBoolean) arg[0], (GeoList) arg[1],
						(GeoList) arg[2], (GeoBoolean) arg[3], (GeoNumeric) arg[4],right) };
				return ret;
			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg[2]);
			else if (!ok[3])
				throw argErr(app, c.getName(), arg[3]);
			else
				throw argErr(app, c.getName(), arg[4]);

			
		default:
			throw argNumErr(app, c.getName(), n);
		}

	}
}
class CmdHistogramRight extends CmdHistogram {
	public CmdHistogramRight(Kernel kernel){
		super(kernel,true);
	}
}

/**
 * Histogram[ <List>, <List> ]
 */
class CmdFrequencyPolygon extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFrequencyPolygon(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoList()))
					&& (ok[1] = (arg[1].isGeoList()))) {
				GeoElement[] ret = { kernel.FrequencyPolygon(c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1]) };
				return ret;
			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else
				throw argErr(app, c.getName(), arg[1]);


		case 3:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoList()))
					&& (ok[1] = (arg[1].isGeoList()))
					&& (ok[2] = (arg[2].isGeoBoolean()))) {
				GeoElement[] ret = { kernel.FrequencyPolygon(c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1],
						(GeoBoolean) arg[2], null) };
				return ret;
			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else
				throw argErr(app, c.getName(), arg[2]);


		case 4:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoList()))
					&& (ok[1] = (arg[1].isGeoList()))
					&& (ok[2] = (arg[2].isGeoBoolean()))
					&& (ok[3] = (arg[3].isGeoNumeric()))) {
				GeoElement[] ret = { kernel.FrequencyPolygon(c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1],
						(GeoBoolean) arg[2], (GeoNumeric) arg[3]) };
				return ret;
			}
			
			else if ((ok[0] = (arg[0].isGeoBoolean()))
					&& (ok[1] = (arg[1].isGeoList()))
					&& (ok[2] = (arg[2].isGeoList()))
					&& (ok[3] = (arg[3].isGeoBoolean()))) {
				GeoElement[] ret = { kernel.FrequencyPolygon(c.getLabel(),
						(GeoBoolean) arg[0], (GeoList) arg[1],
						(GeoList) arg[2], (GeoBoolean) arg[3]) };
				return ret;
			}
			
			else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg[2]);
			else
				throw argErr(app, c.getName(), arg[3]);

		case 5:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoBoolean()))
					&& (ok[1] = (arg[1].isGeoList()))
					&& (ok[2] = (arg[2].isGeoList()))
					&& (ok[3] = (arg[3].isGeoBoolean()))
					&& (ok[4] = (arg[4].isGeoNumeric()))) {
				GeoElement[] ret = { kernel.FrequencyPolygon(c.getLabel(),
						(GeoBoolean) arg[0], (GeoList) arg[1],
						(GeoList) arg[2], (GeoBoolean) arg[3], (GeoNumeric) arg[4]) };
				return ret;
			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg[2]);
			else if (!ok[3])
				throw argErr(app, c.getName(), arg[3]);
			else
				throw argErr(app, c.getName(), arg[4]);

			
		default:
			throw argNumErr(app, c.getName(), n);
		}

	}
}






/**
 * DotPlot[ <List of Numeric> ] G.Sturr 2010-8-10
 */
class CmdDotPlot extends CmdOneListFunction {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdDotPlot(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b) {
		return kernel.DotPlot(a, b);
	}

}


/**
 * NormalQuantilePlot[ <List of Numeric> ] G.Sturr 2011-6-29
 */
class CmdNormalQuantilePlot extends CmdOneListFunction {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdNormalQuantilePlot(Kernel kernel) {
		super(kernel);
	}

	final protected GeoElement doCommand(String a, GeoList b) {
		return kernel.NormalQuantilePlot(a, b);
	}

}



/**
 * LowerSum[ <GeoFunction>, <Number>, <Number>, <Number> ]
 */
class CmdLowerSum extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdLowerSum(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 4:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoFunctionable()))
					&& (ok[1] = (arg[1].isNumberValue()))
					&& (ok[2] = (arg[2].isNumberValue()))
					&& (ok[3] = (arg[3].isNumberValue()))) {
				GeoElement[] ret = { kernel.LowerSum(c.getLabel(),
						((GeoFunctionable) arg[0]).getGeoFunction(),
						(NumberValue) arg[1], (NumberValue) arg[2],
						(NumberValue) arg[3]) };
				return ret;
			} else
				throw argErr(app, "LowerSum", null);

		default:
			throw argNumErr(app, "LowerSum", n);
		}
	}
}

/**
 * LeftSum[ <GeoFunction>, <Number>, <Number>, <Number> ]
 */
class CmdLeftSum extends CommandProcessor {

	/**
	* Create new command processor
	* @param kernel kernel
	*/
	public CmdLeftSum (Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 4 :
			arg = resArgs(c);
			if ((ok[0] = (arg[0] .isGeoFunctionable()))
					&& (ok[1] = (arg[1] .isNumberValue()))
					&& (ok[2] = (arg[2] .isNumberValue()))
					&& (ok[3] = (arg[3] .isNumberValue()))) {
				GeoElement[] ret =
				{
						kernel.LeftSum(
								c.getLabel(),
								((GeoFunctionable) arg[0]).getGeoFunction(),
								(NumberValue) arg[1],
								(NumberValue) arg[2],
								(NumberValue) arg[3])};
				return ret;
			} else
				throw argErr(app, "LeftSum", null);

		default :
			throw argNumErr(app, "LeftSum", n);
		}
	}
}//CmdLeftSum

/**
 * RectangleSum[ <GeoFunction>, <Number>, <Number>, <Number> ]
 */
class CmdRectangleSum extends CommandProcessor {

	/**
	* Create new command processor
	* @param kernel kernel
	*/
	public CmdRectangleSum (Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 5 :
			arg = resArgs(c);
			if ((ok[0] = (arg[0] .isGeoFunctionable()))
					&& (ok[1] = (arg[1] .isNumberValue()))
					&& (ok[2] = (arg[2] .isNumberValue()))
					&& (ok[3] = (arg[3] .isNumberValue()))					
					&& (ok[4] = (arg[4] .isNumberValue()))) {
				GeoElement[] ret =
				{
						kernel.RectangleSum(
								c.getLabel(),
								((GeoFunctionable) arg[0]).getGeoFunction(),
								(NumberValue) arg[1],
								(NumberValue) arg[2],
								(NumberValue) arg[3],								
								(NumberValue) arg[4])};
				return ret;
			} else
				throw argErr(app, "RectangleSum", null);

		default :
			throw argNumErr(app, "RectangleSum", n);
		}
	}
}//CmdRectangleSum

/**
 * LowerSum[ <GeoFunction>, <Number>, <Number>, <Number> ]
 */
class CmdTrapezoidalSum extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdTrapezoidalSum(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 4:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoFunctionable()))
					&& (ok[1] = (arg[1].isNumberValue()))
					&& (ok[2] = (arg[2].isNumberValue()))
					&& (ok[3] = (arg[3].isNumberValue()))) {
				GeoElement[] ret = { kernel.TrapezoidalSum(c.getLabel(),
						((GeoFunctionable) arg[0]).getGeoFunction(),
						(NumberValue) arg[1], (NumberValue) arg[2],
						(NumberValue) arg[3]) };
				return ret;
			} else
				throw argErr(app, "TrapezoidalSum", null);

		default:
			throw argNumErr(app, "TrapezoidalSum", n);
		}
	}
}

/**
 * Polynomial[ <GeoFunction> ]
 */
class CmdPolynomial extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdPolynomial(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 1:
			if (ok[0] = (arg[0].isGeoFunctionable())) {
				GeoElement[] ret = { kernel.PolynomialFunction(c.getLabel(),
						((GeoFunctionable) arg[0]).getGeoFunction()) };
				return ret;
			}
			// Michael Borcherds 2008-01-22 BEGIN
			// PolynomialFromCoordinates
			else if (ok[0] = (arg[0].isGeoList())) {
				GeoElement[] ret = { kernel.PolynomialFunction(c.getLabel(),
						((GeoList) arg[0])) };
				return ret;
			}
			// Michael Borcherds 2008-01-22 END
			else
				throw argErr(app, c.getName(), arg[0]);

			// more than one argument
		default:
			// Markus Hohenwarter 2008-01-26 BEGIN
			// try to create list of points
			GeoList list = wrapInList(kernel, arg, arg.length,
					GeoElement.GEO_CLASS_POINT);
			if (list != null) {
				GeoElement[] ret = { kernel.PolynomialFunction(c.getLabel(),
						list) };
				return ret;
			}
			// Markus Hohenwarter 2008-01-26 END
			else
				throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 * TaylorSeries[ <GeoFunction>, <Number>, <Number> ]
 */
class CmdTaylorSeries extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdTaylorSeries(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 3:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoFunctionable()))
					&& (ok[1] = (arg[1].isNumberValue()))
					&& (ok[2] = (arg[2].isNumberValue()))) {
				GeoElement[] ret = { kernel.TaylorSeries(c.getLabel(),
						((GeoFunctionable) arg[0]).getGeoFunction(),
						(NumberValue) arg[1], (NumberValue) arg[2]) };
				return ret;
			} else
				throw argErr(app, "TaylorSeries", null);

		default:
			throw argNumErr(app, "TaylorSeries", n);
		}
	}
}

/**
 * Root[ <GeoFunction> ] Root[ <GeoFunction> , <Number> ] Root[ <GeoFunction> ,
 * <Number> , <Number> ]
 */
class CmdRoot extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdRoot(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		// roots of polynomial
		case 1:
			arg = resArgs(c);
			if (ok[0] = (arg[0].isGeoFunctionable()))
				return kernel.Root(c.getLabels(), ((GeoFunctionable) arg[0])
						.getGeoFunction());
			else
				throw argErr(app, "Root", arg[0]);

			// root with start value
		case 2:
			arg = resArgs(c);
			if (ok[0] = (arg[0].isGeoFunctionable())
					&& (ok[1] = (arg[1].isNumberValue()))) {
				GeoElement[] ret = { kernel.Root(c.getLabel(),
						((GeoFunctionable) arg[0]).getGeoFunction(),
						(NumberValue) arg[1]) };
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, "Root", arg[0]);
				else
					throw argErr(app, "Root", arg[1]);
			}

			// root in interval
		case 3:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoFunctionable()))
					&& (ok[1] = (arg[1].isNumberValue()))
					&& (ok[2] = (arg[2].isNumberValue()))) {
				GeoElement[] ret = { kernel.Root(c.getLabel(),
						((GeoFunctionable) arg[0]).getGeoFunction(),
						(NumberValue) arg[1], (NumberValue) arg[2]) };
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, "Root", arg[0]);
				else if (!ok[1])
					throw argErr(app, "Root", arg[1]);
				else
					throw argErr(app, "Root", arg[2]);
			}

		default:
			throw argNumErr(app, "Root", n);
		}
	}
}

/**
 * Roots[ <GeoFunction>, <Number> , <Number> ]
 * (Numerical version, more than one root.)
 */
class CmdRoots extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdRoots(Kernel kernel) {
		super(kernel);
	}//Constructor

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 3:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoFunctionable()))
					&& (ok[1] = (arg[1].isNumberValue()))
					&& (ok[2] = (arg[2].isNumberValue()))) {
				GeoElement[] ret = kernel.Roots(c.getLabels(),
						((GeoFunctionable) arg[0]).getGeoFunction(),
						(NumberValue) arg[1], (NumberValue) arg[2]);
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, "Root", arg[0]);
				else if (!ok[1])
					throw argErr(app, "Root", arg[1]);
				else
					throw argErr(app, "Root", arg[2]);
			}//if

		default:
			throw argNumErr(app, "Roots", n);
		}//switch
	}//process(command)
}//class CmdRoots

/**
 * ComplexRoot[ <GeoFunction> ]
 */
class CmdComplexRoot extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdComplexRoot(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		// roots of polynomial
		case 1:
			arg = resArgs(c);
			if (ok[0] = (arg[0].isGeoFunctionable()))
				return kernel.ComplexRoot(c.getLabels(),
						((GeoFunctionable) arg[0]).getGeoFunction());
			else
				throw argErr(app, "ComplexRoot", arg[0]);

		default:
			throw argNumErr(app, "ComplexRoot", n);
		}
	}
}

/**
 * Extremum[ <GeoFunction> ]
 */
class CmdExtremum extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdExtremum(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (ok[0] = (arg[0].isGeoFunctionable()))
				return kernel.Extremum(c.getLabels(),
						((GeoFunctionable) arg[0]).getGeoFunction());
			else
				throw argErr(app, "Extremum", arg[0]);
		case 3: // Ulven 04.02.2011 for Extremum[f,start-x,end-x]
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoFunctionable()))
					&& (ok[1] = (arg[1].isNumberValue()))
					&& (ok[2] = (arg[2].isNumberValue()))

			)
				return kernel.Extremum(c.getLabels(), 
						((GeoFunctionable) arg[0]).getGeoFunction(),
						(NumberValue) arg[1],
						(NumberValue) arg[2]
						);
			else
				throw argErr(app, "Extremum", n);

		default:
			throw argNumErr(app, "Extremum", n);
		}
	}
}

/**
 * TurningPoint[ <GeoFunction> ]
 */
class CmdTurningPoint extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdTurningPoint(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (ok[0] = (arg[0].isGeoFunctionable()))
				return kernel.TurningPoint(c.getLabels(),
						((GeoFunctionable) arg[0]).getGeoFunction());
			else
				throw argErr(app, "TurningPoint", arg[0]);

		default:
			throw argNumErr(app, "TurningPoint", n);
		}
	}
}

/**
 * Centroid[ <Polygon> ]
 */
class CmdCentroid extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCentroid(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (ok[0] = (arg[0].isGeoPolygon())) {
				GeoElement[] ret = { kernel.Centroid(c.getLabel(),
						(GeoPolygon) arg[0]) };
				return ret;
			} else
				throw argErr(app, "Centroid", arg[0]);

		default:
			throw argNumErr(app, "Centroid", n);
		}
	}
}

/**
 * If[ <GeoBoolean>, <GeoElement> ] If[ <GeoBoolean>, <GeoElement>, <GeoElement>
 * ]
 */
class CmdIf extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdIf(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2: // if - then
		case 3: // if - then - else
			arg = resArgs(c);
			GeoElement geoElse = n == 3 ? arg[2] : null;

			// standard case: simple boolean condition
			if (ok[0] = arg[0].isGeoBoolean()) {
				GeoElement[] ret = { kernel.If(c.getLabel(),
						(GeoBoolean) arg[0], arg[1], geoElse) };
				return ret;
			}

			// SPECIAL CASE for functions:
			// boolean function in x as condition
			// example: If[ x < 2, x^2, x + 2 ]
			// DO NOT change instanceof here (see
			// GeoFunction.isGeoFunctionable())
			else if (ok[0] = (arg[0] instanceof GeoFunction)) {
				GeoFunction booleanFun = (GeoFunction) arg[0];
				if ((ok[0] = booleanFun.isBooleanFunction())
						// now that lines are functionable, need to disallow eg if[x<=40, y=20]
						&& (ok[1] = (arg[1].isGeoFunctionable() && !arg[1].isGeoLine()))
						&& (geoElse == null || geoElse.isGeoFunctionable())) {
					GeoFunction elseFun = geoElse == null ? null
							: ((GeoFunctionable) geoElse).getGeoFunction();

					GeoElement[] ret = { kernel.If(c.getLabel(),
							(GeoFunction) booleanFun,
							((GeoFunctionable) arg[1]).getGeoFunction(),
							elseFun) };
					return ret;
				}
			}

			if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (n == 2 || !ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else
				throw argErr(app, c.getName(), arg[2]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}

/**
 * CountIf[ <GeoBoolean>, <GeoList> ]
 */
class CmdCountIf extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCountIf(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:

			arg = resArgs(c);

			// DO NOT change instanceof here (see
			// GeoFunction.isGeoFunctionable())
			if (ok[0] = (arg[0] instanceof GeoFunction)) {
				GeoFunction booleanFun = (GeoFunction) arg[0];
				if ((ok[0] = booleanFun.isBooleanFunction())
						&& (ok[1] = arg[1].isGeoList())) {

					GeoElement[] ret = { kernel.CountIf(c.getLabel(),
							(GeoFunction) booleanFun, ((GeoList) arg[1])) };
					return ret;
				}
			}

			if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}

/**
 * AffineRatio[<Point>,<Point>,<Point>]
 * 
 * @author Victor Franco Espino
 */
class CmdAffineRatio extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdAffineRatio(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 3:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))
					&& (ok[2] = (arg[2].isGeoPoint()))) {
				GeoElement[] ret = { kernel
						.AffineRatio(c.getLabel(), (GeoPoint) arg[0],
								(GeoPoint) arg[1], (GeoPoint) arg[2]) };
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				if (!ok[1])
					throw argErr(app, c.getName(), arg[1]);
				else
					throw argErr(app, c.getName(), arg[2]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 * CrossRtio[<Point>,<Point>,<Point>,<Point>]
 * 
 * @author Victor Franco Espino
 */
class CmdCrossRatio extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCrossRatio(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 4:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoPoint()))
					&& (ok[2] = (arg[2].isGeoPoint()))
					&& (ok[3] = (arg[3].isGeoPoint()))) {
				GeoElement[] ret = { kernel.CrossRatio(c.getLabel(),
						(GeoPoint) arg[0], (GeoPoint) arg[1],
						(GeoPoint) arg[2], (GeoPoint) arg[3]) };
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				if (!ok[1])
					throw argErr(app, c.getName(), arg[1]);
				if (!ok[2])
					throw argErr(app, c.getName(), arg[2]);
				else
					throw argErr(app, c.getName(), arg[3]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 * CurvatureVector[<Point>,<Curve>], CurvatureVector[<Point>,<Function>]
 * 
 * @author Victor Franco Espino
 */
class CmdCurvatureVector extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCurvatureVector(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoFunctionable()))) {
				GeoElement[] ret = { kernel.CurvatureVector(c.getLabel(),
						(GeoPoint) arg[0], (GeoFunction) arg[1]) };
				return ret;
			} else if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoCurveCartesian()))) {
				GeoElement[] ret = { kernel.CurvatureVectorCurve(c.getLabel(),
						(GeoPoint) arg[0], (GeoCurveCartesian) arg[1]) };
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				else
					throw argErr(app, c.getName(), arg[1]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 * Curvature[<Point>,<Curve>], Curvature[<Point>,<Function>]
 * 
 * @author Victor Franco Espino
 */
class CmdCurvature extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCurvature(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoFunctionable()))) {
				GeoElement[] ret = { kernel.Curvature(c.getLabel(),
						(GeoPoint) arg[0], (GeoFunction) arg[1]) };
				return ret;
			} else if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoCurveCartesian()))) {
				GeoElement[] ret = { kernel.CurvatureCurve(c.getLabel(),
						(GeoPoint) arg[0], (GeoCurveCartesian) arg[1]) };
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				else
					throw argErr(app, c.getName(), arg[1]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 * OsculatingCircle[<Point>,<Function>],OsculatingCircle[<Point>,<Curve>]
 * 
 * @author Victor Franco Espino
 */

class CmdOsculatingCircle extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdOsculatingCircle(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoFunctionable()))) {
				GeoElement[] ret = { kernel.OsculatingCircle(c.getLabel(),
						(GeoPoint) arg[0], (GeoFunction) arg[1]) };
				return ret;
			} else if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoCurveCartesian()))) {
				GeoElement[] ret = { kernel.OsculatingCircleCurve(c.getLabel(),
						(GeoPoint) arg[0], (GeoCurveCartesian) arg[1]) };
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				else
					throw argErr(app, c.getName(), arg[1]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 * TableText[<Matrix>],TableText[<Matrix>,<Point>]
 */

class CmdTableText extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdTableText(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {

		case 1:
			if ((ok[0] = (arg[0].isGeoList()))) {
				GeoList list = (GeoList) arg[0];

				if (list.size() == 0)
					throw argErr(app, c.getName(), arg[0]);

				if (list.get(0).isGeoList()) { // list of lists: no need to wrap
					GeoElement[] ret = { kernel.TableText(c.getLabel(),
							(GeoList) arg[0], null) };
					return ret;
				} else {
					list = wrapInList(kernel, arg, arg.length, -1);
					if (list != null) {
						GeoElement[] ret = { kernel.TableText(c.getLabel(),
								list, null) };
						return ret;
					}
					throw argErr(app, c.getName(), arg[0]);
				}
			} else {
				throw argErr(app, c.getName(), arg[0]);
			}

		case 2:
			if (ok[0] = (arg[0].isGeoList()) && (arg[1].isGeoText())) {
				GeoList list = (GeoList) arg[0];

				if (list.size() == 0)
					throw argErr(app, c.getName(), arg[0]);

				if (list.get(0).isGeoList()) { // list of lists: no need to wrap
					GeoElement[] ret = { kernel.TableText(c.getLabel(),
							(GeoList) arg[0], (GeoText) arg[1]) };
					return ret;
				} else {
					list = wrapInList(kernel, arg, arg.length - 1, -1);
					if (list != null) {
						GeoElement[] ret = { kernel.TableText(c.getLabel(),
								list, (GeoText) arg[1]) };
						return ret;
					}
					throw argErr(app, c.getName(), arg[0]);
				}
			}
			if (ok[0] = (arg[0].isGeoList()) && (arg[1].isGeoList())) {
				// two lists, no alignment
				GeoList list = wrapInList(kernel, arg, arg.length, -1);
				if (list != null) {
					GeoElement[] ret = { kernel.TableText(c.getLabel(), list,
							null) };
					return ret;
				}

			} else {
				throw argErr(app, c.getName(), arg[0]);
			}

		case 0:
			throw argNumErr(app, c.getName(), n);

		default:
			// try to create list of numbers
			GeoList list;
			if (arg[arg.length - 1].isGeoText()) {
				list = wrapInList(kernel, arg, arg.length - 1, -1);
				if (list != null) {
					GeoElement[] ret = { kernel.TableText(c.getLabel(), list,
							(GeoText) arg[arg.length - 1]) };
					return ret;
				}
			} else {
				list = wrapInList(kernel, arg, arg.length, -1);
				if (list != null) {
					GeoElement[] ret = { kernel.TableText(c.getLabel(), list,
							null) };
					return ret;
				}
			}
			throw argErr(app, c.getName(), arg[0]);
		}
	}
}

/**
 * StemPlot
 */
class CmdStemPlot extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdStemPlot(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {

		case 1:
			if ((ok[0] = (arg[0].isGeoList()))) {
				GeoList list = (GeoList) arg[0];

				GeoElement[] ret = { kernel.StemPlot(c.getLabel(), list) };
				return ret;
			} else {
				throw argErr(app, c.getName(), arg[0]);
			}

		case 2:
			if (!arg[0].isGeoList()) {
				throw argErr(app, c.getName(), arg[0]);
			}
			if (!arg[1].isGeoNumeric()) {
				throw argErr(app, c.getName(), arg[1]);
			}

			GeoElement[] ret = { kernel.StemPlot(c.getLabel(),
					(GeoList) arg[0], (GeoNumeric) arg[1]) };
			return ret;

		case 0:
			throw argNumErr(app, c.getName(), n);

		default:

			GeoList list = wrapInList(kernel, arg, arg.length, -1);
			if (list != null) {
				GeoElement[] ret2 = { kernel.StemPlot(c.getLabel(), list) };
				return ret2;
			}

			throw argErr(app, c.getName(), arg[0]);
		}
	}
}

/**
 *VerticalText
 */
class CmdVerticalText extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdVerticalText(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 1:
			if ((ok[0] = (arg[0].isGeoText()))) {
				GeoElement[] ret = { kernel.VerticalText(c.getLabel(),
						(GeoText) arg[0]) };
				return ret;
			} else {
				throw argErr(app, c.getName(), arg[0]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *RotateText
 */
class CmdRotateText extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdRotateText(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 2:
			if ((ok[0] = (arg[0].isGeoText()))
					&& (ok[1] = arg[1].isGeoNumeric())) {
				GeoElement[] ret = { kernel.RotateText(c.getLabel(),
						(GeoText) arg[0], (GeoNumeric) arg[1]) };
				return ret;
			} else {
				throw argErr(app, c.getName(), ok[0] ? arg[1] : arg[0]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *Object
 */
class CmdObject extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdObject(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 1:
			if ((ok[0] = (arg[0].isGeoText()))) {
				GeoElement[] ret = { kernel.Object(c.getLabel(),
						(GeoText) arg[0]) };
				return ret;
			} else {
				throw argErr(app, c.getName(), arg[0]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *SlowPlot
 */
class CmdSlowPlot extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSlowPlot(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 1:
			if ((ok[0] = (arg[0].isGeoFunctionable()))) {

				GeoNumeric var = new GeoNumeric(cons, 0.0);

				arg[0].setEuclidianVisible(false);
				arg[0].update();

				var.setLabel(null); // set label to next available
				var.setEuclidianVisible(true);
				var.setIntervalMin(0.0);
				var.setIntervalMax(1.0);
				var.setAnimating(true);
				var.setAnimationStep(0.01);
				var.setAnimationType(GeoElement.ANIMATION_INCREASING);
				var.update();

				StringBuilder sb = new StringBuilder();
				sb.append("Function[");
				sb.append(arg[0].getLabel());
				sb.append(",x(Corner[1]), x(Corner[1]) (1-");
				sb.append(var.getLabel());
				sb.append(") + x(Corner(2)) ");
				sb.append(var.getLabel());
				sb.append("]");

				kernel.getAnimatonManager().startAnimation();
				try {
					return kernel.getAlgebraProcessor()
							.processAlgebraCommandNoExceptionHandling(
									sb.toString(), true, false, true);
				} catch (Exception e) {
					e.printStackTrace();
					throw argErr(app, c.getName(), arg[0]);
				} catch (MyError e) {
					e.printStackTrace();
					throw argErr(app, c.getName(), arg[0]);
				}
			} else {
				throw argErr(app, c.getName(), arg[0]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 * SelectedElement[ <list>, <n> ] SelectedElement[ <point>, <n> ]
 */
class CmdSelectedElement extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */

	public CmdSelectedElement(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			// list
			if (arg[0].isGeoList())

			{
				GeoElement[] ret = { kernel.SelectedElement(c.getLabel(),
						(GeoList) arg[0]) };
				return ret;
			}

			// error
			else {
				throw argErr(app, c.getName(), arg[0]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 * SelectedElement[ <list>, <n> ] SelectedElement[ <point>, <n> ]
 */
class CmdSelectedIndex extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSelectedIndex(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			// list
			if (arg[0].isGeoList())

			{
				GeoElement[] ret = { kernel.SelectedIndex(c.getLabel(),
						(GeoList) arg[0]) };
				return ret;
			}

			// error
			else {
				throw argErr(app, c.getName(), arg[0]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *ToolImage
 */
class CmdToolImage extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdToolImage(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 1:
			if ((ok[0] = (arg[0].isGeoNumeric()))) {

				int mode = (int) ((GeoNumeric) arg[0]).getDouble();

				String modeStr = kernel.getModeText(mode)
						.toLowerCase(Locale.US);

				if ("".equals(modeStr))
					throw argErr(app, c.getName(), arg[0]);

				Image im = app.getImageManager().getImageResource(
						"/geogebra/gui/toolbar/images/mode_" + modeStr
								+ "_32.gif");

				BufferedImage image = ImageManager.toBufferedImage(im);
				String fileName = app.createImage(image, "tool.png");

				GeoImage geoImage = new GeoImage(app.getKernel()
						.getConstruction());
				geoImage.setImageFileName(fileName);
				geoImage.setTooltipMode(GeoElement.TOOLTIP_OFF);

				boolean oldState = cons.isSuppressLabelsActive();
				cons.setSuppressLabelCreation(true);
				GeoPoint corner = new GeoPoint(cons, null, 0, 0, 1);
				cons.setSuppressLabelCreation(oldState);
				try {
					geoImage.setStartPoint(corner);
				} catch (CircularDefinitionException e) {
				}
				geoImage.setLabel(null);

				GeoElement[] ret = {};
				return ret;

			} else {
				throw argErr(app, c.getName(), arg[0]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *Cell
 */
class CmdCell extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCell(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean ok = false;
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 2:
			if ((ok = (arg[0].isNumberValue()) && arg[1].isNumberValue())) {
				GeoElement[] ret = { kernel.Cell(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1]) };
				return ret;
			} else {
				throw argErr(app, c.getName(), ok ? arg[1] : arg[0]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *ColumnName
 */
class CmdColumnName extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdColumnName(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 1:

			if (arg[0].getLabel() != null
					&& GeoElement.isSpreadsheetLabel(arg[0].getLabel())) {
				GeoElement[] ret = { kernel.ColumnName(c.getLabel(), arg[0]) };

				return ret;
			} else {
				throw argErr(app, c.getName(), arg[0]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *Hull
 */
class CmdHull extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdHull(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 2:

			if (arg[0].isGeoList() && arg[1].isGeoNumeric()) {
				GeoElement[] ret = { kernel.Hull(c.getLabel(),
						(GeoList) arg[0], (GeoNumeric) arg[1]) };
				return ret;
			} else

				throw argErr(app, c.getName(), arg[arg[0].isGeoList() ? 1 : 0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}

/**
 *Append
 */
class CmdAppend extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdAppend(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 2:

			if (arg[0].isGeoList()) {
				GeoElement[] ret = { kernel.Append(c.getLabel(),
						(GeoList) arg[0], arg[1]) };
				return ret;
			} else if (arg[1].isGeoList()) {
				GeoElement[] ret = { kernel.Append(c.getLabel(), arg[0],
						(GeoList) arg[1]) };
				return ret;
			} else

				throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}

/**
 *Append
 */
class CmdIndexOf extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdIndexOf(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 2:
			if (arg[1].isGeoText() && arg[0].isGeoText()) {
				GeoElement[] ret = { kernel.IndexOf(c.getLabel(),
						(GeoText) arg[0], (GeoText) arg[1]) };
				return ret;
			} else if (arg[1].isGeoList()) {
				GeoElement[] ret = { kernel.IndexOf(c.getLabel(), arg[0],
						(GeoList) arg[1]) };
				return ret;
			} else

				throw argErr(app, c.getName(), arg[1]);
		case 3:
			boolean[] ok = new boolean[2];
			if ((ok[0] = arg[1].isGeoText() && arg[0].isGeoText())
					&& (ok[1] = arg[2].isNumberValue())) {
				GeoElement[] ret = { kernel.IndexOf(c.getLabel(),
						(GeoText) arg[0], (GeoText) arg[1],
						(NumberValue) arg[2]) };
				return ret;
			} else if ((ok[0] = arg[1].isGeoList())
					&& (ok[1] = arg[2].isNumberValue())) {
				GeoElement[] ret = { kernel.IndexOf(c.getLabel(), arg[0],
						(GeoList) arg[1], (NumberValue) arg[2]) };
				return ret;
			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[1]);
			throw argErr(app, c.getName(), arg[2]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}

/**
 *Join
 */
class CmdJoin extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdJoin(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 1:

			ok[0] = arg[0].isGeoList();

			if (ok[0]) {
				GeoElement[] ret = { kernel
						.Join(c.getLabel(), (GeoList) arg[0]) };
				return ret;
			} else

			if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else
				throw argErr(app, c.getName(), arg[1]);

		default:
			// try to create list of numbers
			GeoList list = wrapInList(kernel, arg, arg.length,
					GeoElement.GEO_CLASS_LIST);
			if (list != null) {
				GeoElement[] ret = { kernel.Join(c.getLabel(), list) };
				return ret;
			}
			throw argNumErr(app, c.getName(), n);
		}
	}

}

/**
 *Union
 */
class CmdUnion extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdUnion(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 2:

			if (arg[0].isGeoList() && arg[1].isGeoList()) {
				GeoElement[] ret = { kernel.Union(c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1]) };
				return ret;
			} else
				if (arg[0].isGeoPolygon() && arg[1].isGeoPolygon()) {
					GeoElement[] ret = kernel.Union(c.getLabels(), (GeoPolygon) arg[0],
							(GeoPolygon) arg[1]);
					return ret;
				} else
					throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}

/**
 *Insert
 */
class CmdInsert extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdInsert(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 3:

			if (arg[1].isGeoList() && arg[2].isGeoNumeric()) {
				GeoElement[] ret = { kernel.Insert(c.getLabel(), arg[0],
						(GeoList) arg[1], (GeoNumeric) arg[2]) };
				return ret;
			} else

				throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}

/**
 *IsInteger
 */
class CmdIsInteger extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdIsInteger(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 1:

			if (arg[0].isGeoNumeric()) {
				GeoElement[] ret = { kernel.IsInteger(c.getLabel(),
						(GeoNumeric) arg[0]) };
				return ret;
			} else

				throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}

/**
 *PrimeFactors
 */
class CmdPrimeFactors extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdPrimeFactors(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 1:

			if (arg[0].isGeoNumeric()) {
				GeoElement[] ret = { kernel.PrimeFactors(c.getLabel(),
						(GeoNumeric) arg[0]) };
				return ret;
			} else

				throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}

/**
 *RandomPoisson
 */
class CmdRandomPoisson extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdRandomPoisson(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 1:

			if (arg[0].isGeoNumeric()) {
				GeoElement[] ret = { kernel.RandomPoisson(c.getLabel(),
						(GeoNumeric) arg[0]) };
				return ret;
			} else

				throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}

/**
 *FractionText
 */
class CmdFractionText extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFractionText(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 1:

			if (arg[0].isGeoNumeric()) {
				GeoElement[] ret = { kernel.FractionText(c.getLabel(),
						(GeoNumeric) arg[0]) };
				return ret;
			} else

				throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}

/**
 *SurdText
 */
class CmdSurdText extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSurdText(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 1:

			if (arg[0].isGeoNumeric()) {
				GeoElement[] ret = { kernel.SurdText(c.getLabel(),
						(GeoNumeric) arg[0]) };
				return ret;
			} else if (arg[0].isGeoPoint()) {
				GeoElement[] ret = { kernel.SurdText(c.getLabel(),
						(GeoPoint) arg[0]) };
				return ret;
			} else

				throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}

/**
 *KeepIf
 */
class CmdKeepIf extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdKeepIf(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);
Application.debug(n);
		switch (n) {
		case 2:

			if (ok[0] = (arg[0] instanceof GeoFunction)) {
				GeoFunction booleanFun = (GeoFunction) arg[0];
				if ((ok[0] = booleanFun.isBooleanFunction())
						&& (ok[1] = arg[1].isGeoList())) {

					GeoElement[] ret = { kernel.KeepIf(c.getLabel(),
							(GeoFunction) booleanFun, ((GeoList) arg[1])) };
					return ret;
				}
			}

			if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else
				throw argErr(app, c.getName(), arg[1]);


		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}

/**
 *AxisStepX
 */
class CmdAxisStepX extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdAxisStepX(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 0:

			GeoElement[] ret = { kernel.AxisStepX(c.getLabel()) };
			return ret;

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}

/**
 *AxisStepY
 */
class CmdAxisStepY extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdAxisStepY(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 0:

			GeoElement[] ret = { kernel.AxisStepY(c.getLabel()) };
			return ret;

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}

/**
 *Simplify
 */
class CmdSimplify extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSimplify(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 1:
			if ((arg[0].isCasEvaluableObject())) {
				GeoElement[] ret = { kernel.Simplify(c.getLabel(),
						(CasEvaluableFunction) arg[0]) };
				return ret;
			} else if ((arg[0].isGeoText())) {
				GeoElement[] ret = { kernel.Simplify(c.getLabel(),
						(GeoText) arg[0]) };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[0]);

			// more than one argument
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *DynamicCoordinates
 */
class CmdDynamicCoordinates extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdDynamicCoordinates(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 3:
			boolean[] ok = new boolean[2];
			if ((ok[0] = (arg[0].isGeoPoint() && arg[0].isMoveable()))
					&& (ok[1] = arg[1].isNumberValue())
					&& (arg[2].isNumberValue())) {
				GeoElement[] ret = { kernel.DynamicCoordinates(c.getLabel(),
						(GeoPoint) arg[0], (NumberValue) arg[1],
						(NumberValue) arg[2]) };
				return ret;
			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else
				throw argErr(app, c.getName(), arg[2]);

			// more than one argument
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *TDistribution
 */
class CmdTDistribution extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdTDistribution(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		
		arg = resArgs(c);

		boolean cumulative = false; // default for n=2
		switch (n) {
		case 3:
			if (!arg[1].isGeoFunction() || !((GeoFunction)arg[1]).toString().equals("x")) {
				throw argErr(app, c.getName(), arg[1]);
			}
			
			if (arg[2].isGeoBoolean()) {
				cumulative = ((GeoBoolean)arg[2]).getBoolean();
			} else
				throw argErr(app, c.getName(), arg[2]);

			// fall through
		case 2:			
			if ((ok[0] = arg[0].isNumberValue()) ) {
				if (arg[1].isGeoFunction() && ((GeoFunction)arg[1]).toString().equals("x")) {

					// needed for eg Normal[1, 0.001, x] 
					kernel.setTemporaryPrintFigures(15);
					String v = arg[0].getLabel();
					kernel.restorePrintAccuracy();
					String command;
					
					if (cumulative) {
						command = "0.5+sign(x)/2*(betaRegularized(("+v+")/2,0.5,1)-betaRegularized(("+v+")/2,0.5,("+v+")/("+v+"+x^2)))";
					} else {
						command = "gamma(("+v+"+1)/2)*(1+x^2/("+v+"))^(-(("+v+"+1)/2))/(gamma(("+v+")/2)*sqrt(pi*("+v+")))";
					}
					
					
					GeoElement[] ret = kernel.getAlgebraProcessor().processAlgebraCommand(command, true);
					return ret;


				} else if (arg[1].isNumberValue()) {
					GeoElement[] ret = { kernel.TDistribution(c.getLabel(),
							(NumberValue) arg[0], (NumberValue) arg[1]) };
					return ret;
				} else
					throw argErr(app, c.getName(), arg[1]);

				} else 
					throw argErr(app, c.getName(), arg[0]);
				

			default:
				throw argNumErr(app, c.getName(), n);
			}
		}
	}


/**
 *InverseTDistribution
 */
class CmdInverseTDistribution extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdInverseTDistribution(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isNumberValue())
					&& (ok[1] = arg[1].isNumberValue())) {
				GeoElement[] ret = { kernel.InverseTDistribution(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1]) };
				return ret;

			} else
				throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}

/**
 *Chi Squared Distribution
 */
class CmdChiSquared extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdChiSquared(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		
		arg = resArgs(c);

		boolean cumulative = false; // default for n=2
		switch (n) {
		case 3:
			
			if (!arg[1].isGeoFunction() || !((GeoFunction)arg[1]).toString().equals("x")) {
				throw argErr(app, c.getName(), arg[1]);
			}
			
			if (arg[2].isGeoBoolean()) {
				cumulative = ((GeoBoolean)arg[2]).getBoolean();
			} else
				throw argErr(app, c.getName(), arg[2]);

			// fall through
		case 2:			
			if ((ok[0] = arg[0].isNumberValue()) ) {
				if (arg[1].isGeoFunction() && ((GeoFunction)arg[1]).toString().equals("x")) {

					// needed for eg Normal[1, 0.001, x] 
					kernel.setTemporaryPrintFigures(15);
					String k = arg[0].getLabel();
					String command = null;
					kernel.restorePrintAccuracy();
					
					if (cumulative) {
						command = "If[x<0,0,gamma(("+k+")/2,x/2)/gamma(("+k+")/2)]";
					} else {
						command = "If[x<0,0,(x^(("+k+")/2-1)exp(-x/2))/(2^(("+k+")/2)gamma(("+k+")/2))]";
					}
					
					
					GeoElement[] ret = kernel.getAlgebraProcessor().processAlgebraCommand(command, true);
					return ret;


				} else if (arg[1].isNumberValue()) {
					GeoElement[] ret = { kernel.ChiSquared(c.getLabel(),
							(NumberValue) arg[0], (NumberValue) arg[1]) };
					return ret;
				} else
					throw argErr(app, c.getName(), arg[1]);

				} else 
					throw argErr(app, c.getName(), arg[0]);
				

			default:
				throw argNumErr(app, c.getName(), n);
			}
		}
	}


/**
 *InverseChiSquared
 */
class CmdInverseChiSquared extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdInverseChiSquared(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isNumberValue())
					&& (ok[1] = arg[1].isNumberValue())) {
				GeoElement[] ret = { kernel.InverseChiSquared(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1]) };
				return ret;

			} else
				throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}

/**
 *Exponential distribution
 */
class CmdExponential extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdExponential(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		
		arg = resArgs(c);

		boolean cumulative = false; // default for n=2
		switch (n) {
		case 3:
			if (!arg[1].isGeoFunction() || !((GeoFunction)arg[1]).toString().equals("x")) {
				throw argErr(app, c.getName(), arg[1]);
			}
			
			if (arg[2].isGeoBoolean()) {
				cumulative = ((GeoBoolean)arg[2]).getBoolean();
			} else
				throw argErr(app, c.getName(), arg[2]);

			// fall through
		case 2:			
			if ((ok[0] = arg[0].isNumberValue()) ) {
				if (arg[1].isGeoFunction() && ((GeoFunction)arg[1]).toString().equals("x")) {

					// needed for eg Normal[1, 0.001, x] 
					kernel.setTemporaryPrintFigures(15);
					String l = arg[0].getLabel();
					kernel.restorePrintAccuracy();
					String command = null;
					
					if (cumulative) {
						command="If[x<0,0,1-exp(-("+l+")x)]";
					} else {
						command="If[x<0,0,("+l+")exp(-("+l+")x)]";
					}
					
					
					GeoElement[] ret = kernel.getAlgebraProcessor().processAlgebraCommand(command, true);
					return ret;


				} else if (arg[1].isNumberValue()) {
					GeoElement[] ret = { kernel.Exponential(c.getLabel(),
							(NumberValue) arg[0], (NumberValue) arg[1]) };
					return ret;
				} else
					throw argErr(app, c.getName(), arg[1]);

				} else 
					throw argErr(app, c.getName(), arg[0]);
				

			default:
				throw argNumErr(app, c.getName(), n);
			}
		}
	}


/**
 *InverseExponential
 */
class CmdInverseExponential extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdInverseExponential(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isNumberValue())
					&& (ok[1] = arg[1].isNumberValue())) {
				GeoElement[] ret = { kernel.InverseExponential(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1]) };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
/*
 * LogNormal distribution
 */
class CmdLogNormal extends CommandProcessor {

	public CmdLogNormal(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean ok;
		GeoElement[] arg;

		boolean cumulative = false; // default for n=3
		arg = resArgs(c);
		
		switch (n) {
		case 4:
			if (!arg[2].isGeoFunction() || !((GeoFunction)arg[2]).toString().equals("x")) {
				throw argErr(app, c.getName(), arg[2]);
			}
			
			if (arg[3].isGeoBoolean()) {
				cumulative = ((GeoBoolean)arg[3]).getBoolean();
			} else
				throw argErr(app, c.getName(), arg[3]);
			
			// fall through
		case 3:			
			if ((ok = arg[0].isNumberValue()) && (arg[1].isNumberValue())) {
				if (arg[2].isGeoFunction() && ((GeoFunction)arg[2]).toString().equals("x")) {
									
					// needed for eg Normal[1, 0.001, x] 
					kernel.setTemporaryPrintFigures(15);
					String mean = arg[0].getLabel();
					String sd = arg[1].getLabel();
					kernel.restorePrintAccuracy();
					
					if (cumulative) {
						GeoElement[] ret = kernel.getAlgebraProcessor().processAlgebraCommand( "If[x<0,0,1/2 erf((ln(x)-("+mean+"))/(sqrt(2)*abs("+sd+"))) + 1/2]", true );
						
						return ret;
						
					} else {
						GeoElement[] ret = kernel.getAlgebraProcessor().processAlgebraCommand( "If[x<0,0,1/(x sqrt(2 * pi) * abs("+sd+"))*exp(-((ln(x)-("+mean+"))^2/(2*("+sd+")^2)))]", true );
						
						return ret;
					}
					
				} else if (arg[2].isNumberValue()) 
				{
					// needed for eg Normal[1, 0.001, x] 
					kernel.setTemporaryPrintFigures(15);
					String mean = arg[0].getLabel();
					String sd = arg[1].getLabel();
					String x = arg[2].getLabel();
					kernel.restorePrintAccuracy();
					GeoElement[] ret = kernel.getAlgebraProcessor().processAlgebraCommand( "1/2 erf((ln(If["+x+"<0,0,"+x+"])-("+mean+"))/(sqrt(2)*abs("+sd+"))) + 1/2", true );
					return ret;
					
				}  else
					throw argErr(app, c.getName(), arg[2]);
		} else throw argErr(app, c.getName(), ok ? arg[1] : arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/*
 * Logistic Distribution
 */
class CmdLogistic extends CommandProcessor {

	public CmdLogistic(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean ok;
		GeoElement[] arg;

		boolean cumulative = false; // default for n=3
		arg = resArgs(c);
		
		switch (n) {
		case 4:
			if (!arg[2].isGeoFunction() || !((GeoFunction)arg[2]).toString().equals("x")) {
				throw argErr(app, c.getName(), arg[2]);
			}
			
			if (arg[3].isGeoBoolean()) {
				cumulative = ((GeoBoolean)arg[3]).getBoolean();
			} else
				throw argErr(app, c.getName(), arg[3]);
			
			// fall through
		case 3:			
			if ((ok = arg[0].isNumberValue()) && (arg[1].isNumberValue())) {
				if (arg[2].isGeoFunction() && ((GeoFunction)arg[2]).toString().equals("x")) {
									
					// needed for eg Normal[1, 0.001, x] 
					kernel.setTemporaryPrintFigures(15);
					String m = arg[0].getLabel();
					String s = arg[1].getLabel();
					kernel.restorePrintAccuracy();
					
					if (cumulative) {
						GeoElement[] ret = kernel.getAlgebraProcessor().processAlgebraCommand( "1/(1+exp(-(x-("+m+"))/abs("+s+")))", true );
						
						return ret;
						
					} else {
						GeoElement[] ret = kernel.getAlgebraProcessor().processAlgebraCommand( "exp(-(x-("+m+"))/abs("+s+"))/(abs("+s+")*(1+exp(-(x-("+m+"))/abs("+s+")))^2)", true );
						
						return ret;
					}
					
				} else if (arg[2].isNumberValue()) 
				{
					// needed for eg Normal[1, 0.001, x] 
					kernel.setTemporaryPrintFigures(15);
					String m = arg[0].getLabel();
					String s = arg[1].getLabel();
					String x = arg[2].getLabel();
					kernel.restorePrintAccuracy();
					GeoElement[] ret = kernel.getAlgebraProcessor().processAlgebraCommand( "1/(1+exp(-("+x+"-("+m+"))/abs("+s+")))", true );
					return ret;
					
				}  else
					throw argErr(app, c.getName(), arg[2]);
		} else throw argErr(app, c.getName(), ok ? arg[1] : arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

	class CmdUniform extends CommandProcessor {

		public CmdUniform(Kernel kernel) {
			super(kernel);
		}

		public GeoElement[] process(Command c) throws MyError {
			int n = c.getArgumentNumber();
			boolean ok;
			GeoElement[] arg;

			boolean cumulative = false; // default for n=3
			arg = resArgs(c);
			
			switch (n) {
			case 4:
				if (!arg[2].isGeoFunction() || !((GeoFunction)arg[2]).toString().equals("x")) {
					throw argErr(app, c.getName(), arg[1]);
				}
				
				if (arg[3].isGeoBoolean()) {
					cumulative = ((GeoBoolean)arg[3]).getBoolean();
				} else
					throw argErr(app, c.getName(), arg[3]);
				
				// fall through
			case 3:			
				if ((ok = arg[0].isNumberValue()) && (arg[1].isNumberValue())) {
					if (arg[2].isGeoFunction() && ((GeoFunction)arg[2]).toString().equals("x")) {
										
						// needed for eg Normal[1, 0.001, x] 
						kernel.setTemporaryPrintFigures(15);
						String a = arg[0].getLabel();
						String b = arg[1].getLabel();
						kernel.restorePrintAccuracy();
						
						if (cumulative) {
							GeoElement[] ret = kernel.getAlgebraProcessor().processAlgebraCommand( "If[x<Min["+a+","+b+"],0,If[x>Max["+a+","+b+"],1,(x-Min["+a+","+b+"])/abs("+b+"-("+a+"))]]", true );
							
							return ret;
							
						} else {
							GeoElement[] ret = kernel.getAlgebraProcessor().processAlgebraCommand( "If[x<Min["+a+","+b+"],0,If[x>Max["+a+","+b+"],0,1/abs("+b+"-("+a+"))]]", true );
							
							return ret;
						}
						
					} else if (arg[2].isNumberValue()) 
					{
						// needed for eg Normal[1, 0.001, x] 
						kernel.setTemporaryPrintFigures(15);
						String a = arg[0].getLabel();
						String b = arg[1].getLabel();
						String x = arg[2].getLabel();
						kernel.restorePrintAccuracy();
						GeoElement[] ret = kernel.getAlgebraProcessor().processAlgebraCommand( "If["+x+"<Min["+a+","+b+"],0,If["+x+">Max["+a+","+b+"],1,("+x+"-Min["+a+","+b+"])/abs("+b+"-("+a+"))]]", true );
						return ret;
						
					}  else
						throw argErr(app, c.getName(), arg[2]);
			} else throw argErr(app, c.getName(), ok ? arg[1] : arg[0]);

			default:
				throw argNumErr(app, c.getName(), n);
			}
		}

	}

	/**
	 *Erlang Distribution
	 */
	class CmdErlang extends CommandProcessor {

		/**
		 * Create new command processor
		 * 
		 * @param kernel
		 *            kernel
		 */
		public CmdErlang(Kernel kernel) {
			super(kernel);
		}

		public GeoElement[] process(Command c) throws MyError {
			int n = c.getArgumentNumber();
			boolean[] ok = new boolean[n];
			GeoElement[] arg;
			
			arg = resArgs(c);

			boolean cumulative = false; // default for n=3
			switch (n) {
			case 4:
				if (!arg[2].isGeoFunction() || !((GeoFunction)arg[2]).toString().equals("x")) {
					throw argErr(app, c.getName(), arg[1]);
				}
				
				if (arg[3].isGeoBoolean()) {
					cumulative = ((GeoBoolean)arg[3]).getBoolean();
				} else
					throw argErr(app, c.getName(), arg[3]);

				// fall through
			case 3:			
				if ((ok[0] = arg[0].isNumberValue()) && (ok[1] = arg[1].isNumberValue())) {
					if (arg[2].isGeoFunction() && ((GeoFunction)arg[2]).toString().equals("x")) {

						// needed for eg Normal[1, 0.001, x] 
						kernel.setTemporaryPrintFigures(15);
						String k = arg[0].getLabel();
						String l = arg[1].getLabel();
						kernel.restorePrintAccuracy();
						String command;
						
						if (cumulative) {
							command = "If[x<0,0,gamma("+k+",("+l+")x)/("+k+"-1)!]";
						} else {
							command = "If[x<0,0,(("+l+")^("+k+")x^("+k+"-1)exp(-("+l+")x))/("+k+"-1)!]";
						}						
						
						GeoElement[] ret = kernel.getAlgebraProcessor().processAlgebraCommand(command, true);
						return ret;


					} else if (arg[2].isNumberValue()) {
						// needed for eg Normal[1, 0.001, x] 
						kernel.setTemporaryPrintFigures(15);
						String k = arg[0].getLabel();
						String l = arg[1].getLabel();
						String x = arg[2].getLabel();
						kernel.restorePrintAccuracy();
						GeoElement[] ret = kernel.getAlgebraProcessor().processAlgebraCommand("If[x<0,0,(("+l+")^("+k+")("+x+")^("+k+"-1)exp(-("+l+")("+x+")))/("+k+"-1)!]", true);
						return ret;
					} else
						throw argErr(app, c.getName(), arg[2]);

					} else if (!ok[0])
						throw argErr(app, c.getName(), arg[0]);
					else if (!ok[1])
						throw argErr(app, c.getName(), arg[1]);
					

				default:
					throw argNumErr(app, c.getName(), n);
				}
			}
		}

	/**
	 *FDistribution
	 */
	class CmdFDistribution extends CommandProcessor {

		/**
		 * Create new command processor
		 * 
		 * @param kernel
		 *            kernel
		 */
		public CmdFDistribution(Kernel kernel) {
			super(kernel);
		}

		public GeoElement[] process(Command c) throws MyError {
			int n = c.getArgumentNumber();
			boolean[] ok = new boolean[n];
			GeoElement[] arg;
			
			arg = resArgs(c);

			boolean cumulative = false; // default for n=3
			switch (n) {
			case 4:
				if (!arg[2].isGeoFunction() || !((GeoFunction)arg[2]).toString().equals("x")) {
					throw argErr(app, c.getName(), arg[2]);
				}
				
				if (arg[3].isGeoBoolean()) {
					cumulative = ((GeoBoolean)arg[3]).getBoolean();
				} else
					throw argErr(app, c.getName(), arg[3]);

				// fall through
			case 3:			
				if ((ok[0] = arg[0].isNumberValue()) && (ok[1] = arg[1].isNumberValue())) {
					if (arg[2].isGeoFunction() && ((GeoFunction)arg[2]).toString().equals("x")) {

						// needed for eg Normal[1, 0.001, x] 
						kernel.setTemporaryPrintFigures(15);
						String d1 = arg[0].getLabel();
						String d2 = arg[1].getLabel();
						kernel.restorePrintAccuracy();
						String command;
						
						if (cumulative) {
							command = "If[x<0,0,betaRegularized(("+d1+")/2,("+d2+")/2,("+d1+")*x/(("+d1+")*x+"+d2+"))]";
						} else {
							command = "If[x<0,0,((("+d1+")*x)^(("+d1+")/2)*("+d2+")^(("+d2+")/2))/(x*(("+d1+")*x+"+d2+")^(("+d1+"+"+d2+")/2)*beta(("+d1+")/2,("+d2+")/2))]";
						}
						
						
						GeoElement[] ret = kernel.getAlgebraProcessor().processAlgebraCommand(command, true);
						return ret;


					} else if (arg[2].isNumberValue()) {
						GeoElement[] ret = { kernel.FDistribution(c.getLabel(),
								(NumberValue) arg[0], (NumberValue) arg[1],
								(NumberValue) arg[2]) };
						return ret;
					} else
						throw argErr(app, c.getName(), arg[2]);

					} else if (!ok[0])
						throw argErr(app, c.getName(), arg[0]);
					else if (!ok[1])
						throw argErr(app, c.getName(), arg[1]);
					

				default:
					throw argNumErr(app, c.getName(), n);
				}
			}
		}

/**
 *InverseFDistribution
 */
class CmdInverseFDistribution extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdInverseFDistribution(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 3:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isNumberValue())
					&& (ok[1] = arg[1].isNumberValue())
					&& (ok[2] = arg[2].isNumberValue())) {
				GeoElement[] ret = { kernel.InverseFDistribution(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1],
						(NumberValue) arg[2]) };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else
				throw argErr(app, c.getName(), arg[2]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}

/**
 *Gamma Distribution
 */
class CmdGamma extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdGamma(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		
		arg = resArgs(c);

		boolean cumulative = false; // default for n=3
		switch (n) {
		case 4:
			if (!arg[2].isGeoFunction() || !((GeoFunction)arg[2]).toString().equals("x")) {
				throw argErr(app, c.getName(), arg[2]);
			}
			
			if (arg[3].isGeoBoolean()) {
				cumulative = ((GeoBoolean)arg[3]).getBoolean();
			} else
				throw argErr(app, c.getName(), arg[3]);

			// fall through
		case 3:			
			if ((ok[0] = arg[0].isNumberValue()) && (ok[1] = arg[1].isNumberValue())) {
				if (arg[2].isGeoFunction() && ((GeoFunction)arg[2]).toString().equals("x")) {

					// needed for eg Normal[1, 0.001, x] 
					kernel.setTemporaryPrintFigures(15);
					String k = arg[0].getLabel();
					String t = arg[1].getLabel();
					kernel.restorePrintAccuracy();
					String command;
					
					if (cumulative) {
						command = "If[x<0,0,gamma("+k+",x/("+t+"))/gamma("+k+")]";
					} else {
						command = "If[x<0,0,x^("+k+"-1) exp(-x/("+t+"))/(gamma("+k+")("+t+")^("+k+"))]";
					}
					
					
					GeoElement[] ret = kernel.getAlgebraProcessor().processAlgebraCommand(command, true);
					return ret;


				} else if (arg[2].isNumberValue()) {
					GeoElement[] ret = { kernel.Gamma(c.getLabel(),
							(NumberValue) arg[0], (NumberValue) arg[1],
							(NumberValue) arg[2]) };
					return ret;
				} else
					throw argErr(app, c.getName(), arg[2]);

				} else if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				else if (!ok[1])
					throw argErr(app, c.getName(), arg[1]);
				

			default:
				throw argNumErr(app, c.getName(), n);
			}
		}
	}

/**
 *InverseGamma
 */
class CmdInverseGamma extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdInverseGamma(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 3:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isNumberValue())
					&& (ok[1] = arg[1].isNumberValue())
					&& (ok[2] = arg[2].isNumberValue())) {
				GeoElement[] ret = { kernel.InverseGamma(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1],
						(NumberValue) arg[2]) };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else
				throw argErr(app, c.getName(), arg[2]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}

/**
 *Cauchy Distribution
 */
class CmdCauchy extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCauchy(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		
		arg = resArgs(c);

		boolean cumulative = false; // default for n=3
		switch (n) {
		case 4:
			if (!arg[2].isGeoFunction() || !((GeoFunction)arg[2]).toString().equals("x")) {
				throw argErr(app, c.getName(), arg[2]);
			}
			
			if (arg[3].isGeoBoolean()) {
				cumulative = ((GeoBoolean)arg[3]).getBoolean();
			} else
				throw argErr(app, c.getName(), arg[3]);

			// fall through
		case 3:			
			if ((ok[0] = arg[0].isNumberValue()) && (ok[1] = arg[1].isNumberValue())) {
				if (arg[2].isGeoFunction() && ((GeoFunction)arg[2]).toString().equals("x")) {

					// needed for eg Normal[1, 0.001, x] 
					kernel.setTemporaryPrintFigures(15);
					String x0 = arg[0].getLabel();
					String g = arg[1].getLabel();
					kernel.restorePrintAccuracy();
					String command;
					
					if (cumulative) {
						command = "1/pi atan((x-("+x0+"))/abs("+g+"))+0.5";
					} else {
						command = "1/pi abs("+g+")/(("+g+")^2+(x-("+x0+"))^2)";
					}
					
					
					GeoElement[] ret = kernel.getAlgebraProcessor().processAlgebraCommand(command, true);
					return ret;


				} else if (arg[2].isNumberValue()) {
					GeoElement[] ret = { kernel.Cauchy(c.getLabel(),
							(NumberValue) arg[0], (NumberValue) arg[1],
							(NumberValue) arg[2]) };
					return ret;
				} else
					throw argErr(app, c.getName(), arg[2]);

				} else if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				else if (!ok[1])
					throw argErr(app, c.getName(), arg[1]);
				

			default:
				throw argNumErr(app, c.getName(), n);
			}
		}
	}
/**
 *InverseCauchy
 */
class CmdInverseCauchy extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdInverseCauchy(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 3:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isNumberValue())
					&& (ok[1] = arg[1].isNumberValue())
					&& (ok[2] = arg[2].isNumberValue())) {
				GeoElement[] ret = { kernel.InverseCauchy(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1],
						(NumberValue) arg[2]) };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else
				throw argErr(app, c.getName(), arg[2]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}

/**
 *Pascal
 */
class CmdPascal extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdPascal(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		
		case 2:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isNumberValue())
					&& (ok[1] = arg[1].isNumberValue())) {
				GeoElement[] ret = { kernel.Pascal(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1]) };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else
				throw argErr(app, c.getName(), arg[1]);

			
		case 3:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isNumberValue())
					&& (ok[1] = arg[1].isNumberValue())
					&& (ok[2] = arg[2].isGeoBoolean())) {
				GeoElement[] ret = { kernel.Pascal(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1], (GeoBoolean)arg[2]) };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else
				throw argErr(app, c.getName(), arg[2]);

		case 4:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isNumberValue())
					&& (ok[1] = arg[1].isNumberValue())
					&& (ok[2] = arg[2].isNumberValue())
					&& (ok[3] = arg[3].isGeoBoolean())) {
				GeoElement[] ret = { kernel.Pascal(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1],
						(NumberValue) arg[2], (GeoBoolean) arg[3]) };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg[2]);
			else
				throw argErr(app, c.getName(), arg[3]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *InversePascal
 */
class CmdInversePascal extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdInversePascal(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 3:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isNumberValue())
					&& (ok[1] = arg[1].isNumberValue())
					&& (ok[2] = arg[2].isNumberValue())) {
				GeoElement[] ret = { kernel.InversePascal(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1],
						(NumberValue) arg[2]) };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else
				throw argErr(app, c.getName(), arg[2]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}

/**
 *Binomial Distribution
 */
class CmdBinomialDist extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdBinomialDist(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		
		case 2:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isNumberValue())
					&& (ok[1] = arg[1].isNumberValue())) {
				GeoElement[] ret = { kernel.BinomialDist(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1]) };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else
				throw argErr(app, c.getName(), arg[1]);

			
		case 3:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isNumberValue())
					&& (ok[1] = arg[1].isNumberValue())
					&& (ok[2] = arg[2].isGeoBoolean())) {
				GeoElement[] ret = { kernel.BinomialDist(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1], (GeoBoolean)arg[2]) };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else
				throw argErr(app, c.getName(), arg[2]);

			
		case 4:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isNumberValue())
					&& (ok[1] = arg[1].isNumberValue())
					&& (ok[2] = arg[2].isNumberValue())
					&& (ok[2] = arg[3].isGeoBoolean())) {
				GeoElement[] ret = { kernel.BinomialDist(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1],
						(NumberValue) arg[2], (GeoBoolean) arg[3]) };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg[2]);
			else
				throw argErr(app, c.getName(), arg[3]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *Binomial Distribution
 */
class CmdBernoulli extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdBernoulli(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		
		case 2:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isNumberValue())
					&& (ok[1] = arg[1].isGeoBoolean())) {
				GeoElement[] ret = { kernel.Bernoulli(c.getLabel(),
						(NumberValue) arg[0], (GeoBoolean) arg[1]) };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else
				throw argErr(app, c.getName(), arg[1]);

			
		case 3:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isNumberValue())
					&& (ok[1] = arg[1].isNumberValue())
					&& (ok[2] = arg[2].isGeoBoolean())) {
				GeoElement[] ret = { kernel.BinomialDist(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1], (GeoBoolean)arg[2]) };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else
				throw argErr(app, c.getName(), arg[2]);

			
		case 4:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isNumberValue())
					&& (ok[1] = arg[1].isNumberValue())
					&& (ok[2] = arg[2].isNumberValue())
					&& (ok[2] = arg[3].isGeoBoolean())) {
				GeoElement[] ret = { kernel.BinomialDist(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1],
						(NumberValue) arg[2], (GeoBoolean) arg[3]) };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg[2]);
			else
				throw argErr(app, c.getName(), arg[3]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}


/**
 *InverseBinomial
 */
class CmdInverseBinomial extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdInverseBinomial(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 3:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isNumberValue())
					&& (ok[1] = arg[1].isNumberValue())
					&& (ok[2] = arg[2].isNumberValue())) {
				GeoElement[] ret = { kernel.InverseBinomial(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1],
						(NumberValue) arg[2]) };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else
				throw argErr(app, c.getName(), arg[2]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}

/**
 *Poisson Distribution
 */
class CmdPoisson extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdPoisson(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		
		case 1:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isNumberValue())) {
				GeoElement[] ret = { kernel.Poisson(c.getLabel(),
						(NumberValue) arg[0]) };
				return ret;
			}
			else
				throw argErr(app, c.getName(), arg[0]);

			
		case 2:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isNumberValue())
					&& (ok[1] = arg[1].isGeoBoolean())) {
				GeoElement[] ret = { kernel.Poisson(c.getLabel(),
						(NumberValue) arg[0], (GeoBoolean)arg[1]) };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else
				throw argErr(app, c.getName(), arg[1]);
		
		case 3:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isNumberValue())
					&& (ok[1] = arg[1].isNumberValue())
					&& (ok[2] = arg[2].isGeoBoolean())) {
				GeoElement[] ret = { kernel.Poisson(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1],
						(GeoBoolean) arg[2]) };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else
				throw argErr(app, c.getName(), arg[2]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *InversePoisson
 */
class CmdInversePoisson extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdInversePoisson(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isNumberValue())
					&& (ok[1] = arg[1].isNumberValue())) {
				GeoElement[] ret = { kernel.InversePoisson(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1]) };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}

/**
 *Weibull Distribution
 */
class CmdWeibull extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdWeibull(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		
		arg = resArgs(c);

		boolean cumulative = false; // default for n=3
		switch (n) {
		case 4:
			if (!arg[2].isGeoFunction() || !((GeoFunction)arg[2]).toString().equals("x")) {
				throw argErr(app, c.getName(), arg[2]);
			}
			
			if (arg[3].isGeoBoolean()) {
				cumulative = ((GeoBoolean)arg[3]).getBoolean();
			} else
				throw argErr(app, c.getName(), arg[3]);

			// fall through
		case 3:			
			if ((ok[0] = arg[0].isNumberValue()) && (ok[1] = arg[1].isNumberValue())) {
				if (arg[2].isGeoFunction() && ((GeoFunction)arg[2]).toString().equals("x")) {

					// needed for eg Normal[1, 0.001, x] 
					kernel.setTemporaryPrintFigures(15);
					String k = arg[0].getLabel();
					String l = arg[1].getLabel();
					kernel.restorePrintAccuracy();
					String command;
					
					if (cumulative) {
						command = "If[x<0,0,1-exp(-(x/("+l+"))^("+k+"))]";
					} else {
						command = "If[x<0,0,("+k+")/("+l+")(x/("+l+"))^("+k+"-1)exp(-(x/("+l+"))^("+k+"))]";
					}
					
					
					GeoElement[] ret = kernel.getAlgebraProcessor().processAlgebraCommand(command, true);
					return ret;


				} else if (arg[2].isNumberValue()) {
					GeoElement[] ret = { kernel.Weibull(c.getLabel(),
							(NumberValue) arg[0], (NumberValue) arg[1],
							(NumberValue) arg[2]) };
					return ret;
				} else
					throw argErr(app, c.getName(), arg[2]);

				} else if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				else if (!ok[1])
					throw argErr(app, c.getName(), arg[1]);
				

			default:
				throw argNumErr(app, c.getName(), n);
			}
		}
	}


/**
 *InverseWeibull
 */
class CmdInverseWeibull extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdInverseWeibull(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 3:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isNumberValue())
					&& (ok[1] = arg[1].isNumberValue())
					&& (ok[2] = arg[2].isNumberValue())) {
				GeoElement[] ret = { kernel.InverseWeibull(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1],
						(NumberValue) arg[2]) };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else
				throw argErr(app, c.getName(), arg[2]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}

/**
 *Zipf
 */
class CmdZipf extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdZipf(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isNumberValue())
					&& (ok[1] = arg[1].isNumberValue())) {
				GeoElement[] ret = {kernel.Zipf(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1]
						) };
				return ret;
				}
				else if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				else if (!ok[1])
					throw argErr(app, c.getName(), arg[1]);
		case 3:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isNumberValue())
					&& (ok[1] = arg[1].isNumberValue())					
					&& (ok[2] = arg[2].isGeoBoolean())) {
				GeoElement[] ret = { kernel.Zipf(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1],
						(GeoBoolean) arg[2]) };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg[2]);
			else
				throw argErr(app, c.getName(), arg[3]);
			
		case 4:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isNumberValue())
					&& (ok[1] = arg[1].isNumberValue())
					&& (ok[2] = arg[2].isNumberValue())
					&& (ok[3] = arg[3].isGeoBoolean())) {
				GeoElement[] ret = { kernel.Zipf(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1],
						(NumberValue) arg[2], (GeoBoolean) arg[3]) };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg[2]);
			else
				throw argErr(app, c.getName(), arg[3]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *Sample
 */
class CmdSample extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSample(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isGeoList())
					&& (ok[1] = arg[1].isNumberValue())) {
				GeoElement[] ret = { kernel.Sample(c.getLabel(),
						(GeoList) arg[0], (NumberValue) arg[1], null) };
				return ret;

			} else
				throw argErr(app, c.getName(), getBadArg(ok, arg));

		case 3:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isGeoList())
					&& (ok[1] = arg[1].isNumberValue())
					&& (ok[2] = arg[2].isGeoBoolean())) {
				GeoElement[] ret = { kernel.Sample(c.getLabel(),
						(GeoList) arg[0], (NumberValue) arg[1],
						(GeoBoolean) arg[2]) };
				return ret;

			} else
				throw argErr(app, c.getName(), getBadArg(ok, arg));

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *InverseZipf
 */
class CmdInverseZipf extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdInverseZipf(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 3:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isNumberValue())
					&& (ok[1] = arg[1].isNumberValue())
					&& (ok[2] = arg[2].isNumberValue())) {
				GeoElement[] ret = { kernel.InverseZipf(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1],
						(NumberValue) arg[2]) };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else
				throw argErr(app, c.getName(), arg[2]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}

/**
 *HyperGeometric
 */
class CmdHyperGeometric extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdHyperGeometric(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		
		case 3:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isNumberValue())
					&& (ok[1] = arg[1].isNumberValue())) {
				GeoElement[] ret = { kernel.HyperGeometric(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1], (NumberValue) arg[2]) };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else
				throw argErr(app, c.getName(), arg[2]);

			
		case 4:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isNumberValue())
					&& (ok[1] = arg[1].isNumberValue())
					&& (ok[2] = arg[2].isNumberValue())
					&& (ok[3] = arg[3].isGeoBoolean())) {
				GeoElement[] ret = { kernel.HyperGeometric(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1], (NumberValue) arg[2], (GeoBoolean)arg[3]) };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg[2]);
			else
				throw argErr(app, c.getName(), arg[3]);
			

		case 5:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isNumberValue())
					&& (ok[1] = arg[1].isNumberValue())
					&& (ok[2] = arg[2].isNumberValue())
					&& (ok[3] = arg[3].isNumberValue())
					&& (ok[4] = arg[4].isGeoBoolean())) {
				GeoElement[] ret = { kernel.HyperGeometric(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1],
						(NumberValue) arg[2], (NumberValue) arg[3],
						(GeoBoolean) arg[4]) };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg[2]);
			else if (!ok[3])
				throw argErr(app, c.getName(), arg[3]);
			else
				throw argErr(app, c.getName(), arg[4]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *InverseHyperGeometric
 */
class CmdInverseHyperGeometric extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdInverseHyperGeometric(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 4:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isNumberValue())
					&& (ok[1] = arg[1].isNumberValue())
					&& (ok[2] = arg[2].isNumberValue())
					&& (ok[3] = arg[3].isNumberValue())) {
				GeoElement[] ret = { kernel.InverseHyperGeometric(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1],
						(NumberValue) arg[2], (NumberValue) arg[3]) };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg[2]);
			else
				throw argErr(app, c.getName(), arg[3]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}

/**
 *CopyFreeObject
 */
class CmdCopyFreeObject extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCopyFreeObject(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 1:

			String label = c.getLabel();
			GeoElement geo = arg[0].copy();
			geo.setLabel(label);
			GeoElement[] ret = { geo };
			return ret;


			// more than one argument
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *SetColor
 */
class CmdSetColor extends CommandProcessor {

	boolean background = false;
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetColor(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 2:

			if (!arg[1].isGeoText())
				throw argErr(app, c.getName(), arg[1]);

			try {

				String color = geogebra.util.Util.removeSpaces(
						((GeoText) arg[1]).getTextString());
				// lookup Color
				//HashMap<String, Color> colors = app.getColorsHashMap();
				//Color col = colors.get(color);
				
				Color col = GeoGebraColorConstants.getGeogebraColor(app,  color);

				// support for translated color names
				//if (col == null) {
				//	// translate to English
				//	color = app.reverseGetColor(color).toUpperCase();
				//	col = (Color) colors.get(color);
				//	// Application.debug(color);
				//}

				if (col == null) 
					throw argErr(app, c.getName(), arg[1]);
				
				
				if (background)
					arg[0].setBackgroundColor(col);
				else
					arg[0].setObjColor(col);
				
				arg[0].updateRepaint();

				GeoElement geo = (GeoElement) arg[0];
				GeoElement[] ret = { geo };
				return ret;

			} catch (Exception e) {
				e.printStackTrace();
				throw argErr(app, c.getName(), arg[0]);
			}

		case 4:
			boolean[] ok = new boolean[n];
			arg = resArgs(c);
			if ((ok[1] = arg[1].isNumberValue())
					&& (ok[2] = arg[2].isNumberValue())
					&& (ok[3] = arg[3].isNumberValue())) {
				int red = (int) (((NumberValue) arg[1]).getDouble() * 255);
				if (red < 0)
					red = 0;
				else if (red > 255)
					red = 255;
				int green = (int) (((NumberValue) arg[2]).getDouble() * 255);
				if (green < 0)
					green = 0;
				else if (green > 255)
					green = 255;
				int blue = (int) (((NumberValue) arg[3]).getDouble() * 255);
				if (blue < 0)
					blue = 0;
				else if (blue > 255)
					blue = 255;

				if (background)
					arg[0].setBackgroundColor(new Color(red, green, blue));
				else
					arg[0].setObjColor(new Color(red, green, blue));
				
				arg[0].updateRepaint();

				GeoElement geo = (GeoElement) arg[0];
				GeoElement[] ret = { geo };
				return ret;

			} else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg[2]);
			else
				throw argErr(app, c.getName(), arg[3]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

class CmdSetBackgroundColor extends CmdSetColor {
	
	public CmdSetBackgroundColor(Kernel kernel) {
		super(kernel);
		background = true;
	}

	final public GeoElement[] process(Command c) throws MyError {
		return super.process(c);
	}
}

/**
 *UpdateConstruction
 */
class CmdUpdateConstruction extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdUpdateConstruction(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 0:
			app.getKernel().updateConstruction();
			app.setUnsaved();
			GeoElement[] ret = {};
			return ret;
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *SetValue
 */
class CmdSetValue extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetValue(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		boolean ok;
		GeoElement[] ret = {};

		switch (n) {
		case 2:
			if (arg[0].isIndependent() || arg[0].isMoveable()) {
				if (arg[0].isGeoNumeric() && arg[1].isNumberValue()) {
					NumberValue num = (NumberValue) arg[1];
					((GeoNumeric) arg[0]).setValue(num.getDouble());
				} else {
					arg[0].set(arg[1]);
				}
				arg[0].updateRepaint();
			} else if (arg[1].isNumberValue() && arg[0].isGeoNumeric() && arg[0].getParentAlgorithm() instanceof SetRandomValue) {
				// eg a = RandomBetween[0,10]
				SetRandomValue algo = (SetRandomValue) arg[0].getParentAlgorithm();
				algo.setRandomValue(((NumberValue)arg[1]).getDouble());
			} else if (arg[1].isNumberValue() && arg[0].getParentAlgorithm() instanceof AlgoDependentNumber) {
				// eg a = random()
				double val = ((NumberValue)arg[1]).getDouble();
				if (val >= 0 && val <= 1) {
					AlgoDependentNumber al = (AlgoDependentNumber)arg[0].getParentAlgorithm();
					ExpressionNode en = al.getExpression();
					if (en.getOperation() == ExpressionNode.RANDOM) {
						GeoNumeric num = ((GeoNumeric)al.getOutput()[0]);
						num.setValue(val);
						num.updateRepaint();
					}
				}
			}
			return ret;
		case 3:
			if (ok = (arg[0].isGeoList() && arg[0].isIndependent()) && arg[1].isNumberValue()) {
				GeoList list = (GeoList) arg[0];
				int nn = (int) ((NumberValue) arg[1]).getDouble();

				if (nn < 1 || nn > list.size() + 1)
					throw argErr(app, c.getName(), arg[1]);
				if(nn > list.size()){
					list.add((GeoElement)arg[2].deepCopy(kernel));
					list.updateRepaint();
					return ret;
				}
				else{
				GeoElement geo = list.get(nn - 1);
				if (geo.isIndependent()) {
					if (geo.isGeoNumeric() && arg[2].isNumberValue()) {
						NumberValue num = (NumberValue) arg[2];
						((GeoNumeric) geo).setValue(num.getDouble());
					} else {
						geo.set(arg[2]);						
					}
				}
				else Application.debug(geo.getParentAlgorithm());

				geo.updateRepaint();

				// update the list too if necessary
				if (!geo.isLabelSet()) { // eg like first element of {1,2,a}
					Iterator<GeoElement> it = kernel.getConstruction()
							.getGeoSetConstructionOrder().iterator();
					while (it.hasNext()) {
						GeoElement geo2 = it.next();
						if (geo2.isGeoList()) {
							GeoList gl = (GeoList) geo2;
							for (int i = 0; i < gl.size(); i++) {
								if (gl.get(i) == geo)
									gl.updateRepaint();
							}
						}
					}
				}}

			} else
				throw argErr(app, c.getName(), ok ? arg[1] : arg[0]);

			return ret;

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *SetDynamicColor
 */
class CmdSetDynamicColor extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetDynamicColor(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 4:
			boolean[] ok = new boolean[n];
			arg = resArgs(c);
			if ((ok[1] = arg[1].isNumberValue())
					&& (ok[2] = arg[2].isNumberValue())
					&& (ok[3] = arg[3].isNumberValue())) {

				GeoElement geo = (GeoElement) arg[0];

				ArrayList<GeoElement> listItems = new ArrayList<GeoElement>();
				listItems.add((GeoElement) arg[1]); 
				listItems.add((GeoElement) arg[2]); 
				listItems.add((GeoElement) arg[3]); 
				//listItems.add((GeoElement) arg[4]); // no opacity 
				AlgoDependentList algo = new AlgoDependentList(cons, listItems, false);
				kernel.getConstruction().removeFromConstructionList(algo);
				GeoList list = algo.getGeoList();

				geo.setColorFunction(list);
				geo.updateRepaint();

				GeoElement[] ret = { geo };
				return ret;

			} else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg[2]);
			else
				throw argErr(app, c.getName(), arg[3]);
		case 5:
			ok = new boolean[n];
			arg = resArgs(c);
			if ((ok[1] = arg[1].isNumberValue())
					&& (ok[2] = arg[2].isNumberValue())
					&& (ok[3] = arg[3].isNumberValue())
					&& (ok[4] = arg[4].isNumberValue())) {

				GeoElement geo = (GeoElement) arg[0];
				
				ArrayList<GeoElement> listItems = new ArrayList<GeoElement>();
				listItems.add((GeoElement) arg[1]); 
				listItems.add((GeoElement) arg[2]); 
				listItems.add((GeoElement) arg[3]); 
				listItems.add((GeoElement) arg[4]); // opacity 
				AlgoDependentList algo = new AlgoDependentList(cons, listItems, false);
				kernel.getConstruction().removeFromConstructionList(algo);
				GeoList list = algo.getGeoList();

				geo.setColorFunction(list);
				geo.updateRepaint();

				GeoElement[] ret = { geo };
				return ret;

			} else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg[2]);
			else if (!ok[3])
				throw argErr(app, c.getName(), arg[3]);
			else
				throw argErr(app, c.getName(), arg[4]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *SetConditionToShowObject
 */
class CmdSetConditionToShowObject extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetConditionToShowObject(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if (arg[1].isGeoBoolean()) {

				GeoElement geo = (GeoElement) arg[0];

				try {
					geo.setShowObjectCondition((GeoBoolean) arg[1]);
				} catch (CircularDefinitionException e) {
					e.printStackTrace();
					throw argErr(app, c.getName(), arg[1]);
				}
				geo.updateRepaint();

				GeoElement[] ret = { geo };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *SetFilling
 */
class CmdSetFilling extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetFilling(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if (arg[1].isNumberValue()) {

				GeoElement geo = (GeoElement) arg[0];

				geo.setAlphaValue((float) ((NumberValue) arg[1]).getDouble());
				geo.updateRepaint();

				GeoElement[] ret = { geo };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *ParseToNumber
 */
class CmdParseToNumber extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdParseToNumber(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		boolean ok;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if (ok = arg[0].isGeoNumeric() && arg[1].isGeoText()) {

				GeoNumeric num = (GeoNumeric) arg[0];
				String str = ((GeoText) arg[1]).getTextString();

				try {
					num.setValue(kernel.getAlgebraProcessor()
							.evaluateToNumeric(str, true).getDouble());
					num.updateCascade();
				} catch (Exception e) {
					num.setUndefined();
					num.updateCascade();
				}

				GeoElement[] ret = { num };
				return ret;
			} else if (!ok)
				throw argErr(app, c.getName(), arg[0]);
			else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *CmdGetTime
 */
class CmdGetTime extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdGetTime(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 0:
			Calendar cal = Calendar.getInstance();
			GeoNumeric ms = new GeoNumeric(cons, cal.get(Calendar.MILLISECOND));
			GeoNumeric secs = new GeoNumeric(cons, cal.get(Calendar.SECOND));
			GeoNumeric mins = new GeoNumeric(cons, cal.get(Calendar.MINUTE));
			GeoNumeric hours = new GeoNumeric(cons, cal.get(Calendar.HOUR_OF_DAY));
			GeoNumeric date = new GeoNumeric(cons, cal.get(Calendar.DAY_OF_MONTH));
			int d = cal.get(Calendar.DAY_OF_WEEK);
			GeoNumeric day = new GeoNumeric(cons, d);
			int m = cal.get(Calendar.MONTH) + 1;
			GeoNumeric month = new GeoNumeric(cons, m);
			GeoNumeric year = new GeoNumeric(cons, cal.get(Calendar.YEAR));
			GeoText monthStr = new GeoText(cons);
			monthStr.setTextString(app.getPlain("Month."+m));
			
			GeoText dayStr = new GeoText(cons);
			dayStr.setTextString(app.getPlain("Day."+d));

			GeoList list = new GeoList(cons);
			list.setLabel(c.getLabel());
			
			list.add(ms);
			list.add(secs);
			list.add(mins);
			list.add(hours);
			list.add(date);
			list.add(month);
			list.add(year);
			list.add(monthStr);
			list.add(dayStr);
			list.add(day);
			list.update();

			GeoElement[] ret = { list };
			return ret;

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *ParseToFunction
 */
class CmdParseToFunction extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdParseToFunction(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		boolean ok;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if (ok = arg[0].isGeoFunction() && arg[1].isGeoText()) {

				GeoFunction fun = (GeoFunction) arg[0];
				String str = ((GeoText) arg[1]).getTextString();

				try {
					fun.set(kernel.getAlgebraProcessor().evaluateToFunction(
							str, true));
					fun.updateCascade();
				} catch (Exception e) {
					fun.setUndefined();
					fun.updateCascade();
				}

				GeoElement[] ret = { fun };
				return ret;
			} else if (!ok)
				throw argErr(app, c.getName(), arg[0]);
			else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *LineStyle
 */
class CmdLineStyle extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdLineStyle(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if (arg[1].isNumberValue()) {

				int style = (int) ((NumberValue) arg[1]).getDouble();
				Integer[] types = EuclidianView.getLineTypes();

				//For invalid number we assume it's 0
				//We do this also for SetPointStyle
				 
				if (style < 0 || style >= types.length)
					style = 0;
				
				arg[0].setLineType(types[style].intValue());
				arg[0].updateRepaint();

				GeoElement[] ret = { arg[0] };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *SetLineThickness
 */
class CmdSetLineThickness extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetLineThickness(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		switch (n) {
		case 2:
			arg = resArgs(c);

			if (arg[1].isNumberValue()) {

				int thickness = (int) ((NumberValue) arg[1]).getDouble();

				arg[0].setLineThickness(thickness);
				arg[0].updateRepaint();

				GeoElement[] ret = { arg[0] };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *SetPointStyle
 */
class CmdSetPointStyle extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetPointStyle(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		boolean ok;
		switch (n) {
		case 2:
			arg = resArgs(c);

			if (ok = arg[0].isGeoPoint() && arg[1].isNumberValue()) {

				GeoPoint point = (GeoPoint) arg[0];

				int style = (int) ((NumberValue) arg[1]).getDouble();

				point.setPointStyle(style);
				point.updateRepaint();

				GeoElement[] ret = { point };
				return ret;
			} else if (!ok)
				throw argErr(app, c.getName(), arg[0]);
			else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *SetPointSize
 */
class CmdSetPointSize extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetPointSize(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		boolean ok;
		switch (n) {
		case 2:
			arg = resArgs(c);

			if (ok = arg[0].isGeoPoint() && arg[1].isNumberValue()) {

				GeoPoint point = (GeoPoint) arg[0];

				int size = (int) ((NumberValue) arg[1]).getDouble();

				point.setPointSize(size);
				point.updateRepaint();

				GeoElement[] ret = { point };
				return ret;
			} else if (!ok)
				throw argErr(app, c.getName(), arg[0]);
			else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *SetFixed
 */
class CmdSetFixed extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetFixed(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if (arg[1].isGeoBoolean()) {

				GeoElement geo = (GeoElement) arg[0];

				geo.setFixed(((GeoBoolean) arg[1]).getBoolean());
				geo.updateRepaint();

				GeoElement[] ret = { geo };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *ShowLabel
 */
class CmdShowLabel extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdShowLabel(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if (arg[1].isGeoBoolean()) {

				GeoElement geo = (GeoElement) arg[0];

				geo.setLabelVisible(((GeoBoolean) arg[1]).getBoolean());
				geo.updateRepaint();

				GeoElement[] ret = { geo };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *SetVisibleInView
 */
class CmdSetVisibleInView extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetVisibleInView(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 3:
			arg = resArgs(c);
			if (!arg[1].isNumberValue())
				throw argErr(app, c.getName(), arg[1]);


			if (arg[2].isGeoBoolean()) {

				GeoElement geo = (GeoElement) arg[0];
				GeoElement[] ret = { geo };

				int viewNo = (int)((NumberValue)arg[1]).getDouble();

				EuclidianView ev = null;

				switch (viewNo) {
				case 1:
					ev = app.getEuclidianView();
					break;
				case 2:
					if (!app.hasEuclidianView2()) break;
					ev = app.getEuclidianView2();
					break;
				default:
					// do nothing
				}

				if (ev != null) {
					boolean show = ((GeoBoolean)arg[2]).getBoolean();

					if (show) {
						geo.addView(ev.getViewID());
						ev.add(geo);
					} else {
						geo.removeView(ev.getViewID());
						ev.remove(geo);
					}
					
					geo.updateRepaint();
				}

				return ret;
			} else
				throw argErr(app, c.getName(), arg[2]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *Rename
 */
class CmdRename extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdRename(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if (arg[1].isGeoText()) {

				GeoElement geo = (GeoElement) arg[0];

				if (RenameInputHandler.checkName(geo, ((GeoText) arg[1]).getTextString())) {
					geo.rename(((GeoText) arg[1]).getTextString());
					geo.updateRepaint();

					GeoElement[] ret = { geo };
					return ret;
				} else {
					throw argErr(app, c.getName(), arg[1]);
				}
			} else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *HideLayer
 */
class CmdHideLayer extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdHideLayer(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isNumberValue()) {
				GeoNumeric layerGeo = (GeoNumeric) arg[0];
				int layer = (int) layerGeo.getDouble();

				Iterator<GeoElement> it = kernel.getConstruction()
						.getGeoSetLabelOrder().iterator();
				while (it.hasNext()) {
					GeoElement geo = it.next();
					if (geo.getLayer() == layer) {
						geo.setEuclidianVisible(false);
						geo.updateRepaint();
					}
				}

				GeoElement[] ret = { layerGeo };
				return ret;

			} else
				throw argErr(app, c.getName(), null);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *ShowLayer
 */
class CmdShowLayer extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdShowLayer(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isNumberValue()) {
				GeoNumeric layerGeo = (GeoNumeric) arg[0];
				int layer = (int) layerGeo.getDouble();

				Iterator<GeoElement> it = kernel.getConstruction()
						.getGeoSetLabelOrder().iterator();
				while (it.hasNext()) {
					GeoElement geo = it.next();
					if (geo.getLayer() == layer) {
						geo.setEuclidianVisible(true);
						geo.updateRepaint();
					}
				}

				GeoElement[] ret = { layerGeo };
				return ret;

			} else
				throw argErr(app, c.getName(), null);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *SetCoords
 */
class CmdSetCoords extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetCoords(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 3:
			arg = resArgs(c);
			//we don't want to change coords unless the point is free or Point[path/region]
			if ((ok[0] = (arg[0] instanceof GeoVec3D && arg[0].isMoveable()))
					&& (ok[1] = (arg[1].isGeoNumeric()))
					&& (ok[2] = (arg[2].isGeoNumeric()))) {

				double x = ((GeoNumeric) arg[1]).getDouble();
				double y = ((GeoNumeric) arg[2]).getDouble();

				GeoElement geo = (GeoElement) arg[0];

				if (geo.isGeoPoint()) {
					((GeoPoint) geo).setCoords(x, y, 1);
					geo.updateRepaint();
				} else if (geo.isGeoVector()) {
					((GeoVector) geo).setCoords(x, y, 0);
					geo.updateRepaint();
				} else
					throw argErr(app, c.getName(), arg[0]);

				GeoElement[] ret = { geo };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else
				throw argErr(app, c.getName(), arg[2]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *FillRow
 */
class CmdFillRow extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFillRow(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoNumeric()))
					&& (ok[1] = (arg[1].isGeoList()))) {

				int row = -1 + (int) ((GeoNumeric) arg[0]).getDouble();

				if (row < 0 || row > SpreadsheetView.MAX_ROWS)
					throw argErr(app, c.getName(), arg[0]);

				GeoList list = (GeoList) arg[1];

				GeoElement[] ret = { list };

				if (list.size() == 0)
					return ret;

				for (int col = 0; col < list.size(); col++) {

					GeoElement cellGeo = list.get(col).copy();

					try {
						GeoElement.setSpreadsheetCell(app, row, col, cellGeo);
					} catch (Exception e) {
						e.printStackTrace();
						throw argErr(app, c.getName(), arg[1]);
					}

				}

				app.storeUndoInfo();
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *FillColumn
 */
class CmdFillColumn extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFillColumn(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoNumeric()))
					&& (ok[1] = (arg[1].isGeoList()))) {

				int col = -1 + (int) ((GeoNumeric) arg[0]).getDouble();

				if (col < 0 || col > SpreadsheetView.MAX_COLUMNS)
					throw argErr(app, c.getName(), arg[0]);

				GeoList list = (GeoList) arg[1];
				GeoElement[] ret = { list };

				if (list.size() == 0)
					return ret;

				for (int row = 0; row < list.size(); row++) {
					GeoElement cellGeo = list.get(row).copy();

					try {
						GeoElement.setSpreadsheetCell(app, row, col, cellGeo);
					} catch (Exception e) {
						e.printStackTrace();
						throw argErr(app, c.getName(), arg[1]);
					}
				}

				app.storeUndoInfo();
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *FillCells
 */
class CmdFillCells extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFillCells(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 2:
			app.setScrollToShow(false);
			arg = resArgs(c);
			if (arg[0].isGeoList()) {

				GeoList cellRange = (GeoList) arg[0];

				if (!(cellRange.getParentAlgorithm() instanceof AlgoCellRange)) {
					Application.debug("not cell range");
					throw argErr(app, c.getName(), arg[0]);

				}

				AlgoCellRange algo = (AlgoCellRange) cellRange
						.getParentAlgorithm();

				Point[] points = algo.getRectangle();

				Point startCoords = points[0];
				Point endCoords = points[1];

				int minCol = Math.min(startCoords.x, endCoords.x);
				int maxCol = Math.max(startCoords.x, endCoords.x);
				int minRow = Math.min(startCoords.y, endCoords.y);
				int maxRow = Math.max(startCoords.y, endCoords.y);

				// Application.debug(minCol+" "+maxCol+" "+minRow+" "+maxRow);

				GeoElement geo = (GeoElement) arg[1];
				GeoElement[] ret = {};

				if (geo.isGeoLocus()) {

					if (!geo.isDefined())
						throw argErr(app, c.getName(), arg[1]);

					if (minCol + 1 != maxCol)
						throw argErr(app, c.getName(), arg[0]);

					GeoLocus locus = (GeoLocus) geo;

					ArrayList<MyPoint> al = locus.getMyPointList();

					int length = Math.min(al.size(), maxRow - minRow);

					for (int i = 0; i < length; i++) {
						int row = i + minRow;

						try {
							// cell will have been autocreated by eg A1:A3 in
							// command, so delete
							kernel.lookupLabel(
									GeoElement.getSpreadsheetCellName(minCol,
											row)).remove();
							kernel.lookupLabel(
									GeoElement.getSpreadsheetCellName(
											minCol + 1, row)).remove();

							MyPoint p = al.get(i);
							
							StringBuilder sb = new StringBuilder();

							GeoElement.setSpreadsheetCell(app, row, minCol,
									new GeoNumeric(cons, p.x));
							GeoElement.setSpreadsheetCell(app, row, minCol + 1,
									new GeoNumeric(cons, p.y));
						} catch (Exception e) {
							e.printStackTrace();
							app.setScrollToShow(true);
							throw argErr(app, c.getName(), arg[1]);
						}

					}
					app.setScrollToShow(true);

					return ret;

				}
				if (!geo.isGeoList()) {
					for (int row = minRow; row <= maxRow; row++)
						for (int col = minCol; col <= maxCol; col++) {
							try {
								// cell will have been autocreated by eg A1:A3
								// in command, so delete
								// in case it's being filled by eg GeoText
								kernel.lookupLabel(
										GeoElement.getSpreadsheetCellName(col,
												row)).remove();

								GeoElement.setSpreadsheetCell(app, row, col,
										geo);
							} catch (Exception e) {
								app.setScrollToShow(true);
								e.printStackTrace();
								throw argErr(app, c.getName(), arg[1]);
							}
						}
					app.setScrollToShow(true);
					return ret;
				}

				// TODO finish
				// GeoList list = (GeoList)geo;
				// if (list.isMatrix())

				app.storeUndoInfo();
				app.setScrollToShow(true);
				return ret;

			} else {

				if (GeoElement.isSpreadsheetLabel(arg[0].getLabel())) {

					if (!arg[1].isGeoList()) {
						app.setScrollToShow(true);
						throw argErr(app, c.getName(), arg[1]);
					}

					GeoList list = (GeoList) arg[1];

					Matcher matcher = GeoElement.spreadsheetPattern
							.matcher(arg[0].getLabel());
					int column = GeoElement.getSpreadsheetColumn(matcher);
					int row = GeoElement.getSpreadsheetRow(matcher);

					if (row == -1 || column == -1) {
						app.setScrollToShow(true);
						throw argErr(app, c.getName(), arg[0]);
					}

					if (list.isMatrix()) {
						// 2D fill
						// FillCells[B3,{"a","b"}] will autocreate B3=0 so we
						// need to remove B3
						arg[0].remove();

						try {
							int rows = list.size();
							int cols = ((GeoList) list.get(0)).size();
							for (int r = 0; r < rows; r++) {
								GeoList rowList = (GeoList) list.get(r);
								for (int c1 = 0; c1 < cols; c1++) {
									GeoElement
											.setSpreadsheetCell(app, row + r,
													column + c1, rowList
															.get(c1).copy());
								}
							}
						} catch (Exception e) {
							app.setScrollToShow(true);
							throw argErr(app, c.getName(), list);
						}

					} else {
						// 1D fill
						// FillCells[B3,{"a","b"}] will autocreate B3=0 so we
						// need to remove B3
						arg[0].remove();

						for (int i = list.size() - 1; i >= 0; i--)
							try {
								// Application.debug("setting "+row+" "+(column+i)+" to "+list.get(i).toString());
								GeoElement.setSpreadsheetCell(app, row, column
										+ i, list.get(i).copy());
							} catch (Exception e) {
								e.printStackTrace();
								app.setScrollToShow(true);
								throw argErr(app, c.getName(), arg[1]);
							}
					}

				} else {
					app.setScrollToShow(true);
					throw argErr(app, c.getName(), arg[0]);
				}
			}

			GeoElement[] ret = {};
			app.storeUndoInfo();
			app.setScrollToShow(true);
			return ret;

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}

/**
 *Pan
 */
class CmdPan extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdPan(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean ok;
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if (ok = arg[0].isGeoNumeric() && arg[1].isGeoNumeric()) {

				GeoNumeric x = (GeoNumeric) arg[0];
				GeoNumeric y = (GeoNumeric) arg[1];
				EuclidianView ev = (EuclidianView)app.getActiveEuclidianView();
				ev.rememberOrigins();
				ev.setCoordSystemFromMouseMove((int) x.getDouble(), -(int) y
						.getDouble(), EuclidianController.MOVE_VIEW);

				GeoElement[] ret = { arg[0] };
				return ret;
			} else if (!ok)
				throw argErr(app, c.getName(), arg[0]);
			else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *SetActiveView
 */
class CmdSetActiveView extends CommandProcessor {

	public CmdSetActiveView(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		
		GeoElement[] ret = {};
		

		if (!app.useFullGui()) return ret;
			
		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoNumeric()) {
				GeoNumeric numGeo = (GeoNumeric) arg[0];

				int view = (int)numGeo.getDouble();
				
				// ignore all errors (eg when a view is not available etc)
				switch (view) {
				case 1:
					app.setActiveView(Application.VIEW_EUCLIDIAN);
					 break;
				case 2:
					app.setActiveView(Application.VIEW_EUCLIDIAN2);
					 break;
				case 3:
					app.setActiveView(Application.VIEW_EUCLIDIAN3D);
					 break;
				case -1:
					app.setActiveView(Application.VIEW_SPREADSHEET);
					 break;
				case -2:
					app.setActiveView(Application.VIEW_ALGEBRA);
					 break;
				case -3:
					app.setActiveView(Application.VIEW_CAS);
					 break;
				// default: // might be needed when support for more than 2 Euclidian Views added 
				}
				
				return ret;

			} else
				throw argErr(app, c.getName(), arg[0]);


		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
/**
 *ZoomIn
 */
class CmdZoomIn extends CommandProcessor {

	public CmdZoomIn(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoNumeric()) {
				GeoNumeric numGeo = (GeoNumeric) arg[0];

				EuclidianView ev = (EuclidianView)app.getActiveEuclidianView();
				double px = ev.getWidth() / 2; // mouseLoc.x;
				double py = ev.getHeight() / 2; // mouseLoc.y;

				double factor = numGeo.getDouble();
				if (Kernel.isZero(factor))
					throw argErr(app, c.getName(), arg[0]);

				ev.zoom(px, py, factor, 4, true);

				app.setUnsaved();

				GeoElement[] ret = {};
				return ret;

			} else
				throw argErr(app, c.getName(), arg[0]);
		case 2:
			arg = resArgs(c);
			boolean ok0;
			if ((ok0 = arg[0].isGeoNumeric()) && arg[1].isGeoPoint()) {
				GeoNumeric numGeo = (GeoNumeric) arg[0];
				GeoPoint p = (GeoPoint) arg[1];

				EuclidianView ev = (EuclidianView)app.getActiveEuclidianView();
				double px = ev.toScreenCoordXd(p.inhomX); // mouseLoc.x;
				double py = ev.toScreenCoordYd(p.inhomY); // mouseLoc.y;

				double factor = numGeo.getDouble();
				if (Kernel.isZero(factor))
					throw argErr(app, c.getName(), arg[0]);

				ev.zoom(px, py, factor, 4, true);

				app.setUnsaved();

				GeoElement[] ret = {};
				return ret;

			} else
				throw argErr(app, c.getName(), ok0 ? arg[1] : arg[0]);
		case 4:
			arg = resArgs(c);
			for(int i=0;i<3;i++)
					if(!arg[i].isNumberValue())
						throw argErr(app, c.getName(),arg[i]);
			EuclidianView ev = (EuclidianView)app.getActiveEuclidianView();
			ev.setXminObject((NumberValue)arg[0]);
			ev.setXmaxObject((NumberValue)arg[2]);
			ev.setYminObject((NumberValue)arg[1]);
			ev.setYmaxObject((NumberValue)arg[3]);
			ev.updateBounds();
			GeoElement[] ret = {};
			return ret;
						
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *ZoomOut
 */
class CmdZoomOut extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdZoomOut(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoNumeric()) {
				GeoNumeric numGeo = (GeoNumeric) arg[0];

				EuclidianView ev = (EuclidianView)app.getActiveEuclidianView();
				double px = ev.getWidth() / 2.0; // mouseLoc.x;
				double py = ev.getHeight() / 2.0; // mouseLoc.y;

				double factor = numGeo.getDouble();

				if (Kernel.isZero(factor))
					throw argErr(app, c.getName(), arg[0]);

				ev.zoom(px, py, 1 / factor, 4, true);

				app.setUnsaved();

				GeoElement[] ret = {};
				return ret;

			} else
				throw argErr(app, c.getName(), arg[0]);

		case 2:
			arg = resArgs(c);
			boolean ok0;
			if ((ok0 = arg[0].isGeoNumeric()) && arg[1].isGeoPoint()) {
				GeoNumeric numGeo = (GeoNumeric) arg[0];
				GeoPoint p = (GeoPoint) arg[1];

				EuclidianView ev = (EuclidianView)app.getActiveEuclidianView();
				double px = ev.toScreenCoordXd(p.inhomX); // mouseLoc.x;
				double py = ev.toScreenCoordYd(p.inhomY); // mouseLoc.y;

				double factor = numGeo.getDouble();
				if (Kernel.isZero(factor))
					throw argErr(app, c.getName(), arg[0]);

				ev.zoom(px, py, 1 / factor, 4, true);

				app.setUnsaved();

				GeoElement[] ret = {};
				return ret;

			} else
				throw argErr(app, c.getName(), ok0 ? arg[1] : arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

class CmdSetAxesRatio extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetAxesRatio(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		boolean ok0;
		switch (n) {
	
		case 2:
			arg = resArgs(c);
			
			if ((ok0 = arg[0].isGeoNumeric()) && arg[1].isGeoNumeric()) {
				
				GeoNumeric numGeo = (GeoNumeric) arg[0];
				GeoNumeric numGeo2 = (GeoNumeric) arg[1];
				EuclidianView ev = (EuclidianView)app.getActiveEuclidianView();
				ev.zoomAxesRatio(numGeo.getDouble()/numGeo2.getDouble(), true);
				GeoElement[] ret = {};
				return ret;

			} else
				throw argErr(app, c.getName(), ok0 ? arg[1] : arg[0]);
		case 3:
			arg = resArgs(c);			
			if ((ok0 = arg[0].isGeoNumeric()) && arg[1].isGeoNumeric()) {
				
				GeoNumeric numGeo = (GeoNumeric) arg[0];
				GeoNumeric numGeo2 = (GeoNumeric) arg[1];
				GeoNumeric numGeo3 = (GeoNumeric) arg[2];
				EuclidianViewInterface ev = (EuclidianViewInterface)app.getActiveEuclidianView();
				//TODO: Fix this once 3D view supports zoom
				if(!ev.isDefault2D()){
					ev.zoom(numGeo.getDouble()/numGeo3.getDouble(),
							numGeo2.getDouble()/numGeo3.getDouble(),  1, 3, true);
				}
				GeoElement[] ret = {};
				return ret;

			} else
				throw argErr(app, c.getName(), ok0 ? arg[1] : arg[0]);
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *SetLayer
 */
class CmdSetLayer extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetLayer(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if (arg[1].isGeoNumeric()) {

				GeoElement geo = (GeoElement) arg[0];

				geo.setLayer((int) ((GeoNumeric) arg[1]).getDouble());
				geo.updateRepaint();

				GeoElement[] ret = { geo };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *SetLabelMode
 */
class CmdSetLabelMode extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetLabelMode(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if (arg[1].isGeoNumeric()) {

				GeoElement geo = (GeoElement) arg[0];

				geo.setLabelMode((int) ((GeoNumeric) arg[1]).getDouble());
				geo.updateRepaint();

				GeoElement[] ret = { geo };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *SetTooltipMode
 */
class CmdSetTooltipMode extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetTooltipMode(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if (arg[1].isGeoNumeric()) {

				GeoElement geo = (GeoElement) arg[0];

				geo.setTooltipMode((int) ((GeoNumeric) arg[1]).getDouble());
				geo.updateRepaint();

				GeoElement[] ret = { geo };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *SetCaption
 */
class CmdSetCaption extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetCaption(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if (arg[1].isGeoText()) {

				GeoElement geo = (GeoElement) arg[0];

				geo.setCaption(((GeoText) arg[1]).getTextString());
				geo.updateRepaint();

				GeoElement[] ret = { geo };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *StartAnimation
 */
class CmdStartAnimation extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdStartAnimation(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		// dummy
		GeoElement[] ret = { new GeoNumeric(cons) };

		switch (n) {
		case 0:

			app.getKernel().getAnimatonManager().startAnimation();
			return ret;

		case 1:
			arg = resArgs(c);
			if ((arg[0].isGeoNumeric() && ((GeoNumeric) arg[0]).isIndependent()) ||
					arg[0].isPointOnPath()) {				
				arg[0].setAnimating(true);
				app.getKernel().getAnimatonManager().startAnimation();
				return ret;
			}			
			else if (arg[0].isGeoBoolean()) {

				GeoBoolean geo = (GeoBoolean) arg[0];

				if (geo.getBoolean()) {
					app.getKernel().getAnimatonManager().startAnimation();

				} else {
					app.getKernel().getAnimatonManager().stopAnimation();
				}
				return ret;
			} else
				throw argErr(app, c.getName(), arg[0]);
		default:
			arg = resArgs(c);
			boolean start = true;
			int sliderCount = n;
			if (arg[n-1].isGeoBoolean()){
				start = ((GeoBoolean) arg[n-1]).getBoolean();
				sliderCount = n-1;
			}
			for(int i = 0; i < sliderCount; i++)
				if(!arg[i].isGeoNumeric() && !arg[i].isPointOnPath())
					throw argErr(app,c.getName(),arg[i]);
			
			for(int i = 0; i < sliderCount; i++){
				if(arg[i].isGeoNumeric())
					((GeoNumeric) arg[0]).setAnimating(start);
				else
					((GeoPoint) arg[0]).setAnimating(start);
				if(start)
					app.getKernel().getAnimatonManager().startAnimation();
			} 
			
			return ret;		
		}
	}
}

/**
 *Checkbox
 */
class CmdCheckbox extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCheckbox(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		// dummy

		String caption = null;
		GeoList geosToHide = null;
		switch (n) {
		case 2:
			arg = resArgs(c);
			if (arg[0].isGeoText())
				caption = ((GeoText) arg[0]).getTextString();
			else
				throw argErr(app, c.getName(), arg[0]);
			if (arg[1].isGeoList()) {
				geosToHide = (GeoList) arg[1];
			} else
				throw argErr(app, c.getName(), arg[1]);
			break;
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoText())
				caption = ((GeoText) arg[0]).getTextString();
			else if (arg[0].isGeoList()) {
				geosToHide = (GeoList) arg[0];
			} else
				throw argErr(app, c.getName(), arg[0]);
			break;
		case 0:
			break;

		default:
			throw argNumErr(app, c.getName(), n);
		}
		GeoBoolean gb = new GeoBoolean(app.getKernel().getConstruction());
		gb.setValue(true);
		gb.initLocation();
		gb.setEuclidianVisible(true);
		gb.setLabel(c.getLabel());	
		
		if (caption != null) {
			gb.setLabelVisible(true);		
			gb.setCaption(caption);
			gb.update();				
		}
		try {

			if (geosToHide != null)
				for (int i = 0; i < geosToHide.size(); i++)
					geosToHide.get(i).setShowObjectCondition(gb);
		} catch (CircularDefinitionException e) {
			app.showError("CircularDefinition");
		}
		return new GeoElement[] {gb};
	}
}
/**
 * Button[],Button[caption]
 * @author Zbynek
 *
 */
class CmdButton extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdButton(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		// dummy

		
		
		switch (n) {
		case 1:
			
			arg = resArgs(c);
			if (arg[0].isGeoText()){
				String caption = ((GeoText) arg[0]).getTextString();
				GeoButton gb = new GeoButton(cons);
				gb.setLabelVisible(true);
				gb.setLabel(c.getLabel());	
				gb.setCaption(caption);
				return new GeoElement[] {gb};
			}
			else
				throw argErr(app, c.getName(), arg[0]);			
		case 0:			
			GeoButton gb = new GeoButton(cons);		
			gb.setLabelVisible(true);
			gb.setLabel(c.getLabel());	
			return new GeoElement[] {gb};

		default:
			throw argNumErr(app, c.getName(), n);
		}
		
	}
}
/**
 * Textfield[],Textfield[object]
 * @author Zbynek
 *
 */
class CmdTextfield extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdTextfield(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		// dummy

		
		
		switch (n) {
		case 1:
			
			arg = resArgs(c);
			if (arg[0].isLabelSet()){				
				return new GeoElement[] {kernel.textfield(c.getLabel(),arg[0])};
			}
			else
				throw argErr(app, c.getName(), arg[0]);			
		case 0:							
			return new GeoElement[] {kernel.textfield(c.getLabel(),null)};

		default:
			throw argNumErr(app, c.getName(), n);
		}
		
	}
}


/**
 *SelectObjects
 */
class CmdSelectObjects extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSelectObjects(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		GeoElement[] ret = {};

		app.clearSelectedGeos();

		if (n > 0) {
			arg = resArgs(c);
			for (int i = 0; i < n; i++) {
				if ((arg[i].isGeoElement())) {
					GeoElement geo = (GeoElement) arg[i];
					app.addSelectedGeo(geo, true);
				}
			}

		}
		return ret;

	}
}

/**
 *PlaySound
 */
class CmdPlaySound extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdPlaySound(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {

		int n = c.getArgumentNumber();
		GeoElement[] arg;
		GeoElement[] ret = {};
		boolean[] ok = new boolean[n];
		SoundManager sm = app.getSoundManager();

		switch (n) {
		case 1:
			arg = resArgs(c);

			// play a midi file
			if (ok[0] = arg[0].isGeoText()) {
				sm.playMidiFile(((String) ((GeoText) arg[0]).toValueString()));
				return ret;
			}
			// pause/resume current sound
			else if (ok[0] = arg[0].isGeoBoolean()) {
				sm.pauseResumeSound(((boolean) ((GeoBoolean) arg[0]).getBoolean()));
				return ret;
			}
			else {
				throw argErr(app, c.getName(), arg[0]);
			}

		case 2:
			arg = resArgs(c);

			if ( (ok[0] = arg[0].isGeoNumeric()) 
					&& (ok[1] = arg[1].isGeoNumeric())) {

				// play a note using args: note and duration
				// using instrument 0 (piano) and velocity 127 (100% of external volume control) 
				sm.playSequenceNote((int) ((GeoNumeric) arg[0]).getDouble(),
						((GeoNumeric) arg[1]).getDouble(), 0, 127);

				return ret;
			}

			else if ((ok[0] = arg[0].isGeoText()) 
					&& (ok[1] = arg[1].isGeoNumeric())) {
				// play a sequence string
				sm.playSequenceFromString(((String) ((GeoText) arg[0]).toValueString()),
						(int) ((GeoNumeric) arg[1]).getDouble());
				return ret;
			}

			else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			

		case 3:
			arg = resArgs(c);

			// play a note using args: note, duration, instrument
			if ((ok[0] = arg[0].isGeoNumeric()) 
					&& (ok[1] = arg[1].isGeoNumeric())
					&& (ok[2] = arg[2].isGeoNumeric())) {

				sm.playSequenceNote((int) ((GeoNumeric) arg[0]).getDouble(), // note
						((GeoNumeric) arg[1]).getDouble(), // duration
						(int) ((GeoNumeric) arg[2]).getDouble(), // instrument
						127); // 100% of external volume control 

				return ret;
			}

			else if ((ok[0] = arg[0].isGeoFunction()) 
					&& (ok[1] = arg[1].isGeoNumeric())
					&& (ok[2] = arg[2].isGeoNumeric())) {

				sm.playFunction(((GeoFunction) arg[0]), // function
						((GeoNumeric) arg[1]).getDouble(), // min value
						((GeoNumeric) arg[2]).getDouble()); // max value
				return ret;
			}
			
			
			else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg[2]);
			
			

		case 5:
			arg = resArgs(c);

			if ((ok[0] = arg[0].isGeoFunction() 
					&& (ok[1] = arg[1].isGeoNumeric())
					&& (ok[2] = arg[2].isGeoNumeric())
					&& (ok[3] = arg[3].isGeoNumeric()) 
					&& (ok[4] = arg[4].isGeoNumeric()))) {

				sm.playFunction(((GeoFunction) arg[0]), // function
						((GeoNumeric) arg[1]).getDouble(), // min value
						((GeoNumeric) arg[2]).getDouble(), // max value
						(int)((GeoNumeric) arg[3]).getDouble(), // sample rate
						(int)((GeoNumeric) arg[4]).getDouble()); // bit depth
				
				return ret;
			} 
			else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg[2]);
			else if (!ok[3])
				throw argErr(app, c.getName(), arg[3]);
			else
				throw argErr(app, c.getName(), arg[4]);
			
			
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *Factors
 */
class CmdFactors extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFactors(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 1:
			if (ok[0] = (arg[0].isGeoFunction())) {
				GeoElement[] ret = { kernel.Factors(c.getLabel(),
						(GeoFunction) arg[0]) };
				return ret;
			} else	if (ok[0] = (arg[0].isGeoNumeric())) {
					GeoElement[] ret = { kernel.PrimeFactorisation(c.getLabel(),
							(GeoNumeric) arg[0]) };
					return ret;
			} else
				throw argErr(app, c.getName(), arg[0]);

			// more than one argument
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *SolveODE
 */
class CmdSolveODE extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSolveODE(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
//		case 1:
//			if ((arg[0] instanceof GeoFunctionable)) {
//				GeoElement[] ret = { kernel.SolveODE(c.getLabel(),
//						((GeoFunctionable) arg[0]).getGeoFunction()) };
//				return ret;
//			}
		case 5:
			if ((ok[0] = arg[0] instanceof FunctionalNVar)
					&& (ok[1] = arg[1].isGeoNumeric())
					&& (ok[2] = arg[2].isGeoNumeric())
					&& (ok[3] = arg[3].isGeoNumeric())
					&& (ok[4] = arg[4].isGeoNumeric())) {
				GeoElement[] ret = { kernel.SolveODE(c.getLabel(),
						(FunctionalNVar) arg[0], null, (GeoNumeric) arg[1],
						(GeoNumeric) arg[2], (GeoNumeric) arg[3],
						(GeoNumeric) arg[4]) };
				return ret;
			} else
				throw argErr(app, c.getName(), getBadArg(ok, arg));
		case 6:
			if ((ok[0] = arg[0] instanceof FunctionalNVar)
					&& (ok[1] = arg[1] instanceof FunctionalNVar)
					&& (ok[2] = arg[2].isGeoNumeric())
					&& (ok[3] = arg[3].isGeoNumeric())
					&& (ok[4] = arg[4].isGeoNumeric())
					&& (ok[5] = arg[5].isGeoNumeric())) {
				GeoElement[] ret = { kernel.SolveODE(c.getLabel(),
						(FunctionalNVar) arg[0], (FunctionalNVar) arg[1],
						(GeoNumeric) arg[2], (GeoNumeric) arg[3],
						(GeoNumeric) arg[4], (GeoNumeric) arg[5]) };
				return ret;
			} else {
				throw argErr(app, c.getName(), getBadArg(ok, arg));
			}

		case 8: // second order ODE
			if ((ok[0] = arg[0].isGeoFunctionable())
					&& (ok[1] = arg[1].isGeoFunctionable())
					&& (ok[2] = arg[2].isGeoFunctionable())
					&& (ok[3] = arg[3].isGeoNumeric())
					&& (ok[4] = arg[4].isGeoNumeric())
					&& (ok[5] = arg[5].isGeoNumeric())
					&& (ok[6] = arg[6].isGeoNumeric())
					&& (ok[7] = arg[7].isGeoNumeric())) {
				GeoElement[] ret = { kernel.SolveODE2(c.getLabel(),
						(GeoFunctionable) arg[0], (GeoFunctionable) arg[1],
						(GeoFunctionable) arg[2], (GeoNumeric) arg[3],
						(GeoNumeric) arg[4], (GeoNumeric) arg[5],
						(GeoNumeric) arg[6], (GeoNumeric) arg[7]) };
				return ret;
			} else {
				throw argErr(app, c.getName(), getBadArg(ok, arg));
			}

			// more than one argument
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *Coefficients
 */
class CmdCoefficients extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCoefficients(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 1:
			if ((arg[0].isGeoFunction())) {
				GeoElement[] ret = { kernel.Coefficients(c.getLabel(),
						(GeoFunction) arg[0]) };
				return ret;
			} else if ((arg[0].isGeoConic())) {
				GeoElement[] ret = { kernel.Coefficients(c.getLabel(),
						(GeoConic) arg[0]) };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[0]);

			// more than one argument
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *Limit
 */
class CmdLimit extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdLimit(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean ok;
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 2:
			if ((ok = arg[0].isGeoFunction()) && (arg[1].isNumberValue())) {
				GeoElement[] ret = { kernel.Limit(c.getLabel(),
						(GeoFunction) arg[0], (NumberValue) arg[1]) };
				return ret;
			} else
				throw argErr(app, c.getName(), ok ? arg[1] : arg[0]);

			// more than one argument
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *LimitBelow
 */
class CmdLimitBelow extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdLimitBelow(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean ok;
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 2:
			if ((ok = arg[0].isGeoFunction()) && (arg[1].isNumberValue())) {
				GeoElement[] ret = { kernel.LimitBelow(c.getLabel(),
						(GeoFunction) arg[0], (NumberValue) arg[1]) };
				return ret;
			} else
				throw argErr(app, c.getName(), ok ? arg[1] : arg[0]);

			// more than one argument
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *LimitAbove
 */
class CmdLimitAbove extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdLimitAbove(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean ok;
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 2:
			if ((ok = arg[0].isGeoFunction()) && (arg[1].isNumberValue())) {
				GeoElement[] ret = { kernel.LimitAbove(c.getLabel(),
						(GeoFunction) arg[0], (NumberValue) arg[1]) };
				return ret;
			} else
				throw argErr(app, c.getName(), ok ? arg[1] : arg[0]);

			// more than one argument
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *PartialFractions
 */
class CmdPartialFractions extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdPartialFractions(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 1:
			if (arg[0] instanceof CasEvaluableFunction) {
				GeoElement[] ret = { kernel.PartialFractions(c.getLabel(),
						(CasEvaluableFunction) arg[0]) };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[0]);

			// more than one argument
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 *Degree
 */
class CmdDegree extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdDegree(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 1:
			if (ok[0] = (arg[0].isGeoFunction())) {
				GeoElement[] ret = { kernel.Degree(c.getLabel(),
						(GeoFunction) arg[0]) };
				return ret;
			} else
				throw argErr(app, c.getName(), arg[0]);

			// more than one argument
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 * 
 * Slider[<Min>,<Max>,<Increment>,<Speed>,<Width>,<Angle>,<Horizontal>,<
 * Animating>,<Random>]
 * 
 */
class CmdSlider extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSlider(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		if (n < 2 || n > 9)
			throw argNumErr(app, c.getName(), n);
		for (int i = 0; i < Math.min(n, 5); i++)
			if (!arg[i].isNumberValue())
				throw argErr(app, c.getName(), arg[i]);
		for (int i = 5; i < n; i++)
			if (!arg[i].isBooleanValue())
				throw argErr(app, c.getName(), arg[i]);
		GeoNumeric slider;
		if (n > 5 && ((BooleanValue) arg[5]).getBoolean())
			slider = new GeoAngle(kernel.getConstruction());
		else
			slider = new GeoNumeric(kernel.getConstruction());
		slider.setIntervalMin((NumberValue) arg[0]);
		slider.setIntervalMax((NumberValue) arg[1]);
		if (n > 2)
			slider.setAnimationStep((NumberValue) arg[2]);
		if (n > 3)
			slider.setAnimationSpeedObject((NumberValue) arg[3]);
		if (n > 4)
			slider.setSliderWidth(((NumberValue) arg[4]).getDouble());
		if (n > 6)
			slider.setSliderHorizontal(((BooleanValue) arg[6]).getBoolean());
		if (n > 7)
			slider.setAnimating(((BooleanValue) arg[7]).getBoolean());
		if (n > 8)
			slider.setRandom(((BooleanValue) arg[8]).getBoolean());
		slider.setEuclidianVisible(true);
		slider.setLabel(c.getLabel());
		return new GeoElement[] { slider };

	}
}

/**
 * Execute[<list of commands>]
 */
class CmdExecute extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdExecute(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		if (n > 10)
			throw argNumErr(app, c.getName(), n);
		if (arg[0].isGeoList() && ((GeoList) arg[0]).size()==0 || !arg[0].isDefined())
			return new GeoElement[] {};
		if ((!arg[0].isGeoList())
				|| (!((GeoList) arg[0]).getGeoElementForPropertiesDialog()
						.isGeoText()))
			throw argErr(app, c.getName(), arg[0]);
		GeoList list = (GeoList) arg[0];
		for (int i = 0; i < list.size(); i++) {
			try {
				String cmdText = ((GeoText) list.get(i)).getTextString();
				for(int k=1;k<n;k++)
					cmdText = cmdText.replace("%"+k, arg[k].getLabel());
				kernel.getAlgebraProcessor()
						.processAlgebraCommandNoExceptionHandling(cmdText
								, false,
								false, true);
			} catch (MyError e) {
				app.showError(e);
				break;
			} catch (Exception e) {
				app.showError(e.getLocalizedMessage());
				e.printStackTrace();
				break;
			}
		}
		app.storeUndoInfo();
		return new GeoElement[] {};

	}
}

/**
 * Identity[<number>]
 */
class CmdIdentity extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdIdentity(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		if (n != 1)
			throw argNumErr(app, c.getName(), n);
		if (!arg[0].isNumberValue())
			throw argErr(app, c.getName(), arg[0]);
		
		StringBuilder sb = new StringBuilder();
		int order = (int)Math.round(((NumberValue)arg[0]).getDouble());
		
		if (order < 1)
			throw argErr(app, c.getName(), arg[0]);
		String label = c.getLabel();
		if (label != null) {
			sb.append(label);
			sb.append('=');
		}
		sb.append('{');
		
		for (int i = 0 ; i < order ; i++) {
			sb.append('{');
			for (int j = 0 ; j < order ; j++) {
			sb.append(i == j ? '1' : '0');
			if (j < order - 1) sb.append(',');
			}
			sb.append(i == order - 1 ? "}" : "},");
		}
		sb.append('}');
		
		kernel.getAlgebraProcessor()
						.processAlgebraCommandNoExceptionsOrErrors(sb.toString()
								, true);
		return new GeoElement[] {};

	}
}

/**
 * IsInRegion[<Point>,<Region>]
 */
class CmdIsInRegion extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdIsInRegion(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		if (n != 2)
			throw argNumErr(app, c.getName(), n);
		if (!arg[0].isGeoPoint())
			throw argErr(app, c.getName(), arg[0]);
		if (!arg[1].isRegion())
			throw argErr(app, c.getName(), arg[1]);

		GeoBoolean slider = kernel.isInRegion(c.getLabel(),
				(GeoPointND) arg[0], (Region) arg[1]);
		return new GeoElement[] { slider };
	}
}

/**
 * ShortestDistance[ <List of Segments>, <Start Point>, <End Point>, <Boolean
 * Weighted> ] Michael Borcherds 2008-03-04
 */
class CmdShortestDistance extends CommandProcessor {

	/**
	 * @param kernel
	 */
	public CmdShortestDistance(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 4:

			if ((ok[0] = arg[0].isGeoList()) && (ok[1] = arg[1].isGeoPoint())
					&& (ok[2] = arg[2].isGeoPoint())
					&& (ok[3] = arg[3].isGeoBoolean())) {
				GeoElement[] ret = { kernel.ShortestDistance(c.getLabel(),
						(GeoList) arg[0], (GeoPointND) arg[1],
						(GeoPointND) arg[2], (GeoBoolean) arg[3]) };
				return ret;
			} else
				throw argErr(app, c.getName(), getBadArg(ok, arg));

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}

/**
 * PathParameter[Point on path]
 * 
 */
class CmdPathParameter extends CommandProcessor {

	/**
	 * @param kernel
	 */
	public CmdPathParameter(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if ((arg[0].isGeoPoint())) {

				GeoElement[] ret = { kernel.PathParameter(c.getLabel(),
						(GeoPoint) arg[0]) };
				return ret;

			} else
				throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}

class CmdIntersectRegion extends CommandProcessor {

	/**
	 * @param kernel
	 */
	public CmdIntersectRegion(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[1];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
			case 2:		
				if ((ok[0]=arg[0].isGeoPolygon()) && arg[1].isGeoPolygon() ) {
					GeoElement[] ret =  kernel.IntersectPolygons(c.getLabels(),
					(GeoPolygon) arg[0], (GeoPolygon)arg[1] ) ;
					return ret;
				} 
				else 
					throw argErr(app, c.getName(), ok[0]?arg[1]:arg[0]);
			default: 
				throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 * Frequency
 */
class CmdFrequency extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFrequency(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {

		case 1:
			if (ok[0] = arg[0].isGeoList()) {
				GeoElement[] ret = { kernel.Frequency(c.getLabel(),
						(GeoList) arg[0]) };
				return ret;

			} else {
				throw argErr(app, c.getName(), arg[0]);
			}

		case 2:

			//  arg[0] = is cumulative,  arg[1] = data list,
			if ((arg[0].isGeoBoolean()) && (arg[1].isGeoList())) {
				GeoElement[] ret = { kernel.Frequency(c.getLabel(),
						(GeoBoolean) arg[0], (GeoList) arg[1]) };
				return ret;
			}

			// arg[0] = class list, arg[1] = data list
			else if ((arg[0].isGeoList()) && (arg[1].isGeoList())) {
				if (arg[1].isGeoList()) {
					GeoElement[] ret = { kernel.Frequency(c.getLabel(),
							(GeoList) arg[0], (GeoList) arg[1]) };
					return ret;
				}
				
			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else {
				throw argErr(app, c.getName(), arg[1]);
			}

		case 3:

			// arg[0] = isCumulative, arg[1] = class list, arg[2] = data list
			if ((ok[0] = arg[0].isGeoBoolean()) && (ok[1] = arg[1].isGeoList())
					&& (ok[2] = arg[2].isGeoList())) {
				GeoElement[] ret = { kernel
						.Frequency(c.getLabel(), (GeoBoolean) arg[0],
								(GeoList) arg[1], (GeoList) arg[2]) };
				return ret;

			} 
			// arg[0] = class list, arg[1] = data list, arg[2] = useDensity
			else if ((ok[0] = arg[0].isGeoList())
					&& (ok[1] = arg[1].isGeoList())
					&& (ok[2] = arg[2].isGeoBoolean())) {
				GeoElement[] ret = { kernel.Frequency(c.getLabel(), (GeoList) arg[0],
								(GeoList) arg[1], (GeoBoolean) arg[2]) };
				return ret;

			} 
			else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else 
				throw argErr(app, c.getName(), arg[2]);

		case 4:
			arg = resArgs(c);
			// arg[0] = class list, arg[2] = data list, arg[2] = useDensity, arg[3]= density scale factor
			if ((ok[0] = (arg[0].isGeoList()))
					&& (ok[1] = (arg[1].isGeoList()))
					&& (ok[2] = (arg[2].isGeoBoolean()))
					&& (ok[3] = (arg[3].isGeoNumeric()))) {
				GeoElement[] ret = { kernel.Frequency(c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1],
						(GeoBoolean) arg[2], (GeoNumeric) arg[3]) };
				return ret;
			}
			
			// arg[0] = isCumulative, arg[1] = class list, arg[2] = data list, arg[3] = useDensity
			else if ((ok[0] = (arg[0].isGeoBoolean()))
					&& (ok[1] = (arg[1].isGeoList()))
					&& (ok[2] = (arg[2].isGeoList()))
					&& (ok[3] = (arg[3].isGeoBoolean()))) {
				GeoElement[] ret = { kernel.Frequency(c.getLabel(),
						(GeoBoolean) arg[0], (GeoList) arg[1],
						(GeoList) arg[2], (GeoBoolean) arg[3]) };
				return ret;
			}
			
			else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg[2]);
			else
				throw argErr(app, c.getName(), arg[3]);

			
		case 5:
			// arg[0] = isCumulative, arg[1] = class list, arg[2] = data list,
			// arg[3] = useDensity, arg[4] = density scale factor,
			if ((ok[0] = arg[0].isGeoBoolean()) 
					&& (ok[1] = arg[1].isGeoList())
					&& (ok[2] = arg[2].isGeoList())  
					&& (ok[3] = arg[3].isGeoBoolean())
					&& (ok[4] = arg[4].isGeoNumeric())) 
			{
				GeoElement[] ret = { kernel.Frequency(c.getLabel(), 
								(GeoBoolean) arg[0],
								(GeoList) arg[1], 
								(GeoList) arg[2], 
								(GeoBoolean) arg[3],
								(GeoNumeric) arg[4]) 
				};
				return ret;
			}		

			else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg[2]);
			else if (!ok[3])
				throw argErr(app, c.getName(), arg[3]);
			else
				throw argErr(app, c.getName(), arg[4]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

class CmdFrequencyTable extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFrequencyTable(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {

		case 1:
			if (ok[0] = arg[0].isGeoList()) {
				GeoElement[] ret = { kernel.FrequencyTable(c.getLabel(),
						(GeoList) arg[0]) };
				return ret;

			} else {
				throw argErr(app, c.getName(), arg[0]);
			}

		case 2:

			//  arg[0] = is cumulative,  arg[1] = data list,
			if ((arg[0].isGeoBoolean()) && (arg[1].isGeoList())) {
				GeoElement[] ret = { kernel.FrequencyTable(c.getLabel(),
						(GeoBoolean) arg[0], (GeoList) arg[1]) };
				return ret;
			}

			// arg[0] = class list, arg[1] = data list
			else if ((arg[0].isGeoList()) && (arg[1].isGeoList())) {
				if (arg[1].isGeoList()) {
					GeoElement[] ret = { kernel.FrequencyTable(c.getLabel(),
							(GeoList) arg[0], (GeoList) arg[1]) };
					return ret;
				}
				
			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else {
				throw argErr(app, c.getName(), arg[1]);
			}

		case 3:

			// arg[0] = isCumulative, arg[1] = class list, arg[2] = data list
			if ((ok[0] = arg[0].isGeoBoolean()) && (ok[1] = arg[1].isGeoList())
					&& (ok[2] = arg[2].isGeoList())) {
				GeoElement[] ret = { kernel
						.FrequencyTable(c.getLabel(), (GeoBoolean) arg[0],
								(GeoList) arg[1], (GeoList) arg[2]) };
				return ret;

			} 
			// arg[0] = class list, arg[1] = data list, arg[2] = useDensity
			else if ((ok[0] = arg[0].isGeoList())
					&& (ok[1] = arg[1].isGeoList())
					&& (ok[2] = arg[2].isGeoBoolean())) {
				GeoElement[] ret = { kernel.FrequencyTable(c.getLabel(), (GeoList) arg[0],
								(GeoList) arg[1], (GeoBoolean) arg[2]) };
				return ret;

			} 
			else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else 
				throw argErr(app, c.getName(), arg[2]);

		case 4:
			arg = resArgs(c);
			// arg[0] = class list, arg[2] = data list, arg[2] = useDensity, arg[3]= density scale factor
			if ((ok[0] = (arg[0].isGeoList()))
					&& (ok[1] = (arg[1].isGeoList()))
					&& (ok[2] = (arg[2].isGeoBoolean()))
					&& (ok[3] = (arg[3].isGeoNumeric()))) {
				GeoElement[] ret = { kernel.FrequencyTable(c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1],
						(GeoBoolean) arg[2], (GeoNumeric) arg[3]) };
				return ret;
			}
			
			// arg[0] = isCumulative, arg[1] = class list, arg[2] = data list, arg[3] = useDensity
			else if ((ok[0] = (arg[0].isGeoBoolean()))
					&& (ok[1] = (arg[1].isGeoList()))
					&& (ok[2] = (arg[2].isGeoList()))
					&& (ok[3] = (arg[3].isGeoBoolean()))) {
				GeoElement[] ret = { kernel.FrequencyTable(c.getLabel(),
						(GeoBoolean) arg[0], (GeoList) arg[1],
						(GeoList) arg[2], (GeoBoolean) arg[3]) };
				return ret;
			}
			
			else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg[2]);
			else
				throw argErr(app, c.getName(), arg[3]);

			
		case 5:
			// arg[0] = isCumulative, arg[1] = class list, arg[2] = data list,
			// arg[3] = useDensity, arg[4] = density scale factor,
			if ((ok[0] = arg[0].isGeoBoolean()) 
					&& (ok[1] = arg[1].isGeoList())
					&& (ok[2] = arg[2].isGeoList())  
					&& (ok[3] = arg[3].isGeoBoolean())
					&& (ok[4] = arg[4].isGeoNumeric())) 
			{
				GeoElement[] ret = { kernel.FrequencyTable(c.getLabel(), 
								(GeoBoolean) arg[0],
								(GeoList) arg[1], 
								(GeoList) arg[2], 
								(GeoBoolean) arg[3],
								(GeoNumeric) arg[4]) 
				};
				return ret;
			}		

			else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg[2]);
			else if (!ok[3])
				throw argErr(app, c.getName(), arg[3]);
			else
				throw argErr(app, c.getName(), arg[4]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 * Unique
 */
class CmdUnique extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdUnique(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {

		case 1:
			if (ok[0] = arg[0].isGeoList()) {
				GeoElement[] ret = { kernel.Unique(c.getLabel(),
						(GeoList) arg[0]) };
				return ret;

			} else {
				throw argErr(app, c.getName(), arg[0]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 * Sequence[ <expression>, <number-var>, <from>, <to> ] Sequence[ <expression>,
 * <number-var>, <from>, <to>, <step> ] Sequence[ <number-var>]
 */
class CmdZip extends CommandProcessor {
	/**
	 * Creates new zip command
	 * 
	 * @param kernel
	 */
	public CmdZip(Kernel kernel) {
		super(kernel);
	}

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
			return kernel.Zip(c.getLabel(), arg[0], vars, over);
		} else {
			for (int i = 0; i < n; i++) {
				if (!ok[i])
					throw argErr(app, c.getName(), arg[i]);
			}
		}
		return null;
	}

	private GeoElement[] vars;
	private GeoList[] over;

	/**
	 * Resolves arguments, creates local variables and fills the vars and
	 * overlists
	 * 
	 * @param c
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
			cmdCons.removeLocalVariable(localVar.getLabel());
		return arg;
	}
}

/**
 * Classes
 */
class CmdClasses extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdClasses(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {

		case 2:
			if ((ok[0] = arg[0].isGeoList()) && (ok[1] = arg[1].isGeoNumeric())) {
				GeoElement[] ret = { kernel.Classes(c.getLabel(),
						(GeoList) arg[0], (GeoNumeric) arg[1]) };
				return ret;

			} else {
				throw argErr(app, c.getName(), arg[0]);
			}

		case 3:
			if ((ok[0] = arg[0].isGeoList()) && (ok[1] = arg[1].isGeoNumeric())
					&& (ok[2] = arg[2].isGeoNumeric())) {
				GeoElement[] ret = { kernel.Classes(c.getLabel(),
						(GeoList) arg[0], (GeoNumeric) arg[1],
						(GeoNumeric) arg[2]) };
				return ret;

			} else {
				throw argErr(app, c.getName(), arg[0]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}


/**
 * TTest (t test of a sample mean)
 */
class CmdTTest extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdTTest(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {

		case 3:
			if ((ok[0] = arg[0].isGeoList()) 
					&& (ok[1] = arg[1].isGeoNumeric())
					&& (ok[2] = arg[2].isGeoText())) {
				GeoElement[] ret = { kernel.TTest(c.getLabel(),
						(GeoList) arg[0], (GeoNumeric) arg[1], (GeoText) arg[2]) };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else 
				throw argErr(app, c.getName(), arg[2]);

		case 5:
			if ((ok[0] = arg[0].isGeoNumeric()) 
					&& (ok[1] = arg[1].isGeoNumeric())
					&& (ok[2] = arg[2].isGeoNumeric())
					&& (ok[3] = arg[3].isGeoNumeric())
					&& (ok[4] = arg[4].isGeoText())
			) {
				GeoElement[] ret = { kernel.TTest(c.getLabel(),
						(GeoNumeric) arg[0], 
						(GeoNumeric) arg[1],
						(GeoNumeric) arg[2],
						(GeoNumeric) arg[3],
						(GeoText) arg[4]) };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg[2]);
			else if (!ok[3])
				throw argErr(app, c.getName(), arg[3]);
			else
				throw argErr(app, c.getName(), arg[4]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 * TTestPaired (paired t test)
 */
class CmdTTestPaired extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdTTestPaired(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {

		case 3:
			if ((ok[0] = arg[0].isGeoList()) 
					&& (ok[1] = arg[1].isGeoList())
					&& (ok[2] = arg[2].isGeoText())) {
				GeoElement[] ret = { kernel.TTestPaired(c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1], (GeoText) arg[2]) };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else 
				throw argErr(app, c.getName(), arg[2]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}

/**
 * TTest2 (t test of two sample means)
 */
class CmdTTest2 extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdTTest2(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {

		case 4:
			if ((ok[0] = arg[0].isGeoList()) 
					&& (ok[1] = arg[1].isGeoList())
					&& (ok[2] = arg[2].isGeoText())
					&& (ok[3] = arg[3].isGeoBoolean())) {
				GeoElement[] ret = { kernel.TTest2(c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1], (GeoText) arg[2], (GeoBoolean) arg[3]) };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg[2]);
			else 
				throw argErr(app, c.getName(), arg[3]);

		case 8:
			if ((ok[0] = arg[0].isGeoNumeric()) 
					&& (ok[1] = arg[1].isGeoNumeric())
					&& (ok[2] = arg[2].isGeoNumeric())
					&& (ok[3] = arg[3].isGeoNumeric())
					&& (ok[4] = arg[4].isGeoNumeric())
					&& (ok[5] = arg[5].isGeoNumeric())
					&& (ok[6] = arg[6].isGeoText())
					&& (ok[7] = arg[7].isGeoBoolean())
			) {
				GeoElement[] ret = { kernel.TTest2(c.getLabel(),
						(GeoNumeric) arg[0], 
						(GeoNumeric) arg[1],
						(GeoNumeric) arg[2],
						(GeoNumeric) arg[3],
						(GeoNumeric) arg[4], 
						(GeoNumeric) arg[5],
						(GeoText) arg[6],
						(GeoBoolean) arg[7]) };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg[2]);
			else if (!ok[3])
				throw argErr(app, c.getName(), arg[3]);
			else if (!ok[4])
				throw argErr(app, c.getName(), arg[4]);
			else if (!ok[5])
				throw argErr(app, c.getName(), arg[5]);
			else if (!ok[6])
				throw argErr(app, c.getName(), arg[6]);
			else
				throw argErr(app, c.getName(), arg[7]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 * TEstimate (t confidence interval estimate of a mean)
 */
class CmdTMeanEstimate extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdTMeanEstimate(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {

		case 2:
			if ((ok[0] = arg[0].isGeoList()) 
					&& (ok[1] = arg[1].isGeoNumeric())) {
				GeoElement[] ret = { kernel.TMeanEstimate(c.getLabel(),
						(GeoList) arg[0], (GeoNumeric) arg[1]) };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else 
				throw argErr(app, c.getName(), arg[1]);

		case 4:
			if ((ok[0] = arg[0].isGeoNumeric()) 
					&& (ok[1] = arg[1].isGeoNumeric())
					&& (ok[2] = arg[2].isGeoNumeric())
					&& (ok[3] = arg[3].isGeoNumeric()))
			{
				GeoElement[] ret = { kernel.TMeanEstimate(c.getLabel(),
						(GeoNumeric) arg[0], 
						(GeoNumeric) arg[1],
						(GeoNumeric) arg[2],
						(GeoNumeric) arg[3]) };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg[2]);
			else if (!ok[3])
				throw argErr(app, c.getName(), arg[3]);
			else
				throw argErr(app, c.getName(), arg[4]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}


/**
 * TEstimate (t confidence interval estimate of the difference of means)
 */
class CmdTMean2Estimate extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdTMean2Estimate(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {

		case 4:
			if ((ok[0] = arg[0].isGeoList()) 
					&& (ok[1] = arg[1].isGeoList())
					&& (ok[2] = arg[2].isGeoNumeric())
					&& (ok[3] = arg[3].isGeoBoolean())) {
				GeoElement[] ret = { kernel.TMean2Estimate(c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1], (GeoNumeric) arg[2], (GeoBoolean) arg[3]) };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg[2]);
			else
				throw argErr(app, c.getName(), arg[3]);

		case 8:
			if ((ok[0] = arg[0].isGeoNumeric()) 
					&& (ok[1] = arg[1].isGeoNumeric())
					&& (ok[2] = arg[2].isGeoNumeric())
					&& (ok[3] = arg[3].isGeoNumeric())
					&& (ok[4] = arg[4].isGeoNumeric())
					&& (ok[5] = arg[5].isGeoNumeric())
					&& (ok[6] = arg[6].isGeoNumeric())
					&& (ok[7] = arg[7].isGeoBoolean()))
			{
				GeoElement[] ret = { kernel.TMean2Estimate(c.getLabel(),
						(GeoNumeric) arg[0], 
						(GeoNumeric) arg[1],
						(GeoNumeric) arg[2],
						(GeoNumeric) arg[3], 
						(GeoNumeric) arg[4],
						(GeoNumeric) arg[5],
						(GeoNumeric) arg[6],
						(GeoBoolean) arg[7]) };
				return ret;

			} else if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			else if (!ok[1])
				throw argErr(app, c.getName(), arg[1]);
			else if (!ok[2])
				throw argErr(app, c.getName(), arg[2]);
			else if (!ok[3])
				throw argErr(app, c.getName(), arg[3]);
			else if (!ok[4])
				throw argErr(app, c.getName(), arg[4]);
			else if (!ok[5])
				throw argErr(app, c.getName(), arg[5]);
			else if (!ok[6])
				throw argErr(app, c.getName(), arg[6]);
			else
				throw argErr(app, c.getName(), arg[7]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}

/**
 * ANOVA test 
 */
class CmdANOVA extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdANOVA(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {

		case 1: // list of lists, result of XML conversion
			if ((ok[0] = (arg[0].isGeoList()))) {
				GeoList list = (GeoList) arg[0];

				if (list.size() == 0)
					throw argErr(app, c.getName(), arg[0]);

				if (list.get(0).isGeoList()) { 
					GeoElement[] ret = { kernel.ANOVATest(c.getLabel(),
							(GeoList) arg[0]) };
					return ret;

				} else {
					throw argErr(app, c.getName(), arg[0]);
				}
			}

		default:
			GeoList list = wrapInList(kernel, arg, arg.length, GeoElement.GEO_CLASS_LIST);
			if (list != null) {
				GeoElement[] ret = { kernel.ANOVATest(c.getLabel(), list) };
				return ret;
			}
			else{
				// null ret should mean that an arg is not a GeoList
				// so find the bad one
				for(int i = 0; i <= n; i++){
					if(!arg[i].isGeoList())
						throw argErr(app, c.getName(), arg[i]);
				}
				// throw error for any other reason ... 
				throw argErr(app, c.getName(), arg[0]);
			}
		}
	}
}




/*
 * Incircle[ <GeoPoint>, <GeoPoint>, <GeoPoint> ]
 * dsun [6/26/2011]
 */
class CmdIncircle extends CommandProcessor {
    public CmdIncircle(Kernel kernel) {
	super(kernel);
    }
    public GeoElement[] process(Command c) throws MyError {
	int n = c.getArgumentNumber();
	boolean[] ok = new boolean[n];
	GeoElement[] arg;
	switch (n) {
	case 3 :
	    arg = resArgs(c);
	    if ((ok[0] = (arg[0] .isGeoPoint()))
		&& (ok[1] = (arg[1] .isGeoPoint()))
		&& (ok[2] = (arg[2] .isGeoPoint()))) {
		GeoElement[] ret =
		{
		    kernel.Incircle(
			c.getLabel(),
			(GeoPoint) arg[0],
			(GeoPoint) arg[1],
			(GeoPoint) arg[2])};
		return ret;
	    } else {
		if (!ok[0])
		    throw argErr(app, "Incircle", arg[0]);
		else if (!ok[1])
		    throw argErr(app, "Incircle", arg[1]);
		else
		    throw argErr(app, "Incircle", arg[2]);
	    }
	default :
	    throw argNumErr(app, "Incircle", n);
	}
    }
} // CmdIncircle

class CmdCompleteSquare extends CommandProcessor {
	
	public CmdCompleteSquare (Kernel kernel) {
		super(kernel);
	}
	
final public GeoElement[] process(Command c) throws MyError {
     int n = c.getArgumentNumber();
     boolean[] ok = new boolean[n];
     GeoElement[] arg;
     arg = resArgs(c);
     
     switch (n) {
         case 1 :             
        	 if (ok[0] = (arg[0].isGeoFunction())) {
	                 GeoElement[] ret =
	                 { kernel.CompleteSquare(c.getLabel(), (GeoFunction) arg[0] )};
	             return ret;                
	         }                        
              else
            	 throw argErr(app, c.getName(), arg[0]);         
			 
	     // more than one argument
         default :
            	 throw argNumErr(app, c.getName(), n);
     }
 }    
}
