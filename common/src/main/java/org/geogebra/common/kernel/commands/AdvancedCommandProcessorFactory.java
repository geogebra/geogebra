package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.advanced.CmdAffineRatio;
import org.geogebra.common.kernel.advanced.CmdApplyMatrix;
import org.geogebra.common.kernel.advanced.CmdAsymptote;
import org.geogebra.common.kernel.advanced.CmdAttachCopyToView;
import org.geogebra.common.kernel.advanced.CmdAxes;
import org.geogebra.common.kernel.advanced.CmdAxis;
import org.geogebra.common.kernel.advanced.CmdAxisLength;
import org.geogebra.common.kernel.advanced.CmdAxisStepX;
import org.geogebra.common.kernel.advanced.CmdCentroid;
import org.geogebra.common.kernel.advanced.CmdClosestPoint;
import org.geogebra.common.kernel.advanced.CmdCommonDenominator;
import org.geogebra.common.kernel.advanced.CmdComplexRoot;
import org.geogebra.common.kernel.advanced.CmdConstructionStep;
import org.geogebra.common.kernel.advanced.CmdContinuedFraction;
import org.geogebra.common.kernel.advanced.CmdCrossRatio;
import org.geogebra.common.kernel.advanced.CmdCurvature;
import org.geogebra.common.kernel.advanced.CmdCurvatureVector;
import org.geogebra.common.kernel.advanced.CmdDeterminant;
import org.geogebra.common.kernel.advanced.CmdDimension;
import org.geogebra.common.kernel.advanced.CmdDirectrix;
import org.geogebra.common.kernel.advanced.CmdDivision;
import org.geogebra.common.kernel.advanced.CmdDivisorsList;
import org.geogebra.common.kernel.advanced.CmdDivisorsOrDivisorsSum;
import org.geogebra.common.kernel.advanced.CmdDynamicCoordinates;
import org.geogebra.common.kernel.advanced.CmdEccentricity;
import org.geogebra.common.kernel.advanced.CmdExcentricity;
import org.geogebra.common.kernel.advanced.CmdFactors;
import org.geogebra.common.kernel.advanced.CmdFinancialFV;
import org.geogebra.common.kernel.advanced.CmdFinancialNper;
import org.geogebra.common.kernel.advanced.CmdFinancialPV;
import org.geogebra.common.kernel.advanced.CmdFinancialPmt;
import org.geogebra.common.kernel.advanced.CmdFinancialRate;
import org.geogebra.common.kernel.advanced.CmdFlatten;
import org.geogebra.common.kernel.advanced.CmdFromBase;
import org.geogebra.common.kernel.advanced.CmdIdentity;
import org.geogebra.common.kernel.advanced.CmdImplicitPoly;
import org.geogebra.common.kernel.advanced.CmdImplicitSurface;
import org.geogebra.common.kernel.advanced.CmdIncircle;
import org.geogebra.common.kernel.advanced.CmdIndexOf;
import org.geogebra.common.kernel.advanced.CmdInsert;
import org.geogebra.common.kernel.advanced.CmdIntersectPath;
import org.geogebra.common.kernel.advanced.CmdIntersection;
import org.geogebra.common.kernel.advanced.CmdInvert;
import org.geogebra.common.kernel.advanced.CmdIsInRegion;
import org.geogebra.common.kernel.advanced.CmdIsPrime;
import org.geogebra.common.kernel.advanced.CmdIteration;
import org.geogebra.common.kernel.advanced.CmdIterationList;
import org.geogebra.common.kernel.advanced.CmdLeftRightSide;
import org.geogebra.common.kernel.advanced.CmdMatrixRank;
import org.geogebra.common.kernel.advanced.CmdMaximize;
import org.geogebra.common.kernel.advanced.CmdMinimize;
import org.geogebra.common.kernel.advanced.CmdNSolveODE;
import org.geogebra.common.kernel.advanced.CmdNumeratorDenominator;
import org.geogebra.common.kernel.advanced.CmdOrdinal;
import org.geogebra.common.kernel.advanced.CmdOsculatingCircle;
import org.geogebra.common.kernel.advanced.CmdParameter;
import org.geogebra.common.kernel.advanced.CmdPathParameter;
import org.geogebra.common.kernel.advanced.CmdPolar;
import org.geogebra.common.kernel.advanced.CmdPrimeFactors;
import org.geogebra.common.kernel.advanced.CmdReducedRowEchelonForm;
import org.geogebra.common.kernel.advanced.CmdRootList;
import org.geogebra.common.kernel.advanced.CmdRoots;
import org.geogebra.common.kernel.advanced.CmdRotateText;
import org.geogebra.common.kernel.advanced.CmdScientificText;
import org.geogebra.common.kernel.advanced.CmdSelectedElement;
import org.geogebra.common.kernel.advanced.CmdSelectedIndex;
import org.geogebra.common.kernel.advanced.CmdSetConstructionStep;
import org.geogebra.common.kernel.advanced.CmdSlopeField;
import org.geogebra.common.kernel.advanced.CmdTaylorSeries;
import org.geogebra.common.kernel.advanced.CmdToBase;
import org.geogebra.common.kernel.advanced.CmdToComplexPolar;
import org.geogebra.common.kernel.advanced.CmdTranspose;
import org.geogebra.common.kernel.advanced.CmdUnion;
import org.geogebra.common.kernel.advanced.CmdUnique;
import org.geogebra.common.kernel.advanced.CmdVerticalText;
import org.geogebra.common.kernel.advanced.CmdZip;
import org.geogebra.common.main.Feature;

