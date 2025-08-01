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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.gui.view.algebra.AlgebraOutputFormat;
import org.geogebra.common.io.MathMLParser;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.EquationBehaviour;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.KernelCAS;
import org.geogebra.common.kernel.LinearEquationRepresentable;
import org.geogebra.common.kernel.QuadraticEquationRepresentable;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoDependentBoolean;
import org.geogebra.common.kernel.algos.AlgoDependentConic;
import org.geogebra.common.kernel.algos.AlgoDependentEquationList;
import org.geogebra.common.kernel.algos.AlgoDependentFunction;
import org.geogebra.common.kernel.algos.AlgoDependentFunctionNVar;
import org.geogebra.common.kernel.algos.AlgoDependentGeoCopy;
import org.geogebra.common.kernel.algos.AlgoDependentLine;
import org.geogebra.common.kernel.algos.AlgoDependentListExpression;
import org.geogebra.common.kernel.algos.AlgoDependentNumber;
import org.geogebra.common.kernel.algos.AlgoDependentPoint;
import org.geogebra.common.kernel.algos.AlgoDependentText;
import org.geogebra.common.kernel.algos.AlgoDependentVector;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoLaTeX;
import org.geogebra.common.kernel.arithmetic.ArcTrigReplacer;
import org.geogebra.common.kernel.arithmetic.AssignmentType;
import org.geogebra.common.kernel.arithmetic.BooleanValue;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.CoordMultiplyReplacer;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVarCollector;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.MySpecialDouble;
import org.geogebra.common.kernel.arithmetic.MyStringBuffer;
import org.geogebra.common.kernel.arithmetic.MyVecNDNode;
import org.geogebra.common.kernel.arithmetic.MyVecNode;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.Polynomial;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.arithmetic.TextValue;
import org.geogebra.common.kernel.arithmetic.Traversing;
import org.geogebra.common.kernel.arithmetic.Traversing.CollectUndefinedVariables;
import org.geogebra.common.kernel.arithmetic.Traversing.DegreeReplacer;
import org.geogebra.common.kernel.arithmetic.Traversing.ReplaceUndefinedVariables;
import org.geogebra.common.kernel.arithmetic.Traversing.VariableReplacer;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.arithmetic.VectorValue;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilter;
import org.geogebra.common.kernel.arithmetic.traversing.SqrtMinusOneReplacer;
import org.geogebra.common.kernel.arithmetic.traversing.SqrtMultiplyFixer;
import org.geogebra.common.kernel.arithmetic.variable.Variable;
import org.geogebra.common.kernel.arithmetic3D.Vector3DValue;
import org.geogebra.common.kernel.commands.redefinition.RuleCollection;
import org.geogebra.common.kernel.commands.selector.CommandFilter;
import org.geogebra.common.kernel.commands.selector.CommandFilterFactory;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoAngle.AngleStyle;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoDummyVariable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSetup;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPointVector;
import org.geogebra.common.kernel.geos.GeoScriptAction;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.GeoVec2D;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.geos.HasArbitraryConstant;
import org.geogebra.common.kernel.geos.HasSymbolicMode;
import org.geogebra.common.kernel.implicit.AlgoDependentImplicitPoly;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoQuadric3DInterface;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.kernel.parser.ParserInterface;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.main.settings.Settings;
import org.geogebra.common.main.syntax.CommandSyntax;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Analytics;
import org.geogebra.common.util.debug.Log;
import org.geogebra.regexp.shared.MatchResult;
import org.geogebra.regexp.shared.RegExp;

import com.google.j2objc.annotations.Weak;
import com.himamis.retex.editor.share.util.Unicode;

/**
 * Processes algebra input as Strings and valid expressions into GeoElements
 *
 * @author Markus
 *
 */
public class AlgebraProcessor {

	/**
	 * String code returned from the dialog if the user wants to create a new
	 * slider
	 */
	public static final String CREATE_SLIDER = "1";

	/** kernel */
	@Weak
	protected final Kernel kernel;
	/** construction */
	@Weak
	protected final Construction cons;
	/** app */
	@Weak
	protected final App app;
	private final Localization loc;
	private final ParserInterface parser;
	/** command dispatcher */
	protected final CommandDispatcher cmdDispatcher;

	private MyStringBuffer xBracket = null;
	private MyStringBuffer yBracket = null;
	private MyStringBuffer zBracket = null;
	private MyStringBuffer closeBracket = null;

	private boolean structuresEnabled = true;
	private MathMLParser mathmlParserGGB;
	private MathMLParser mathmlParserLaTeX;

	/**
	 * Parametric processor (shared with 3D)
	 */
	protected ParametricProcessor paramProcessor;

	private final List<ExpressionFilter> inputExpressionFilters = new ArrayList<>();
	private final List<ExpressionFilter> outputExpressionFilters = new ArrayList<>();
	private final Set<GeoElementSetup> geoElementSetups = new HashSet<>();

	/** TODO use the selector from CommandDispatcher instead. */
	@Deprecated
	private CommandFilter noCASfilter;

	private SymbolicProcessor symbolicProcessor;
	private final SqrtMinusOneReplacer sqrtMinusOneReplacer;

	// Somewhat duplicates EvalInfo.isRedefinition but propagating EvalInfo to constructors of
	// all geos would be an overkill (needed for autocolor)
	private boolean isRedefining;

	/**
	 * @param kernel
	 *            kernel
	 * @param commandDispatcher
	 *            command dispatcher
	 */
	public AlgebraProcessor(Kernel kernel, CommandDispatcher commandDispatcher) {
		cons = kernel.getConstruction();
		this.kernel = kernel;

		this.cmdDispatcher = commandDispatcher;
		app = kernel.getApplication();
		loc = app.getLocalization();
		parser = kernel.getParser();
		setEnableStructures(app.getConfig().isEnableStructures());
		sqrtMinusOneReplacer = new SqrtMinusOneReplacer(kernel);
	}

	/**
	 * @return construction
	 */
	public Construction getConstruction() {
		return cons;
	}

