/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */
package org.geogebra.common.kernel.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.geogebra.common.geogebra3D.kernel3D.implicit3D.AlgoDependentImplicitSurface;
import org.geogebra.common.geogebra3D.kernel3D.implicit3D.GeoImplicitSurface;
import org.geogebra.common.io.MathMLParser;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.KernelCAS;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoDependentBoolean;
import org.geogebra.common.kernel.algos.AlgoDependentConic;
import org.geogebra.common.kernel.algos.AlgoDependentFunctionNVar;
import org.geogebra.common.kernel.algos.AlgoDependentGeoCopy;
import org.geogebra.common.kernel.algos.AlgoDependentInterval;
import org.geogebra.common.kernel.algos.AlgoDependentLine;
import org.geogebra.common.kernel.algos.AlgoDependentListExpression;
import org.geogebra.common.kernel.algos.AlgoDependentNumber;
import org.geogebra.common.kernel.algos.AlgoDependentPoint;
import org.geogebra.common.kernel.algos.AlgoDependentText;
import org.geogebra.common.kernel.algos.AlgoDependentVector;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoLaTeX;
import org.geogebra.common.kernel.arithmetic.AssignmentType;
import org.geogebra.common.kernel.arithmetic.BooleanValue;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.MyStringBuffer;
import org.geogebra.common.kernel.arithmetic.MyVecNode;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.Polynomial;
import org.geogebra.common.kernel.arithmetic.TextValue;
import org.geogebra.common.kernel.arithmetic.Traversing;
import org.geogebra.common.kernel.arithmetic.Traversing.CollectFunctionVariables;
import org.geogebra.common.kernel.arithmetic.Traversing.CollectUndefinedVariables;
import org.geogebra.common.kernel.arithmetic.Traversing.FVarCollector;
import org.geogebra.common.kernel.arithmetic.Traversing.ReplaceUndefinedVariables;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.arithmetic.Variable;
import org.geogebra.common.kernel.arithmetic.VectorValue;
import org.geogebra.common.kernel.arithmetic3D.MyVec3DNode;
import org.geogebra.common.kernel.arithmetic3D.Vector3DValue;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoAngle.AngleStyle;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoInterval;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoScriptAction;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.GeoUserInputElement;
import org.geogebra.common.kernel.geos.GeoVec2D;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.implicit.AlgoDependentImplicitCurve;
import org.geogebra.common.kernel.implicit.AlgoDependentImplicitPoly;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;
import org.geogebra.common.kernel.implicit.GeoImplicitPoly;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.kernel.parser.ParserInterface;
import org.geogebra.common.main.App;
import org.geogebra.common.main.BracketsError;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

/**
 * Processes algebra input as Strings and valid expressions into GeoElements
 * 
 * @author Markus
 *
 */
public class AlgebraProcessor {

	/**
	 * String code returned from the dialog if the user wants to create a new slider
	 */
	public static final String CREATE_SLIDER = "1";

	/** kernel */
	protected final Kernel kernel;
	/** construction */
	protected final Construction cons;
	/** app */
	protected final App app;
	private final Localization loc;
	private final ParserInterface parser;
	/** command dispatcher */
	protected final CommandDispatcher cmdDispatcher;

	private boolean disableGcd = false;

	/**
	 * @param kernel
	 *            kernel
	 * @param commandDispatcher
	 *            command dispatcher
	 */
	public AlgebraProcessor(Kernel kernel, CommandDispatcher commandDispatcher) {
		this.kernel = kernel;
		cons = kernel.getConstruction();

		this.cmdDispatcher = commandDispatcher;
		app = kernel.getApplication();
		loc = app.getLocalization();
		parser = kernel.getParser();
	}

	/**
	 * Returns the localized name of a command subset. Indices are defined in
	 * CommandDispatcher.
	 * 
	 * @param index
	 *            commands subtable index
	 * @return set of commands for given subtable
	 */
	public String getSubCommandSetName(int index) {
		return cmdDispatcher.getSubCommandSetName(index);
	}

	/**
	 * Returns whether the given command name is supported in GeoGebra.
	 * 
	 * @param cmd
	 *            command name
	 * @return true if available
	 */
	public boolean isCommandAvailable(String cmd) {
		return cmdDispatcher.isCommandAvailable(cmd);
	}

	/**
	 * @param c
	 *            command
	 * @param labelOutput
	 *            true to label output
	 * @return resulting geos
	 * @throws MyError
	 *             e.g. on syntax error
	 */
	final public GeoElement[] processCommand(Command c, boolean labelOutput)
			throws MyError {
		return cmdDispatcher.processCommand(c, labelOutput);
	}

	/**
	 * @param c
	 *            command
	 * @param labelOutput
	 *            whether output should get a label
	 * @return simplified expression
	 * @throws MyError
	 *             error
	 */
	final public ExpressionValue simplifyCommand(Command c,
			boolean labelOutput)
			throws MyError {
		return cmdDispatcher.simplifyCommand(c, labelOutput);
	}

