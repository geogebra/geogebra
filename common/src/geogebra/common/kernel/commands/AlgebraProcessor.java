/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */
package geogebra.common.kernel.commands;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoDependentBoolean;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.arithmetic.BooleanValue;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.Equation;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.FunctionNVar;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.MyList;
import geogebra.common.kernel.arithmetic.MyStringBuffer;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.arithmetic.Parametric;
import geogebra.common.kernel.arithmetic.Polynomial;
import geogebra.common.kernel.arithmetic.TextValue;
import geogebra.common.kernel.arithmetic.Traversing.FVarCollector;
import geogebra.common.kernel.arithmetic.Traversing.PolyReplacer;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.kernel.arithmetic.VectorValue;
import geogebra.common.kernel.geos.GeoAngle;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoDummyVariable;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoFunctionNVar;
import geogebra.common.kernel.geos.GeoFunctionable;
import geogebra.common.kernel.geos.GeoInterval;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoScriptAction;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.geos.GeoUserInputElement;
import geogebra.common.kernel.geos.GeoVec2D;
import geogebra.common.kernel.geos.GeoVec3D;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.kernel.implicit.AlgoDependentImplicitPoly;
import geogebra.common.kernel.implicit.GeoImplicitPoly;
import geogebra.common.kernel.kernelND.GeoConicNDConstants;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.parser.ParseException;
import geogebra.common.kernel.parser.ParserInterface;
import geogebra.common.main.App;
import geogebra.common.main.MyError;
import geogebra.common.plugin.GeoClass;
import geogebra.common.plugin.Operation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * Processes algebra input as Strings and valid expressions into GeoElements 
 * @author Markus
 *
 */
public class AlgebraProcessor {

	/** kernel */
	protected Kernel kernel;
	private Construction cons;
	private App app;
	private ParserInterface parser;
	/** command dispatcher */
	protected CommandDispatcher cmdDispatcher;

	
	/**
	 * @param kernel kernel
	 * @param commandDispatcher command dispatcher
	 */
	public AlgebraProcessor(Kernel kernel,CommandDispatcher commandDispatcher) {
		this.kernel = kernel;
		cons = kernel.getConstruction();

		this.cmdDispatcher = commandDispatcher;
		app = kernel.getApplication();
		parser = kernel.getParser();
	}

	

	/**
	 * @return set of all public commands (i.e. no compatibility commands)
	 */
	public Set<String> getPublicCommandSet() {
		return cmdDispatcher.getPublicCommandSet();
	}


	/**
	 * Returns the localized name of a command subset. Indices are defined in
	 * CommandDispatcher.
	 * @param index commands subtable index
	 * @return set of commands for given subtable
	 */
	public String getSubCommandSetName(int index) {
		return cmdDispatcher.getSubCommandSetName(index);
	}

	/**
	 * Returns whether the given command name is supported in GeoGebra.
	 * @param cmd command name
	 * @return true if available
	 */
	public boolean isCommandAvailable(String cmd) {
		return cmdDispatcher.isCommandAvailable(cmd);
	}

	/**
	 * @param c command
	 * @param labelOutput true to label output
	 * @return resulting geos
	 * @throws MyError e.g. on syntax error
	 */
	final public GeoElement[] processCommand(Command c,
			boolean labelOutput) throws MyError {
		return cmdDispatcher.processCommand( c, labelOutput);
	}

	/**
	 * Processes the given casCell, i.e. compute its output depending on its
	 * input. Note that this may create an additional twin GeoElement.
	 * @param casCell cas cell
	 * @throws MyError e.g. on syntax error
	 */
	final public void processCasCell(GeoCasCell casCell) throws MyError {
		// check for CircularDefinition
		if (casCell.isCircularDefinition()) {
			// set twin geo to undefined
			casCell.computeOutput();
			casCell.updateCascade();
			app.showError("CircularDefinition");
			return;
		}

		AlgoElement algoParent = casCell.getParentAlgorithm();
		boolean prevFree = algoParent == null;
		boolean nowFree = casCell.getGeoElementVariables() == null;
		boolean needsRedefinition = false;

		if (prevFree) {
			if (nowFree) {
				// free -> free, e.g. m := 7 -> m := 8
				cons.addToConstructionList(casCell, true);
				casCell.computeOutput(); // create twinGeo if necessary
				casCell.setLabelOfTwinGeo();
				needsRedefinition = false;
			} else {
				// free -> dependent, e.g. m := 7 -> m := c+2
				if (casCell.isOutputEmpty() && !casCell.hasChildren()) {
					// this is a new casCell
					cons.removeFromConstructionList(casCell);
					Kernel.DependentCasCell(casCell);
					needsRedefinition = false;
				} else {
					// existing casCell with possible twinGeo
					needsRedefinition = true;
				}
			}
		} else {
			if (nowFree) {
				// dependent -> free, e.g. m := c+2 -> m := 7
				// algorithm will be removed through redefinition
				needsRedefinition = true;
			} else {
				// dependent -> dependent, e.g. m := c+2 -> m := c+d
				// we already have an algorithm but need redefinition
				// in order to move it to the right place in construction list
				needsRedefinition = true;
			}
		}

		if (needsRedefinition) {
			try {
				// update construction order and
				// rebuild construction using XML
				cons.changeCasCell(casCell);
				casCell.computeOutput();
				casCell.updateCascade();
			} catch (Exception e) {
				e.printStackTrace();
				casCell.setError("RedefinitionFailed");
				// app.showError(e.getMessage());
			}
		} else {
			casCell.notifyAdd();
			casCell.updateCascade();
		}
	}
	
	/**
	 * decides if the ExperssionNode leaf could become a plotable function or not
	 * eg. leaf = x^2+1 -> functionable f(x) = x^2+1
	 * leaf = (a,1) -> not functionable f(x) != (a,1)
	 * @param node node to be decided
	 * @return if leaf not able to become a function
	 */
	public boolean isNotFunctionAble(ExpressionNode node){
		ExpressionNode n = node;
		boolean result = false;
		// command is leaf: process command
		if (n.isLeaf()) {
			result = result | isNotFunctionAbleEV(n.getLeft());
			return result;
		}
		result = result | isNotFunctionAble(n.getLeftTree());
		result = result | isNotFunctionAble(n.getRightTree());
		return result;
	}
	
	/**
	 * decides if the ExperssionValue leaf could become a plotable function or not
	 * eg. leaf = x^2 -> functionable f(x) = x^2
	 * leaf = (a,1) -> not functionable f(x) != (a,1)
	 * @param leaf node to be decided
	 * @return if leaf not able to become a function
	 */
	public boolean isNotFunctionAbleEV(ExpressionValue leaf){
		boolean result = false;
		if (leaf instanceof Command)
			result = result | true;
		if (leaf instanceof Equation) 
			result = result | true;
		if (leaf instanceof Function) 
			result = result | true;
		if (leaf instanceof FunctionNVar) 
			result = result | true;
		if (leaf.isBooleanValue())
			result = result | true;
		if (leaf.isNumberValue())
			result = result | false;
		if (leaf.isVectorValue())
			result = result | true;
		if (leaf.isVector3DValue())
			result = result | true;
		if (leaf.isTextValue())
			result = result | true;
		if (leaf instanceof GeoDummyVariable)
			result = result | !((GeoDummyVariable)leaf).getVarName().equals("x");
		if (leaf instanceof MyList)
			result = result | true;
			//MyList myList = (MyList) leaf;
			//for(int i=0; i<myList.size(); i++)
			//	result = result | isNotFunctionAbleEV(myList.getItem(i));
		if (leaf instanceof Function) 
			result = result | !((Function)leaf).isFunctionVariable("x");
		if (leaf instanceof FunctionNVar) 
			result = result | true;
		return result;
	}

