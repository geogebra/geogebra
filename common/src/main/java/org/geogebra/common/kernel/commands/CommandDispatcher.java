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
import java.util.HashMap;
import java.util.List;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.cas.UsesCAS;
import org.geogebra.common.kernel.commands.filter.CommandArgumentFilter;
import org.geogebra.common.kernel.commands.selector.CommandNameFilter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.util.debug.Log;

/**
 * Runs commands and handles string to command processor conversion.
 *
 */
public abstract class CommandDispatcher {

	/** kernel **/
	protected Kernel kernel;
	private Construction cons;
	/**
	 * Application
	 */
	protected App app;

	/**
	 * stores public (String name, CommandProcessor cmdProc) pairs
	 *
	 * NB: Do not put CAS-specific commands in this table! If you ever want to,
	 * call Markus, so he can give you one million reasons why this is a
	 * terribly bad idea!
	 **/
	protected HashMap<String, CommandProcessor> cmdTable;
	/** Similar to cmdTable, but for CAS */

	/** dispatcher for discrete math */
	protected static CommandDispatcherInterface discreteDispatcher = null;
	/** dispatcher for scripting commands */
	protected static CommandDispatcherInterface scriptingDispatcher = null;
	/** dispatcher for CAS commands */
	protected static CommandDispatcherInterface casDispatcher = null;
	/** dispatcher for advanced commands */
	protected static CommandDispatcherInterface advancedDispatcher = null;
	/** dispatcher for stats commands */
	protected static CommandDispatcherInterface statsDispatcher = null;
	/** dispatcher for steps commands */
	protected static CommandDispatcherInterface stepsDispatcher = null;
	/** disptcher for prover commands */
	protected static CommandDispatcherInterface proverDispatcher = null;

	private CommandDispatcherBasic basicDispatcher = null;

	private CommandArgumentFilter commandArgumentFilter;

	/** stores internal (String name, CommandProcessor cmdProc) pairs */
	private MacroProcessor macroProc;
	private boolean enabled = true;
	private List<CommandNameFilter> commandNameFilters;
	/** number of visible tables */
	public static final int tableCount = 20;

	/**
	 * Returns localized name of given command set
	 *
	 * @param index
	 *            number of set (see Commands.TABLE_*)
	 * @return localized name
	 */
	public String getSubCommandSetName(int index) {
		Localization loc = app.getLocalization();
		switch (index) {
		case CommandsConstants.TABLE_GEOMETRY:
			return loc.getMenu("Type.Geometry");
		case CommandsConstants.TABLE_ALGEBRA:
			return loc.getMenu("Type.Algebra");
		case CommandsConstants.TABLE_TEXT:
			return loc.getMenu("Type.Text");
		case CommandsConstants.TABLE_LOGICAL:
			return loc.getMenu("Type.Logic");
		case CommandsConstants.TABLE_FUNCTION:
			return loc.getMenu("Type.FunctionsAndCalculus");
		case CommandsConstants.TABLE_CONIC:
			return loc.getMenu("Type.Conic");
		case CommandsConstants.TABLE_LIST:
			return loc.getMenu("Type.List");
		case CommandsConstants.TABLE_VECTOR:
			return loc.getMenu("Type.VectorAndMatrix");
		case CommandsConstants.TABLE_TRANSFORMATION:
			return loc.getMenu("Type.Transformation");
		case CommandsConstants.TABLE_CHARTS:
			return loc.getMenu("Type.Chart");
		case CommandsConstants.TABLE_STATISTICS:
			return loc.getMenu("Type.Statistics");
		case CommandsConstants.TABLE_PROBABILITY:
			return loc.getMenu("Type.Probability");
		case CommandsConstants.TABLE_SPREADSHEET:
			return loc.getMenu("Type.Spreadsheet");
		case CommandsConstants.TABLE_SCRIPTING:
			return loc.getMenu("Type.Scripting");
		case CommandsConstants.TABLE_DISCRETE:
			return loc.getMenu("Type.DiscreteMath");
		case CommandsConstants.TABLE_GEOGEBRA:
			return loc.getMenu("Type.GeoGebra");
		case CommandsConstants.TABLE_OPTIMIZATION:
			return loc.getMenu("Type.OptimizationCommands");
		case CommandsConstants.TABLE_CAS:
			return loc.getMenu("Type.CAS");
		case CommandsConstants.TABLE_3D:
			return loc.getMenu("Type.3D");
		case CommandsConstants.TABLE_FINANCIAL:
			return loc.getMenu("Type.Financial");
		// Commands.TABLE_ENGLISH:
		default:
			return null;
		}
	}

