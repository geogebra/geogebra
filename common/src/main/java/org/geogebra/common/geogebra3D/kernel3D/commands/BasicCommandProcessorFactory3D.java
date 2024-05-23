package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.BasicCommandProcessorFactory;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;

/**
 * Provides 3D overrides for commands produced by {@link BasicCommandProcessorFactory}.
 * Functionality from those overrides could be merged into their superclasses,
 * making this factory redundant.
 */
public class BasicCommandProcessorFactory3D extends BasicCommandProcessorFactory {

	@Override
	public CommandProcessor getProcessor(Commands command, Kernel kernel) {
		switch (command) {
		case Segment:
			return new CmdSegment3D(kernel);
		case Line:
			return new CmdLine3D(kernel);
		case Ray:
			return new CmdRay3D(kernel);
		case Polygon:
			return new CmdPolygon3D(kernel);
		case Area:
			return new CmdArea3D(kernel);
		case Polyline:
		case PolyLine:
			return new CmdPolyLine3D(kernel);
		case Point:
			return new CmdPoint3D(kernel);
		case Midpoint:
		case Center:
			return new CmdMidpoint3D(kernel);

		case Tangent:
			return new CmdTangent3D(kernel);
		case Diameter:
		case ConjugateDiameter:
			return new CmdDiameter3D(kernel);

		case Circle:
			return new CmdCircle3D(kernel);

		case Ellipse:
			return new CmdEllipseHyperbola3D(kernel,
					GeoConicNDConstants.CONIC_ELLIPSE);
		case Hyperbola:
			return new CmdEllipseHyperbola3D(kernel,
					GeoConicNDConstants.CONIC_HYPERBOLA);
		case Conic:
			return new CmdConic3D(kernel);

		case CircumcircleSector:
		case CircumcircularSector:
			return new CmdCircumcircleSector3D(kernel);

		case CircumcircleArc:
		case CircumcircularArc:
			return new CmdCircumcircleArc3D(kernel);

		case Arc:
			return new CmdArcSector3D(kernel,
					GeoConicNDConstants.CONIC_PART_ARC);
		case Sector:
			return new CmdArcSector3D(kernel,
					GeoConicNDConstants.CONIC_PART_SECTOR);

		case CircleArc:
		case CircularArc:
			return new CmdCircleArcSector3D(kernel,
					GeoConicNDConstants.CONIC_PART_ARC);

		case CircleSector:
		case CircularSector:
			return new CmdCircleArcSector3D(kernel,
					GeoConicNDConstants.CONIC_PART_SECTOR);

		case Semicircle:
			return new CmdSemicircle3D(kernel);

		case Parabola:
			return new CmdParabola3D(kernel);

		case Corner:
			return new CmdCorner3D(kernel);

		case Locus:
			return new CmdLocus3D(kernel);

		case Vertex:
			return new CmdVertex3D(kernel);
		case Focus:
			return new CmdFocus3D(kernel);

		case PerpendicularLine:
		case OrthogonalLine:
			return new CmdOrthogonalLine3D(kernel);

		case LineBisector:
		case PerpendicularBisector:
			return new CmdLineBisector3D(kernel);

		case AngleBisector:
		case AngularBisector:
			return new CmdAngularBisector3D(kernel);

		case PerpendicularVector:
		case OrthogonalVector:
			return new CmdOrthogonalVector3D(kernel);

		case UnitPerpendicularVector:
		case UnitOrthogonalVector:
			return new CmdUnitOrthogonalVector3D(kernel);

		case Direction:
			return new CmdUnitVector3D(kernel, false);
		case UnitVector:
			return new CmdUnitVector3D(kernel, true);

		case Curve:
		case CurveCartesian:
			return new CmdCurveCartesian3D(kernel);

		case PointIn:
			return new CmdPointIn3D(kernel);

		case Distance:
			return new CmdDistance3D(kernel);

		case Intersect:
			return new CmdIntersect3D(kernel);

		case Angle:
			return new CmdAngle3D(kernel);

		case InteriorAngles:
			return new CmdInteriorAngles3D(kernel);

		case Translate:
			return new CmdTranslate3D(kernel);

		case Rotate:
			return new CmdRotate3D(kernel);
		case Reflect:
		case Mirror:
			return new CmdMirror3D(kernel);

		case Dilate:
			return new CmdDilate3D(kernel);

		case Length:
			return new CmdLength3D(kernel);
		default:
			return super.getProcessor(command, kernel);
		}
	}
}
