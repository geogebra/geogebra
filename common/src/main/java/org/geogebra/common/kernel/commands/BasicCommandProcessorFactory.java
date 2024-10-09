package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.plugin.Operation;

/**
 * Factory for the most commonly used command processors.
 * This is the only {@link CommandProcessorFactory} that can be
 * directly instantiated in common and does not require aynchronously
 * loading a module in web.
 */
public class BasicCommandProcessorFactory implements CommandProcessorFactory {
	@Override
	public CommandProcessor getProcessor(Commands command, Kernel kernel) {
		switch (command) {
		// basic

		case Tangent:
			return new CmdTangent(kernel);
		case Length:
			return new CmdLength(kernel);
		case Sort:
			return new CmdSort(kernel);
		case Product:
			return new CmdProduct(kernel);
		case Extremum:
			return new CmdExtremum(kernel);
		case RemovableDiscontinuity:
			return new CmdRemovableDiscontinuity(kernel);
		case Join:
			return new CmdJoin(kernel);
		case LCM:
			return new CmdLCM(kernel);
		case GCD:
			return new CmdGCD(kernel);
		case Normalize:
			return new CmdNormalize(kernel);
		case Object:
			return new CmdObject(kernel);
		case LetterToUnicode:
			return new CmdLetterToUnicode(kernel);
		case UnicodeToLetter:
			return new CmdUnicodeToLetter(kernel);
		case CountIf:
			return new CmdCountIf(kernel);
		case Direction:
			return new CmdUnitVector(kernel, false);
		case UnitVector:
			return new CmdUnitVector(kernel, true);

		case UnitPerpendicularVector:
		case UnitOrthogonalVector:
			return new CmdUnitOrthogonalVector(kernel);

		case Text:
			return new CmdText(kernel);
		case Vector:
			return new CmdVector(kernel);
		case Dot:
			return new CmdCAStoOperation(kernel, Operation.MULTIPLY);
		case Cross:
			return new CmdCAStoOperation(kernel, Operation.VECTORPRODUCT);
		case nPr:
			return new CmdCAStoOperation(kernel, Operation.NPR);
		case PolyLine:
			return new CmdPolyLine(kernel);
		case PenStroke:
			return new CmdPenStroke(kernel);
		case PointIn:
			return new CmdPointIn(kernel);
		case Line:
			return new CmdLine(kernel);
		case Ray:
			return new CmdRay(kernel);

		case AngleBisector:
		case AngularBisector:
			return new CmdAngularBisector(kernel);

		case Segment:
			return new CmdSegment(kernel);
		case Slope:
			return new CmdSlope(kernel);
		case Angle:
			return new CmdAngle(kernel);
		case InteriorAngles:
			return new CmdInteriorAngles(kernel);
		case Point:
			return new CmdPoint(kernel);
		case Midpoint:
		case Center:
			return new CmdMidpoint(kernel);
		case Intersect:
			return new CmdIntersect(kernel);
		case Distance:
			return new CmdDistance(kernel);
		case Radius:
			return new CmdRadius(kernel);
		case Type:
			return new CmdType(kernel);
		case Arc:
			return new CmdArcSector(kernel, GeoConicNDConstants.CONIC_PART_ARC);
		case Sector:
			return new CmdArcSector(kernel,
					GeoConicNDConstants.CONIC_PART_SECTOR);

		case CircleArc:
		case CircularArc:
			return new CmdCircleArcSector(kernel,
					GeoConicNDConstants.CONIC_PART_ARC);

		case CircleSector:
		case CircularSector:
			return new CmdCircleArcSector(kernel,
					GeoConicNDConstants.CONIC_PART_SECTOR);

		case CircumcircleSector:
		case CircumcircularSector:
			return new CmdCircumcircleSector(kernel);

		case CircumcircleArc:
		case CircumcircularArc:
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
		case InflectionPoint:
		case TurningPoint:
			return new CmdTurningPoint(kernel);
		case Polynomial:
			return new CmdPolynomial(kernel);
		case Function:
			return new CmdFunction(kernel);

		case Curve:
		case CurveCartesian:
			return new CmdCurveCartesian(kernel);
			
		case ExportImage:
			return new CmdExportImage(kernel);

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
			return new CmdEllipseHyperbola(kernel,
					GeoConicNDConstants.CONIC_ELLIPSE);
		case Hyperbola:
			return new CmdEllipseHyperbola(kernel,
					GeoConicNDConstants.CONIC_HYPERBOLA);
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
		case RandomPointIn:
			return new CmdRandomPointIn(kernel);

		case Sum:
			return new CmdSum(kernel);

		case Binomial:
		case BinomialCoefficient:
		case nCr:
			return new CmdBinomial(kernel);

		case Mod:
			return new CmdMod(kernel);
		case Div:
			return new CmdDiv(kernel);
		case Min:
			return new CmdMinMax(kernel, Commands.Min);
		case Max:
			return new CmdMinMax(kernel, Commands.Max);
		case Append:
			return new CmdAppend(kernel);
		case First:
			return new CmdFirst(kernel);
		case Last:
			return new CmdLast(kernel);
		case RemoveUndefined:
			return new CmdRemoveUndefined(kernel);
		case Remove:
			return new CmdRemove(kernel);
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
		case IsFactored:
			return new CmdIsFactored(kernel);
		case Defined:
		case IsDefined:
			return new CmdDefined(kernel);
		case Spline:
			return new CmdSpline(kernel);
		// case Nyquist:
		// return new CmdNyquist(kernel);
		case FormulaText:
		case LaTeX:
			return new CmdLaTeX(kernel);
		case InputBox:
		case Textfield:
			return new CmdTextfield(kernel);
		case Surface:
			return new CmdSurfaceCartesian(kernel);
		default:
			break;
		}
		return null;
	}
}