	/**
	 * Processes the given casCell, i.e. compute its output depending on its
	 * input. Note that this may create an additional twin GeoElement.
	 * 
	 * @param casCell
	 *            cas cell
	 * @param isLastRow
	 *            whether this cell is in last row -- allows us to skip
	 *            recompuattion of the whole CAS
	 * @throws MyError
	 *             e.g. on syntax error
	 */
	final public void processCasCell(GeoCasCell casCell, boolean isLastRow)
			throws MyError {
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
		boolean nowFree = !casCell.hasVariablesOrCommands();
		boolean needsRedefinition = false;
		// If we change dependencies of CAS cells, we need to update
		// construction
		// to make sure the CAS cells are painted in right order (#232)
		boolean needsConsUpdate = false;
		if (prevFree) {
			if (nowFree) {
				// free -> free, e.g. m := 7 -> m := 8
				cons.addToConstructionList(casCell, true);
				casCell.computeOutput(); // create twinGeo if necessary
				casCell.setLabelOfTwinGeo();
				needsRedefinition = false;
			} else {
				boolean isInCons = cons.isInConstructionList(casCell);
				// update output for existent casCell
				// needed for #4118
				if (isInCons) {
					casCell.computeOutput();
					casCell.setLabelOfTwinGeo();
					needsRedefinition = false;
				}
				// free -> dependent, e.g. m := 7 -> m := c+2
				else if (casCell.isOutputEmpty() && !casCell.hasChildren()) {
					// this is a new casCell
					cons.removeFromConstructionList(casCell);
					KernelCAS.DependentCasCell(casCell);
					needsRedefinition = false;
					needsConsUpdate = !isLastRow;
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
				// the changeCasCell command computes the output
				// so we don't need to call computeOutput,
				// which also causes marble crashes

				// casCell.computeOutput();
				// casCell.updateCascade();
			} catch (Exception e) {
				e.printStackTrace();
				casCell.setError("RedefinitionFailed");
				// app.showError(e.getMessage());
			}
		} else {
			casCell.notifyAdd();
			casCell.updateCascade();
			if (needsConsUpdate)
				cons.updateCasCells();
		}

	}

	/**
	 * for AlgebraView changes in the tree selection and redefine dialog
	 * 
	 * @param geo
	 *            old geo
	 * @param newValue
	 *            new value
	 * @param redefineIndependent
	 *            true to allow redefinition of free objects
	 * @param storeUndoInfo
	 *            true to make undo step
	 * 
	 * @return changed geo
	 */
	public GeoElement changeGeoElement(GeoElement geo, String newValue,
			boolean redefineIndependent, boolean storeUndoInfo) {

		try {
			return changeGeoElementNoExceptionHandling(geo, newValue,
					redefineIndependent, storeUndoInfo);
		} catch (MyError e) {
			app.showError(e);
			return null;
		} catch (Exception e) {
			app.showError(e.getMessage());
			return null;
		}
	}



	/**
	 * for AlgebraView changes in the tree selection and redefine dialog
	 * 
	 * @param geo
	 *            old geo
	 * @param newValue
	 *            new value
	 * @param redefineIndependent
	 *            true to allow redefinition of free objects
	 * @param storeUndoInfo
	 *            true to make undo step
	 * @return changed geo
	 * @throws Exception
	 *             e.g. parse exception or circular definition
	 * @throws MyError
	 *             eg assignment to fixed object
	 *
	 */
	public GeoElement changeGeoElementNoExceptionHandling(GeoElement geo,
			String newValue, boolean redefineIndependent, boolean storeUndoInfo)
			throws Exception, MyError {

		try {
			ValidExpression ve = parser.parseGeoGebraExpression(newValue);
			if ("X".equals(ve.getLabel())) {
				ve = getParamProcessor().checkParametricEquationF(ve, ve, cons);
			}
			return changeGeoElementNoExceptionHandling(geo, ve,
					redefineIndependent, storeUndoInfo);
		} catch (CircularDefinitionException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(loc.getError("InvalidInput") + ":\n" + newValue);
		} catch (MyError e) {
			e.printStackTrace();
			throw e;
		} catch (Error e) {
			e.printStackTrace();
			throw new Exception(loc.getError("InvalidInput") + ":\n" + newValue);
		}
	}

	/**
	 * for AlgebraView changes in the tree selection and redefine dialog
	 * 
	 * @param geo
	 *            old geo
	 * @param newValue
	 *            new value
	 * @param redefineIndependent
	 *            true to make sure independent are redefined instead of value
	 *            change
	 * @param storeUndoInfo
	 *            true to makeundo step
	 * 
	 * @return changed geo
	 * @throws Exception
	 *             circular definition
	 */
	public GeoElement changeGeoElementNoExceptionHandling(GeoElement geo,
			ValidExpression newValue, boolean redefineIndependent,
			boolean storeUndoInfo) throws Exception {
		String oldLabel, newLabel;
		GeoElement[] result;

		try {
			app.getCompanion().storeViewCreators();
			oldLabel = geo.getLabel(StringTemplate.defaultTemplate);
			// need to check isDefined() eg redefine FitPoly[{A, B, C, D, E, F,
			// G, H, I}, 22] to FitPoly[{A, B, C, D, E, F, G, H, I}, 2]
			/*
			 * if (geo instanceof GeoFunction && ((GeoFunction)
			 * geo).isDefined()) { cons.registerFunctionVariable(((GeoFunction)
			 * geo).getFunction()
			 * .getVarString(StringTemplate.defaultTemplate)); }
			 */
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
				boolean listeners = app.getScriptManager().hasListeners();
				app.getScriptManager().disableListeners();
				result = processValidExpression(newValue, redefineIndependent);
				app.getScriptManager().enableListeners();
				if (listeners) {
					geo.updateCascade();
				}
				if (result != null) {
					app.getCompanion().recallViewCreators();
					if (storeUndoInfo)
						app.storeUndoInfo();
				}

				return result[0];
			} else if (cons.isFreeLabel(newLabel)) {
				newValue.setLabel(oldLabel);
				// rename to oldLabel to enable overwriting
				result = processValidExpression(newValue, redefineIndependent);
				result[0].setLabel(newLabel); // now we rename
				app.getCompanion().recallViewCreators();
				if (storeUndoInfo)
					app.storeUndoInfo();
				return result[0];
			} else {
				String str[] = { "NameUsed", newLabel };
				throw new MyError(loc, str);
			}
		} catch (CircularDefinitionException e) {
			App.debug("CircularDefinition");
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(loc.getError("InvalidInput") + ":\n" + newValue);
		} catch (MyError e) {
			throw e;
		} catch (Error e) {
			e.printStackTrace();
			throw new Exception(loc.getError("InvalidInput") + ":\n" + newValue);
		} finally {
			cons.registerFunctionVariable(null);
		}
	}