	/**
	 * for AlgebraView changes in the tree selection and redefine dialog
	 * @param geo old geo
	 * @param newValue new value
	 * @param redefineIndependent  true to allow redefinition of free objects
	 * @param storeUndoInfo true to make undo step 
	 * 
	 * @return changed geo
	 */
	public GeoElement changeGeoElement(GeoElement geo, String newValue,
			boolean redefineIndependent, boolean storeUndoInfo) {

		try {
			return changeGeoElementNoExceptionHandling(geo, newValue,
					redefineIndependent, storeUndoInfo);
		} catch (Exception e) {
			app.showError(e.getMessage());
			return null;
		}
	}

	/**
	 * for AlgebraView changes in the tree selection and redefine dialog
	 * @param geo old geo
	 * @param newValueVE new value
	 * @param redefineIndependent  true to allow redefinition of free objects
	 * @param storeUndoInfo true to make undo step 
	 * 
	 * @return changed geo
	 */
	public GeoElement changeGeoElement(GeoElement geo,
			ValidExpression newValueVE, boolean redefineIndependent,
			boolean storeUndoInfo) {

		try {
			return changeGeoElementNoExceptionHandling(geo, newValueVE,
					redefineIndependent, storeUndoInfo);
		} catch (Exception e) {
			app.showError(e.getMessage());
			return null;
		}
	}

	/**
	 * for AlgebraView changes in the tree selection and redefine dialog
	 * 
	* @param geo old geo
	 * @param newValue new value
	 * @param redefineIndependent  true to allow redefinition of free objects
	 * @param storeUndoInfo true to make undo step 
	 * @return changed geo
	 * @throws Exception e.g. parse exception or circular definition
	 *
	 */
	public GeoElement changeGeoElementNoExceptionHandling(GeoElement geo,
			String newValue, boolean redefineIndependent, boolean storeUndoInfo)
			throws Exception {

		try {
			ValidExpression ve = parser.parseGeoGebraExpression(newValue);
			return changeGeoElementNoExceptionHandling(geo, ve,
					redefineIndependent, storeUndoInfo);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(app.getError("InvalidInput") + ":\n" + newValue);
		} catch (MyError e) {
			e.printStackTrace();
			throw new Exception(e.getLocalizedMessage());
		} catch (Error e) {
			e.printStackTrace();
			throw new Exception(app.getError("InvalidInput") + ":\n" + newValue);
		}
	}

	/**
	 * for AlgebraView changes in the tree selection and redefine dialog
	 * @param geo old geo
	 * @param newValue new value
	 * @param redefineIndependent true to make sure independent are redefined instead of value change
	 * @param storeUndoInfo true to makeundo step
	 * 
	 * @return changed geo
	 * @throws Exception  circular definition
	 */
	public GeoElement changeGeoElementNoExceptionHandling(GeoElement geo,
			ValidExpression newValue, boolean redefineIndependent,
			boolean storeUndoInfo) throws Exception {
		String oldLabel, newLabel;
		GeoElement[] result;

		try {
			oldLabel = geo.getLabel(StringTemplate.defaultTemplate);
			if(geo instanceof GeoFunction)
				cons.registerFunctionVariable(((GeoFunction)geo).getFunction().getVarString(StringTemplate.defaultTemplate));
			newLabel = newValue.getLabel();

			if (newLabel == null) {
				newLabel = oldLabel;
				newValue.setLabel(newLabel);
			}

			// make sure that points stay points and vectors stay vectors
			if (newValue instanceof ExpressionNode) {
				ExpressionNode n = (ExpressionNode) newValue;
				if (geo.isGeoPoint())
					n.setForcePoint();
				else if (geo.isGeoVector())
					n.setForceVector();
				else if (geo.isGeoFunction())
					n.setForceFunction();
			}

			if (newLabel.equals(oldLabel)) {
				// try to overwrite
				result = processValidExpression(newValue, redefineIndependent);
				if (result != null && storeUndoInfo)
					app.storeUndoInfo();
				return result[0];
			} else if (cons.isFreeLabel(newLabel)) {
				newValue.setLabel(oldLabel);
				// rename to oldLabel to enable overwriting
				result = processValidExpression(newValue, redefineIndependent);
				result[0].setLabel(newLabel); // now we rename
				if (storeUndoInfo)
					app.storeUndoInfo();
				return result[0];
			} else {
				String str[] = { "NameUsed", newLabel };
				throw new MyError(app, str);
			}
		} catch (CircularDefinitionException e) {
			App.debug("CircularDefinition");
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(app.getError("InvalidInput") + ":\n" + newValue);
		} catch (MyError e) {
			e.printStackTrace();
			throw new Exception(e.getLocalizedMessage());
		} catch (Error e) {
			e.printStackTrace();
			throw new Exception(app.getError("InvalidInput") + ":\n" + newValue);
		}finally{
			cons.registerFunctionVariable(null);
		}
	}

	/*
	 * methods for processing an input string
	 */
	// returns non-null GeoElement array when successful
	/**
	 * @param cmd string to process
	 * @param storeUndo true to make undo step
	 * @return resulting geos
	 */
	public GeoElement[] processAlgebraCommand(String cmd, boolean storeUndo) {

		try {
			return processAlgebraCommandNoExceptionHandling(cmd, storeUndo,
					true, false);
		} catch (Exception e) {
			e.printStackTrace();
			app.showError(e.getMessage());
			return null;
		}
	}