/**
 * Factory for command processors that are not used very often
 * (so they can be loaded asynchronously) but do not fit
 * any specific category (stats, CAS, discrete).
 * @see CommandProcessorFactory
 */
public class AdvancedCommandProcessorFactory implements CommandProcessorFactory {

	@Override
	public CommandProcessor getProcessor(Commands command, Kernel kernel) {
		switch (command) {
		// advanced

		case Factors:
			return new CmdFactors(kernel);
		case IntersectPath:
		case IntersectionPaths: // deprecated
		case IntersectRegion: // deprecated
			return new CmdIntersectPath(kernel);

		case Difference:
			return new CmdDifference(kernel);

		case TaylorPolynomial:
		case TaylorSeries:
			return new CmdTaylorSeries(kernel);
		case SecondAxis:
		case MinorAxis:
			return new CmdAxis(kernel, 1);
		case SemiMinorAxisLength:
		case SecondAxisLength:
			return new CmdAxisLength(kernel, 1);

		case Directrix:
			return new CmdDirectrix(kernel);
		case Numerator:
			return new CmdNumeratorDenominator(kernel, command);
		case Denominator:
			return new CmdNumeratorDenominator(kernel, command);
		case ComplexRoot:
			return new CmdComplexRoot(kernel);
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
		case ImplicitSurface:
			return !kernel.getApplication().has(Feature.IMPLICIT_SURFACES)
					? null : new CmdImplicitSurface(kernel);
		case Roots:
			return new CmdRoots(kernel);
		case AffineRatio:
			return new CmdAffineRatio(kernel);
		case CrossRatio:
			return new CmdCrossRatio(kernel);
		case ClosestPoint:
			return new CmdClosestPoint(kernel);
		case IsInRegion:
			return new CmdIsInRegion(kernel);
		case PrimeFactors:
			return new CmdPrimeFactors(kernel);
		case Union:
			return new CmdUnion(kernel);
		case ScientificText:
			return new CmdScientificText(kernel);
		case VerticalText:
			return new CmdVerticalText(kernel);
		case RotateText:
			return new CmdRotateText(kernel);
		case Ordinal:
			return new CmdOrdinal(kernel);
		case Parameter:
			return new CmdParameter(kernel);
		case Incircle:
			return new CmdIncircle(kernel);
		case SelectedElement:
			return new CmdSelectedElement(kernel);
		case SelectedIndex:
			return new CmdSelectedIndex(kernel);
		case Unique:
			return new CmdUnique(kernel);
		case Zip:
			return new CmdZip(kernel);
		case Intersection:
			return new CmdIntersection(kernel);
		case PointList:
			return new CmdPointList(kernel);
		case ApplyMatrix:
			return new CmdApplyMatrix(kernel);
		case NInvert:
			return new CmdInvert(kernel, true);
		case Invert:
			return new CmdInvert(kernel, !kernel.getApplication().getSettings()
					.getCasSettings().isEnabled());
		case Transpose:
			return new CmdTranspose(kernel);
		case ReducedRowEchelonForm:
			return new CmdReducedRowEchelonForm(kernel);
		case Determinant:
			return new CmdDeterminant(kernel);
		// case MatrixPlot:
		// return new CmdMatrixPlot(kernel);
		case Identity:
			return new CmdIdentity(kernel);
		case Centroid:
			return new CmdCentroid(kernel);

		case MajorAxis:
		case FirstAxis:
			return new CmdAxis(kernel, 0);

		case SemiMajorAxisLength:
		case FirstAxisLength:
			return new CmdAxisLength(kernel, 0);

		case AxisStepX:
			return new CmdAxisStepX(kernel, 0);
		case AxisStepY:
			return new CmdAxisStepX(kernel, 1);
		case ConstructionStep:
			return new CmdConstructionStep(kernel);
		case SetConstructionStep:
			return new CmdSetConstructionStep(kernel);
		case Polar:
			return new CmdPolar(kernel);

		case LinearEccentricity:
		case Excentricity:
			return new CmdExcentricity(kernel);

		case Eccentricity:
			return new CmdEccentricity(kernel);
		case Axes:
			return new CmdAxes(kernel);
		case IndexOf:
			return new CmdIndexOf(kernel);
		case Flatten:
			return new CmdFlatten(kernel);
		case Insert:
			return new CmdInsert(kernel);
		case DynamicCoordinates:
			return new CmdDynamicCoordinates(kernel);
		case Maximize:
			return new CmdMaximize(kernel);
		case Minimize:
			return new CmdMinimize(kernel);
		case ToBase:
			return new CmdToBase(kernel);
		case FromBase:
			return new CmdFromBase(kernel);
		case ContinuedFraction:
			return new CmdContinuedFraction(kernel);
		case AttachCopyToView:
			return new CmdAttachCopyToView(kernel);
		case Divisors:
			return new CmdDivisorsOrDivisorsSum(kernel, false);
		case DivisorsSum:
			return new CmdDivisorsOrDivisorsSum(kernel, true);
		case Dimension:
			return new CmdDimension(kernel);
		case DivisorsList:
			return new CmdDivisorsList(kernel);
		case IsPrime:
			return new CmdIsPrime(kernel);
		case LeftSide:
			return new CmdLeftRightSide(kernel, true);
		case RightSide:
			return new CmdLeftRightSide(kernel, false);
		case Division:
			return new CmdDivision(kernel);
		case MatrixRank:
			return new CmdMatrixRank(kernel);
		case CommonDenominator:
			return new CmdCommonDenominator(kernel);
		case ToPoint:
			return new CmdToComplexPolar(kernel, Kernel.COORD_CARTESIAN);
		case ToComplex:
			return new CmdToComplexPolar(kernel, Kernel.COORD_COMPLEX);
		case ToPolar:
			return new CmdToComplexPolar(kernel, Kernel.COORD_POLAR);
		case NSolveODE:
			return new CmdNSolveODE(kernel);
		case Rate:
			return new CmdFinancialRate(kernel);
		case Periods:
			return new CmdFinancialNper(kernel);
		case Payment:
			return new CmdFinancialPmt(kernel);
		case PresentValue:
			return new CmdFinancialPV(kernel);
		case FutureValue:
			return new CmdFinancialFV(kernel);
		case SVD:
			return new CmdSVD(kernel);
		case IsVertexForm:
			return new CmdIsVertexForm(kernel);
		case ReplaceAll:
			return new CmdReplaceAll(kernel);
		case Split:
			return new CmdSplit(kernel);
		default:
			break;
		}
		return null;
	}
}