	/**
	 * @return command dispatcher
	 */
	public CommandDispatcher getCmdDispatcher() {
		return cmdDispatcher;
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
	 * Add an input expression filter (used for dynamically filtering valid input expressions).
	 * @param filter An input expression filter.
	 */
	public void addInputExpressionFilter(@Nonnull ExpressionFilter filter) {
		inputExpressionFilters.add(filter);
	}

	/**
	 * Remove an input expression filter.
	 * @param filter An input expression filter.
	 */
	public void removeInputExpressionFilter(@Nonnull ExpressionFilter filter) {
		inputExpressionFilters.remove(filter);
	}

	/**
	 * Add an output expression filter (used for dynamically filtering output expressions).
	 * @param filter An output expression filter.
	 */
	public void addOutputExpressionFilter(@Nonnull ExpressionFilter filter) {
		outputExpressionFilters.add(filter);
	}

	/**
	 * Remove an output expression filter.
	 * @param filter An output expression filter.
	 */
	public void removeOutputExpressionFilter(@Nonnull ExpressionFilter filter) {
		outputExpressionFilters.remove(filter);
	}

	private boolean isExpressionAllowed(ValidExpression expression,
			List<ExpressionFilter> filters) {
		for (ExpressionFilter expressionFilter : filters) {
			if (!expressionFilter.isAllowed(expression)) {
				return false;
			}
		}
		return true;
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
	final public GeoElement[] processCommand(Command c, EvalInfo info)
			throws MyError {
		boolean validated = true;
		try {
			return cmdDispatcher.processCommand(c, info);
		} catch (MyError error) {
			validated = false;
			throw error;
		} finally {
			maybeLogCommandValidatedEvent(c, info, validated);
		}
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
	final public ExpressionValue simplifyCommand(Command c, EvalInfo info)
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
	final public void processCasCell(GeoCasCell casCell, boolean isLastRow, String oldXML)
			throws MyError {
		// check for CircularDefinition
		if (casCell.isCircularDefinition()) {
			// set twin geo to undefined
			casCell.computeOutput();
			casCell.updateCascade();
			app.showError(Errors.CircularDefinition);
			return;
		}

		AlgoElement algoParent = casCell.getParentAlgorithm();
		boolean prevFree = algoParent == null;
		boolean nowFree = !casCell.hasVariablesOrCommands();
		boolean needsRedefinition;
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
				app.getEventDispatcher().disableListeners();
				cons.changeCasCell(casCell, oldXML);
				app.getEventDispatcher().enableListeners();
				app.getEventDispatcher().notifyListenersUpdateCascade(casCell);
				// the changeCasCell command computes the output
				// so we don't need to call computeOutput,
				// which also causes marble crashes

			} catch (Exception e) {
				app.getEventDispatcher().enableListeners();
				Log.debug(e);
				casCell.setError("ReplaceFailed");
			} catch (Error e) { // including CommandNotFoundError
				app.getEventDispatcher().enableListeners();
				throw e;
			}
		} else {
			casCell.notifyAdd();
			casCell.updateCascade();
			if (needsConsUpdate) {
				cons.updateCasCells();
			}
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
			changeGeoElement(geo, newValue, redefineIndependent, storeUndoInfo,
					true, handler, callback);
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
	 * @param withSliders
	 *            true to autocreate sliders
	 * @param handler
	 *            error handler
	 *
	 * @param callback
	 *            receives changed geo
	 */
	public void changeGeoElement(final GeoElementND geo, String newValue,
			boolean redefineIndependent, boolean storeUndoInfo,
			boolean withSliders, ErrorHandler handler,
			AsyncOperation<GeoElementND> callback) {
		EvalInfo info =
				new EvalInfo(!cons.isSuppressLabelsActive(), redefineIndependent)
						.withSymbolicMode(app.getKernel().getSymbolicMode())
						.withLabelRedefinitionAllowedFor(geo.getLabelSimple())
						.withSymbolic(true);
		changeGeoElementNoExceptionHandling(geo, newValue,
				info.withSliders(withSliders), storeUndoInfo, callback, handler);
	}

	/**
	 * for AlgebraView changes in the tree selection and redefine dialog
	 *
	 * @param geo
	 *            old geo
	 * @param newValue
	 *            new value
	 * @param info
	 *            evaluation flags
	 * @param storeUndoInfo
	 *            true to make undo step
	 * @param callback
	 *            what to do with the changed geo
	 * @param handler
	 *            decides how to handle exceptions
	 *
	 */
	public void changeGeoElementNoExceptionHandling(GeoElementND geo,
			String newValue, EvalInfo info, boolean storeUndoInfo,
			AsyncOperation<GeoElementND> callback, ErrorHandler handler) {

		try {
			ValidExpression ve;

			if (info.isMultipleUnassignedAllowed()) {
				ve = parser.parseInputBoxExpression(newValue);
				if (ve.getLabel() != null && !ve.getLabel().equals(geo.getLabelSimple())) {
					handler.showError(getIllegalAssignmentError());
					return;
				}
			} else {
				ve = parser.parseGeoGebraExpression(newValue);
			}

			if (ve.isOperation(Operation.MULTIPLY)
					&& ve.wrap().getLeft() == geo && ve.wrap().getRight() instanceof MyVecNDNode) {
				ve = ve.wrap().getRightTree();
			}

			if ("X".equals(ve.getLabel())) {
				ve = getParamProcessor().checkParametricEquationF(ve, ve, cons,
						new EvalInfo(!cons.isSuppressLabelsActive()));
			}

			replaceDerivative(ve, geo);
			if (GeoPoint.isComplexNumber(geo)) {
				ve = replaceSqrtMinusOne(ve);
			}
			changeGeoElementNoExceptionHandling(geo, ve, info,
					storeUndoInfo, callback, handler);
		} catch (MyError e) {
			ErrorHelper.handleError(e, newValue, loc, handler);
		} catch (ParseException exception) {
			handler.showError(exception.getMessage());
			if (callback != null) {
				callback.callback(geo);
			}
		} catch (Exception e) {
			ErrorHelper.handleException(e, app, handler);
		} catch (CommandNotLoadedError e) {
			throw e;
		} catch (Error e) {
			Log.debug(e);
			handler.showError(
					loc.getInvalidInputError() + ":\n"
							+ newValue);
		}
	}

	private String getIllegalAssignmentError() {
		return new MyError(kernel.getLocalization(), Errors.IllegalAssignment)
				.getLocalizedMessage();
	}

	private ValidExpression replaceSqrtMinusOne(ValidExpression ve) {
		ExpressionValue result = ve.traverse(sqrtMinusOneReplacer);
		if (ExpressionNode.isImaginaryUnit(result)) {
			result = result.wrap();
		}
		return (ValidExpression) result;
	}

	/**
	 * Replace f' by ExNode(f, Operation.Derivative, 1) in new definition of f'.
	 *
	 * @param ve
	 *            new definition
	 * @param geo
	 *            geo to be replaced by definition
	 */
	private void replaceDerivative(ValidExpression ve, final GeoElementND geo) {
		if (geo.getLabelSimple() != null && geo.getLabelSimple().endsWith("'")
				&& geo.getParentAlgorithm() instanceof AlgoDependentFunction) {
			ve.traverse(new Traversing() {

				@Override
				public ExpressionValue process(ExpressionValue ev) {
					if (ev == geo) {
						ExpressionNode en = ((AlgoDependentFunction) geo
								.getParentAlgorithm()).getExpression().unwrap()
										.wrap();
						// f'(x) => f'
						if (en.getOperation() == Operation.FUNCTION
								&& en.getRight() instanceof FunctionVariable) {
							en = en.getLeft().wrap();
						}
						return en.deepCopy(kernel);
					}
					return ev;
				}
			});
		}

	}

	/**
	 * for AlgebraView changes in the tree selection and redefine dialog
	 *
	 * @param geo
	 *            old geo
	 * @param newValue
	 *            new value
	 * @param info
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
			ValidExpression newValue, EvalInfo info,
			final boolean storeUndoInfo,
			final AsyncOperation<GeoElementND> callback, ErrorHandler handler) {
		String oldLabel, newLabel;

		app.getCompanion().storeViewCreators();

		oldLabel = geo.getLabel(StringTemplate.defaultTemplate);
		updateLabelIfSymbolic(newValue, info);
		newLabel = newValue.getLabel();
		if (!app.getConfig().hasAutomaticLabels()) {
			geo.setAlgebraLabelVisible(newLabel != null);
		}

		if (newLabel == null) {
			newLabel = oldLabel;
			newValue.setLabel(newLabel);
		}

		// make sure that points stay points and vectors stay vectors
		updateTypePreservingFlags(newValue, geo, info.isPreventingTypeChange());
		if (sameLabel(newLabel, oldLabel)) {
			app.getEventDispatcher().disableListeners();
			AsyncOperation<GeoElementND[]> changeCallback = obj -> {
				if (obj != null) {
					app.getEventDispatcher().enableListeners();
					if (obj.length > 0) {
						app.getEventDispatcher().notifyListenersUpdateCascade(obj[0]);
						app.dispatchEvent(new Event(EventType.REDEFINE, obj[0].toGeoElement()));
					}
					app.getCompanion().recallViewCreators();
					if (storeUndoInfo) {
						app.storeUndoInfo();
					}
					if (callback != null) {
						callback.callback(obj.length > 0 ? obj[0] : null);
					}
				}
			};

			processAlgebraCommandNoExceptionHandling(newValue, false, handler,
					changeCallback, info);
			// make sure listeners are enabled if redefinition failed
			app.getEventDispatcher().enableListeners();
			cons.registerFunctionVariable(null);
			return;
		} else if (cons.isFreeLabel(newLabel)) {
			newValue.setLabel(oldLabel);
			// rename to oldLabel to enable overwriting
			GeoElementND[] result = processAlgebraCommandNoExceptionHandling(newValue, false,
					handler, null, info);
			if (result != null) {
				result[0].setLabel(newLabel); // now we rename
				app.getCompanion().recallViewCreators();
				if (storeUndoInfo) {
					app.storeUndoInfo();
				}
				if (result.length > 0 && callback != null) {
					callback.callback(result[0]);
				}
			}
		} else {
			throw new MyError(loc, Errors.NameUsed, newLabel);
		}

		cons.registerFunctionVariable(null);
	}

	private void updateTypePreservingFlags(ValidExpression newValue, GeoElementND geo,
			boolean preventTypeChange) {
		if (newValue instanceof ExpressionNode) {
			ExpressionNode n = (ExpressionNode) newValue;
			if (geo.isGeoPoint()) {
				n.setForcePoint();
			} else if (geo.isGeoVector()) {
				n.setForceVector();
			} if (geo instanceof GeoFunction) {
				if (((GeoFunction) geo).isForceInequality()) {
					n.setForceInequality();
				} else {
					n.setForceFunction();
				}
			} else if (geo.isGeoSurfaceCartesian() && preventTypeChange) {
				n.setForceSurfaceCartesian();
			} else if (geo instanceof GeoFunctionNVar) {
				if (((GeoFunctionNVar) geo).isForceInequality()) {
					n.setForceInequality();
				}
			} else if (geo.isGeoAngle() && preventTypeChange) {
				n.setForceAngle();
			}
		}
		if (newValue.unwrap() instanceof Equation) {
			if (geo instanceof GeoPlaneND) {
				((Equation) newValue.unwrap()).setForcePlane();
			} else if (geo instanceof GeoImplicitCurve && preventTypeChange) {
				((Equation) newValue.unwrap()).setForceImplicitPoly();
			} else if (geo instanceof GeoConic && preventTypeChange) {
				((Equation) newValue.unwrap()).setForceConic();
			} else if (geo instanceof GeoLine && preventTypeChange) {
				((Equation) newValue.unwrap()).setForceLine();
			} else if (geo instanceof GeoQuadric3DInterface && preventTypeChange) {
				((Equation) newValue.unwrap()).setForceQuadric();
			}
		}
	}

	private void updateLabelIfSymbolic(ValidExpression expression, EvalInfo info) {
		if (info.getSymbolicMode() == SymbolicMode.SYMBOLIC_AV && symbolicProcessor != null) {
			symbolicProcessor.updateLabel(expression, info);
		}
	}

	private static boolean sameLabel(String newLabel, String oldLabel) {
		if (newLabel.equals(oldLabel)) {
			return true;
		}
		if (oldLabel == null) {
			return false;
		}
		if (newLabel.indexOf('_') > 0) {
			return curlyLabel(newLabel).equals(curlyLabel(oldLabel));
		}

		return false;
	}

	/**
	 * @param newLabel
	 *            input label
	 * @return a_1 transformed into a_{1}
	 */
	public static String curlyLabel(String newLabel) {
		if (newLabel.indexOf("_{") > 0 || !newLabel.contains("_")) {
			return newLabel;
		}
		return newLabel.replace("_", "_{") + "}";
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
			app.showGenericError(e);
			return null;
		}
	}

	/**
	 * @param cmd       string to process
	 * @param storeUndo true to make undo step
	 * @param callback  callback after the geos are created
	 * @return resulting geos
	 */
	public GeoElementND[] processAlgebraCommand(String cmd, boolean storeUndo,
			final AsyncOperation<GeoElementND[]> callback) {

		try {
			return processAlgebraCommandNoExceptionHandling(cmd, storeUndo,
					app.getErrorHandler(), false, callback);
		} catch (Exception e) {
			app.showGenericError(e);
			return null;
		}
	}

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
		} catch (Exception | MyError e) {
			return null;
		}
	}

	/**
	 * @param cmd
	 *            string to process
	 * @param storeUndo
	 *            true to make undo step
	 * @param handler
	 *            decides how to handle exceptions
	 * @param autoCreateSlidersAndDegrees
	 *            whether to create sliders (using a popup) and add degrees
	 * @param callback0
	 *            callback after the geos are created
	 * @return resulting geos
	 */
	public GeoElementND[] processAlgebraCommandNoExceptionHandling(
			final String cmd, final boolean storeUndo,
			final ErrorHandler handler, boolean autoCreateSlidersAndDegrees,
			final AsyncOperation<GeoElementND[]> callback0) {
		EvalInfo info = getEvalInfo(autoCreateSlidersAndDegrees,
				autoCreateSlidersAndDegrees);
		return processAlgebraCommandNoExceptionHandling(cmd, storeUndo, handler,
				info, callback0);
	}

	/**
	 * @param addDegree
	 *           whether to add degrees
	 * @return evaluation flags
	 */
	public EvalInfo getEvalInfo(boolean addDegree) {
		return getEvalInfo(true, addDegree);
	}

	/**
	 * @param autoCreateSliders
	 *            whether to create sliders (using a popup)
	 * @param addDegreesIfKernelInDegrees
	 *            whether to add degrees
	 * @return evaluation flags
	 */
	public EvalInfo getEvalInfo(boolean autoCreateSliders,
			boolean addDegreesIfKernelInDegrees) {
		return new EvalInfo(!cons.isSuppressLabelsActive(), true)
				.withSliders(autoCreateSliders)
				.addDegree(addDegreesIfKernelInDegrees
						&& app.getKernel().getAngleUnitUsesDegrees())
				.withSymbolicMode(kernel.getSymbolicMode());
	}