	/*
	 * methods for processing an input string
	 */
	// returns non-null GeoElement array when successful
	/**
	 * @param cmd
	 *            string to process
	 * @param storeUndo
	 *            true to make undo step
	 * @return resulting geos
	 */
	public GeoElement[] processAlgebraCommand(String cmd, boolean storeUndo) {

		try {
			return processAlgebraCommandNoExceptionHandling(cmd, storeUndo,
					true, false, false);
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
	 * 
	 * @param cmd
	 *            string to process
	 * @param storeUndo
	 *            true to create undo step
	 * @return resulting geos
	 */
	public GeoElement[] processAlgebraCommandNoExceptions(String cmd,
			boolean storeUndo) {

		try {
			return processAlgebraCommandNoExceptionHandling(cmd, storeUndo,
					true, false, false);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Processes the string and hides all errors
	 * 
	 * @param str
	 *            string to process
	 * @param storeUndo
	 *            true to create undo step
	 * @return resulting elements
	 */
	public GeoElement[] processAlgebraCommandNoExceptionsOrErrors(String str,
			boolean storeUndo) {

		try {
			return processAlgebraCommandNoExceptionHandling(str, storeUndo,
					false, false, false);
		} catch (Exception e) {
			return null;
		} catch (MyError e) {
			return null;
		}
	}

	/**
	 * @param cmd
	 *            command
	 * @param storeUndo
	 *            whether to make undo point
	 * @param allowErrorDialog
	 *            whether error dialog might pop up
	 * @param throwMyError
	 *            whether myError may be thrown
	 * @param autoCreateSliders
	 *            whether sliders may be created
	 * @return resulting elements
	 * @throws Exception
	 *             exception
	 */
	public final GeoElement[] processAlgebraCommandNoExceptionHandling(
			String cmd,
			boolean storeUndo, boolean allowErrorDialog, boolean throwMyError,
			boolean autoCreateSliders) throws Exception {
		return processAlgebraCommandNoExceptionHandling(cmd, storeUndo,
				allowErrorDialog, throwMyError, autoCreateSliders, null);
	}

	private MathMLParser mathmlParserGGB;
	private MathMLParser mathmlParserLaTeX;

	/**
	 * Parametric processor (shared with 3D)
	 */
	protected ParametricProcessor paramProcessor;

	// G.Sturr 2010-7-5
	// added 'allowErrorDialog' flag to handle the case of unquoted text
	// entries in the spreadsheet
	/**
	 * @param cmd
	 *            string to process
	 * @param storeUndo
	 *            true to make undo step
	 * @param allowErrorDialog
	 *            true to allow dialogs
	 * @param throwMyError
	 *            true to throw MyErrors (if dialogs are not allowed)
	 * @param autoCreateSliders
	 *            whether to show a popup for undefined variables
	 * @param callback0
	 *            callback after the geos are created
	 * @return resulting geos
	 * @throws Exception
	 *             e.g. circular definition or parse exception
	 */
	public GeoElement[] processAlgebraCommandNoExceptionHandling(
			final String cmd, final boolean storeUndo,
			final boolean allowErrorDialog, final boolean throwMyError,
			boolean autoCreateSliders, final AsyncOperation callback0)
			throws Exception {

		// both return this and call callback0 in case of success!
		GeoElement[] rett;

		if (cmd.length() > 0 && cmd.charAt(0) == '<' && cmd.startsWith("<math")) {
			rett = parseMathml(cmd, storeUndo, throwMyError,
					autoCreateSliders, callback0);
			if (rett != null && callback0 != null) {
				callback0.callback(rett);
			}
			return rett;
		}
		ValidExpression ve;
		try {
			ve = parser.parseGeoGebraExpression(cmd);

			// collect undefined variables
			CollectUndefinedVariables collecter = new Traversing.CollectUndefinedVariables();
			ve.traverse(collecter);
			final TreeSet<String> undefinedVariables = collecter.getResult();


			// check if there's already an "x" in expression. Create one if not.
			// eg sinx + x -> sin(x) + x
			CollectFunctionVariables fvCollecter = new Traversing.CollectFunctionVariables();
			ve.traverse(fvCollecter);
			ArrayList<FunctionVariable> fvTree = fvCollecter.getResult();
			FunctionVariable fvX = null;
			Iterator<FunctionVariable> fvIt = fvTree.iterator();
			while (fvIt.hasNext()) {
				FunctionVariable fv = fvIt.next();
				if ("x".equals(fv.getLabel())) {
					fvX = fv;
					break;
				}
			}
			if (fvX == null) {
				fvX = new FunctionVariable(kernel, "x");
			}
			GeoElement[] ret = getParamProcessor().checkParametricEquation(ve,
					undefinedVariables, autoCreateSliders, callback0);
			if (ret != null) {
				return ret;
			}
			if (undefinedVariables.size() > 0) {

				// ==========================
				// step0: check if there's an error on processing
				// eg we don't want to create slider 't' for
				// Curve[t^3,t^2,t,0,2]
				// ==========================
				GeoElement[] geoElements = null;
				try {
					geoElements = processValidExpression(ve);
					if (storeUndo && geoElements != null)
						app.storeUndoInfo();
				} catch (Throwable ex) {
					// ex.printStackTrace();
					// do nothing
				}

				if (geoElements != null) {
					kernel.getConstruction().registerFunctionVariable(null);

					// this was forgotten to do here, added by Arpad
					// TODO: maybe need to add this to more places here?
					if (callback0 != null) {
						callback0.callback(geoElements);
					}

					return geoElements;
				}

				StringBuilder sb = new StringBuilder();


				// ==========================
				// step3: make a list of undefined variables so we can ask the
				// user
				// ==========================
				Iterator<String> it = undefinedVariables.iterator();
				while (it.hasNext()) {
					String label = it.next();
					if (kernel.lookupLabel(label) == null) {
						Log.debug("not found" + label);
						sb.append(label);
						sb.append(", ");
					} else {
						Log.debug("found" + label);
					}
				}

				if (sb.toString().endsWith(", ")) {
					sb.setLength(sb.length() - 2);
				}

				// ==========================
				// step4: ask user
				// ==========================
				if (sb.length() > 0) {
					// eg from Spreadsheet we don't want a popup
					if (!autoCreateSliders) {
						rett = tryReplacingProducts(ve);
						if (callback0 != null && rett != null) {
							callback0.callback(rett);
						}
						return rett;
					}

					// boolean autoCreateSlidersAnswer = false;

					// "Create sliders for a, b?" Create Sliders / Cancel
					// Yes: create sliders and draw line
					// No: go back into input bar and allow user to change input
					if (app.getGuiManager() != null) {
						AsyncOperation callback = null;
						if (callback0 != null) {

							//final FunctionVariable fvX2 = fvX;
							final ValidExpression ve2 = ve;

							callback = new AsyncOperation() {

								@Override
								public void callback(Object obj) {
									String[] dialogResult = (String[]) obj;
									GeoElement[] geos = null;

									// TODO: need we to catch the Exception
									// here,
									// which can throw the
									// processAlgebraInputCommandNoExceptionHandling
									// function?
									if (CREATE_SLIDER.equals(dialogResult[0])) {
										// insertStarIfNeeded(undefinedVariables,
										// ve2, fvX2);
										replaceUndefinedVariables(ve2,
												new TreeSet<GeoNumeric>(), null);
										try {
											geos = processValidExpression(
													storeUndo,
													allowErrorDialog,
													throwMyError, ve2);
										} catch (Exception ee) {
											AlgebraProcessor.this.app
													.showError(ee.getMessage());
											return;
										}
									}
									callback0.callback(geos);
								}

							};
						}
						boolean autoCreateSlidersAnswer = this.app
								.getGuiManager().checkAutoCreateSliders(
										sb.toString(), callback);
						if (!autoCreateSlidersAnswer) {
							return null;
						}
					}
				}

				// App.debug("list of variables: "+sb.toString());

				// ==========================
				// step5: replace undefined variables
				// ==========================
				replaceUndefinedVariables(ve, new TreeSet<GeoNumeric>(), null);
			}

		} catch (Exception e) {

			e.printStackTrace();
			if (allowErrorDialog) {
				app.showError(loc.getError("InvalidInput") + ":\n" + cmd);
				return null;
			}
			throw new MyException(loc.getError("InvalidInput") + ":\n" + cmd,
					MyException.INVALID_INPUT);
		} catch (BracketsError e) {
			e.printStackTrace();
			if (allowErrorDialog) {
				app.showError(e.getLocalizedMessage());
				return null;
			}
			throw new MyException(e, MyException.IMBALANCED_BRACKETS);
		} catch (Error e) {
			e.printStackTrace();
			if (allowErrorDialog) {
				app.showError(loc.getError("InvalidInput") + ":\n" + cmd);
				return null;
			}
			throw new Exception(loc.getError("InvalidInput") + ":\n" + cmd);
		}

		// process ValidExpression (built by parser)
		GeoElement[] geos = processValidExpression(storeUndo, allowErrorDialog,
				throwMyError, ve);
		if (callback0 != null)
			callback0.callback(geos);
		return geos;
	}



	/**
	 * @param ve
	 *            expression to check for parametrics
	 * @param undefinedVariables
	 *            set of undefined variables
	 * @return result of parametric evaluation if successfull
	 */
	/*
	 * GeoElement[] checkParametricAfterUndefinedChanged( ValidExpression ve,
	 * TreeSet<String> undefinedVariables) { replaceUndefinedVariables(ve,
	 * undefinedVariables, false); GeoElement[] param2 =
	 * getParamProcessor().checkParametricEquation(ve, undefinedVariables); if
	 * (param2 != null) { return param2; } if (!undefinedVariables.isEmpty()) {
	 * replaceUndefinedVariables(ve, undefinedVariables, true); } return null; }
	 */

	private GeoElement[] tryReplacingProducts(ValidExpression ve) {
		ValidExpression ve2 = (ValidExpression) ve.traverse(new Traversing() {

			@Override
			public ExpressionValue process(ExpressionValue ev) {
				if (ev.isExpressionNode()
						&& ((ExpressionNode) ev).getOperation() == Operation.MULTIPLY) {
					String lt = ((ExpressionNode) ev).getLeft()
							.toString(StringTemplate.defaultTemplate)
							.replace(" ", "");
					Operation op = app.getParserFunctions().get(lt, 1);
					if (op != null) {
						return new ExpressionNode(kernel, ((ExpressionNode) ev)
								.getRight().traverse(this), op, null);
					}
				}
				return ev;
			}
		});
		GeoElement[] ret = null;
		try {
			ret = this.processValidExpression(ve2);
		} catch (Throwable t) {
			// invalid user input, ignore
		}
		return ret;
	}

	/**
	 * TODO figure out how to handle sliders here
	 * 
	 * @param autoCreateSliders
	 *            whether sliders should be autocreated
	 */
	private GeoElement[] parseMathml(String cmd, final boolean storeUndo,
			final boolean throwMyError,
			boolean autoCreateSliders, final AsyncOperation callback0) {
		if (mathmlParserGGB == null) {
			mathmlParserGGB = new MathMLParser(true);
		}
		GeoElement[] ret = null;
		try {
			String ggb = mathmlParserGGB.parse(cmd, false, true);
			RegExp assignment = RegExp.compile("^(\\w+) \\(x\\)=(.*)$");
			MatchResult lhs = assignment.exec(ggb);
			if (lhs != null) {
				ggb = lhs.getGroup(1) + "(x)=" + lhs.getGroup(2);
			}
			Log.debug(cmd);
			Log.debug(ggb);
			ret = this.processAlgebraCommandNoExceptionHandling(ggb, storeUndo,
					false, throwMyError, false, callback0);
		} catch (Throwable t) {
			Log.warn(t.getMessage());
		}
		if (ret != null && ret.length != 0) {
			return ret;
		}
		if (mathmlParserLaTeX == null) {
			mathmlParserLaTeX = new MathMLParser(false);
		}
		String latex = mathmlParserLaTeX.parse(cmd, false, false);
		GeoText arg = new GeoText(cons, latex);
		AlgoLaTeX texAlgo = new AlgoLaTeX(cons, null, arg);
		return new GeoElement[] { texAlgo.getOutput(0) };
	}

	/**
	 * @param storeUndo
	 *            whether to create an undo point
	 * @param allowErrorDialog
	 *            whether to allow error dialogs
	 * @param throwMyError
	 *            whethe my error may be thrown
	 * @param ve
	 *            input expression
	 * @return processed expression
	 * @throws Exception
	 *             when circular definition or eg. parse exception happens
	 */
	public GeoElement[] processValidExpression(boolean storeUndo,
			boolean allowErrorDialog, boolean throwMyError, ValidExpression ve)
			throws Exception {
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
			} else if (throwMyError) {
				throw new MyError(loc, e.getLocalizedMessage(),
						e.getcommandName(),e);
			}
			return null;
		} catch (CircularDefinitionException e) {
			App.debug("CircularDefinition");
			throw e;
		} catch (Exception ex) {
			App.debug("Exception");
			ex.printStackTrace();
			throw new Exception(loc.getError("Error") + ":\n"
					+ ex.getLocalizedMessage());
		} finally {
			kernel.getConstruction().registerFunctionVariable(null);
		}
		return geoElements;
	}

	/**
	 * Replaces undefined variables inside of expression
	 * 
	 * @param ve
	 *            expression
	 * @param undefined
	 *            list of variables undefined so far; items will be removed from
	 *            it as we go
	 * @param except
	 *            list of variable names that should not be replaced, null means
	 *            replace everything
	 */
	public void replaceUndefinedVariables(ValidExpression ve,
			TreeSet<GeoNumeric> undefined, String[] except) {
		ReplaceUndefinedVariables replacer = new Traversing.ReplaceUndefinedVariables(
				this.kernel, undefined, except);
		ve.traverse(replacer);

	}

	/**
	 * Parses given String str and tries to evaluate it to a double. Returns
	 * Double.NaN if something went wrong.
	 * 
	 * @param str
	 *            string to process
	 * @return result as double
	 */
	public double evaluateToDouble(String str) {
		return evaluateToDouble(str, false);
	}

	/**
	 * Parses given String str and tries to evaluate it to a double. Returns
	 * Double.NaN if something went wrong.
	 * 
	 * @param str
	 *            string to process
	 * @param suppressErrors
	 *            false to show error messages (only stacktrace otherwise)
	 * @return result as double
	 */
	public double evaluateToDouble(String str, boolean suppressErrors) {
		try {
			ValidExpression ve = parser.parseExpression(str);
			ExpressionNode en = (ExpressionNode) ve;
			en.resolveVariables();
			NumberValue nv = (NumberValue) en
					.evaluate(StringTemplate.defaultTemplate);
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
	 * 
	 * @param str
	 *            string to process
	 * @param showError
	 *            whether to show error dialog
	 * @return resulting boolean
	 */
	public GeoBoolean evaluateToBoolean(String str, boolean showError) {
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		GeoBoolean bool = null;
		try {
			ValidExpression ve = parser.parseGeoGebraExpression(str);
			GeoElement[] temp = processValidExpression(ve);
			bool = (GeoBoolean) temp[0];
		} catch (CircularDefinitionException e) {
			App.debug("CircularDefinition");
			if (showError) {
			app.showError("CircularDefinition");
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (showError) {
			app.showError("InvalidInput", str);
			}
		} catch (MyError e) {
			e.printStackTrace();
			if (showError) {
			app.showError(e);
			}
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
	 * 
	 * @param str
	 *            input string
	 * @return resulting list
	 */
	public GeoList evaluateToList(String str) {
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		GeoList list = null;
		try {
			ValidExpression ve = parser.parseGeoGebraExpression(str);
			GeoElement[] temp = processValidExpression(ve);
			// CAS in GeoGebraWeb dies badly if we don't handle this case
			// (Simon's hack):
			// list = (GeoList) temp[0];
			if (temp[0] instanceof GeoList) {
				list = (GeoList) temp[0];
			} else {
				App.error("return value was not a list");
			}
		} catch (CircularDefinitionException e) {
			App.debug("CircularDefinition");
			// app.showError("CircularDefinition");
		} catch (Exception e) {
			e.printStackTrace();
			// app.showError("InvalidInput", str);
		} catch (MyError e) {
			e.printStackTrace();
			// app.showError(e);
		} catch (Error e) {
			e.printStackTrace();
			// app.showError("InvalidInput", str);
		}

		cons.setSuppressLabelCreation(oldMacroMode);
		return list;
	}

	/**
	 * Parses given String str and tries to evaluate it to a GeoFunction Returns
	 * null if something went wrong. Michael Borcherds 2008-04-04
	 * 
	 * @param str
	 *            input string
	 * @param suppressErrors
	 *            false to show error messages (only stacktrace otherwise)
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
	 * @param str
	 *            input string
	 * @param suppressErrors
	 *            true to suppress error messages
	 * @return str parsed to multivariate function
	 */
	public GeoFunctionNVar evaluateToFunctionNVar(String str,
			boolean suppressErrors) {
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		GeoFunctionNVar func = null;
		try {
			ValidExpression ve = parser.parseGeoGebraExpression(str);
			GeoElement[] temp = processValidExpression(ve);

			if (temp[0] instanceof GeoFunctionNVar) {
				func = (GeoFunctionNVar) temp[0];
			} else if (temp[0] instanceof GeoFunction) {
				FunctionVariable[] funVars;
				if (((GeoFunction) temp[0]).isFunctionOfY()) {

					funVars = new FunctionVariable[] {
							((GeoFunction) temp[0]).getFunction()
									.getFunctionVariable(),
							new FunctionVariable(kernel, "y") };
				} else {
					funVars = new FunctionVariable[] {
							new FunctionVariable(kernel, "y"),
							((GeoFunction) temp[0]).getFunction()
									.getFunctionVariable() };
				}

				FunctionNVar fn = new FunctionNVar(
						((GeoFunction) temp[0]).getFunctionExpression(),
						funVars);
				func = new GeoFunctionNVar(cons, fn);
			} else if (temp[0] instanceof GeoNumeric) {
				FunctionVariable[] funVars = new FunctionVariable[] {
						new FunctionVariable(kernel, "x"),
						new FunctionVariable(kernel, "y") };
				FunctionNVar fn = new FunctionNVar(new ExpressionNode(kernel,
						temp[0]), funVars);
				func = new GeoFunctionNVar(cons, fn);

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
	 * 
	 * @param str
	 *            string to parse
	 * @param suppressErrors
	 *            false to show error messages (only stacktrace otherwise)
	 * @return resulting number
	 */
	public GeoNumberValue evaluateToNumeric(String str, boolean suppressErrors) {
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		GeoNumberValue num = null;
		try {
			ValidExpression ve = parser.parseGeoGebraExpression(str);
			GeoElement[] temp = processValidExpression(ve);
			num = (GeoNumberValue) temp[0];
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
	 * 
	 * @param str
	 *            string to process
	 * @param showErrors
	 *            true to show error messages (only stacktrace otherwise)
	 * @param suppressLabels
	 *            true to suppress labeling
	 * @return resulting point
	 */
	public GeoPointND evaluateToPoint(String str, boolean showErrors,
			boolean suppressLabels) {
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
	 * 
	 * @param str
	 *            input string
	 * @param createLabel
	 *            true to label result
	 * @param showErrors
	 *            true to show error messages (only stacktrace otherwise)
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
	 * @param str
	 *            stringInput
	 * @param showErrors
	 *            if false, only stacktraces are printed
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
	 * 
	 * @param label
	 *            potential label
	 * @return valid label
	 * @throws ParseException
	 *             if label is invalid
	 */
	public String parseLabel(String label) throws ParseException {
		return parser.parseLabel(label);
	}

	/**
	 * @param ve
	 *            expression to process
	 * @return resulting elements
	 * @throws MyError
	 *             e.g. for wrong syntax
	 * @throws Exception
	 *             e.g. for circular definition
	 */
	public GeoElement[] processValidExpression(ValidExpression ve)
			throws MyError, Exception {
		return processValidExpression(ve, true);
	}

	/**
	 * processes valid expression.
	 * 
	 * @param ve
	 *            expression to process
	 * @param redefineIndependent
	 *            == true: independent objects are redefined too
	 * @throws MyError
	 *             e.g. on wrong syntax
	 * @throws Exception
	 *             e.g. for circular definition
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
						throw new MyError(loc, strs);
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
				throw new MyError(loc, loc.getError("InvalidInput") + ":\n"
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
					String errStr = loc.getError("IllegalAssignment") + "\n"
							+ replaceable.getLongDescription() + "     =     "
							+ ret[0].getLongDescription();
					throw new MyError(loc, errStr);
				}
			}
			// redefine
			else {
				try {
					if (!ret[0].isLabelSet()
							&& ret[0].getParentAlgorithm() instanceof AlgoDependentGeoCopy) {
						ret[0] = ((AlgoDependentGeoCopy) ret[0]
								.getParentAlgorithm()).getOrigGeo();
					}
					// SPECIAL CASE: set value
					// new and old object are both independent and have same
					// type:
					// simply assign value and don't redefine
					if (replaceable.isIndependent()
							&& ret[0].isIndependent()
							&& compatibleTypes(replaceable.getGeoClassType(),
									ret[0].getGeoClassType())) {
						if (replaceable instanceof GeoNumeric) {
							((GeoNumeric) replaceable).extendMinMax(ret[0]);
						}
						replaceable.set(ret[0]);
						replaceable.updateRepaint();
						ret[0] = replaceable;
					}

					// STANDARD CASE: REDFINED
					else {
						GeoElement newGeo = ret[0];
						GeoCasCell cell = replaceable.getCorrespondingCasCell();
						if (cell != null) {
							// this is a ValidExpression since we don't get
							// GeoElements from parsing
							ValidExpression vexp = (ValidExpression) ve
									.unwrap();
							cell.setAssignmentType(AssignmentType.DEFAULT);
							cell.setInput(vexp
									.toAssignmentString(StringTemplate.defaultTemplate, cell.getAssignmentType()));
							processCasCell(cell, false);
						} else {
							cons.replace(replaceable, newGeo);
						}
						// now all objects have changed
						// get the new object with same label as our result
						String newLabel = newGeo.isLabelSet() ? newGeo
								.getLabelSimple() : replaceable
								.getLabelSimple();
						ret[0] = kernel.lookupLabel(newLabel);
					}
				} catch (CircularDefinitionException e) {
					throw e;
				} catch (Exception e) {
					e.printStackTrace();
					throw new MyError(loc, "ReplaceFailed");
				} catch (MyError e) {
					e.printStackTrace();
					throw new MyError(loc, "ReplaceFailed");
				}
			}
		}

		return ret;
	}

	private static boolean compatibleTypes(GeoClass type, GeoClass type2) {
		if (type2.equals(type))
			return true;
		if (type2.equals(GeoClass.NUMERIC) && type.equals(GeoClass.ANGLE))
			return true;
		if (type.equals(GeoClass.NUMERIC) && type2.equals(GeoClass.ANGLE))
			return true;
		return false;
	}

	/**
	 * Processes valid expression
	 * 
	 * @param ve
	 *            expression to process
	 * @return array of geos
	 * @throws MyError
	 *             if syntax error occurs
	 * @throws CircularDefinitionException
	 *             if circular definition occurs
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
			} else if (ret != null && ret.length > 0
					&& ret[0] instanceof GeoList) {
				int actions = ((GeoList) ret[0]).performScriptActions();
				if (actions > 0) {
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

		// // Assignment: variable
		// else if (ve instanceof Assignment) {
		// ret = processAssignment((Assignment) ve);
		// }

		return ret;
	}

	/**
	 * Wraps given function into GeoFunction, if dependent,
	 * AlgoDependentFunction is created.
	 * 
	 * @param fun
	 *            function
	 * @return GeoFunction
	 */
	public final GeoElement[] processFunction(Function fun) {
		if (!fun.initFunction()) {
			return getParamProcessor().processParametricFunction(
					fun.getExpression(),
					fun
					.getExpression().evaluate(StringTemplate.defaultTemplate),
					new FunctionVariable[] { fun.getFunctionVariable() },
					fun.getLabel());
		}

		String label = fun.getLabel();
		GeoFunction f;
		GeoElement[] ret = new GeoElement[1];

		GeoElement[] vars = fun.getGeoElementVariables();
		boolean isIndependent = true;
		for (int i = 0; vars != null && i < vars.length; i++) {
			if (Inspecting.dynamicGeosFinder.check(vars[i])) {
				isIndependent = false;
			}
		}
		// check for interval

		ExpressionNode en = fun.getExpression();
		if (en.getOperation().equals(Operation.AND)
				|| en.getOperation().equals(Operation.AND_INTERVAL)) {
			ExpressionValue left = en.getLeft();
			ExpressionValue right = en.getRight();

			if (left.isExpressionNode() && right.isExpressionNode()) {
				ExpressionNode enLeft = (ExpressionNode) left;
				ExpressionNode enRight = (ExpressionNode) right;

				// directions of inequalities, need one + and one - for an
				// interval
				int leftDir = getDirection(enLeft);
				int rightDir = getDirection(enRight);

				// opposite directions -> OK
				if (leftDir * rightDir < 0) {
					if (isIndependent) {
						f = new GeoInterval(cons, label, fun);
					} else {
						f = DependentInterval(label, fun);
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
			// the isConstant() here makes difference between f(1) and f(x), see
			// #2155
			if (left.isLeaf() && left.isGeoElement() && right.isLeaf()
					&& right.isNumberValue() && !right.isConstant()
					&& !isIndependent) {
				f = (GeoFunction) DependentGeoCopy(label,
						((GeoFunctionable) left).getGeoFunction());
				ret[0] = f;
				return ret;
			}
		}

		if (isIndependent) {
			f = new GeoFunction(cons, label, fun);
		} else {
			f = kernel.getAlgoDispatcher().DependentFunction(label, fun);
		}
		ret[0] = f;
		return ret;
	}



	/**
	 * @return parametric processor
	 */
	public ParametricProcessor getParamProcessor() {
		if (this.paramProcessor == null) {
			paramProcessor = new ParametricProcessor(kernel, this);
		}
		return paramProcessor;
	}

	/**
	 * @param length
	 *            length
	 * @return array of zeros with given length
	 */
	protected ExpressionValue[] arrayOfZeros(int length) {
		ExpressionValue[] ret = new ExpressionValue[length];
		for (int i = 0; i < length; i++) {
			ret[i] = new MyDouble(kernel, 0);
		}
		return ret;
	}

	/**
	 * @param cx
	 *            expression
	 * @param coefX
	 *            output array for coeffs
	 * @param mult
	 *            multiplicator
	 * @param loc2
	 *            variable
	 * @return degree if successful, -1 otherwise
	 */
	public int getPolyCoeffs(ExpressionNode cx, ExpressionValue[] coefX,
			ExpressionNode mult, GeoNumeric loc2) {
		if (!cx.containsDeep(loc2)) {
			add(coefX, 0, mult.multiply(cx));
			return 0;
		} else if (cx.getOperation() == Operation.PLUS) {
			int deg1 = getPolyCoeffs(cx.getLeftTree(), coefX, mult, loc2);
			int deg2 = getPolyCoeffs(cx.getRightTree(), coefX, mult, loc2);
			if (deg1 < 0 || deg2 < 0) {
				return -1;
			}
			return Math.max(deg1, deg2);
		} else if (cx.getOperation() == Operation.MINUS) {
			int deg1 = getPolyCoeffs(cx.getLeftTree(), coefX, mult, loc2);
			int deg2 = getPolyCoeffs(cx.getRightTree(), coefX,
					mult.multiply(-1), loc2);
			if (deg1 < 0 || deg2 < 0) {
				return -1;
			}
			return Math.max(deg1, deg2);
		} else if (cx.getOperation() == Operation.MULTIPLY) {
			if (!cx.getLeft().contains(loc2)) {
				return getPolyCoeffs(cx.getRightTree(), coefX,
						mult.multiply(cx.getLeft().unwrap()), loc2);
			} else if (!cx.getRight().contains(loc2)) {
				return getPolyCoeffs(cx.getLeftTree(), coefX,
						mult.multiply(cx.getRight().unwrap()), loc2);
			} else {
				ExpressionValue[] left = arrayOfZeros(3);
				ExpressionValue[] right = arrayOfZeros(3);
				int degL = getPolyCoeffs(cx.getLeftTree(), left, mult, loc2);
				int degR = getPolyCoeffs(cx.getRightTree(), right,
						new ExpressionNode(kernel, 1), loc2);

				if (degL == 1 && degR == 1) {
					add(coefX, 0, left[0].wrap().multiply(right[0]));
					add(coefX, 1, left[1].wrap().multiply(right[0]));
					add(coefX, 1, left[0].wrap().multiply(right[1]));
					add(coefX, 2, left[1].wrap().multiply(right[1]));
					return 2;
				}
				return -1;
			}
		} else if (cx.getOperation() == Operation.POWER) {
			if (cx.getRight().unwrap() instanceof MyDouble
					&& Kernel.isEqual(2, cx.getRight().evaluateDouble())) {
				ExpressionValue[] left = arrayOfZeros(3);
				int degL = getPolyCoeffs(cx.getLeftTree(), left,
						new ExpressionNode(kernel, 1), loc2);
				if (degL == 1) {
					add(coefX, 0, left[0].wrap().power(2).multiply(mult));
					add(coefX, 1, left[1].wrap().multiply(left[0]).multiply(2)
							.multiply(mult));
					add(coefX, 2, left[1].wrap().power(2).multiply(mult));
					return 2;
				}
				return -1;
			}
		} else if (cx.unwrap() == loc2) {
			add(coefX, 1, mult);
			return 1;
		}
		return -1;
	}



	/**
	 * @param cx
	 *            expression
	 * @param coefX
	 *            output array for coefficients
	 * @param scale
	 *            multiplicator
	 * 
	 * @param var
	 *            variable
	 * @return cx is in one of the forms a+b sin(var)+c*cos(var), a+b
	 *         sinh(var)+c*cosh(var)
	 */
	public boolean getTrigCoeffs(ExpressionNode cx, ExpressionValue[] coefX,
			ExpressionNode scale, GeoElement var) {
		boolean childrenOK = true;
		if (cx.getOperation() == Operation.PLUS) {
			childrenOK = getTrigCoeffs(cx.getLeftTree(), coefX, scale, var)
					&& getTrigCoeffs(cx.getRightTree(), coefX, scale, var);
		} else if (cx.getOperation() == Operation.MINUS) {
			childrenOK = getTrigCoeffs(cx.getLeftTree(), coefX, scale, var)
					&& getTrigCoeffs(cx.getRightTree(), coefX,
							scale.multiply(-1), var);
		} else if (cx.getOperation() == Operation.MULTIPLY) {
			if (cx.getLeft().evaluatesToNumber(false)
					&& !cx.getLeft().wrap().containsDeep(var)) {
				return getTrigCoeffs(cx.getRightTree(), coefX,
						scale.multiply(cx.getLeft().unwrap()), var);
			} else if (cx.getRight().evaluatesToNumber(false)
					&& !cx.getRight().wrap().containsDeep(var)) {
				return getTrigCoeffs(cx.getLeftTree(), coefX,
						scale.multiply(cx.getRight().unwrap()), var);
			}
			return false;
		} else if (cx.getOperation() == Operation.SIN) {
			if (cx.getLeft().unwrap() != var) {
				return false;
			}
			add(coefX, 1, scale);
		} else if (cx.getOperation() == Operation.COS) {
			if (cx.getLeft().unwrap() != var) {
				return false;
			}
			add(coefX, 2, scale);
		} else if (cx.getOperation() == Operation.SINH) {
			if (cx.getLeft().unwrap() != var) {
				return false;
			}
			add(coefX, 3, scale);
		} else if (cx.getOperation() == Operation.COSH) {
			if (cx.getLeft().unwrap() != var) {
				return false;
			}
			add(coefX, 4, scale);
		} else if (cx.isLeaf()) {
			if (cx.getLeft().contains(var)) {
				return false;
			}
			add(coefX, 0, cx.multiply(scale));
		} else {
			return false;
		}
		return childrenOK
				&& ((coefX[1] == null && coefX[2] == null) || (coefX[3] == null && coefX[4] == null));
	}

	private static void add(ExpressionValue[] coefX, int i, ExpressionNode scale) {
		if (coefX[i] == null) {
			coefX[i] = scale;
		} else {
			coefX[i] = scale.plus(coefX[i]);
		}
	}

	/**
	 * @param exp
	 *            expression
	 * @param i
	 *            0 for x, 1 for y, 2 for z
	 * @return given coordinate of expression
	 */
	public ExpressionNode computeCoord(ExpressionNode exp, int i) {
		Operation[] ops = new Operation[] { Operation.XCOORD, Operation.YCOORD,
				Operation.ZCOORD };
		if (exp.isLeaf()) {
			if (exp.getLeft() instanceof MyVecNode
					&& ((MyVecNode) exp.getLeft()).getMode() == Kernel.COORD_CARTESIAN) {
				return i == 0 ? ((MyVecNode) exp.getLeft()).getX().wrap()
						: (i == 1 ? ((MyVecNode) exp.getLeft()).getY().wrap()
								: new ExpressionNode(kernel, 0));
			}
			if (exp.getLeft() instanceof MyVecNode
					&& ((MyVecNode) exp.getLeft()).getMode() == Kernel.COORD_POLAR) {
				if (i == 2) {
					return new ExpressionNode(kernel, 0);
				}
				return ((MyVecNode) exp.getLeft())
						.getX()
						.wrap()
						.multiply(
								((MyVecNode) exp.getLeft())
										.getY()
										.wrap()
										.apply(i == 0 ? Operation.COS
												: Operation.SIN));
			}
			if (exp.getLeft() instanceof MyVec3DNode
					&& (((MyVec3DNode) exp.getLeft()).getMode() == Kernel.COORD_CARTESIAN || ((MyVec3DNode) exp
							.getLeft()).getMode() == Kernel.COORD_CARTESIAN_3D)) {
				return i == 0 ? ((MyVec3DNode) exp.getLeft()).getX().wrap()
						: (i == 1 ? ((MyVec3DNode) exp.getLeft()).getY().wrap()
								: ((MyVec3DNode) exp.getLeft()).getZ().wrap());
			}
		}
		switch (exp.getOperation()) {
		case PLUS:
			return computeCoord(exp.getLeftTree(), i).plus(
					computeCoord(exp.getRightTree(), i));
		case MINUS:
			return computeCoord(exp.getLeftTree(), i).subtract(
					computeCoord(exp.getRightTree(), i));
		case MULTIPLY:
			if (exp.getRight().evaluatesToNonComplex2DVector()
					|| exp.getRight().evaluatesTo3DVector()) {
				return computeCoord(exp.getRightTree(), i).multiply(
						exp.getLeft());
			} else if (exp.getLeft().evaluatesToNonComplex2DVector()
					|| exp.getLeft().evaluatesTo3DVector()) {
				return computeCoord(exp.getLeftTree(), i).multiply(
						exp.getRight());
			}
		default:
			return new ExpressionNode(kernel, exp, ops[i], null);
		}
		
	}

	private static int getDirection(ExpressionNode enLeft) {
		int dir = 0;
		ExpressionValue left = enLeft.getLeft();
		ExpressionValue right = enLeft.getRight();
		Operation op = enLeft.getOperation();
		if ((op.equals(Operation.LESS) || op.equals(Operation.LESS_EQUAL))) {
			if (left instanceof FunctionVariable && right.isNumberValue()
					&& right.isConstant())
				dir = -1;
			else if (right instanceof FunctionVariable && left.isNumberValue()
					&& left.isConstant())
				dir = +1;

		} else if ((op.equals(Operation.GREATER) || op
				.equals(Operation.GREATER_EQUAL))) {
			if (left instanceof FunctionVariable && right.isNumberValue()
					&& right.isConstant())
				dir = +1;
			else if (right instanceof FunctionVariable && left.isNumberValue()
					&& right.isConstant())
				dir = -1;

		}
		return dir;
	}

	/**
	 * Interval dependent on coefficients of arithmetic expressions with
	 * variables, represented by trees. e.g. x > a && x < b
	 */
	final private GeoFunction DependentInterval(String label, Function fun) {
		AlgoDependentInterval algo = new AlgoDependentInterval(cons, label, fun);
		GeoFunction f = algo.getFunction();
		return f;
	}

	final private GeoElement DependentGeoCopy(String label,
			GeoElement origGeoNode) {
		AlgoDependentGeoCopy algo = new AlgoDependentGeoCopy(cons, label,
				origGeoNode);
		return algo.getGeo();
	}

	/**
	 * Wraps given functionNVar into GeoFunctionNVar, if dependent,
	 * AlgoDependentFunctionNVar is created.
	 * 
	 * @param fun
	 *            function
	 * @return GeoFunctionNVar
	 */
	public GeoElement[] processFunctionNVar(FunctionNVar fun) {
		if (!fun.initFunction()) {
			return getParamProcessor().processParametricFunction(
					fun.getExpression(),
					fun.getExpression()
							.evaluate(StringTemplate.defaultTemplate),
					fun.getFunctionVariables(), fun.getLabel());
		}


		String label = fun.getLabel();
		GeoElement[] ret = new GeoElement[1];

		GeoElement[] vars = fun.getGeoElementVariables();
		boolean isIndependent = (vars == null || vars.length == 0);

		if (isIndependent) {
			ret[0] = new GeoFunctionNVar(cons, label, fun);
		} else {
			ret[0] = DependentFunctionNVar(label, fun);
		}
		return ret;
	}

	/**
	 * Multivariate Function depending on coefficients of arithmetic expressions
	 * with variables, e.g. f(x,y) = a x^2 + b y^2
	 */
	final private GeoFunctionNVar DependentFunctionNVar(String label,
			FunctionNVar fun) {
		AlgoDependentFunctionNVar algo = new AlgoDependentFunctionNVar(cons,
				label, fun);
		GeoFunctionNVar f = algo.getFunction();
		return f;
	}

	/**
	 * Processes given equation to an array containing single line / conic /
	 * implicit polynomial. Throws MyError for degree 0 equations, eg. 1=2 or
	 * x=x.
	 * 
	 * @param equ
	 *            equation
	 * @return line, conic, implicit poly or plane
	 * @throws MyError
	 *             e.g. for invalid operation
	 */
	public final GeoElement[] processEquation(Equation equ) throws MyError {
		ExpressionValue lhs = equ.getLHS().unwrap();
		//z = 7
		if (lhs instanceof FunctionVariable
				&& !equ.getRHS().containsFreeFunctionVariable(null)
				&& !equ.getRHS().evaluatesToNumber(true)) {
			equ.getRHS().setLabel(lhs.toString(StringTemplate.defaultTemplate));
			try {
				return processValidExpression(equ.getRHS());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// s = t^2
		if (lhs instanceof Variable
 && kernel.lookupLabel("X") == null
				&& "X".equals(lhs.toString(StringTemplate.defaultTemplate))) {
			return getParamProcessor().processXEquation(equ);
		}
		if (lhs instanceof Variable
				&& kernel.lookupLabel(((Variable) lhs).getName()) == null) {
			equ.getRHS().setLabel(lhs.toString(StringTemplate.defaultTemplate));
			try {
				return processValidExpression(equ.getRHS());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}



		// z(x) = sin(x), see #5484
		if (lhs instanceof ExpressionNode
				&& ((ExpressionNode)lhs).getOperation() == Operation.ZCOORD
				&& ((ExpressionNode) lhs).getLeft().unwrap() instanceof FunctionVariable
				) {
			equ.getRHS().setLabel("z");
			try {
				return processValidExpression(equ.getRHS());
			} catch (Exception e) {
				e.printStackTrace();
			
			}
		}
		return processEquation(equ, kernel.getConstruction().isFileLoading());
	}

	/**
	 * @param equ
	 *            equation
	 * @param allowConstant
	 *            true to allow equations like 2=3 or x=x, false to throw
	 *            MyError for those
	 * @return line, conic, implicit poly or plane
	 * @throws MyError
	 *             e.g. for invalid operation
	 */
	public final GeoElement[] processEquation(Equation equ,
			boolean allowConstant) throws MyError {
		// AbstractApplication.debug("EQUATION: " + equ);
		// AbstractApplication.debug("NORMALFORM POLYNOMIAL: " +
		// equ.getNormalForm());

		equ.initEquation();

		// check no terms in z
		checkNoTermsInZ(equ);

		if (equ.isFunctionDependent()) {
			return processImplicitPoly(equ);
		}
		int deg = equ.mayBePolynomial() && !equ.hasVariableDegree() ? equ
				.degree() : -1;
		// consider algebraic degree of equation
		// check not equation of eg plane
		switch (deg) {
		// linear equation -> LINE
		case 1:
			return processLine(equ);

			// quadratic equation -> CONIC
		case 2:
			return processConic(equ);
			// pi = 3 is not an equation, #1391
		case 0:
			if (!allowConstant) {
				throw new MyError(app.getLocalization(), "InvalidEquation");
			}
			// if constants are allowed, build implicit poly
		default:
			// test for "y= <rhs>" here as well
			String lhsStr = equ.getLHS().toString(StringTemplate.xmlTemplate)
					.trim();

			if (lhsStr.equals("y")
					&& !equ.getRHS().containsFreeFunctionVariable("y")) {

				Function fun = new Function(equ.getRHS());
				// try to use label of equation
				fun.setLabel(equ.getLabel());
				return processFunction(fun);
			}
			if (app.has(Feature.IMPLICIT_CURVES)) {
				return processImplicitCurve(equ);
			} else if (equ.mayBePolynomial()) {
				return processImplicitPoly(equ);
			} 

			String[] errors = { "InvalidEquation" };
			throw new MyError(loc, errors);
		}

	}

	/**
	 * @param equ
	 *            equation
	 */
	protected void checkNoTermsInZ(Equation equ) {
		if (!equ.getNormalForm().isFreeOf('z'))
			equ.setIsPolynomial(false);
	}

	/**
	 * @param equ
	 *            equation
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
			line = DependentLine(label, equ);

		if (isExplicit) {
			line.setToExplicit();
			line.updateRepaint();
		}
		ret[0] = line;
		return ret;
	}

	/**
	 * Line dependent on coefficients of arithmetic expressions with variables,
	 * represented by trees. e.g. y = k x + d
	 */
	final private GeoLine DependentLine(String label, Equation equ) {
		AlgoDependentLine algo = new AlgoDependentLine(cons, label, equ);
		GeoLine line = algo.getLine();
		return line;
	}

	/**
	 * @param equ
	 *            equation
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
			conic = DependentConic(label, equ);
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
	 * Conic dependent on coefficients of arithmetic expressions with variables,
	 * represented by trees.
	 */
	final private GeoConic DependentConic(String label, Equation equ) {
		AlgoDependentConic algo = new AlgoDependentConic(cons, label, equ);
		GeoConic conic = algo.getConic();
		return conic;
	}

	/**
	 * @param equ
	 *            equation
	 * @return resulting implicit polynomial
	 */
	protected GeoElement[] processImplicitPoly(Equation equ) {
		GeoElement[] ret = new GeoElement[1];
		String label = equ.getLabel();
		Polynomial lhs = equ.getNormalForm();
		boolean isIndependent = !equ.isFunctionDependent() && lhs.isConstant()
				&& !equ.hasVariableDegree();
		GeoImplicitPoly poly;
		GeoElement geo = null;
		if (isIndependent) {
			poly = new GeoImplicitPoly(cons, label, lhs);
			poly.setUserInput(equ);
			geo = poly;
		} else {
			AlgoDependentImplicitPoly algo = new AlgoDependentImplicitPoly(
					cons, label, equ, true);

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

	/**
	 * @param equ
	 *            equation
	 * @return implicit curve wrapped in array
	 */
	protected GeoElement[] processImplicitCurve(Equation equ) {
		GeoElement[] ret = new GeoElement[1];
		String label = equ.getLabel();
		boolean isIndependent = equ.getVariables() == null
				|| equ.getVariables().size() == 0;
		GeoImplicitCurve poly;
		GeoElement geo = null;
		if (isIndependent) {
			if (kernel.getApplication().getActiveEuclidianView()
					.isEuclidianView3D()) {
				geo = new GeoImplicitSurface(cons, equ);
			} else {
				poly = new GeoImplicitCurve(cons, label, equ);
				geo = poly;
			}
		} else {
			if (kernel.getApplication().getActiveEuclidianView()
					.isEuclidianView3D()) {
				AlgoDependentImplicitSurface algo = new AlgoDependentImplicitSurface(
						cons, label, equ, true);

				geo = algo.getGeo();
			} else {
				AlgoDependentImplicitCurve algo = new AlgoDependentImplicitCurve(
						cons, label, equ, true);

				geo = algo.getGeo();
			}
		}
		ret[0] = geo;
		// AbstractApplication.debug("User Input: "+equ);
		ret[0].updateRepaint();
		return ret;
	}

	/**
	 * @param node
	 *            expression
	 * @return resulting geos
	 * @throws MyError
	 *             on invalid operation
	 */
	public GeoElement[] processExpressionNode(ExpressionNode node)
			throws MyError {
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
		n.resolveVariables();
		String label = n.getLabel();
		if (n.containsFreeFunctionVariable(null)) {
			Set<String> fvSet = new TreeSet<String>();
			FVarCollector fvc = FVarCollector.getCollector(fvSet);
			n.traverse(fvc);

			if (fvSet.size() == 1) {
				n = new ExpressionNode(kernel, new Function(n,
						new FunctionVariable(kernel, fvSet.iterator().next())));
			} else {
				FunctionVariable[] fvArray = new FunctionVariable[fvSet.size()];
				Iterator<String> it = fvSet.iterator();
				int i = 0;
				while (it.hasNext()) {
					fvArray[i++] = new FunctionVariable(kernel, it.next());
				}
				n = new ExpressionNode(kernel, new FunctionNVar(n, fvArray));
			}
		}
		eval = n.evaluate(StringTemplate.defaultTemplate);
		if (eval instanceof ValidExpression && label != null) {
			((ValidExpression) eval).setLabel(label);
		}
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

		if (eval instanceof BooleanValue)
			return processBoolean(n, eval);
		else if (eval instanceof NumberValue)
			return processNumber(n, eval);
		else if (eval instanceof VectorValue)
			return processPointVector(n, eval);
		else if (eval instanceof Vector3DValue)
			return processPointVector3D(n, eval);
		else if (eval instanceof TextValue)
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
			return processList(n, ((GeoList) eval).getMyList());
		} else if (eval.isGeoElement()) {

			// e.g. B1 = A1 where A1 is a GeoElement and B1 does not exist yet
			// create a copy of A1
			if (n.getLabel() != null || dollarLabelFound) {
				App.debug(n.getLabel());
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
		App.debug("Unhandled ExpressionNode: " + eval + ", " + eval.getClass());
		return null;
	}

	private GeoElement[] processNumber(ExpressionNode n,
			ExpressionValue evaluate) {
		GeoElement[] ret = new GeoElement[1];
		String label = n.getLabel();
		boolean isIndependent = !n.inspect(Inspecting.dynamicGeosFinder);
		MyDouble val = ((NumberValue) evaluate).getNumber();
		boolean isAngle = val.isAngle();
		double value = val.getDouble();

		if (isIndependent) {
			if (isAngle) {
				ret[0] = new GeoAngle(cons, label, value, AngleStyle.UNBOUNDED);
			} else {
				ret[0] = new GeoNumeric(cons, label, value);
			}
			ret[0].setDefinition(n);
		} else {
			ret[0] = DependentNumber(label, n, isAngle, evaluate)
					.toGeoElement();
		}

		if (n.isForcedFunction()) {
			ret[0] = ((GeoFunctionable) (ret[0])).getGeoFunction();
		}

		return ret;
	}

	/**
	 * Number dependent on arithmetic expression with variables, represented by
	 * a tree. e.g. t = 6z - 2
	 */
	final private GeoNumberValue DependentNumber(String label,
			ExpressionNode root,
			boolean isAngle, ExpressionValue evaluate) {
		AlgoDependentNumber algo = new AlgoDependentNumber(cons, label, root,
				isAngle, evaluate);
		GeoNumberValue number = algo.getNumber();
		return number;
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
				ExpressionNode en = evalList.getListElement(i).wrap();
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
			ret[0] = kernel.getAlgoDispatcher().List(label, geoElements,
					isIndependent);
		}

		// operations and variables are present
		// e.g. {3, 2, 1} + {a, b, 2}
		else {
			ret[0] = ListExpression(label, n);
		}

		return ret;
	}

	/**
	 * Creates a dependent list object with the given label, e.g. {3, 2, 1} +
	 * {a, b, 2}
	 * 
	 * @param label
	 *            label for output
	 * @param root
	 *            expression defining the dependent list
	 * @return resulting list
	 */
	final public GeoList ListExpression(String label, ExpressionNode root) {
		AlgoDependentListExpression algo = new AlgoDependentListExpression(
				cons, label, root);
		return algo.getList();
	}

	private GeoElement[] processText(ExpressionNode n, ExpressionValue evaluate) {
		GeoElement[] ret = new GeoElement[1];
		String label = n.getLabel();

		boolean isIndependent = n.isConstant();

		if (isIndependent) {
			MyStringBuffer val = ((TextValue) evaluate).getText();
			ret[0] = Text(label,
					val.toValueString(StringTemplate.defaultTemplate));
		} else
			ret[0] = DependentText(label, n);
		return ret;
	}

	/**
	 * Text dependent on coefficients of arithmetic expressions with variables,
	 * represented by trees. e.g. text = "Radius: " + r
	 */
	final private GeoText DependentText(String label, ExpressionNode root) {
		AlgoDependentText algo = new AlgoDependentText(cons, label, root);
		GeoText t = algo.getGeoText();
		return t;
	}

	/**
	 * @param label
	 *            label for result
	 * @param text
	 *            content of the text
	 * @return resulting text
	 */
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

		boolean isIndependent = !n.inspect(Inspecting.dynamicGeosFinder);

		if (isIndependent) {

			ret[0] = new GeoBoolean(cons);
			((GeoBoolean) ret[0]).setValue(((BooleanValue) evaluate)
					.getBoolean());
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
		boolean isIndependent = !n.inspect(Inspecting.dynamicGeosFinder);

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
		boolean isVector = n.shouldEvaluateToGeoVector();

		if (isIndependent) {
			// get coords
			double x = p.getX();
			double y = p.getY();
			if (isVector) {
				ret[0] = kernel.getAlgoDispatcher().Vector(label, x, y);
			} else {
				ret[0] = kernel.getAlgoDispatcher().Point(label, x, y, complex);
			}
			ret[0].setDefinition(n);
		} else {
			if (isVector)
				ret[0] = DependentVector(label, n);
			else
				ret[0] = DependentPoint(label, n, complex);
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
	 * Point dependent on arithmetic expression with variables, represented by a
	 * tree. e.g. P = (4t, 2s)
	 */
	final private GeoPoint DependentPoint(String label, ExpressionNode root,
			boolean complex) {
		AlgoDependentPoint algo = new AlgoDependentPoint(cons, label, root,
				complex);
		GeoPoint P = algo.getPoint();
		return P;
	}

	/**
	 * Vector dependent on arithmetic expression with variables, represented by
	 * a tree. e.g. v = u + 3 w
	 */
	final private GeoVector DependentVector(String label, ExpressionNode root) {
		AlgoDependentVector algo = new AlgoDependentVector(cons, label, root);
		GeoVector v = algo.getVector();
		return v;
	}

	/**
	 * empty method in 2D : see AlgebraProcessor3D to see implementation in 3D
	 * 
	 * @param n
	 *            3D point expression
	 * @param evaluate
	 *            evaluated node n
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
		ret[0] = DependentGeoCopy(copyLabel, origGeoNode);
		return ret;
	}

	/**
	 * Creates a dependent copy of origGeo with label
	 */
	final private GeoElement DependentGeoCopy(String label,
			ExpressionNode origGeoNode) {
		AlgoDependentGeoCopy algo = new AlgoDependentGeoCopy(cons, label,
				origGeoNode);
		return algo.getGeo();
	}

	/**
	 * Show error dialog
	 * 
	 * @param key
	 *            key for error.properties
	 */
	public void showError(String key) {
		app.showError(loc.getError(key));
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
	// throw new MyError(loc, str);
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

	private MyStringBuffer xBracket = null, yBracket = null, zBracket = null,
			closeBracket = null;

	/** @return "x(" */
	public MyStringBuffer getXBracket() {
		if (xBracket == null)
			xBracket = new MyStringBuffer(kernel, "x(");
		return xBracket;
	}

	/** @return "y(" */
	public MyStringBuffer getYBracket() {
		if (yBracket == null)
			yBracket = new MyStringBuffer(kernel, "y(");
		return yBracket;
	}

	/** @return "z(" */
	public MyStringBuffer getZBracket() {
		if (zBracket == null)
			zBracket = new MyStringBuffer(kernel, "z(");
		return zBracket;
	}

	/** @return ")" */
	public MyStringBuffer getCloseBracket() {
		if (closeBracket == null)
			closeBracket = new MyStringBuffer(kernel, ")");
		return closeBracket;
	}

	/**
	 * @return flag for disabled GCD when parsing lines from CAS
	 */
	public boolean getDisableGcd() {
		return disableGcd;
	}

	/**
	 * @param disableGcd
	 *            flag for disabled GCD when parsing lines from CAS
	 */
	public void setDisableGcd(boolean disableGcd) {
		this.disableGcd = disableGcd;
	}

	/**
	 * Reinitialize the set of commands that can be handled after some commands
	 * were disabled
	 */
	public void reinitCommands() {
		if (this.cmdDispatcher != null) {
			cmdDispatcher.initCmdTable();
		}

	}

}
