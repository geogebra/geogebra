package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.CmdUnitOrthogonalVector;
import geogebra.common.kernel.algos.CmdUnitVector;
import geogebra.common.plugin.Operation;

/**
 * class to split off some CmdXXX classes into another jar (for faster applet loading)
 *
 */
public class CommandDispatcherBasic implements CommandDispatcherInterface {
	public CommandProcessor dispatch(Commands c, Kernel kernel){
		switch(c){
		// basic

		case Tangent:
			return new CmdTangent(kernel);
		case Length:
			return new CmdLength(kernel);
		case Sort:
			return new CmdSort(kernel);
		case BarChart:
			return new CmdBarChart(kernel);
		case Product:
			return new CmdProduct(kernel);
		case Extremum:
			return new CmdExtremum(kernel);
		case Join:
			return new CmdJoin(kernel);
		case LCM:
			return new CmdLCM(kernel);
		case GCD:
			return new CmdGCD(kernel);
		case Object:
			return new CmdObject(kernel);
		case LetterToUnicode:
			return new CmdLetterToUnicode(kernel);
		case UnicodeToLetter:
			return new CmdUnicodeToLetter(kernel);
		case CountIf:
			return new CmdCountIf(kernel);
		case UnitVector:
			return new CmdUnitVector(kernel);
			
		case UnitPerpendicularVector:
		case UnitOrthogonalVector:
			return new CmdUnitOrthogonalVector(kernel);
			
		case Text:
			return new CmdText(kernel);
		case Vector:
			return new CmdVector(kernel);
		case Dot:
			return new CmdCAStoOperation(kernel,Operation.MULTIPLY);
		case Cross:
			return new CmdCAStoOperation(kernel,Operation.VECTORPRODUCT);
		case PolyLine:
			return new CmdPolyLine(kernel);
		case PointIn:
			return new CmdPointIn(kernel);
		case FreePointIn:
			return new CmdFreePointIn(kernel);
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
			
		case Reflect:
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
