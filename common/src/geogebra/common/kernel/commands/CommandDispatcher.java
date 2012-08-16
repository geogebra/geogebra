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
			// This enum switch is Markus's idea.
			// Arpad Fekete, 2011-09-28
			switch (Commands.valueOf(cmdName)) {
			case Mod:
				return new CmdMod(kernel);
			case Div:
				return new CmdDiv(kernel);
			case Min:
				return new CmdMin(kernel);
			case Max:
				return new CmdMax(kernel);
			case LCM:
				return new CmdLCM(kernel);
			case GCD:
				return new CmdGCD(kernel);
			case Expand:
				return new CmdExpand(kernel);
			case Factor:
				return new CmdFactor(kernel);
			case Simplify:
				return new CmdSimplify(kernel);
			case PrimeFactors:
				return new CmdPrimeFactors(kernel);
			case CompleteSquare:
				return new CmdCompleteSquare(kernel);
			case Line:
				return new CmdLine(kernel);
			case Ray:
				return new CmdRay(kernel);
			case AngularBisector:
				return new CmdAngularBisector(kernel);
			case OrthogonalLine:
				return new CmdOrthogonalLine(kernel);
			case Tangent:
				return new CmdTangent(kernel);
			case Segment:
				return new CmdSegment(kernel);
			case Slope:
				return new CmdSlope(kernel);
			case Angle:
				return new CmdAngle(kernel);
			case Direction:
				return new CmdDirection(kernel);
			case Point:
				return new CmdPoint(kernel);
			case Midpoint:
				return new CmdMidpoint(kernel);
			case LineBisector:
				return new CmdLineBisector(kernel);
			case Intersect:
				return new CmdIntersect(kernel);
			case IntersectRegion:
				return new CmdIntersectRegion(kernel);
			case Distance:
				return new CmdDistance(kernel);
			case Length:
				return new CmdLength(kernel);
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
			case RigidPolygon:
				return new CmdRigidPolygon(kernel);
			case Area:
				return new CmdArea(kernel);
			case Union:
				return new CmdUnion(kernel);
			case Circumference:
				return new CmdCircumference(kernel);
			case Perimeter:
				return new CmdPerimeter(kernel);
			case Locus:
				return new CmdLocus(kernel);
			case LocusEquation:
				return new CmdLocusEquation(kernel);
			case Centroid:
				return new CmdCentroid(kernel);
			case TriangleCenter:
				return new CmdKimberling(kernel);
			case Barycenter:
				return new CmdBarycenter(kernel);
			case Trilinear:
				return new CmdTrilinear(kernel);
			case Cubic:
				return new CmdCubic(kernel);
			case TriangleCurve:
				return new CmdTriangleCurve(kernel);
			case Vertex:
				return new CmdVertex(kernel);
			case PolyLine:
				return new CmdPolyLine(kernel);
			case PointIn:
				return new CmdPointIn(kernel);
			case AffineRatio:
				return new CmdAffineRatio(kernel);
			case CrossRatio:
				return new CmdCrossRatio(kernel);
			case ClosestPoint:
				return new CmdClosestPoint(kernel);
			case Text:
				return new CmdText(kernel);
			case LaTeX:
				return new CmdLaTeX(kernel);
			case LetterToUnicode:
				return new CmdLetterToUnicode(kernel);
			case TextToUnicode:
				return new CmdTextToUnicode(kernel);
			case UnicodeToText:
				return new CmdUnicodeToText(kernel);
			case UnicodeToLetter:
				return new CmdUnicodeToLetter(kernel);
			case FractionText:
				return new CmdFractionText(kernel);
			case SurdText:
				return new CmdSurdText(kernel);
			case ScientificText:
				return new CmdScientificText(kernel);
			case TableText:
				return new CmdTableText(kernel);
			case VerticalText:
				return new CmdVerticalText(kernel);
			case RotateText:
				return new CmdRotateText(kernel);
			case Ordinal:
				return new CmdOrdinal(kernel);
			case If:
				return new CmdIf(kernel);
			case CountIf:
				return new CmdCountIf(kernel);
			case IsInteger:
				return new CmdIsInteger(kernel);
			case KeepIf:
				return new CmdKeepIf(kernel);
			case Relation:
				return new CmdRelation(kernel);
			case Defined:
				return new CmdDefined(kernel);
			case IsInRegion:
				return new CmdIsInRegion(kernel);
			case Root:
				return new CmdRoot(kernel);
			case Roots:
				return new CmdRoots(kernel);
			case TurningPoint:
				return new CmdTurningPoint(kernel);
			case Polynomial:
				return new CmdPolynomial(kernel);
			case Function:
				return new CmdFunction(kernel);
			case Extremum:
				return new CmdExtremum(kernel);
			case CurveCartesian:
				return new CmdCurveCartesian(kernel);
			case ParametricDerivative:
				return new CmdParametricDerivative(kernel);
			case Derivative:
				return new CmdDerivative(kernel);
			case Integral:
				return new CmdIntegral(kernel, Commands.Integral);
			case IntegralBetween:
				return new CmdIntegral(kernel, Commands.IntegralBetween);
			case NIntegral:
				return new CmdIntegral(kernel, Commands.NIntegral);
			case LowerSum:
				return new CmdLowerSum(kernel);
			case LeftSum:
				return new CmdLeftSum(kernel);
			case RectangleSum:
				return new CmdRectangleSum(kernel);
			case TaylorSeries:
				return new CmdTaylorSeries(kernel);
			case UpperSum:
				return new CmdUpperSum(kernel);
			case TrapezoidalSum:
				return new CmdTrapezoidalSum(kernel);
			case Limit:
				return new CmdLimit(kernel);
			case LimitBelow:
				return new CmdLimitBelow(kernel);
			case LimitAbove:
				return new CmdLimitAbove(kernel);
			case Factors:
				return new CmdFactors(kernel);
			case Degree:
				return new CmdDegree(kernel);
			case Coefficients:
				return new CmdCoefficients(kernel);
			case PartialFractions:
				return new CmdPartialFractions(kernel);
			case Numerator:
				return new CmdNumerator(kernel);
			case Denominator:
				return new CmdDenominator(kernel);
			case ComplexRoot:
				return new CmdComplexRoot(kernel);
			case SolveODE:
				return new CmdSolveODE(kernel);
			case SlopeField:
				return new CmdSlopeField(kernel);
			case Iteration:
				return new CmdIteration(kernel);
			case PathParameter:
				return new CmdPathParameter(kernel);
			case Asymptote:
				return new CmdAsymptote(kernel);
			case CurvatureVector:
				return new CmdCurvatureVector(kernel);
			case Curvature:
				return new CmdCurvature(kernel);
			case OsculatingCircle:
				return new CmdOsculatingCircle(kernel);
			case IterationList:
				return new CmdIterationList(kernel);
			case RootList:
				return new CmdRootList(kernel);
			case ImplicitCurve:
				return new CmdImplicitPoly(kernel);
			case Ellipse:
				return new CmdEllipse(kernel);
			case Hyperbola:
				return new CmdHyperbola(kernel);
			case SecondAxisLength:
				return new CmdSecondAxisLength(kernel);
			case SecondAxis:
				return new CmdSecondAxis(kernel);
			case Directrix:
				return new CmdDirectrix(kernel);
			case Diameter:
				return new CmdDiameter(kernel);
			case Conic:
				return new CmdConic(kernel);
			case FirstAxis:
				return new CmdFirstAxis(kernel);
			case Circle:
				return new CmdCircle(kernel);
			case Incircle:
				return new CmdIncircle(kernel);
			case Semicircle:
				return new CmdSemicircle(kernel);
			case FirstAxisLength:
				return new CmdFirstAxisLength(kernel);
			case Parabola:
				return new CmdParabola(kernel);
			case Focus:
				return new CmdFocus(kernel);
			case Parameter:
				return new CmdParameter(kernel);
			case Center:
				return new CmdCenter(kernel);
			case Polar:
				return new CmdPolar(kernel);
			case Excentricity:
				return new CmdExcentricity(kernel);
			case Eccentricity:
				return new CmdEccentricity(kernel);
			case Axes:
				return new CmdAxes(kernel);
			case Sort:
				return new CmdSort(kernel);
			case First:
				return new CmdFirst(kernel);
			case Last:
				return new CmdLast(kernel);
			case Take:
				return new CmdTake(kernel);
			case RemoveUndefined:
				return new CmdRemoveUndefined(kernel);
			case Reverse:
				return new CmdReverse(kernel);
			case Element:
				return new CmdElement(kernel);
			case IndexOf:
				return new CmdIndexOf(kernel);
			case Append:
				return new CmdAppend(kernel);
			case Join:
				return new CmdJoin(kernel);
			case Flatten:
				return new CmdFlatten(kernel);
			case Insert:
				return new CmdInsert(kernel);
			case Sequence:
				return new CmdSequence(kernel);
			case SelectedElement:
				return new CmdSelectedElement(kernel);
			case SelectedIndex:
				return new CmdSelectedIndex(kernel);
			case RandomElement:
				return new CmdRandomElement(kernel);
			case Product:
				return new CmdProduct(kernel);
			case Frequency:
				return new CmdFrequency(kernel);
			case Unique:
				return new CmdUnique(kernel);
			case Classes:
				return new CmdClasses(kernel);
			case Zip:
				return new CmdZip(kernel);
			case Intersection:
				return new CmdIntersection(kernel);
			case PointList:
				return new CmdPointList(kernel);
			case OrdinalRank:
				return new CmdOrdinalRank(kernel);
			case TiedRank:
				return new CmdTiedRank(kernel);
			case BarChart:
				return new CmdBarChart(kernel);
			case BoxPlot:
				return new CmdBoxPlot(kernel);
			case Histogram:
				return new CmdHistogram(kernel);
			case HistogramRight:
				return new CmdHistogramRight(kernel);
			case DotPlot:
				return new CmdDotPlot(kernel);
			case StemPlot:
				return new CmdStemPlot(kernel);
			case ResidualPlot:
				return new CmdResidualPlot(kernel);
			case FrequencyPolygon:
				return new CmdFrequencyPolygon(kernel);
			case NormalQuantilePlot:
				return new CmdNormalQuantilePlot(kernel);
			case FrequencyTable:
				return new CmdFrequencyTable(kernel);
			case Sum:
				return new CmdSum(kernel);
			case Mean:
				return new CmdMean(kernel);
			case Variance:
				return new CmdVariance(kernel);
			case SD:
				return new CmdSD(kernel);
			case SampleVariance:
				return new CmdSampleVariance(kernel);
			case SampleSD:
				return new CmdSampleSD(kernel);
			case Median:
				return new CmdMedian(kernel);
			case Q1:
				return new CmdQ1(kernel);
			case Q3:
				return new CmdQ3(kernel);
			case Mode:
				return new CmdMode(kernel);
			case SigmaXX:
				return new CmdSigmaXX(kernel);
			case SigmaXY:
				return new CmdSigmaXY(kernel);
			case SigmaYY:
				return new CmdSigmaYY(kernel);
			case Covariance:
				return new CmdCovariance(kernel);
			case SXY:
				return new CmdSXY(kernel);
			case SXX:
				return new CmdSXX(kernel);
			case SYY:
				return new CmdSYY(kernel);
			case MeanX:
				return new CmdMeanX(kernel);
			case MeanY:
				return new CmdMeanY(kernel);
			case PMCC:
				return new CmdPMCC(kernel);
			case SampleSDX:
				return new CmdSampleSDX(kernel);
			case SampleSDY:
				return new CmdSampleSDY(kernel);
			case SDX:
				return new CmdSDX(kernel);
			case SDY:
				return new CmdSDY(kernel);
			case FitLineY:
				return new CmdFitLineY(kernel);
			case FitLineX:
				return new CmdFitLineX(kernel);
			case FitPoly:
				return new CmdFitPoly(kernel);
			case FitExp:
				return new CmdFitExp(kernel);
			case FitLog:
				return new CmdFitLog(kernel);
			case FitPow:
				return new CmdFitPow(kernel);
			case Fit:
				return new CmdFit(kernel);
			case FitGrowth:
				return new CmdFitGrowth(kernel);
			case FitSin:
				return new CmdFitSin(kernel);
			case FitLogistic:
				return new CmdFitLogistic(kernel);
			case SumSquaredErrors:
				return new CmdSumSquaredErrors(kernel);
			case RSquare:
				return new CmdRSquare(kernel);
			case Sample:
				return new CmdSample(kernel);
			case Shuffle:
				return new CmdShuffle(kernel);
			case Spearman:
				return new CmdSpearman(kernel);
			case TTest:
				return new CmdTTest(kernel);
			case TTestPaired:
				return new CmdTTestPaired(kernel);
			case TTest2:
				return new CmdTTest2(kernel);
			case TMeanEstimate:
				return new CmdTMeanEstimate(kernel);
			case TMean2Estimate:
				return new CmdTMean2Estimate(kernel);
			case ChiSquareTest:
				return new CmdChiSquareTest(kernel);
			case ANOVA:
				return new CmdANOVA(kernel);
			case Percentile:
				return new CmdPercentile(kernel);
			case GeometricMean:
				return new CmdGeometricMean(kernel);
			case HarmonicMean:
				return new CmdHarmonicMean(kernel);
			case RootMeanSquare:
				return new CmdRootMeanSquare(kernel);
			case Random:
				return new CmdRandom(kernel);
			case RandomNormal:
				return new CmdRandomNormal(kernel);
			case RandomUniform:
				return new CmdRandomUniform(kernel);
			case RandomBinomial:
				return new CmdRandomBinomial(kernel);
			case RandomPoisson:
				return new CmdRandomPoisson(kernel);
			case Normal:
				return new CmdNormal(kernel);
			case LogNormal:
				return new CmdLogNormal(kernel);
			case InverseLogNormal:
				return new CmdInverseLogNormal(kernel);
			case Logistic:
				return new CmdLogistic(kernel);
			case InverseLogistic:
				return new CmdInverseLogistic(kernel);
			case InverseNormal:
				return new CmdInverseNormal(kernel);
			case Binomial:
				return new CmdBinomial(kernel);
			case BinomialDist:
				return new CmdBinomialDist(kernel);
			case Bernoulli:
				return new CmdBernoulli(kernel);
			case InverseBinomial:
				return new CmdInverseBinomial(kernel);
			case TDistribution:
				return new CmdTDistribution(kernel);
			case InverseTDistribution:
				return new CmdInverseTDistribution(kernel);
			case FDistribution:
				return new CmdFDistribution(kernel);
			case InverseFDistribution:
				return new CmdInverseFDistribution(kernel);
			case Gamma:
				return new CmdGamma(kernel);
			case InverseGamma:
				return new CmdInverseGamma(kernel);
			case Cauchy:
				return new CmdCauchy(kernel);
			case InverseCauchy:
				return new CmdInverseCauchy(kernel);
			case ChiSquared:
				return new CmdChiSquared(kernel);
			case InverseChiSquared:
				return new CmdInverseChiSquared(kernel);
			case Exponential:
				return new CmdExponential(kernel);
			case InverseExponential:
				return new CmdInverseExponential(kernel);
			case HyperGeometric:
				return new CmdHyperGeometric(kernel);
			case InverseHyperGeometric:
				return new CmdInverseHyperGeometric(kernel);
			case Pascal:
				return new CmdPascal(kernel);
			case InversePascal:
				return new CmdInversePascal(kernel);
			case Poisson:
				return new CmdPoisson(kernel);
			case InversePoisson:
				return new CmdInversePoisson(kernel);
			case Weibull:
				return new CmdWeibull(kernel);
			case InverseWeibull:
				return new CmdInverseWeibull(kernel);
			case Zipf:
				return new CmdZipf(kernel);
			case InverseZipf:
				return new CmdInverseZipf(kernel);
			case Triangular:
				return new CmdTriangular(kernel);
			case Uniform:
				return new CmdUniform(kernel);
			case Erlang:
				return new CmdErlang(kernel);
			case ApplyMatrix:
				return new CmdApplyMatrix(kernel);
			case UnitVector:
				return new CmdUnitVector(kernel);
			case Vector:
				return new CmdVector(kernel);
			case UnitOrthogonalVector:
				return new CmdUnitOrthogonalVector(kernel);
			case OrthogonalVector:
				return new CmdOrthogonalVector(kernel);
			case Invert:
				return new CmdInvert(kernel);
			case Transpose:
				return new CmdTranspose(kernel);
			case ReducedRowEchelonForm:
				return new CmdReducedRowEchelonForm(kernel);
			case Determinant:
				return new CmdDeterminant(kernel);
			case Identity:
				return new CmdIdentity(kernel);
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
			case CellRange:
				return new CmdCellRange(kernel); // cell range for spreadsheet
													// like A1:A5
			case Row:
				return new CmdRow(kernel);
			case Column:
				return new CmdColumn(kernel);
			case ColumnName:
				return new CmdColumnName(kernel);
			case FillRow:
				return new CmdFillRow(kernel);
			case FillColumn:
				return new CmdFillColumn(kernel);
			case FillCells:
				return new CmdFillCells(kernel);
			case Cell:
				return new CmdCell(kernel);
			case CopyFreeObject:
				return new CmdCopyFreeObject(kernel);
			case SetColor:
				return new CmdSetColor(kernel);
			case SetBackgroundColor:
				return new CmdSetBackgroundColor(kernel);
			case SetDynamicColor:
				return new CmdSetDynamicColor(kernel);
			case SetConditionToShowObject:
				return new CmdSetConditionToShowObject(kernel);
			case SetFilling:
				return new CmdSetFilling(kernel);
			case SetLineThickness:
				return new CmdSetLineThickness(kernel);
			case SetLineStyle:
				return new CmdLineStyle(kernel);
			case SetPointStyle:
				return new CmdSetPointStyle(kernel);
			case SetPointSize:
				return new CmdSetPointSize(kernel);
			case SetFixed:
				return new CmdSetFixed(kernel);
			case SetTrace:
				return new CmdSetTrace(kernel);
			case Rename:
				return new CmdRename(kernel);
			case HideLayer:
				return new CmdHideLayer(kernel);
			case ShowLayer:
				return new CmdShowLayer(kernel);
			case SetCoords:
				return new CmdSetCoords(kernel);
			case Pan:
				return new CmdPan(kernel);
			case CenterView:
				return new CmdCenterView(kernel);
			case ZoomIn:
				return new CmdZoomIn(kernel);
			case SetSeed:
				return new CmdSetSeed(kernel);
			case ZoomOut:
				return new CmdZoomOut(kernel);
			case SetActiveView:
				return new CmdSetActiveView(kernel);
			case SelectObjects:
				return new CmdSelectObjects(kernel);
			case SetLayer:
				return new CmdSetLayer(kernel);
			case SetCaption:
				return new CmdSetCaption(kernel);
			case SetLabelMode:
				return new CmdSetLabelMode(kernel);
			case SetTooltipMode:
				return new CmdSetTooltipMode(kernel);
			case UpdateConstruction:
				return new CmdUpdateConstruction(kernel);
			case SetValue:
				return new CmdSetValue(kernel);
			case PlaySound:
				return new CmdPlaySound(kernel);
			case ParseToNumber:
				return new CmdParseToNumber(kernel);
			case ParseToFunction:
				return new CmdParseToFunction(kernel);
			case StartAnimation:
				return new CmdStartAnimation(kernel);
			case Delete:
				return new CmdDelete(kernel);
			case Slider:
				return new CmdSlider(kernel);
			case Checkbox:
				return new CmdCheckbox(kernel);
			case Textfield:
				return new CmdTextfield(kernel);
			case Button:
				return new CmdButton(kernel);
			case Execute:
				return new CmdExecute(kernel);
			case GetTime:
				return new CmdGetTime(kernel);
			case ShowLabel:
				return new CmdShowLabel(kernel);
			case SetAxesRatio:
				return new CmdSetAxesRatio(kernel);
			case SetVisibleInView:
				return new CmdSetVisibleInView(kernel);
			case ShowAxes:
				return new CmdShowAxes(kernel);
			case ShowGrid:
				return new CmdShowGrid(kernel);
			case Voronoi:
				return new CmdVoronoi(kernel);
			case Hull:
				return new CmdHull(kernel);
			case ConvexHull:
				return new CmdConvexHull(kernel);
			case MinimumSpanningTree:
				return new CmdMinimumSpanningTree(kernel);
			case DelauneyTriangulation:
				return new CmdDelauneyTriangulation(kernel);
			case TravelingSalesman:
				return new CmdTravelingSalesman(kernel);
			case ShortestDistance:
				return new CmdShortestDistance(kernel);
			case Corner:
				return new CmdCorner(kernel);
			case AxisStepX:
				return new CmdAxisStepX(kernel);
			case AxisStepY:
				return new CmdAxisStepY(kernel);
			case ConstructionStep:
				return new CmdConstructionStep(kernel);
			case Object:
				return new CmdObject(kernel);
			case Name:
				return new CmdName(kernel);
			case SlowPlot:
				return new CmdSlowPlot(kernel);
			case ToolImage:
				return new CmdToolImage(kernel);
			case BarCode:
				return kernel.getApplication().newCmdBarCode();
			case Prove:
				return new CmdProve(kernel);
			case ProveDetails:
				return new CmdProveDetails(kernel);
			case DynamicCoordinates:
				return new CmdDynamicCoordinates(kernel);
			case Maximize:
				return new CmdMaximize(kernel);
			case Minimize:
				return new CmdMinimize(kernel);
			case Curve:
				return new CmdCurveCartesian(kernel);
			case FormulaText:
				return new CmdLaTeX(kernel);
			case IsDefined:
				return new CmdDefined(kernel);
			case ConjugateDiameter:
				return new CmdDiameter(kernel);
			case LinearEccentricity:
				return new CmdExcentricity(kernel);
			case MajorAxis:
				return new CmdFirstAxis(kernel);
			case SemiMajorAxisLength:
				return new CmdFirstAxisLength(kernel);
			case PerpendicularBisector:
				return new CmdLineBisector(kernel);
			case PerpendicularLine:
				return new CmdOrthogonalLine(kernel);
			case PerpendicularVector:
				return new CmdOrthogonalVector(kernel);
			case MinorAxis:
				return new CmdSecondAxis(kernel);
			case SemiMinorAxisLength:
				return new CmdSecondAxisLength(kernel);
			case UnitPerpendicularVector:
				return new CmdUnitOrthogonalVector(kernel);
			case CorrelationCoefficient:
				return new CmdPMCC(kernel);
			case FitLine:
				return new CmdFitLineY(kernel);
			case BinomialCoefficient:
				return new CmdBinomial(kernel);
			case RandomBetween:
				return new CmdRandom(kernel);
			case AreCollinear:
				return new CmdAreCollinear(kernel);
			case AreParallel:
				return new CmdAreParallel(kernel);
			case AreConcyclic:
				return new CmdAreConcyclic(kernel);
			case ArePerpendicular:
				return new CmdArePerpendicular(kernel);
			case AreEqual:
				return new CmdAreEqual(kernel);
			case AreConcurrent:
				return new CmdAreConcurrent(kernel);
			case ToBase:
				return new CmdToBase(kernel);
			case FromBase:
				return new CmdFromBase(kernel);
			case ContinuedFraction:
				return new CmdContinuedFraction(kernel);
			case AttachCopyToView:
				return new CmdAttachCopyToView(kernel);
			case Dot:
				return new CmdCAStoOperation(kernel,Operation.MULTIPLY);
			case Cross:
				return new CmdCAStoOperation(kernel,Operation.VECTORPRODUCT);
			case IntegerPart:
				return new CmdCAStoOperation(kernel,Operation.FLOOR);
			case Divisors:
				return new CmdDivisorsOrDivisorsSum(kernel,false);
			case DivisorsSum:
				return new CmdDivisorsOrDivisorsSum(kernel,true);
			case Dimension:
				return new CmdDimension(kernel);
			case DivisorsList:
				return new CmdDivisorsList(kernel);
			case ImplicitDerivative:
				return new CmdImplicitDerivative(kernel);
			case RandomPolynomial:
				return new CmdRandomPolynomial(kernel);
			case IsPrime:
				return new CmdIsPrime(kernel);
			case LeftSide:
				return new CmdLeftRightSide(kernel,true);
			case RightSide:
				return new CmdLeftRightSide(kernel,false);
			case nPr:
				return new CmdNpR(kernel);
			case Division:
				return new CmdDivision(kernel);
			case MatrixRank:
				return new CmdMatrixRank(kernel);
			case NextPrime:
				return new CmdNextPreviousPrime(kernel,true);
			case PreviousPrime:
				return new CmdNextPreviousPrime(kernel,false);
		
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
			case CommonDenominator:
				return new CmdCommonDenominator(kernel);
			case ToPoint:
				return new CmdToComplexPolar(kernel,Kernel.COORD_CARTESIAN);
			case ToComplex:
				return new CmdToComplexPolar(kernel,Kernel.COORD_COMPLEX);
			case ToPolar:
				return new CmdToComplexPolar(kernel,Kernel.COORD_POLAR);
			case TrigExpand:
				return new CmdTrigExpand(kernel);
			case TrigSimplify:
				return new CmdTrigSimplify(kernel);
			case TrigCombine:
				return new CmdTrigCombine(kernel);
			case Turtle: 
				return new CmdTurtle(kernel);
			case TurtleForward:
				return new CmdTurtleForward(kernel);
			case TurtleBack:
				return new CmdTurtleBack(kernel);
			case TurtleLeft:
				return new CmdTurtleLeft(kernel);
			case TurtleRight:
				return new CmdTurtleRight(kernel);
			case ZProportionTest:
				return new CmdZProportionTest(kernel);
			case ZProportion2Test:
				return new CmdZProportion2Test(kernel);
			case ZProportionEstimate:
				return new CmdZProportionEstimate(kernel);
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
}
