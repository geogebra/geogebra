/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * Command.java
 *
 * Created on 05. September 2001, 12:05
 */

package org.geogebra.common.kernel.arithmetic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic3D.Vector3DValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoVec2D;
import org.geogebra.common.main.App;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.Unicode;
import org.geogebra.common.util.debug.Log;

/**
 * 
 * @author Markus
 */
public class Command extends ValidExpression implements
		ReplaceChildrenByValues, GetItem {

	// list of arguments
	private ArrayList<ExpressionNode> args = new ArrayList<ExpressionNode>();
	private String name; // internal command name (in English)

	private Kernel kernel;
	private App app;
	private GeoElement[] evalGeos; // evaluated Elements
	private Macro macro; // command may correspond to a macro
	private boolean allowEvaluationForTypeCheck = true;

	/**
	 * Creates a new command object.
	 * 
	 * @param kernel
	 *            kernel
	 * @param name
	 *            internal name or translated name
	 * @param translateName
	 *            true to translate name to internal
	 * 
	 */
	public Command(Kernel kernel, String name, boolean translateName) {
		this(kernel, name, translateName, true);
	}

	/**
	 * Creates a new command object.
	 * 
	 * @param kernel
	 *            kernel
	 * @param name
	 *            internal name or translated name
	 * @param translateName
	 *            true to translate name to internal
	 * @param allowEvaluationForTypeCheck
	 *            whether this command is allowed to be evaluated in type checks
	 *            like isTextValue()
	 */
	public Command(Kernel kernel, String name, boolean translateName,
			boolean allowEvaluationForTypeCheck) {
		this.kernel = kernel;
		app = kernel.getApplication();
		this.allowEvaluationForTypeCheck = allowEvaluationForTypeCheck;

		/*
		 * need to check app.isUsingInternalCommandNames() due to clash with
		 * BinomialDist=Binomial Binomial=BinomialCoefficient Should also allow
		 * other languages to use English names for different commands
		 */

		if (translateName && !kernel.isUsingInternalCommandNames()) {
			// translate command name to internal name
			this.name = app.getReverseCommand(name);
			// in CAS functions get parsed as commands as well and we want to
			// keep the name
			if (this.name == null)
				this.name = name;
		} else {
			this.name = name;
		}
	}

	public Kernel getKernel() {
		return kernel;
	}

	/**
	 * @param arg
	 *            argument to add
	 */
	public void addArgument(ExpressionNode arg) {
		args.add(arg);
	}

	/**
	 * Returns the name of the variable at the specified argument position. If
	 * there is no variable name at this position, null is returned.
	 * 
	 * @param i
	 *            position
	 * @return name of the variable at the specified argument position
	 */
	public String getVariableName(int i) {
		if (i >= args.size())
			return null;

		ExpressionValue ev = args.get(i).getLeft();
		if (ev instanceof Variable)
			return ((Variable) ev).getName(StringTemplate.defaultTemplate);
		else if (ev instanceof GeoElement) {
			// XML Handler looks up labels of GeoElements
			// so we may end up having a GeoElement object here
			// return its name to use as local variable name
			GeoElement geo = ((GeoElement) ev);
			if (geo.isLabelSet())
				return ((GeoElement) ev).getLabelSimple();
		} else if (ev instanceof FunctionVariable) {
			return ((FunctionVariable) ev).getSetVarString();
		} else if (ev instanceof Function) {
			String str = ev.toString(StringTemplate.defaultTemplate);
			if (str.length() == 1 && StringUtil.isLetter(str.charAt(0)))
				return str;
		} else if (ev instanceof GeoVec2D) {
			if (((GeoVec2D) ev).isImaginaryUnit()) {
				return Unicode.IMAGINARY;
			}
		} else if (ev instanceof MySpecialDouble) {
			if (((MySpecialDouble) ev).isEulerConstant()) {
				return Unicode.EULER_STRING;
			}
		}

		return null;
	}

	/**
	 * @return array of arguments
	 */
	public ExpressionNode[] getArguments() {
		return args.toArray(new ExpressionNode[0]);
	}

	/**
	 * @param i
	 *            index
	 * @return i-th argument
	 */
	public ExpressionNode getArgument(int i) {
		return args.get(i);
	}

	/**
	 * @param i
	 *            index
	 * @param en
	 *            argument
	 */
	public void setArgument(int i, ExpressionNode en) {
		args.set(i, en);
	}

	/**
	 * @return number of arguments
	 */
	public int getArgumentNumber() {
		return args.size();
	}

	/**
	 * @return internal command name
	 */
	public String getName() {
		return name;
	}

	@Override
	public String toString(StringTemplate tpl) {
		return toString(true, false, tpl);
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return toString(false, false, tpl);
	}

	public String toLaTeXString(boolean symbolic, StringTemplate tpl) {
		return toString(symbolic, true, tpl);
	}

	private String toString(boolean symbolic, boolean LaTeX, StringTemplate tpl) {
		switch (tpl.getStringType()) {
		case GIAC:
			return (kernel.getGeoGebraCAS()).getCASCommand(name, args,
					symbolic, tpl);
		case LATEX:
			if (sbToString == null)
				sbToString = new StringBuilder();
			sbToString.setLength(0);
			if (name.equals("Integral")) {
				sbToString.append("\\int");
				Set<GeoElement> vars = getArgument(0).getVariables();
				String var = "x";
				if (vars != null && !vars.isEmpty())
					var = vars.iterator().next().toString(tpl);
				switch (getArgumentNumber()) {
				case 1:
					sbToString.append(" ");
					sbToString.append(getArgument(0).toString(tpl));
					break;
				case 2:
					sbToString.append(" ");
					sbToString.append(getArgument(0).toString(tpl));
					var = getArgument(1).toString(tpl);
					break;
				case 3:
					sbToString.append("\\limits_{");
					sbToString.append(getArgument(1).toString(tpl));
					sbToString.append("}^{");
					sbToString.append(getArgument(2).toString(tpl));
					sbToString.append("}");
					sbToString.append(getArgument(0).toString(tpl));
					break;
				case 4:
					sbToString.append("\\limits_{");
					sbToString.append(getArgument(2).toString(tpl));
					sbToString.append("}^{");
					sbToString.append(getArgument(3).toString(tpl));
					sbToString.append("}");
					sbToString.append(getArgument(0).toString(tpl));
					var = getArgument(1).toString(tpl);
					break;
				default:
					break;
				}
				sbToString.append("\\,\\mathrm{d}");
				sbToString.append(var);
				return sbToString.toString();
			} else if (name.equals("Sum") && getArgumentNumber() == 4) {
				sbToString.append("\\sum_{");
				sbToString.append(args.get(1).toString(tpl));
				sbToString.append("=");
				sbToString.append(args.get(2).toString(tpl));
				sbToString.append("}^{");
				sbToString.append(args.get(3).toString(tpl));
				sbToString.append("}");
				sbToString.append(args.get(0).toString(tpl));
				return sbToString.toString();
			} else if (name.equals("Product") && getArgumentNumber() == 4) {
				sbToString.append("\\prod_{");
				sbToString.append(args.get(1).toString(tpl));
				sbToString.append("=");
				sbToString.append(args.get(2).toString(tpl));
				sbToString.append("}^{");
				sbToString.append(args.get(3).toString(tpl));
				sbToString.append("}");
				sbToString.append(args.get(0).toString(tpl));
				return sbToString.toString();
			}
		default:
			if (sbToString == null)
				sbToString = new StringBuilder();
			sbToString.setLength(0);

			// GeoGebra command syntax
			if (tpl.isPrintLocalizedCommandNames()) {
				sbToString.append(app.getLocalization().getCommand(name));
			} else {
				sbToString.append(name);
			}
			if (LaTeX) {
				sbToString.append(" \\left");
			}
			sbToString.append('[');
			int size = args.size();
			for (int i = 0; i < size; i++) {
				sbToString.append(toString(args.get(i), symbolic, LaTeX, tpl));
				sbToString.append(',');
			}
			if (size > 0)
				sbToString.deleteCharAt(sbToString.length() - 1);
			if (LaTeX) {
				sbToString.append(" \\right");
			}
			sbToString.append(']');

			return sbToString.toString();
		}
	}

	private StringBuilder sbToString;

	private static String toString(ExpressionValue ev, boolean symbolic,
			boolean LaTeX, StringTemplate tpl) {
		if (LaTeX) {
			return ev.toLaTeXString(symbolic, tpl);
		}
		return symbolic ? ev.toString(tpl) : ev.toValueString(tpl);
	}

	/**
	 * @return array of resulting geos
	 */
	public GeoElement[] evaluateMultiple() {
		GeoElement[] geos = null;
		geos = kernel.getAlgebraProcessor().processCommand(this, false);
		return geos;
	}

	@Override
	public ExpressionValue evaluate(StringTemplate tpl) {
		// not yet evaluated: process command
		if (evalGeos == null) {
			evalGeos = evaluateMultiple();
		}
		if (evalGeos != null && evalGeos.length >= 1) {
			return evalGeos[0];
		}
		App.debug("invalid command evaluation: " + name);
		throw new MyError(app.getLocalization(), app.getLocalization()
				.getError("InvalidInput") + ":\n" + this);

	}

	public ExpressionValue simplify(StringTemplate tpl) {
		// not yet evaluated: process command
		ExpressionValue result = kernel.getAlgebraProcessor().simplifyCommand(
				this, false);
		if (result instanceof GeoElement) {
			evalGeos = new GeoElement[]{(GeoElement)result};
		}
		if (result != null) {
			return result;
		}
		App.debug("invalid command evaluation: " + name);
		throw new MyError(app.getLocalization(), app.getLocalization()
				.getError("InvalidInput") + ":\n" + this);

	}

	public void resolveVariables() {
		// standard case:
		// nothing to do here: argument variables are resolved
		// while command processing (see evaluate())

		// CAS parsing case: we need to resolve arguments also
		if (kernel.isResolveUnkownVarsAsDummyGeos()) {
			for (int i = 0; i < args.size(); i++) {
				args.get(i).resolveVariables();
			}

			// avoid evaluation of command
			allowEvaluationForTypeCheck = false;
		}
	}

	// rewritten to cope with {Root[f]}
	// Michael Borcherds 2008-10-02
	public boolean isConstant() {

		// not yet evaluated: process command
		if (evalGeos == null)
			evalGeos = evaluateMultiple();

		if (evalGeos == null || evalGeos.length == 0)
			throw new MyError(app.getLocalization(), app.getLocalization()
					.getError("InvalidInput") + ":\n" + this);

		for (int i = 0; i < evalGeos.length; i++)
			if (!evalGeos[i].isConstant())
				return false;
		return true;

	}

	public boolean isLeaf() {
		// return evaluate().isLeaf();
		return true;
	}

	/*
	 * Type checking with evaluate Try to evaluate using GeoGebra if fails, try
	 * with CAS else throw Exception
	 */

	public boolean isNumberValue() {
		if (!allowEvaluationForTypeCheck) {
			return false;
		}
		try {
			return evaluate(StringTemplate.defaultTemplate).isNumberValue();
		} catch (MyError ex) {
			ExpressionValue ev = kernel.getGeoGebraCAS().getCurrentCAS()
					.evaluateToExpression(this, null, kernel);
			if (ev != null)
				return ev.unwrap().isNumberValue();
			throw ex;
		}
	}

	@Override
	public boolean evaluatesToNonComplex2DVector() {
		if (!allowEvaluationForTypeCheck) {
			return false;
		}
		try {
			return evaluate(StringTemplate.defaultTemplate) instanceof VectorValue;
		} catch (MyError ex) {
			ExpressionValue ev = kernel.getGeoGebraCAS().getCurrentCAS()
					.evaluateToExpression(this, null, kernel);
			if (ev != null)
				return ev.unwrap().evaluatesToNonComplex2DVector();
			throw ex;
		}
	}

	@Override
	public boolean evaluatesToVectorNotPoint() {
		if (!allowEvaluationForTypeCheck) {
			return false;
		}
		try {
			return evaluate(StringTemplate.defaultTemplate) instanceof VectorValue;
		} catch (MyError ex) {
			ExpressionValue ev = kernel.getGeoGebraCAS().getCurrentCAS()
					.evaluateToExpression(this, null, kernel);
			if (ev != null)
				return ev.unwrap().evaluatesToNonComplex2DVector();
			throw ex;
		}
	}

	@Override
	public boolean evaluatesToText() {
		if (!allowEvaluationForTypeCheck) {
			return false;
		}
		if (app.getInternalCommand(name) == null
				&& kernel.getMacro(name) == null) {
			return false;
		}
		try {
			return evaluate(StringTemplate.defaultTemplate).evaluatesToText();
		} catch (MyError ex) {
			ExpressionValue ev = kernel.getGeoGebraCAS().getCurrentCAS()
					.evaluateToExpression(this, null, kernel);
			if (ev != null)
				return ev.unwrap().evaluatesToText();
			throw ex;
		}
	}

	public ExpressionValue deepCopy(Kernel kernel1) {
		Command c = new Command(kernel1, name, false);
		// copy arguments
		int size = args.size();
		for (int i = 0; i < size; i++) {
			c.addArgument(args.get(i).getCopy(kernel1));
		}
		return c;
	}

	public void replaceChildrenByValues(GeoElement geo) {
		int size = args.size();
		for (int i = 0; i < size; i++) {
			args.get(i).replaceChildrenByValues(geo);
		}
	}

	public HashSet<GeoElement> getVariables() {
		HashSet<GeoElement> set = new HashSet<GeoElement>();
		int size = args.size();
		for (int i = 0; i < size; i++) {
			Set<GeoElement> s = args.get(i).getVariables();
			if (s != null)
				set.addAll(s);
		}
		return set;
	}

	final public boolean contains(ExpressionValue ev) {
		return ev == this;
	}

	@Override
	public boolean evaluatesToList() {
		if ("x".equals(getName()) || "y".equals(getName())
				|| "z".equals(getName()) || "If".equals(getName())) {
			return this.getArgument(0).evaluatesToList();
		}
		if (!allowEvaluationForTypeCheck) {
			return false;
		}
		try {
			return evaluate(StringTemplate.defaultTemplate) instanceof ListValue;
		} catch (MyError ex) {
			ExpressionValue ev = kernel.getGeoGebraCAS().getCurrentCAS()
					.evaluateToExpression(this, null, kernel);
			if (ev != null)
				return ev.unwrap() instanceof ListValue;
			throw ex;
		}

	}

	/**
	 * @return macro macro associated with this command
	 */
	public final Macro getMacro() {
		return macro;
	}

	/**
	 * @param macro
	 *            macro associated with this command
	 */
	public final void setMacro(Macro macro) {
		this.macro = macro;
	}

	final public boolean evaluatesTo3DVector() {
		if (!allowEvaluationForTypeCheck) {
			return false;
		}
		try {
			return evaluate(StringTemplate.defaultTemplate) instanceof Vector3DValue;
		} catch (MyError ex) {
			ExpressionValue ev = kernel.getGeoGebraCAS().getCurrentCAS()
					.evaluateToExpression(this, null, kernel);
			if (ev != null)
				return ev.unwrap() instanceof Vector3DValue;
			throw ex;
		}
	}

	@Override
	public boolean isTopLevelCommand() {
		return true;
	}

	@Override
	public Command getTopLevelCommand() {
		return this;
	}

	public String toOutputValueString(StringTemplate tpl) {
		return toValueString(tpl);
	}

	@Override
	public ExpressionValue traverse(Traversing t) {
		ExpressionValue v = t.process(this);
		if (v != this)
			return v;
		for (int i = 0; i < args.size(); i++) {
			ExpressionNode en = args.get(i).traverse(t).wrap();
			args.set(i, en);
		}
		return this;
	}

	@Override
	public boolean inspect(Inspecting t) {
		if (t.check(this))
			return true;
		for (int i = 0; i < args.size(); i++) {
			if (args.get(i).inspect(t))
				return true;
		}
		return false;
	}

	public ExpressionValue getItem(int i) {
		return args.get(i);
	}

	@Override
	public boolean hasCoords() {
		if ("x".equals(name) || "y".equals(name) || "z".equals(name))
			return false;
		return true;
	}

	/**
	 * for commands with different output types and that need to know each
	 * lenght to set labels correctly
	 */
	private int[] outputSizes;

	/**
	 * set output sizes
	 * 
	 * @param sizes
	 *            output sizes
	 */
	public void setOutputSizes(int[] sizes) {
		outputSizes = sizes;
	}

	/**
	 * 
	 * @return output sizes
	 */
	public int[] getOutputSizes() {
		return outputSizes;
	}

	public int getLength() {
		return getArgumentNumber();
	}

	/**
	 * Replaces all Variable objects with the given varName in the arguments by
	 * the given FunctionVariable object.
	 * 
	 * @param varName
	 *            variable name
	 * @param fVar
	 *            replacement variable
	 * @return number of replacements done
	 */
	public int replaceVariables(String varName, FunctionVariable fVar) {
		int replacements = 0;

		for (ExpressionNode arg : args) {
			replacements += arg.replaceVariables(varName, fVar);
		}

		return replacements;
	}

	public ExpressionNode wrap() {
		return new ExpressionNode(kernel, this);
	}

	public static ExpressionNode xyzCAS(ExpressionNode en, int i, boolean mayCheck,ArrayList<ExpressionNode > undecided) {
			Operation[] ops = new Operation[]{Operation.XCOORD, Operation.YCOORD, Operation.ZCOORD};
			Kernel k = en.getKernel();
			
			ExpressionNode en2;
			if(en.evaluatesToList()){
				  Command cmd = new Command(k, "Element", true, mayCheck );
		          cmd.addArgument( en );
		          //Element uses 1 for first element
		          cmd.addArgument( new MyDouble(k,i+1).wrap() );
		          en2 = cmd.wrap();
			}else if(en.hasCoords()){
				en2 = new ExpressionNode(k,en.unwrap(),ops[i],null);
				/*char funName = (char) ('x'+i);
				  Command cmd = new Command(k, funName+"", true, mayCheck );
		          cmd.addArgument( en );
		          en2 = cmd.wrap();*/
			}else{ 
				char funName = (char) ('x'+i);
				en2 = new ExpressionNode(k,new FunctionVariable(k,funName+""),Operation.MULTIPLY_OR_FUNCTION,en);
				undecided.add(en2);
			}
			Log.debug(en2);
			//App.printStacktrace("");
			return en2;
	     
          
	}

	public void setName(String string) {
		this.name = string;
	}

}
