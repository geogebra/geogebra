package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.advanced.*;
import geogebra.common.kernel.algos.CmdAppend;
import geogebra.common.kernel.algos.CmdDefined;
import geogebra.common.kernel.algos.CmdDiv;
import geogebra.common.kernel.algos.CmdFirst;
import geogebra.common.kernel.algos.CmdIsInteger;
import geogebra.common.kernel.algos.CmdKeepIf;
import geogebra.common.kernel.algos.CmdLaTeX;
import geogebra.common.kernel.algos.CmdLast;
import geogebra.common.kernel.algos.CmdMax;
import geogebra.common.kernel.algos.CmdMin;
import geogebra.common.kernel.algos.CmdMod;
import geogebra.common.kernel.algos.CmdRemoveUndefined;
import geogebra.common.kernel.algos.CmdReverse;
import geogebra.common.kernel.algos.CmdTableText;
import geogebra.common.kernel.algos.CmdTake;
import geogebra.common.kernel.algos.CmdTextToUnicode;
import geogebra.common.kernel.algos.CmdUnicodeToText;

/**
 * class to split off some CmdXXX classes into another jar (for faster applet loading)
 *
 */
public class CommandDispatcherAdvanced {
	public CommandProcessor dispatch(Commands c, Kernel kernel){
		switch(c){
		// advanced
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
		case IntersectRegion:
			return new CmdIntersectRegion(kernel);
		case Direction:
			return new CmdDirection(kernel);
		case Extremum:
			return new CmdExtremum(kernel);
		case TaylorPolynomial:
		case TaylorSeries:
			return new CmdTaylorSeries(kernel);
		case Defined:
		case IsDefined:
			return new CmdDefined(kernel);
		case UnitPerpendicularVector:
		case UnitOrthogonalVector:
			return new CmdUnitOrthogonalVector(kernel);
		case Text:
			return new CmdText(kernel);
		case FormulaText:
		case LaTeX:
			return new CmdLaTeX(kernel);
		case SecondAxis:
		case MinorAxis:
			return new CmdSecondAxis(kernel);

		case SemiMinorAxisLength:
		case SecondAxisLength:
			return new CmdSecondAxisLength(kernel);

		case Directrix:
			return new CmdDirectrix(kernel);
		case Numerator:
			return new CmdNumerator(kernel);
		case Denominator:
			return new CmdDenominator(kernel);
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
		case Roots:
			return new CmdRoots(kernel);
		case AffineRatio:
			return new CmdAffineRatio(kernel);
		case CrossRatio:
			return new CmdCrossRatio(kernel);
		case ClosestPoint:
			return new CmdClosestPoint(kernel);
		case CountIf:
			return new CmdCountIf(kernel);
		case IsInteger:
			return new CmdIsInteger(kernel);
		case KeepIf:
			return new CmdKeepIf(kernel);
		case IsInRegion:
			return new CmdIsInRegion(kernel);
		case PrimeFactors:
			return new CmdPrimeFactors(kernel);
		case CompleteSquare:
			return new CmdCompleteSquare(kernel);
		case Union:
			return new CmdUnion(kernel);
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
		case UnitVector:
			return new CmdUnitVector(kernel);
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
		case Centroid:
			return new CmdCentroid(kernel);

		case MajorAxis:
		case FirstAxis:
			return new CmdFirstAxis(kernel);

		case SemiMajorAxisLength:
		case FirstAxisLength:
			return new CmdFirstAxisLength(kernel);

		case AxisStepX:
			return new CmdAxisStepX(kernel);
		case AxisStepY:
			return new CmdAxisStepY(kernel);
		case ConstructionStep:
			return new CmdConstructionStep(kernel);
		case Object:
			return new CmdObject(kernel);
		case Polar:
			return new CmdPolar(kernel);

		case LinearEccentricity:
		case Excentricity:
			return new CmdExcentricity(kernel);

		case Eccentricity:
			return new CmdEccentricity(kernel);
		case Axes:
			return new CmdAxes(kernel);
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
		case Divisors:
			return new CmdDivisorsOrDivisorsSum(kernel,false);
		case DivisorsSum:
			return new CmdDivisorsOrDivisorsSum(kernel,true);
		case Dimension:
			return new CmdDimension(kernel);
		case DivisorsList:
			return new CmdDivisorsList(kernel);
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
		case CommonDenominator:
			return new CmdCommonDenominator(kernel);
		case ToPoint:
			return new CmdToComplexPolar(kernel,Kernel.COORD_CARTESIAN);
		case ToComplex:
			return new CmdToComplexPolar(kernel,Kernel.COORD_COMPLEX);
		case ToPolar:
			return new CmdToComplexPolar(kernel,Kernel.COORD_POLAR);
		}
		return null;
	}
}