	/**
	 * @param cmd
	 *            string to process
	 * @param storeUndo
	 *            true to make undo step
	 * @param handler
	 *            decides how to handle exceptions
	 * @param info
	 *            flags for labeling output, using sliders etc.
	 * @param callback0
	 *            callback after the geos are created
	 * @return resulting geos
	 */
	public GeoElementND[] processAlgebraCommandNoExceptionHandling(
			final String cmd, final boolean storeUndo,
			final ErrorHandler handler, EvalInfo info,
			final AsyncOperation<GeoElementND[]> callback0) {

		// both return this and call callback0 in case of success!
		GeoElementND[] rett;

		if (cmd.length() > 0 && cmd.charAt(0) == '<'
				&& cmd.startsWith("<math")) {
			rett = parseMathml(cmd, storeUndo, handler,
					info.isAutocreateSliders(),
					callback0);
			return rett;
		}
		try {
			GeoCasCell casEval = checkCasEval(cmd, "(:=?)|=|" + Unicode.ASSIGN_STRING);
			if (casEval != null) {
				if (callback0 != null) {
					callback0.callback(array(casEval));
				}
				return new GeoElement[0];
			}
			ValidExpression ve = parser.parseGeoGebraExpression(cmd);
			return processAlgebraCommandNoExceptionHandling(ve, storeUndo,
					handler, callback0, info);

		} catch (Exception e) {
			ErrorHelper.handleException(e, app, handler);
		} catch (MyError e) {
			ErrorHelper.handleError(e, cmd, loc, handler);
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
			final AsyncOperation<GeoElementND[]> callback0,
			final EvalInfo info) {
		if (!isExpressionAllowed(ve, inputExpressionFilters)) {
			ErrorHelper.handleError(new MyError(loc, Errors.CASGeneralErrorMessage),
					null, loc, handler);
			return null;
		}
		// collect undefined variables
		CollectUndefinedVariables collector = new Traversing.CollectUndefinedVariables(
				info.isMultipleUnassignedAllowed());
		ve.any(collector);
		final TreeSet<String> undefinedVariables = collector.getResult();

		GeoElement[] ret = getParamProcessor().checkParametricEquation(ve,
				undefinedVariables, callback0,
				new EvalInfo(!cons.isSuppressLabelsActive())
						.withSliders(info.isAutocreateSliders()));
		if (ret != null) {
			if (storeUndo) {
				app.storeUndoInfo();
			}
			return postProcessCreatedElements(ve, callback0, ret, handler, null);
		}
		EvalInfo newInfo = info;
		Set<GeoNumeric> sliders = null;
		if (!undefinedVariables.isEmpty()) {

			// ==========================
			// step0: check if there's an error on processing
			// eg we don't want to create slider 't' for
			// Curve[t^3,t^2,t,0,2]
			// ==========================
			GeoElement[] geoElements = null;
			try {
				ValidExpression cp = ve.deepCopy(kernel);
				cp.setLabels(ve.getLabels());
				geoElements = processValidExpression(cp, info);
				if (storeUndo && geoElements != null) {
					app.storeUndoInfo();
				}
			} catch (Throwable ex) {
				// do nothing
			}
			if (geoElements != null) {
				kernel.getConstruction().registerFunctionVariable(null);
				return postProcessCreatedElements(ve, callback0, geoElements, handler, null);
			}

			StringBuilder sb = new StringBuilder();

			// ==========================
			// step3: make a list of undefined variables so we can ask the
			// user
			// ==========================
			for (String label : undefinedVariables) {
				if (kernel.lookupLabel(label) == null) {
					sb.append(label);
					sb.append(", ");
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
				if (!info.isAutocreateSliders()) {
					GeoElementND[] rett = tryReplacingProducts(ve, handler,
							info);
					return postProcessCreatedElements(ve, callback0, rett, handler, null);
				}

				// "Create sliders for a, b?" Create Sliders / Cancel
				// Yes: create sliders and draw line
				// No: go back into input bar and allow user to change input

				final ValidExpression ve2 = ve;

				AsyncOperation<String[]> callback = dialogResult -> {
					Set<GeoNumeric> asyncSliders = new HashSet<>();
					if (CREATE_SLIDER.equals(dialogResult[0])) {
						replaceUndefinedVariables(ve2,
								asyncSliders, null,
								info.isMultipleUnassignedAllowed());
					}

					GeoElement[] geos = processValidExpression(storeUndo, handler,
								ve2, info, asyncSliders);

					postProcessCreatedElements(ve, callback0, geos, handler, asyncSliders);
				};
				boolean autoCreateSlidersAnswer = handler
						.onUndefinedVariables(sb.toString(), callback);

				if (!autoCreateSlidersAnswer) {
					return null;
				}
			}

			// ==========================
			// step5: replace undefined variables
			// ==========================
			sliders = new HashSet<>();
			replaceUndefinedVariables(ve, sliders, null,
					info.isMultipleUnassignedAllowed());

			// Do not copy plain variables, as
			// they might have been just created now
			newInfo = info.withCopyingPlainVariables(false);
		}

		// process ValidExpression (built by parser)

		GeoElement[] geos = processValidExpression(storeUndo, handler, ve,
				newInfo, sliders);

		return postProcessCreatedElements(ve, callback0, geos, handler, sliders);
	}

	private GeoElement evalSymbolic(final ValidExpression ve, EvalInfo info) {
		if (symbolicProcessor == null) {
			symbolicProcessor = new SymbolicProcessor(kernel);
		}
		ValidExpression extracted = replaceFunctionVariables(ve);
		if (ve.unwrap() instanceof Equation && info != null) {
			Equation equation = (Equation) ve.unwrap();
			extracted = symbolicProcessor.extractAssignment(equation, info);
			ve.setLabel(extracted.getLabel());
		}
		GeoElement symbolic = symbolicProcessor.evalSymbolicNoLabel(extracted, info);
		String label = extracted.getLabel();
		if (label != null && kernel.lookupLabel(label) != null
				&& !info.isLabelRedefinitionAllowedFor(label)) {
			throw new MyError(kernel.getLocalization(), "LabelAlreadyUsed");
		}
		setLabel(symbolic, label);
		return symbolic;
	}

	private ExpressionNode replaceFunctionVariables(ValidExpression expression) {
		FunctionVarCollector collector = FunctionVarCollector.getCollector();
		expression.traverse(collector);
		FunctionVariable[] fxvArray = collector.buildVariables(kernel);
		FunctionVariable[] xyzVars = FunctionNVar.getXYZVars(fxvArray);
		ExpressionNode node =
				expression.traverse(new CoordMultiplyReplacer(xyzVars[0], xyzVars[1], xyzVars[2]))
						.traverse(SqrtMultiplyFixer.INSTANCE).wrap();
		node.setLabels(expression.getLabels());
		return node;
	}

	private void setLabel(GeoElement element, String label) {
		ExpressionNode definition = element.getDefinition();
		definition.setLabel(label);
		element.setLabel(label);
		ExpressionValue unwrappedDefinition = definition.unwrap();
		if (unwrappedDefinition instanceof ValidExpression) {
			((ValidExpression) unwrappedDefinition).setLabel(label);
		}
		if (element instanceof GeoSymbolic && isVectorLabel(label)) {
			setVectorPrintingModeFor((GeoSymbolic) element);
		}
		element.notifyUpdate();
	}

	private void setVectorPrintingModeFor(GeoSymbolic element) {
		ExpressionValue unwrappedDefinition = element.getDefinition().unwrap();
		if (unwrappedDefinition instanceof MyVecNDNode) {
			((MyVecNDNode) unwrappedDefinition).setupCASVector();
		}
		ExpressionValue value = element.getValue();
		ExpressionValue unwrappedValue = value != null ? value.unwrap() : null;
		if (unwrappedValue instanceof MyVecNDNode) {
			((MyVecNDNode) unwrappedValue).setupCASVector();
		}
	}

	/**
	 * Run callback on new geos if there are any or empty array otherwise
	 *
	 * @param input
	 *            input expression
	 * @param callback0
	 *            callback
	 * @param geos
	 *            possible new geos
	 * @param sliders
	 *            auto-created sliders
	 */
	GeoElementND[] postProcessCreatedElements(ValidExpression input,
			AsyncOperation<GeoElementND[]> callback0, GeoElementND[] geos,
			ErrorHandler handler, @CheckForNull Set<GeoNumeric> sliders) {
		GeoElementND[] filteredGeos = geos;
		if (geos != null) {
			// Switch to an enabled algebra output format
			// before checking if the output expression is restricted.
			if (!app.getSettings().getAlgebra().getAlgebraOutputFormatFilters().isEmpty()) {
				Arrays.stream(geos).map(GeoElementND::toGeoElement).forEach(geoElement ->
						AlgebraOutputFormat.switchFromDisabledFormat(geoElement,
								app.getSettings().getAlgebra().isEngineeringNotationEnabled(),
								app.getSettings().getAlgebra().getAlgebraOutputFormatFilters()));
			}

			// Check if any input or output expression should be restricted.
			boolean containsRestrictedInputExpression =
					!isExpressionAllowed(input, inputExpressionFilters);
			boolean containsRestrictedOutputExpressions = Arrays.stream(geos)
					.map(ExpressionValue::wrap)
					.anyMatch(geo -> !isExpressionAllowed(geo, outputExpressionFilters));

			// If the expression does not contain any restriction,
			if (!containsRestrictedInputExpression && !containsRestrictedOutputExpressions) {
				// apply the element setups,
				if (!geoElementSetups.isEmpty()) {
					Arrays.stream(geos).forEach(geoElement -> {
						geoElementSetups.forEach(setup -> setup.applyTo(geoElement));
						geoElement.updateRepaint();
					});
				}
			} else {
				// otherwise remove the elements from the construction.
				Arrays.stream(geos).forEach(GeoElementND::remove);
				filteredGeos = null;
				MyError myError = new MyError(loc, Errors.InvalidInput);
				ErrorHelper.handleError(myError, null, loc, handler);
				removeSliders(sliders);
			}
		}
		if (callback0 != null) {
			callback0.callback(filteredGeos);
		}
		return filteredGeos;
	}

	private void removeSliders(Set<GeoNumeric> sliders) {
		if (sliders != null) {
			sliders.forEach(GeoElementND::remove);
			sliders.clear();
		}
	}

	/**
	 * create valid expression (if possible) from command string
	 *
	 * @param cmd
	 *            command
	 * @return valid expression
	 * @throws ParseException
	 *             exception if syntax is invalid
	 */
	public ValidExpression getValidExpressionNoExceptionHandling(
			final String cmd) throws ParseException {
		return parser.parseGeoGebraExpression(cmd);
	}

	/**
	 * @param input
	 *            whole command including label
	 * @param allowedAssignmentRegex regular expression matching allowed assignment operators
	 * @return cell
	 */
	public GeoCasCell checkCasEval(String input, String allowedAssignmentRegex) {
		if (input != null && input.startsWith("$")) {
			String[] result = input.split(allowedAssignmentRegex, 2);

			if (result.length != 2 || result[1].startsWith("=")) {
				return null;
			}

			int row = cellNumber(result[0].substring(1));
			if (row >= 0) {
				return casEval(row, result[1]);
			}
		}
		return null;
	}

	private GeoCasCell casEval(int row, String rhs) {
		if (app.getGuiManager() != null) {
			app.getGuiManager().getCasView().cancelEditItem();
		}
		StringBuilder oldXML = cons.getCurrentUndoXML(false);
		GeoCasCell cell = cons.getCasCell(row);
		if (cell == null) {
			cell = new GeoCasCell(cons);
		}
		cell.setInput(rhs);
		processCasCell(cell, false, oldXML.toString());
		return cell;
	}

	private int cellNumber(String lhs) {
		try {
			return Integer.parseInt(lhs) - 1;
		} catch (Exception e) {
			// eg $A$1 label, do nothing
		}
		return -1;
	}

	/**
	 * Changes "s i n x" to "sin(x)" (needed for evalMathml) and processes the expression.
	 * @return processed expression
	 */
	private GeoElementND[] tryReplacingProducts(ValidExpression ve,
			ErrorHandler eh, EvalInfo info) {
		ValidExpression ve2 = (ValidExpression) ve.traverse(new Traversing() {

			@Override
			public ExpressionValue process(ExpressionValue ev) {
				if (ev.isExpressionNode() && ((ExpressionNode) ev)
						.getOperation() == Operation.MULTIPLY) {
					String lt = ((ExpressionNode) ev).getLeft()
							.toString(StringTemplate.defaultTemplate)
							.replace(" ", "");
					Operation op = app.getParserFunctions().getSingleArgumentOp(lt);
					if (op != null) {
						return new ExpressionNode(kernel,
								((ExpressionNode) ev).getRight().traverse(this),
								op, null);
					}
				}
				return ev;
			}
		});
		GeoElementND[] ret = null;
		try {
			ret = this.processValidExpression(ve2, info);
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
	 * @param cmd
	 *            command in presentation MathML
	 * @param storeUndo
	 *            whether to create an undo point
	 * @param handler
	 *            error handler
	 *
	 * @param autoCreateSliders
	 *            whether sliders should be autocreated
	 * @param callback0
	 *            callback
	 * @return resulting elements
	 */
	public GeoElementND[] parseMathml(String cmd, final boolean storeUndo,
			ErrorHandler handler, boolean autoCreateSliders,
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
			ret = this.processAlgebraCommandNoExceptionHandling(ggb, storeUndo,
					handler, autoCreateSliders, callback0);
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
	public synchronized GeoElement[] processValidExpression(boolean storeUndo,
			ErrorHandler handler, ValidExpression ve, EvalInfo info, Set<GeoNumeric> sliders) {
		GeoElement[] geoElements = null;
		try {
			geoElements = processValidExpression(ve, info);
			if (storeUndo && geoElements != null) {
				app.storeUndoInfo();
			}
		} catch (MyError e) {
			removeSliders(sliders);
			ErrorHelper.handleError(e,
					ve == null ? null
							: ve.toString(StringTemplate.defaultTemplate),
					loc, handler);
		} catch (Exception ex) {
			removeSliders(sliders);
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
	 * @param multiplication whether to interpret long unknown variable names as multiplication of
	 *     more variables
	 */
	public void replaceUndefinedVariables(ValidExpression ve,
			Set<GeoNumeric> undefined, String[] except, boolean multiplication) {
		ReplaceUndefinedVariables replacer = new Traversing.ReplaceUndefinedVariables(
				this.kernel, undefined, except);
		replacer.setSimplifyMultiplication(multiplication);
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

	private NumberValue evaluateToNumberValue(
			ExpressionNode expressionNode) {
		expressionNode.resolveVariables(new EvalInfo(false));
		if (expressionNode.containsFreeFunctionVariable(null)) {
			throw new MyError(loc, "IncompleteEquation");
		}
		return (NumberValue) expressionNode
				.evaluate(StringTemplate.defaultTemplate);
	}

	/**
	 * Converts a String into a double.
	 * @param string The String to be converted to double.
	 * @return the double value of the String after the conversion
	 * @throws NumberFormatException this exception is thrown if the String cannot be converted.
	 */
	public double convertToDouble(String string) throws NumberFormatException {
		try {
			return evaluateToNumberValue(parser.parseExpression(string)).getDouble();
		} catch (MyError | RuntimeException | ParseException e) {
			throw new NumberFormatException(e.getMessage());
		}
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
			ExpressionNode en = parser.parseExpression(str);
			NumberValue nv = evaluateToNumberValue(en);
			if (forGeo != null) {
				forGeo.setValue(nv.getDouble());

				// if forGeo is a slider, the value might be out of range
				// in which case we mustn't set the definition
				if (DoubleUtil.isEqual(forGeo.getDouble(), nv.getDouble())
						&& en.isConstant()) {
					forGeo.setDefinition(en);
				}
			}
			return nv.getDouble();
		} catch (CommandNotLoadedError e) {
			throw e;
		} catch (Throwable t) {
			Log.debug(t);
			if (!suppressErrors) {
				app.showError(Errors.InvalidInput, str);
			}

			if (forGeo != null) {
				forGeo.setUndefined();
			}

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
				ve = new ExpressionNode(kernel,
						new Variable(kernel, ve.getLabel()),
						Operation.EQUAL_BOOLEAN, ve);
				// A+B=C as comparison, not equation
			} else if (ve.unwrap() instanceof Equation) {
				Equation eq = (Equation) ve.unwrap();
				ve = new ExpressionNode(kernel, eq.getLHS(),
						Operation.EQUAL_BOOLEAN, eq.getRHS());
			} else if (ve.unwrap() instanceof Variable && !isBoolean((Variable) ve.unwrap())) {
				// GGB-1043
				ve = new ExpressionNode(kernel, ve.unwrap(),
						Operation.NOT_EQUAL, new MyDouble(kernel, 0d));
			}
			GeoElementND[] temp = processValidExpression(ve);

			// GGB-1043 GWT: can't rely on ClassCast Exception
			if (temp[0] instanceof GeoBoolean) {
				bool = (GeoBoolean) temp[0];
			} else {
				handler.showError(loc.getInvalidInputError());
			}
		} catch (Exception e) {
			ErrorHelper.handleException(e, app, handler);
		} catch (MyError e) {
			ErrorHelper.handleError(e, str, loc, handler);
		} catch (CommandNotLoadedError e) {
			throw e;
		} catch (Error e) {
			Log.debug(e);
			handler.showError(loc.getInvalidInputError());
		} finally {
			cons.setSuppressLabelCreation(oldMacroMode);
			cons.registerFunctionVariable(null);
		}

		return bool;
	}

	private boolean isBoolean(Variable variable) {
		return kernel.lookupLabel(variable.getName()) instanceof GeoBoolean;
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
		if ("?".equals(str)) {
			return null;
		}
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		GeoList list = null;
		try {
			ValidExpression ve = parser.parseGeoGebraExpressionLowPrecision(str);
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
		} catch (CommandNotLoadedError e) {
			throw e;
		} catch (Throwable t) {
			Log.debug(t);
		} finally {
			cons.registerFunctionVariable(null);
			cons.setSuppressLabelCreation(oldMacroMode);
		}

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
				exp = exp.traverse(getArbcostReverse()).wrap();
			}
			GeoElementND[] temp = processValidExpression(exp);

			if (temp[0].isRealValuedFunction()) {
				GeoFunctionable f = (GeoFunctionable) temp[0];
				func = f.getGeoFunction();
			} else if (!suppressErrors) {
				app.showError(Errors.InvalidInput, str);
			}

		} catch (CircularDefinitionException e) {
			Log.debug("CircularDefinition");
			if (!suppressErrors) {
				app.showError(Errors.CircularDefinition);
			}
		} catch (CommandNotLoadedError e) {
			throw e;
		} catch (Throwable t) {
			Log.debug(t);
			if (!suppressErrors) {
				app.showError(Errors.InvalidInput, str);
			}
		} finally {
			cons.registerFunctionVariable(null);
			cons.setSuppressLabelCreation(oldMacroMode);
		}

		return func;
	}

	private Traversing getArbcostReverse() {
		return new Traversing() {

			@Override
			public ExpressionValue process(ExpressionValue ev) {
				if (ev instanceof Variable) {
					GeoElement geo = kernel
							.lookupLabel(((Variable) ev).getName());
					String[] parts = ((Variable) ev).getName().split("_");
					if (geo == null && parts.length == 2) {
						try {
							int idx = Integer.parseInt(
									parts[1].replace("{", "").replace("}", ""));
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
		};
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
	 * @param revertArbconst
	 *            whether to replace c_1 back with arbconst(1)
	 * @return str parsed to multivariate function
	 */
	public GeoFunctionNVar evaluateToFunctionNVar(String str,
			boolean suppressErrors, boolean revertArbconst) {
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		GeoFunctionNVar func = null;
		try {
			ValidExpression ve = parser.parseGeoGebraExpression(str);
			if (revertArbconst) {
				ve = ve.traverse(getArbcostReverse()).wrap();
			}
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
				FunctionNVar fn = new FunctionNVar(
						new ExpressionNode(kernel, temp[0]), funVars);
				func = new GeoFunctionNVar(cons, fn);

			}
		} catch (CircularDefinitionException e) {
			Log.debug("CircularDefinition");
			if (!suppressErrors) {
				app.showError(Errors.CircularDefinition);
			}
		} catch (CommandNotLoadedError e) {
			throw e;
		} catch (Throwable t) {
			Log.debug(t);
			if (!suppressErrors) {
				app.showError(Errors.InvalidInput, str);
			}
		} finally {
			cons.registerFunctionVariable(null);
			cons.setSuppressLabelCreation(oldMacroMode);
		}

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
	public GeoNumberValue evaluateToNumeric(String str,
			boolean suppressErrors) {
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
		EvalInfo info = new EvalInfo(!cons.isSuppressLabelsActive(), true);
		return evaluateToNumeric(str, handler, info);
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
	public GeoNumberValue evaluateToNumeric(String str, ErrorHandler handler, EvalInfo info) {
		if (StringUtil.empty(str)) {
			ErrorHelper.handleInvalidInput(str, loc, handler);
			return new GeoNumeric(cons, Double.NaN);
		}

		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		GeoNumberValue num = null;
		try {
			ValidExpression ve = parser.parseGeoGebraExpression(str);
			GeoElementND[] temp = processValidExpression(ve, info);

			if (temp[0].unwrapSymbolic() instanceof GeoNumberValue) {
				num = (GeoNumberValue) temp[0].unwrapSymbolic();
			} else {
				num = new GeoNumeric(cons, Double.NaN);
				ErrorHelper.handleInvalidInput(str, loc, handler);
			}
		} catch (Exception e) {
			ErrorHelper.handleException(e, app, handler);
		} catch (MyError e) {
			ErrorHelper.handleError(e, str, loc, handler);
		} catch (CommandNotLoadedError e) {
			throw e;
		} catch (Error e) {
			Log.debug(e);
			ErrorHelper.handleException(new Exception(e), app, handler);
		} finally {
			cons.registerFunctionVariable(null);
			cons.setSuppressLabelCreation(oldMacroMode);
		}

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
		GeoElementND[] temp;
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
		} catch (CommandNotLoadedError e) {
			throw e;
		} catch (Error e) {
			ErrorHelper.handleException(new Exception(e), app, handler);
		} finally {
			cons.registerFunctionVariable(null);
			if (suppressLabels) {
				cons.setSuppressLabelCreation(oldMacroMode);
			}
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
		GeoElementND[] temp;
		try {
			ValidExpression ve = parser.parseGeoGebraExpression(str);
			temp = processValidExpression(ve);
			text = (GeoText) temp[0];
		} catch (CircularDefinitionException e) {
			if (showErrors) {
				Log.debug("CircularDefinition");
				app.showError(Errors.CircularDefinition);
			}
		} catch (CommandNotLoadedError e) {
			throw e;
		} catch (Throwable t) {
			if (showErrors) {
				Log.debug(t);
				app.showError(Errors.InvalidInput, str);
			}
		} finally {
			cons.registerFunctionVariable(null);
			cons.setSuppressLabelCreation(oldMacroMode);
		}

		return text;
	}

	/**
	 * Parses given String str and tries to evaluate it to a GeoImplicitPoly
	 * object. Returns null if something went wrong.
	 *
	 * @param str
	 *            stringInput
	 * @param showErrors
	 *            if false, only stack traces are printed
	 * @return construction element or null
	 */
	public GeoElementND evaluateToGeoElement(String str, boolean showErrors) {
		return evaluateToGeoElement(str, showErrors,
				new EvalInfo(!cons.isSuppressLabelsActive(), true), null);
	}

	/**
	 * Parses given String str and tries to evaluate it to a GeoImplicitPoly
	 * object. Returns null if something went wrong.
	 *
	 * @param str
	 *            stringInput
	 * @param showErrors
	 *            if false, only stacktraces are printed
	 * @param template used to determine preferred type flags
	 * @return construction element or null
	 */
	public GeoElementND evaluateToGeoElement(String str, boolean showErrors, EvalInfo info,
			GeoElementND template) {
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		GeoElementND geo = null;
		try {
			ValidExpression ve = parser.parseGeoGebraExpression(str);
			if (template != null) {
				updateTypePreservingFlags(ve, template, info.isPreventingTypeChange());
			}
			GeoElementND[] temp = processValidExpression(ve, info);
			geo = temp[0];
		} catch (CircularDefinitionException e) {
			Log.debug("CircularDefinition");
			app.showError(Errors.CircularDefinition);
		} catch (CommandNotLoadedError e) {
			throw e;
		} catch (Throwable t) {
			Log.debug(t);
			if (showErrors) {
				app.showError(Errors.InvalidInput, str);
			}
		} finally {
			cons.registerFunctionVariable(null);
			cons.setSuppressLabelCreation(oldMacroMode);
		}

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
	 * @throws CircularDefinitionException
	 *             for circular definition
	 */
	public GeoElement[] processValidExpression(ValidExpression ve)
			throws MyError, CircularDefinitionException {
		return processValidExpression(ve,
				new EvalInfo(!cons.isSuppressLabelsActive(), true));
	}

	/**
	 * processes valid expression.
	 *
	 * @param exp
	 *            expression to process
	 * @param info
	 *            processing information
	 * @throws MyError
	 *             e.g. on wrong syntax
	 * @throws CircularDefinitionException
	 *             for circular definition
	 * @return resulting geos
	 */
	public GeoElement[] processValidExpression(ValidExpression exp,
			EvalInfo info) throws MyError, CircularDefinitionException {
		EvalInfo evalInfo = info;
		// check for existing labels
		String[] labels = exp.getLabels();
		GeoElement replaceable = getReplaceable(labels);
		// detect clashes caused by unstable output length in XML
		// in such case just rename the old geo and continue
		if (replaceable != null && replaceable.isSecondaryOutput() && kernel.getLoadingMode()) {
			replaceable.setLabel(replaceable.getFreeLabel(replaceable.getLabelSimple()));
			replaceable = null;
		}
		if (replaceable instanceof HasArbitraryConstant) {
			HasArbitraryConstant hasConstant = (HasArbitraryConstant) replaceable;
			evalInfo = evalInfo.withArbitraryConstant(hasConstant.getArbitraryConstant());
		}

		GeoElement[] ret;
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		if (replaceable != null) {
			evalInfo = evalInfo.withRedefinition(true);
			cons.setSuppressLabelCreation(true);
			isRedefining = true;
			if (replaceable instanceof GeoNumeric && !replaceable.getSendValueToCas()) {
				evalInfo = evalInfo.withSymbolicMode(SymbolicMode.NONE);
			}
        }

		// we have to make sure that the macro mode is
		// set back at the end
		try {
			ret = doProcessValidExpression(exp, evalInfo);
			if (ret == null) { // eg (1,2,3) running in 2D
				if (isFreehandFunction(exp)) {
					return kernel.lookupLabel(exp.getLabel()).asArray();
				}
				throw new MyError(loc,
						loc.getInvalidInputError() + ":\n" + exp);
			}
		} finally {
			isRedefining = false;
			cons.setSuppressLabelCreation(oldMacroMode);
		}
		if (!info.getKeepDefinition()) {
			stripDefinition(ret);
		}
		processReplace(replaceable, ret, exp, evalInfo);

		return ret;
	}

	/**
	 * Process given expression silently (without adding to construction or labeling)
	 * @param ve expression
	 * @return resulting elements
	 * @throws MyError when expression is invalid
	 * @throws CircularDefinitionException for circular definition
	 */
	public GeoElement[] processValidExpressionSilent(ValidExpression ve)
			throws MyError, CircularDefinitionException {
		boolean oldSuppressLabel = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);
		try {
			return processValidExpression(ve, new EvalInfo(false));
		} finally {
			cons.setSuppressLabelCreation(oldSuppressLabel);
		}
	}

	private void stripDefinition(GeoElement[] elements) {
		for (GeoElement element: elements) {
			element.setDefinition(null);
		}
	}

	private boolean isFreehandFunction(ValidExpression expression) {
		return expression.isTopLevelCommand(loc.getFunction("freehand"));
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
					if (geo.isProtected(EventType.UPDATE)) {
						throw new MyError(loc, Errors.IllegalAssignment,
								Errors.AssignmentToFixed.getError(loc), ":\n",
								geo.getLongDescription());
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
			if (replaceable instanceof GeoNumeric) {
				((GeoNumeric) replaceable).extendMinMax(ret[0]);
			}
			RuleCollection rule = info.getRedefinitionRule();
			if (rule != null && !rule.allowed(replaceable,
					ret[0])) {
				Log.debug("Cannot change " + replaceable.getGeoClassType() + " to "
						+ ret[0].getGeoClassType());
				// Set undefined
				ret[0] = replaceable;
				geoElementSetups.forEach(setup -> setup.applyTo(replaceable));
				replaceable.setUndefined();
				replaceable.updateRepaint();
				throw new MyError(loc, Errors.ReplaceFailed);
			} else
			// a changeable replaceable is not redefined:
			// it gets the value of ret[0]
			// (note: texts are always redefined)
			if (!info.mayRedefineIndependent()
					&& ret[0].isIndependent()
					&& replaceable.isChangeable()
					&& !replaceable.isGeoText()) {
				try {
					if (compatibleFunctions(replaceable, ret[0])) {
						replaceable.set(ret[0]);
						ret[0] = replaceable;
						geoElementSetups.forEach(setup -> setup.applyTo(replaceable));
						replaceable.updateRepaint();
					} else {
						ret[0] = replaceable;
						geoElementSetups.forEach(setup -> setup.applyTo(replaceable));
						replaceable.setUndefined();
						replaceable.updateRepaint();
						throw new MyError(loc, Errors.ReplaceFailed);
					}
				} catch (Exception e) {
					throw new MyError(loc, Errors.IllegalAssignment,
							replaceable.getLongDescription(), "     =     ",
							ret[0].getLongDescription());
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
							&& compatibleTypes(replaceable, ret[0])) {
						// copy equation style
						ret[0].setVisualStyle(replaceable);
						replaceable.set(ret[0]);
						geoElementSetups.forEach(setup -> setup.applyTo(replaceable));
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

					// STANDARD CASE: REDEFINED
					else if (!(info.isPreventingTypeChange())
							|| compatibleTypes(replaceable,
							ret[0])) {
						GeoElement newGeo = ret[0];
						geoElementSetups.forEach(setup -> setup.applyTo(newGeo));
						GeoCasCell cell = replaceable.getCorrespondingCasCell();
						if (cell != null) {
							// this is a ValidExpression since we don't get
							// GeoElements from parsing
							StringBuilder oldXML = cons.getCurrentUndoXML(false);
							ValidExpression vexp = (ValidExpression) ve
									.unwrap();
							cell.setAssignmentType(AssignmentType.DEFAULT);
							cell.setInput(vexp.toAssignmentString(
									StringTemplate.defaultTemplate,
									cell.getAssignmentType()));
							processCasCell(cell, false, oldXML.toString());
						} else {
							cons.replace(replaceable, newGeo, info);
						}
						// now all objects have changed
						// get the new object with same label as our result
						String newLabel = newGeo.isLabelSet()
								? newGeo.getLabelSimple()
								: replaceable.getLabelSimple();
						ret[0] = kernel.lookupLabel(newLabel);
					} else {
						// Set undefined
						ret[0] = replaceable;
						geoElementSetups.forEach(setup -> setup.applyTo(replaceable));
						replaceable.setUndefined();
						replaceable.updateRepaint();
						throw new MyError(loc, Errors.ReplaceFailed);
					}
				} catch (CircularDefinitionException e) {
					throw e;
				} catch (Exception | MyError e) {
					if (replaceable.getLabelSimple() != null) {
						Log.debug(e);
					} else {
						Log.debug("Replacing unlabeled element");
					}
					throw new MyError(loc, Errors.ReplaceFailed);
				}
			}
		}
	}

	public boolean isRedefining() {
		return this.isRedefining;
	}

	private static boolean isFunctionIneq(GeoElement geo) {
		return geo instanceof FunctionalNVar
				&& (((FunctionalNVar) geo).isBooleanFunction()
				|| ((FunctionalNVar) geo).isForceInequality());
	}

	private boolean compatibleFunctions(GeoElement replaceableGeo, GeoElement returnGeo) {
			return (isFunctionIneq(replaceableGeo)
				&& isFunctionIneq(returnGeo)) // both ineq functions
				|| (!isFunctionIneq(replaceableGeo)
				&& !isFunctionIneq(returnGeo)); // none ineq functions
	}

	private static boolean compatibleTypes(GeoElement type, GeoElement type2) {
		if (type2.getGeoClassType().equals(type.getGeoClassType())) {
			return true;
		}
		if (type2.getGeoClassType().equals(GeoClass.NUMERIC)
				&& type.getGeoClassType().equals(GeoClass.ANGLE)) {
			return true;
		}
		if (type.getGeoClassType().equals(GeoClass.NUMERIC)
				&& type2.getGeoClassType().equals(GeoClass.ANGLE)) {
			return true;
		}
		if (type2.getGeoClassType().equals(GeoClass.LIST)
				&& type.getGeoClassType().equals(GeoClass.VECTOR)) {
			return true;
		}
		return type.getGeoClassType().equals(GeoClass.LIST)
				&& type2.getGeoClassType().equals(GeoClass.VECTOR);
	}

	/**
	 * Processes valid expression
	 *
	 * @param ve2
	 *            expression to process
	 * @param info
	 *            flags for processing
	 * @return array of geos
	 * @throws MyError
	 *             if syntax error occurs
	 * @throws CircularDefinitionException
	 *             if circular definition occurs
	 */
	public final GeoElement[] doProcessValidExpression(
			final ValidExpression ve2,
			EvalInfo info) throws MyError, CircularDefinitionException {
		GeoElement[] ret = null;

		ExpressionValue ve = ve2;

		if (info.autoAddDegree()) {
			// sin(15) -> sin(15deg)
			ve = ve2.traverse(DegreeReplacer.getReplacer(kernel));
			if (kernel.degreesMode()) {
				// asin(x) -> asind(x)
				ve = ve.traverse(ArcTrigReplacer.getReplacer());
			}
			if (ve instanceof ValidExpression) {
				((ValidExpression) ve).setLabels(ve2.getLabels());
			}
		}

		if (ve instanceof ExpressionNode) {
			ExpressionNode node = (ExpressionNode) ve;
			ret = processExpressionNode(node, info);
			boolean singleReturnValue = ret != null && ret.length == 1;
			if (ret != null && ret.length > 0
					&& ret[0] instanceof GeoScriptAction) {
				if (info.isScripting()) {
					((GeoScriptAction) ret[0]).perform();
				}
				return new GeoElement[] {};
			} else if (info.isCopyingPlainVariables() && singleReturnValue) {
				boolean isPlainVariable = node.isLeaf();
				boolean returnValueIsInput = node.unwrap() == ret[0];
				if (isPlainVariable && returnValueIsInput) {
					ret = array(dependentGeoCopy(ret[0], node));
				}
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
			ret = processEquationOrAssignment((Equation) ve, ve.wrap(), info, null);
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

		if (ret != null) {
			GeoElement[] finalRet = ret;
			geoElementSetups.forEach(setup -> setup.applyTo(finalRet[0]));
		}

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
		if (!enableStructures() && !info.isForceFunctionsEnabled()) {
			throw new MyError(loc, Errors.InvalidInput);
		}
		String varName = fun.getVarString(StringTemplate.defaultTemplate);
		if (varName.equals(Unicode.theta_STRING)
				&& !kernel.getConstruction()
						.isRegisteredFunctionVariable(Unicode.theta_STRING)
				&& fun.getExpression().evaluatesToNumber(true)) {
			String label = fun.getLabel();
			MyVecNode ve = new MyVecNode(kernel, fun.getExpression(),
					fun.getFunctionVariable().wrap());
			ve.setMode(Kernel.COORD_POLAR);
			// TODO the "r" check is there to allow r=theta in the
			// future
			if (!"r".equals(label)) {
				ve.setLabel(label);
			}
			ExpressionNode exp = ve.deepCopy(kernel).traverse(VariableReplacer
					.getReplacer(varName, fun.getFunctionVariable(), kernel))
					.wrap();
			exp.resolveVariables(info);
			GeoElement[] ret = getParamProcessor().processParametricFunction(
					exp, exp.evaluate(StringTemplate.defaultTemplate),
					new FunctionVariable[] { fun.getFunctionVariable() },
					"X".equals(ve.getLabel()) ? null : ve.getLabel(), info);
			if (ret != null) {
				return ret;
			}
		}
		if (!fun.initFunction(info.withSimplifying(false))) {
			return processFunctionAsSurface(fun, info);
		}

		String label = fun.getLabel();
		GeoFunction f;

		GeoElement[] vars = fun.getGeoElementVariables(
				info.getSymbolicMode());
		boolean isIndependent = true;
		for (int i = 0; vars != null && i < vars.length; i++) {
			if (Inspecting.isDynamicGeoElement(vars[i])) {
				isIndependent = false;
			}
		}
		// check for interval

		final ExpressionNode en = fun.getExpression();
		if (fun.isForceInequality()) {
			en.setForceInequality();
		}
		if (en.getOperation().equals(Operation.FUNCTION)) {
			ExpressionValue left = en.getLeft();
			ExpressionValue right = en.getRight();
			// the isConstant() here makes difference between f(1) and f(x), see
			// #2155
			if (left.isLeaf() && left.isGeoElement() && right.isLeaf()
					&& right.isNumberValue() && !right.isConstant()
					&& !isIndependent) {
				f = (GeoFunction) dependentGeoCopy(
						((GeoFunctionable) left).getGeoFunction(), en);
				f.setShortLHS(fun.getShortLHS());
				f.setLabel(label);
				return array(f);
			}
		}

		if (isIndependent) {
			f = new GeoFunction(cons, fun, info.isSimplifyingIntegers());
			f.getIneqs();
			f.setForceInequality(forceInequality(en, f));
		} else {
			f = kernel.getAlgoDispatcher().dependentFunction(fun, info);
			if (label == null) {
				label = AlgoDependentFunction.getDerivativeLabel(fun);
			}
		}

		geoElementSetups.forEach(setup -> setup.applyTo(f));

		if (f.validate(label == null)) {
			f.setShortLHS(fun.getShortLHS());
			f.setLabel(label);
			return array(f);
		}
		f.remove();
		throw new MyError(loc, Errors.InvalidFunctionA,
				fun.getFunctionVariable().getSetVarString());
	}

	private boolean forceInequality(ExpressionNode en, FunctionalNVar fun) {
		// use parser flags if undefined, actual expression type otherwise
		if (en.unwrap() instanceof MyDouble && Double.isNaN(en.evaluateDouble())) {
			return en.isForceInequality();
		}
		return fun.isBooleanFunction();
	}

	private GeoElement[] processFunctionAsSurface(Function fun, EvalInfo info) {
		ExpressionNode copy = fun.getExpression().deepCopy(kernel);
		return getParamProcessor().processParametricFunction(
				fun.getExpression(),
				copy.evaluate(StringTemplate.defaultTemplate),
				new FunctionVariable[] { fun.getFunctionVariable() },
				ParametricProcessor.getParametricLabel(fun), info);
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
	 *            multiplier
	 * @param loc2
	 *            variable
	 * @return degree if successful, -1 otherwise
	 */
	public int getPolyCoeffs(ExpressionNode cx, ExpressionValue[] coefX,
			ExpressionNode mult, GeoNumeric loc2) {
		if (!cx.containsDeep(loc2)) {
			add(coefX, 0, mult.multiply(cx));
			return 0;
		} else if (cx.getOperation() == Operation.PLUS
				|| cx.getOperation() == Operation.INVISIBLE_PLUS) {
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
					&& DoubleUtil.isEqual(2, cx.getRight().evaluateDouble())) {
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
	 *            multiplier
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
		return childrenOK && ((coefX[1] == null && coefX[2] == null)
				|| (coefX[3] == null && coefX[4] == null));
	}

	private static void add(ExpressionValue[] coefX, int i,
			ExpressionNode scale) {
		if (coefX[i] == null) {
			coefX[i] = scale;
		} else {
			coefX[i] = scale.plus(coefX[i]);
		}
	}

	private GeoElement dependentGeoCopy(GeoElement origGeoNode, ExpressionNode node) {
		AlgoDependentGeoCopy algo = new AlgoDependentGeoCopy(cons, origGeoNode, node);
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
		if (!enableStructures()) {
			throw new MyError(loc, Errors.InvalidInput);
		}
		if (!fun.initFunction(info)) {
			return getParamProcessor().processParametricFunction(
					fun.getExpression(),
					fun.getExpression()
							.evaluate(StringTemplate.defaultTemplate),
					fun.getFunctionVariables(), fun.getLabel(), info);
		}

		String label = fun.getLabel();
		GeoFunctionNVar gf;

		GeoElement[] vars = fun.getGeoElementVariables(
				info.getSymbolicMode());
		boolean isIndependent = vars == null || vars.length == 0;

		if (isIndependent) {
			gf = new GeoFunctionNVar(cons, fun, info.isSimplifyingIntegers());
			gf.getIneqs();
			final ExpressionNode en = fun.getExpression();
			if (fun.isForceInequality()) {
				en.setForceInequality();
			}
			gf.setForceInequality(forceInequality(en, gf));
		} else {
			gf = dependentFunctionNVar(fun);
		}
		gf.setShortLHS(fun.getShortLHS());
		gf.setLabel(label);
		if (!gf.validate()) {
			gf.remove();
			throw new MyError(loc, Errors.InvalidInput);
		}

		return array(gf);
	}

	/**
	 * Multivariate Function depending on coefficients of arithmetic expressions
	 * with variables, e.g. f(x,y) = a x^2 + b y^2
	 */
	private GeoFunctionNVar dependentFunctionNVar(
			FunctionNVar fun) {
		AlgoDependentFunctionNVar algo = new AlgoDependentFunctionNVar(cons,
				fun);
		return algo.getFunction();
	}

	/**
	 * Processes given equation to an array containing single line / conic /
	 * implicit polynomial. May throws MyError for degree 0 equations (see allowConstant)
	 *
	 * @param equ
	 *            equation
	 * @param def
	 *            definition node (not same as equation in case of list1(2))
	 * @param info
	 *            processing information
	 * @param allowConstant
	 *            true to allow equations like 2=3 or x=x, false to throw
	 *            MyError for those
	 * @return line, conic, implicit poly or plane
	 * @throws MyError
	 *             e.g. for invalid operation
	 */
	public final GeoElement[] processEquation(Equation equ, ExpressionNode def,
			boolean allowConstant, EvalInfo info) throws MyError {
		return processEquation(equ, def, allowConstant, info, null);
	}

	/**
	 * Processes given equation to an array containing single line / conic /
	 * implicit polynomial. Throws MyError for degree 0 equations, eg. 1=2 or
	 * x=x. Special handling for equations like z=7 that may be handled as assignment.
	 */
	private GeoElement[] processEquationOrAssignment(Equation equ, ExpressionNode def,
			EvalInfo info, ExpressionValue evaluatedDef) throws MyError {
		if (!enableStructures()) {
			throw new MyError(loc, Errors.InvalidInput);
		}
		if (info.getSymbolicMode() == SymbolicMode.SYMBOLIC_AV) {
			return evalSymbolic(equ, info).asArray();
		}

		ExpressionValue lhs = equ.getLHS().unwrap();
		// z = 7
		if (lhs instanceof FunctionVariable
				&& (!equ.getRHS().containsFreeFunctionVariable(null)
				&& !equ.getRHS().evaluatesToNumber(true)
				|| equ.getRHS().any(Inspecting::isComplexNumber))) {
			equ.getRHS().setLabel(lhs.toString(StringTemplate.defaultTemplate));
			try {
				GeoElement[] ret = processValidExpression(equ.getRHS(), info);
				saveComplexSolutionVariableForPoints(ret,
						((FunctionVariable) lhs).getSetVarString());
				return ret;
			} catch (Exception e) {
				Log.debug(e);
			}
		}
		// s = t^2
		String singleLeftVariable = null;
		if (lhs instanceof Variable || lhs instanceof GeoDummyVariable) {
			singleLeftVariable = lhs.toString(StringTemplate.defaultTemplate);
			if (kernel.lookupLabel(singleLeftVariable) != null) {
				singleLeftVariable = null;
			}
		}
		if ("X".equals(singleLeftVariable)) {
			return getParamProcessor().processXEquation(equ, info);
		}
		if ("r".equals(singleLeftVariable)) {
			try {
				equ.getRHS().setLabel(equ.getLabel());
				return doProcessValidExpression(equ.getRHS(), info);
			} catch (Exception e) {
				Log.debug(e);
			}
		}

		if (singleLeftVariable != null && equ.getLabel() == null && info.isAssignmentAllowed()) {
			equ.getRHS().setLabel(lhs.toString(StringTemplate.defaultTemplate));
			try {
				return processValidExpression(equ.getRHS());
			} catch (Exception e) {
				Log.debug(e);
			}
		}
		if (lhs instanceof MyDouble
				&& MyDouble.exactEqual(lhs.evaluateDouble(), MyMath.DEG)) {
			equ.getRHS().setLabel("deg");
			try {
				return processValidExpression(equ.getRHS());
			} catch (Exception e) {
				Log.debug(e);
			}
		}

		// z(x) = sin(x), see #5484
		if (lhs instanceof ExpressionNode
				&& lhs.isOperation(Operation.ZCOORD)
				&& ((ExpressionNode) lhs).getLeft()
						.unwrap() instanceof FunctionVariable) {
			equ.getRHS().setLabel("z");
			try {
				return processValidExpression(equ.getRHS());
			} catch (Exception e) {
				Log.debug(e);

			}
		}
		return processEquation(equ, def,
				kernel.getConstruction().isFileLoading(), info, evaluatedDef);
	}

	/**
	 * In case there are GeoPoints being created, which might later be forced to be printed
	 * e.g. in the form { <b>x</b> = i, <b>x</b> = -i } (as output of CSolve),
	 * stores the name of the variable
	 * @param elements GeoElements
	 * @param var Variable name
	 */
	private void saveComplexSolutionVariableForPoints(GeoElement[] elements, String var) {
		Arrays.stream(elements).filter(element -> element instanceof GeoPoint)
				.forEach(point -> ((GeoPoint) point).setComplexSolutionVar(var));
	}

	private GeoElement[] processEquation(Equation equ, ExpressionNode def,
			boolean allowConstant, EvalInfo info, ExpressionValue evaluatedDef) throws MyError {
		equ.initEquation();

		// check no terms in z
		checkNoTermsInZ(equ);
		checkNoTheta(equ);
		if (equ.getLHS().evaluatesToList() || equ.getRHS().evaluatesToList()) {
			return proceeEquationList(equ);
		}

		if (equ.isFunctionDependent() || equ.isForceFunction()) {
			return functionOrImplicitPoly(equ, def, info, evaluatedDef);
		}
		int deg = equ.mayBePolynomial() && !equ.hasVariableDegree()
				? Math.max(equ.preferredDegree(), equ.degree()) : -1;
		// consider algebraic degree of equation
		// check not equation of eg plane
		switch (deg) {
		// linear equation -> LINE
		case 0:
			if (allowConstant) {
				return functionOrImplicitPoly(equ, def, info, evaluatedDef);
			}
			return processLine(equ, def, info);
		case 1:
			return processLine(equ, def, info);

		// quadratic equation -> CONIC
		case 2:
			return processConic(equ, def, info);
		// pi = 3 is not an equation, #1391

			// if constants are allowed, build implicit poly
		default:
			// test for "y= <rhs>" here as well
			return functionOrImplicitPoly(equ, def, info, evaluatedDef);
		}

	}

	private GeoElement[] proceeEquationList(Equation equ) {
		AlgoDependentEquationList algo = new AlgoDependentEquationList(cons,
				equ);
		if (algo.validate()) {
			GeoList list = algo.getList();
			list.setLabel(equ.getLabel());
			return list.asArray();
		} else {
			algo.getList().remove();
			throw new MyError(loc, Errors.InvalidEquation,
					equ.toString(StringTemplate.defaultTemplate));
		}
	}

	private GeoElement[] functionOrImplicitPoly(Equation equ,
			ExpressionNode def, EvalInfo info, ExpressionValue evaluatedDef) {
		String lhsStr = equ.getLHS().toString(StringTemplate.xmlTemplate)
				.trim();

		if ("y".equals(lhsStr)
				&& canEvaluateToFunction(equ)
				&& !equ.getRHS().containsFreeFunctionVariable("y")
				&& !equ.getRHS().containsFreeFunctionVariable("z")) {

			Function fun = new Function(kernel, equ.getRHS());
			// try to use label of equation
			fun.setLabel(equ.getLabel());
			fun.setShortLHS("y");
			return processFunction(fun,
					new EvalInfo(!cons.isSuppressLabelsActive()));
		}

		if ("z".equals(lhsStr)
				&& !equ.getRHS().containsFreeFunctionVariable("z")
				&& kernel.lookupLabel("z") == null) {
			FunctionVariable x = new FunctionVariable(kernel, "x");
			FunctionVariable y = new FunctionVariable(kernel, "y");
			FunctionNVar fun = new FunctionNVar(equ.getRHS(),
					new FunctionVariable[] { x, y });
			// try to use label of equation
			fun.setLabel(equ.getLabel());
			fun.setShortLHS("z");
			return processFunctionNVar(fun,
					new EvalInfo(!cons.isSuppressLabelsActive()));
		}

		return processImplicitPoly(equ, def, info, evaluatedDef);
	}

	private boolean canEvaluateToFunction(Equation equ) {
		return !equ.isForcedImplicitPoly()
				&& !equ.isForcedConic()
				&& !equ.isForcedLine();
	}

	private void checkNoTheta(Equation equ) {
		if (equ.getLHS().containsFreeFunctionVariable(Unicode.theta_STRING)
				|| equ.getRHS()
						.containsFreeFunctionVariable(Unicode.theta_STRING)) {
			throw new MyError(loc, Errors.InvalidEquation);
		}

	}

	/**
	 * @param equ
	 *            equation
	 */
	protected void checkNoTermsInZ(Equation equ) {
		if (!equ.getNormalForm().isFreeOf('z')) {
			equ.setIsPolynomial(false);
		}
	}

	/**
	 * @param equ
	 *            equation
	 * @param def
	 *            definition expression (equation without any simplifications)
	 * @param info
	 *            evaluation flags
	 * @return resulting line
	 *
	 */
	protected GeoElement[] processLine(Equation equ, ExpressionNode def,
			EvalInfo info) {
		GeoLine line;
		String label = equ.getLabel();
		Polynomial lhs = equ.getNormalForm();
		boolean isExplicit = equ.isExplicit("y");
		boolean isIndependent = lhs.isConstant(info) && !def.any(Inspecting::isDynamicGeoElement);
		if (isIndependent) {
			// get coefficients
			double a = lhs.getCoeffValue("x");
			double b = lhs.getCoeffValue("y");
			double c = lhs.getCoeffValue("");
			line = new GeoLine(cons, a, b, c);
		} else {
			line = dependentLine(equ);
		}

		if (isExplicit) {
			line.setToExplicitForm();
		}

		line.showUndefinedInAlgebraView(true);
		line.setDefinition(def);
		setEquationLabelAndVisualStyle(line, label, info);

		return array(line);
	}

	/**
	 * @param geo
	 *            line or conic
	 * @param label
	 *            new label
	 * @param info
	 *            evaluation flags
	 */
	protected void setEquationLabelAndVisualStyle(GeoElementND geo,
			String label, EvalInfo info) {
		if (kernel.getApplication().isUnbundledGraphing()) {
			geo.setObjColor(geo.getAutoColorScheme()
					.getNext(!cons.getKernel().isSilentMode()));
			geo.setLineOpacity(
					EuclidianStyleConstants.OBJSTYLE_DEFAULT_LINE_OPACITY_EQUATION_GEOMETRY);
		}
		if (geo.isFunctionOrEquationFromUser()) {
			geo.setFixed(true);
		}

		customizeEquationForm(geo);

		if (info.isLabelOutput()) {
			geo.setLabel(label);
		}
	}

	private void customizeEquationForm(GeoElementND geo) {
		EquationBehaviour equationBehaviour = kernel.getEquationBehaviour();
		if (equationBehaviour == null) {
			return;
		}
		if (geo instanceof LinearEquationRepresentable) {
			LinearEquationRepresentable.Form equationForm =
					equationBehaviour.getLinearAlgebraInputEquationForm();
			if (equationForm != null) {
				((LinearEquationRepresentable) geo).setEquationForm(equationForm);
			}
		} else if (geo instanceof QuadraticEquationRepresentable) {
			QuadraticEquationRepresentable.Form equationForm =
					equationBehaviour.getConicAlgebraInputEquationForm();
			if (equationForm != null) {
				((QuadraticEquationRepresentable) geo).setEquationForm(equationForm);
			}
		}  else if (geo instanceof GeoImplicitCurve) {
			GeoImplicit.Form equationForm =
					equationBehaviour.getCurveAlgebraInputEquationForm();
			if (equationForm != null) {
				((GeoImplicitCurve) geo).setEquationForm(equationForm);
			}
		}

	}

	/**
	 * Line dependent on coefficients of arithmetic expressions with variables,
	 * represented by trees. e.g. y = k x + d
	 */
	private GeoLine dependentLine(Equation equ) {
		AlgoDependentLine algo = new AlgoDependentLine(cons, equ);
		return algo.getLine();
	}

	/**
	 * @param equ
	 *            equation
	 * @param def
	 *            definition expression
	 * @param info
	 *            evaluation flags
	 * @return resulting conic
	 */
	public GeoElement[] processConic(Equation equ, ExpressionNode def,
			EvalInfo info) {
		double a, b, c, d, e, f;
		GeoConic conic;
		String label = equ.getLabel();
		Polynomial lhs = equ.getNormalForm();

		boolean isExplicit = equ.isExplicit("y");
		boolean isSpecific = !isExplicit
				&& (equ.isExplicit("yy") || equ.isExplicit("xx"));
		boolean isIndependent = lhs.isConstant(info);

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
		} else if (isSpecific || conic.getType() == GeoConicNDConstants.CONIC_CIRCLE) {
			conic.setToSpecificForm();
		}
		conic.setDefinition(def);
		setEquationLabelAndVisualStyle(conic, label, info);

		return array(conic);
	}

	/**
	 * Conic dependent on coefficients of arithmetic expressions with variables,
	 * represented by trees.
	 */
	private GeoConic dependentConic(Equation equ) {
		AlgoDependentConic algo = new AlgoDependentConic(cons, equ);
		return algo.getConic();
	}

	/**
	 * @param equ
	 *            equation
	 * @param definition
	 *            definition node (not same as equation in case of list1(2))
	 * @param info
	 *            evaluation flags
	 * @return resulting implicit polynomial
	 */
	protected GeoElement[] processImplicitPoly(Equation equ,
			ExpressionNode definition, EvalInfo info, ExpressionValue evaluatedDef) {
		String label = equ.getLabel();
		Polynomial lhs = equ.getNormalForm();
		boolean isIndependent = !equ.isFunctionDependent()
				&& lhs.isConstant(info)
				&& !equ.hasVariableDegree()
				&& (equ.isPolynomial() || !equ.any(Inspecting::isDynamicGeoElement));
		GeoImplicit poly;
		GeoElement geo;
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
			AlgoDependentImplicitPoly algo = new AlgoDependentImplicitPoly(cons,
					equ, definition, true, evaluatedDef);

			geo = algo.getGeo(); // might also return
			// Line or Conic
			geo.setDefinition(definition);
		}
		setEquationLabelAndVisualStyle(geo, label, info);
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
			EvalInfo info) throws MyError {
		ExpressionNode n = node;
		if (info.getSymbolicMode() == SymbolicMode.SYMBOLIC_AV && !containsText(node)
				&& !willResultInSlider(node)) {
			return new GeoElement[] { evalSymbolic(node, info) };
		}
		// command is leaf: process command
		if (n.isLeaf()) {
			ExpressionValue leaf = n.getLeft();
			if (leaf instanceof Command) {
				Command c = (Command) leaf;
				c.setLabels(n.getLabels());
				return processCommand(c, info);
			} else if (leaf instanceof Equation) {
				Equation eqn = (Equation) leaf;
				eqn.setLabels(n.getLabels());
				return processEquationOrAssignment(eqn, n, info, null);
			} else if (leaf instanceof Function) {
				Function fun = (Function) leaf;
				fun.setLabels(n.getLabels());
				if (node.isForceSurface()) {
					fun.initFunction(info.withSimplifying(false));
					return getParamProcessor().complexSurface(fun.getExpression(),
							fun.getFunctionVariable(), fun.getLabel());
				}
				fun.setForceInequality(node.isForceInequality());
				return processFunction(fun, info);
			} else if (leaf instanceof FunctionNVar) {
				FunctionNVar fun = (FunctionNVar) leaf;
				fun.setLabels(n.getLabels());
				fun.setForceInequality(node.isForceInequality());
				return processFunctionNVar(fun, info);
			}

		}
		ExpressionValue eval; // ggb3D : used by AlgebraProcessor3D in
		// extended processExpressionNode

		// ELSE: resolve variables and evaluate expressionnode
		n.resolveVariables(info);

		if (n.isLeaf() && n.getLeft().isExpressionNode()) {
			// we changed f' to f'(x) -> clean double wrap
			ExpressionNode unwrapped = n.getLeft().wrap();
			n.copyAttributesTo(unwrapped);
			unwrapped.setLabels(n.getLabels());
			n = unwrapped;
		}

		String label = n.getLabel();
		if (n.containsFreeFunctionVariable(null)) {
			n = makeFunctionNVar(n).wrap();
		}
		eval = n.evaluate(StringTemplate.defaultTemplate);
		if (eval instanceof ValidExpression && label != null) {
			((ValidExpression) eval).setLabel(label);
		}
		boolean dollarLabelFound = false;

		ExpressionNode myNode = n;
		if (myNode.isLeaf()) {
			myNode = myNode.getLeftTree();
		}

		// leaf (no new label specified): just return the existing GeoElement
		if (eval.isGeoElement() && n.getLabel() == null
				&& !myNode.getOperation().equals(Operation.ELEMENT_OF)
				&& !myNode.getOperation().equals(Operation.IF_ELSE)) {
			// take care of spreadsheet $ names: don't loose the wrapper
			// ExpressionNode here
			// check if we have a Variable
			switch (myNode.getOperation()) {
			case DOLLAR_VAR_COL:
			case DOLLAR_VAR_ROW:
			case DOLLAR_VAR_ROW_COL:
				// don't do anything here: we need to keep the wrapper
				// ExpressionNode
				// and must not return the GeoElement here
				dollarLabelFound = true;
				break;

			default:
				// return the GeoElement
				return new GeoElement[] {(GeoElement) eval};
			}
		}

		if (eval instanceof BooleanValue) {
			return processBoolean(n, eval);
		} else if (eval instanceof NumberValue) {
			if (n.isForcedFunction()) {
				return processFunction(new Function(kernel, eval.wrap()), info);
			}

			// complex number stored in XML as exp="3" so looks like a GeoNumeric
			if (n.isForcedPoint()) {
				return processPointVector(n, eval, info);
			}

			if (n.isForceInequality()) {
				return processFunction(new Function(kernel, n), info);
			}

			return processNumber(n, eval, info);
		} else if (eval instanceof VectorValue) {
			return processPointVector(n, eval, info);
		} else if (eval instanceof Vector3DValue) {
			return processPointVector3D(n, eval, info);
		} else if (eval instanceof TextValue) {
			return processText(n, eval, info);
		} else if (eval instanceof MyList) {
			return processList(n, (MyList) eval, info);
		} else if (eval instanceof EquationValue && !n.unwrap().isGeoElement()) {
			Equation eq = ((EquationValue) eval).getEquation();
			eq.setFunctionDependent(true);
			eq.setLabel(n.getLabel());
			return processEquationOrAssignment(eq, n, info, eval);
		} else if (eval instanceof Function) {
			return processFunction((Function) eval, info);
		} else if (eval instanceof FunctionNVar) {

			return processFunctionNVar((FunctionNVar) eval, info);
		}
		// we have to process list in case list=matrix1(1), but not when
		// list=list2
		else if (eval instanceof GeoList && myNode.hasOperations()) {
			return processList(n, ((GeoList) eval).getMyList(), info);
		} else if (eval.isGeoElement()) {

			// e.g. B1 = A1 where A1 is a GeoElement and B1 does not exist yet
			// create a copy of A1
			if (n.getLabel() != null || dollarLabelFound) {
				return array(dependentGeoCopy(n.getLabel(), n));
			}
		}

		// if we get here, nothing worked
		Log.debug("Unhandled ExpressionNode: " + eval + ", " + eval.getClass());
		return null;
	}

	private boolean containsText(ExpressionNode ev) {
		return ev.any(Inspecting::isText);
	}

	private boolean willResultInSlider(ExpressionNode node) {
		return node.unwrap() instanceof Command
				&& ((Command) node.unwrap()).getName().equals("Slider");
	}

	/**
	 * Make function or nvar function from expression, using all function
	 * variables it has
	 *
	 * @param n
	 *            expression
	 * @return function or nvar function
	 */
	public FunctionNVar makeFunctionNVar(ExpressionNode n) {
		FunctionVarCollector fvc = FunctionVarCollector.getCollector();
		n.traverse(fvc);
		return kernel.getArithmeticFactory().newFunction(n, fvc.buildVariables(kernel));
	}

	/**
	 * @param n
	 *            expression node
	 * @param evaluate
	 *            evaluated node
	 * @param info
	 *            flags for setting label, using symbolic mode
	 * @return value
	 */
	GeoElement[] processNumber(ExpressionNode n, ExpressionValue evaluate,
			EvalInfo info) {
		GeoElement ret;
		boolean isIndependent = !n.any(Inspecting::isDynamicGeoElement);
		MyDouble val = ((NumberValue) evaluate).getNumber();
		boolean isAngle = val.isAngle() || n.isForceAngle();
		double value = val.getDouble();

		if (isIndependent) {
			if (isAngle) {
				boolean keepDegrees = n.getOperation().doesReturnDegrees()
						&& !app.getConfig().isAngleUnitSettingEnabled();
				if (app.getSettings().getAlgebra().isAngleConversionRestricted()) {
					keepDegrees = true;
				}
				ret = new GeoAngle(cons, value, AngleStyle.UNBOUNDED, keepDegrees);
			} else {
				ret = new GeoNumeric(cons, value);
			}
			if (val instanceof MySpecialDouble) {
				((GeoNumeric) ret).setExactValue(val.toDecimal());
			}
			ret.setDefinition(n);
		} else {
			ret = dependentNumber(n, isAngle, evaluate).toGeoElement();
		}

		if (info.isSymbolic() && ret instanceof HasSymbolicMode) {
			((HasSymbolicMode) ret).initSymbolicMode();
		}
		if (info.isLabelOutput()) {
			String label = n.getLabel();
			ret.setLabel(label);
			if (ret instanceof GeoNumeric && needsEngineeringNotation(ret)) {
				((GeoNumeric) ret).setEngineeringNotationMode(true);
			}
		} else {
			cons.removeFromConstructionList(ret);
		}

		return array(ret);
	}

	private boolean needsEngineeringNotation(GeoElement number) {
		return app.getSettings().getAlgebra().isEngineeringNotationEnabled()
				&& !app.getKernel().getLoadingMode()
				&& !app.getKernel().useSignificantFigures
				&& Math.abs(number.evaluateDouble())
				< Math.pow(10, -app.getKernel().getPrintDecimals());
	}

	/**
	 * Number dependent on arithmetic expression with variables, represented by
	 * a tree. e.g. t = 6z - 2
	 */
	private GeoNumberValue dependentNumber(ExpressionNode root,
			boolean isAngle, ExpressionValue evaluate) {
		AlgoDependentNumber algo = new AlgoDependentNumber(cons, root, isAngle,
				evaluate);
		if (algo.getNumber() instanceof GeoAngle
				&& app.getSettings().getAlgebra().isAngleConversionRestricted()) {
			((GeoAngle) algo.getNumber()).setKeepDegrees();
		}
		return algo.getNumber();
	}

	private GeoElement[] processList(ExpressionNode n, MyList evalList,
			EvalInfo info) {
		String label = n.getLabel();

		GeoList ret;

		// no operations or no variables are present, e.g.
		// { a, b, 7 } or { 2, 3, 5 } + {1, 2, 4}
		if (!n.hasOperations() || n.isConstant()) {

			// PROCESS list items to generate a list of geoElements
			ArrayList<GeoElement> geoElements = new ArrayList<>();
			boolean isIndependent = true;

			// make sure we don't create any labels for the list elements
			boolean oldMacroMode = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);

			int size = evalList.size();
			for (int i = 0; i < size; i++) {
				ExpressionNode en = evalList.get(i).wrap();
				// we only take one resulting object
				GeoElement[] results = processExpressionNode(en,
						new EvalInfo(false));
				GeoElement geo = results[0];
				// TODO APPS-5867 do we need more conditions here?
				if (Equation.isAlgebraEquation(geo)) {
					customizeEquationForm(geo);
				}
				// add to list
				geoElements.add(geo);
				if (geo.isLabelSet() || geo.isLocalVariable()
						|| !geo.isIndependent()) {
					isIndependent = false;
				}
			}
			cons.setSuppressLabelCreation(oldMacroMode);

			// Create GeoList object
			ret = kernel.getAlgoDispatcher().list(geoElements,
					isIndependent);
			if (info.isSymbolic()) {
				((HasSymbolicMode) ret).initSymbolicMode();
			}
			if (!evalList.isDefined() || (isIndependent && ret.isUndefinedMatrix())) {
				ret.setUndefined();
			}
			ret.setDefinition(n);
			if (info.isLabelOutput()) {
				ret.setLabel(label);
			}
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
		AlgoDependentListExpression algo = new AlgoDependentListExpression(cons,
				root);
		return algo.getList();
	}

	private GeoElement[] processText(ExpressionNode n,
			ExpressionValue evaluate, EvalInfo info) {
		GeoElement ret;
		String label = n.getLabel();

		boolean isIndependent = n.isConstant();

		if (isIndependent) {
			MyStringBuffer val = ((TextValue) evaluate).getText();
			ret = text(val.toValueString(StringTemplate.defaultTemplate));
		} else {
			ret = dependentText(n);
		}
		if (info.isLabelOutput()) {
			ret.setLabel(label);
		}
		return array(ret);
	}

	/**
	 * Text dependent on coefficients of arithmetic expressions with variables,
	 * represented by trees. e.g. text = "Radius: " + r
	 */
	private GeoText dependentText(ExpressionNode root) {
		AlgoDependentText algo = new AlgoDependentText(cons, root, true);
		return algo.getGeoText();
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

		boolean isIndependent = !n.any(Inspecting::isDynamicGeoElement);

		if (isIndependent) {

			ret = new GeoBoolean(cons);
			ret.setValue(((BooleanValue) evaluate).getBoolean());
			ret.setDefinition(n);

		} else {
			ret = (new AlgoDependentBoolean(cons, n, true)).getGeoBoolean();
		}
		ret.setLabel(label);
		return array(ret);
	}

	private static boolean isEquation(ExpressionValue ev) {
		return ev.unwrap() instanceof EquationValue
				&& !(ev.unwrap() instanceof NumberValue);
	}

	private GeoElement[] processPointVector(ExpressionNode n,
			ExpressionValue evaluate, EvalInfo info) {
		String label = n.getLabel();
		if (evaluate instanceof MyVecNode) {
			// force vector for CAS vector GGB-1492
			if (((MyVecNode) evaluate).isCASVector()) {
				n.setForceVector();
			}
			ExpressionValue x = ((MyVecNode) evaluate).getX();
			ExpressionValue y = ((MyVecNode) evaluate).getY();
			if (isEquation(x) && isEquation(y)) {
				return processEquationIntersect(x, y);
			}
			if (x.unwrap() instanceof MyVecNDNode || y.unwrap() instanceof MyVecNDNode) {
				throw new MyError(loc, Errors.InvalidInput);
			}
		}

		GeoVec2D p;
		if (!(evaluate instanceof VectorValue)) {
			// complex number in XML as eg exp="3" goes to 3+0i
			double real = evaluate.evaluateDouble();
			// exp="?" -> ?+i?
			double im = Double.isNaN(real) ? Double.NaN : 0;
			p = new GeoVec2D(kernel, real, im);
		} else {
			p = ((VectorValue) evaluate).getVector();

		}

		boolean polar = p.getToStringMode() == Kernel.COORD_POLAR;

		// we want z = 3 + i to give a (complex) GeoPoint not a GeoVector
		boolean complex = p.getToStringMode() == Kernel.COORD_COMPLEX;

		boolean isIndependent = !n.any(Inspecting::isDynamicGeoElement);

		// make point if complex parts are present, e.g. 3 + i
		if (complex) {
			n.setForcePoint();
		}
		else if (label != null) {
			if (!(n.isForcedPoint() || n.isForcedVector())) { // may be set by
				// MyXMLHandler
				if (isVectorLabel(label)) {
					n.setForceVector();
				} else {
					n.setForcePoint();
				}
			}
		}
		boolean isVector = n.shouldEvaluateToGeoVector();

		GeoPointVector vector;
		if (isIndependent) {
			// get coords
			double x = p.getX();
			double y = p.getY();
			if (isVector) {
				vector = kernel.getAlgoDispatcher().vector(x, y);
			} else {
				vector = kernel.getAlgoDispatcher().point(x, y, complex);
			}
			vector.setDefinition(n);
		} else {
			if (isVector) {
				vector = dependentVector(n);
			} else {
				vector = dependentPoint(n, complex);
			}
		}
		geoElementSetups.forEach(setup -> setup.applyTo(vector));
		if (polar) {
			vector.setMode(Kernel.COORD_POLAR);
		} else if (complex) {
			vector.setMode(Kernel.COORD_COMPLEX);
		}
		if (info.isLabelOutput()) {
			vector.setLabel(label);
		}
		return new GeoElement[]{vector};
	}

	/**
	 * Determines whether the element should be a vector or not based on its label.
	 * @param element element
	 * @return true if the element's label starts with a lowercase character, otherwise false.
	 */
	public boolean hasVectorLabel(GeoElement element) {
		String alreadySetLabel = element.getLabelSimple();
		String label =
				alreadySetLabel != null ? alreadySetLabel : element.getDefinition().getLabel();
		return isVectorLabel(label);
	}

	private boolean isVectorLabel(String label) {
		return label != null && StringUtil.isLowerCase(label.charAt(0));
	}

	/**
	 *
	 * @param x {@link ExpressionNode}
	 * @param y {@link ExpressionNode}
	 * @return the intersection of x and y, if they have any.
	 */
	public GeoElement[] processEquationIntersect(ExpressionValue x,
			ExpressionValue y) {

		GeoElement[] ret = processCommand(intersectCommand(x, y),
				new EvalInfo(true));
		if (ret[0].getParentAlgorithm() instanceof HasShortSyntax) {
			((HasShortSyntax) ret[0].getParentAlgorithm()).setShortSyntax(true);
			ret[0].updateRepaint();
		}
		return ret;
	}

	/**
	 * @param x
	 *            first equation
	 * @param y
	 *            second equation
	 * @return intersection line as command
	 */
	private Command intersectCommand(ExpressionValue x, ExpressionValue y) {
		if (y.unwrap() instanceof Equation && x.unwrap() instanceof Equation) {
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
		return inter;
	}

	/**
	 * Point dependent on arithmetic expression with variables, represented by a
	 * tree. e.g. P = (4t, 2s)
	 */
	private GeoPoint dependentPoint(ExpressionNode root,
			boolean complex) {
		return new AlgoDependentPoint(cons, root, complex).getPoint();
	}

	/**
	 * Vector dependent on arithmetic expression with variables, represented by
	 * a tree. e.g. v = u + 3 w
	 */
	private GeoVector dependentVector(ExpressionNode root) {
		return new AlgoDependentVector(cons, root).getVector();
	}

	/**
	 * empty method in 2D : see AlgebraProcessor3D to see implementation in 3D
	 *
	 * @param n
	 *            3D point expression
	 * @param evaluate
	 *            evaluated node n
	 * @param evalInfo flags for evaluation
	 * @return null
	 */
	protected GeoElement[] processPointVector3D(ExpressionNode n,
			ExpressionValue evaluate, EvalInfo evalInfo) {

		return null;
	}

	/**
	 * Creates a dependent copy of origGeo with label
	 */
	private GeoElement dependentGeoCopy(String label,
			ExpressionNode origGeoNode) {
		AlgoDependentGeoCopy algo = new AlgoDependentGeoCopy(cons, origGeoNode);
		algo.getGeo().setLabel(label);
		return algo.getGeo();
	}

	/** @return "x(" */
	public MyStringBuffer getXBracket() {
		if (xBracket == null) {
			xBracket = new MyStringBuffer(kernel, "x(");
		}
		return xBracket;
	}

	/** @return "y(" */
	public MyStringBuffer getYBracket() {
		if (yBracket == null) {
			yBracket = new MyStringBuffer(kernel, "y(");
		}
		return yBracket;
	}

	/** @return "z(" */
	public MyStringBuffer getZBracket() {
		if (zBracket == null) {
			zBracket = new MyStringBuffer(kernel, "z(");
		}
		return zBracket;
	}

	/** @return ")" */
	public MyStringBuffer getCloseBracket() {
		if (closeBracket == null) {
			closeBracket = new MyStringBuffer(kernel, ")");
		}
		return closeBracket;
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

	/**
	 * @param geo
	 *            element
	 * @return geo wrapped in array
	 */
	protected static GeoElement[] array(GeoElement geo) {
		return new GeoElement[] { geo };
	}

	/**
	 * @param geoConic
	 *            element represented by equation
	 * @return equation of the element
	 */
	public Equation parseEquation(GeoElementND geoConic) {
		ValidExpression ret = null;
		try {
			ret = kernel.getParser().parseGeoGebraExpression(
					geoConic.toValueString(StringTemplate.maxPrecision));
		} catch (ParseException e) {
			// could be ParseException or Classcast Exception
			// https://play.google.com/apps/publish/?dev_acc=05873811091523087820#ErrorClusterDetailsPlace:p=org.geogebra.android&et=CRASH&lr=LAST_7_DAYS&ecn=java.lang.StringIndexOutOfBoundsException&tf=String.java&tc=java.lang.String&tm=startEndAndLength&nid&an&c&s=new_status_desc&ed=0
			Log.debug(e);
		}
		if (ret instanceof Equation) {
			return (Equation) ret;
		}

		return new Equation(kernel, new ExpressionNode(kernel, Double.NaN),
				new ExpressionNode(kernel, Double.NaN));

	}

	/**
	 * @return whether structure parsing is enabled
	 */
	public boolean enableStructures() {
		return structuresEnabled;
	}

	/**
	 * @param enableStructures
	 *            whether structure parsing is enabled
	 */
	public void setEnableStructures(boolean enableStructures) {
		this.structuresEnabled = enableStructures;
	}

	/**
	 * Sets the CommandFilter to the CommandDispatcher
	 * 
	 * @param commandFilter
	 *            only the commands that are allowed by the CommandFilter
	 *            will be added to the command table
	 */
	public void addCommandFilter(@Nonnull CommandFilter commandFilter) {
		cmdDispatcher.addCommandFilter(commandFilter);
	}

	/**
	 * Returns the syntax for the given command, if the command is allowed.
	 * @param syntax an abstraction for loading command syntax definitions
	 * @param internalCommandName the internal command name (see {@link Commands}).
	 * @param settings the current (application) settings.
	 * @return the syntax for the command if the command is allowed, null otherwise.
	 */
	public String getSyntax(CommandSyntax syntax, String internalCommandName, Settings settings) {
		int dim = settings.getEuclidian(-1).isEnabled() ? 3 : 2;
		if (cmdDispatcher.isCASAllowed()) {
			return syntax.getCommandSyntax(internalCommandName, dim);
		}
		Commands command = null;
		try {
			command = Commands.valueOf(internalCommandName);
		} catch (Exception ignore) {
			// macro or error
		}
		if (command == null) {
			return syntax.getCommandSyntax(internalCommandName, dim);
		}
		if (!cmdDispatcher.isAllowedByCommandFilters(command)) {
			return null;
		}
		if (noCASfilter == null) {
			noCASfilter = CommandFilterFactory.createNoCasCommandFilter();
		}
		if (!noCASfilter.isCommandAllowed(command)) {
			return null;
		}

		return syntax.getCommandSyntax(internalCommandName, dim);
	}

	/**
	 * @return command dispatcher
	 */
	public CommandDispatcher getCommandDispatcher() {
		return cmdDispatcher;
	}

	private void maybeLogCommandValidatedEvent(Command command, EvalInfo info, boolean validated) {
		if (kernel.isSilentMode() || info == null || !info.useAnalytics()) {
			return;
		}
		String commandName = command.getName();
		String validatedParam = validated ? Analytics.Param.OK : Analytics.Param.ERROR;
		boolean isRedefinition = info.isRedefinition();
		String objectCreation = isRedefinition ? Analytics.Param.REDEFINED : Analytics.Param.NEW;

		Map<String, Object> params = new HashMap<>();
		params.put(Analytics.Param.COMMAND, commandName);
		params.put(Analytics.Param.STATUS, validatedParam);
		params.put(Analytics.Param.OBJECT_CREATION, objectCreation);
		Analytics.logEvent(Analytics.Event.COMMAND_VALIDATED, params);
	}

	/**
	 * Adds a {@link GeoElementSetup} which can modify the
	 * initial setup of elements when they are created or after they have been modified.
	 * @param geoElementSetup The {@link GeoElementSetup} to be added
	 */
	public void addGeoElementSetup(GeoElementSetup geoElementSetup) {
		geoElementSetups.add(geoElementSetup);
	}

	/**
	 * Removes the previously added {@link GeoElementSetup} from the {@code AlgebraProcessor}.
	 * Once removed, it will no longer affect the initial setup of elements.
	 *
	 * @param geoElementSetup The {@link GeoElementSetup} to be removed
	 */
	public void removeGeoElementSetup(GeoElementSetup geoElementSetup) {
		geoElementSetups.remove(geoElementSetup);
	}
}
