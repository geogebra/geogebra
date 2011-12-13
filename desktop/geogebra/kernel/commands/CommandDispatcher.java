/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.commands;

import geogebra.common.kernel.AbstractKernel;
import geogebra.common.kernel.commands.AbstractCommandDispatcher;
import geogebra.common.kernel.commands.CmdLine;
import geogebra.common.kernel.commands.CmdLineBisector;
import geogebra.common.kernel.commands.CmdSegment;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.main.AbstractApplication;
import geogebra.kernel.Kernel;

import java.util.HashMap;

/**
 * Runs commands and handles string to command processor conversion.
 * 
 */
public class CommandDispatcher extends AbstractCommandDispatcher{
    
	/**
	 * @param kernel Kernel
	 */
	public CommandDispatcher(AbstractKernel kernel){
		super(kernel);
	}
	
	@Override
	protected void fillInternalCmdTable(){
		// internal command table for commands that should not be visible to the user
		internalCmdTable = new HashMap<String,CommandProcessor>();
		// support parsing diff() results back from Maxima
		internalCmdTable.put("diff", new CmdDerivative(kernel));
		
		// if there will be more commands here, it could be refactored
		// in the same manner as cmdTable initialization was refactored

	}
    /**
     * This method returns a CommandProcessor Object for a corresponding
     * command name. This should be called only if that CommandProcessor
     * object is not there already in the command table.
     * 
     * @param cmdName String command name
     */
    @Override
	public CommandProcessor commandTableSwitch(String cmdName) {
    	try {
    		Kernel kernel = (Kernel)this.kernel;
    		// This enum switch is Markus's idea.
    		// Arpad Fekete, 2011-09-28
    		switch(Commands.valueOf(cmdName)) {
    			case Mod: return new CmdMod(kernel);
    			case Div: return new CmdDiv(kernel);
    			case Min: return new CmdMin(kernel);
    			case Max: return new CmdMax(kernel);
    			case LCM: return new CmdLCM(kernel);
    			case GCD: return new CmdGCD(kernel);
    			case Expand: return new CmdExpand(kernel);
    			case Factor: return new CmdFactor(kernel);
    			case Simplify: return new CmdSimplify(kernel);   
    			case PrimeFactors: return new CmdPrimeFactors(kernel);
    			case CompleteSquare: return new CmdCompleteSquare(kernel);
    			case Line: return new CmdLine(kernel);	   
    			case Ray: return new CmdRay(kernel);	   
    			case AngularBisector: return new CmdAngularBisector(kernel);
    			case OrthogonalLine: return new CmdOrthogonalLine(kernel);
    			case Tangent: return new CmdTangent(kernel);
    			case Segment: return new CmdSegment(kernel);
    			case Slope: return new CmdSlope(kernel);
    			case Angle: return new CmdAngle(kernel);
    			case Direction: return new CmdDirection(kernel);
    			case Point: return new CmdPoint(kernel);
    			case Midpoint: return new CmdMidpoint(kernel);
    			case LineBisector: return new CmdLineBisector(kernel);
    			case Intersect: return new CmdIntersect(kernel);
    			case IntersectRegion: return new CmdIntersectRegion(kernel);
    			case Distance: return new CmdDistance(kernel);
    			case Length: return new CmdLength(kernel);
    			case Radius: return new CmdRadius(kernel);
    			case CircleArc: return new CmdCircleArc(kernel);
    			case Arc: return new CmdArc(kernel);
    			case Sector: return new CmdSector(kernel);
    			case CircleSector: return new CmdCircleSector(kernel);	   
    			case CircumcircleSector: return new CmdCircumcircleSector(kernel);	     
    			case CircumcircleArc: return new CmdCircumcircleArc(kernel);
    			case Polygon: return new CmdPolygon(kernel);
    			case RigidPolygon: return new CmdRigidPolygon(kernel);	   
    			case Area: return new CmdArea(kernel);
    			case Union: return new CmdUnion(kernel);
    			case Circumference: return new CmdCircumference(kernel);
    			case Perimeter: return new CmdPerimeter(kernel);
    			case Locus: return new CmdLocus(kernel);	   
    			case Centroid: return new CmdCentroid(kernel);	   
    			case TriangleCenter: return new CmdKimberling(kernel);
    			case Barycenter: return new CmdBarycenter(kernel);
    			case Trilinear: return new CmdTrilinear(kernel);
    			case TriangleCubic: return new CmdTriangleCubic(kernel);
    			case TriangleCurve: return new CmdTriangleCurve(kernel);
    			case Vertex: return new CmdVertex(kernel);	
    			case PolyLine: return new CmdPolyLine(kernel);	   
    			case PointIn: return new CmdPointIn(kernel);   
    			case AffineRatio: return new CmdAffineRatio(kernel);
    			case CrossRatio: return new CmdCrossRatio(kernel);
    			case ClosestPoint: return new CmdClosestPoint(kernel);     
    			case Text: return new CmdText(kernel);    	
    			case LaTeX: return new CmdLaTeX(kernel);
    			case LetterToUnicode: return new CmdLetterToUnicode(kernel);    	
    			case TextToUnicode: return new CmdTextToUnicode(kernel);    	
    			case UnicodeToText: return new CmdUnicodeToText(kernel);    
    			case UnicodeToLetter: return new CmdUnicodeToLetter(kernel);    
    			case FractionText: return new CmdFractionText(kernel);
    			case SurdText: return new CmdSurdText(kernel); 
    			case TableText: return new CmdTableText(kernel); 
    			case VerticalText: return new CmdVerticalText(kernel);	   
    			case RotateText: return new CmdRotateText(kernel);	   
    			case Ordinal: return new CmdOrdinal(kernel);
    			case If: return new CmdIf(kernel);
    			case CountIf: return new CmdCountIf(kernel);   
    			case IsInteger: return new CmdIsInteger(kernel);
    			case KeepIf: return new CmdKeepIf(kernel);
    			case Relation: return new CmdRelation(kernel);	 
    			case Defined: return new CmdDefined(kernel);
    			case IsInRegion: return new CmdIsInRegion(kernel);    
    			case Root: return new CmdRoot(kernel);	
    			case Roots: return new CmdRoots(kernel);
    			case TurningPoint: return new CmdTurningPoint(kernel);
    			case Polynomial: return new CmdPolynomial(kernel);	
    			case Function: return new CmdFunction(kernel);	   
    			case Extremum: return new CmdExtremum(kernel);	
    			case CurveCartesian: return new CmdCurveCartesian(kernel);
    			case Derivative: return new CmdDerivative(kernel);
    			case Integral: return new CmdIntegral(kernel, false);	   
    			case IntegralBetween: return new CmdIntegral(kernel, true);	   
    			case LowerSum: return new CmdLowerSum(kernel);
    			case LeftSum: return new CmdLeftSum(kernel);
    			case RectangleSum: return new CmdRectangleSum(kernel);    	
    			case TaylorSeries: return new CmdTaylorSeries(kernel);	 
    			case UpperSum: return new CmdUpperSum(kernel);
    			case TrapezoidalSum: return new CmdTrapezoidalSum(kernel); 
    			case Limit: return new CmdLimit(kernel);
    			case LimitBelow: return new CmdLimitBelow(kernel);   
    			case LimitAbove: return new CmdLimitAbove(kernel);
    			case Factors: return new CmdFactors(kernel);
    			case Degree: return new CmdDegree(kernel);
    			case Coefficients: return new CmdCoefficients(kernel);
    			case PartialFractions: return new CmdPartialFractions(kernel);
    			case Numerator: return new CmdNumerator(kernel);
    			case Denominator: return new CmdDenominator(kernel);
    			case ComplexRoot: return new CmdComplexRoot(kernel);
    			case SolveODE: return new CmdSolveODE(kernel);
    			case Iteration: return new CmdIteration(kernel);
    			case PathParameter: return new CmdPathParameter(kernel);
    			case Asymptote: return new CmdAsymptote(kernel);
    			case CurvatureVector: return new CmdCurvatureVector(kernel);
    			case Curvature: return new CmdCurvature(kernel);
    			case OsculatingCircle: return new CmdOsculatingCircle(kernel);
    			case IterationList: return new CmdIterationList(kernel);
    			case RootList: return new CmdRootList(kernel);
    			case ImplicitCurve: return new CmdImplicitPoly(kernel);
    			case Ellipse: return new CmdEllipse(kernel);
    			case Hyperbola: return new CmdHyperbola(kernel);	   
    			case SecondAxisLength: return new CmdSecondAxisLength(kernel);	
    			case SecondAxis: return new CmdSecondAxis(kernel);
    			case Directrix: return new CmdDirectrix(kernel);	   
    			case Diameter: return new CmdDiameter(kernel);
    			case Conic: return new CmdConic(kernel);
    			case FirstAxis: return new CmdFirstAxis(kernel);
    			case Circle: return new CmdCircle(kernel);
    			case Incircle: return new CmdIncircle(kernel);
    			case Semicircle: return new CmdSemicircle(kernel);
    			case FirstAxisLength: return new CmdFirstAxisLength(kernel);
    			case Parabola: return new CmdParabola(kernel);
    			case Focus: return new CmdFocus(kernel);
    			case Parameter: return new CmdParameter(kernel);
    			case Center: return new CmdCenter(kernel);
    			case Polar: return new CmdPolar(kernel);	 
    			case Excentricity: return new CmdExcentricity(kernel);	  
    			case Eccentricity: return new CmdEccentricity(kernel);	  
    			case Axes: return new CmdAxes(kernel);
    			case Sort: return new CmdSort(kernel);
    			case First: return new CmdFirst(kernel);
    			case Last: return new CmdLast(kernel);
    			case Take: return new CmdTake(kernel);
    			case RemoveUndefined: return new CmdRemoveUndefined(kernel);
    			case Reverse: return new CmdReverse(kernel);
    			case Element: return new CmdElement(kernel);
    			case IndexOf: return new CmdIndexOf(kernel);
    			case Append: return new CmdAppend(kernel);
    			case Join: return new CmdJoin(kernel);
    			case Flatten: return new CmdFlatten(kernel);
    			case Insert: return new CmdInsert(kernel);   
    			case Sequence: return new CmdSequence(kernel);
    			case SelectedElement: return new CmdSelectedElement(kernel);
    			case SelectedIndex: return new CmdSelectedIndex(kernel);
    			case RandomElement: return new CmdRandomElement(kernel);
    			case Product: return new CmdProduct(kernel);
    			case Frequency: return new CmdFrequency(kernel);
    			case Unique: return new CmdUnique(kernel);
    			case Classes: return new CmdClasses(kernel);
    			case Zip: return new CmdZip(kernel);
    			case Intersection: return new CmdIntersection(kernel);  	
    			case PointList: return new CmdPointList(kernel);
    			case OrdinalRank: return new CmdOrdinalRank(kernel);
    			case TiedRank: return new CmdTiedRank(kernel);
    			case BarChart: return new CmdBarChart(kernel);    	
    			case BoxPlot: return new CmdBoxPlot(kernel);    	
    			case Histogram: return new CmdHistogram(kernel);
    			case HistogramRight: return new CmdHistogramRight(kernel); 
    			case DotPlot: return new CmdDotPlot(kernel);
    			case StemPlot: return new CmdStemPlot(kernel);  
    			case ResidualPlot: return new CmdResidualPlot(kernel);  
    			case FrequencyPolygon: return new CmdFrequencyPolygon(kernel);
    			case NormalQuantilePlot: return new CmdNormalQuantilePlot(kernel);
    			case FrequencyTable: return new CmdFrequencyTable(kernel);
    			case Sum: return new CmdSum(kernel);
    			case Mean: return new CmdMean(kernel);
    			case Variance: return new CmdVariance(kernel);
    			case SD: return new CmdSD(kernel);
    			case SampleVariance: return new CmdSampleVariance(kernel);
    			case SampleSD: return new CmdSampleSD(kernel);
    			case Median: return new CmdMedian(kernel);
    			case Q1: return new CmdQ1(kernel);
    			case Q3: return new CmdQ3(kernel);
    			case Mode: return new CmdMode(kernel);	
    			case SigmaXX: return new CmdSigmaXX(kernel);
    			case SigmaXY: return new CmdSigmaXY(kernel);
    			case SigmaYY: return new CmdSigmaYY(kernel);
    			case Covariance: return new CmdCovariance(kernel);
    			case SXY: return new CmdSXY(kernel);
    			case SXX: return new CmdSXX(kernel);
    			case SYY: return new CmdSYY(kernel);
    			case MeanX: return new CmdMeanX(kernel);
    			case MeanY: return new CmdMeanY(kernel);
    			case PMCC: return new CmdPMCC(kernel);
    			case SampleSDX: return new CmdSampleSDX(kernel);
    			case SampleSDY: return new CmdSampleSDY(kernel);
    			case SDX: return new CmdSDX(kernel);
    			case SDY: return new CmdSDY(kernel);
    			case FitLineY: return new CmdFitLineY(kernel);
    			case FitLineX: return new CmdFitLineX(kernel);
    			case FitPoly: return new CmdFitPoly(kernel);
    			case FitExp: return new CmdFitExp(kernel);
    			case FitLog: return new CmdFitLog(kernel);
    			case FitPow: return new CmdFitPow(kernel);
    			case Fit: return new CmdFit(kernel);
    			case FitGrowth: return new CmdFitGrowth(kernel);
    			case FitSin: return new CmdFitSin(kernel);
    			case FitLogistic: return new CmdFitLogistic(kernel);  
    			case SumSquaredErrors: return new CmdSumSquaredErrors(kernel);
    			case RSquare: return new CmdRSquare(kernel);
    			case Sample: return new CmdSample(kernel);	  
    			case Shuffle: return new CmdShuffle(kernel);
    			case Spearman: return new CmdSpearman(kernel);
    			case TTest: return new CmdTTest(kernel);
    			case TTestPaired: return new CmdTTestPaired(kernel);
    			case TTest2: return new CmdTTest2(kernel);
    			case TMeanEstimate: return new CmdTMeanEstimate(kernel);
    			case TMean2Estimate: return new CmdTMean2Estimate(kernel);
    			case ANOVA: return new CmdANOVA(kernel);
    			case Percentile: return new CmdPercentile(kernel);
    			case GeometricMean: return new CmdGeometricMean(kernel);
    			case HarmonicMean: return new CmdHarmonicMean(kernel);
    			case RootMeanSquare: return new CmdRootMeanSquare(kernel);
    			case Random: return new CmdRandom(kernel);   
    			case RandomNormal: return new CmdRandomNormal(kernel);
    			case RandomUniform: return new CmdRandomUniform(kernel);  
    			case RandomBinomial: return new CmdRandomBinomial(kernel);   
    			case RandomPoisson: return new CmdRandomPoisson(kernel); 
    			case Normal: return new CmdNormal(kernel);
    			case LogNormal: return new CmdLogNormal(kernel);
    			case Logistic: return new CmdLogistic(kernel);
    			case InverseNormal: return new CmdInverseNormal(kernel);
    			case Binomial: return new CmdBinomial(kernel);
    			case BinomialDist: return new CmdBinomialDist(kernel);
    			case Bernoulli: return new CmdBernoulli(kernel);
    			case InverseBinomial: return new CmdInverseBinomial(kernel); 
    			case TDistribution: return new CmdTDistribution(kernel);
    			case InverseTDistribution: return new CmdInverseTDistribution(kernel);  
    			case FDistribution: return new CmdFDistribution(kernel);
    			case InverseFDistribution: return new CmdInverseFDistribution(kernel);
    			case Gamma: return new CmdGamma(kernel);
    			case InverseGamma: return new CmdInverseGamma(kernel);
    			case Cauchy: return new CmdCauchy(kernel);
    			case InverseCauchy: return new CmdInverseCauchy(kernel);
    			case ChiSquared: return new CmdChiSquared(kernel);
    			case InverseChiSquared: return new CmdInverseChiSquared(kernel);
    			case Exponential: return new CmdExponential(kernel);
    			case InverseExponential: return new CmdInverseExponential(kernel);
    			case HyperGeometric: return new CmdHyperGeometric(kernel);
    			case InverseHyperGeometric: return new CmdInverseHyperGeometric(kernel);
    			case Pascal: return new CmdPascal(kernel);
    			case InversePascal: return new CmdInversePascal(kernel);
    			case Poisson: return new CmdPoisson(kernel);
    			case InversePoisson: return new CmdInversePoisson(kernel);
    			case Weibull: return new CmdWeibull(kernel);
    			case InverseWeibull: return new CmdInverseWeibull(kernel);
    			case Zipf: return new CmdZipf(kernel);
    			case InverseZipf: return new CmdInverseZipf(kernel);
    			case Triangular: return new CmdTriangular(kernel);
    			case Uniform: return new CmdUniform(kernel);
    			case Erlang: return new CmdErlang(kernel);
    			case ApplyMatrix: return new CmdApplyMatrix(kernel); 
    			case UnitVector: return new CmdUnitVector(kernel);	   
    			case Vector: return new CmdVector(kernel);
    			case UnitOrthogonalVector: return new CmdUnitOrthogonalVector(kernel);	
    			case OrthogonalVector: return new CmdOrthogonalVector(kernel);
    			case Invert: return new CmdInvert(kernel);
    			case Transpose: return new CmdTranspose(kernel);   
    			case ReducedRowEchelonForm: return new CmdReducedRowEchelonForm(kernel);
    			case Determinant: return new CmdDeterminant(kernel);
    			case Identity: return new CmdIdentity(kernel);
    			case Mirror: return new CmdMirror(kernel);
    			case Dilate: return new CmdDilate(kernel);	
    			case Rotate: return new CmdRotate(kernel);	
    			case Translate: return new CmdTranslate(kernel);
    			case Shear: return new CmdShear(kernel);
    			case Stretch: return new CmdStretch(kernel);
    			case CellRange: return new CmdCellRange(kernel);  // cell range for spreadsheet like A1:A5
    			case Row: return new CmdRow(kernel);
    			case Column: return new CmdColumn(kernel);
    			case ColumnName: return new CmdColumnName(kernel); 
    			case FillRow: return new CmdFillRow(kernel);
    			case FillColumn: return new CmdFillColumn(kernel);
    			case FillCells: return new CmdFillCells(kernel);   	
    			case Cell: return new CmdCell(kernel);
    			case CopyFreeObject: return new CmdCopyFreeObject(kernel);
    			case SetColor: return new CmdSetColor(kernel);
    			case SetBackgroundColor: return new CmdSetBackgroundColor(kernel);
    			case SetDynamicColor: return new CmdSetDynamicColor(kernel);
    			case SetConditionToShowObject: return new CmdSetConditionToShowObject(kernel);
    			case SetFilling: return new CmdSetFilling(kernel);
    			case SetLineThickness: return new CmdSetLineThickness(kernel);
    			case SetLineStyle: return new CmdLineStyle(kernel);
    			case SetPointStyle: return new CmdSetPointStyle(kernel);
    			case SetPointSize: return new CmdSetPointSize(kernel);
    			case SetFixed: return new CmdSetFixed(kernel);
    			case Rename: return new CmdRename(kernel);
    			case HideLayer: return new CmdHideLayer(kernel);
    			case ShowLayer: return new CmdShowLayer(kernel);
    			case SetCoords: return new CmdSetCoords(kernel);
    			case Pan: return new CmdPan(kernel);
    			case ZoomIn: return new CmdZoomIn(kernel);
    			case ZoomOut: return new CmdZoomOut(kernel);
    			case SetActiveView: return new CmdSetActiveView(kernel);
    			case SelectObjects: return new CmdSelectObjects(kernel);
    			case SetLayer: return new CmdSetLayer(kernel);
    			case SetCaption: return new CmdSetCaption(kernel);
    			case SetLabelMode: return new CmdSetLabelMode(kernel);
    			case SetTooltipMode: return new CmdSetTooltipMode(kernel);
    			case UpdateConstruction: return new CmdUpdateConstruction(kernel);
    			case SetValue: return new CmdSetValue(kernel);
    			case PlaySound: return new CmdPlaySound(kernel);
    			case ParseToNumber: return new CmdParseToNumber(kernel);
    			case ParseToFunction: return new CmdParseToFunction(kernel); 
    			case StartAnimation: return new CmdStartAnimation(kernel); 
    			case Delete: return new CmdDelete(kernel);
    			case Slider: return new CmdSlider(kernel);
    			case Checkbox: return new CmdCheckbox(kernel);
    			case Textfield: return new CmdTextfield(kernel);
    			case Button: return new CmdButton(kernel);
    			case Execute: return new CmdExecute(kernel);     
    			case GetTime: return new CmdGetTime(kernel);     
    			case ShowLabel: return new CmdShowLabel(kernel);
    			case SetAxesRatio: return new CmdSetAxesRatio(kernel);   
    			case SetVisibleInView: return new CmdSetVisibleInView(kernel);
    			case Voronoi: return new CmdVoronoi(kernel);
    			case Hull: return new CmdHull(kernel);
    			case ConvexHull: return new CmdConvexHull(kernel);
    			case MinimumSpanningTree: return new CmdMinimumSpanningTree(kernel);
    			case DelauneyTriangulation: return new CmdDelauneyTriangulation(kernel);     	                  
    			case TravelingSalesman: return new CmdTravelingSalesman(kernel);
    			case ShortestDistance: return new CmdShortestDistance(kernel);
    			case Corner: return new CmdCorner(kernel);
    			case AxisStepX: return new CmdAxisStepX(kernel);   
    			case AxisStepY: return new CmdAxisStepY(kernel);   
    			case ConstructionStep: return new CmdConstructionStep(kernel);
    			case Object: return new CmdObject(kernel);
    			case Name: return new CmdName(kernel);
    			case SlowPlot: return new CmdSlowPlot(kernel);	   
    			case ToolImage: return new CmdToolImage(kernel);
    			case DynamicCoordinates: return new CmdDynamicCoordinates(kernel);
    			case Maximize: return new CmdMaximize(kernel);
    			case Minimize: return new CmdMinimize(kernel);
    			case Curve: return new CmdCurveCartesian(kernel);
    			case FormulaText: return new CmdLaTeX(kernel);
    			case IsDefined: return new CmdDefined(kernel);
    			case ConjugateDiameter: return new CmdDiameter(kernel);
    			case LinearEccentricity: return new CmdExcentricity(kernel);
    			case MajorAxis: return new CmdFirstAxis(kernel);
    			case SemiMajorAxisLength: return new CmdFirstAxisLength(kernel);
    			case PerpendicularBisector: return new CmdLineBisector(kernel);
    			case PerpendicularLine: return new CmdOrthogonalLine(kernel);
    			case PerpendicularVector: return new CmdOrthogonalVector(kernel);
    			case MinorAxis: return new CmdSecondAxis(kernel);
    			case SemiMinorAxisLength: return new CmdSecondAxisLength(kernel);
    			case UnitPerpendicularVector: return new CmdUnitOrthogonalVector(kernel);
    			case CorrelationCoefficient: return new CmdPMCC(kernel);
    			case FitLine: return new CmdFitLineY(kernel);
    			case BinomialCoefficient: return new CmdBinomial(kernel);
    			case RandomBetween: return new CmdRandom(kernel);  
    			default:
    				AbstractApplication.debug("missing case in CommandDispatcher");
    				return null;
    		}
    	} catch (Exception e) {
    		AbstractApplication.debug("Warning: command not found / CAS command called");
    	}
    	return null;
    }
}