	/**
	 * Creates new command dispatcher
	 *
	 * @param kernel
	 *            Kernel of current application
	 */
	public CommandDispatcher(Kernel kernel) {
		cons = kernel.getConstruction();
		this.kernel = kernel;
		app = kernel.getApplication();
		commandNameFilters = new ArrayList<>();
		addCommandNameFilter(app.getConfig().getCommandNameFilter());
	}

	/**
	 * Returns whether the given command name is supported in GeoGebra.
	 *
	 * @param cmd
	 *            command name
	 * @return whether the given command name is supported in GeoGebra.
	 */
	public boolean isCommandAvailable(String cmd) {
		if (cmdTable == null) {
			initCmdTable();
		}
		return cmdTable.containsKey(cmd);
	}

	/**
	 * @param c
	 *            Command to be executed
	 * @param info
	 *            specifies if output GeoElements of this command should get
	 *            labels
	 * @throws MyError
	 *             in case command execution fails
	 * @return Geos created by the command
	 */
	final public GeoElement[] processCommand(Command c, EvalInfo info)
			throws MyError {

		CommandProcessor cmdProc = getProcessor(c);

		if (cmdProc == null) {
			if (c.getName()
					.equals(app.getLocalization().getFunction("freehand"))) {
				return null;
			}
			throw createUnknownCommandError(c);
		}
		return process(cmdProc, c, info);
	}

	/**
	 * @param command
	 *            command
	 * @return whether selector accepts it
	 */
	protected boolean isAllowedByNameFilter(Commands command) {
		boolean allowed = true;
		for (CommandNameFilter filter : commandNameFilters) {
			allowed = allowed && filter.isCommandAllowed(command);
		}
		return allowed;
	}

	private void checkAllowedByArgumentFilter(Command command,
			CommandProcessor commandProcessor) throws MyError {
		if (commandArgumentFilter != null) {
			commandArgumentFilter.checkAllowed(command, commandProcessor);
		}
	}

	private MyError createUnknownCommandError(Command command) {
		throw new MyError(app.getLocalization(),
				app.getLocalization().getError("UnknownCommand") + " : "
						+ app.getLocalization().getCommand(command.getName()));
	}

	private GeoElement[] process(CommandProcessor cmdProc, Command c,
			EvalInfo info) {
		checkAllowedByArgumentFilter(c, cmdProc);
		// switch on macro mode to avoid labeling of output if desired
		// Solve[{e^-(x*x/2)=1,x>0},x]
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		if (info != null && !info.isLabelOutput()) {
			cons.setSuppressLabelCreation(true);
		}

		try {

			// disable preview for commands using CAS
			// if CAS not loaded
			if (info != null && !info.isUsingCAS() && !kernel.isGeoGebraCASready()
					&& cmdProc instanceof UsesCAS) {
				return null;
			}
			if (cmdProc == null) {
				throw createUnknownCommandError(c);
			}
			return cmdProc.process(c, info);
		} catch (Exception e) {
			cons.setSuppressLabelCreation(oldMacroMode);
			Log.debug(e);
			throw MyError.forCommand(app.getLocalization(),
					Errors.CASGeneralErrorMessage.getKey(),
					c.getName(), e);
		} finally {
			cons.setSuppressLabelCreation(oldMacroMode);
		}
	}

	private CommandProcessor getProcessor(Command c) throws MyError {
		if (!enabled) {
			throw new MyError(kernel.getLocalization(), Errors.InvalidInput);
		}
		if (cmdTable == null) {
			initCmdTable();
		}

		// cmdName
		String cmdName = c.getName();
		CommandProcessor cmdProc;
		//
		// // remove CAS variable prefix from command name if present
		// cmdName = cmdName.replace(ExpressionNode.GGBCAS_VARIABLE_PREFIX, "");

		// MACRO: is there a macro with this command name?
		Macro macro = kernel.getMacro(cmdName);
		if (macro != null) {
			c.setMacro(macro);
			cmdProc = macroProc;
			// remember macro command used:
			// this is needed when a single tool A[] is exported to find
			// all other tools that are needed for A[]
			cons.addUsedMacro(macro);
		}
		// STANDARD CASE
		else {
			// get CommandProcessor object for command name from command table
			cmdProc = cmdTable.get(cmdName);

			if (cmdProc == null) {
				cmdProc = commandTableSwitch(c);
				if (cmdProc != null) {
					cmdTable.put(cmdName, cmdProc);
				}
			}

		}
		return cmdProc;
	}

