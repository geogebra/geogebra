package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.advanced.CmdAffineRatio;
import geogebra.common.kernel.advanced.CmdApplyMatrix;
import geogebra.common.kernel.advanced.CmdAreCollinear;
import geogebra.common.kernel.advanced.CmdAreConcurrent;
import geogebra.common.kernel.advanced.CmdAreConcyclic;
import geogebra.common.kernel.advanced.CmdAreEqual;
import geogebra.common.kernel.advanced.CmdAreParallel;
import geogebra.common.kernel.advanced.CmdArePerpendicular;
import geogebra.common.kernel.advanced.CmdAsymptote;
import geogebra.common.kernel.advanced.CmdAttachCopyToView;
import geogebra.common.kernel.advanced.CmdAxes;
import geogebra.common.kernel.advanced.CmdAxisStepX;
import geogebra.common.kernel.advanced.CmdAxisStepY;
import geogebra.common.kernel.advanced.CmdCentroid;
import geogebra.common.kernel.advanced.CmdClosestPoint;
import geogebra.common.kernel.advanced.CmdCommonDenominator;
import geogebra.common.kernel.advanced.CmdCompleteSquare;
import geogebra.common.kernel.advanced.CmdComplexRoot;
import geogebra.common.kernel.advanced.CmdConstructionStep;
import geogebra.common.kernel.advanced.CmdContinuedFraction;
import geogebra.common.kernel.advanced.CmdCountIf;
import geogebra.common.kernel.advanced.CmdCrossRatio;
import geogebra.common.kernel.advanced.CmdCurvature;
import geogebra.common.kernel.advanced.CmdCurvatureVector;
import geogebra.common.kernel.advanced.CmdDenominator;
import geogebra.common.kernel.advanced.CmdDeterminant;
import geogebra.common.kernel.advanced.CmdDimension;
import geogebra.common.kernel.advanced.CmdDirection;
import geogebra.common.kernel.advanced.CmdDirectrix;
import geogebra.common.kernel.advanced.CmdDivision;
import geogebra.common.kernel.advanced.CmdDivisorsList;
import geogebra.common.kernel.advanced.CmdDivisorsOrDivisorsSum;
import geogebra.common.kernel.advanced.CmdDynamicCoordinates;
import geogebra.common.kernel.advanced.CmdEccentricity;
import geogebra.common.kernel.advanced.CmdExcentricity;
import geogebra.common.kernel.advanced.CmdExtremum;
import geogebra.common.kernel.advanced.CmdFirstAxis;
import geogebra.common.kernel.advanced.CmdFirstAxisLength;
import geogebra.common.kernel.advanced.CmdFlatten;
import geogebra.common.kernel.advanced.CmdFromBase;
import geogebra.common.kernel.advanced.CmdGCD;
import geogebra.common.kernel.advanced.CmdIdentity;
import geogebra.common.kernel.advanced.CmdImplicitPoly;
import geogebra.common.kernel.advanced.CmdIncircle;
import geogebra.common.kernel.advanced.CmdIndexOf;
import geogebra.common.kernel.advanced.CmdInsert;
import geogebra.common.kernel.advanced.CmdIntersectRegion;
import geogebra.common.kernel.advanced.CmdIntersection;
import geogebra.common.kernel.advanced.CmdInvert;
import geogebra.common.kernel.advanced.CmdIsInRegion;
import geogebra.common.kernel.advanced.CmdIsPrime;
import geogebra.common.kernel.advanced.CmdIteration;
import geogebra.common.kernel.advanced.CmdIterationList;
import geogebra.common.kernel.advanced.CmdJoin;
import geogebra.common.kernel.advanced.CmdLCM;
import geogebra.common.kernel.advanced.CmdLeftRightSide;
import geogebra.common.kernel.advanced.CmdLetterToUnicode;
import geogebra.common.kernel.advanced.CmdMatrixRank;
import geogebra.common.kernel.advanced.CmdMaximize;
import geogebra.common.kernel.advanced.CmdMinimize;
import geogebra.common.kernel.advanced.CmdNumerator;
import geogebra.common.kernel.advanced.CmdObject;
import geogebra.common.kernel.advanced.CmdOrdinal;
import geogebra.common.kernel.advanced.CmdOsculatingCircle;
import geogebra.common.kernel.advanced.CmdParameter;
import geogebra.common.kernel.advanced.CmdPathParameter;
import geogebra.common.kernel.advanced.CmdPolar;
import geogebra.common.kernel.advanced.CmdPrimeFactors;
import geogebra.common.kernel.advanced.CmdProve;
import geogebra.common.kernel.advanced.CmdProveDetails;
import geogebra.common.kernel.advanced.CmdReducedRowEchelonForm;
import geogebra.common.kernel.advanced.CmdRootList;
import geogebra.common.kernel.advanced.CmdRoots;
import geogebra.common.kernel.advanced.CmdRotateText;
import geogebra.common.kernel.advanced.CmdScientificText;
import geogebra.common.kernel.advanced.CmdSecondAxis;
import geogebra.common.kernel.advanced.CmdSecondAxisLength;
import geogebra.common.kernel.advanced.CmdSelectedElement;
import geogebra.common.kernel.advanced.CmdSelectedIndex;
import geogebra.common.kernel.advanced.CmdSlopeField;
import geogebra.common.kernel.advanced.CmdTaylorSeries;
import geogebra.common.kernel.advanced.CmdText;
import geogebra.common.kernel.advanced.CmdToBase;
import geogebra.common.kernel.advanced.CmdToComplexPolar;
import geogebra.common.kernel.advanced.CmdTranspose;
import geogebra.common.kernel.advanced.CmdUnicodeToLetter;
import geogebra.common.kernel.advanced.CmdUnion;
import geogebra.common.kernel.advanced.CmdUnique;
import geogebra.common.kernel.advanced.CmdUnitOrthogonalVector;
import geogebra.common.kernel.advanced.CmdUnitVector;
import geogebra.common.kernel.advanced.CmdVerticalText;
import geogebra.common.kernel.advanced.CmdZip;
import geogebra.common.kernel.statistics.CmdNpR;
import geogebra.common.plugin.Operation;

