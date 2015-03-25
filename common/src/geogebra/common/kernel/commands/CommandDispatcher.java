/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.commands;

import geogebra.common.GeoGebraConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Macro;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.main.MyError;
import geogebra.common.util.debug.Log;

import java.util.HashMap;

/**
 * Runs commands and handles string to command processor conversion.
 * 
 */
public abstract class CommandDispatcher {

	/** kernel **/
	protected Kernel kernel;
	private Construction cons;
	private App app;

	private boolean isCasActive = false;

	/**
	 * stores public (String name, CommandProcessor cmdProc) pairs
	 * 
	 * NB: Do not put CAS-specific commands in this table! If you ever want to,
	 * call Markus, so he can give you one million reasons why this is a
	 * terribly bad idea!
	 **/
	protected HashMap<String, CommandProcessor> cmdTable;
	/** Similar to cmdTable, but for CAS */
	protected HashMap<String, CommandProcessor> casTable;

	/** number of visible tables */
	public static final int tableCount = GeoGebraConstants.CAS_VIEW_ENABLED ? 20
			: 19;

	/**
	 * Returns localized name of given command set
	 * 
	 * @param index
	 *            number of set (see Commands.TABLE_*)
	 * @return localized name
	 */
	public String getSubCommandSetName(int index) {
		switch (index) {
		case CommandsConstants.TABLE_GEOMETRY:
			return app.getMenu("Type.Geometry");
		case CommandsConstants.TABLE_ALGEBRA:
			return app.getMenu("Type.Algebra");
		case CommandsConstants.TABLE_TEXT:
			return app.getMenu("Type.Text");
		case CommandsConstants.TABLE_LOGICAL:
			return app.getMenu("Type.Logic");
		case CommandsConstants.TABLE_FUNCTION:
			return app.getMenu("Type.FunctionsAndCalculus");
		case CommandsConstants.TABLE_CONIC:
			return app.getMenu("Type.Conic");
		case CommandsConstants.TABLE_LIST:
			return app.getMenu("Type.List");
		case CommandsConstants.TABLE_VECTOR:
			return app.getMenu("Type.VectorAndMatrix");
		case CommandsConstants.TABLE_TRANSFORMATION:
			return app.getMenu("Type.Transformation");
		case CommandsConstants.TABLE_CHARTS:
			return app.getMenu("Type.Chart");
		case CommandsConstants.TABLE_STATISTICS:
			return app.getMenu("Type.Statistics");
		case CommandsConstants.TABLE_PROBABILITY:
			return app.getMenu("Type.Probability");
		case CommandsConstants.TABLE_SPREADSHEET:
			return app.getMenu("Type.Spreadsheet");
		case CommandsConstants.TABLE_SCRIPTING:
			return app.getMenu("Type.Scripting");
		case CommandsConstants.TABLE_DISCRETE:
			return app.getMenu("Type.DiscreteMath");
		case CommandsConstants.TABLE_GEOGEBRA:
			return app.getMenu("Type.GeoGebra");
		case CommandsConstants.TABLE_OPTIMIZATION:
			return app.getMenu("Type.OptimizationCommands");
		case CommandsConstants.TABLE_CAS:
			return app.getMenu("Type.CAS");
		case CommandsConstants.TABLE_3D:
			return app.getMenu("Type.3D");
		case CommandsConstants.TABLE_FINANCIAL:
			return app.getMenu("Type.Financial");
			// Commands.TABLE_ENGLISH:
		default:
			return null;
		}
	}

	/** stores internal (String name, CommandProcessor cmdProc) pairs */
	protected HashMap<String, CommandProcessor> internalCmdTable;
	private MacroProcessor macroProc;

