package geogebra.kernel.commands;

import geogebra.kernel.AlgoElement;
import geogebra.kernel.CircularDefinitionException;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoAngle;
import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoCasCell;
import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoFunctionable;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoText;
import geogebra.kernel.GeoUserInputElement;
import geogebra.kernel.GeoVec2D;
import geogebra.kernel.GeoVec3D;
import geogebra.kernel.GeoVector;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.BooleanValue;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.Equation;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.FunctionNVar;
import geogebra.kernel.arithmetic.FunctionVariable;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.MyList;
import geogebra.kernel.arithmetic.MyStringBuffer;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.arithmetic.Parametric;
import geogebra.kernel.arithmetic.Polynomial;
import geogebra.kernel.arithmetic.TextValue;
import geogebra.kernel.arithmetic.ValidExpression;
import geogebra.kernel.arithmetic.VectorValue;
import geogebra.kernel.cas.AlgoDependentCasCell;
import geogebra.kernel.implicit.GeoImplicitPoly;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.parser.ParseException;
import geogebra.kernel.parser.Parser;
import geogebra.main.Application;
import geogebra.main.MyError;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

public class AlgebraProcessor {
	
	protected Kernel kernel;
	private Construction cons;
	protected Application app;
	private Parser parser;
	protected CommandDispatcher cmdDispatcher;
	
	protected ExpressionValue eval; //ggb3D : used by AlgebraProcessor3D in extended processExpressionNode
	
	public AlgebraProcessor(Kernel kernel) {
		this.kernel = kernel;
		cons = kernel.getConstruction();
		
		cmdDispatcher = newCommandDispatcher(kernel);
		app = kernel.getApplication();
		parser = kernel.getParser();
	}
	
	/**
	 * @param kernel 
	 * @return a new command dispatcher (used for 3D)
	 */
	protected CommandDispatcher newCommandDispatcher(Kernel kernel){
		return new CommandDispatcher(kernel);
	}
	
	
	
	public Set getPublicCommandSet() {
		return cmdDispatcher.getPublicCommandSet();
	}
	
	/**
	 * Returns an array of public command sets. Each set is a categorized subset
	 * of the command table.
	 */
	public Set[] getPublicCommandSubSets() {
		return cmdDispatcher.getPublicCommandSubSets();
	}
	
	/**
	 * Returns the localized name of a command subset.
	 * Indices are defined in CommandDispatcher.
	 */
	public String getSubCommandSetName(int index){
		return cmdDispatcher.getSubCommandSetName(index);
	}
	
	/**
	 * Returns whether the given command name is supported in GeoGebra.
	 */
	public boolean isCommandAvailable(String cmd) {
		return cmdDispatcher.isCommandAvailable(cmd);
	}
	

	final public GeoElement[] processCommand(Command c, boolean labelOutput) throws MyError {
		return cmdDispatcher.processCommand(c, labelOutput);
	}
	
