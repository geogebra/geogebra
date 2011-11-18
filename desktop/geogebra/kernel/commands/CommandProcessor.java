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

import geogebra.gui.view.spreadsheet.SpreadsheetView;
import geogebra.kernel.CircularDefinitionException;
import geogebra.kernel.Construction;
import geogebra.kernel.Kernel;
import geogebra.kernel.MyPoint;
import geogebra.kernel.algos.AlgoCellRange;
import geogebra.kernel.arithmetic.BooleanValue;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.FunctionalNVar;
import geogebra.kernel.arithmetic.MySpecialDouble;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.arithmetic.Variable;
import geogebra.kernel.geos.CasEvaluableFunction;
import geogebra.kernel.geos.Dilateable;
import geogebra.kernel.geos.GeoAngle;
import geogebra.kernel.geos.GeoBoolean;
import geogebra.kernel.geos.GeoButton;
import geogebra.kernel.geos.GeoConic;
import geogebra.kernel.geos.GeoCurveCartesian;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoFunction;
import geogebra.kernel.geos.GeoFunctionNVar;
import geogebra.kernel.geos.GeoFunctionable;
import geogebra.kernel.geos.GeoImage;
import geogebra.kernel.geos.GeoLine;
import geogebra.kernel.geos.GeoList;
import geogebra.kernel.geos.GeoLocus;
import geogebra.kernel.geos.GeoNumeric;
import geogebra.kernel.geos.GeoPoint;
import geogebra.kernel.geos.GeoPolyLineInterface;
import geogebra.kernel.geos.GeoPolygon;
import geogebra.kernel.geos.GeoSegment;
import geogebra.kernel.geos.GeoText;
import geogebra.kernel.geos.GeoVec3D;
import geogebra.kernel.geos.GeoVector;
import geogebra.kernel.geos.Path;
import geogebra.kernel.geos.Region;
import geogebra.kernel.implicit.GeoImplicitPoly;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.Application;
import geogebra.main.MyError;
import geogebra.util.ImageManager;
import geogebra.util.Unicode;

import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Calendar;
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


class CmdBarycenter extends CommandProcessor 
{

	public CmdBarycenter(Kernel kernel) 
	{
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isGeoList()) &&
					(ok[1] = arg[1].isGeoList())) {
				GeoElement[] ret = { kernel.Barycenter(c.getLabel(),
						(GeoList)arg[0], (GeoList)arg[1])} ;
				return ret;
				
			} else{
				if(!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				throw argErr(app, c.getName(), arg[1]);
			}
		default:
			throw argNumErr(app, "Barycenter", n);
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
			throw argNumErr(app, "Kimberling", n);
		}
	}
}


class CmdTriangleCubic extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdTriangleCubic(Kernel kernel) {
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
				GeoElement[] ret = { kernel.TriangleCubic(c.getLabel(),
						(GeoPoint)arg[0], (GeoPoint)arg[1], (GeoPoint)arg[2],
						(NumberValue) arg[3])} ;
				return ret;
				
			}
				
			else{
				if(!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				if(!ok[1])
					throw argErr(app, c.getName(), arg[1]);
				if(!ok[2])
					throw argErr(app, c.getName(), arg[2]);
				throw argErr(app, c.getName(), arg[3]);
			}
		default:
			throw argNumErr(app, "TriangleCubic", n);
		}
	}
}

class CmdTriangleCurve extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdTriangleCurve(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 4:
			GeoNumeric ta=null,tb=null,tc=null;
			
			ta =new GeoNumeric(cons);
			tb =new GeoNumeric(cons);
			tc =new GeoNumeric(cons);
			cons.addLocalVariable("A", ta);
			cons.addLocalVariable("B", tb);
			cons.addLocalVariable("C", tc);
			arg = resArgs(c);

			if ((ok[0] = arg[0].isGeoPoint()) &&
					(ok[1] = arg[1].isGeoPoint()) &&
					(ok[2] = arg[2].isGeoPoint()) &&
					(ok[3] = arg[3].isGeoImplicitPoly())) {
				
				
				GeoElement[] ret = { kernel.TriangleCubic(c.getLabel(),
						(GeoPoint)arg[0], (GeoPoint)arg[1], (GeoPoint)arg[2],
						(GeoImplicitPoly) arg[3],ta,tb,tc)} ;
				cons.removeLocalVariable("A");
				cons.removeLocalVariable("B");
				cons.removeLocalVariable("C");
				return ret;
				
			}			
			else{
				if(!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				if(!ok[1])
					throw argErr(app, c.getName(), arg[1]);
				if(!ok[2])
					throw argErr(app, c.getName(), arg[2]);
				throw argErr(app, c.getName(), arg[3]);
			}
		default:
			throw argNumErr(app, "TriangleCubic", n);
		}
	}
}


class CmdTrilinear extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdTrilinear(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 6:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isGeoPoint()) &&
					(ok[1] = arg[1].isGeoPoint()) &&
					(ok[2] = arg[2].isGeoPoint()) &&
					(ok[3] = arg[3].isNumberValue()) &&
					(ok[4] = arg[5].isNumberValue()) &&
					(ok[5] = arg[5].isNumberValue())) {
				GeoElement[] ret = { kernel.Trilinear(c.getLabel(),
						(GeoPoint)arg[0], (GeoPoint)arg[1], (GeoPoint)arg[2],
						(NumberValue) arg[3], (NumberValue) arg[4], (NumberValue) arg[5])} ;
				return ret;
				
			} else{
				if(!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				if(!ok[1])
					throw argErr(app, c.getName(), arg[1]);
				if(!ok[2])
					throw argErr(app, c.getName(), arg[2]);
				if(!ok[3])
					throw argErr(app, c.getName(), arg[3]);
				if(!ok[4])
					throw argErr(app, c.getName(), arg[4]);
				throw argErr(app, c.getName(), arg[5]);
			}
		default:
			throw argNumErr(app, "Trilinear", n);
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
				GeoElement[] ret = { kernel.Area(c.getLabel(),
						(GeoPolygon) arg[0]) };
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
