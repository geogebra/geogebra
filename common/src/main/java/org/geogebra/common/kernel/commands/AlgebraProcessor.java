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

import org.geogebra.common.io.MathMLParser;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.KernelCAS;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoDependentBoolean;
import org.geogebra.common.kernel.algos.AlgoDependentConic;
import org.geogebra.common.kernel.algos.AlgoDependentFunction;
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
import org.geogebra.common.kernel.arithmetic.EquationValue;
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
import org.geogebra.common.kernel.arithmetic.Traversing.CollectUndefinedVariables;
import org.geogebra.common.kernel.arithmetic.Traversing.CommandReplacer;
import org.geogebra.common.kernel.arithmetic.Traversing.FVarCollector;
import org.geogebra.common.kernel.arithmetic.Traversing.ReplaceUndefinedVariables;
import org.geogebra.common.kernel.arithmetic.Traversing.VariableReplacer;
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
import org.geogebra.common.kernel.geos.GeoDummyVariable;
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
import org.geogebra.common.kernel.geos.GeoVec2D;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.implicit.AlgoDependentImplicitPoly;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.kernel.parser.ParserInterface;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.Unicode;
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

	private MyStringBuffer xBracket = null, yBracket = null, zBracket = null,
			closeBracket = null;

	private boolean vectorsEnabled = true;

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
	 * @param info
	 *            flag for output label
	 * @return resulting geos
	 * @throws MyError
	 *             e.g. on syntax error
	 */
	final public GeoElement[] processCommand(Command c,
			EvalInfo info)
			throws MyError {
		return cmdDispatcher.processCommand(c, info);
	}

	/**
	 * @param c
	 *            command
	 * @param info
	 *            flag for output label
	 * @return simplified expression
	 * @throws MyError
	 *             error
	 */
	final public ExpressionValue simplifyCommand(Command c,
			EvalInfo info)
			throws MyError {
		return cmdDispatcher.simplifyCommand(c, info);
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
					KernelCAS.dependentCasCell(casCell);
					needsRedefinition = false;
					needsConsUpdate = !isLastRow;
				} else {
					// existing casCell with possible twinGeo
					needsRedefinition = true;
				}
			}
		} else {

			// dependent -> free, e.g. m := c+2 -> m := 7
			// algorithm will be removed through redefinition
			// OR
			// dependent -> dependent, e.g. m := c+2 -> m := c+d
			// we already have an algorithm but need redefinition
			// in order to move it to the right place in construction list
			needsRedefinition = true;

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
	 * @param handler
	 *            error handler
	 * 
	 * @param callback
	 *            receives changed geo
	 */
	public void changeGeoElement(GeoElementND geo, String newValue,
			boolean redefineIndependent, boolean storeUndoInfo,
			ErrorHandler handler, AsyncOperation<GeoElementND> callback) {

		try {
			changeGeoElementNoExceptionHandling(geo, newValue,
					redefineIndependent, storeUndoInfo, callback,
					handler);
		} catch (MyError e) {
			ErrorHelper.handleError(e, newValue, loc, handler);
		} catch (Exception e) {
			handler.showError(e.getMessage());
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
	 * @param callback
	 *            what to do with the changed geo
	 * @param handler
	 *            decides how to handle exceptions
	 * @throws Exception
	 *             e.g. parse exception or circular definition
	 * @throws MyError
	 *             eg assignment to fixed object
	 *
	 */
	public void changeGeoElementNoExceptionHandling(GeoElementND geo,
			String newValue, boolean redefineIndependent, boolean storeUndoInfo,
 AsyncOperation<GeoElementND> callback,
			ErrorHandler handler)
			throws Exception, MyError {

		try {
			ValidExpression ve = parser.parseGeoGebraExpression(newValue);
			if ("X".equals(ve.getLabel())) {
				ve = getParamProcessor().checkParametricEquationF(ve, ve, cons,
						new EvalInfo(!cons.isSuppressLabelsActive()));
			}
			changeGeoElementNoExceptionHandling(geo, ve,
					redefineIndependent, storeUndoInfo, callback, handler);
		} catch (Exception e) {
			Log.debug("EXCEPTION" + e.getMessage() + ":" + newValue);
			e.printStackTrace();
			throw new Exception(loc.getError("InvalidInput") + ":\n" + newValue);
		} catch (MyError e) {
			e.printStackTrace();
			throw e;
		} catch (Error e) {
			Log.debug("ERROR" + e.getMessage() + ":" + newValue);
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
	 * @param callback
	 *            what to do with the changed geo
	 * @param handler
	 *            decides how to handle exceptions
	 */
	public void changeGeoElementNoExceptionHandling(final GeoElementND geo,
			ValidExpression newValue, boolean redefineIndependent,
			final boolean storeUndoInfo,
			final AsyncOperation<GeoElementND> callback, ErrorHandler handler) {
		String oldLabel, newLabel;
		GeoElementND[] result;
		EvalInfo info = new EvalInfo(!cons.isSuppressLabelsActive(),
				redefineIndependent);

		app.getCompanion().storeViewCreators();
		oldLabel = geo.getLabel(StringTemplate.defaultTemplate);
		// need to check isDefined() eg redefine FitPoly[{A, B, C, D, E, F,
		// G, H, I}, 22] to FitPoly[{A, B, C, D, E, F, G, H, I}, 2]
		/*
		 * if (geo instanceof GeoFunction && ((GeoFunction) geo).isDefined()) {
		 * cons.registerFunctionVariable(((GeoFunction) geo).getFunction()
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
			final boolean listeners = app.getScriptManager().hasListeners();
			app.getScriptManager().disableListeners();
			AsyncOperation<GeoElementND[]> changeCallback = new AsyncOperation<GeoElementND[]>() {

				@Override
				public void callback(GeoElementND[] obj) {
					if (listeners) {
						geo.updateCascade();
						}
					if (obj != null) {
						app.getCompanion().recallViewCreators();
						if (storeUndoInfo)
							app.storeUndoInfo();
						if (obj.length > 0) {
							callback.callback(obj[0]);
						}
					}

				}
			};
			app.getScriptManager().enableListeners();

			processAlgebraCommandNoExceptionHandling(newValue,
					false, handler, true, changeCallback, info);

			cons.registerFunctionVariable(null);
			return;
		} else if (cons.isFreeLabel(newLabel)) {
			newValue.setLabel(oldLabel);
			// rename to oldLabel to enable overwriting
			result = processAlgebraCommandNoExceptionHandling(newValue, false,
					handler, true, null, info);
			result[0].setLabel(newLabel); // now we rename
			app.getCompanion().recallViewCreators();
			if (storeUndoInfo)
				app.storeUndoInfo();
			if (result.length > 0) {
				callback.callback(result[0]);
			}
		} else {
			String str[] = { "NameUsed", newLabel };
			throw new MyError(loc, str);
				}

		cons.registerFunctionVariable(null);

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
	public GeoElementND[] processAlgebraCommand(String cmd, boolean storeUndo) {

		try {
			return processAlgebraCommandNoExceptionHandling(cmd, storeUndo,
					app.getErrorHandler(), false, null);
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
	public GeoElementND[] processAlgebraCommandNoExceptions(String cmd,
			boolean storeUndo) {

		try {
			return processAlgebraCommandNoExceptionHandling(cmd, storeUndo,
					ErrorHelper.silent(), false, null);
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
	public GeoElementND[] processAlgebraCommandNoExceptionsOrErrors(String str,
			boolean storeUndo) {

		try {
			return processAlgebraCommandNoExceptionHandling(str, storeUndo,
					ErrorHelper.silent(), false, null);
		} catch (Exception e) {
			return null;
		} catch (MyError e) {
			return null;
		}
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
	 * @param handler
	 *            decides how to handle exceptions
	 * @param autoCreateSliders
	 *            whether to show a popup for undefined variables
	 * @param callback0
	 *            callback after the geos are created
	 * @return resulting geos
	 */
	public GeoElementND[] processAlgebraCommandNoExceptionHandling(
			final String cmd, final boolean storeUndo,
			final ErrorHandler handler,
			boolean autoCreateSliders,
			final AsyncOperation<GeoElementND[]> callback0)
	{

		// both return this and call callback0 in case of success!
		GeoElementND[] rett;

		if (cmd.length() > 0 && cmd.charAt(0) == '<' && cmd.startsWith("<math")) {
			rett = parseMathml(cmd, storeUndo, handler,
					autoCreateSliders, callback0);
			if (rett != null && callback0 != null) {
				callback0.callback(rett);
			}
			return rett;
		}
		ValidExpression ve;
		try {
			ve = parser.parseGeoGebraExpression(cmd);
			GeoCasCell casEval = checkCasEval(ve.getLabel(), cmd, ve);
			if (casEval != null) {
				if (callback0 != null) {
					callback0.callback(array(casEval));
				}
				return new GeoElement[0];
			}
			return processAlgebraCommandNoExceptionHandling(ve, storeUndo,
					handler,
					autoCreateSliders, callback0,
					new EvalInfo(!cons.isSuppressLabelsActive(), true));

		} catch (Exception e) {
			e.printStackTrace();
			ErrorHelper.handleException(e, app, handler);
		} catch (MyError e) {
			ErrorHelper.handleError(e, cmd, loc, handler);
		} catch (Error e) {
			// Sometimes TokenManagerError comes from parser
			ErrorHelper.handleException(new Exception(e), app, handler);
		}
		if (callback0 != null) {
			callback0.callback(null);
		}
		return null;

	}

	/**
	 * @param ve
	 *            valid expression (already pasted)
	 * @param storeUndo
	 *            true to make undo step
	 * @param handler
	 *            defines how to deal with exceptions
	 * @param autoCreateSliders
	 *            whether to show a popup for undefined variables
	 * @param callback0
	 *            callback after the geos are created
	 * @param info
	 *            flags: whether to label output, whether independent may be
	 *            redefined etc
	 * @return resulting geos
	 */
	public GeoElementND[] processAlgebraCommandNoExceptionHandling(
			ValidExpression ve, final boolean storeUndo,
			final ErrorHandler handler,
			boolean autoCreateSliders,
			final AsyncOperation<GeoElementND[]> callback0,
			final EvalInfo info) {
		// collect undefined variables
		CollectUndefinedVariables collecter = new Traversing.CollectUndefinedVariables();
		ve.traverse(collecter);
		final TreeSet<String> undefinedVariables = collecter.getResult();


		GeoElement[] ret = getParamProcessor().checkParametricEquation(ve,
				undefinedVariables, autoCreateSliders, callback0,
				new EvalInfo(!cons.isSuppressLabelsActive()));
		if (ret != null) {
			if (storeUndo) {
				app.storeUndoInfo();
			}
			if (callback0 != null) {
				callback0.callback(ret);
			}
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
				ValidExpression cp = ve.deepCopy(kernel);
				cp.setLabels(ve.getLabels());
				geoElements = processValidExpression(cp,
						info);
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
					GeoElementND[] rett = tryReplacingProducts(ve, handler);
					if (callback0 != null && rett != null) {
						callback0.callback(rett);
					}
					return rett;
				}

				// boolean autoCreateSlidersAnswer = false;

				// "Create sliders for a, b?" Create Sliders / Cancel
				// Yes: create sliders and draw line
				// No: go back into input bar and allow user to change input
				final Localization loc2 = loc;

					AsyncOperation<String[]> callback = null;

					// final FunctionVariable fvX2 = fvX;
					final ValidExpression ve2 = ve;

					callback = new AsyncOperation<String[]>() {

						@Override
						public void callback(String[] dialogResult) {
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
						}
								try {
									geos = processValidExpression(storeUndo,
											handler,
											ve2, info);
								} catch (MyError ee) {
									ErrorHelper.handleError(ee,
											ve2.toString(
											StringTemplate.defaultTemplate),
											loc2, handler);
									return;
								} catch (Exception ee) {
									ErrorHelper.handleException(ee, app,
											handler);
									return;
								}

							if (callback0 != null) {
								callback0.callback(geos);
								}
						}

					};
				boolean autoCreateSlidersAnswer = handler
						.onUndefinedVariables(sb.toString(), callback);

					if (!autoCreateSlidersAnswer) {
						return null;
					}
				}


			// Log.debug("list of variables: "+sb.toString());

			// ==========================
			// step5: replace undefined variables
			// ==========================
			replaceUndefinedVariables(ve, new TreeSet<GeoNumeric>(), null);
		}

		// process ValidExpression (built by parser)
		GeoElement[] geos = processValidExpression(storeUndo, handler, ve,
				info);
		if (callback0 != null)
			callback0.callback(geos);
		return geos;

	}

	/**
	 * create valid expression (if possible) from command string
	 * 
	 * @param cmd
	 *            command
	 * @return valid expression
	 * @throws Exception
	 *             exception
	 */
	public ValidExpression getValidExpressionNoExceptionHandling(
			final String cmd) throws Exception {

		return parser.parseGeoGebraExpression(cmd);
	}

	/**
	 * @param label
	 *            dollar label
	 * @param input
	 *            whole command including label
	 * @param parsed
	 *            parsed content of input
	 * @return cell
	 */
	public GeoCasCell checkCasEval(String label, String input,
			ValidExpression parsed) {
		if (label != null && label.startsWith("$")) {
			Integer row = -1;
			try {
				row = Integer.parseInt(label.substring(1)) - 1;
			} catch (Exception e) {
				// eg $A$1 label, do nothing
			}
			if (row < 0) {
				return null;
			}
			if (app.getGuiManager() != null) {
				app.getGuiManager().getCasView().cancelEditItem();
			}
			GeoCasCell cell = cons.getCasCell(row);
			if (cell == null) {
				cell = new GeoCasCell(cons);
			}
			String cmd = input == null ? parsed
					.toString(StringTemplate.defaultTemplate) : input;
			Log.debug(parsed);
			Log.debug(input + " MAY BE CAS");
			if (parsed != null && parsed.unwrap() instanceof Command) {
				Command c = (Command) parsed.unwrap();
				if ("Rename".equals(c.getName())) {
					cmd = "="
							+ c.getArgument(1)
									.traverse(CommandReplacer
											.getReplacer(kernel,
											true))
							.toString(StringTemplate.defaultTemplate) + ":="
							+ c.getArgument(0)
									.toString(StringTemplate.defaultTemplate);
				}
			}
			int colonPos = cmd.indexOf(':') + 1;
			int eqPos = cmd.indexOf('=') + 1;
			int prefixLength = eqPos > 0 ? (colonPos > 0 ? Math.min(colonPos,
					eqPos) : eqPos) : colonPos;
			if (cmd.charAt(prefixLength) == '=') {
				prefixLength++;
			}
			
			cell.setInput(cmd.substring(prefixLength));
			this.processCasCell(cell, false);
			return cell;
		}
		return null;
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

	private GeoElementND[] tryReplacingProducts(ValidExpression ve,
			ErrorHandler eh) {
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
		GeoElementND[] ret = null;
		try {
			ret = this.processValidExpression(ve2);
		} catch (MyError t) {
			ErrorHelper.handleError(t, null, loc, eh);
		} catch (Exception e) {
			ErrorHelper.handleException(e, app, eh);
		}
		return ret;
	}

	/**
	 * TODO figure out how to handle sliders here
	 * 
	 * @param autoCreateSliders
	 *            whether sliders should be autocreated
	 */
	private GeoElementND[] parseMathml(String cmd, final boolean storeUndo,
			ErrorHandler handler,
			boolean autoCreateSliders,
			final AsyncOperation<GeoElementND[]> callback0) {
		if (mathmlParserGGB == null) {
			mathmlParserGGB = new MathMLParser(true);
		}
		GeoElementND[] ret = null;
		try {
			String ggb = mathmlParserGGB.parse(cmd, false, true);
			RegExp assignment = RegExp.compile("^(\\w+) \\(x\\)=(.*)$");
			MatchResult lhs = assignment.exec(ggb);
			if (lhs != null) {
				ggb = lhs.getGroup(1) + "(x)=" + lhs.getGroup(2);
			}
			Log.debug(ggb);
			ret = this.processAlgebraCommandNoExceptionHandling(ggb, storeUndo,
					handler, false, callback0);
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
		return array(texAlgo.getOutput(0));
	}

	/**
	 * @param storeUndo
	 *            whether to create an undo point
	 * @param handler
	 *            handles exceptions
	 * @param ve
	 *            input expression
	 * @param info
	 *            processing information
	 * @return processed expression
	 */
	public GeoElement[] processValidExpression(boolean storeUndo,
			ErrorHandler handler, ValidExpression ve,
			EvalInfo info)
	{
		GeoElement[] geoElements = null;
		try {

			geoElements = processValidExpression(ve, info);
			if (storeUndo && geoElements != null)
				app.storeUndoInfo();
		} catch (MyError e) {
			e.printStackTrace();
			ErrorHelper.handleError(e,
					ve == null ? null
							: ve.toString(StringTemplate.defaultTemplate),
					loc, handler);
		} catch (Exception ex) {
			Log.debug("Exception" + ex.getLocalizedMessage());
			ErrorHelper.handleException(ex, app, handler);
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
		return evaluateToDouble(str, false, null);
	}

	/**
	 * Parses given String str and tries to evaluate it to a double. Returns
	 * Double.NaN if something went wrong.
	 * 
	 * @param str
	 *            string to process
	 * @param suppressErrors
	 *            false to show error messages (only stacktrace otherwise)
	 * @param forGeo
	 *            geo that can receive the value and definition
	 * @return result as double
	 */
	public double evaluateToDouble(String str, boolean suppressErrors,
			GeoNumeric forGeo) {
		try {
			ValidExpression ve = parser.parseExpression(str);
			ExpressionNode en = (ExpressionNode) ve;
			en.resolveVariables(new EvalInfo(false));
			NumberValue nv = (NumberValue) en
					.evaluate(StringTemplate.defaultTemplate);
			if (forGeo != null) {
				forGeo.setValue(nv.getDouble());

				// if forGeo is a slider, the value might be out of range
				// in which case we mustn't set the definition
				if (forGeo.getDouble() == nv.getDouble()) {
					forGeo.setDefinition(en);
				}
			}
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
	 * @param handler
	 *            takes care of errors
	 * @return resulting boolean
	 */
	public GeoBoolean evaluateToBoolean(String str, ErrorHandler handler) {
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		GeoBoolean bool = null;
		try {
			ValidExpression ve = parser.parseGeoGebraExpression(str);
			// A=B as comparison, not assignment
			if (ve.getLabel() != null) {
				ve = new ExpressionNode(kernel, new Variable(kernel,
						ve.getLabel()), Operation.EQUAL_BOOLEAN, ve);
				// A+B=C as comparison, not equation
			} else if (ve.unwrap() instanceof Equation) {
				Equation eq = (Equation) ve.unwrap();
				ve = new ExpressionNode(kernel, eq.getLHS(),
						Operation.EQUAL_BOOLEAN, eq.getRHS());
			}
			GeoElementND[] temp = processValidExpression(ve);

			// GGB-1043 GWT: can't rely on ClassCast Exception
			if (temp[0] instanceof GeoBoolean) {
				bool = (GeoBoolean) temp[0];
			} else {
				handler.showError(loc.getError("InvalidInput"));
			}
		} catch (Exception e) {
			ErrorHelper.handleException(e, app, handler);
		} catch (MyError e) {
			ErrorHelper.handleError(e, str, loc, handler);
		} catch (Error e) {
			e.printStackTrace();
			handler.showError(loc.getError("InvalidInput"));
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
			GeoElementND[] temp = processValidExpression(ve);
			// CAS in GeoGebraWeb dies badly if we don't handle this case
			// (Simon's hack):
			// list = (GeoList) temp[0];
			if (temp[0] instanceof GeoList) {
				list = (GeoList) temp[0];
			} else {
				Log.error("return value was not a list");
			}
		} catch (CircularDefinitionException e) {
			Log.debug("CircularDefinition");
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
		return evaluateToFunction(str, suppressErrors, false);
	}
	
	/**
	 * Parses given String str and tries to evaluate it to a GeoFunction Returns
	 * null if something went wrong. Michael Borcherds 2008-04-04
	 * 
	 * @param str
	 *            input string
	 * @param suppressErrors
	 *            false to show error messages (only stacktrace otherwise)
	 * @param revertArbconst
	 *            whether to replace c_1 back with arbconst(1)
	 * @return resulting function
	 */
	public GeoFunction evaluateToFunction(String str, boolean suppressErrors,
			boolean revertArbconst) {
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		GeoFunction func = null;
		try {
			ValidExpression ve = parser.parseGeoGebraExpression(str);
			String[] varName = kernel.getConstruction()
					.getRegisteredFunctionVariables();
			FunctionVariable[] fv = new FunctionVariable[varName.length];
			ExpressionNode exp = ve.wrap();
			replaceVariables(exp, varName, fv);
			if (revertArbconst) {
				exp = exp.traverse(new Traversing() {

					public ExpressionValue process(ExpressionValue ev) {
						if (ev instanceof Variable) {
							GeoElement geo = kernel
									.lookupLabel(((Variable) ev).getName());
							String[] parts = ((Variable) ev).getName()
									.split("_");
							if (geo == null && parts.length == 2) {
								try {
									int idx = Integer.parseInt(parts[1]
											.replace("{", "").replace("}", ""));
									return new ExpressionNode(kernel,
											new MyDouble(kernel, idx),
											Operation.ARBCONST, null);
								} catch (Exception e) {
									Log.debug("Invalid variable");
								}
							} else if (geo != null) {
								return geo;
							}
						}
						return ev;
					}
				}).wrap();
			}
			GeoElementND[] temp = processValidExpression(exp);

			if (temp[0].isGeoFunctionable()) {
				GeoFunctionable f = (GeoFunctionable) temp[0];
				func = f.getGeoFunction();
			} else if (!suppressErrors)
				app.showError("InvalidInput", str);

		} catch (CircularDefinitionException e) {
			Log.debug("CircularDefinition");
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
	 * @param argument
	 *            expression
	 * @param varName
	 *            variable names to be replaced
	 * @param fv
	 *            function variables
	 * @return number of replacements
	 */
	public int replaceVariables(ExpressionNode argument, String[] varName,
			FunctionVariable[] fv) {
		int rep = 0;
		for (int i = 0; i < varName.length; i++) {
			if (fv[i] == null) {
				fv[i] = new FunctionVariable(kernel, varName[i]);
			}
			rep += argument.replaceVariables(varName[i], fv[i]);
		}
		return rep;
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
			GeoElementND[] temp = processValidExpression(ve);

			if (temp[0] instanceof GeoFunctionNVar) {
				func = (GeoFunctionNVar) temp[0];
			} else if (temp[0] instanceof GeoFunction) {
				FunctionVariable[] funVars;
				if (((GeoFunction) temp[0]).isFunctionOfY()) {

					funVars = new FunctionVariable[] {
							new FunctionVariable(kernel, "x"),
							((GeoFunction) temp[0]).getFunction()
									.getFunctionVariable() };
				} else {
					funVars = new FunctionVariable[] {

							((GeoFunction) temp[0]).getFunction()
									.getFunctionVariable(),
							new FunctionVariable(kernel, "y") };
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
			Log.debug("CircularDefinition");
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
		return evaluateToNumeric(str, suppressErrors ? ErrorHelper.silent()
				: app.getDefaultErrorHandler());
	}

	/**
	 * Parses given String str and tries to evaluate it to a NumberValue Returns
	 * null if something went wrong.
	 * 
	 * @param str
	 *            string to parse
	 * @param handler
	 *            callback for handling errors
	 * @return resulting number
	 */
	public GeoNumberValue evaluateToNumeric(String str, ErrorHandler handler) {

		if (str == null || "".equals(str)) {
			ErrorHelper.handleInvalidInput(str, loc, handler);
			return new GeoNumeric(cons, Double.NaN);
		}

		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		GeoNumberValue num = null;
		try {
			ValidExpression ve = parser.parseGeoGebraExpression(str);
			GeoElementND[] temp = processValidExpression(ve);

			if (temp[0] instanceof GeoNumberValue) {
				num = (GeoNumberValue) temp[0];
			} else {
				num = new GeoNumeric(cons, Double.NaN);
				ErrorHelper.handleInvalidInput(str, loc, handler);
			}
		} catch (Exception e) {
			ErrorHelper.handleException(e, app, handler);
		} catch (MyError e) {
			e.printStackTrace();
			ErrorHelper.handleError(e, str, loc, handler);
		} catch (Error e) {
			e.printStackTrace();
			ErrorHelper.handleException(new Exception(e), app, handler);
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
	 * @param handler
	 *            error handler
	 * @param suppressLabels
	 *            true to suppress labeling
	 * @return resulting point
	 */
	public GeoPointND evaluateToPoint(String str, ErrorHandler handler,
			boolean suppressLabels) {
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		if (suppressLabels) {
			cons.setSuppressLabelCreation(true);
		}

		GeoPointND p = null;
		GeoElementND[] temp = null;
		try {
			ValidExpression ve = parser.parseGeoGebraExpression(str);
			if (ve instanceof ExpressionNode) {
				ExpressionNode en = (ExpressionNode) ve;
				en.setForcePoint();
			}

			temp = processValidExpression(ve);
			if (temp[0] instanceof GeoVectorND) {
				p = kernel.wrapInPoint((GeoVectorND) temp[0]);
			} else if (temp[0] instanceof GeoPointND) {
				p = (GeoPointND) temp[0];
			} else {
				handler.showError(loc.getError("VectorExpected"));
			}
		} catch (Exception e) {
			ErrorHelper.handleException(e, app, handler);
		} catch (MyError e) {
			ErrorHelper.handleError(e, str, loc, handler);
		} catch (Error e) {
			ErrorHelper.handleException(new Exception(e), app, handler);
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
		GeoElementND[] temp = null;
		try {
			ValidExpression ve = parser.parseGeoGebraExpression(str);
			temp = processValidExpression(ve);
			text = (GeoText) temp[0];
		} catch (CircularDefinitionException e) {
			if (showErrors) {
				Log.debug("CircularDefinition");
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
	public GeoElementND evaluateToGeoElement(String str, boolean showErrors) {
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		GeoElementND geo = null;
		try {
			ValidExpression ve = parser.parseGeoGebraExpression(str);
			GeoElementND[] temp = processValidExpression(ve);
			geo = temp[0];
		} catch (CircularDefinitionException e) {
			Log.debug("CircularDefinition");
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
		return processValidExpression(ve,
				new EvalInfo(!cons.isSuppressLabelsActive(), true));
	}

	/**
	 * processes valid expression.
	 * 
	 * @param ve
	 *            expression to process
	 * @param info
	 *            processing information
	 * @throws MyError
	 *             e.g. on wrong syntax
	 * @throws Exception
	 *             e.g. for circular definition
	 * @return resulting geos
	 */
	public GeoElement[] processValidExpression(ValidExpression ve,
			EvalInfo info)
			throws MyError, Exception {

		// check for existing labels
		String[] labels = ve.getLabels();
		GeoElement replaceable = getReplaceable(labels);

		GeoElement[] ret;
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		if (replaceable != null)
			cons.setSuppressLabelCreation(true);

		// we have to make sure that the macro mode is
		// set back at the end
		try {
			ret = doProcessValidExpression(ve, info);

			if (ret == null) { // eg (1,2,3) running in 2D
				Log.warn("Unhandled ValidExpression : " + ve);
				throw new MyError(loc, loc.getError("InvalidInput") + ":\n"
						+ ve);
			}
		} finally {
			cons.setSuppressLabelCreation(oldMacroMode);
		}

		processReplace(replaceable, ret, ve, info);

		return ret;
	}

	/**
	 * @param labels
	 *            output labels of a command/expr
	 * @return geo with same label as one of the outputs
	 */
	GeoElement getReplaceable(String[] labels) {
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
		return replaceable;
	}

	/**
	 * 
	 * @param replaceable
	 *            old geo with same label
	 * @param ret
	 *            result of processing
	 * @param ve
	 *            original expression
	 * @param info
	 *            evaluation flags
	 * @throws CircularDefinitionException
	 *             when circular definition occurs
	 */
	void processReplace(GeoElement replaceable, GeoElement[] ret,
			ValidExpression ve, EvalInfo info)
			throws CircularDefinitionException {
		// try to replace replaceable geo by ret[0]
		if (replaceable != null && ret.length > 0) {
			// a changeable replaceable is not redefined:
			// it gets the value of ret[0]
			// (note: texts are always redefined)
			if (!info.mayRedefineIndependent() && replaceable.isChangeable()
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
					// SPECIAL CASE: set value
					// new and old object are both independent and have same
					// type:
					// simply assign value and don't redefine
					if (replaceable.isIndependent() && ret[0].isIndependent()
							&& compatibleTypes(replaceable.getGeoClassType(),
									ret[0].getGeoClassType())) {
						if (replaceable instanceof GeoNumeric) {
							((GeoNumeric) replaceable).extendMinMax(ret[0]);
						}
						replaceable.set(ret[0]);
						if (replaceable instanceof GeoFunction
								&& !((GeoFunction) replaceable)
										.validate(true)) {
							replaceable.setUndefined();
						} else {
							replaceable.setDefinition(ret[0].getDefinition());
						}
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
							cell.setInput(vexp.toAssignmentString(
									StringTemplate.defaultTemplate,
									cell.getAssignmentType()));
							processCasCell(cell, false);
						} else {
							cons.replace(replaceable, newGeo);
						}
						// now all objects have changed
						// get the new object with same label as our result
						String newLabel = newGeo.isLabelSet()
								? newGeo.getLabelSimple()
								: replaceable.getLabelSimple();
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
	 * @param info
	 *            flags for processing
	 * @return array of geos
	 * @throws MyError
	 *             if syntax error occurs
	 * @throws CircularDefinitionException
	 *             if circular definition occurs
	 */
	public final GeoElement[] doProcessValidExpression(final ValidExpression ve,
			EvalInfo info)
			throws MyError, CircularDefinitionException {
		GeoElement[] ret = null;

		if (ve instanceof ExpressionNode) {
			ret = processExpressionNode((ExpressionNode) ve, info);
			if (ret != null && ret.length > 0
					&& ret[0] instanceof GeoScriptAction) {
				if (info.isScripting()) {
					((GeoScriptAction) ret[0]).perform();
				}
				return new GeoElement[] {};
			} else if (ret != null && ret.length > 0
					&& ret[0] instanceof GeoList) {
				int actions = ((GeoList) ret[0]).performScriptActions(info);
				if (actions > 0) {
					ret[0].remove();
					return new GeoElement[] {};
				}
			}
		}

		// Command
		else if (ve instanceof Command) {
			ret = cmdDispatcher.processCommand((Command) ve,
					new EvalInfo(true));
		}

		// Equation in x,y (linear or quadratic are valid): line or conic
		else if (ve instanceof Equation) {
			ret = processEquation((Equation) ve, ve.wrap(), info);
		}

		// explicit Function in one variable
		else if (ve instanceof Function) {
			ret = processFunction((Function) ve, info);
		}

		// explicit Function in multiple variables
		else if (ve instanceof FunctionNVar) {
			ret = processFunctionNVar((FunctionNVar) ve, info);
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
	 * @param info
	 *            processing information
	 * @return GeoFunction
	 */
	public final GeoElement[] processFunction(Function fun, EvalInfo info) {
		String varName = fun.getVarString(StringTemplate.defaultTemplate);
		if (varName.equals(
				Unicode.thetaStr)
				&& !kernel.getConstruction().isRegistredFunctionVariable(
						Unicode.thetaStr)
				&& fun.getExpression().evaluatesToNumber(true)) {
			String label = fun.getLabel();
			ValidExpression ve = new MyVecNode(kernel, fun.getExpression(),
 fun
					.getFunctionVariable().wrap());
			((MyVecNode) ve).setMode(Kernel.COORD_POLAR);
			// TODO the "r" check is there to allow r=theta in the
			// future
			if (!"r".equals(label)) {
				ve.setLabel(label);
			}
			ExpressionNode exp = ve
					.deepCopy(kernel)
					.traverse(
							VariableReplacer.getReplacer(varName,
									fun.getFunctionVariable(), kernel))
					.wrap();
			exp.resolveVariables(info);
			GeoElement[] ret = getParamProcessor().processParametricFunction(
					exp,
					exp.evaluate(StringTemplate.defaultTemplate),
					new FunctionVariable[] { fun.getFunctionVariable() },
					"X".equals(ve.getLabel()) ? null : ve.getLabel(), info);
			if (ret != null) {
				return ret;
			}
		}
		if (!fun.initFunction(info.isSimplifyingIntegers())) {
			return getParamProcessor().processParametricFunction(
					fun.getExpression(),
					fun
					.getExpression().evaluate(StringTemplate.defaultTemplate),
					new FunctionVariable[] { fun.getFunctionVariable() },
					fun.getLabel(), info);
		}

		String label = fun.getLabel();
		GeoFunction f;

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
						f = new GeoInterval(cons, fun);
					} else {
						f = dependentInterval(fun);
					}
					f.setLabel(label);
					return array(f);

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
				f = (GeoFunction) dependentGeoCopy(
						((GeoFunctionable) left).getGeoFunction());
				f.setLabel(label);
				return array(f);
			}
		}

		if (isIndependent) {
			f = new GeoFunction(cons, fun, info.isSimplifyingIntegers());

		} else {
			f = kernel.getAlgoDispatcher().DependentFunction(fun, info);
			if (label == null) {
				label = AlgoDependentFunction.getDerivativeLabel(fun);
			}

		}

		if (f.validate(label == null)) {
			f.setLabel(label);
			return array(f);
		}
		f.remove();
		throw new MyError(loc, "InvalidFunction");

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
		case IF:
			return new ExpressionNode(kernel, exp.getLeft().deepCopy(kernel),
					Operation.IF, computeCoord(exp.getRightTree(), i));
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
					&& left.isConstant())
				dir = -1;

		}
		return dir;
	}

	/**
	 * Interval dependent on coefficients of arithmetic expressions with
	 * variables, represented by trees. e.g. x > a && x < b
	 */
	final private GeoFunction dependentInterval(Function fun) {
		AlgoDependentInterval algo = new AlgoDependentInterval(cons, fun);
		GeoFunction f = algo.getFunction();
		return f;
	}

	final private GeoElement dependentGeoCopy(
			GeoElement origGeoNode) {
		AlgoDependentGeoCopy algo = new AlgoDependentGeoCopy(cons, 
				origGeoNode);
		return algo.getGeo();
	}

	/**
	 * Wraps given functionNVar into GeoFunctionNVar, if dependent,
	 * AlgoDependentFunctionNVar is created.
	 * 
	 * @param fun
	 *            function
	 * @param info
	 *            processing information
	 * @return GeoFunctionNVar
	 */
	public GeoElement[] processFunctionNVar(FunctionNVar fun, EvalInfo info) {
		if (!fun.initFunction(info.isSimplifyingIntegers())) {
			return getParamProcessor().processParametricFunction(
					fun.getExpression(),
					fun.getExpression()
							.evaluate(StringTemplate.defaultTemplate),
					fun.getFunctionVariables(), fun.getLabel(), info);
		}


		String label = fun.getLabel();
		GeoFunctionNVar gf;

		GeoElement[] vars = fun.getGeoElementVariables();
		boolean isIndependent = (vars == null || vars.length == 0);

		if (isIndependent) {
			gf = new GeoFunctionNVar(cons, fun, info.isSimplifyingIntegers());
			gf.setLabel(label);
		} else {
			gf = dependentFunctionNVar(label, fun);
		}
		if (!gf.validate(label == null)) {
			gf.remove();
			throw new MyError(loc, "InvalidInput");
		}
		return array(gf);
	}

	/**
	 * Multivariate Function depending on coefficients of arithmetic expressions
	 * with variables, e.g. f(x,y) = a x^2 + b y^2
	 */
	final private GeoFunctionNVar dependentFunctionNVar(String label,
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
	 * @param def
	 *            definition node (not same as equation in case of list1(2))
	 * @param info
	 *            processing information
	 * @return line, conic, implicit poly or plane
	 * @throws MyError
	 *             e.g. for invalid operation
	 */
	public final GeoElement[] processEquation(Equation equ, ExpressionNode def,
			EvalInfo info)
			throws MyError {
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
		if ((lhs instanceof Variable || lhs instanceof GeoDummyVariable)
				&& kernel.lookupLabel("X") == null
				&& "X".equals(lhs.toString(StringTemplate.defaultTemplate))) {
			return getParamProcessor().processXEquation(equ, info);
		}
		if (lhs instanceof Variable
				&& kernel.lookupLabel(((Variable) lhs).getName()) == null) {
			GeoCasCell c = this.checkCasEval(((Variable) lhs).getName(), null,
					equ);
			if (c != null) {
				return new GeoElement[0];
			}
			equ.getRHS().setLabel(lhs.toString(StringTemplate.defaultTemplate));
			try {
				return processValidExpression(equ.getRHS());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (lhs instanceof MyDouble
				&& MyDouble.exactEqual(lhs.evaluateDouble(), MyMath.DEG)) {
			equ.getRHS().setLabel("deg");
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
		return processEquation(equ, def,
				kernel.getConstruction().isFileLoading());
	}

	/**
	 * @param equ
	 *            equation
	 * @param def
	 *            defining expression (either wrapped equation or something like
	 *            list1(1))
	 * @param allowConstant
	 *            true to allow equations like 2=3 or x=x, false to throw
	 *            MyError for those
	 * @return line, conic, implicit poly or plane
	 * @throws MyError
	 *             e.g. for invalid operation
	 */
	public final GeoElement[] processEquation(Equation equ, ExpressionNode def,
			boolean allowConstant) throws MyError {
		// AbstractApplication.debug("EQUATION: " + equ);
		// AbstractApplication.debug("NORMALFORM POLYNOMIAL: " +
		// equ.getNormalForm());

		equ.initEquation();

		// check no terms in z
		checkNoTermsInZ(equ);
		checkNoTheta(equ);

		if (equ.isFunctionDependent()) {
			return processImplicitPoly(equ, def);
		}
		int deg = equ.mayBePolynomial() && !equ.hasVariableDegree() ? equ
				.degree() : -1;
		// consider algebraic degree of equation
		// check not equation of eg plane
		switch (deg) {
		// linear equation -> LINE
		case 1:

			return processLine(equ, def);

			// quadratic equation -> CONIC
		case 2:
			return processConic(equ, def);
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
				return processFunction(fun,
						new EvalInfo(!cons.isSuppressLabelsActive()));
			}
			return processImplicitPoly(equ, def);
		}

	}

	private void checkNoTheta(Equation equ) {
		if (equ.getRHS().containsFreeFunctionVariable(Unicode.thetaStr) || equ
				.getRHS().containsFreeFunctionVariable(Unicode.thetaStr)) {
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
	 * @param def
	 *            definition expression (equation without any simplifications)
	 * @return resulting line
	 */
	protected GeoElement[] processLine(Equation equ, ExpressionNode def) {
		double a = 0, b = 0, c = 0;
		GeoLine line;
		String label = equ.getLabel();
		Polynomial lhs = equ.getNormalForm();
		boolean isExplicit = equ.isExplicit("y");
		boolean isIndependent = lhs.isConstant();
		if (isIndependent) {
			// get coefficients
			a = lhs.getCoeffValue("x");
			b = lhs.getCoeffValue("y");
			c = lhs.getCoeffValue("");
			line = new GeoLine(cons, a, b, c);
		} else {
			line = dependentLine(equ);
		}
		line.setDefinition(def);
		if (isExplicit) {
			line.setToExplicit();
		}
		line.showUndefinedInAlgebraView(true);
		line.setLabel(label);

		return array(line);
	}

	/**
	 * Line dependent on coefficients of arithmetic expressions with variables,
	 * represented by trees. e.g. y = k x + d
	 */
	final private GeoLine dependentLine(Equation equ) {
		AlgoDependentLine algo = new AlgoDependentLine(cons, equ);
		GeoLine line = algo.getLine();
		return line;
	}

	/**
	 * @param equ
	 *            equation
	 * @param def
	 *            definition expression
	 * @return resulting conic
	 */
	public GeoElement[] processConic(Equation equ, ExpressionNode def) {
		double a = 0, b = 0, c = 0, d = 0, e = 0, f = 0;
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
			conic = new GeoConic(cons, coeffs);

		} else {
			conic = dependentConic(equ);
		}

		if (isExplicit) {
			conic.setToExplicit();
		} else if (isSpecific
				|| conic.getType() == GeoConicNDConstants.CONIC_CIRCLE) {
			conic.setToSpecific();
		}
		conic.setDefinition(def);
		conic.setLabel(label);

		return array(conic);
	}

	/**
	 * Conic dependent on coefficients of arithmetic expressions with variables,
	 * represented by trees.
	 */
	final private GeoConic dependentConic(Equation equ) {
		AlgoDependentConic algo = new AlgoDependentConic(cons, equ);
		GeoConic conic = algo.getConic();
		return conic;
	}

	/**
	 * @param equ
	 *            equation
	 * @param definition
	 *            definition node (not same as equation in case of list1(2))
	 * @return resulting implicit polynomial
	 */
	protected GeoElement[] processImplicitPoly(Equation equ,
			ExpressionNode definition) {
		String label = equ.getLabel();
		Polynomial lhs = equ.getNormalForm();
		boolean isIndependent = !equ.isFunctionDependent() && lhs.isConstant()
				&& !equ.hasVariableDegree();
		GeoImplicit poly;
		GeoElement geo = null;
		boolean is3d = equ.isForcedSurface() || equ.isForcedQuadric()
				|| equ.isForcedPlane();
		if (isIndependent || is3d) {
			poly = new GeoImplicitCurve(cons, equ);
			poly.setDefinition(equ.wrap());
			geo = poly.toGeoElement();
			if (is3d) {
				geo.setUndefined();
			}
		} else {
			AlgoDependentImplicitPoly algo = new AlgoDependentImplicitPoly(
					cons, equ, definition, true);

			geo = algo.getGeo(); // might also return
			// Line or Conic
			geo.setDefinition(definition);
		}
		// AbstractApplication.debug("User Input: "+equ);
		geo.setLabel(label);
		return array(geo);
	}


	/**
	 * @param node
	 *            expression
	 * @param info
	 *            processing information
	 * @return resulting geos
	 * @throws MyError
	 *             on invalid operation
	 */
	public final GeoElement[] processExpressionNode(ExpressionNode node,
			EvalInfo info)
			throws MyError {
		ExpressionNode n = node;
		// command is leaf: process command
		if (n.isLeaf()) {
			ExpressionValue leaf = n.getLeft();
			if (leaf instanceof Command) {
				Command c = (Command) leaf;
				c.setLabels(n.getLabels());
				return cmdDispatcher.processCommand(c, info);
			} else if (leaf instanceof Equation) {
				Equation eqn = (Equation) leaf;
				eqn.setLabels(n.getLabels());
				return processEquation(eqn, n, info);
			} else if (leaf instanceof Function) {
				Function fun = (Function) leaf;
				fun.setLabels(n.getLabels());
				return processFunction(fun, info);
			} else if (leaf instanceof FunctionNVar) {
				FunctionNVar fun = (FunctionNVar) leaf;
				fun.setLabels(n.getLabels());
				return processFunctionNVar(fun, info);
			}

		}
		ExpressionValue eval; // ggb3D : used by AlgebraProcessor3D in
		// extended processExpressionNode

		// ELSE: resolve variables and evaluate expressionnode
		n.resolveVariables(info);
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
				&& !myNode.getOperation().equals(Operation.ELEMENT_OF)
				&& !myNode.getOperation().equals(Operation.IF_ELSE)) {
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
			return processNumber(n, eval, true);
		else if (eval instanceof VectorValue)
			return processPointVector(n, eval);
		else if (eval instanceof Vector3DValue)
			return processPointVector3D(n, eval);
		else if (eval instanceof TextValue)
			return processText(n, eval);
		else if (eval instanceof MyList) {
			return processList(n, (MyList) eval);
		} else if (eval instanceof EquationValue) {
			Equation eq = ((EquationValue) eval).getEquation();
			eq.setFunctionDependent(true);
			eq.setLabel(n.getLabel());
			return processEquation(eq, n, info);
		} else if (eval instanceof Function) {
			return processFunction((Function) eval, info);
		} else if (eval instanceof FunctionNVar) {

			return processFunctionNVar((FunctionNVar) eval, info);
		}
		// we have to process list in case list=matrix1(1), but not when
		// list=list2
		else if (eval instanceof GeoList && myNode.hasOperations()) {
			return processList(n, ((GeoList) eval).getMyList());
		} else if (eval.isGeoElement()) {

			// e.g. B1 = A1 where A1 is a GeoElement and B1 does not exist yet
			// create a copy of A1
			if (n.getLabel() != null || dollarLabelFound) {
				return array(dependentGeoCopy(n.getLabel(), n));
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
		Log.debug("Unhandled ExpressionNode: " + eval + ", " + eval.getClass());
		return null;
	}

	/**
	 * @param n
	 *            expression node
	 * @param evaluate
	 *            evaluated node
	 * @param needsLabel
	 *            whether to call setLabel
	 * @return value
	 */
	GeoElement[] processNumber(ExpressionNode n, ExpressionValue evaluate,
			boolean needsLabel) {
		GeoElement ret;
		boolean isIndependent = !n.inspect(Inspecting.dynamicGeosFinder);
		MyDouble val = ((NumberValue) evaluate).getNumber();
		boolean isAngle = val.isAngle();
		double value = val.getDouble();

		if (isIndependent) {
			if (isAngle) {
				ret = new GeoAngle(cons, value, AngleStyle.UNBOUNDED);
			} else {
				ret = new GeoNumeric(cons, value);
			}
			ret.setDefinition(n);

		} else {
			ret = dependentNumber(n, isAngle, evaluate).toGeoElement();
		}

		if (n.isForcedFunction()) {
			ret = ((GeoFunctionable) ret).getGeoFunction();
		}
		if (needsLabel) {
			String label = n.getLabel();
			ret.setLabel(label);
		} else {
			cons.removeFromConstructionList(ret);
		}
		return array(ret);
	}

	/**
	 * Number dependent on arithmetic expression with variables, represented by
	 * a tree. e.g. t = 6z - 2
	 */
	final private GeoNumberValue dependentNumber(
			ExpressionNode root,
			boolean isAngle, ExpressionValue evaluate) {
		AlgoDependentNumber algo = new AlgoDependentNumber(cons, root,
				isAngle, evaluate);
		GeoNumberValue number = algo.getNumber();
		return number;
	}

	private GeoElement[] processList(ExpressionNode n, MyList evalList) {
		String label = n.getLabel();

		GeoElement ret;

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
				GeoElement[] results = processExpressionNode(en,
						new EvalInfo(false));
				GeoElement geo = results[0];

				// add to list
				geoElements.add(geo);
				if (geo.isLabelSet() || !geo.isIndependent())
					isIndependent = false;
			}
			cons.setSuppressLabelCreation(oldMacroMode);

			// Create GeoList object
			ret = kernel.getAlgoDispatcher().List(label, geoElements,
					isIndependent);
			if (!evalList.isDefined()) {
				ret.setUndefined();
				ret.updateRepaint();
			}
			ret.setDefinition(n);
		}

		// operations and variables are present
		// e.g. {3, 2, 1} + {a, b, 2}
		else {
			ret = listExpression(n);
			ret.setLabel(label);
		}

		return array(ret);
	}

	/**
	 * Creates a dependent list object with the given label, e.g. {3, 2, 1} +
	 * {a, b, 2}
	 * 
	 * @param root
	 *            expression defining the dependent list
	 * @return resulting list
	 */
	final public GeoList listExpression(ExpressionNode root) {
		AlgoDependentListExpression algo = new AlgoDependentListExpression(
				cons, root);
		return algo.getList();
	}

	private GeoElement[] processText(ExpressionNode n, ExpressionValue evaluate) {
		GeoElement ret;
		String label = n.getLabel();

		boolean isIndependent = n.isConstant();

		if (isIndependent) {
			MyStringBuffer val = ((TextValue) evaluate).getText();
			ret = text(val.toValueString(StringTemplate.defaultTemplate));
		} else {
			ret = dependentText(n);
		}
		ret.setLabel(label);
		return array(ret);
	}

	/**
	 * Text dependent on coefficients of arithmetic expressions with variables,
	 * represented by trees. e.g. text = "Radius: " + r
	 */
	final private GeoText dependentText(ExpressionNode root) {
		AlgoDependentText algo = new AlgoDependentText(cons, root, true);
		GeoText t = algo.getGeoText();
		return t;
	}

	/**
	 * @param text
	 *            content of the text
	 * @return resulting text
	 */
	final public GeoText text(String text) {
		GeoText t = new GeoText(cons);
		t.setTextString(text);
		return t;
	}

	private GeoElement[] processBoolean(ExpressionNode n,
			ExpressionValue evaluate) {
		GeoBoolean ret;
		String label = n.getLabel();

		boolean isIndependent = !n.inspect(Inspecting.dynamicGeosFinder);

		if (isIndependent) {

			ret = new GeoBoolean(cons);
			ret.setValue(((BooleanValue) evaluate)
					.getBoolean());
			ret.setDefinition(n);

		} else {
			ret = (new AlgoDependentBoolean(cons, n)).getGeoBoolean();
		}
		ret.setLabel(label);
		return array(ret);
	}

	private static boolean isEquation(ExpressionValue ev) {
		return ev.unwrap() instanceof EquationValue
				&& !(ev.unwrap() instanceof NumberValue);
	}
	private GeoElement[] processPointVector(ExpressionNode n,
			ExpressionValue evaluate) {
		String label = n.getLabel();
		if(evaluate instanceof MyVecNode){
			ExpressionValue x = ((MyVecNode) evaluate).getX();
			ExpressionValue y = ((MyVecNode) evaluate).getY();
			if ( isEquation(x)			&& isEquation(y)) {
				return processEquationIntersect(x, y);
			}
		}
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
				ret[0] = dependentVector(label, n);
			else
				ret[0] = dependentPoint(label, n, complex);
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

	private GeoElement[] processEquationIntersect(ExpressionValue x,
			ExpressionValue y) {
		if (y.unwrap() instanceof Equation && x.unwrap() instanceof Equation){
			boolean yHasZ = ((Equation) y.unwrap())
					.containsFreeFunctionVariable("z");
			boolean xHasZ = ((Equation) x.unwrap())
					.containsFreeFunctionVariable("z");
			if (xHasZ != yHasZ) {
				Equation needsFix = (Equation) (xHasZ ? y.unwrap()
						: x.unwrap());
				ExpressionNode lhs = needsFix.getLHS()
						.plus(new ExpressionNode(kernel,
								new MyDouble(kernel, 0), Operation.MULTIPLY,
								new FunctionVariable(kernel, "z")));
				needsFix.setLHS(lhs);
			}
		}
		Command inter = new Command(kernel, "Intersect", false);
		inter.addArgument(x.wrap());
		inter.addArgument(y.wrap());
		return processCommand(inter, new EvalInfo(true));
	}

	/**
	 * Point dependent on arithmetic expression with variables, represented by a
	 * tree. e.g. P = (4t, 2s)
	 */
	final private GeoPoint dependentPoint(String label, ExpressionNode root,
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
	final private GeoVector dependentVector(String label, ExpressionNode root) {
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
	final private GeoElement dependentGeoCopy(String label,
			ExpressionNode origGeoNode) {
		AlgoDependentGeoCopy algo = new AlgoDependentGeoCopy(cons,
				origGeoNode);
		algo.getGeo().setLabel(label);
		return algo.getGeo();
	}

	/**
	 * Show error dialog
	 * 
	 * @param key
	 *            key for error.properties
	 */
	public void showError(String key) {
		app.showError(key);
	}

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

	/**
	 * @return reference to kernel
	 */
	public Kernel getKernel() {
		return kernel;
	}

	private static GeoElement[] array(GeoElement geo) {
		return new GeoElement[] { geo };
	}

	/**
	 * @param geoConic
	 *            element represented by equation
	 * @return equation of the element
	 */
	public Equation parseEquation(GeoElementND geoConic) {
		try {
			return (Equation) kernel.getParser().parseGeoGebraExpression(
					geoConic.toValueString(StringTemplate.maxPrecision));
		} catch (Exception e) {
			// could be ParseException or Classcast Exception
			// https://play.google.com/apps/publish/?dev_acc=05873811091523087820#ErrorClusterDetailsPlace:p=org.geogebra.android&et=CRASH&lr=LAST_7_DAYS&ecn=java.lang.StringIndexOutOfBoundsException&tf=String.java&tc=java.lang.String&tm=startEndAndLength&nid&an&c&s=new_status_desc&ed=0
			e.printStackTrace();
		}
		return new Equation(kernel, new ExpressionNode(kernel, Double.NaN),
				new ExpressionNode(kernel, Double.NaN));

	}

	/**
	 * @param enable
	 *            whether commands should be enabled
	 */
	public void setCommandsEnabled(boolean enable) {
		this.vectorsEnabled = enable;
		cmdDispatcher.setEnabled(enable);

	}

	/**
	 * @return whether vector parsing is enabled
	 */
	public boolean enableVectors() {
		return vectorsEnabled;
	}

	/**
	 * @return whether commands dispatching is enabled
	 */
	public boolean isCommandsEnabled() {
		return cmdDispatcher.isEnabled();
	}
}