	/**
	 * Processes the given casCell, i.e. compute its output depending on 
	 * its input. Note that this may create an additional twin GeoElement.	 
	 */
	final public void processCasCell(GeoCasCell casCell) throws MyError {
		// TODO: remove
		System.out.println("*** processCasCell: " + casCell);
		
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
				// free -> free, e.g. m := 7  ->  m := 8
				cons.addToConstructionList(casCell, true);	
				casCell.computeOutput(); // create twinGeo if necessary			    	        
				casCell.setLabelOfTwinGeo();
				needsRedefinition = false;
			}
			else {
				// free -> dependent, e.g. m := 7  ->  m := c+2	
				if (casCell.isOutputEmpty() && !casCell.hasChildren()) {
					// this is a new casCell
					cons.removeFromConstructionList(casCell);	
					kernel.DependentCasCell(casCell);
					needsRedefinition = false;
				} else {
					// existing casCell with possible twinGeo
					needsRedefinition = true;
				}
			}			
		} else {
			if (nowFree) {
				// dependent -> free, e.g. m := c+2  ->  m := 7
				// algorithm will be removed through redefinition 				
				needsRedefinition = true;
			}
			else {
				// dependent -> dependent, e.g. m := c+2  ->  m := c+d	
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
			} catch (Exception e) {
				casCell.setError("RedefinitionFailed");
				//app.showError(e.getMessage());				
			}	
		} else {
			casCell.updateCascade();
		}
	}
	
	/**
	 * for AlgebraView changes in the tree selection and redefine dialog
	 * @return changed geo
	 */
	public GeoElement changeGeoElement(
			GeoElement geo,
			String newValue,
			boolean redefineIndependent, boolean storeUndoInfo) {
						
			try {
				return changeGeoElementNoExceptionHandling(geo, newValue, redefineIndependent, storeUndoInfo);
			} catch (Exception e) {
				app.showError(e.getMessage());
				return null;
			}						
	}	
	
	/**
	 * for AlgebraView changes in the tree selection and redefine dialog
	 * @return changed geo
	 */
	public GeoElement changeGeoElement(
			GeoElement geo,
			ValidExpression newValueVE,
			boolean redefineIndependent, boolean storeUndoInfo) {
						
			try {
				return changeGeoElementNoExceptionHandling(geo, newValueVE, redefineIndependent, storeUndoInfo);
			} catch (Exception e) {
				app.showError(e.getMessage());
				return null;
			}						
	}	
	
	/**
	 * for AlgebraView changes in the tree selection and redefine dialog
	 * @return changed geo
	 */
	public GeoElement changeGeoElementNoExceptionHandling(GeoElement geo, String newValue, boolean redefineIndependent, boolean storeUndoInfo) 
	throws Exception {
		
		try {
			ValidExpression ve = parser.parseGeoGebraExpression(newValue);
			return changeGeoElementNoExceptionHandling(geo, ve, redefineIndependent, storeUndoInfo);
		} 
		catch (Exception e) {
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
	 * @return changed geo
	 */
	public GeoElement changeGeoElementNoExceptionHandling(GeoElement geo, ValidExpression newValue, boolean redefineIndependent, boolean storeUndoInfo) 
	throws Exception {
		String oldLabel, newLabel;
		GeoElement[] result;

		try {
			oldLabel = geo.getLabel();
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
			Application.debug("CircularDefinition");
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
		}
	}
	
	/*
	 * methods for processing an input string
	 */
	// returns non-null GeoElement array when successful
	public GeoElement[] processAlgebraCommand(String cmd, boolean storeUndo) {
		
		try {
			return processAlgebraCommandNoExceptionHandling(cmd, storeUndo,true,false);
		} catch (Exception e) {
			e.printStackTrace();
			app.showError(e.getMessage());
			return null;
		}	
	}
	
	
	// G.Sturr 2010-7-5
	// normal usage ... default to show error dialog
	public GeoElement[] processAlgebraCommandNoExceptions(String cmd,
			boolean storeUndo) {

		try {
			return processAlgebraCommandNoExceptionHandling(cmd, storeUndo,
					true, false);
		} catch (Exception e) {
			return null;
		}
	}
	
	public GeoElement[] processAlgebraCommandNoExceptionsOrErrors(String cmd,
			boolean storeUndo) {

		try {
			return processAlgebraCommandNoExceptionHandling(cmd, storeUndo,
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
	public GeoElement[] processAlgebraCommandNoExceptionHandling(
			String cmd, boolean storeUndo, boolean allowErrorDialog, boolean throwMyError) 
	throws Exception {
		ValidExpression ve;					
		
		try {
			ve = parser.parseGeoGebraExpression(cmd);
		} catch (ParseException e) {
			//e.printStackTrace();
			if (allowErrorDialog) {app.showError(app.getError("InvalidInput") + ":\n" + cmd);return null;}
			throw new MyException(app.getError("InvalidInput") + ":\n" + cmd, MyException.INVALID_INPUT);						
		} catch (MyError e) {
			//e.printStackTrace();
			if (allowErrorDialog) {app.showError(e.getLocalizedMessage());return null;}
			throw new Exception(e.getLocalizedMessage());
		} catch (Error e) {
			//e.printStackTrace();
			if (allowErrorDialog) {app.showError(app.getError("InvalidInput") + ":\n" + cmd);return null;}
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
			//throw new Exception(e.getLocalizedMessage());
			
			// show error with nice "Show Online Help" box
			if(allowErrorDialog) {// G.Sturr 2010-7-5
				app.showError(e);
				e.printStackTrace();
			}
			else if (throwMyError) throw new MyError(app, e.getLocalizedMessage(), e.getcommandName());

			return null;
		} catch (CircularDefinitionException e) {
			Application.debug("CircularDefinition");
			throw e;
		} catch (Exception ex) {
			Application.debug("Exception");
			ex.printStackTrace();
			throw new Exception(app.getError("Error") + ":\n" + ex.getLocalizedMessage());
		}
		return geoElements;
	}

	/**
	 * Parses given String str and tries to evaluate it to a double.
	 * Returns Double.NaN if something went wrong.
	 */
	public double evaluateToDouble(String str) {
		try {
			ValidExpression ve = parser.parseExpression(str);
			ExpressionNode en = (ExpressionNode) ve;
			en.resolveVariables();
			NumberValue nv = (NumberValue) en.evaluate();
			return nv.getDouble();
		} catch (Exception e) {
			e.printStackTrace();
			app.showError("InvalidInput", str);
			return Double.NaN;
		} catch (MyError e) {
			e.printStackTrace();
			app.showError(e);
			return Double.NaN;
		} catch (Error e) {
			e.printStackTrace();
			app.showError("InvalidInput", str);
			return Double.NaN;
		}
	}
	
	/**
	 * Parses given String str and tries to evaluate it to a GeoBoolean object.
	 * Returns null if something went wrong.
	 */
	public GeoBoolean evaluateToBoolean(String str) {
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		GeoBoolean bool = null;
		try {
			ValidExpression ve = parser.parseGeoGebraExpression(str);		
			GeoElement [] temp = processValidExpression(ve);
			bool = (GeoBoolean) temp[0];
		} catch (CircularDefinitionException e) {
			Application.debug("CircularDefinition");
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
	 * Returns null if something went wrong.
	 * Michael Borcherds 2008-04-02
	 */
	public GeoList evaluateToList(String str) {
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		GeoList list = null;
		try {
			ValidExpression ve = parser.parseGeoGebraExpression(str);		
			GeoElement [] temp = processValidExpression(ve);
			list = (GeoList) temp[0];
		} catch (CircularDefinitionException e) {
			Application.debug("CircularDefinition");
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
		return list;
	}

	/**
	 * Parses given String str and tries to evaluate it to a GeoFunction
	 * Returns null if something went wrong.
	 * Michael Borcherds 2008-04-04
	 */
	public GeoFunction evaluateToFunction(String str, boolean suppressErrors) {
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		GeoFunction func = null;
		try {
			ValidExpression ve = parser.parseGeoGebraExpression(str);		
			GeoElement [] temp = processValidExpression(ve);
			
			if (temp[0].isGeoFunctionable()) {
				GeoFunctionable f = (GeoFunctionable) temp[0];
				func = f.getGeoFunction();
			}						
			else 
				if (!suppressErrors) app.showError("InvalidInput", str);
			
		} catch (CircularDefinitionException e) {
			Application.debug("CircularDefinition");
			if (!suppressErrors) app.showError("CircularDefinition");
		} catch (Exception e) {		
			e.printStackTrace();
			if (!suppressErrors) app.showError("InvalidInput", str);
		} catch (MyError e) {
			e.printStackTrace();
			if (!suppressErrors) app.showError(e);
		} catch (Error e) {
			e.printStackTrace();
			if (!suppressErrors) app.showError("InvalidInput", str);
		} 
		
		cons.setSuppressLabelCreation(oldMacroMode);
		return func;
	}

	/**
	 * Parses given String str and tries to evaluate it to a NumberValue
	 * Returns null if something went wrong.
	 * Michael Borcherds 2008-08-13
	 */
	public NumberValue evaluateToNumeric(String str, boolean suppressErrors) {
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		NumberValue num = null;
		try {
			ValidExpression ve = parser.parseGeoGebraExpression(str);		
			GeoElement [] temp = processValidExpression(ve);
			num = (NumberValue) temp[0];
		} catch (CircularDefinitionException e) {
			Application.debug("CircularDefinition");
			if (!suppressErrors) app.showError("CircularDefinition");
		} catch (Exception e) {		
			e.printStackTrace();
			if (!suppressErrors) app.showError("InvalidInput", str);
		} catch (MyError e) {
			e.printStackTrace();
			if (!suppressErrors) app.showError(e);
		} catch (Error e) {
			e.printStackTrace();
			if (!suppressErrors) app.showError("InvalidInput", str);
		} 
		
		cons.setSuppressLabelCreation(oldMacroMode);
		return num;
	}

	/**
	 * Parses given String str and tries to evaluate it to a GeoPoint.
	 * Returns null if something went wrong.
	 */
	public GeoPointND evaluateToPoint(String str) {
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		GeoPointND p = null;
		GeoElement [] temp = null;;
		try {
			ValidExpression ve = parser.parseGeoGebraExpression(str);
			if (ve instanceof ExpressionNode) {
				ExpressionNode en = (ExpressionNode) ve;
				en.setForcePoint();	
			}
			 
			 temp = processValidExpression(ve);
			 p = (GeoPointND) temp[0];
		} catch (CircularDefinitionException e) {
			Application.debug("CircularDefinition");
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
		return p;
	}
	
	/**
	 * Parses given String str and tries to evaluate it to a GeoText.
	 * Returns null if something went wrong.
	 */
	public GeoText evaluateToText(String str, boolean createLabel) {
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(!createLabel);

		GeoText text = null;
		GeoElement [] temp = null;;
		try {
			ValidExpression ve = parser.parseGeoGebraExpression(str);			
			temp = processValidExpression(ve);
			text = (GeoText) temp[0];
		} catch (CircularDefinitionException e) {
			Application.debug("CircularDefinition");
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
		return text;
	}

	/**
	 * Parses given String str and tries to evaluate it to a GeoImplicitPoly object.
	 * Returns null if something went wrong.
	 * @param str 
	 * @boolean showErrors if false, only stacktraces are printed
	 * @return implicit polygon or null
	 */
	public GeoElement evaluateToGeoElement(String str,boolean showErrors) {
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		GeoElement geo = null;
		try {
			ValidExpression ve = parser.parseGeoGebraExpression(str);		
			GeoElement [] temp = processValidExpression(ve);
			geo = temp[0];			
		} catch (CircularDefinitionException e) {
			Application.debug("CircularDefinition");
			app.showError("CircularDefinition");
		} catch (Exception e) {		
			e.printStackTrace();
			if(showErrors)app.showError("InvalidInput", str);			
		} catch (MyError e) {
			e.printStackTrace();
			if(showErrors)app.showError(e);
		} catch (Error e) {
			e.printStackTrace();
			if(showErrors)app.showError("InvalidInput", str);			 
		} 
		
		cons.setSuppressLabelCreation(oldMacroMode);
		return geo;
	}
	
	/**
	 * Checks if label is valid.	 
	 */
	public String parseLabel(String label) throws ParseException {
		return parser.parseLabel(label);
	}

	public GeoElement[] processValidExpression(ValidExpression ve)
		throws MyError, Exception {
		return processValidExpression(ve, true);
	}

	/**
	 * processes valid expression. 
	 * @param ve
	 * @param redefineIndependent == true: independent objects are redefined too
	 * @return
	 * @throws MyError
	 * @throws Exception
	 */
	public GeoElement[] processValidExpression(
		ValidExpression ve,
		boolean redefineIndependent)
		throws MyError, Exception {
			
		// check for existing labels		
		String[] labels = ve.getLabels();
		GeoElement replaceable = null;
		if (labels != null && labels.length > 0) {
			boolean firstTime = true;
			for (int i = 0; i < labels.length; i++) {
				GeoElement geo = kernel.lookupLabel(labels[i]);
				if (geo != null) {
					if (geo.isFixed()) {
						String[] strs =
							{
								"IllegalAssignment",
								"AssignmentToFixed",
								":\n",
								geo.getLongDescription()};
						throw new MyError(app, strs);
					} else {
						// replace (overwrite or redefine) geo
						if (firstTime) { // only one geo can be replaced
							replaceable = geo;
							firstTime = false;
						}
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
				Application.debug("Unhandled ValidExpression : " + ve);
				throw new MyError(app, app.getError("InvalidInput") + ":\n" + ve);
			}
		}
		finally {
			cons.setSuppressLabelCreation(oldMacroMode);
		}
			
		//	try to replace replaceable geo by ret[0]		
		if (replaceable != null && ret != null && ret.length > 0) {						
			// a changeable replaceable is not redefined:
			// it gets the value of ret[0]
			// (note: texts are always redefined)
			if (!redefineIndependent
				&& replaceable.isChangeable()
				&& !(replaceable.isGeoText())) {
				try {
					replaceable.set(ret[0]);
					replaceable.updateRepaint();
					ret[0] = replaceable;
				} catch (Exception e) {		
					String errStr = app.getError("IllegalAssignment") + "\n" +
						replaceable.getLongDescription() + "     =     " 
						+
						ret[0].getLongDescription(); 
					throw new MyError(app, errStr);
				}
			}
			// redefine
			else {
				try {							
					// SPECIAL CASE: set value
					// new and old object are both independent and have same type:
					// simply assign value and don't redefine
					if (replaceable.isIndependent() && ret[0].isIndependent() &&
							replaceable.getGeoClassType() == ret[0].getGeoClassType()) 
					{
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
						String newLabel = newGeo.isLabelSet() ? newGeo.getLabel() : replaceable.getLabel();
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
	
	public GeoElement [] doProcessValidExpression(ValidExpression ve) throws MyError, Exception {
		GeoElement [] ret = null;	
		
			if (ve instanceof ExpressionNode) {
				ret = processExpressionNode((ExpressionNode) ve);
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
				ret = processFunction(null, (Function) ve);
			}	
			
			// explicit Function in multiple variables
			else if (ve instanceof FunctionNVar) {
				ret = processFunctionNVar(null, (FunctionNVar) ve);
			}	
	
			// Parametric Line        
			else if (ve instanceof Parametric) {
				ret = processParametric((Parametric) ve);
			}
	
//			// Assignment: variable
//			else if (ve instanceof Assignment) {
//				ret = processAssignment((Assignment) ve);
//			} 
			

		return ret;
	}
	

	protected GeoElement[] processFunction(ExpressionNode funNode, Function fun) {		
		fun.initFunction();		
		
		String label = fun.getLabel();
		GeoFunction f;
		GeoElement[] ret = new GeoElement[1];

		GeoElement[] vars = fun.getGeoElementVariables();				
		boolean isIndependent = (vars == null || vars.length == 0);
		
		// check for interval
		
		ExpressionNode en = fun.getExpression();
		if (en.operation == en.AND) {
			ExpressionValue left = en.left;
			ExpressionValue right = en.right;
			
			if (left.isExpressionNode() && right.isExpressionNode()) {
				ExpressionNode enLeft = (ExpressionNode)left;
				ExpressionNode enRight = (ExpressionNode)right;
				
				int opLeft = enLeft.operation;
				int opRight = enRight.operation;
				
				ExpressionValue leftLeft = enLeft.left;
				ExpressionValue leftRight = enLeft.right;
				ExpressionValue rightLeft = enRight.left;
				ExpressionValue rightRight = enRight.right;
				
				// directions of inequalities, need one + and one - for an interval
				int leftDir = 0;
				int rightDir = 0;
				
	
				if ((opLeft == en.LESS || opLeft == en.LESS_EQUAL)) {
					if (leftLeft instanceof FunctionVariable && leftRight.isNumberValue()) leftDir = -1;
					else if (leftRight instanceof FunctionVariable && leftLeft.isNumberValue()) leftDir = +1;
					
				} else
				if ((opLeft == en.GREATER || opLeft == en.GREATER_EQUAL)) {
					if (leftLeft instanceof FunctionVariable && leftRight.isNumberValue()) leftDir = +1;
					else if (leftRight instanceof FunctionVariable && leftLeft.isNumberValue()) leftDir = -1;
					
				}
				
				if ((opRight == en.LESS || opRight == en.LESS_EQUAL)) {
					if (rightLeft instanceof FunctionVariable && rightRight.isNumberValue()) rightDir = -1;
					else if (rightRight instanceof FunctionVariable && rightLeft.isNumberValue()) rightDir = +1;
					
				} else
				if ((opRight == en.GREATER || opRight == en.GREATER_EQUAL)) {
					if (rightLeft instanceof FunctionVariable && rightRight.isNumberValue()) rightDir = +1;
					else if (rightRight instanceof FunctionVariable && rightLeft.isNumberValue()) rightDir = -1;
					
				}
				
				//Application.debug(leftDir+" "+rightDir);
				//Application.debug(leftLeft.getClass()+" "+leftRight.getClass());
				//Application.debug(rightLeft.getClass()+" "+rightRight.getClass());
				
				// opposite directions -> OK
				if (leftDir * rightDir < 0) {
					if (isIndependent) {
						f = kernel.Interval(label, fun);			
					} else {			
						f = kernel.DependentInterval(label, fun);
					}
					ret[0] = f;		
					return ret;

				}
				
				
				//Application.debug(enLeft.operation+"");
				//Application.debug(enLeft.left.getClass()+"");
				//Application.debug(enLeft.right.getClass()+"");
				

			}
			//Application.debug(left.getClass()+"");
			//Application.debug(right.getClass()+"");
			//Application.debug("");
		} else if (en.operation == en.FUNCTION) {
			ExpressionValue left = en.left;
			ExpressionValue right = en.right;
			if (left.isLeaf() && left.isGeoElement() &&
				right.isLeaf() && right.isNumberValue() &&
				!isIndependent) {
				f = (GeoFunction) kernel.DependentGeoCopy(label, (GeoElement)left);
				ret[0] = f;		
				return ret;
			}
		}

		if (isIndependent) {
			f = kernel.Function(label, fun);			
		} else {			
			f = kernel.DependentFunction(label, fun);
		}
		ret[0] = f;		
		return ret;
	}
	
	protected GeoElement[] processFunctionNVar(ExpressionNode funNode, FunctionNVar fun) {		
		fun.initFunction();		
		
		String label = fun.getLabel();
		GeoElement[] ret = new GeoElement[1];

		GeoElement[] vars = fun.getGeoElementVariables();				
		boolean isIndependent = (vars == null || vars.length == 0);		
		
		if (isIndependent) {				
			ret[0] = kernel.FunctionNVar(label, fun );			
		} else {
			ret[0] = kernel.DependentFunctionNVar(label, fun);
		}
		return ret;
	}

	public GeoElement[] processEquation(Equation equ) throws MyError {		
//		Application.debug("EQUATION: " + equ);        
//		Application.debug("NORMALFORM POLYNOMIAL: " + equ.getNormalForm());        		
		
		
		try {
			equ.initEquation();	
			//Application.debug("EQUATION: " + equ.getNormalForm());    	
			// check no terms in z
			checkNoTermsInZ(equ);
			
			if (equ.isFunctionDependent()){
				return processImplicitPoly(equ);
			}

			// consider algebraic degree of equation  
			 // check not equation of eg plane
			switch (equ.degree()) {
				// linear equation -> LINE   
				case 1 :
					return processLine(equ, false);
	
				// quadratic equation -> CONIC                                  
				case 2 :
					return processConic(equ);
	
				default :
					//test for "y= <rhs>" here as well
					if (equ.getLHS().toString().trim().equals("y")){
						Function fun = new Function(equ.getRHS());
						// try to use label of equation							
						fun.setLabel(equ.getLabel());
						return processFunction(null, fun);
					}
					return processImplicitPoly(equ);
			}
		} 
		catch (MyError eqnError) {
			eqnError.printStackTrace();
			
        	// invalid equation: maybe a function of form "y = <rhs>"?			
			String lhsStr = equ.getLHS().toString().trim();
			if (lhsStr.equals("y")) {
				try {
					// try to create function from right hand side
					Function fun = new Function(equ.getRHS());

					// try to use label of equation							
					fun.setLabel(equ.getLabel());
					return processFunction(null, fun);
				}
				catch (MyError funError) {
					funError.printStackTrace();
				}        
			} 
			
			// throw invalid equation error if we get here
			if (eqnError.getMessage() == "InvalidEquation")
				throw eqnError;
			else {
				String [] errors = {"InvalidEquation", eqnError.getLocalizedMessage()};
				throw new MyError(app, errors);
			}
        }        
		
		
	}
	
	
	
	protected void checkNoTermsInZ(Equation equ){
		if (!equ.getNormalForm().isFreeOf('z')) 
			throw new MyError(app, "InvalidEquation");
	}

	protected GeoElement[] processLine(Equation equ, boolean inequality) {
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
			line =  kernel.Line(label, a, b, c);
		} else
			line =  kernel.DependentLine(label, equ);

		if (isExplicit) {
			line.setToExplicit();
			line.updateRepaint();
		}
		ret[0] = line;
		return ret;
	}

	protected GeoElement[] processConic(Equation equ) {
		double a = 0, b = 0, c = 0, d = 0, e = 0, f = 0;
		GeoElement[] ret = new GeoElement[1];
		GeoConic conic;
		String label = equ.getLabel();
		Polynomial lhs = equ.getNormalForm();
		
		boolean isExplicit = equ.isExplicit("y");
		boolean isSpecific =
			!isExplicit && (equ.isExplicit("yy") || equ.isExplicit("xx"));
		boolean isIndependent = lhs.isConstant();

		if (isIndependent) {
			a = lhs.getCoeffValue("xx");
			b = lhs.getCoeffValue("xy");
			c = lhs.getCoeffValue("yy");
			d = lhs.getCoeffValue("x");
			e = lhs.getCoeffValue("y");
			f = lhs.getCoeffValue("");
			conic = kernel.Conic(label, a, b, c, d, e, f);
		} else
			conic = kernel.DependentConic(label, equ);
		if (isExplicit) {
			conic.setToExplicit();
			conic.updateRepaint();
		} else if (isSpecific || conic.getType() == GeoConic.CONIC_CIRCLE) {
			conic.setToSpecific();
			conic.updateRepaint();
		}
		ret[0] = conic;
		return ret;
	}

	protected GeoElement[] processImplicitPoly(Equation equ){
		GeoElement[] ret = new GeoElement[1];
		String label = equ.getLabel();
		Polynomial lhs = equ.getNormalForm();
		boolean isIndependent = !equ.isFunctionDependent()&&lhs.isConstant();
		GeoImplicitPoly poly;
		GeoElement geo=null;
		if (isIndependent){
			poly=kernel.ImplicitPoly(label, lhs);
			poly.setUserInput(equ);
			geo=poly;
		}else{
			geo=kernel.DependentImplicitPoly(label, equ); //might also return Line or Conic
			if (geo instanceof GeoUserInputElement){
				((GeoUserInputElement)geo).setUserInput(equ);
			}
		}
		ret[0]=geo;
//		Application.debug("User Input: "+equ);
		ret[0].updateRepaint();
		return ret;
	}

	private GeoElement[] processParametric(Parametric par)
		throws CircularDefinitionException {
		
		/*
		ExpressionValue temp = P.evaluate();
        if (!temp.isVectorValue()) {
            String [] str = { "VectorExpected", temp.toString() };
            throw new MyParseError(kernel.getApplication(), str);        
        }

        v.resolveVariables();
        temp = v.evaluate();
        if (!(temp instanceof VectorValue)) {
            String [] str = { "VectorExpected", temp.toString() };
            throw new MyParseError(kernel.getApplication(), str);
        } */       
		
		// point and vector are created silently
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		// get point
		ExpressionNode node = par.getP();
		node.setForcePoint();
		GeoElement[] temp = processExpressionNode(node);
		GeoPoint P = (GeoPoint) temp[0];

		//	get vector
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
	
	


	protected GeoElement[] processExpressionNode(ExpressionNode n) throws MyError {					
		// command is leaf: process command		
		if (n.isLeaf()) {			
			 ExpressionValue leaf =  n.getLeft();
			 if (leaf instanceof Command) {			
				Command c = (Command) leaf;
				c.setLabels(n.getLabels());
				return cmdDispatcher.processCommand(c, true);
			 }
			 else if (leaf instanceof Equation) {
				 Equation eqn = (Equation) leaf;
				 eqn.setLabels(n.getLabels());
				 return processEquation(eqn);
			 }
			 else if (leaf instanceof Function) {				 
				Function fun = (Function) leaf;
				fun.setLabels(n.getLabels());
				return processFunction(n, fun);			
			} 
			 else if (leaf instanceof FunctionNVar) {
				FunctionNVar fun = (FunctionNVar) leaf;
				fun.setLabels(n.getLabels());
				return processFunctionNVar(n, fun);			
			} 
			 
			 
		}											
		
		// ELSE:  resolve variables and evaluate expressionnode		
		n.resolveVariables();			
		eval = n.evaluate(); 
		boolean dollarLabelFound = false;		
		
		ExpressionNode myNode = n;
		if (myNode.isLeaf()) myNode = myNode.getLeftTree();
		// leaf (no new label specified): just return the existing GeoElement
		if (eval.isGeoElement() &&  n.getLabel() == null && !(n.operation == ExpressionNode.ELEMENT_OF)) 
		{
			// take care of spreadsheet $ names: don't loose the wrapper ExpressionNode here
			// check if we have a Variable 
			switch (myNode.getOperation()) {
				case ExpressionNode.$VAR_COL:
				case ExpressionNode.$VAR_ROW:
				case ExpressionNode.$VAR_ROW_COL:
					// don't do anything here: we need to keep the wrapper ExpressionNode
					// and must not return the GeoElement here	
					dollarLabelFound = true;
					break;
					
				default:
					// return the GeoElement
					GeoElement[] ret = {(GeoElement) eval };					
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
			return processFunction(n, (Function) eval);			
		} 
		else if (eval instanceof FunctionNVar) {
			
			return processFunctionNVar(n, (FunctionNVar) eval);			
		} 
		//we have to process list in case list=matrix1(1), but not when list=list2 
		else if (eval instanceof GeoList  && myNode.hasOperations()) {
			Application.debug("should work");
			return processList(n, ((GeoList) eval).getMyList());
		} else if (eval.isGeoElement()) {	

			// e.g. B1 = A1 where A1 is a GeoElement and B1 does not exist yet
			// create a copy of A1
				if (n.getLabel() != null || dollarLabelFound) {
					return processGeoCopy(n.getLabel(), n);	
				}									
			} 	

		
		// REMOVED due to issue 131: http://code.google.com/p/geogebra/issues/detail?id=131
//		// expressions like 2 a (where a:x + y = 1)
//		// A1=b doesn't work for these objects
//		else if (eval instanceof GeoLine) {
//			if (((GeoLine)eval).getParentAlgorithm() instanceof AlgoDependentLine) {
//				GeoElement[] ret = {(GeoElement) eval };
//				return ret;
//			}
// 
//		}
		 
		
		// if we get here, nothing worked
		Application.debug(
				"Unhandled ExpressionNode: " + eval + ", " + eval.getClass());
		return null;
	}

	private GeoElement[] processNumber(
		ExpressionNode n,
		ExpressionValue evaluate) {
		GeoElement[] ret = new GeoElement[1];
		String label = n.getLabel();
		boolean isIndependent = n.isConstant();
		MyDouble eval = ((NumberValue) evaluate).getNumber();
		boolean isAngle = eval.isAngle();
		double value = eval.getDouble();

		if (isIndependent) {
			if (isAngle)
				ret[0] = new GeoAngle(cons, label, value);
			else
				ret[0] = new GeoNumeric(cons, label, value);
		} else {
			ret[0] = kernel.DependentNumber(label, n, isAngle);
		}	
		
		if (n.isForcedFunction()) {
			ret[0] = ((GeoFunctionable)(ret[0])).getGeoFunction();
		}
		
		return ret;
	}
	
	private GeoElement [] processList(ExpressionNode n, MyList evalList) {		
		String label = n.getLabel();		
				
		GeoElement[] ret = new GeoElement[1];
		
		// no operations or no variables are present, e.g.
		// { a, b, 7 } or  { 2, 3, 5 } + {1, 2, 4}
		if (!n.hasOperations() || n.isConstant()) {		
			
			// PROCESS list items to generate a list of geoElements		
			ArrayList geoElements = new ArrayList();
			boolean isIndependent = true;
							
			// make sure we don't create any labels for the list elements
			boolean oldMacroMode = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);
			
			int size = evalList.size();
			for (int i=0; i < size; i++) {
				ExpressionNode en = (ExpressionNode) evalList.getListElement(i);
				// we only take one resulting object	
				GeoElement [] results = processExpressionNode(en);						
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

	private GeoElement[] processText(
		ExpressionNode n,
		ExpressionValue evaluate) {
		GeoElement[] ret = new GeoElement[1];
		String label = n.getLabel();

		boolean isIndependent = n.isConstant();

		if (isIndependent) {
			MyStringBuffer eval = ((TextValue) evaluate).getText();
			ret[0] = kernel.Text(label, eval.toValueString());
		} else
			ret[0] = kernel.DependentText(label, n);
		return ret;
	}
	
	private GeoElement[] processBoolean(
		ExpressionNode n,
		ExpressionValue evaluate) {
		GeoElement[] ret = new GeoElement[1];
		String label = n.getLabel();

		boolean isIndependent = n.isConstant();

		if (isIndependent) {				
			ret[0] = kernel.Boolean(label, ((BooleanValue) evaluate).getBoolean());
		} else
			ret[0] = kernel.DependentBoolean(label, n);
		return ret;
	}

	private GeoElement[] processPointVector(
		ExpressionNode n,
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
			if (!(n.isForcedPoint() || n.isForcedVector())) { // may be set by MyXMLHandler
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
		
	/** empty method in 2D : see AlgebraProcessor3D to see implementation in 3D
	 * @param n
	 * @param evaluate
	 * @return null
	 */
	protected GeoElement[] processPointVector3D(
			ExpressionNode n,
			ExpressionValue evaluate) {

		return null;
	}
		
	/** 
	 * Creates a dependent copy of origGeo with label
	 */
	private GeoElement[] processGeoCopy(String copyLabel, ExpressionNode origGeoNode) {
		GeoElement[] ret = new GeoElement[1];
		ret[0] = kernel.DependentGeoCopy(copyLabel, origGeoNode);		
		return ret;
	}

	/** Enables CAS specific behaviour */
	public void enableCAS() {
		cmdDispatcher.initCASCommands();
	}

//	/**
//	 * Processes assignments, i.e. input of the form leftVar = geoRight where geoRight is an existing GeoElement.
//	 */
//	private GeoElement[] processAssignment(String leftVar, GeoElement geoRight) throws MyError {		
//		GeoElement[] ret = new GeoElement[1];
//
//		// don't allow copying of dependent functions
//		
//		/*
//		if (
//			geoRight instanceof GeoFunction && !geoRight.isIndependent()) {
//			String[] str = { "IllegalAssignment", rightVar };
//			throw new MyError(app, str);
//		}
//		*/
//
//		
//		GeoElement geoLeft = cons.lookupLabel(leftVar, false);
//		if (geoLeft == null) { // create kernel object and copy values
//			geoLeft = geoRight.copy();
//			geoLeft.setLabel(leftVar);
//			ret[0] = geoLeft;
//		} else { // overwrite
//			ret[0] = geoRight;
//		}
//		
//		
//		if (ret[0] != null && !ret[0].isLabelSet()) {
//			ret[0].setLabel(null);
//		}
//		
//		return ret;
//	}
	

}