	/**
	 * Creates new command dispatcher
	 * 
	 * @param kernel2
	 *            Kernel of current application
	 */
	public CommandDispatcher(Kernel kernel2) {
		this.kernel = kernel2;
		cons = kernel2.getConstruction();
		app = kernel2.getApplication();
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
	 * @param labelOutput
	 *            specifies if output GeoElements of this command should get
	 *            labels
	 * @throws MyError
	 *             in case command execution fails
	 * @return Geos created by the command
	 */
	final public GeoElement[] processCommand(Command c, boolean labelOutput)
			throws MyError {

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
		}
		// STANDARD CASE
		else {
			// get CommandProcessor object for command name from command table
			cmdProc = cmdTable.get(cmdName);

			if (cmdProc == null) {
				cmdProc = commandTableSwitch(cmdName);
				if (cmdProc != null) {
					cmdTable.put(cmdName, cmdProc);
				}
			}

			if (cmdProc == null && internalCmdTable != null) {
				// try internal command
				cmdProc = internalCmdTable.get(cmdName);
			}
		}

		if (cmdProc == null)
			throw new MyError(app.getLocalization(), app.getLocalization()
					.getError("UnknownCommand")
					+ " : "
					+ app.getLocalization().getCommand(c.getName()));

		// switch on macro mode to avoid labeling of output if desired
		// Solve[{e^-(x*x/2)=1,x>0},x]
		boolean oldMacroMode = cons.isSuppressLabelsActive();
		if (!labelOutput)
			cons.setSuppressLabelCreation(true);

		GeoElement[] ret = null;
		try {
			ret = cmdProc.process(c);
		} catch (MyError e) {
			throw e;
		} catch (Exception e) {
			cons.setSuppressLabelCreation(oldMacroMode);
			e.printStackTrace();
			throw new MyError(app.getLocalization(), "CAS.GeneralErrorMessage");
		} finally {
			cons.setSuppressLabelCreation(oldMacroMode);
		}

		// remember macro command used:
		// this is needed when a single tool A[] is exported to find
		// all other tools that are needed for A[]
		if (macro != null)
			cons.addUsedMacro(macro);

		return ret;
	}

	/**
	 * Fills the string-command map
	 */
	protected void initCmdTable() {
		macroProc = new MacroProcessor(kernel);

		// external commands: visible to users
		cmdTable = new HashMap<String, CommandProcessor>(500);
		casTable = new HashMap<String, CommandProcessor>(500);

		for (Commands comm : Commands.values()) {
			cmdTable.put(comm.name(), null);
		}

		// =============================================================
		// CAS
		// do *after* above loop as we must add only those CAS commands without
		// a ggb equivalent
		// =============================================================

		if (GeoGebraConstants.CAS_VIEW_ENABLED && app.isUsingFullGui()
				&& isCasActive)
			initCASCommands();

	}

	/**
	 * Loads CAS commands into the cmdSubTable.
	 */
	public void initCASCommands() {

		if (!GeoGebraConstants.CAS_VIEW_ENABLED)
			return;

		isCasActive = true;

		// this method might get called during initialization. In that case
		// this method will be called again during the normal initCmdTable
		// since isCasActive is now true.
		if (cmdTable == null)
			return;

		fillInternalCmdTable();
	}

	/**
	 * Fills internal command table (table for commands that should not be
	 * visible to the user but are returned by CAS)
	 */
	protected void fillInternalCmdTable() {
		internalCmdTable = new HashMap<String, CommandProcessor>();
		// support parsing diff() results back from Maxima
		internalCmdTable.put("diff", new CmdDerivative(kernel));

		// if there will be more commands here, it could be refactored
		// in the same manner as cmdTable initialization was refactored

	}