	// G.Sturr 2010-7-5
	// 
	/**
	 * normal usage ... default to show error dialog (Exceptions hidden)
	 * @param cmd string to process
	 * @param storeUndo true to create undo step
	 * @return resulting geos
	 */
	public GeoElement[] processAlgebraCommandNoExceptions(String cmd,
			boolean storeUndo) {

		try {
			return processAlgebraCommandNoExceptionHandling(cmd, storeUndo,
					true, false);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Processes the string and hides all errors
	 * @param str string to process
	 * @param storeUndo true to create undo step
	 * @return resulting elements
	 */
	public GeoElement[] processAlgebraCommandNoExceptionsOrErrors(String str,
			boolean storeUndo) {

		try {
			return processAlgebraCommandNoExceptionHandling(str, storeUndo,
					false, false);
		} catch (Exception e) {
			return null;
		} catch (MyError e) {
			return null;
		}
	}

	// G.Sturr 2010-7-5
	// added 'allowErrorDialog' flag to handle the case of unquoted text
	// entries in the spreadsheet
	/**
	 * @param cmd string to process
	 * @param storeUndo true to make undo step
	 * @param allowErrorDialog true to allow dialogs
	 * @param throwMyError true to throw MyErrors (if dialogs are not allowed)
	 * @return resulting geos
	 * @throws Exception e.g. circular definition or parse exception
	 */
	public GeoElement[] processAlgebraCommandNoExceptionHandling(String cmd,
			boolean storeUndo, boolean allowErrorDialog, boolean throwMyError)
			throws Exception {
		ValidExpression ve;
		try {
			ve = parser.parseGeoGebraExpression(cmd);			
		} catch (Exception e) {

			e.printStackTrace();
			if (allowErrorDialog) {
				app.showError(app.getError("InvalidInput") + ":\n" + cmd);
				return null;
			}
			throw new MyException(app.getError("InvalidInput") + ":\n" + cmd,
					MyException.INVALID_INPUT);
		} catch (MyError e) {
			e.printStackTrace();
			if (allowErrorDialog) {
				app.showError(e.getLocalizedMessage());
				return null;
			}
			throw new MyException(e,MyException.IMBALANCED_BRACKETS);
		} catch (Error e) {
			e.printStackTrace();
			if (allowErrorDialog) {
				app.showError(app.getError("InvalidInput") + ":\n" + cmd);
				return null;
			}
			throw new Exception(app.getError("InvalidInput") + ":\n" + cmd);
		}

		// process ValidExpression (built by parser)
		GeoElement[] geoElements = null;
		try {		
			geoElements = processValidExpression(ve);
			if (storeUndo && geoElements != null)
				app.storeUndoInfo();
		} catch (MyError e) {
			e.printStackTrace();
			// throw new Exception(e.getLocalizedMessage());

			// show error with nice "Show Online Help" box
			if (allowErrorDialog) {// G.Sturr 2010-7-5
				app.showError(e);
				e.printStackTrace();
			} else if (throwMyError){
				throw new MyError(app, e.getLocalizedMessage(),
						e.getcommandName());
			}
			return null;
		} catch (CircularDefinitionException e) {
			App.debug("CircularDefinition");
			throw e;
		} catch (Exception ex) {
			App.debug("Exception");
			ex.printStackTrace();
			throw new Exception(app.getError("Error") + ":\n"
					+ ex.getLocalizedMessage());
		}finally{
			kernel.getConstruction().registerFunctionVariable(null);
		}
		return geoElements;
	}

	/**
	 * Parses given String str and tries to evaluate it to a double. Returns
	 * Double.NaN if something went wrong.
	 * @param str string to process
	 * @return result as double
	 */
	public double evaluateToDouble(String str) {
		return evaluateToDouble(str, false);
	}

	/**
	 * Parses given String str and tries to evaluate it to a double. Returns
	 * Double.NaN if something went wrong.
	 * @param str string to process
	 * @param suppressErrors false to show error messages (only stacktrace otherwise)
	 * @return result as double
	 */
	public double evaluateToDouble(String str, boolean suppressErrors) {
		try {
			ValidExpression ve = parser.parseExpression(str);
			ExpressionNode en = (ExpressionNode) ve;
			en.resolveVariables(false);
			NumberValue nv = (NumberValue) en.evaluate(StringTemplate.defaultTemplate);
			return nv.getDouble();
		} catch (Exception e) {
			e.printStackTrace();
			if (!suppressErrors)
				app.showError("InvalidInput", str);
			return Double.NaN;
		} catch (MyError e) {
			e.printStackTrace();
			if (!suppressErrors)
				app.showError(e);
			return Double.NaN;
		} catch (Error e) {
			e.printStackTrace();
			if (!suppressErrors)
				app.showError("InvalidInput", str);
			return Double.NaN;
		}
	}

	/**
	 * Parses given String str and tries to evaluate it to a GeoBoolean object.
	 * Returns null if something went wrong.
	 * @param str string to process
	 * @return resulting boolean
	 */
	public GeoBoolean evaluateToBoolean(String str) {
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		GeoBoolean bool = null;
		try {
			ValidExpression ve = parser.parseGeoGebraExpression(str);
			GeoElement[] temp = processValidExpression(ve);
			bool = (GeoBoolean) temp[0];
		} catch (CircularDefinitionException e) {
			App.debug("CircularDefinition");
			app.showError("CircularDefinition");
		} catch (Exception e) {
			e.printStackTrace();
			app.showError("InvalidInput", str);
		} catch (MyError e) {
			e.printStackTrace();
			app.showError(e);
		} catch (Error e) {
			e.printStackTrace();
			app.showError("InvalidInput", str);
		}

		cons.setSuppressLabelCreation(oldMacroMode);
		return bool;
	}

	/**
	 * Parses given String str and tries to evaluate it to a List object.
	 * Returns null if something went wrong. Michael Borcherds 2008-04-02
	 * @param str input string
	 * @return resulting list
	 */
	public GeoList evaluateToList(String str) {
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		GeoList list = null;
		try {
			ValidExpression ve = parser.parseGeoGebraExpression(str);
			GeoElement[] temp = processValidExpression(ve);
			// CAS in GeoGebraWeb dies badly if we don't handle this case (Simon's hack): 
			// list = (GeoList) temp[0];
			if (temp[0] instanceof GeoList) {
				list = (GeoList) temp[0];
				} else {
				App.error("return value was not a list");
				}
		} catch (CircularDefinitionException e) {
			App.debug("CircularDefinition");
			//app.showError("CircularDefinition");
		} catch (Exception e) {
			e.printStackTrace();
			//app.showError("InvalidInput", str);
		} catch (MyError e) {
			e.printStackTrace();
			//app.showError(e);
		} catch (Error e) {
			e.printStackTrace();
			//app.showError("InvalidInput", str);
		}

		cons.setSuppressLabelCreation(oldMacroMode);
		return list;
	}

	/**
	 * Parses given String str and tries to evaluate it to a GeoFunction Returns
	 * null if something went wrong. Michael Borcherds 2008-04-04
	 * @param str input string
	 * @param suppressErrors false to show error messages (only stacktrace otherwise) 
	 * @return resulting function
	 */
	public GeoFunction evaluateToFunction(String str, boolean suppressErrors) {
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		GeoFunction func = null;
		try {
			ValidExpression ve = parser.parseGeoGebraExpression(str);
			GeoElement[] temp = processValidExpression(ve);

			if (temp[0].isGeoFunctionable()) {
				GeoFunctionable f = (GeoFunctionable) temp[0];
				func = f.getGeoFunction();
			} else if (!suppressErrors)
				app.showError("InvalidInput", str);

		} catch (CircularDefinitionException e) {
			App.debug("CircularDefinition");
			if (!suppressErrors)
				app.showError("CircularDefinition");
		} catch (Exception e) {
			e.printStackTrace();
			if (!suppressErrors)
				app.showError("InvalidInput", str);
		} catch (MyError e) {
			e.printStackTrace();
			if (!suppressErrors)
				app.showError(e);
		} catch (Error e) {
			e.printStackTrace();
			if (!suppressErrors)
				app.showError("InvalidInput", str);
		}

		cons.setSuppressLabelCreation(oldMacroMode);
		return func;
	}
	/**
	 * 
	 * @param str input string
	 * @param suppressErrors true to suppress error messages
	 * @return str parsed to multivariate function
	 */
	public GeoFunctionNVar evaluateToFunctionNVar(String str, boolean suppressErrors) {
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		GeoFunctionNVar func = null;
		try {
			ValidExpression ve = parser.parseGeoGebraExpression(str);
			GeoElement[] temp = processValidExpression(ve);

			if (temp[0]instanceof GeoFunctionNVar) {
				func = (GeoFunctionNVar)temp[0];
			} else
				if (temp[0]instanceof GeoFunction) {
					FunctionVariable[] funVars;
					if(((GeoFunction)temp[0]).isFunctionOfY()){
					
					 funVars = new FunctionVariable[]{((GeoFunction)temp[0]).getFunction().getFunctionVariable(),
							new FunctionVariable(kernel,"y")};
					}else{
						funVars = new FunctionVariable[]{new FunctionVariable(kernel,"y"),
								((GeoFunction)temp[0]).getFunction().getFunctionVariable()};					
						}
					
				FunctionNVar fn = new FunctionNVar(((GeoFunction)temp[0]).getFunctionExpression(),
					funVars);
					func = new GeoFunctionNVar(cons,fn);
				} else if(temp[0] instanceof GeoNumeric){
					FunctionVariable[] funVars = new FunctionVariable[]{new FunctionVariable(kernel,"x"),
							new FunctionVariable(kernel,"y")};
					FunctionNVar fn = new FunctionNVar(
							new ExpressionNode(kernel,temp[0]),funVars);
					func = new GeoFunctionNVar(cons,fn);
					
				
					
				}
				if (!suppressErrors)
				app.showError("InvalidInput", str);

		} catch (CircularDefinitionException e) {
			App.debug("CircularDefinition");
			if (!suppressErrors)
				app.showError("CircularDefinition");
		} catch (Exception e) {
			e.printStackTrace();
			if (!suppressErrors)
				app.showError("InvalidInput", str);
		} catch (MyError e) {
			e.printStackTrace();
			if (!suppressErrors)
				app.showError(e);
		} catch (Error e) {
			e.printStackTrace();
			if (!suppressErrors)
				app.showError("InvalidInput", str);
		}

		cons.setSuppressLabelCreation(oldMacroMode);
		return func;
	}

	/**
	 * Parses given String str and tries to evaluate it to a NumberValue Returns
	 * null if something went wrong. Michael Borcherds 2008-08-13
	 * @param str string to parse
	 * @param suppressErrors false to show error messages (only stacktrace otherwise)
	 * @return resulting number
	 */
	public NumberValue evaluateToNumeric(String str, boolean suppressErrors) {
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		NumberValue num = null;
		try {
			ValidExpression ve = parser.parseGeoGebraExpression(str);
			GeoElement[] temp = processValidExpression(ve);
			num = (NumberValue) temp[0];
		} catch (CircularDefinitionException e) {
			App.debug("CircularDefinition");
			if (!suppressErrors)
				app.showError("CircularDefinition");
		} catch (Exception e) {
			e.printStackTrace();
			if (!suppressErrors)
				app.showError("InvalidInput", str);
		} catch (MyError e) {
			e.printStackTrace();
			if (!suppressErrors)
				app.showError(e);
		} catch (Error e) {
			e.printStackTrace();
			if (!suppressErrors)
				app.showError("InvalidInput", str);
		}

		cons.setSuppressLabelCreation(oldMacroMode);
		return num;
	}

	/**
	 * Parses given String str and tries to evaluate it to a GeoPoint. Returns
	 * null if something went wrong.
	 * @param str string to process
	 * @param showErrors true to show error messages (only stacktrace otherwise) 
	 * @param suppressLabels true to suppress labeling
	 * @return resulting point
	 */
	public GeoPointND evaluateToPoint(String str, boolean showErrors, boolean suppressLabels) {
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		if (suppressLabels) {
			cons.setSuppressLabelCreation(true);
		}
		
		GeoPointND p = null;
		GeoElement[] temp = null;
		try {
			ValidExpression ve = parser.parseGeoGebraExpression(str);
			if (ve instanceof ExpressionNode) {
				ExpressionNode en = (ExpressionNode) ve;
				en.setForcePoint();
			}

			temp = processValidExpression(ve);
			p = (GeoPointND) temp[0];
		} catch (CircularDefinitionException e) {
			if (showErrors) {
				App.debug("CircularDefinition");
				app.showError("CircularDefinition");
			}
		} catch (Exception e) {
			if (showErrors) {
				e.printStackTrace();
				app.showError("InvalidInput", str);
			}
		} catch (MyError e) {
			if (showErrors) {
				e.printStackTrace();
				app.showError(e);
			}
		} catch (Error e) {
			if (showErrors) {
				e.printStackTrace();
				app.showError("InvalidInput", str);
			}
		}

		if (suppressLabels) {
			cons.setSuppressLabelCreation(oldMacroMode);
		}
		return p;
	}

	/**
	 * Parses given String str and tries to evaluate it to a GeoText. Returns
	 * null if something went wrong.
	 * @param str input string
	 * @param createLabel true to label result
	 * @param showErrors true to show error messages (only stacktrace otherwise)
	 * @return resulting text
	 */
	public GeoText evaluateToText(String str, boolean createLabel,
			boolean showErrors) {
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(!createLabel);

		GeoText text = null;
		GeoElement[] temp = null;
		try {
			ValidExpression ve = parser.parseGeoGebraExpression(str);
			temp = processValidExpression(ve);
			text = (GeoText) temp[0];
		} catch (CircularDefinitionException e) {
			if (showErrors) {
				App.debug("CircularDefinition");
				app.showError("CircularDefinition");
			}
		} catch (Exception e) {
			if (showErrors) {
				e.printStackTrace();
				app.showError("InvalidInput", str);
			}
		} catch (MyError e) {
			if (showErrors) {
				e.printStackTrace();
				app.showError(e);
			}
		} catch (Error e) {
			if (showErrors) {
				e.printStackTrace();
				app.showError("InvalidInput", str);
			}
		}

		cons.setSuppressLabelCreation(oldMacroMode);
		return text;
	}

	/**
	 * Parses given String str and tries to evaluate it to a GeoImplicitPoly
	 * object. Returns null if something went wrong.
	 * 
	 * @param str stringInput
	 * @param showErrors if false, only stacktraces are printed
	 * @return implicit polygon or null
	 */
	public GeoElement evaluateToGeoElement(String str, boolean showErrors) {
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		GeoElement geo = null;
		try {
			ValidExpression ve = parser.parseGeoGebraExpression(str);
			GeoElement[] temp = processValidExpression(ve);
			geo = temp[0];
		} catch (CircularDefinitionException e) {
			App.debug("CircularDefinition");
			app.showError("CircularDefinition");
		} catch (Exception e) {
			e.printStackTrace();
			if (showErrors)
				app.showError("InvalidInput", str);
		} catch (MyError e) {
			e.printStackTrace();
			if (showErrors)
				app.showError(e);
		} catch (Error e) {
			e.printStackTrace();
			if (showErrors)
				app.showError("InvalidInput", str);
		}

		cons.setSuppressLabelCreation(oldMacroMode);
		return geo;
	}

	/**
	 * Checks if label is valid.
	 * @param label potential label
	 * @return valid label
	 * @throws ParseException if label is invalid
	 */
	public String parseLabel(String label) throws ParseException {
		return parser.parseLabel(label);
	}

	/**
	 * @param ve expression to process
	 * @return resulting elements
	 * @throws MyError e.g. for wrong syntax
	 * @throws Exception e.g. for circular definition
	 */
	public GeoElement[] processValidExpression(ValidExpression ve)
			throws MyError, Exception {
		return processValidExpression(ve, true);
	}

	/**
	 * processes valid expression.
	 * 
	 * @param ve expression to process
	 * @param redefineIndependent
	 *            == true: independent objects are redefined too
	 * @throws MyError e.g. on wrong syntax
	 * @throws Exception e.g. for circular definition
	 * @return resulting geos
	 */
	public GeoElement[] processValidExpression(ValidExpression ve,
			boolean redefineIndependent) throws MyError, Exception {

		// check for existing labels
		String[] labels = ve.getLabels();
		GeoElement replaceable = null;
		if (labels != null && labels.length > 0) {
			boolean firstTime = true;
			for (int i = 0; i < labels.length; i++) {
				GeoElement geo = kernel.lookupLabel(labels[i]);
				if (geo != null) {
					if (geo.isFixed()) {
						String[] strs = { "IllegalAssignment",
								"AssignmentToFixed", ":\n",
								geo.getLongDescription() };
						throw new MyError(app, strs);
					}
					// replace (overwrite or redefine) geo
					if (firstTime) { // only one geo can be replaced
						replaceable = geo;
						firstTime = false;
					}
				}
			}
		}

		GeoElement[] ret;
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		if (replaceable != null)
			cons.setSuppressLabelCreation(true);

		// we have to make sure that the macro mode is
		// set back at the end
		try {
			ret = doProcessValidExpression(ve);

			if (ret == null) { // eg (1,2,3) running in 2D
				App.debug("Unhandled ValidExpression : " + ve);
				throw new MyError(app, app.getError("InvalidInput") + ":\n"
						+ ve);
			}
		} finally {
			cons.setSuppressLabelCreation(oldMacroMode);
		}

		// try to replace replaceable geo by ret[0]
		if (replaceable != null && ret.length > 0) {
			// a changeable replaceable is not redefined:
			// it gets the value of ret[0]
			// (note: texts are always redefined)
			if (!redefineIndependent && replaceable.isChangeable()
					&& !(replaceable.isGeoText())) {
				try {
					replaceable.set(ret[0]);
					replaceable.updateRepaint();
					ret[0] = replaceable;
				} catch (Exception e) {
					String errStr = app.getError("IllegalAssignment") + "\n"
							+ replaceable.getLongDescription() + "     =     "
							+ ret[0].getLongDescription();
					throw new MyError(app, errStr);
				}
			}
			// redefine
			else {
				try {
					// SPECIAL CASE: set value
					// new and old object are both independent and have same
					// type:
					// simply assign value and don't redefine
					if (replaceable.isIndependent()
							&& ret[0].isIndependent()
							&& compatibleTypes(replaceable.getGeoClassType(), ret[0]
									.getGeoClassType())) {
						replaceable.set(ret[0]);
						replaceable.updateRepaint();
						ret[0] = replaceable;
					}

					// STANDARD CASE: REDFINED
					else {
						GeoElement newGeo = ret[0];
						cons.replace(replaceable, newGeo);

						// now all objects have changed
						// get the new object with same label as our result
						String newLabel = newGeo.isLabelSet() ? newGeo
								.getLabelSimple() : replaceable.getLabelSimple();
						ret[0] = kernel.lookupLabel(newLabel, false);
					}
				} catch (CircularDefinitionException e) {
					throw e;
				} catch (Exception e) {
					e.printStackTrace();
					throw new MyError(app, "ReplaceFailed");
				} catch (MyError e) {
					e.printStackTrace();
					throw new MyError(app, "ReplaceFailed");
				}
			}
		}

		return ret;
	}

	private static boolean compatibleTypes(GeoClass type,
			GeoClass type2) {
		if(type2.equals(type))
			return true;
		if(type2.equals(GeoClass.NUMERIC)&&type.equals(GeoClass.ANGLE))
			return true;
		if(type.equals(GeoClass.NUMERIC)&&type2.equals(GeoClass.ANGLE))
			return true;
		return false;
	}
	/**
	 * Processes valid expression
	 * @param ve expression to process
	 * @return array of geos
	 * @throws MyError if syntax error occurs
	 * @throws CircularDefinitionException if circular definition occurs
	 */
	public GeoElement[] doProcessValidExpression(final ValidExpression ve)
			throws MyError, CircularDefinitionException {
		GeoElement[] ret = null;

		if (ve instanceof ExpressionNode) {
			ret = processExpressionNode((ExpressionNode) ve);
			if (ret != null && ret.length > 0
					&& ret[0] instanceof GeoScriptAction) {
				((GeoScriptAction) ret[0]).perform();
				return new GeoElement[] {};
			}else if (ret != null && ret.length > 0 && ret[0] instanceof GeoList){
				int actions =((GeoList)ret[0]).performScriptActions();
				if(actions>0){
					ret[0].remove();
					return new GeoElement[] {};
				}
			}
		}

		// Command
		else if (ve instanceof Command) {
			ret = cmdDispatcher.processCommand((Command) ve, true);
		}

		// Equation in x,y (linear or quadratic are valid): line or conic
		else if (ve instanceof Equation) {
			ret = processEquation((Equation) ve);
		}

		// explicit Function in one variable
		else if (ve instanceof Function) {
			ret = processFunction((Function) ve);
		}

		// explicit Function in multiple variables
		else if (ve instanceof FunctionNVar) {
			ret = processFunctionNVar((FunctionNVar) ve);
		}

		// Parametric Line
		else if (ve instanceof Parametric) {
			ret = processParametric((Parametric) ve);
		}

		// // Assignment: variable
		// else if (ve instanceof Assignment) {
		// ret = processAssignment((Assignment) ve);
		// }

		return ret;
	}

	/**
	 * Wraps given function into GeoFunction, if dependent,
	 * AlgoDependentFunction is created. 
	 * @param fun function
	 * @return GeoFunction
	 */
	public final GeoElement[] processFunction(Function fun) {
		fun.initFunction();

		String label = fun.getLabel();
		GeoFunction f;
		GeoElement[] ret = new GeoElement[1];

		GeoElement[] vars = fun.getGeoElementVariables();
		boolean isIndependent = (vars == null || vars.length == 0);

		// check for interval

		ExpressionNode en = fun.getExpression();
		if (en.getOperation().equals(Operation.AND)) {
			ExpressionValue left = en.getLeft();
			ExpressionValue right = en.getRight();

			if (left.isExpressionNode() && right.isExpressionNode()) {
				ExpressionNode enLeft = (ExpressionNode) left;
				ExpressionNode enRight = (ExpressionNode) right;

				Operation opLeft = enLeft.getOperation();
				Operation opRight = enRight.getOperation();

				ExpressionValue leftLeft = enLeft.getLeft();
				ExpressionValue leftRight = enLeft.getRight();
				ExpressionValue rightLeft = enRight.getLeft();
				ExpressionValue rightRight = enRight.getRight();

				// directions of inequalities, need one + and one - for an
				// interval
				int leftDir = 0;
				int rightDir = 0;

				if ((opLeft.equals(Operation.LESS) || opLeft
						.equals(Operation.LESS_EQUAL))) {
					if (leftLeft instanceof FunctionVariable
							&& leftRight.isNumberValue())
						leftDir = -1;
					else if (leftRight instanceof FunctionVariable
							&& leftLeft.isNumberValue())
						leftDir = +1;

				} else if ((opLeft.equals(Operation.GREATER) || opLeft
						.equals(Operation.GREATER_EQUAL))) {
					if (leftLeft instanceof FunctionVariable
							&& leftRight.isNumberValue())
						leftDir = +1;
					else if (leftRight instanceof FunctionVariable
							&& leftLeft.isNumberValue())
						leftDir = -1;

				}

				if ((opRight.equals(Operation.LESS) || opRight
						.equals(Operation.LESS_EQUAL))) {
					if (rightLeft instanceof FunctionVariable
							&& rightRight.isNumberValue())
						rightDir = -1;
					else if (rightRight instanceof FunctionVariable
							&& rightLeft.isNumberValue())
						rightDir = +1;

				} else if ((opRight.equals(Operation.GREATER) || opRight
						.equals(Operation.GREATER_EQUAL))) {
					if (rightLeft instanceof FunctionVariable
							&& rightRight.isNumberValue())
						rightDir = +1;
					else if (rightRight instanceof FunctionVariable
							&& rightLeft.isNumberValue())
						rightDir = -1;

				}

				// AbstractApplication.debug(leftDir+" "+rightDir);
				// AbstractApplication.debug(leftLeft.getClass()+" "+leftRight.getClass());
				// AbstractApplication.debug(rightLeft.getClass()+" "+rightRight.getClass());

				// opposite directions -> OK
				if (leftDir * rightDir < 0) {
					if (isIndependent) {
						f = new GeoInterval(cons, label, fun);
					} else {
						f = kernel.DependentInterval(label, fun);
					}
					ret[0] = f;
					return ret;

				}

				// AbstractApplication.debug(enLeft.operation+"");
				// AbstractApplication.debug(enLeft.left.getClass()+"");
				// AbstractApplication.debug(enLeft.right.getClass()+"");

			}
			// AbstractApplication.debug(left.getClass()+"");
			// AbstractApplication.debug(right.getClass()+"");
			// AbstractApplication.debug("");
		} else if (en.getOperation().equals(Operation.FUNCTION)) {
			ExpressionValue left = en.getLeft();
			ExpressionValue right = en.getRight();
			//the isConstant() here makes difference between f(1) and f(x), see #2155
			if (left.isLeaf() && left.isGeoElement() && right.isLeaf()
					&& right.isNumberValue() && !right.isConstant() && !isIndependent) {
				f = (GeoFunction) kernel.DependentGeoCopy(label,
						((GeoFunctionable)left).getGeoFunction());
				ret[0] = f;
				return ret;
			}
		}

		if (isIndependent) {
			f = new GeoFunction(cons, label, fun);
		} else {
			f = kernel.DependentFunction(label, fun);
		}
		ret[0] = f;
		return ret;
	}

	/**
	 * Wraps given functionNVar into GeoFunctionNVar, if dependent,
	 * AlgoDependentFunctionNVar is created. 
	 * @param fun function
	 * @return GeoFunctionNVar
	 */
	public GeoElement[] processFunctionNVar(FunctionNVar fun) {
		fun.initFunction();

		String label = fun.getLabel();
		GeoElement[] ret = new GeoElement[1];

		GeoElement[] vars = fun.getGeoElementVariables();
		boolean isIndependent = (vars == null || vars.length == 0);

		if (isIndependent) {
			ret[0] = new GeoFunctionNVar(cons, label, fun);
		} else {
			ret[0] = kernel.DependentFunctionNVar(label, fun);
		}
		return ret;
	}

	/**
	 * @param equ equation
	 * @return line, conic, implicit poly or plane
	 * @throws MyError e.g. for invalid operation
	 */
	public GeoElement[] processEquation(Equation equ) throws MyError {
		// AbstractApplication.debug("EQUATION: " + equ);
		// AbstractApplication.debug("NORMALFORM POLYNOMIAL: " +
		// equ.getNormalForm());

		try {
			equ.initEquation();
			// AbstractApplication.debug("EQUATION: " + equ.getNormalForm());
			// check no terms in z
			checkNoTermsInZ(equ);

			if (equ.isFunctionDependent()) {
				return processImplicitPoly(equ);
			}

			// consider algebraic degree of equation
			// check not equation of eg plane
			switch (equ.degree()) {
			// linear equation -> LINE
			case 1:
				return processLine(equ);

				// quadratic equation -> CONIC
			case 2:
				return processConic(equ);

			default:
				// test for "y= <rhs>" here as well
				if (equ.getLHS().toString(StringTemplate.defaultTemplate).trim().equals("y")) {
					PolyReplacer rep = PolyReplacer.getReplacer();
					Function fun = new Function(equ.getRHS().traverse(rep).wrap());
					// try to use label of equation					
					fun.setLabel(equ.getLabel());
					return processFunction(fun);
				}
				return processImplicitPoly(equ);
			}
		} catch (MyError eqnError) {
			eqnError.printStackTrace();

			// invalid equation: maybe a function of form "y = <rhs>"?
			String lhsStr = equ.getLHS().toString(StringTemplate.defaultTemplate).trim();
			if (lhsStr.equals("y")) {
				try {
					// try to create function from right hand side
					PolyReplacer rep = PolyReplacer.getReplacer();
					Function fun = new Function(equ.getRHS().traverse(rep).wrap());

					// try to use label of equation
					fun.setLabel(equ.getLabel());
					return processFunction(fun);
				} catch (MyError funError) {
					funError.printStackTrace();
				}
			}

			// throw invalid equation error if we get here
			if (eqnError.getMessage() == "InvalidEquation") {
				throw eqnError;
			}
			String[] errors = { "InvalidEquation",
					eqnError.getLocalizedMessage() };
			throw new MyError(app, errors);
		}
	}

	/**
	 * @param equ equation
	 * @throws MyError if equation contains terms in Z
	 */
	protected void checkNoTermsInZ(Equation equ) throws MyError{
		if (!equ.getNormalForm().isFreeOf('z'))
			throw new MyError(app, "InvalidEquation");
	}

	/**
	 * @param equ equation
	 * @return resulting line
	 */
	protected GeoElement[] processLine(Equation equ) {
		double a = 0, b = 0, c = 0;
		GeoLine line;
		GeoElement[] ret = new GeoElement[1];
		String label = equ.getLabel();
		Polynomial lhs = equ.getNormalForm();
		boolean isExplicit = equ.isExplicit("y");
		boolean isIndependent = lhs.isConstant();

		if (isIndependent) {
			// get coefficients
			a = lhs.getCoeffValue("x");
			b = lhs.getCoeffValue("y");
			c = lhs.getCoeffValue("");
			line = new GeoLine(cons, label, a, b, c);
		} else
			line = kernel.DependentLine(label, equ);

		if (isExplicit) {
			line.setToExplicit();
			line.updateRepaint();
		}
		ret[0] = line;
		return ret;
	}

	/**
	 * @param equ equation
	 * @return resulting conic
	 */
	protected GeoElement[] processConic(Equation equ) {
		double a = 0, b = 0, c = 0, d = 0, e = 0, f = 0;
		GeoElement[] ret = new GeoElement[1];
		GeoConic conic;
		String label = equ.getLabel();
		Polynomial lhs = equ.getNormalForm();

		boolean isExplicit = equ.isExplicit("y");
		boolean isSpecific = !isExplicit
				&& (equ.isExplicit("yy") || equ.isExplicit("xx"));
		boolean isIndependent = lhs.isConstant();

		if (isIndependent) {
			a = lhs.getCoeffValue("xx");
			b = lhs.getCoeffValue("xy");
			c = lhs.getCoeffValue("yy");
			d = lhs.getCoeffValue("x");
			e = lhs.getCoeffValue("y");
			f = lhs.getCoeffValue("");
			
			double[] coeffs = { a, b, c, d, e, f };
			conic = new GeoConic(cons, label, coeffs);
		} else
			conic = kernel.DependentConic(label, equ);
		if (isExplicit) {
			conic.setToExplicit();
			conic.updateRepaint();
		} else if (isSpecific
				|| conic.getType() == GeoConicNDConstants.CONIC_CIRCLE) {
			conic.setToSpecific();
			conic.updateRepaint();
		}
		ret[0] = conic;
		return ret;
	}

	/**
	 * @param equ equation
	 * @return resulting implicit polynomial
	 */
	protected GeoElement[] processImplicitPoly(Equation equ) {
		GeoElement[] ret = new GeoElement[1];
		String label = equ.getLabel();
		Polynomial lhs = equ.getNormalForm();
		boolean isIndependent = !equ.isFunctionDependent() && lhs.isConstant();
		GeoImplicitPoly poly;
		GeoElement geo = null;
		if (isIndependent) {
			poly = new GeoImplicitPoly(cons, label, lhs);
			poly.setUserInput(equ);
			geo = poly;
		} else {
			AlgoDependentImplicitPoly algo = new AlgoDependentImplicitPoly(cons,
					label, equ);

			geo = algo.getGeo(); // might also return
															// Line or Conic
			if (geo instanceof GeoUserInputElement) {
				((GeoUserInputElement) geo).setUserInput(equ);
			}
		}
		ret[0] = geo;
		// AbstractApplication.debug("User Input: "+equ);
		ret[0].updateRepaint();
		return ret;
	}

	private GeoElement[] processParametric(Parametric par)
			throws MyError {

		/*
		 * ExpressionValue temp = P.evaluate(); if (!temp.isVectorValue()) {
		 * String [] str = { "VectorExpected", temp.toString() }; throw new
		 * MyParseError(kernel.getApplication(), str); }
		 * 
		 * v.resolveVariables(); temp = v.evaluate(); if (!(temp instanceof
		 * VectorValue)) { String [] str = { "VectorExpected", temp.toString()
		 * }; throw new MyParseError(kernel.getApplication(), str); }
		 */

		// point and vector are created silently
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		// get point
		ExpressionNode node = par.getP();
		node.setForcePoint();
		GeoElement[] temp = processExpressionNode(node);
		GeoPoint P = (GeoPoint) temp[0];

		// get vector
		node = par.getv();
		node.setForceVector();
		temp = processExpressionNode(node);
		GeoVector v = (GeoVector) temp[0];

		// switch back to old mode
		cons.setSuppressLabelCreation(oldMacroMode);

		// Line through P with direction v
		GeoLine line;
		// independent line
		if (P.isConstant() && v.isConstant()) {
			line = new GeoLine(cons);
			line.setCoords(-v.y, v.x, v.y * P.inhomX - v.x * P.inhomY);
		}
		// dependent line
		else {
			line = kernel.Line(par.getLabel(), P, v);
		}
		line.setToParametric(par.getParameter());
		line.updateRepaint();
		GeoElement[] ret = { line };
		return ret;
	}

	/**
	 * @param node expression
	 * @return resulting geos
	 * @throws MyError on invalid operation
	 */
	public GeoElement[] processExpressionNode(ExpressionNode node) throws MyError {
		ExpressionNode n = node;
		// command is leaf: process command
		if (n.isLeaf()) {
			ExpressionValue leaf = n.getLeft();
			if (leaf instanceof Command) {
				Command c = (Command) leaf;
				c.setLabels(n.getLabels());
				return cmdDispatcher.processCommand(c, true);
			} else if (leaf instanceof Equation) {
				Equation eqn = (Equation) leaf;
				eqn.setLabels(n.getLabels());
				return processEquation(eqn);
			} else if (leaf instanceof Function) {
				Function fun = (Function) leaf;
				fun.setLabels(n.getLabels());
				return processFunction(fun);
			} else if (leaf instanceof FunctionNVar) {
				FunctionNVar fun = (FunctionNVar) leaf;
				fun.setLabels(n.getLabels());
				return processFunctionNVar(fun);
			}

		}
		ExpressionValue eval; // ggb3D : used by AlgebraProcessor3D in
		// extended processExpressionNode


		// ELSE: resolve variables and evaluate expressionnode
		n.resolveVariables(false);
		if(n.containsFunctionVariable()){
			Set<String> fvSet = new TreeSet<String>();
			FVarCollector fvc = FVarCollector.getCollector(fvSet);
			n.traverse(fvc);
			if(fvSet.size()==1){
				n= new ExpressionNode(kernel,new Function(n,new FunctionVariable(kernel,fvSet.iterator().next())));
			}else{
				FunctionVariable[] fvArray= new FunctionVariable[fvSet.size()];
				Iterator<String> it = fvSet.iterator();
				int i=0;
				while(it.hasNext()){
					fvArray[i++]=new FunctionVariable(kernel,it.next());
				}
				n= new ExpressionNode(kernel,new FunctionNVar(n,fvArray));
			}
		}
		eval = n.evaluate(StringTemplate.defaultTemplate);
		boolean dollarLabelFound = false;

		ExpressionNode myNode = n;
		if (myNode.isLeaf())
			myNode = myNode.getLeftTree();
		// leaf (no new label specified): just return the existing GeoElement
		if (eval.isGeoElement() && n.getLabel() == null
				&& !(n.getOperation().equals(Operation.ELEMENT_OF))) {
			// take care of spreadsheet $ names: don't loose the wrapper
			// ExpressionNode here
			// check if we have a Variable
			switch (myNode.getOperation()) {
			case $VAR_COL:
			case $VAR_ROW:
			case $VAR_ROW_COL:
				// don't do anything here: we need to keep the wrapper
				// ExpressionNode
				// and must not return the GeoElement here
				dollarLabelFound = true;
				break;

			default:
				// return the GeoElement
				GeoElement[] ret = { (GeoElement) eval };
				return ret;
			}
		}

		if (eval.isBooleanValue())
			return processBoolean(n, eval);
		else if (eval.isNumberValue())
			return processNumber(n, eval);
		else if (eval.isVectorValue())
			return processPointVector(n, eval);
		else if (eval.isVector3DValue())
			return processPointVector3D(n, eval);
		else if (eval.isTextValue())
			return processText(n, eval);
		else if (eval instanceof MyList) {
			return processList(n, (MyList) eval);
		} else if (eval instanceof Function) {
			return processFunction((Function) eval);
		} else if (eval instanceof FunctionNVar) {

			return processFunctionNVar((FunctionNVar) eval);
		}
		// we have to process list in case list=matrix1(1), but not when
		// list=list2
		else if (eval instanceof GeoList && myNode.hasOperations()) {
			App.debug("should work");
			return processList(n, ((GeoList) eval).getMyList());
		} else if (eval.isGeoElement()) {

			// e.g. B1 = A1 where A1 is a GeoElement and B1 does not exist yet
			// create a copy of A1
			if (n.getLabel() != null || dollarLabelFound) {
				return processGeoCopy(n.getLabel(), n);
			}
		}

		// REMOVED due to issue 131:
		// http://code.google.com/p/geogebra/issues/detail?id=131
		// // expressions like 2 a (where a:x + y = 1)
		// // A1=b doesn't work for these objects
		// else if (eval instanceof GeoLine) {
		// if (((GeoLine)eval).getParentAlgorithm() instanceof
		// AlgoDependentLine) {
		// GeoElement[] ret = {(GeoElement) eval };
		// return ret;
		// }
		//
		// }

		// if we get here, nothing worked
		App.debug("Unhandled ExpressionNode: " + eval + ", "
				+ eval.getClass());
		return null;
	}

	private GeoElement[] processNumber(ExpressionNode n,
			ExpressionValue evaluate) {
		GeoElement[] ret = new GeoElement[1];
		String label = n.getLabel();
		boolean isIndependent = n.isConstant();
		MyDouble val = ((NumberValue) evaluate).getNumber();
		boolean isAngle = val.isAngle();
		double value = val.getDouble();

		if (isIndependent) {
			if (isAngle)
				ret[0] = new GeoAngle(cons, label, value);
			else
				ret[0] = new GeoNumeric(cons, label, value);
		} else {
			ret[0] = kernel.DependentNumber(label, n, isAngle);
		}

		if (n.isForcedFunction()) {
			ret[0] = ((GeoFunctionable) (ret[0])).getGeoFunction();
		}

		return ret;
	}

	private GeoElement[] processList(ExpressionNode n, MyList evalList) {
		String label = n.getLabel();

		GeoElement[] ret = new GeoElement[1];

		// no operations or no variables are present, e.g.
		// { a, b, 7 } or { 2, 3, 5 } + {1, 2, 4}
		if (!n.hasOperations() || n.isConstant()) {

			// PROCESS list items to generate a list of geoElements
			ArrayList<GeoElement> geoElements = new ArrayList<GeoElement>();
			boolean isIndependent = true;

			// make sure we don't create any labels for the list elements
			boolean oldMacroMode = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);

			int size = evalList.size();
			for (int i = 0; i < size; i++) {
				ExpressionNode en = (ExpressionNode) evalList.getListElement(i);
				// we only take one resulting object
				GeoElement[] results = processExpressionNode(en);
				GeoElement geo = results[0];

				// add to list
				geoElements.add(geo);
				if (geo.isLabelSet() || !geo.isIndependent())
					isIndependent = false;
			}
			cons.setSuppressLabelCreation(oldMacroMode);

			// Create GeoList object
			ret[0] = kernel.List(label, geoElements, isIndependent);
		}

		// operations and variables are present
		// e.g. {3, 2, 1} + {a, b, 2}
		else {
			ret[0] = kernel.ListExpression(label, n);
		}

		return ret;
	}

	private GeoElement[] processText(ExpressionNode n, ExpressionValue evaluate) {
		GeoElement[] ret = new GeoElement[1];
		String label = n.getLabel();

		boolean isIndependent = n.isConstant();

		if (isIndependent) {
			MyStringBuffer val = ((TextValue) evaluate).getText();
			ret[0] = Text(label, val.toValueString(StringTemplate.defaultTemplate));
		} else
			ret[0] = kernel.DependentText(label, n);
		return ret;
	}

	final public GeoText Text(String label, String text) {
		GeoText t = new GeoText(cons);
		t.setTextString(text);
		t.setLabel(label);
		return t;
	}

	private GeoElement[] processBoolean(ExpressionNode n,
			ExpressionValue evaluate) {
		GeoElement[] ret = new GeoElement[1];
		String label = n.getLabel();

		boolean isIndependent = n.isConstant();

		if (isIndependent) {
			
			ret[0] = new GeoBoolean(cons);
			((GeoBoolean)ret[0]).setValue(((BooleanValue) evaluate).getBoolean());
			ret[0].setLabel(label);

		} else {
			ret[0] = (new AlgoDependentBoolean(cons, label, n)).getGeoBoolean();
		}
		return ret;
	}

	private GeoElement[] processPointVector(ExpressionNode n,
			ExpressionValue evaluate) {
		String label = n.getLabel();

		GeoVec2D p = ((VectorValue) evaluate).getVector();

		boolean polar = p.getMode() == Kernel.COORD_POLAR;

		// we want z = 3 + i to give a (complex) GeoPoint not a GeoVector
		boolean complex = p.getMode() == Kernel.COORD_COMPLEX;

		GeoVec3D[] ret = new GeoVec3D[1];
		boolean isIndependent = n.isConstant();

		// make point if complex parts are present, e.g. 3 + i
		if (complex) {
			n.setForcePoint();
		}
		// make vector, if label begins with lowercase character
		else if (label != null) {
			if (!(n.isForcedPoint() || n.isForcedVector())) { // may be set by
																// MyXMLHandler
				if (Character.isLowerCase(label.charAt(0)))
					n.setForceVector();
				else
					n.setForcePoint();
			}
		}
		boolean isVector = n.isVectorValue();

		if (isIndependent) {
			// get coords
			double x = p.getX();
			double y = p.getY();
			if (isVector)
				ret[0] = kernel.Vector(label, x, y);
			else
				ret[0] = kernel.Point(label, x, y, complex);
		} else {
			if (isVector)
				ret[0] = kernel.DependentVector(label, n);
			else
				ret[0] = kernel.DependentPoint(label, n, complex);
		}
		if (polar) {
			ret[0].setMode(Kernel.COORD_POLAR);
			ret[0].updateRepaint();
		} else if (complex) {
			ret[0].setMode(Kernel.COORD_COMPLEX);
			ret[0].updateRepaint();
		}
		return ret;
	}

	/**
	 * empty method in 2D : see AlgebraProcessor3D to see implementation in 3D
	 * 
	 * @param n 3D point expression
	 * @param evaluate evaluated node n
	 * @return null
	 */
	protected GeoElement[] processPointVector3D(ExpressionNode n,
			ExpressionValue evaluate) {

		return null;
	}

	/**
	 * Creates a dependent copy of origGeo with label
	 */
	private GeoElement[] processGeoCopy(String copyLabel,
			ExpressionNode origGeoNode) {
		GeoElement[] ret = new GeoElement[1];
		ret[0] = kernel.DependentGeoCopy(copyLabel, origGeoNode);
		return ret;
	}

	/** Enables CAS specific behaviour */
	public void enableCAS() {
		cmdDispatcher.initCASCommands();
	}

	// /**
	// * Processes assignments, i.e. input of the form leftVar = geoRight where
	// geoRight is an existing GeoElement.
	// */
	// private GeoElement[] processAssignment(String leftVar, GeoElement
	// geoRight) throws MyError {
	// GeoElement[] ret = new GeoElement[1];
	//
	// // don't allow copying of dependent functions
	//
	// /*
	// if (
	// geoRight instanceof GeoFunction && !geoRight.isIndependent()) {
	// String[] str = { "IllegalAssignment", rightVar };
	// throw new MyError(app, str);
	// }
	// */
	//
	//
	// GeoElement geoLeft = cons.lookupLabel(leftVar, false);
	// if (geoLeft == null) { // create kernel object and copy values
	// geoLeft = geoRight.copy();
	// geoLeft.setLabel(leftVar);
	// ret[0] = geoLeft;
	// } else { // overwrite
	// ret[0] = geoRight;
	// }
	//
	//
	// if (ret[0] != null && !ret[0].isLabelSet()) {
	// ret[0].setLabel(null);
	// }
	//
	// return ret;
	// }

}