	/**
	 * Fills the string-command map
	 */
	protected void initCmdTable() {
		macroProc = new MacroProcessor(kernel);

		// external commands: visible to users
		cmdTable = new HashMap<>(500);

		for (Commands comm : Commands.values()) {
			cmdTable.put(comm.name(), null);
		}

		// =============================================================
		// CAS
		// do *after* above loop as we must add only those CAS commands without
		// a ggb equivalent
		// =============================================================

	}

	/**
	 * This method returns a CommandProcessor Object for a corresponding command
	 * name. This should be called only if that CommandProcessor object is not
	 * there already in the command table.
	 *
	 * @param c
	 *            Command to be processed
	 * @return Processor for given command
	 */
	public CommandProcessor commandTableSwitch(Command c) {
		String cmdName = c.getName();
		try {

			Commands command = Commands.valueOf(cmdName);

			if (!isAllowedByNameFilter(command)) {
				Log.info("The command is not allowed by the command filter");
				return null;
			}

			switch (command) {

			// scripting
			case RigidPolygon:
			case Relation:
			case CopyFreeObject:
			case DataFunction:
			case SetColor:
			case SetBackgroundColor:
			case SetDecoration:
			case SetDynamicColor:
			case SetConditionToShowObject:
			case SetFilling:
			case SetLevelOfDetail:
			case SetLineThickness:
			case SetLineStyle:
			case SetPointStyle:
			case SetPointSize:
			case SetFixed:
			case SetTrace:
			case Rename:
			case HideLayer:
			case ShowLayer:
			case SetCoords:
			case Pan:
			case CenterView:
			case ZoomIn:
			case SetSeed:
			case ZoomOut:
			case SetActiveView:
			case SelectObjects:
			case SetLayer:
			case SetCaption:
			case SetLabelMode:
			case SetTooltipMode:
			case UpdateConstruction:
			case SetValue:
			case PlaySound:
			case ReadText:
			case ParseToNumber:
			case ParseToFunction:
			case StartAnimation:
			case StartLogging:
			case StopLogging:
			case StartRecord:
			case SetPerspective:
			case Delete:
			case Repeat:
			case Slider:
			case Checkbox:
			case InputBox:
			case Textfield:
			case Button:
			case Execute:
			case GetTime:
			case ShowLabel:
			case SetAxesRatio:
			case SetVisibleInView:
			case ShowAxes:
			case ShowGrid:
			case SlowPlot:
			case ToolImage:
			case Turtle:
			case TurtleForward:
			case TurtleBack:
			case TurtleLeft:
			case TurtleRight:
			case TurtleUp:
			case TurtleDown:
			case RunClickScript:
			case RunUpdateScript:
				// case DensityPlot:
				return getScriptingDispatcher().dispatch(command, kernel);

			// advanced
			case IntersectPath:
			case IntersectRegion:

			case Difference:

			case TaylorPolynomial:
			case TaylorSeries:

			case SecondAxis:
			case MinorAxis:

			case SemiMinorAxisLength:
			case SecondAxisLength:

			case Directrix:
			case Numerator:
			case Denominator:
			case ComplexRoot:
			case SlopeField:
			case Iteration:
			case PathParameter:
			case Asymptote:
			case CurvatureVector:
			case Curvature:
			case OsculatingCircle:
			case IterationList:
			case RootList:
			case ImplicitCurve:
			case ImplicitSurface:
			case Roots:
			case AffineRatio:
			case CrossRatio:
			case ClosestPoint:
			case IsInRegion:
			case PrimeFactors:
			case Union:
			case ScientificText:
			case VerticalText:
			case RotateText:
			case Ordinal:
			case Parameter:
			case Incircle:
			case SelectedElement:
			case SelectedIndex:
			case Unique:
			case Zip:
			case Intersection:
			case PointList:
			case ApplyMatrix:
			case Invert:
			case NInvert:
			case Transpose:
			case ReducedRowEchelonForm:
			case Determinant:
				// case MatrixPlot:
			case Identity:
			case Centroid:
			case MajorAxis:
			case FirstAxis:

			case SemiMajorAxisLength:
			case FirstAxisLength:

			case AxisStepX:
			case AxisStepY:
			case ConstructionStep:
			case SetConstructionStep:
			case Polar:

			case LinearEccentricity:
			case Excentricity:

			case Eccentricity:
			case Axes:
			case IndexOf:
			case Flatten:
			case Insert:
			case DynamicCoordinates:
			case Maximize:
			case Minimize:
			case ToBase:
			case FromBase:
			case ContinuedFraction:
			case AttachCopyToView:
			case Divisors:
			case DivisorsSum:
			case Dimension:
			case DivisorsList:
			case IsPrime:
			case LeftSide:
			case RightSide:
			case Division:
			case MatrixRank:
			case CommonDenominator:
			case ToPoint:
			case ToComplex:
			case ToPolar:
			case Factors:
			case NSolveODE:
			case Rate:
			case Periods:
			case Payment:
			case FutureValue:
			case PresentValue:
			case SVD:
				return getAdvancedDispatcher().dispatch(command, kernel);

			// prover
			case Prove:
			case ProveDetails:
			case AreCollinear:
			case AreParallel:
			case AreConcyclic:
			case ArePerpendicular:
			case AreEqual:
			case AreCongruent:
			case AreConcurrent:
			case IsTangent:
			case LocusEquation:
			case Envelope:
				return getProverDispatcher().dispatch(command, kernel);

			// basic

			case Tangent:
			case Length:
			case UnitPerpendicularVector:
			case UnitOrthogonalVector:

			case Sort:
			case BarChart:
			case Product:
			case Join:
			case LCM:
			case GCD:
			case LetterToUnicode:
			case UnicodeToLetter:
			case Object:
			case CountIf:
			case Extremum:
			case Holes:
			case UnitVector:
			case Direction:
			case Text:
			case Vector:
			case Dot:
			case Cross:
			case nPr:
			case PolyLine:
			case Polyline:
			case PointIn:
			case Line:
			case Ray:

			case AngleBisector:
			case AngularBisector:

			case Segment:
			case Slope:
			case Angle:
			case InteriorAngles:
			case Point:
			case Midpoint:
			case Intersect:
			case Distance:
			case Radius:
			case Arc:
			case Sector:

			case CircleArc:
			case CircularArc:
			case CircleSector:
			case CircularSector:
			case CircumcircleSector:
			case CircumcircularSector:
			case CircumcircleArc:
			case CircumcircularArc:

			case Polygon:
			case Area:
			case Circumference:
			case Perimeter:
			case Locus:
			case Vertex:
			case If:
			case Root:
			case TurningPoint:
			case Polynomial:
			case Spline:
				// case Nyquist:
			case Function:
			case Curve:
			case CurveCartesian:
			case LowerSum:
			case LeftSum:
			case RectangleSum:
			case UpperSum:
			case TrapezoidalSum:
			case Ellipse:
			case Hyperbola:
			case Conic:
			case Circle:
			case Semicircle:
			case Parabola:
			case Focus:
			case Center:
			case Element:
			case Sequence:

			case Reflect:
			case Mirror:

			case Dilate:
			case Rotate:
			case Translate:
			case Shear:
			case Stretch:

			case Corner:
			case Name:

			case Diameter:
			case ConjugateDiameter:

			case LineBisector:
			case PerpendicularBisector:

			case OrthogonalLine:
			case PerpendicularLine:

			case OrthogonalVector:
			case PerpendicularVector:

			case Random:
			case RandomBetween:
			case RandomPointIn:

			case Sum:

			case Binomial:
			case BinomialCoefficient:
			case nCr:

			case Mod:
			case Div:
			case Min:
			case Max:
			case Append:
			case First:
			case Last:
			case Remove:
			case RemoveUndefined:
			case Reverse:
			case TableText:
			case Take:
			case TextToUnicode:
			case UnicodeToText:
			case FractionText:
			case KeepIf:
			case IsInteger:

			case Defined:
			case IsDefined:

			case FormulaText:
			case LaTeX:
			case Normalize:
			case ExportImage:

				return getBasicDispatcher().dispatch(command, kernel);

			case CFactor:
			case CIFactor:
			case CSolutions:
			case CSolve:
			case Eliminate:
			case GroebnerLex:
			case GroebnerDegRevLex:
			case GroebnerLexDeg:
			case Numeric:
			case MixedNumber:
			case Rationalize:
			case Substitute:
			case ToExponential:
			case Laplace:
			case InverseLaplace:
			case Assume:
			case SolveCubic:
			case JordanDiagonalization:
			case Eigenvalues:
			case Eigenvectors:
				return new CAScmdProcessor(kernel);

			// ************** STATS ***************

			case ANOVA:
			case Bernoulli:
			case BinomialDist:
			case BoxPlot:
			case Cauchy:
			case Cell:
			case CellRange:
			case ChiSquaredTest:
			case ChiSquared:
			case Classes:
			case Column:
			case ColumnName:
			case CorrelationCoefficient:
			case cov:
			case Covariance:
			case ContingencyTable:
			case DotPlot:
			case Erlang:
			case Exponential:
			case FDistribution:
			case FillCells:
			case FillColumn:
			case FillRow:
			case Fit:
			case FitImplicit:
			case FitExp:
			case FitGrowth:
			case FitLine:
			case FitLineX:
			case FitLineY:
			case FitLog:
			case FitLogistic:
			case FitPoly:
			case FitPow:
			case FitSin:
			case Frequency:
			case FrequencyPolygon:
			case FrequencyTable:
			case Gamma:
			case GeometricMean:
			case HarmonicMean:
			case Histogram:
			case HistogramRight:
			case HyperGeometric:
			case InverseBinomial:
			case InverseCauchy:
			case InverseChiSquared:
			case InverseExponential:
			case InverseFDistribution:
			case InverseGamma:
			case InverseHyperGeometric:
			case InverseLogNormal:
			case InverseLogistic:
			case InverseNormal:
			case InversePascal:
			case InversePoisson:
			case InverseTDistribution:
			case InverseWeibull:
			case InverseZipf:
			case LogNormal:
			case Logistic:
			case Mean:
			case mean:
			case MeanX:
			case MeanY:
			case Median:
			case Mode:
			case Normal:
			case NormalQuantilePlot:
			case OrdinalRank:
			case PMCC:
			case Pascal:
			case Percentile:
			case Poisson:
			case Q1:
			case Q3:
			case Quartile1:
			case Quartile3:
			case RSquare:
			case RandomDiscrete:
			case RandomElement:
			case RandomPolynomial:
			case RandomBinomial:
			case RandomNormal:
			case RandomPoisson:
			case RandomUniform:
			case ResidualPlot:
			case RootMeanSquare:
			case Row:
			case SD:
			case MAD:
			case mad:
			case SDX:
			case SDY:
			case Sxx:
			case Sxy:
			case Syy:
			case SXX:
			case SXY:
			case SYY:
			case Sample:
			case stdevp:
			case stdev:
			case SampleSD:
			case SampleSDX:
			case SampleSDY:
			case SampleVariance:
			case Shuffle:
			case SigmaXX:
			case SigmaXY:
			case SigmaYY:
			case Spearman:
			case StemPlot:
			case StepGraph:
			case StickGraph:
			case SumSquaredErrors:
			case TDistribution:
			case TMean2Estimate:
			case TMeanEstimate:
			case TTest2:
			case TTest:
			case TTestPaired:
			case TiedRank:
			case Triangular:
			case Uniform:
			case var:
			case Variance:
			case Weibull:
			case ZMean2Estimate:
			case ZMean2Test:
			case ZMeanEstimate:
			case ZMeanTest:
			case ZProportion2Estimate:
			case ZProportion2Test:
			case ZProportionEstimate:
			case ZProportionTest:
			case Zipf:
				return getStatsDispatcher().dispatch(command, kernel);

			case TriangleCenter:
			case Barycenter:
			case Trilinear:
			case Cubic:
			case TriangleCurve:

			case Voronoi:
			case Hull:
			case ConvexHull:
			case MinimumSpanningTree:
			case DelauneyTriangulation:
			case TravelingSalesman:
			case ShortestDistance:
				return getDiscreteDispatcher().dispatch(command, kernel);
			case NSolve:
			case Solve:
			case Solutions:
			case NSolutions:
				return getCASDispatcher().dispatch(command, kernel);
			case Expand:
			case Factor:
			case IFactor:
			case Simplify:
			case SurdText:
			case ParametricDerivative:
			case Derivative:
			case NDerivative:
			case Integral:
			case IntegralBetween:
			case NIntegral:
			case TrigExpand:
			case TrigSimplify:
			case TrigCombine:
			case Limit:
			case LimitBelow:
			case LimitAbove:
			case Degree:
			case Coefficients:
			case PartialFractions:
			case SolveODE:
			case ImplicitDerivative:
			case NextPrime:
			case PreviousPrime:
			case CompleteSquare:
				return getCASDispatcher().dispatch(command, kernel);
			case ShowSteps:
				return getStepsDispatcher().dispatch(command, kernel);
			default:
				Log.error("missing case in CommandDispatcher " + cmdName);
				return null;
			}
		} catch (RuntimeException e) {
			Log.warn("command not found / CAS command called:" + cmdName);
		}
		return null;
	}