/**
 * class to split off some CmdXXX classes into another jar (for faster applet loading)
 *
 */
public class CommandDispatcherBasic {
	public CommandProcessor dispatch(Commands c, Kernel kernel){
		switch(c){
		// basic
		
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

	case Sum:
		return new CmdSum(kernel);

	case Binomial:
	case BinomialCoefficient:
		return new CmdBinomial(kernel);
		
	case Mod:
		return new CmdMod(kernel);
	case Div:
		return new CmdDiv(kernel);
	case Min:
		return new CmdMin(kernel);
	case Max:
		return new CmdMax(kernel);
	case Append:
		return new CmdAppend(kernel);
	case First:
		return new CmdFirst(kernel);
	case Last:
		return new CmdLast(kernel);
	case RemoveUndefined:
		return new CmdRemoveUndefined(kernel);
	case Reverse:
		return new CmdReverse(kernel);
	case TableText:
		return new CmdTableText(kernel);
	case Take:
		return new CmdTake(kernel);
	case TextToUnicode:
		return new CmdTextToUnicode(kernel);
	case UnicodeToText:
		return new CmdUnicodeToText(kernel);
	case FractionText:
		return new CmdFractionText(kernel);
	case KeepIf:
		return new CmdKeepIf(kernel);
	case IsInteger:
		return new CmdIsInteger(kernel);
	
	case Defined:
	case IsDefined:
		return new CmdDefined(kernel);

	case FormulaText:
	case LaTeX:
		return new CmdLaTeX(kernel);
		}
		return null;
	}
}
