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
import geogebra.common.plugin.Operation;

import java.util.HashMap;
import java.util.Set;

/**
 * Runs commands and handles string to command processor conversion.
 * 
 */
public class CommandDispatcher {

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
	public static final int tableCount = GeoGebraConstants.CAS_VIEW_ENABLED ? 19
			: 18;

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
	 * Returns a set with all command names available in the GeoGebra input
	 * field.
	 * 
	 * @return Set of all command names
	 */
	public Set<String> getPublicCommandSet() {
		if (cmdTable == null) {
			initCmdTable();
		}

		return cmdTable.keySet();
	}

	/**
	 * Returns whether the given command name is supported in GeoGebra.
	 * 
	 * @param cmd
	 *            command name
	 * @return whether the given command name is supported in GeoGebra.
	 */
	public boolean isCommandAvailable(String cmd) {
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
			throw new MyError(app, app.getError("UnknownCommand") + " : "
					+ app.getCommand(c.getName()));

		// switch on macro mode to avoid labeling of output if desired
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
			throw new MyError(app, app.getError("CAS.GeneralErrorMessage"));
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
			case Delete:
			case Slider:
			case Checkbox:
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
				return getScriptingDispatcher().dispatch(command,kernel);
	
				
				
				
				
				// advanced
			case Mod:
			case Div:
			case Min:
			case Max:
			case LCM:
			case GCD:
			case IntersectRegion:
			case Direction:
			case Extremum:

			case TaylorPolynomial:
			case TaylorSeries:

			case Defined:
			case IsDefined:
			
			case UnitPerpendicularVector:
			case UnitOrthogonalVector:
				
			case Text:
			case FormulaText:
			case LaTeX:

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
			case CountIf:
			case IsInteger:
			case KeepIf:
			case IsInRegion:
			case PrimeFactors:
			case CompleteSquare:
			case Union:
			case LetterToUnicode:
			case TextToUnicode:
			case UnicodeToText:
			case UnicodeToLetter:
			case FractionText:
			case ScientificText:
			case TableText:
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
			case UnitVector:
			case Invert:
			case Transpose:
			case ReducedRowEchelonForm:
			case Determinant:
			case Identity:
			case Centroid:
			
			case MajorAxis:
			case FirstAxis:
			
			case SemiMajorAxisLength:
			case FirstAxisLength:
				
			case AxisStepX:
			case AxisStepY:
			case ConstructionStep:
			case Object:
			case Polar:
			
			case LinearEccentricity:
			case Excentricity:
			
			case Eccentricity:
			case Axes:
			case First:
			case Last:
			case Take:
			case RemoveUndefined:
			case Reverse:
			case IndexOf:
			case Append:
			case Join:
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
			case nPr:
			case Division:
			case MatrixRank:
			case CommonDenominator:
			case ToPoint:
			case ToComplex:
			case ToPolar:
				return getAdvancedDispatcher().dispatch(command,kernel);
				
				
				
				
				
			case Vector:
				return new CmdVector(kernel);
			case BarCode:
				return kernel.getApplication().newCmdBarCode();
			case Dot:
				return new CmdCAStoOperation(kernel,Operation.MULTIPLY);
			case Cross:
				return new CmdCAStoOperation(kernel,Operation.VECTORPRODUCT);
			case IntegerPart:
				return new CmdCAStoOperation(kernel,Operation.FLOOR);
			case PolyLine:
				return new CmdPolyLine(kernel);
			case PointIn:
				return new CmdPointIn(kernel);
			case Line:
				return new CmdLine(kernel);
			case Ray:
				return new CmdRay(kernel);
			case AngularBisector:
				return new CmdAngularBisector(kernel);
			case Segment:
				return new CmdSegment(kernel);
			case Slope:
				return new CmdSlope(kernel);
			case Angle:
				return new CmdAngle(kernel);
			case Point:
				return new CmdPoint(kernel);
			case Midpoint:
				return new CmdMidpoint(kernel);
			case Intersect:
				return new CmdIntersect(kernel);
			case Distance:
				return new CmdDistance(kernel);
			case Radius:
				return new CmdRadius(kernel);
			case CircleArc:
				return new CmdCircleArc(kernel);
			case Arc:
				return new CmdArc(kernel);
			case Sector:
				return new CmdSector(kernel);
			case CircleSector:
				return new CmdCircleSector(kernel);
			case CircumcircleSector:
				return new CmdCircumcircleSector(kernel);
			case CircumcircleArc:
				return new CmdCircumcircleArc(kernel);
			case Polygon:
				return new CmdPolygon(kernel);
			case Area:
				return new CmdArea(kernel);
			case Circumference:
				return new CmdCircumference(kernel);
			case Perimeter:
				return new CmdPerimeter(kernel);
			case Locus:
				return new CmdLocus(kernel);
			case Vertex:
				return new CmdVertex(kernel);
			case If:
				return new CmdIf(kernel);
			case Root:
				return new CmdRoot(kernel);
			case TurningPoint:
				return new CmdTurningPoint(kernel);
			case Polynomial:
				return new CmdPolynomial(kernel);
			case Function:
				return new CmdFunction(kernel);

			case Curve:
			case CurveCartesian:
				return new CmdCurveCartesian(kernel);
			
			case LowerSum:
				return new CmdLowerSum(kernel);
			case LeftSum:
				return new CmdLeftSum(kernel);
			case RectangleSum:
				return new CmdRectangleSum(kernel);
				
				
			case UpperSum:
				return new CmdUpperSum(kernel);
			case TrapezoidalSum:
				return new CmdTrapezoidalSum(kernel);
			case Ellipse:
				return new CmdEllipse(kernel);
			case Hyperbola:
				return new CmdHyperbola(kernel);
			case Conic:
				return new CmdConic(kernel);
			case Circle:
				return new CmdCircle(kernel);
			case Semicircle:
				return new CmdSemicircle(kernel);
			case Parabola:
				return new CmdParabola(kernel);
			case Focus:
				return new CmdFocus(kernel);
			case Center:
				return new CmdCenter(kernel);
			case Element:
				return new CmdElement(kernel);
			case Sequence:
				return new CmdSequence(kernel);
			case Mirror:
				return new CmdMirror(kernel);
			case Dilate:
				return new CmdDilate(kernel);
			case Rotate:
				return new CmdRotate(kernel);
			case Translate:
				return new CmdTranslate(kernel);
			case Shear:
				return new CmdShear(kernel);
			case Stretch:
				return new CmdStretch(kernel);

			case Corner:
				return new CmdCorner(kernel);
			case Name:
				return new CmdName(kernel);

			
			case Diameter:
			case ConjugateDiameter:
				return new CmdDiameter(kernel);
				
			case LineBisector:
			case PerpendicularBisector:
				return new CmdLineBisector(kernel);
				
			case OrthogonalLine:
			case PerpendicularLine:
				return new CmdOrthogonalLine(kernel);

			case OrthogonalVector:
			case PerpendicularVector:
				return new CmdOrthogonalVector(kernel);
				
			case Random:
				return new CmdRandom(kernel);
			case RandomBetween:
				return new CmdRandom(kernel);


			case CFactor:
			case CSolutions:
			case CSolve:
			case Groebner:
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
			case BarChart:
			case Bernoulli:
			case Binomial:
			case BinomialCoefficient:
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
			case DotPlot:
			case Erlang:
			case Exponential:
			case FDistribution:
			case FillCells:
			case FillColumn:
			case FillRow:
			case Fit:
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
			case OrdinalRank:
			case PMCC:
			case Pascal:
			case Percentile:
			case Poisson:
			case Product:
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
			case Sort:
			case Spearman:
			case StemPlot:
			case Sum:
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
				return getStatsDispatcher().dispatch(command,kernel);

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
				return getDiscreteDispatcher().dispatch(command,kernel);
				
			case LocusEquation:
			case Expand:
			case Factor:
			case Simplify:
			case SurdText:
			case Tangent:
			case ParametricDerivative:
			case Derivative:
			case Integral:
			case IntegralBetween:
			case NIntegral:
			case TrigExpand:
			case TrigSimplify:
			case TrigCombine:
			case Length:
			case Limit:
			case LimitBelow:
			case LimitAbove:
			case Factors:
			case Degree:
			case Coefficients:
			case PartialFractions:
			case SolveODE:
			case ImplicitDerivative:
			case NextPrime:
			case PreviousPrime:
				return getCASDispatcher().dispatch(command,kernel);

			default:
				App.error("missing case in CommandDispatcher "+cmdName);
				return null;
			}
		} catch (Exception e) {
			App.warn("command not found / CAS command called:"
					+ cmdName);
		}
		return null;
	}
	
	private CommandDispatcherStats statsDispatcher = null;
	private CommandDispatcherStats getStatsDispatcher() {
		if(statsDispatcher == null) {
			statsDispatcher = new CommandDispatcherStats();
		}
		return statsDispatcher;
	}

	private CommandDispatcherDiscrete discreteDispatcher = null;
	private CommandDispatcherDiscrete getDiscreteDispatcher() {
		if(discreteDispatcher == null) {
			discreteDispatcher = new CommandDispatcherDiscrete();
		}
		return discreteDispatcher;
	}
	
	private CommandDispatcherCAS casDispatcher = null;
	private CommandDispatcherCAS getCASDispatcher() {
		if(casDispatcher == null) {
			casDispatcher = new CommandDispatcherCAS();
		}
		return casDispatcher;
	}

	private CommandDispatcherScripting scriptingDispatcher = null;
	private CommandDispatcherScripting getScriptingDispatcher() {
		if(scriptingDispatcher == null) {
			scriptingDispatcher = new CommandDispatcherScripting();
		}
		return scriptingDispatcher;
	}

	private CommandDispatcherAdvanced advancedDispatcher = null;
	private CommandDispatcherAdvanced getAdvancedDispatcher() {
		if(advancedDispatcher == null) {
			advancedDispatcher = new CommandDispatcherAdvanced();
		}
		return advancedDispatcher;
	}

}