	/**
	 * This method returns a CommandProcessor Object for a corresponding command
	 * name. This should be called only if that CommandProcessor object is not
	 * there already in the command table.
	 * 
	 * @param cmdName
	 *            String command name
	 * @return Processor for given command
	 */
	public CommandProcessor commandTableSwitch(String cmdName) {
		try {

			Commands command = Commands.valueOf(cmdName);
			switch (command) {

			// scripting
			case RigidPolygon:
			case Relation:
			case CopyFreeObject:
			case SetColor:
			case SetBackgroundColor:
			case SetDynamicColor:
			case SetConditionToShowObject:
			case SetFilling:
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
			case ParseToNumber:
			case ParseToFunction:
			case StartAnimation:
			case StartLogging:
			case StopLogging:
			case StartRecord:
			case SetPerspective:
			case Delete:
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
			case Direction:

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
			case Roots:
			case AffineRatio:
			case CrossRatio:
			case ClosestPoint:
			case IsInRegion:
			case PrimeFactors:
			case CompleteSquare:
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
			case Polar:

			case LinearEccentricity:
			case Excentricity:

			case Eccentricity:
			case Axes:
			case IndexOf:
			case Flatten:
			case Insert:
			case Prove:
			case ProveDetails:
			case DynamicCoordinates:
			case Maximize:
			case Minimize:
			case AreCollinear:
			case AreParallel:
			case AreConcyclic:
			case ArePerpendicular:
			case AreEqual:
			case AreConcurrent:
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
				return getAdvancedDispatcher().dispatch(command, kernel);

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
			case UnitVector:
			case Text:
			case Vector:
			case Dot:
			case Cross:
			case PolyLine:
			case PointIn:
			case Line:
			case Ray:

			case AngleBisector:
			case AngularBisector:

			case Segment:
			case Slope:
			case Angle:
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
				// case ContourPlot:

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

			case Sum:

			case Binomial:
			case BinomialCoefficient:

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
				return getBasicDispatcher().dispatch(command, kernel);
			case Normalize:
				return new CmdNormalize(kernel);
			case CFactor:
			case CIFactor:
			case CSolutions:
			case CSolve:
			case Eliminate:
			case GroebnerLex:
			case GroebnerDegRevLex:
			case GroebnerLexDeg:
			case NSolve:
			case NSolutions:
			case Numeric:
			case MixedNumber:
			case Rationalize:
			case Solutions:
			case Solve:
			case Substitute:
			case ToExponential:
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
			case MeanX:
			case MeanY:
			case Median:
			case Mode:
			case Normal:
			case NormalQuantilePlot:
			case nPr:
			case OrdinalRank:
			case PMCC:
			case Pascal:
			case Percentile:
			case Poisson:
			case Q1:
			case Q3:
			case RSquare:
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
			case SDX:
			case SDY:
			case SXX:
			case SXY:
			case SYY:
			case Sample:
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

			case LocusEquation:
			case Envelope:
			case Expand:
			case Factor:
			case IFactor:
			case Simplify:
			case SurdText:
			case ParametricDerivative:
			case Derivative:
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
				return getCASDispatcher().dispatch(command, kernel);
			default:
				Log.error("missing case in CommandDispatcher " + cmdName);
				return null;
			}
		} catch (Exception e) {
			Log.warn("command not found / CAS command called:" + cmdName);
		}
		return null;
	}

	private CommandDispatcherStats statsDispatcher = null;

	private CommandDispatcherStats getStatsDispatcher() {
		if (statsDispatcher == null) {
			statsDispatcher = new CommandDispatcherStats();
		}
		return statsDispatcher;
	}

	/** dispatcher for discrete math */
	protected CommandDispatcherInterface discreteDispatcher = null;

	/** @return dispatcher for discrete math */
	protected CommandDispatcherInterface getDiscreteDispatcher() {
		if (discreteDispatcher == null) {
			discreteDispatcher = new CommandDispatcherDiscrete();
		}
		return discreteDispatcher;
	}

	/** dispatcher for CAS commands */
	protected CommandDispatcherInterface casDispatcher = null;

	/** @return dispatcher for CAS commands */
	protected CommandDispatcherInterface getCASDispatcher() {
		if (casDispatcher == null) {
			casDispatcher = new CommandDispatcherCAS();
		}
		return casDispatcher;
	}

	/** dispatcher for scripting commands */
	protected CommandDispatcherInterface scriptingDispatcher = null;

	/** @return dispatcher for scripting commands */
	protected CommandDispatcherInterface getScriptingDispatcher() {
		if (scriptingDispatcher == null) {
			scriptingDispatcher = new CommandDispatcherScripting();
		}
		return scriptingDispatcher;
	}

	/** dispatcher for advanced commands */
	protected CommandDispatcherInterface advancedDispatcher = null;

	/** @return dispatcher for advanced commands */
	protected CommandDispatcherInterface getAdvancedDispatcher() {
		if (advancedDispatcher == null) {
			advancedDispatcher = new CommandDispatcherAdvanced();
		}
		return advancedDispatcher;
	}

	private CommandDispatcherBasic basicDispatcher = null;

	private CommandDispatcherBasic getBasicDispatcher() {
		if (basicDispatcher == null) {
			basicDispatcher = new CommandDispatcherBasic();
		}
		return basicDispatcher;
	}
}