	/** @return dispatcher for stats commands */
	public abstract CommandDispatcherInterface getStatsDispatcher();

	/** @return dispatcher for discrete math */
	public abstract CommandDispatcherInterface getDiscreteDispatcher();

	/** @return dispatcher for CAS commands */
	public abstract CommandDispatcherInterface getCASDispatcher();

	/** @return dispatcher for scripting commands */
	public abstract CommandDispatcherInterface getScriptingDispatcher();

	/** @return dispatcher for advanced commands */
	public abstract CommandDispatcherInterface getAdvancedDispatcher();

	/** @return dispatcher for steps commands */
	public abstract CommandDispatcherInterface getStepsDispatcher();

	/** @return dispatcher for prover commands */
	public abstract CommandDispatcherInterface getProverDispatcher();

	/** @return dispatcher for 3D commands */
	public CommandDispatcherInterface get3DDispatcher() {
		return null;
	}

	/**
	 * @return dispatcher for basic commands
	 */
	protected CommandDispatcherBasic getBasicDispatcher() {
		if (basicDispatcher == null) {
			basicDispatcher = new CommandDispatcherBasic();
		}
		return basicDispatcher;
	}

	/**
	 * A way to process a command to an expression value rather than GeoELement
	 *
	 * @param c
	 *            command
	 * @param info
	 *            flags eg whether output needs label
	 * @return command result
	 */
	public ExpressionValue simplifyCommand(Command c, EvalInfo info) {
		CommandProcessor cmdProc = getProcessor(c);
		if (cmdProc != null) {
			ExpressionValue simple = cmdProc.simplify(c);
			if (simple != null) {
				return simple;
			}
		}
		GeoElement[] processed = process(cmdProc, c, info);
		if (processed.length > 0) {
			return processed[0];
		}
		return null;
	}

	/**
	 * @param enable
	 *            true to enable commands
	 */
	public void setEnabled(boolean enable) {
		this.enabled = enable;
	}

	/**
	 * @return whether commands are supported
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * add a new CommandNameFilter
	 * 
	 * @param filter
	 *            to add. only the commands that are allowed by all
	 *            commandNameFilters will be added to the command table
	 */
	public void addCommandNameFilter(CommandNameFilter filter) {
		if (filter != null) {
			commandNameFilters.add(filter);
		}
	}

	/**
	 * remove commandNameFilter
	 * 
	 * @param filter
	 *            to remove.
	 */
	public void removeCommandNameFilter(CommandNameFilter filter) {
		commandNameFilters.remove(filter);
	}

	/**
	 * Sets the CommandArgumentFilter
	 * 
	 * @param filter
	 *            only the commands that are allowed by the
	 *            commandArgumentFilter will be allowed
	 */
	public void setCommandArgumentFilter(CommandArgumentFilter filter) {
		this.commandArgumentFilter = filter;
	}

	/**
	 * @return command filter
	 */
	public CommandArgumentFilter getCommandArgumentFilter() {
		return commandArgumentFilter;
	}

	/**
	 * @return whether CAS commands are allowed
	 */
	public boolean isCASAllowed() {
		return isAllowedByNameFilter(Commands.Solve);
	}

}
